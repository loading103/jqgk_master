package com.daqsoft.module_work.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.fence.GeoFence
import com.amap.api.fence.GeoFenceClient
import com.amap.api.location.AMapLocation
import com.amap.api.location.DPoint
import com.amap.api.maps2d.model.LatLng
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.TimeUtils
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.library_base.wrapper.loadsircallback.LoadingCallback
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.FragmentClockBinding
import com.daqsoft.module_work.viewmodel.ClockViewMode
import com.daqsoft.library_common.widget.CalendarPopup
import com.daqsoft.library_common.widget.RemarkPopup
import com.daqsoft.library_common.utils.MyAMapUtils
import com.daqsoft.module_work.adapter.ClockAdapter
import com.daqsoft.module_work.repository.pojo.dto.ClockInfoRequest
import com.daqsoft.module_work.repository.pojo.vo.ClockedIn
import com.daqsoft.module_work.warrper.NotJoinedCallback
import com.daqsoft.module_work.warrper.RosterEmptyCallback
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.daqsoft.mvvmfoundation.utils.dp
import com.google.gson.Gson
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableObserver
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @package name???com.daqsoft.module_work.fragment
 * @date 10/5/2021 ?????? 4:45
 * @author zp
 * @describe ??????
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_CLOCK)
class ClockFragment : AppBaseFragment<FragmentClockBinding, ClockViewMode>() {

    companion object{
        // ?????????????????????action
        const val  FENCE_BROADCAST_ACTION = "com.daqsoft.smartscenicmanager.fence_broadcast_action"
    }

    // ?????????????????????
    private var fenceClient: GeoFenceClient? = null
    // ????????????
    private var currentLocation : AMapLocation? = null

    lateinit var todayCalendar : com.haibin.calendarview.Calendar
    lateinit var selectCalendar : com.haibin.calendarview.Calendar


