package de.koenidv.ablaufdaten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text

@Composable
fun GroceriesList(padding: PaddingValues = PaddingValues()) {
    val groceries = remember { TestDataProvider.groceriesList }
    LazyColumn(contentPadding = padding) {
        items(
            items = groceries,
            itemContent = { it.ListItem() }
        )
    }
}