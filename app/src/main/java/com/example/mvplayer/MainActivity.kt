package com.example.mvplayer

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.mvplayer.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val scope = CoroutineScope(Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureBottomNavigation()
    }

    private fun configureBottomNavigation() {
            val vp_ac_main_frag_pager: ViewPager = findViewById(R.id.vp_ac_main_frag_pager)
            vp_ac_main_frag_pager.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 3)

            val  tl_ac_main_bottom_menu: TabLayout = findViewById(R.id.tl_ac_main_bottom_menu)
            tl_ac_main_bottom_menu.setupWithViewPager(vp_ac_main_frag_pager)

            val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

            tl_ac_main_bottom_menu.getTabAt(0)!!.customView =
                bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
            tl_ac_main_bottom_menu.getTabAt(1)!!.customView =
                bottomNaviLayout.findViewById(R.id.btn_bottom_navi_search_tab) as RelativeLayout
            tl_ac_main_bottom_menu.getTabAt(2)!!.customView =
                bottomNaviLayout.findViewById(R.id.btn_bottom_navi_list_tab) as RelativeLayout

    }


}

