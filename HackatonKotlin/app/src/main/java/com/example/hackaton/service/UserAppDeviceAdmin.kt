package com.example.hackaton.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class UserAppDeviceAdmin : DeviceAdminReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context, intent: Intent) {

    }

    override fun onDisabled(context: Context, intent: Intent) {

    }
}