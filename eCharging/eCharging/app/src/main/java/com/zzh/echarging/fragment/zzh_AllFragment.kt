package com.zzh.echarging.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.coroutineScope
import com.bumptech.glide.Glide
import com.zzh.echarging.AbsBindingRVAdapter
import com.zzh.echarging.BaseBindingFragment
import com.zzh.echarging.Plie
import com.zzh.echarging.R
import com.zzh.echarging.databinding.FragmentAllZzhBinding
import com.zzh.echarging.databinding.ZhuangItemZzhBinding
import com.zzh.echarging.getDao
import com.zzh.echarging.runOnUiThread
import com.zzh.echarging.runOnWorkThread
import com.zzh.echarging.toast
import com.zzh.echarging.user
import kotlinx.coroutines.launch
import kotlin.random.Random

class zzh_AllFragment(
    private val predicate: (Plie) -> Boolean
) : BaseBindingFragment<FragmentAllZzhBinding>() {
    override var layoutRes: Int
        get() = R.layout.fragment_all_zzh
        set(value) {}

    private val plieAdapter by lazy { PlieAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            recyclerview.adapter = plieAdapter
        }

        lifecycle.coroutineScope.launch {
            getDao().getAllPilesAsFlow().collect() {
                plieAdapter.submitList(
                    it.filter { predicate(it) }
                )
            }
        }
    }

    inner class PlieAdapter : AbsBindingRVAdapter<ZhuangItemZzhBinding, Plie>() {
        override val data: MutableList<Plie> = mutableListOf()

        override fun getLayoutRes(): Int {
            return R.layout.zhuang_item_zzh
        }

        override fun getVariableId(): Int {
            return 0
        }

        override fun bindDefaultData(
            holder: BindingViewHolder<ZhuangItemZzhBinding>,
            position: Int
        ) {
            super.bindDefaultData(holder, position)
            val plie = data[position]

            holder.binding.apply {

                Glide.with(requireContext()).load(plie.image).error(resources.getDrawable(R.drawable.logo)).into(iv)

                root.setBackgroundColor(
                    if (!plie.isOccupied) Color.parseColor("#E8F5E9")
                    else Color.TRANSPARENT
                )

                minzi.text = "充电桩名: ${plie.id}"
                dizhi.text = "地址: ${plie.location}"
                jiage.text = "价格: ¥${plie.pricePerKw}/度"
                zhanyong.text = "状态: ${if (plie.isOccupied) "被占用" else "空闲"}"

                gonglv.text = "功率: ${plie.powerOutput}KW"

                progressbar.isVisible = plie.isOccupied

                if (plie.isOccupied) {
                    jindu.text = "充电进度: ${plie.progress}"
                    progressbar.setProgress(plie.progress, true)
                }

                root.setOnClickListener {
                    if (plie.isOccupied) {
                        toast("正在充电不能使用")
                    } else {

                        if (user!!.isCharging) {
                            toast("请先停止充电")
                            return@setOnClickListener
                        }

                        AlertDialog.Builder(requireContext()).apply {
                            setTitle("确认充电")
                            setMessage("是否对车牌为[${user!!.chepaihao}]的新能源车进行充电?")
                            setPositiveButton("确认") { dialog, which ->
                                runOnWorkThread {
                                    user?.let {
                                        // 双更新
                                        it.isCharging = true
                                        val progress = Random.nextInt(100)
                                        it.chargingProgress = progress

                                        plie.isOccupied = true
                                        plie.progress = progress

                                        getDao().updatePlie(plie)
                                        getDao().updateUser(user!!)

                                        runOnUiThread {
                                            toast("${user!!.chepaihao} 开始充电")
                                            dialog.dismiss()
                                        }
                                    }
                                }
                            }
                            setNegativeButton("取消") { dialog, which ->
                                dialog.dismiss()
                            }
                        }.show()
                    }
                }
            }
        }
    }

}