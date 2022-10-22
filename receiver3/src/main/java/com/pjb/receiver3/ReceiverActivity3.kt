package com.pjb.receiver3

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.pjb.receiver3.ui.theme.IPCTestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class ReceiverActivity3 : ComponentActivity() {
    companion object {
        const val TAG = "ReceiverActivity3"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val messageDao = MessageDatabase.getDatabase(this).messageDao()
        setContent {
            val messages = remember {mutableStateListOf<Message>()}

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    messages.addAll(messageDao.loadAllMessages())
                    MessageProvider.receiveFlow.collect {
                        Log.d(TAG, "collect $it")
                        if (it != 0L) {
                            messages.add(messageDao.getMessageById(it)[0])
                        }
                    }
                }
            }
            IPCTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting(messages = messages)
                }
            }
        }
    }
}

@Composable
fun Greeting(messages: List<Message>) {
    LazyColumn {
        items(messages) {
            Text(text = it.content)
        }
    }
}