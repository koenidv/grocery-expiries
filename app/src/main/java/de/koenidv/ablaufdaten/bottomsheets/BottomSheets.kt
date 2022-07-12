package de.koenidv.ablaufdaten.bottomsheets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    sheetState: ModalBottomSheetState,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    ModalBottomSheetLayout(sheetState = sheetState, sheetContent = content) {}
}