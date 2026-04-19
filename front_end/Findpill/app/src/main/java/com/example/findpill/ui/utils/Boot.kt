package com.example.findpill

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Boot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            AlarmUtil.scheduleMidnightRefresh(context)
        }
    }
}