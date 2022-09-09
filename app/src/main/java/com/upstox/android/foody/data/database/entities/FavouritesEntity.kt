package com.upstox.android.foody.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.upstox.android.foody.models.Result
import com.upstox.android.foody.util.Constants.Companion.FAVOURITE_RECIPES_TABLE

@Entity(tableName = FAVOURITE_RECIPES_TABLE)
class FavouritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var result: Result
)