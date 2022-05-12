package com.example.photobook.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.photobook.data.PostFirestore
import com.example.photobook.databinding.FragmentDetailBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "DetailFragment"
/**
 * DetailFragment - Fragment that displays details of a post
 *
 * @post: The post that we have to display its details
 */
class DetailFragment : Fragment()
{

    private lateinit var binding: FragmentDetailBinding
    private lateinit var selectedPost: PostFirestore
    private lateinit var _viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        val viewModelFactory = DetailViewModelFactory(requireActivity().application)

        binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        selectedPost = DetailFragmentArgs.fromBundle(requireArguments()).post
        binding.post = selectedPost
        _viewModel = ViewModelProvider(this, viewModelFactory)[DetailViewModel::class.java]

        _viewModel.snackBarContain.observe(viewLifecycleOwner){
            if (it != null) {
                Snackbar.make(
                    this.requireView(),
                    getString(it),
                    Snackbar.LENGTH_SHORT
                ).show()
                _viewModel.onSnackBarShowed()
            }
        }

        return binding.root
    }
}
