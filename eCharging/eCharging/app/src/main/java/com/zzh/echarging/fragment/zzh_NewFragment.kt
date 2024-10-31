package com.zzh.echarging.fragment

import android.os.Bundle
import android.view.View
import com.zzh.echarging.BaseBottomSheetDialogFragment
import com.zzh.echarging.Plie
import com.zzh.echarging.R
import com.zzh.echarging.databinding.FragmentNewZzhBinding
import com.zzh.echarging.getDao
import com.zzh.echarging.runOnUiThread
import com.zzh.echarging.runOnWorkThread
import com.zzh.echarging.toast


class zzh_NewFragment : BaseBottomSheetDialogFragment<FragmentNewZzhBinding>() {
    override var layoutRes: Int
        get() = R.layout.fragment_new_zzh
        set(value) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            tijiao.setOnClickListener {
                if (name.text.isEmpty() || dizhi.text.isEmpty() || jiage.text.isEmpty() || gonglv.text.isEmpty()){
                    toast("请填写完整信息")
                    return@setOnClickListener
                } else {
                    runOnWorkThread {
                        getDao().insert(
                            Plie(
                                id = name.text.toString(),
                                location = dizhi.text.toString(),
                                pricePerKw = jiage.text.toString().toDouble(),
                                powerOutput = gonglv.text.toString(),
                                isOccupied = false
                            )
                        )
                        runOnUiThread {
                            toast("添加成功")
                            dismiss()
                        }
                    }
                }
            }
        }
    }
}