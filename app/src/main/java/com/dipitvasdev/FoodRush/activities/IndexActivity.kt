package com.dipitvasdev.FoodRush.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.dipitvasdev.FoodRush.R
import com.dipitvasdev.FoodRush.fragment.Dashboard
import com.dipitvasdev.FoodRush.fragment.FavouriteRestaurantFragment
import com.dipitvasdev.FoodRush.fragment.ProfileFragment
import com.dipitvasdev.FoodRush.fragment.faqsFragment

lateinit var coordinatorLayout: CoordinatorLayout
lateinit var toolbar: androidx.appcompat.widget.Toolbar
lateinit var frameLayout: FrameLayout
lateinit var navigationView: NavigationView
lateinit var drawerLayout: DrawerLayout
lateinit var textViewcurrentUser: TextView
lateinit var textViewMobileNumber: TextView
lateinit var sharedPreferencess: SharedPreferences
var previousMenuItemSelected: MenuItem? = null

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sharedPreferencess = getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolBar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        val headerView = navigationView.getHeaderView(0)
        textViewcurrentUser = headerView.findViewById(R.id.textViewcurrentUser)
        textViewMobileNumber = headerView.findViewById(R.id.textViewMobileNumber)
        navigationView.menu.getItem(0).setCheckable(true)
        navigationView.menu.getItem(0).setChecked(true)
        setToolBar()
        textViewcurrentUser.text = sharedPreferencess.getString("user_name", "Flashkp")
        textViewMobileNumber.text = "+91-" + sharedPreferencess.getString("mobile", "9999999999")


        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@HomeActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItemSelected != null) {
                previousMenuItemSelected?.setChecked(false)
            }

            previousMenuItemSelected = it

            it.setCheckable(true)
            it.setChecked(true)


            when (it.itemId) {
                R.id.homee -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            ProfileFragment(this)
                        )
                        .commit()

                    supportActionBar?.title = "My Profile"

                    drawerLayout.closeDrawers()

                    Toast.makeText(this@HomeActivity, "My Profile", Toast.LENGTH_SHORT).show()
                }
                R.id.favouriteRestaurants -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FavouriteRestaurantFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Favourite Restaurants"

                    drawerLayout.closeDrawers()

                }
                R.id.orderHistory -> {
                    val intent = Intent(this, OrderHistoryActivity::class.java)

                    drawerLayout.closeDrawers()

                    startActivity(intent)

                }
                R.id.faqs -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            faqsFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Frequently Asked Questions"

                    drawerLayout.closeDrawers()

                    Toast.makeText(this@HomeActivity, "FAQs", Toast.LENGTH_SHORT).show()
                }
                R.id.logout -> {

                    drawerLayout.closeDrawers()

                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)

                    alterDialog.setTitle("Warning!")
                    alterDialog.setMessage("You'll be logged out of the app!")
                    alterDialog.setPositiveButton("LOG OUT") { text, listener ->
                        sharedPreferencess.edit().putBoolean("isLoggedIn", false).apply()

                        ActivityCompat.finishAffinity(this)
                    }

                    alterDialog.setNegativeButton("STAY LOGGED IN") { text, listener ->

                    }
                    alterDialog.create()
                    alterDialog.show()

                }
            }
            return@setNavigationItemSelectedListener true
        }
        openDashboard()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)

        when (currentFragment) {
            !is Dashboard -> {
                navigationView.menu.getItem(0).setChecked(true)
                openDashboard()
            }
            else -> super.onBackPressed()
        }
    }

    fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu2)
    }


    fun openDashboard() {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(
            R.id.frameLayout,
            Dashboard()
        )
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.homee)
    }


    override fun onResume() {
        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        super.onResume()
    }

}