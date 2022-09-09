package com.upstox.android.foody.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.upstox.android.foody.models.FoodJoke
import com.upstox.android.foody.util.Constants.Companion.FOOD_JOKE_TABLE

@Entity(tableName = FOOD_JOKE_TABLE)
class FoodJokeEntity (
    @Embedded
    var foodJoke: FoodJoke
        ){
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}