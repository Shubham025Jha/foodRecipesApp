package com.upstox.android.foody.viewModels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.upstox.android.foody.data.DataStoreRepository
import com.upstox.android.foody.data.MealAndDietType
import com.upstox.android.foody.util.Constants.Companion.API_KEY
import com.upstox.android.foody.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.upstox.android.foody.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.upstox.android.foody.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.upstox.android.foody.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.upstox.android.foody.util.Constants.Companion.QUERY_API_KEY
import com.upstox.android.foody.util.Constants.Companion.QUERY_DIET
import com.upstox.android.foody.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.upstox.android.foody.util.Constants.Companion.QUERY_NUMBER
import com.upstox.android.foody.util.Constants.Companion.QUERY_SEARCH
import com.upstox.android.foody.util.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    private lateinit var mealAndDiet: MealAndDietType

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    //So this function is actually storing the state of selected chips in our bottom Sheet to dataStoreRepository
    fun saveMealAndDietType() =
        viewModelScope.launch(Dispatchers.IO) {
            if(this@RecipesViewModel::mealAndDiet.isInitialized) {
                dataStoreRepository.saveMealAndDietType(
                    mealAndDiet.selectedMealType,
                    mealAndDiet.selectedMealTypeId,
                    mealAndDiet.selectedDietType,
                    mealAndDiet.selectedDietTypeId
                )
            }
        }

    //this is a temporary function that locally saves the state of selected chip
    //And the function above stores in data repository after the success of getting desired recipes
    fun saveMealAndDietTypeTemp(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        mealAndDiet = MealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
    }

    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER.toString()
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        if(this@RecipesViewModel::mealAndDiet.isInitialized){
            queries[QUERY_TYPE] = mealAndDiet.selectedMealType
            queries[QUERY_DIET] = mealAndDiet.selectedDietType
        }else{
            queries[QUERY_TYPE] = DEFAULT_MEAL_TYPE
            queries[QUERY_DIET] = DEFAULT_DIET_TYPE
        }
        return queries
    }

    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER.toString()
        queries[API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We are back Online", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }
}