package com.upstox.android.foody.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.upstox.android.foody.data.Repository
import com.upstox.android.foody.data.database.entities.FavouritesEntity
import com.upstox.android.foody.data.database.entities.FoodJokeEntity
import com.upstox.android.foody.data.database.entities.RecipesEntity
import com.upstox.android.foody.models.FoodJoke
import com.upstox.android.foody.models.FoodRecipe
import com.upstox.android.foody.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application): AndroidViewModel(application) {

    /**ROOM DATABASE*/

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()
    val readFavouriteRecipes : LiveData<List<FavouritesEntity>> = repository.local.readFavouriteRecipes().asLiveData()
    val readFoodJoke: LiveData<List<FoodJokeEntity>> = repository.local.readFoodJoke().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO){
            repository.local.insertRecipes(recipesEntity)
        }

     fun insertFavouriteRecipes(favouritesEntity: FavouritesEntity)=
        viewModelScope.launch (Dispatchers.IO){
            repository.local.insertFavouriteRecipes(favouritesEntity)
        }

    private fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFoodJoke(foodJokeEntity)
        }

     fun deleteFavouriteRecipe(favouritesEntity: FavouritesEntity)=
        viewModelScope.launch (Dispatchers.IO){
            repository.local.deleteFavouriteRecipe(favouritesEntity)
        }

     fun deleteAllFavouriteRecipes()=
        viewModelScope.launch (Dispatchers.IO){
            repository.local.deleteAllFavouriteRecipes()
        }

    /** RETROFIT */
     var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var foodJokeResponse: MutableLiveData<NetworkResult<FoodJoke>> = MutableLiveData()

    fun searchRecipes(searchQuery: Map<String,String>)= viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    fun getRecipes(queries: Map<String,String>)= viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun getFoodJoke(apiKey: String) = viewModelScope.launch {
        getFoodJokeSafeCall(apiKey)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try{
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipeResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe != null){
                    offlineCacheRecipes(foodRecipe)
                }
            }
            catch (e: Exception){
                recipesResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        }
        else{
            recipesResponse.value= NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try{
                val response = repository.remote.searchRecipes(searchQuery)
                searchedRecipesResponse.value = handleFoodRecipeResponse(response)
            }
            catch (e: Exception){
                //e.printStackTrace()
                searchedRecipesResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        }
        else{
            searchedRecipesResponse.value= NetworkResult.Error("No Internet Connection.")
        }
    }


    private suspend fun getFoodJokeSafeCall(apiKey: String) {
        foodJokeResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try{
                val response = repository.remote.getFoodJoke(apiKey)
                foodJokeResponse.value = handleFoodJokeResponse(response)

                val foodJoke = foodJokeResponse.value!!.data
                if(foodJoke != null){
                    offlineCacheFoodJoke(foodJoke)
                }
            }
            catch (e: Exception){
                //e.printStackTrace()
                foodJokeResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        }
        else{
            foodJokeResponse.value= NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun offlineCacheFoodJoke(foodJoke: FoodJoke) {
        val foodJokeEntity = FoodJokeEntity(foodJoke)
        insertFoodJoke(foodJokeEntity)
    }

    private fun handleFoodRecipeResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        when {
            response.message().toString().contains("timeout")-> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }

            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not found")
            }

            response.isSuccessful ->{
                val foodRecipe = response.body()
                return NetworkResult.Success(foodRecipe!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleFoodJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke> {
        return when {
            response.message().toString().contains("timeout")-> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful ->{
                val foodJoke = response.body()
                NetworkResult.Success(foodJoke!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }


    private fun hasInternetConnection():Boolean{
            val connectivityManager = getApplication<Application>().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?: return false

            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)-> true
                capabilities.hasTransport((NetworkCapabilities.TRANSPORT_CELLULAR))->true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                else -> false
            }
        }

}