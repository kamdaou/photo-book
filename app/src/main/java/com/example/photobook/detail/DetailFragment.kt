package com.example.photobook.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
                _mainViewModel.getImage(selectedPost.media!!.url[i])
                _mainViewModel.image.observe(viewLifecycleOwner) {
                    image ->
                    when (i)
                    {
                        0 -> binding.imageVal0 = image
                        1 -> binding.imageVal1 = image
                        2 -> binding.imageVal2 = image
                        3 -> binding.imageVal3 = image
                        4 -> binding.imageVal4 = image
                    }
                }
            }
        }

        return binding.root
    }
}
