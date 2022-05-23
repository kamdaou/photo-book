package com.example.photobook.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.photobook.data.PostFirestore
import com.example.photobook.databinding.FragmentDetailBinding
import com.example.photobook.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

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
    private val _viewModel: DetailViewModel by viewModel()
    private val _mainViewModel: MainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        /* val viewModelFactory = DetailViewModelFactory(requireActivity().application) */

        binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        selectedPost = DetailFragmentArgs.fromBundle(requireArguments()).post
        binding.post = selectedPost
        /* _viewModel = ViewModelProvider(this, viewModelFactory)[DetailViewModel::class.java] */

        binding.image1.setOnClickListener {
            navigateToMediaFragment(0)
        }
        binding.image2.setOnClickListener {
            navigateToMediaFragment(1)
        }
        binding.image3.setOnClickListener {
            navigateToMediaFragment(2)
        }
        binding.image4.setOnClickListener {
            navigateToMediaFragment(3)
        }
        binding.image5.setOnClickListener {
            navigateToMediaFragment(4)
        }
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
        if (selectedPost.media != null)
        {
            for (i in 0 until selectedPost.media!!.url.size)
            {
                _mainViewModel.getImage(selectedPost.media!!.url[i], i)
            }
        }

        setImages()

        return binding.root
    }

    private fun setImages()
    {
        _mainViewModel.image0.observe(viewLifecycleOwner) { image ->
            binding.imageVal0 = image
        }
        _mainViewModel.image1.observe(viewLifecycleOwner) { image ->
            binding.imageVal1 = image
        }
        _mainViewModel.image2.observe(viewLifecycleOwner) { image ->
            binding.imageVal2 = image
        }
        _mainViewModel.image3.observe(viewLifecycleOwner) { image ->
            binding.imageVal3 = image
        }
        _mainViewModel.image4.observe(viewLifecycleOwner) { image ->
            binding.imageVal4 = image
        }
    }

    private fun navigateToMediaFragment(
        selecteMediaNumber: Int
    )
    {
        if (!selectedPost.media?.url.isNullOrEmpty())
        {
            findNavController()
                .navigate(
                    DetailFragmentDirections
                        .actionDetailFragmentToMediaFragment(
                            selectedPost, selecteMediaNumber
                        )
                )
        }
    }
}
