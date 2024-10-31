package com.zzh.echarging.fragment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.coroutineScope
import com.zzh.echarging.BaseBindingFragment
import com.zzh.echarging.R
import com.zzh.echarging.databinding.FragmentPersonalZzhBinding
import com.zzh.echarging.getDao
import com.zzh.echarging.runOnUiThread
import com.zzh.echarging.runOnWorkThread
import com.zzh.echarging.toast
import com.zzh.echarging.user
import kotlinx.coroutines.launch

class zzh_PersonalFragment : BaseBindingFragment<FragmentPersonalZzhBinding>() {
    override var layoutRes: Int
        get() = R.layout.fragment_personal_zzh
        set(value) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {

            lifecycle.coroutineScope.launch {
                getDao().getUserAsFlow().collect() {
                    it.first { it.chepaihao == user!!.chepaihao }?.let {
                        username.text = "车牌号: ${it.chepaihao}"
                        password.text = "密码: ${it.password}"

                        chepaohao.text = it.chepaihao
                        progress.setProgress(it.chargingProgress, true)
                    }
                }
            }

            editProgress.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    var progress = editProgress.text.toString().toInt()
                    if (progress > 100) {
                        progress = 100
                    }

                    runOnWorkThread {

                        val dao = getDao()
                        user!!.chargingProgress = progress
                        dao.updateUser(user!!)

                        dao.findPlieByChePai(user!!.chepaihao)?.let {
                            it.progress = progress
                            dao.updatePlie(it)
                        }

                        runOnUiThread {
                            toast("设置进度为 ${progress}%")
                        }
                    }

                    true
                }
                false
            }

            user?.let {
                username.text = "车牌号: ${it.chepaihao}"
                password.text = "密码: ${it.password}"

                chepaohao.text = it.chepaihao
                progress.setProgress(it.chargingProgress, true)
            }

            stop.setOnClickListener {
                runOnWorkThread {
                    val progress = 0
                    val dao = getDao()
                    user!!.chargingProgress = progress
                    user!!.isCharging = false
                    dao.updateUser(user!!)

                    dao.findPlieByChePai(user!!.chepaihao)?.let {
                        it.isOccupied = false
                        it.progress = progress
                        dao.updatePlie(it)
                    }
                }
            }
        }
    }

}