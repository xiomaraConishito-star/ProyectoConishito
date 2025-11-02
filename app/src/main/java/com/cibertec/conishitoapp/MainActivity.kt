package com.cibertec.conishitoapp

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.cibertec.conishitoapp.databinding.ActivityMainBinding
import com.cibertec.conishitoapp.ui.fav.FavoritesFragment
import com.cibertec.conishitoapp.ui.home.HomeFragment
import com.cibertec.conishitoapp.ui.report.ReportPetFragment
import com.cibertec.conishitoapp.ui.store.CartFragment
import com.cibertec.conishitoapp.ui.store.StoreFragment
import com.cibertec.conishitoapp.ui.user.UserFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val splashVm: SplashViewModel by lazy { SplashViewModel() }
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash: SplashScreen = installSplashScreen()
        splash.setKeepOnScreenCondition { splashVm.isLoading }
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_open,
            R.string.navigation_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Manejar la navegación del Drawer
        binding.navigationView.setNavigationItemSelectedListener(this)

        // Manejar back gestures / botón atrás con OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    // delegar al comportamiento por defecto
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })

        if (savedInstanceState == null) {
            navigateTo(R.id.nav_home, shouldCheck = true)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navigateTo(item.itemId, shouldCheck = true)
        return true
    }

    fun navigateTo(itemId: Int, shouldCheck: Boolean = false) {
        val (fragment, titleRes) = when (itemId) {
            R.id.nav_home -> HomeFragment() to R.string.home_title
            R.id.nav_user -> UserFragment() to R.string.user_title
            R.id.nav_fav -> FavoritesFragment() to R.string.favorites_title
            R.id.nav_report -> ReportPetFragment() to R.string.report_title
            R.id.nav_store -> StoreFragment() to R.string.store_title
            R.id.nav_cart -> CartFragment() to R.string.cart_title
            else -> null to null
        }

        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, it)
                .commit()
            titleRes?.let { res -> binding.toolbar.setTitle(res) }
            if (shouldCheck) {
                binding.navigationView.setCheckedItem(itemId)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
}
