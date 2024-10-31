package com.zzh.echarging

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.zzh.echarging.databinding.ActivityMainZzhBinding
import com.zzh.echarging.fragment.zzh_AllFragment
import com.zzh.echarging.fragment.zzh_NewFragment
import com.zzh.echarging.fragment.zzh_PersonalFragment

class zzh_MainActivity : BaseBindingActivity<ActivityMainZzhBinding>() {
    override var layoutRes: Int
        get() = R.layout.activity_main_zzh
        set(value) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.exit -> {
                        user = null
                        startActivity(Intent(this@zzh_MainActivity, LoginActivity::class.java))
                        finish()

                    }
                    R.id.add -> {
                        zzh_NewFragment().show(supportFragmentManager, "new")
                    }
                }
                true
            }

            vp2.adapter = FragmentAdapter()

            TabLayoutMediator(tablayout, vp2) { tab, position ->
                when (position) {
                    0 -> tab.text = "充电桩"
                    1 -> tab.text = "已占用"
                    2 -> tab.text = "未占用"
                    3 -> tab.text = "个人"
                }
            }.attach()
        }
    }

    inner class FragmentAdapter : FragmentStateAdapter(this) {

        val fragments = listOf(
            zzh_AllFragment {
                it.id.isNotEmpty()
            },
            zzh_AllFragment {
                it.isOccupied
            },
            zzh_AllFragment {
                it.isOccupied.not()
            },
            zzh_PersonalFragment()
        )

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}