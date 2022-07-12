package de.koenidv.ablaufdaten

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.koenidv.ablaufdaten.BottomSheets.showAsBottomSheet
import de.koenidv.ablaufdaten.ui.theme.AblaufdatenTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnsafeOptInUsageError")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AblaufdatenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = { LargeTopAppBar(title = { Text("Ablaufdaten") }) },
                        floatingActionButton = {
                            ExtendedFloatingActionButton(
                                text = { Text("Scan") },
                                icon = { Icon(Icons.Rounded.Add, null) },
                                onClick = {
                                    showAsBottomSheet {
                                        CameraBottomSheet(applicationContext) { result ->

                                        }
                                    }
                                })
                        },
                        floatingActionButtonPosition = FabPosition.Center
                    ) {
                        GroceriesList(padding = it)
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