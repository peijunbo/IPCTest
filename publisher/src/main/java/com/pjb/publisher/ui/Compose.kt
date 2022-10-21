package com.pjb.publisher.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun Greeting(name: String, onSendClick: () -> Unit) {
    Text(text = "Hello $name!")
    Button(onClick = onSendClick) {
        Text(text = "点击")
    }
}
