package com.example.demoadmin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.demoadmin.Adapter.CategoryAdapter
import com.example.demoadmin.Model.CategoryModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var bottomNavigation: MeowBottomNavigation
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var recyclerView: RecyclerView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val LOCATION_ENABLE_REQUEST_CODE = 1002
    private lateinit var auth: FirebaseAuth
    private lateinit var titleTextView: TextView

    private val Home = 1
    private val Driver = 2
    private val User = 3

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNavigation = findViewById(R.id.BottomNavigation)
        bottomNavigation.add(MeowBottomNavigation.Model(Home, R.drawable.home))
        bottomNavigation.add(MeowBottomNavigation.Model(Driver, R.drawable.driver))
        bottomNavigation.add(MeowBottomNavigation.Model(User, R.drawable.user))
        titleTextView = findViewById(R.id.TxTitle)
        auth = FirebaseAuth.getInstance()

        bottomNavigation.show(Home, true)

        bottomNavigation.setOnClickMenuListener { model ->
            // Handle click events for bottom navigation items
            when (model.id) {
                Home -> {
                    // Start HomeActivity
                    startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
                    finish() // Finish the current activity if you don't want to keep it in the back stack
                }
                 Driver -> {
                    // Start DriverActivity
                    startActivity(Intent(this@HomeActivity, DriverApprovalListActivity::class.java))
                    finish() // Finish the current activity if you don't want to keep it in the back stack
                }
                User -> {
                    // Start UserActivity
                    startActivity(Intent(this@HomeActivity, UserRequestsActivity::class.java))
                    finish() // Finish the current activity if you don't want to keep it in the back stack
                }
            }
        }

        if (auth.currentUser == null) {
            // User is not registered, start registration form activity
            startActivity(Intent(this@HomeActivity, CapRegistrationFormActivity::class.java))
            finish() // Finish the home activity so the user can't come back without registration
            showToast("Please register to continue")
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                // Permission already granted, check if location is enabled
                checkLocationEnabled()
            }
        }

        val imgMenu: ImageView = findViewById(R.id.ImgMenu)
        drawerLayout = findViewById(R.id.DrawerLayout)
        navigationView = findViewById(R.id.nav_view)
        imgMenu.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val list: ArrayList<CategoryModel> = ArrayList()

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        recyclerView = findViewById(R.id.RecyclerAddCategory)

        val layoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = layoutManager
        val categoryAdapter = CategoryAdapter(this, list)
        recyclerView.adapter = categoryAdapter

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val model = dataSnapshot.getValue(CategoryModel::class.java)
                        model?.let { list.add(it) }
                    }
                    categoryAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@HomeActivity, "Category Not Exist", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        }

        database.getReference().child("categories").addValueEventListener(valueEventListener)
    }

    private fun checkLocationEnabled() {
        if (!isLocationEnabled()) {
            // Location is not enabled, prompt the user to enable it
            val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(locationSettingsIntent, LOCATION_ENABLE_REQUEST_CODE)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, check if location is enabled
                checkLocationEnabled()
            } else {
                // Permission denied, show a message or take appropriate action
                showToast("Location permission denied!")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_ENABLE_REQUEST_CODE) {
            // Check if location is enabled after the user's action
            checkLocationEnabled()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Finish the activity
            finish()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_Home -> {
                // Open Activity for nav_item1
                startActivity(Intent(this, HomeActivity::class.java))
            }

            R.id.item_logout -> {
                auth.signOut()
                startActivity(Intent(this, PhoneActivity::class.java))
         }

            R.id.item_Help ->
            {
                startActivity(Intent(this,HelpActivity::class.java))
            }

            R.id.item_FeedBAck ->
            {
                startActivity(Intent(this,FeedBackActivity::class.java))
            }

            R.id.item_Profile ->
            {
                startActivity(Intent(this,ProfileActivity::class.java))
            }
            // Add more cases for other menu items as needed
            R.id.item_Share -> shareApp(this)
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun shareApp(context: Context) {
        val appPackageName = context.packageName
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out this amazing app: https://play.google.com/store/apps/details?id=$appPackageName"
        )
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}
