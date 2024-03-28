package com.example.dessertclicker.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.dessertclicker.R
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.dessertclicker.data.Datasource.dessertList
import kotlinx.coroutines.flow.update

class DessertViewModel : ViewModel() {

    // Variables

    private val _uiState = MutableStateFlow(DessertUiState())
    // backing property
    val uiState : StateFlow<DessertUiState>
        get() = _uiState.asStateFlow()

    private var desserts = dessertList

    // Initialization

    init {
        val initialIndex = _uiState.value.currentDessertIndex
        _uiState.value = DessertUiState(currentDessertPrice = desserts[initialIndex].price, currentDessertImageId = desserts[initialIndex].imageId)
    }

    // Helper Methods

    fun getDessertList() = desserts

    fun determineDessertToShow(
        desserts: List<Dessert>,
        dessertsSold: Int
    ): Dessert {
        var dessertToShow = desserts.first()
        for (dessert in desserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }

        return dessertToShow
    }

    fun onDessertClicked(){
        // Update the revenue and show the next dessert
        _uiState.update {
            currentState ->
            val newRevenue = currentState.revenue + currentState.currentDessertPrice
            val dessertsSold = currentState.dessertsSold + 1
            val dessertToShow = determineDessertToShow(desserts, dessertsSold)


            currentState.copy(revenue = newRevenue, dessertsSold = dessertsSold, currentDessertImageId = dessertToShow.imageId, currentDessertPrice = dessertToShow.price)


        }


    }

    fun shareSoldDessertsInformation(intentContext: Context) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                intentContext.getString(R.string.share_text, _uiState.value.dessertsSold, _uiState.value.revenue)
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)

        try {
            ContextCompat.startActivity(intentContext, shareIntent, null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                intentContext,
                intentContext.getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

}