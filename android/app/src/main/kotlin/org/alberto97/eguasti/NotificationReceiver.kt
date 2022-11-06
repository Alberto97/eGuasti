package org.alberto97.eguasti

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class NotificationReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "NotificationReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        val id = bundle?.getInt("id")
        val title = bundle?.getString("title")
        val text = bundle?.getString("text")

        Log.i(TAG, "Posting notification - id: $id - title: $title - text: $text")

        val helper = NotificationHelper(context)
        helper.postNotification(id!!, title!!, text!!)
    }
}