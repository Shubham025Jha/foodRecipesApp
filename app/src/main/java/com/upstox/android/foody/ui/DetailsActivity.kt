package com.upstox.android.foody.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.upstox.android.foody.R
import com.upstox.android.foody.adapters.PagerAdapter
import com.upstox.android.foody.data.database.entities.FavouritesEntity
import com.upstox.android.foody.databinding.ActivityDetailsBinding
import com.upstox.android.foody.ui.fragments.ingredients.IngredientsFragment
import com.upstox.android.foody.ui.fragments.instructions.InstructionsFragment
import com.upstox.android.foody.ui.fragments.overview.OverViewFragment
import com.upstox.android.foody.util.Constants.Companion.RECIPE_RESULT_KEY
import com.upstox.android.foody.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()

    private var recipeSaved = false
    private var savedRecipeId = 0

    private lateinit var menuItem:MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments= ArrayList<Fragment>()
        fragments.add(OverViewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        val titles= ArrayList<String>()
        titles.add("OverView")
        titles.add("Ingredients")
        titles.add("Instructions")

        val resultBundle= Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result)

        val pagerAdapter = PagerAdapter(
            resultBundle,
            fragments,
            this
        )

        //The below line solves the conflict between motion layout and swipe gestures(video no 148)
        //The solution is to remove swipe gestures
        binding.viewPager2.isUserInputEnabled = false


        binding.viewPager2.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2){ tab, position ->
            tab.text = titles[position]
        }.attach()

    }

    override fun onBackPressed() {
        Log.d("check","back pressed")
            super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu,menu)
        menuItem = menu!!.findItem(R.id.save_to_favourites_menu)
        checkSavedRecipes(menuItem)
        return true
    }


    private fun checkSavedRecipes(menuItem: MenuItem) {
        mainViewModel.readFavouriteRecipes.observe(this) { favouritesEntity ->
            try {
                for (savedRecipe in favouritesEntity) {
                    if (savedRecipe.result.id == args.result.id) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        }
    }

    //selection and deselection of favourites icon and other icon in options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }else if(item.itemId == R.id.save_to_favourites_menu && !recipeSaved){
            saveToFavourites(item)
        }else if(item.itemId == R.id.save_to_favourites_menu && recipeSaved){
            removeFromFavourites(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveToFavourites(item: MenuItem) {
        val favouritesEntity= FavouritesEntity(0,args.result)
        mainViewModel.insertFavouriteRecipes(favouritesEntity)

        changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Recipe Saved.")
        recipeSaved = true
    }

    private fun removeFromFavourites(item:MenuItem){
        val favouritesEntity= FavouritesEntity(savedRecipeId,args.result)

        mainViewModel.deleteFavouriteRecipe(favouritesEntity)
        changeMenuItemColor(item,R.color.white)
        showSnackBar("Removed from Favourites")
        recipeSaved  = false
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.detailsLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay"){}
            .show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon.setTint(ContextCompat.getColor(this,color))

    }

    override fun onDestroy() {
        super.onDestroy()
        changeMenuItemColor(menuItem,R.color.white)
    }
}