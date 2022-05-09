package com.example.photobook.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.photobook.adapters.PostListener
import com.example.photobook.adapters.PostRecyclerViewAdapter
import com.example.photobook.databinding.FragmentMainBinding
import com.example.photobook.utils.Constants
import com.google.android.material.snackbar.Snackbar

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

    private lateinit var _viewModel: MainViewModel
    private lateinit var recyclerViewAdapter: PostRecyclerViewAdapter
    private lateinit var contxt: Context

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

        val application = this.requireActivity().application
        val viewModelProvider = MainViewModelFactory(application)


        _viewModel = ViewModelProvider(this, viewModelProvider)[MainViewModel::class.java]
        recyclerViewAdapter = PostRecyclerViewAdapter(PostListener { id ->
        _viewModel.onPostSelected(id)
        })
        binding.postList.adapter = recyclerViewAdapter

        binding.retryImage.setOnClickListener {
            _viewModel.loadPosts()
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
     * navigateToDetailFragment - Navigates to DetailFragment,
     * when value of navigateToDetailFragment in viewModel changes
     */
    private fun navigateToDetailFragment()
    {
        _viewModel.navigateToPostDetail.observe(viewLifecycleOwner) { post ->
            if (post != null) {
                findNavController().navigate(
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
                recyclerViewAdapter.submitList(postList.post)
            }
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
