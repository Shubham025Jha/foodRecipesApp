package com.upstox.android.foody.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.upstox.android.foody.R
import com.upstox.android.foody.adapters.RecipesAdapter
import com.upstox.android.foody.databinding.FragmentRecipesBinding
import com.upstox.android.foody.util.NetworkListener
import com.upstox.android.foody.util.NetworkResult
import com.upstox.android.foody.util.observeOnce
import com.upstox.android.foody.viewModels.MainViewModel
import com.upstox.android.foody.viewModels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {

    private val args by navArgs<RecipesFragmentArgs>()

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!


    private val mAdapter by lazy { RecipesAdapter() }
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel

    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        setHasOptionsMenu(true)

        setupRecyclerView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->
                Log.d("NetworkListener", status.toString())
                recipesViewModel.networkStatus = status
                recipesViewModel.showNetworkStatus()
                readDatabase()
            }
        }

        binding.recipesFilter.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }

        return binding.root
    }


    private fun setupRecyclerView() {
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }


    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    mAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData() {
        Log.d("RecipesFragment", "requestApiData called!")
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                    recipesViewModel.saveMealAndDietType()
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }

        }
    }

    private fun searchApiData(searchQuery: String) {
        showShimmerEffect()

        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    val foodRecipe = response.data
                    foodRecipe?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    mAdapter.setData((database[0].foodRecipe))
                }
            }
        }
    }

    private fun showShimmerEffect() {
        binding.recyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.recyclerView.hideShimmer()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}