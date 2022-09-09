package com.upstox.android.foody.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.upstox.android.foody.models.FoodRecipe
import com.upstox.android.foody.util.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(var foodRecipe: FoodRecipe
) {

    @PrimaryKey(autoGenerate = false)
    var id: Int =0
}