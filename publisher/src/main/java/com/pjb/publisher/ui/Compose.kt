package com.pjb.publisher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun Greeting(name: String, onSendClick1: () -> Unit, onSendClick2: () -> Unit, text: MutableState<String>) {
    Column {
        Button(
            onClick = onSendClick1,
            modifier = Modifier
                .fillMaxHeight(0.2f)
                .fillMaxWidth(1f)
        ) {
            Text(text = "发送给1")
        }
        Button(
            onClick = onSendClick2,
            modifier = Modifier
                .fillMaxHeight(0.25f)
                .fillMaxWidth(1f)
        ) {
            Text(text = "发送给2")
        }
        TextField(value = text.value, onValueChange = {text.value = it})
    }

}
