package com.fourcode.clients.fashion

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import com.fourcode.clients.fashion.arcore.ARActivity
import com.fourcode.clients.fashion.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(),
    OnNavigationItemSelectedListener,
    OnBackStackChangedListener {

    internal lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize firestore
        firestore = FirebaseFirestore.getInstance()

        // Config views
        navigation.setOnNavigationItemSelectedListener(this)

        // Initial fragment is home
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, HomeFragment.newInstance())
            .commit()

        supportFragmentManager.addOnBackStackChangedListener(this)
        onBackStackChanged()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                return true
            }
            R.id.navigation_ar -> {
                startActivity<ARActivity>()
                return true
            }
//            R.id.navigation_notifications -> {
//                return true
//            }

            R.id.navigation_profile-> {
                return true
            }
        }

        return false
    }

    override fun onBackStackChanged() {

        val isBackStackMany = supportFragmentManager.backStackEntryCount > 0

        supportActionBar?.setDisplayHomeAsUpEnabled(isBackStackMany)
        title = if (isBackStackMany) supportFragmentManager
            .getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
        else getString(R.string.title_home)


    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }
}
