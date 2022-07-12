package de.koenidv.ablaufdaten

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.koenidv.ablaufdaten.bottomsheets.AddProductBottomSheet
import de.koenidv.ablaufdaten.ui.theme.AblaufdatenTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val scanner = remember { ScannerBottomSheet(applicationContext) }
            val addSheet = remember { AddProductBottomSheet(applicationContext) }

            AblaufdatenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = { SmallTopAppBar(title = { Text("Ablaufdaten") }) },
                        floatingActionButton = {
                            ExtendedFloatingActionButton(
                                text = { Text("Scan") },
                                icon = { Icon(Icons.Rounded.Add, null) },
                                onClick = {
                                    coroutineScope.launch {
                                        scanner.show()
                                    }
                                })
                        },
                        floatingActionButtonPosition = FabPosition.Center
                    ) {
                        GroceriesList(padding = it)
                        scanner.Scan { result ->
                            coroutineScope.launch {
                                scanner.hide()
                                addSheet.add(result)
                            }
                        }
                        addSheet.Sheet {}
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AblaufdatenTheme {
        Greeting("Android")
    }
}