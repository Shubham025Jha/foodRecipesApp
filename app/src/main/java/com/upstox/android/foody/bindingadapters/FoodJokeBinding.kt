package com.upstox.android.foody.bindingadapters

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import com.upstox.android.foody.data.database.entities.FoodJokeEntity
import com.upstox.android.foody.models.FoodJoke
import com.upstox.android.foody.util.NetworkResult

class FoodJokeBinding {

    companion object{

        @BindingAdapter("readApiResponseJoke","readDatabaseJoke", requireAll = false)
        @JvmStatic
        fun setCardAndProgressVisibility(
            view: View,
            apiResponse: NetworkResult<FoodJoke>?,
            database: List<FoodJokeEntity>?
        ){
            when(apiResponse){
                is NetworkResult.Loading -> {
                    when(view){
                        is ProgressBar ->{
                            view.visibility = View.VISIBLE
                        }
                        is MaterialCardView ->{
                            view.visibility = View.INVISIBLE
                        }
                    }
                }

                is NetworkResult.Error ->{
                    when(view){
                        is ProgressBar ->{
                            view.visibility = View.INVISIBLE
                        }
                        is MaterialCardView ->{
                            view.visibility = View.VISIBLE
                            if(database != null){
                                if(database.isEmpty()){
                                    view.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }

                is NetworkResult.Success ->{
                    when(view){
                        is ProgressBar ->{
                            view.visibility = View.INVISIBLE
                        }
                        is MaterialCardView ->{
                            view.visibility = View.VISIBLE
                        }
                    }
                }
                else -> {}
            }
        }

        @BindingAdapter("readApiResponseJoke2","readDatabaseJoke2", requireAll = true)
        @JvmStatic
        fun setErrorViewsVisibility(
            view:View,
            apiResponse: NetworkResult<FoodJoke>?,
            database: List<FoodJokeEntity>?
        ){
            if(database!= null){
                if(database.isEmpty()){
                    view.visibility = View.VISIBLE
                    if(view is TextView){
                        if(apiResponse != null){
                            view.text = apiResponse.message.toString()
                        }
                    }
                }
            }
            if(apiResponse is NetworkResult.Success){
                view.visibility = View.INVISIBLE
            }
        }
    }
}