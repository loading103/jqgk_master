package com.daqsoft.module_mine.activity

import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.adapter.ScenicListAdapter
import com.daqsoft.module_mine.databinding.ActivityScenicListBinding
import com.daqsoft.module_mine.databinding.ActivitySetUpBinding
import com.daqsoft.module_mine.repository.pojo.vo.Company
import com.daqsoft.module_mine.repository.pojo.vo.CompanyInfo
import com.daqsoft.module_mine.viewmodel.ScenicListViewModel
import com.daqsoft.module_mine.viewmodel.SetUpViewModel
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 28/6/2021 下午 1:43
 * @author zp
 * @describe 设置
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_SET_UP)
class SetUpActivity : AppBaseActivity<ActivitySetUpBinding, SetUpViewModel>() {


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_set_up
    }

    override fun initViewModel(): SetUpViewModel? {
        return viewModels<SetUpViewModel>().value
    }

    override fun initView() {
        super.initView()

        initOnClick()
    }

    private fun initOnClick() {

        binding.audioSwitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

                Timber.e("audioSwitch ${isChecked}")
            }

        })

        binding.vibratorSwitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

                Timber.e("vibratorSwitch ${isChecked}")
            }

        })

    }

}