package com.pjb.receiver2

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReceiveService2 : Service() {

    companion object {
        const val TAG = "ReceiveService2"
    }
    private val binder = object : IRemoteService.Stub() {
        override fun sendMessage(content: String) {
            Log.d(TAG, "sendMessage: receive $content")
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    insertMessage(content)
                }
            }
        }

        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun insertMessage(content: String) {
        val messageDao = MessageDatabase.getDatabase(this@ReceiveService2).messageDao()
        messageDao.loadAllMessages().forEach {
            if (it.content == content) {
                return
            }
        }
        messageDao.insertMessage(
            Message(content)
        )
        Log.d(TAG, "insertMessage: save successfully")
    }
}