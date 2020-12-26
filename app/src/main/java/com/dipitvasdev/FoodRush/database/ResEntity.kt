package com.dipitvasdev.FoodRush.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant")
data class ResEntity(
    @PrimaryKey val restaurant_Id: String,
    @ColumnInfo(name = "restaurant_name") val restaurantName: String,
    @ColumnInfo(name = "restaurant_rating") val restaurantRating: String,
    @ColumnInfo(name = "restaurant_cost_for_one") val cost_for_one: String,
    @ColumnInfo(name = "restaurant_image") val restaurantImage: String
)
