package com.example.photobook.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.photobook.R
import com.example.photobook.adapters.PostListener
import com.example.photobook.adapters.PostRecyclerViewAdapter
import com.example.photobook.databinding.FragmentMainBinding
import com.example.photobook.utils.Constants
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * MainFragment - The fragment that contains list of posts
 *
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment()
{
    private lateinit var binding: FragmentMainBinding

    /* private lateinit var _viewModel: MainViewModel */
    private val _viewModel: MainViewModel by viewModel()
    private lateinit var recyclerViewAdapter: PostRecyclerViewAdapter
    private lateinit var contxt: Context
    private lateinit var navController: NavController

    /**
     * onCreateView - Inflates mainFragment
     *
     * All interaction with the main fragment will be done here.
     * It also displays list of posts and UIs for interactions
     *
     * Return: view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        /* val application = this.requireActivity().application */
        /* val viewModelProvider = MainViewModelFactory(application) */


        /* _viewModel = ViewModelProvider(this, viewModelProvider)[MainViewModel::class.java] */
        recyclerViewAdapter = PostRecyclerViewAdapter(PostListener { id ->
        _viewModel.onPostSelected(id)
        }, _viewModel)
        binding.postList.adapter = recyclerViewAdapter

        binding.retryImage.setOnClickListener {
            _viewModel.loadPosts()
        }

        binding.addPostButton.setOnClickListener {
            _viewModel.navigateToAddPostFragment()
        }

        _viewModel.navigateToAddPostFragment.observe(viewLifecycleOwner){
            if (it)
            {
                navController.navigate(R.id.action_mainFragment_to_addPostFragment)
                _viewModel.onAddPostFragmentNavigated()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            requireActivity().finishAffinity()
        }
        loadPosts()
        navigateToDetailFragment()
        showSnackBar()
        handleLoadingStatus()

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
    }

    /**
     * onAttach - puts value of contxt to the one of
     * the context once the fragment is attached
     *
     * @context: context of the application
     */
    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        contxt = context
    }

    /**
     * onResume - Tells viewModel to refresh list of post
     * so that we get brand new data
     */
    override fun onResume() {
        super.onResume()
        _viewModel.shouldRefresh.value = true
        /* _viewModel.loadPosts() */
    }

    /**
     * navigateToDetailFragment - Navigates to DetailFragment,
     * when value of navigateToDetailFragment in viewModel changes
     */
    private fun navigateToDetailFragment()
    {
        _viewModel.navigateToPostDetail.observe(viewLifecycleOwner) { post ->
            if (post != null) {
                navController.navigate(
                    MainFragmentDirections.actionMainFragmentToDetailFragment(
                        post
                    )
                )
                _viewModel.onPostDetailNavigated()
            }

        }
    }

    /**
     * loadPosts - Submits value of postLists to the recyclerView
     */
    private fun loadPosts()
    {
        _viewModel.posts?.observe(viewLifecycleOwner) { postList ->
            Log.i(TAG, "post list: $postList")
            postList?.apply {
                recyclerViewAdapter.submitList(postList.post?.toMutableList()?.reversed())
            }
            _viewModel.postRead(postList)
        }
    }

    /**
     * showSnackBar - Shows the snackBar, if the value
     * of snackBarContain in viewModel is not null
     */
    private fun showSnackBar()
    {
        _viewModel.snackBarContain.observe(viewLifecycleOwner) { contain ->
            if (contain != null) {
                Snackbar.make(requireView(), contxt.getString(contain), Snackbar.LENGTH_SHORT).show()
                _viewModel.onSnackBarShowed()
            }
        }
    }

    /**
     * handleLoadingStatus - Shows a loading image, if posts are being load.
     * If the result finished successfully, the image is removed. Otherwise,
     * it's replaced by a retry image.
     */
    private fun handleLoadingStatus()
    {
        _viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            Log.i(TAG, "status value changed: $status")
            when(status){
                Constants.Status.LOADING -> {
                    binding.statusLoadingWheel.visibility = View.VISIBLE
                    binding.retryImage.visibility = View.GONE
                }
                Constants.Status.DONE ->{
                    binding.statusLoadingWheel.visibility = View.GONE
                    binding.retryImage.visibility = View.GONE
                    Log.i(TAG, "image loaded")
                }
                Constants.Status.ERROR -> {
                    binding.retryImage.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }
}
private const val TAG = "MainFragment"
