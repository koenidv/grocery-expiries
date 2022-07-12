package de.koenidv.ablaufdaten.bottomsheets

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import de.koenidv.ablaufdaten.Grocery
import org.json.JSONObject

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnsafeOptInUsageError")
class AddProductBottomSheet(private val context: Context) {
    private val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var barcode = mutableStateOf(null as String?)


    @Composable
    fun Sheet(onAdded: (Grocery) -> Unit) {
        BottomSheet(sheetState) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                ProductInfoView()
            }
        }
    }

    suspend fun add(barcode: String) {
        this.barcode.value = barcode
        show()
    }


    suspend fun show() {
        sheetState.show()
    }

    suspend fun hide() {
        sheetState.hide()
    }

    @Composable
    fun ProductInfoView() {
        var name by remember { mutableStateOf(null as String?) }

        LaunchedEffect(barcode.value) {
            if (barcode.value == null) return@LaunchedEffect
            Log.d("Scanner", "Barcode: ${barcode.value}")
            AndroidNetworking.initialize(context)
            AndroidNetworking.get("https://world.openfoodfacts.org/api/v0/product/${barcode.value}.json")
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        name = response.getJSONObject("product").getString("product_name")
                        Log.d("Scanner", name ?: "Name invalid")
                    }

                    override fun onError(anError: ANError) {
                        Log.e("Scanner", anError.errorBody)
                    }
                })
        }

        Text(
            name ?: "Loading...",
            modifier = Modifier.fillMaxWidth(),
            style = typography.titleLarge,
            color = colors.onSurface,
            textAlign = TextAlign.Center
        )

    }

}