package com.upstox.android.foody.data.network

import com.upstox.android.foody.models.FoodJoke
import retrofit2.http.GET
import retrofit2.http.QueryMap
import com.upstox.android.foody.models.FoodRecipe
import retrofit2.Response
import retrofit2.http.Query

interface FoodRecipeApi {

    //suspend would mean that this function would run on a background thread instead of our main thread.
    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>):Response<FoodRecipe>

    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap SearchQuery: Map<String, String>):Response<FoodRecipe>

    @GET("food/jokes/random")
    suspend fun getFoodJoke(
        @Query("apiKey") apiKey: String
    ):Response<FoodJoke>

}

