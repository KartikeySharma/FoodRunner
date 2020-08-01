package com.kartikey.foodrunner.model

data class OrderHistoryRestaurant
    (
    var orderId: String,
    var restaurantName: String,
    var totalCost: String,
    var orderPlacedAt: String
)