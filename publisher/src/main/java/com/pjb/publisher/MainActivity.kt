package com.pjb.publisher

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pjb.publisher.ui.Greeting
import com.pjb.publisher.ui.theme.IPCTestTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val MSG_FROM_CLIENT = 1001
    }

    private lateinit var serviceConnection: ServiceConnection
    private lateinit var serverMessenger: Messenger
    private val clientMessenger: Messenger = Messenger(object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {

        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IPCTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android") {
                        sendMessage("aaa")
                    }
                }
            }
        }
        bindReceiveService()
    }

    /**
     * 绑定接受端的Service
     */
    private fun bindReceiveService() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.e(TAG, "onServiceConnected: !!!!!!!!")
                serverMessenger = Messenger(service)
                val message = Message.obtain(null, MSG_FROM_CLIENT)
                val data = Bundle()
                data.putString("msg", "client msg: 1")
                message.data = data
                message.replyTo = clientMessenger
                try {
                    serverMessenger.send(message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                Log.d(TAG, "ServiceDisconnected")
            }
        }
        val intent = Intent().apply {
            component = ComponentName("com.pjb.receiver1", "com.pjb.receiver1.ReceiveService")
        }

        val b = applicationContext.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        Log.e(TAG, "bindReceiveService: $b")
    }

    private fun sendMessage(text: String): Unit {
        val message = Message.obtain(null, MSG_FROM_CLIENT)
        val data = Bundle()
        data.putString("msg", text)
        message.data = data
        message.replyTo = clientMessenger
        try {
            serverMessenger.send(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }
}