package com.upstox.android.foody.data.database

import androidx.room.*
import com.upstox.android.foody.data.database.entities.FavouritesEntity
import com.upstox.android.foody.data.database.entities.FoodJokeEntity
import com.upstox.android.foody.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    //onConflict will replace the old data with the new data in Recipes List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteRecipes(favouritesEntity: FavouritesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)

    @Query("Select * from RECIPES_TABLE order by id Asc")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("Select * from FAVOURITE_RECIPES_TABLE order by id Asc")
    fun readFavouriteRecipes() : Flow<List<FavouritesEntity>>

    @Query("Select * from FOOD_JOKE_TABLE order by id Asc")
    fun readFoodJoke(): Flow<List<FoodJokeEntity>>

    @Delete
    suspend fun deleteFavouriteRecipe(favouritesEntity: FavouritesEntity)

    @Query("DELETE FROM FAVOURITE_RECIPES_TABLE")
    suspend fun deleteAllFavouriteRecipes()
}