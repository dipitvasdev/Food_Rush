package com.dipitvasdev.FoodRush.model

data class OrderHistory(
    var orderId: String,
    var resName: String,
    var total: String,
    var orderPlacedAt: String
)