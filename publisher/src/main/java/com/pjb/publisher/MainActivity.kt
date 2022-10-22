package com.pjb.publisher

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.contentValuesOf
import com.pjb.publisher.ui.Greeting
import com.pjb.publisher.ui.theme.IPCTestTheme
import com.pjb.receiver2.IRemoteService

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val MSG_FROM_CLIENT = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var text = remember{mutableStateOf("a")}
            IPCTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android",
                        onSendClick1 = {sendMessage1(text.value)},
                        onSendClick2 = {sendMessage2(text.value)},
                        onSendClick3 = {sendMessage3(text.value)},
                        text=text
                    )
                }
            }
        }
        bindReceiveService1()
        bindReceiveService2()
    }

    private var serviceConnection1 = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(TAG, "onServiceConnected: !!!!!!!!")
            serverMessenger1 = Messenger(service)
            val message = Message.obtain(null, MSG_FROM_CLIENT)
            val data = Bundle()
            data.putString("msg", "client msg: 1")
            message.data = data
            try {
                serverMessenger1.send(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d(TAG, "ServiceDisconnected")
        }
    }
    private lateinit var serverMessenger1: Messenger

    /**
     * 绑定接收端1
     */
    private fun bindReceiveService1() {
        val intent = Intent().apply {
            component = ComponentName("com.pjb.receiver1", "com.pjb.receiver1.ReceiveService")
        }
        val b = bindService(intent, serviceConnection1, BIND_AUTO_CREATE)
        Log.e(TAG, "bindReceiveService: $b")
    }

    private fun sendMessage1(text: String): Unit {
        val message = Message.obtain(null, MSG_FROM_CLIENT)
        val data = Bundle()
        data.putString("msg", text)
        message.data = data
        try {
            serverMessenger1.send(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var iRemoteService: IRemoteService? = null
    private var serviceConnection2 = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(TAG, "receiver2 connected!", )
            iRemoteService = IRemoteService.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e(TAG, "onServiceDisconnected: 2")
            iRemoteService = null
        }

    }
    /**
     * 绑定接收端2
     */
    private fun bindReceiveService2() {
        val intent = Intent().apply {
            component = ComponentName("com.pjb.receiver2", "com.pjb.receiver2.ReceiveService2")
        }
        applicationContext.bindService(intent, serviceConnection2, BIND_AUTO_CREATE)
        iRemoteService?.sendMessage("first message")
    }
    private fun sendMessage2(text: String) {
        iRemoteService?.sendMessage(text)
    }

    /**
     * 接收端3
     */
    private fun sendMessage3(text: String) {
        val uri = Uri.parse("content://com.pjb.receiver3.provider/Message")
        val values = contentValuesOf("content" to text)
        contentResolver.insert(uri, values)
    }

    override fun onDestroy() {
        unbindService(serviceConnection1)
        unbindService(serviceConnection2)
        super.onDestroy()
    }
}