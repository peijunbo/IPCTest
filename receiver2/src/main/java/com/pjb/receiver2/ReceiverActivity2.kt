package com.pjb.receiver2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pjb.receiver2.ui.Greeting
import com.pjb.receiver2.ui.theme.IPCTestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReceiverActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val messageDao = MessageDatabase.getDatabase(this).messageDao()
        setContent {
            val messages = remember { mutableStateListOf<Message>() }
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    messages.addAll(messageDao.loadAllMessages())
                }
            }
            IPCTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android", messages=messages)
                }
            }
        }
    }
}