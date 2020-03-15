package com.ydhnwb.frozonecashier

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ydhnwb.frozonecashier.utils.BranchPopup
import com.ydhnwb.frozonecashier.utils.JusticeUtils
import com.ydhnwb.frozonecashier.viewmodels.SettingState
import com.ydhnwb.frozonecashier.viewmodels.SettingViewModel
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.content_setting.*

class SettingActivity : AppCompatActivity() {
    private lateinit var settingViewModel : SettingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        settingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        settingViewModel.listenToUIState().observe(this, Observer {
            when(it){
                is SettingState.SelectedBranch -> {
                    tv_branch.text = it.branchName
                }
            }
        })
        setting_branch.setOnClickListener {
            BranchPopup().show(supportFragmentManager, "branch_popup")
        }
        setting_device_name.setOnClickListener {}
        val deviceId = JusticeUtils.getDeviceId(this)
        deviceId?.let {
            tv_device.text = it
        }
    }

    override fun onResume() {
        super.onResume()
        val branchName = JusticeUtils.getCurrentBranchName(this)
        branchName?.let {
            tv_branch.text = it
        } ?: run {
            tv_branch.text = resources.getString(R.string.info_no_branch_selected)
        }
    }

}
