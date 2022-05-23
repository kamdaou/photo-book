package com.example.photobook.detail.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.photobook.data.Media
import com.example.photobook.data.PostFirestore
import com.example.photobook.databinding.FragmentMediaBinding
import com.example.photobook.main.MainViewModel
import com.example.photobook.utils.Constants.IMAGE_NAME
import com.google.android.exoplayer2.util.Util
import org.koin.androidx.viewmodel.ext.android.viewModel


class MediaFragment : Fragment()
{

    private lateinit var binding: FragmentMediaBinding
    private lateinit var post: PostFirestore
    private var selectedMediaIndex:Int = 0
    private var window: Window? = null
    private var mainContainer:View?=null
    private val mainViewModel: MainViewModel by viewModel()
    private val _viewModel: MediaViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentMediaBinding.inflate(inflater)

        selectedMediaIndex = MediaFragmentArgs.fromBundle(requireArguments()).selectedIndex
        _viewModel.setSelectedIndex(selectedMediaIndex)
        window = activity?.window
        mainContainer = activity?.window?.decorView
        post = MediaFragmentArgs.fromBundle(requireArguments()).post
        _viewModel.selectedIndex.observe(viewLifecycleOwner)
        {
            post.media?.let { it1 -> _viewModel.showPreviousAndNext(it1) }
        }

        _viewModel.showPrevious.observe(viewLifecycleOwner)
        {
            if (it)
                binding.buttonPrevious.visibility = View.VISIBLE
            else
                binding.buttonPrevious.visibility = View.GONE
        }
        _viewModel.showNext.observe(viewLifecycleOwner)
        {
            if (it)
                binding.buttonNext.visibility = View.VISIBLE
            else
                binding.buttonNext.visibility = View.GONE
        }

        return binding.root
    }

    private fun displayMedia(media: Media, selectedMediaIndex: Int) {
        if (media.media_type.contains(IMAGE_NAME) && media.url.isNotEmpty())
        {
            mainViewModel.getImage(media.url[selectedMediaIndex], selectedMediaIndex)
            when (selectedMediaIndex)
            {
                0 -> mainViewModel.image0.observe(viewLifecycleOwner){
                    Glide
                        .with(binding.image.context)
                        .load(it.image)
                        .into(binding.image)
                    binding.image.visibility = View.VISIBLE
                }
                1 -> mainViewModel.image1.observe(viewLifecycleOwner){
                    Glide
                        .with(binding.image.context)
                        .load(it.image)
                        .into(binding.image)
                    binding.image.visibility = View.VISIBLE
                }
                2 -> mainViewModel.image2.observe(viewLifecycleOwner){
                    Glide
                        .with(binding.image.context)
                        .load(it.image)
                        .into(binding.image)
                    binding.image.visibility = View.VISIBLE
                }
                3 -> mainViewModel.image3.observe(viewLifecycleOwner){
                    Glide
                        .with(binding.image.context)
                        .load(it.image)
                        .into(binding.image)
                    binding.image.visibility = View.VISIBLE
                }
                4 -> mainViewModel.image4.observe(viewLifecycleOwner){
                    Glide
                        .with(binding.image.context)
                        .load(it.image)
                        .into(binding.image)
                    binding.image.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // hideSystemUI()

        if (Util.SDK_INT < 24){

            binding.buttonNext.setOnClickListener {
                selectedMediaIndex += 1
                _viewModel.setSelectedIndex(selectedMediaIndex)
                displayMedia(post.media!!, selectedMediaIndex)
            }
            binding.buttonPrevious.setOnClickListener {
                selectedMediaIndex -= 1
                 _viewModel.setSelectedIndex(selectedMediaIndex)
                displayMedia(post.media!!, selectedMediaIndex)
            }

            displayMedia(post.media!!, selectedMediaIndex)

        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24){
            displayMedia(post.media!!, selectedMediaIndex)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24)
        {
            clearImage()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24)
        {
            clearImage()
        }
    }

    /**
     * clearImage - clear image
     */
    private fun clearImage() {
        Glide
            .with(binding.image.context)
            .clear(binding.image)
    }
}