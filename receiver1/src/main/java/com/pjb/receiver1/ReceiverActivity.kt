package com.pjb.receiver1

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Messenger
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pjb.receiver1.ui.Greeting
import com.pjb.receiver1.ui.theme.IPCTestTheme
import kotlinx.coroutines.*

class ReceiverActivity : ComponentActivity() {
    companion object {
        const val TAG = "ReceiverActivity"
        const val MSG_FROM_ACTIVITY = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val messageDao = MessageDatabase.getDatabase(this).messageDao()
        setContent {
            val composeScope = rememberCoroutineScope()
            val messages = remember { mutableStateListOf<Message>() }
            // 处理界面更新的handler
            val refreshHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: android.os.Message) {
                    val content = msg.data.getBoolean("refresh")
                    if (content) {
                        Toast.makeText(this@ReceiverActivity, "更新UI", Toast.LENGTH_SHORT).show()
                        composeScope.launch {
                            withContext(Dispatchers.IO) {
                                messages.clear()
                                messages.addAll(messageDao.loadAllMessages())
                                delay(1000)
                            }
                        }
                    }
                }
            }
            // 创建Messenger
            val messenger = Messenger(refreshHandler)
            // 连接service
            val intent = Intent(this, ReceiveService::class.java)
            val connection = object :ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val serviceMessenger = Messenger(service)
                    val message = android.os.Message.obtain(null, MSG_FROM_ACTIVITY)
                    message.replyTo = messenger
                    serviceMessenger.send(message)
                }
                override fun onServiceDisconnected(p0: ComponentName?) {
                    Log.e(TAG, "onServiceDisconnected: ", )
                }
            }
            bindService(intent, connection, BIND_AUTO_CREATE)
            IPCTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android", messages = messages)
                }
            }
        }
    }

    fun bindService() {

    }
}