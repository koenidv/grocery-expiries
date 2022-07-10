package de.koenidv.ablaufdaten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.time.LocalDate

data class Grocery(
    val id: Int,
    val barcode: Int,
    val name: String,
    val expiry: LocalDate,
    val notes: String
) {

    constructor(id: Int, name: String, expiry: LocalDate) : this(id, 0, name, expiry, "")

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ListItem() {
        Card {
            Row {
                Column {
                    Text(name, style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}