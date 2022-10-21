package com.pjb.receiver1

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReceiveService : Service() {
    companion object {
        const val TAG = "ReceiveService"
        const val MSG_FROM_CLIENT = 1001
        const val MSG_FROM_ACTIVITY = 1002
    }
    private lateinit var activityMessenger: Messenger

    inner class MessengerHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                MSG_FROM_CLIENT -> {
                    val content = msg.data.getString("msg")
                    Log.e(TAG, "handleMessage: $content")
                    Toast.makeText(this@ReceiveService, "receive message", Toast.LENGTH_SHORT)
                        .show()
                    GlobalScope.launch() {
                        withContext(Dispatchers.IO) {
                            insertMessage(content ?: "content")
                            // 发送更新UI的消息
                            val message = android.os.Message()
                            val data = Bundle()
                            data.putBoolean("refresh", true)
                            message.data = data
                            activityMessenger.send(message)
                        }
                    }
                }
                MSG_FROM_ACTIVITY -> {
                    activityMessenger = msg.replyTo
                }
            }
        }
    }

    private fun insertMessage(content: String) {
        val messageDao = MessageDatabase.getDatabase(this@ReceiveService).messageDao()
        messageDao.loadAllMessages().forEach {
            if (it.content == content) {
                return
            }
        }
        messageDao.insertMessage(
            Message(content)
        )
    }

    private val messenger = Messenger(MessengerHandler(Looper.myLooper()!!))

    override fun onBind(intent: Intent): IBinder {
        return messenger.binder
    }
}