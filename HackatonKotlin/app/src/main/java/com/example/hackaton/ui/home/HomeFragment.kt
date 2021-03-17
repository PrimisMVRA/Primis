package com.example.hackaton.ui.home

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.support.v4.app.Fragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.widget.Switch
import com.example.hackaton.MainActivity
import com.example.hackaton.R


private const val PERM_REQ_CODE = 1;
private val perms = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.READ_PHONE_STATE
)
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val logTextView: TextView = root.findViewById(R.id.homeTextViewLog)

        val switchService : Switch = root.findViewById(R.id.homeSwIsEnabled)
        switchService.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                if (hasPermissions(perms)){
                    (activity as MainActivity).tryStartListenerService()
                }
                else {
                    perms.forEach {
                        shouldShowRequestPermissionRationale(it)
                    }
                    requestPermissions(perms, PERM_REQ_CODE)
                }
            } else{
                (activity as MainActivity).stopListenerService()
            }
        }

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            logTextView.text = it
        })
        return root
    }

    private fun hasPermissions(perms : Array<String>) : Boolean = perms.all {
        ContextCompat.checkSelfPermission(activity!!.baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}