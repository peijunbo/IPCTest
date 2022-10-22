package com.pjb.receiver3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pjb.receiver3.ui.theme.IPCTestTheme

class ReceiverActivity3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val messageDao = MessageDatabase.getDatabase(this).messageDao()
        setContent {
            val messages by messageDao.getMessagesFlow().collectAsState(mutableListOf())
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
}

@Composable
fun Greeting(name: String, messages: List<Message>) {
    LazyColumn {
        items(messages) {
            Text(text = it.content)
        }
    }
}