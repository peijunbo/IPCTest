package com.pjb.receiver1.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.pjb.receiver1.Message

@Composable
fun Greeting(name: String, messages: List<Message>) {
    LazyColumn {
        items(messages) {
            Text(text = it.content)
        }
    }
}