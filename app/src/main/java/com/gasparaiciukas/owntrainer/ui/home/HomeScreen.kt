package com.gasparaiciukas.owntrainer.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen() {
    Column {
        HomeNavigation()
    }
}

@Composable
fun HomeNavigation() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                // Do nothing.
            }
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Filled.ArrowBackIos,
                contentDescription = "Back arrow"
            )
        }
        Text(
            text = "Sample Text",
            style = MaterialTheme.typography.headlineLarge
        )
        IconButton(
            onClick = {
                // Do nothing.
            }
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = "Forward arrow"
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeNavigationPreview() {
    HomeNavigation()
}
