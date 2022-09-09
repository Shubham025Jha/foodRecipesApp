package com.upstox.android.foody.data

import com.upstox.android.foody.data.network.FoodRecipeApi
import com.upstox.android.foody.models.FoodJoke
import com.upstox.android.foody.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipeApi: FoodRecipeApi
){

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe>{
        return foodRecipeApi.getRecipes(queries)
    }

    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipe>{
        return foodRecipeApi.searchRecipes(searchQuery)
    }

    suspend fun getFoodJoke(apiKey: String): Response<FoodJoke>{
        return foodRecipeApi.getFoodJoke(apiKey)
    }
}