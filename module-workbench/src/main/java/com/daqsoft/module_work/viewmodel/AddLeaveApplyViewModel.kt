package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_common.bean.LeaveType
import com.daqsoft.module_work.R
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding


/**
 * @ClassName    AddLeaveApplyViewModel
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/5/7
 */
class AddLeaveApplyViewModel : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository : WorkRepository):super(application,workRepository)

    /**
     * 给RecyclerView添加ObservableList
     */
    val items: ObservableList<String> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    val itemBinding = ItemBinding.of<String>(BR.viewModel, R.layout.recyclerview_employee_popup_item)

    val StartTimeLiveData = MutableLiveData<String?>()

    val EndTimeLiveData = MutableLiveData<String?>()

    val LeaveTypeLiveData = MutableLiveData<String?>()
    /**
     * 请假类型（todo  等接口从后台获取数据）
     */
    val LeaveTypeChoose: MutableList<LeaveType> = mutableListOf(
        LeaveType("1", "事假"),
        LeaveType("2", "病假"),
        LeaveType("3", "产假(剩余15天)"),
        LeaveType("4", "年假(剩余1天)"),
        LeaveType("5", "婚假"),
        LeaveType("6", "丧假"),
        LeaveType("7", "调休(剩余1小时)"),
    )

    /**
     * 单位
     */
    val unitString= ObservableField<String>("小时")
    /**
     * 选择时间有没有时分  true 代表年月日时分
     */
    val haveHour= MutableLiveData<Boolean?>(true)



    override fun onCreate() {
        initToolbar()
        if(haveHour?.value as Boolean){
            unitString.set("小时")
        }else{
            unitString.set("天")
        }

        items.add("hahah")
        items.add("hahah")
        items.add("hahah")
        items.add("hahah")
        items.add("hahah")
        items.add("hahah")
    }


    private fun initToolbar(){
        setBackground(R.color.white)
        //    setBackIconSrc(R.mipmap.project_top_return_white)
        setTitleTextColor(R.color.color_333333)
        setTitleText(getApplication<Application>().resources.getString(R.string.module_workbench_add_leave_apply))
    }

    /**
     * 点击选择请假类型
     */
    val onClickChooseType = BindingCommand<Unit>(BindingAction {
        LeaveTypeLiveData.postValue(null)
    })
    /**
     * 点击选择结束时间
     */
    val onClickEndTime = BindingCommand<Unit>(BindingAction {
        EndTimeLiveData.postValue(null)
    })

    /**
     * 点击选择开始时间
     */
    val onClickStartTime = BindingCommand<Unit>(BindingAction {
        StartTimeLiveData.postValue(null)
    })
    /**
     * 图片点击事件
     */
    var avatarLiveData = MutableLiveData<Unit>()
    val AddIconOnClick = BindingCommand<Unit>(BindingAction {
        avatarLiveData.value = null
    })
}