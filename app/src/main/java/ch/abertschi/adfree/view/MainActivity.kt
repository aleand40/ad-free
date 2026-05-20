/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.view

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import ch.abertschi.adfree.AdFreeApplication
import ch.abertschi.adfree.R
import ch.abertschi.adfree.view.home.HomeActivity
import ch.abertschi.adfree.view.about.AboutActivity
import ch.abertschi.adfree.view.setting.SettingsActivity
import com.google.android.material.tabs.TabLayout

/**
 * Created by abertschi on 21.04.17.
 */

class MainActivity : FragmentActivity() {

    companion object {
        private const val NUM_PAGES = 3
    }

    private var mPager: ViewPager? = null
    private var mPagerAdapter: PagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        mPager = findViewById(R.id.pager)
        mPagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager!!.adapter = mPagerAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tabDots)
        tabLayout.setupWithViewPager(mPager, true)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorBackground)

        // XXX: Workaround, global access to activity to prevent detached fragments
        val app = applicationContext as AdFreeApplication
        app.mainActivity = this
    }

    private class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> HomeActivity()
                1 -> SettingsActivity()
                else -> AboutActivity()
            }
        }

        override fun getCount(): Int {
            return NUM_PAGES
        }
    }
}