    private val clockAdapter : ClockAdapter by lazy {
        ClockAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recycleview_clock_record_item)
            setOnClickListener(object : ClockAdapter.OnClickListener{
                override fun renew(position: Int, content: ClockedIn) {
                    viewModel.renewClockIn(content.id.toString())
                }
            })
        }
    }


    val calendarPopup : CalendarPopup by lazy {
        CalendarPopup(requireActivity()).apply {
            setOnClickListener(object : CalendarPopup.OnClickListener{
                override fun determineOnClick(calendar: com.haibin.calendarview.Calendar?) {
                    selectCalendar = calendar!!
                    binding.date.text = "${calendar.year}???${calendar.month}???${calendar.day}???"

                    LoadSirUtil.postLoading(loadService!!)
                    if (selectCalendar == todayCalendar){
                        val location = "${currentLocation?.longitude},${currentLocation?.latitude}"
                        viewModel.getClockInfo(location)
                    }else{
                        viewModel.getClockRecord("${selectCalendar.year}-${String.format("%02d", selectCalendar.month)}-${selectCalendar.day}")
                    }
                }

            })
        }
    }


    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_clock
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): ClockViewMode? {
        return requireActivity().viewModels<ClockViewMode>().value
    }

    override fun initView() {
        super.initView()
        initOnClick()
        initLoadService()
        initRecyclerView()

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
         val day = calendar.get(Calendar.DAY_OF_MONTH)
        binding.date.text = "${year}???${month}???${day}???"

        todayCalendar = com.haibin.calendarview.Calendar()
        todayCalendar.year = year
        todayCalendar.month = month
        todayCalendar.day = day

        selectCalendar = todayCalendar

    }

    private fun initRecyclerView() {
        binding.clockRecord.apply {
            layoutManager = LinearLayoutManager(requireContext())
            if (itemDecorationCount == 0){
             addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position != 0){
                        outRect.top = 16.dp
                    }
                }
            })
            }
            adapter = clockAdapter
        }

    }

    private fun initLoadService() {
        val loadSir = LoadSir.Builder()
            .addCallback(ErrorCallback())
            .addCallback(RosterEmptyCallback())
            .addCallback(LoadingCallback())
            .addCallback(NotJoinedCallback())
            .setDefaultCallback(SuccessCallback::class.java)
            .build()

        loadService = loadSir.register(binding.nestedScrolView, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initOnClick() {
        binding.clockBg.setOnClickListenerThrottleFirst {
            val should = viewModel.clockInfo.value!!.shouldClockIn!!
            val calendar = Calendar.getInstance()
            val compare = calendar.time.time.compareTo(TimeUtils.stringToDate("${should.workDate} ${should.workTime}","yyyy-MM-dd HH:mm:ss")!!.time)

            if (should.type == 1){
                // ??????
                if (compare == 1){
                    // ??????
                    showLatePopup("??????")
                    return@setOnClickListenerThrottleFirst
                }
            }else{
                // ??????
                if (compare == -1){
                    // ??????
                    showLatePopup("??????")
                    return@setOnClickListenerThrottleFirst
                }
            }

            val body = ClockInfoRequest(
                viewModel.clockInfo.value!!.address.id,
                "",
                "${currentLocation!!.longitude},${currentLocation!!.latitude}",
                viewModel.clockInfo.value!!.shouldClockIn!!.id,
            )
            viewModel.postClockIn(body)
        }

        binding.retry.setOnClickListenerThrottleFirst {
            requestPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                callback = {
                    startLocation()
                })
        }

        binding.date.setOnClickListenerThrottleFirst {
            calendarPopup()
        }
    }

    override fun initData() {
        super.initData()
        registeredFence()

        requestPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            callback = {
                startLocation()
            })
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.clockInfo.observe(this, Observer {
//            val location = LatLng(it.address.latitude.toDouble(), it.address.longitude.toDouble())
//            val distance = it.address.distanceRange
//            // ??????????????????
//            createCompanyFence(location,distance)

            if (selectCalendar != todayCalendar){
                return@Observer
            }

            binding.clockGroup.isVisible = true

            clockAdapter.setMaxSegment(it.maxSegment)
            clockAdapter.setToday(selectCalendar == todayCalendar)
            clockAdapter.setItems(it.clockedIns)

            if (it.shouldClockIn != null){
                binding.timeToCheckIn.text = "${if (it.shouldClockIn.type==1)"??????" else "??????"}?????? ${it.shouldClockIn.workTime}"

                binding.clockTitle.text =  "${if (it.shouldClockIn.type==1)"??????" else "??????"}??????"
            }else{
                binding.clockTitle.text = "${if (it.clockedIns.last().type==1)"??????" else "??????"}??????"
            }

            when(it.state){
                "RANGE_IN"->{
                    binding.address.text = currentLocation?.address
                    binding.address.setCompoundDrawables(null,null,null,null)
                    binding.retry.text = "????????????????????????"
                    val drawable = resources.getDrawable(R.mipmap.kaoqing_dui)
                    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    binding.retry.setCompoundDrawables(drawable, null, null, null)
                    binding.clockBg.isEnabled = true
                    binding.clockBg.setImageResource(R.mipmap.kaoqing_daka_bg)
                }
                "OUT_RANGE"->{
                    binding.address.text = "??????????????????????????????"
                    val drawable = resources.getDrawable(R.mipmap.kaoqing_niwen)
                    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    binding.address.setCompoundDrawables(drawable,null,null,null)
                    binding.retry.text = "????????????????????????"
                    binding.retry.setCompoundDrawables(null, null, null, null)
                    binding.clockBg.isEnabled = false
                    binding.clockBg.setImageResource(R.mipmap.kaoqing_daka_bg_bukedian)
                }
            }

        })


        viewModel.record.observe(this, Observer {

            binding.clockGroup.isVisible = false

            clockAdapter.setMaxSegment(it.maxSegment)
            clockAdapter.setToday(selectCalendar == todayCalendar)
            clockAdapter.setItems(it.clockedIns)

            if (it.shouldClockIn != null){
                binding.clockTitle.text =  "${if (it.shouldClockIn.type==1)"??????" else "??????"}??????"
            }else{
                binding.clockTitle.text = "${if (it.clockedIns.last().type==1)"??????" else "??????"}??????"
            }


        })


        viewModel.refresh.observe(this, Observer {
            requestPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                callback = {
                    startLocation()
                })
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            requireActivity().unregisterReceiver(mGeoFenceReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fenceClient?.removeGeoFence()

    }

    /**
     * ????????????????????????
     */
    private fun registeredFence() {
        val filter = IntentFilter()
        filter.addAction(FENCE_BROADCAST_ACTION)
        requireActivity().registerReceiver(mGeoFenceReceiver, filter)
    }

    /**
     * ????????????
     */
    private fun startLocation() {
        MyAMapUtils.getLocation(requireContext(),object : MyAMapUtils.MyLocationListener {
            override fun onNext(aMapLocation: AMapLocation) {
                Timber.e("aMapLocation ${Gson().toJson(aMapLocation)}")
                currentLocation = aMapLocation
                val location = "${aMapLocation.longitude},${aMapLocation.latitude}"
                viewModel.getClockInfo(location)
                MyAMapUtils.destroy()
            }

            override fun onError(errorMessage: String) {
                Timber.e("errorMessage ${errorMessage}")
                ToastUtils.showShortSafe("????????????????????????")
                MyAMapUtils.destroy()
            }

        })
    }



    /**
     * ??????????????????
     */
    private fun createCompanyFence(latlng: LatLng,distance:Int){
        // ?????????????????????
        fenceClient = GeoFenceClient(context).apply {
            // ?????????????????????????????????/?????????
            this.setActivateAction(GeoFenceClient.GEOFENCE_IN or GeoFenceClient.GEOFENCE_OUT)
            //???????????????????????????
            val centerPoint = DPoint()
            // ?????????????????????
            centerPoint.latitude = latlng.latitude
            // ?????????????????????
            centerPoint.longitude = latlng.longitude
            // ????????????????????????/??????m/??????id???
            this.addGeoFence(centerPoint, distance.toFloat(), "com.daqsoft.smartscenicmanager")
            // ???????????????PendingIntent
            this.createPendingIntent(FENCE_BROADCAST_ACTION)
            // ??????????????????
            this.setGeoFenceListener { p0, p1, p2 ->
                // ??????????????????????????????
                if (p1 == GeoFence.ADDGEOFENCE_SUCCESS) {
                    Timber.e("??????????????????")
                }else{
                    Timber.e("??????????????????")
                }
            }
            this.createPendingIntent(FENCE_BROADCAST_ACTION)
        }
    }

    /**
     * ??????????????????????????????,??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ?????????????????????????????????????????????,???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private val mGeoFenceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == FENCE_BROADCAST_ACTION) {
                // ??????Bundle
                val bundle = intent.extras
                // ?????????????????????????????????
                val customId = bundle?.getString(GeoFence.BUNDLE_KEY_CUSTOMID)
                // ????????????ID:
                val fenceId = bundle?.getString(GeoFence.BUNDLE_KEY_FENCEID)
                // ?????????????????????
                val status = bundle?.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS)
                when(status){
                    GeoFence.STATUS_LOCFAIL -> {
                        // ????????????
                        Timber.e("????????????")
                        setClockStatus(false)
                    }
                    GeoFence.STATUS_IN -> {
                        // ????????????
                        Timber.e("????????????")
                        setClockStatus(true)
                    }
                    GeoFence.STATUS_OUT -> {
                        // ????????????
                        Timber.e("????????????")
                        setClockStatus(false)
                    }
                    GeoFence.STATUS_STAYED -> {
                        // ??????????????????
                        Timber.e("??????????????????")
                    }
                }
            }
        }
    }

    /**
     * ??????????????????
     * @param enable Boolean
     */
    fun setClockStatus(enable : Boolean){

        binding.clockBg.isEnabled = enable
        binding.clockBg.setImageResource(if (enable) R.mipmap.kaoqing_daka_bg else R.mipmap.kaoqing_daka_bg_bukedian)
        binding.retry.isEnabled = !enable

        if (enable){
            binding.address.setCompoundDrawables(null, null, null, null)
            binding.address.setTextColor(resources.getColor(R.color.color_333333))
            binding.address.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            binding.address.text = currentLocation?.address?:""

            val drawable = resources.getDrawable(R.mipmap.kaoqing_dui)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            binding.retry.setCompoundDrawables(drawable, null, null, null)
            binding.retry.text = "????????????????????????"
        }else{
            val drawable = resources.getDrawable(R.mipmap.kaoqing_niwen)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            binding.address.setCompoundDrawables(drawable, null, null, null)
            binding.address.setTextColor(resources.getColor(R.color.color_666666))
            binding.address.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            binding.address.text = "??????????????????????????????"

            binding.retry.setCompoundDrawables(null, null, null, null)
            binding.retry.text = "????????????"
        }


    }


    /**
     * ?????? popup
     */
    fun showLatePopup(title:String){
        XPopup
            .Builder(requireActivity())
            .isDestroyOnDismiss(true)
            .asCustom(RemarkPopup(requireActivity(),"${title}????????????","?????????${title}?????????????????????","${title}??????").apply {
                setOnClickListener(object : RemarkPopup.OnClickListener{
                    override fun onClick(remark: String) {
                        this@apply.dismiss()
                        val body = ClockInfoRequest(
                            viewModel.clockInfo.value!!.address.id,
                            remark,
                            "${currentLocation!!.longitude},${currentLocation!!.latitude}",
                            viewModel.clockInfo.value!!.shouldClockIn!!.id,
                        )
                        viewModel.postClockIn(body)
                    }
                })
            })
            .show()
    }

    /**
     * ?????? popup
     */
    fun calendarPopup(){
        XPopup
            .Builder(requireActivity())
            .asCustom(calendarPopup)
            .show()
    }



}