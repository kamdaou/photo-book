package com.example.photobook.detail.media

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.photobook.data.Media
import com.example.photobook.data.PostFirestore
import com.example.photobook.databinding.FragmentMediaBinding
import com.example.photobook.utils.Constants.IMAGE_NAME
import com.example.photobook.utils.Constants.VIDEO_NAME
import com.example.photobook.utils.GlideApp
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.Util
import com.google.firebase.storage.FirebaseStorage

class MediaFragment : Fragment() {

    private lateinit var binding:FragmentMediaBinding
    private var player: ExoPlayer? = null
    private lateinit var post: MutableLiveData<PostFirestore>
    private var selectedMediaIndex:Int = 0
    private lateinit var selectedMediaIndexLiveData: MutableLiveData<Int>
    private var window: Window? = null
    private var mainContainer:View?=null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private val TAG = "MediaFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMediaBinding.inflate(inflater)
        val selectedMediaIndex = MediaFragmentArgs.fromBundle(requireArguments()).selectedMediaNumber
        window = activity?.window
        mainContainer = activity?.window?.decorView
        post = MutableLiveData(MediaFragmentArgs.fromBundle(requireArguments()).selectedPost)
        selectedMediaIndexLiveData= MutableLiveData(selectedMediaIndex)
        selectedMediaIndexLiveData.observe(viewLifecycleOwner){
            if (it<=0){
                binding.buttonPrevious.visibility = View.GONE
            }
            if (it>0){
                binding.buttonPrevious.visibility = View.VISIBLE
            }
            post.observe(viewLifecycleOwner){ post->

                if (it<post.media?.url?.size!!-1){
                    binding.buttonNext.visibility = View.VISIBLE
                }
                if (it>= post.media!!.url.size -1){
                    binding.buttonNext.visibility = View.GONE
                }
                if (selectedMediaIndex< post.media!!.url.size -1){
                    binding.buttonNext.visibility = View.VISIBLE
                }
                if (selectedMediaIndex>0){
                    binding.buttonPrevious.visibility = View.VISIBLE
                }
            }
        }

        return binding.root
    }

    private fun displayMedia(media: Media, selectedMediaIndex: Int) {
        val storageReference = FirebaseStorage.getInstance()
        if (media.media_type.contains(IMAGE_NAME)) {
            val mediaReference =
                storageReference.getReferenceFromUrl("gs://just-book-221c8.appspot.com/images/${media.url[selectedMediaIndex]}")
            GlideApp
                .with(binding.image.context)
                .load(mediaReference)
                .into(binding.image)
            binding.image.visibility = View.VISIBLE
        } else if (media.media_type.contains(VIDEO_NAME)) {
            binding.video.visibility = View.VISIBLE
            Log.i(TAG, "Media is a video")
            player = ExoPlayer.Builder(this.requireActivity())
                .build()
                .also { exoPlayer ->
                    val mediaReference = storageReference.getReferenceFromUrl("gs://just-book-221c8.appspot.com/videos/${media.url[selectedMediaIndex]}")
                    binding.video.player = exoPlayer
                    var mediaItem: MediaItem? = null
                    mediaReference.downloadUrl.addOnSuccessListener { uri ->
                        mediaItem = MediaItem.fromUri(uri)
                        exoPlayer.setMediaItem(mediaItem!!)
                        Log.i(TAG, "Uri read successfully")
                    }.addOnFailureListener {
                        Log.i(TAG, "exception: ${it.message}")
                    }
                }
            player?.playWhenReady = playWhenReady
            player?.seekTo(currentWindow, playbackPosition)
            player?.prepare()

        }
    }

    override fun onResume() {
        super.onResume()
        // hideSystemUI()

        if (Util.SDK_INT < 24 || player == null){

            binding.buttonNext.setOnClickListener {
                selectedMediaIndex += 1
                selectedMediaIndexLiveData.value = selectedMediaIndex
                displayMedia(post.value?.media!!, selectedMediaIndex)
            }
            binding.buttonPrevious.setOnClickListener {
                selectedMediaIndex -= 1
                selectedMediaIndexLiveData.value = selectedMediaIndex
                displayMedia(post.value?.media!!, selectedMediaIndex)
            }

            displayMedia(post.value?.media!!, selectedMediaIndex)

        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24){
            displayMedia(post.value?.media!!, selectedMediaIndex)

        }
    }

    override fun onPause() {
        super.onPause()
        //showSystemUI()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUI() {
        if(window != null && mainContainer != null){
            WindowCompat.setDecorFitsSystemWindows(window!!, false)
            WindowInsetsControllerCompat(window!!, mainContainer!!).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    private fun showSystemUI() {
        if(window != null && mainContainer != null) {
            WindowCompat.setDecorFitsSystemWindows(window!!, true)
            WindowInsetsControllerCompat(window!!, mainContainer!!).show(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun releasePlayer() {
        binding.video.visibility = View.GONE
        player?.run {
            playbackPosition = this.currentPosition
            @Deprecated("Deprecated in Java")
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }
}
