package com.dipitvasdev.FoodRush.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResDao {
    @Insert
    fun insertRestaurant(restaurantEntity: ResEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: ResEntity)

    @Query("SELECT * FROM restaurant")
    fun getAllRestaurants(): List<ResEntity>

    @Query("SELECT * FROM restaurant WHERE restaurant_id = :restaurantId")
    fun getRestaurantById(restaurantId: String): ResEntity
}