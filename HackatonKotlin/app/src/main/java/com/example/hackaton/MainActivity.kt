package com.example.hackaton

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.text.BoringLayout
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hackaton.service.ListenerService
import com.example.hackaton.service.UserAppDeviceAdmin
import java.lang.Exception
private const val REQUEST_CODE_ADM : Int = 0
class MainActivity : AppCompatActivity() {
    var isServiceStarted = false
    private lateinit var mDPM : DevicePolicyManager
    private lateinit var mAdminName : ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_settings))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun tryStartListenerService(){
        try {
            mDPM = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            mAdminName = ComponentName(this, UserAppDeviceAdmin::class.java)
            if (!mDPM.isAdminActive(mAdminName)){
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.app_admin_activation)
                startActivityForResult(intent, REQUEST_CODE_ADM)
            }
            else{
                val intent = Intent(this, ListenerService::class.java)
                startService(intent)
                isServiceStarted = true;
            }
        } catch (ex : Exception){
            Log.e("ADM_DEV Exception", if (ex.message != null) ex.message!! else "")
        }
    }

    fun stopListenerService(){
        val intent = Intent(this, ListenerService::class.java)
        stopService(intent)
        isServiceStarted = false;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADM){
            val intent = Intent(this, ListenerService::class.java)
            startService(intent)
            isServiceStarted = true;
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(intent)
    }
}