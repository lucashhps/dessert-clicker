package com.example.dessertclicker.ui

import androidx.annotation.DrawableRes

data class DessertUiState(
    var revenue : Int = 0,
    var dessertsSold : Int = 0,
    var currentDessertIndex : Int = 0,
    var currentDessertPrice : Int = 0,
    @DrawableRes var currentDessertImageId : Int = 0
)