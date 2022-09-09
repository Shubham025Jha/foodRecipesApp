package com.upstox.android.foody.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upstox.android.foody.adapters.FavoriteRecipeAdapter
import com.upstox.android.foody.data.database.entities.FavouritesEntity

class FavoriteRecipesBinding {

    companion object {

        @BindingAdapter("viewVisibility", "setData", requireAll = false)
        @JvmStatic
        fun setDataAdViewVisibility(
            view: View,
            favouritesEntity: List<FavouritesEntity>?,
            mAdapter: FavoriteRecipeAdapter?
        ) {
            if(favouritesEntity.isNullOrEmpty()){
                when(view){
                    is ImageView -> {
                        view.visibility=  View.VISIBLE
                    }
                    is TextView -> {
                        view.visibility = View.VISIBLE
                    }
                    is RecyclerView -> {
                        view.visibility = View.INVISIBLE
                    }
                }
            }else {
                when(view){
                    is ImageView -> {
                        view.visibility=  View.INVISIBLE
                    }
                    is TextView -> {
                        view.visibility = View.INVISIBLE
                    }
                    is RecyclerView -> {
                        view.visibility = View.VISIBLE
                        mAdapter?.setData(favouritesEntity)
                    }
                }

            }
        }
    }
}