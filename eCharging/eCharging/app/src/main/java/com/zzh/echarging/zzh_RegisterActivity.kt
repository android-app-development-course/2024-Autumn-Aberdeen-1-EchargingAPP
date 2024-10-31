package com.zzh.echarging

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.zzh.echarging.databinding.ActivityRegisterZzhBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class zzh_RegisterActivity : BaseBindingActivity<ActivityRegisterZzhBinding>() {
    override var layoutRes: Int
        get() = R.layout.activity_register_zzh
        set(value) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {

            uid.bindVehicleKeyboard()

            register.setOnClickListener {
                val count1 = uid.text.toString()
                val password1 = ps.text.toString()
                if (count1.isEmpty() || password1.isEmpty()) {
                    toast("输入完整信息")
                    return@setOnClickListener
                }

                lifecycleScope.launch(Dispatchers.Default) {
                    getDao().getUserByChepaiHao(count1).let {
                        if (it != null) {
                            launch(Dispatchers.Main) {
                                toast("车牌已存在")
                            }
                            return@launch
                        } else {
                            getDao().insertUser(User(count1, password1))
                            launch(Dispatchers.Main) {
                                toast("注册成功")
                            }
                            finish()
                        }
                    }
                }
            }
        }
    }
}