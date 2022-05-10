package com.example.photobook.addPost

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.photobook.R
import com.example.photobook.data.Media
import com.example.photobook.data.Post
import com.example.photobook.data.User
import com.example.photobook.databinding.FragmentAddPostBinding
import com.example.photobook.utils.Constants.IMAGE_NAME
import com.example.photobook.utils.Constants.VIDEO_NAME
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val IMAGE_VIEW = "imageView"
private const val IMAGE_MEDIA_TITLE = "imageMediaTitle"
private const val IMAGE_MEDIA_URL = "imageMediaUrl"
private const val VIDEO_VIEW = "videoView"
private const val VIDEO_MEDIA_TITLE = "videoMediaTitle"
private const val VIDEO_MEDIA_URL = "videoMediaUrl"
private const val TAKEN_VIDEOS = "takenVideo"
private const val TAKEN_IMAGES = "takenVideo"
internal const val TAG = "AddPostFragment"
private const val REQUEST_IMAGE_CAPTURE = 12
private const val REQUEST_VIDEO_CAPTURE = 32


class AddPostFragment : Fragment()
{

    private lateinit var _viewModel: AddPostViewModel
    private lateinit var binding: FragmentAddPostBinding
    private lateinit var geocoder: Geocoder
    private var takenImages = 0
    private var takenVideo = 0
    private var post = Post(submitter_id = "")
    private var imageNames:MutableList<String> = mutableListOf()
    private val imageMedia = Media(media_type = IMAGE_NAME, title="", url = listOf())
    private var imageViews:MutableList<Bitmap> = mutableListOf()
    private var videoNames:MutableList<String> = mutableListOf()
    private val videoMedia = Media(media_type = VIDEO_NAME, title="", url = listOf())
    private var videoViews:MutableList<Uri> = mutableListOf()
    private lateinit var navController: NavController
    private lateinit var contxt: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        val viewModelFactory = AddPostViewModelFactory()
        navController = this.findNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser

        binding = FragmentAddPostBinding.inflate(inflater)
        retrieveSavedData(savedInstanceState)
        binding.lifecycleOwner = this
        binding.post = post
        _viewModel = ViewModelProvider(this, viewModelFactory)[AddPostViewModel::class.java]
        _viewModel.snackBarContain.observe(viewLifecycleOwner){
            if (it != null) {
                Snackbar.make(this.requireView(), contxt.getString(it), Snackbar.LENGTH_SHORT).show()
                _viewModel.onSnackBarShowed()
            }
        }

        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        // disabling take a picture and record video for phones which don't have camera
        val pm  = requireActivity().packageManager
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            binding.takePictureText.visibility = View.GONE
            binding.takePictureButton.visibility = View.GONE
            binding.takeVideoButton.visibility = View.GONE
            binding.takeVideoText.visibility = View.GONE
        }

        deleteButtonListeners()

        binding.takePictureButton.setOnClickListener {
            onTakePictureHit()
        }
        binding.takePictureText.setOnClickListener { onTakePictureHit() }

        binding.savePostButton.setOnClickListener{
            //Closes the keyboard
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            Log.d(TAG, "post value : $post")
            _viewModel.observable.setBody(post.body)
            _viewModel.observable.setTitle(post.title)
            // simply way for other argument
            /**
             * TODO replace the following line by post.submitter_id = currentUser.uid!!
             * after implementing login
             */
            post.submitter_id = "userId"
            // TODO add cty using location functions
            post.city = ""
            Log.d(TAG, "post value : $post")
            // If there is nothing to post
            if ((post.body == "" || post.title == "") && (imageNames.isEmpty() && videoNames.isEmpty())){
                Toast.makeText(requireContext(), "Please add a media or at least write a something to be able to post", Toast.LENGTH_LONG).show()
            }
            else{
                /**
                 * TODO replace the following line by val user = createUserFromCurrentUser(currentUser)
                 * after implementing login
                 */
                val user = User("username", "userid")
                when {
                    imageNames.isNotEmpty() -> {
                        // saving medias then post
                        imageMedia.url = imageNames
                        CoroutineScope(Dispatchers.IO).launch {
                            for (i in 0 until imageViews.size){
                                val storage = FirebaseStorage.getInstance().getReference("images/")
                                val imageRef = storage.child(imageNames[i])
                                saveImage(imageRef = imageRef, imageBitmap = imageViews[i])
                            }
                            _viewModel.saveMedia(imageMedia)
                            _viewModel.savePost(post = post, media = imageMedia, user = user)
                        }
                    }
                    videoNames.isNotEmpty() -> {
                        videoMedia.url = videoNames
                        CoroutineScope(Dispatchers.IO).launch {
                            for (i in 0 until videoNames.size){
                                val storage = FirebaseStorage.getInstance().getReference("videos/")
                                val videoRef = storage.child(videoNames[i])
                                saveVideo(videoUri = videoViews[i], videoReference = videoRef)
                            }
                            _viewModel.saveMedia(media = videoMedia)
                            _viewModel.savePost(post = post, media = videoMedia, user = user)
                        }
                    }
                    else -> {
                        // saving only post
                        // Creating a new post so that inserted time can be the same than at the current time
                        _viewModel.savePost(post, user, null)
                    }
                }
                binding.title.text.clear()
                binding.body.text.clear()
                imageNames = mutableListOf()
                videoNames = mutableListOf()
                navController.popBackStack()
            }

        }
        binding.takeVideoButton.setOnClickListener {
            onTakeVideoHit()
        }
        binding.takeVideoText.setOnClickListener {
            onTakeVideoHit()
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contxt = context
    }

    private fun deleteButtonListeners() {
        binding.deleteTakenImage.setOnClickListener {
            imageNames.removeAt(0)
            imageViews.removeAt(0)
            takenImages--
            if (imageViews.isNotEmpty()) {
                for (i in 0 until imageViews.size) {
                    addImageUI(imageViews[i])
                }
            } else {
                binding.deleteTakenImage.visibility = View.GONE
                binding.takenImage.visibility = View.GONE
            }
        }
        binding.deleteTakenImage2.setOnClickListener {
            imageNames.removeAt(1)
            imageViews.removeAt(1)
            takenImages--
            if (imageViews.isNotEmpty()) {
                for (i in 0 until imageViews.size) {
                    addImageUI(imageViews[i])
                }
            } else {
                binding.deleteTakenImage.visibility = View.GONE
                binding.takenImage.visibility = View.GONE
            }
        }
        binding.deleteTakenImage3.setOnClickListener {
            imageNames.removeAt(2)
            imageViews.removeAt(2)
            takenImages--
            if (imageViews.isNotEmpty()) {
                for (i in 0 until imageViews.size) {
                    addImageUI(imageViews[i])
                }
            } else {
                binding.deleteTakenImage.visibility = View.GONE
                binding.takenImage.visibility = View.GONE
            }
        }

        binding.deleteTakenVideo.setOnClickListener {
            videoNames.removeAt(0)
            videoViews.removeAt(0)
            takenVideo--
            if (videoViews.isNotEmpty()) {
                for (i in 0 until videoViews.size) {
                    addVideoUI(videoViews[i])
                }
            } else {
                binding.deleteTakenVideo.visibility = View.GONE
                binding.takenVideo.visibility = View.GONE
            }
        }

        binding.deleteTakenVideo1.setOnClickListener {
            videoNames.removeAt(1)
            videoViews.removeAt(1)
            takenVideo--
            if (videoViews.isNotEmpty()) {
                for (i in 0 until videoViews.size) {
                    videoViews[i]
                }
            } else {
                binding.deleteTakenVideo.visibility = View.GONE
                binding.takenVideo.visibility = View.GONE
            }
        }
        binding.deleteTakenVideo2.setOnClickListener {
            videoNames.removeAt(2)
            videoViews.removeAt(2)
            takenVideo--
            if (videoViews.isNotEmpty()) {
                for (i in 0 until videoViews.size) {
                    videoViews[i]
                }
            } else {
                binding.deleteTakenVideo.visibility = View.GONE
                binding.takenVideo.visibility = View.GONE
            }
        }
    }

    private fun retrieveSavedData(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            imageNames = savedInstanceState.getStringArrayList(IMAGE_NAME) as MutableList<String>
            videoNames = savedInstanceState.getStringArrayList(VIDEO_NAME) as MutableList<String>
            imageViews =
                savedInstanceState.getParcelableArrayList<Bitmap>(IMAGE_VIEW) as MutableList<Bitmap>
            videoViews =
                savedInstanceState.getParcelableArrayList<Uri>(VIDEO_VIEW) as MutableList<Uri>
            imageMedia.url =
                savedInstanceState.getStringArrayList(IMAGE_MEDIA_URL) as MutableList<String>
            videoMedia.url =
                savedInstanceState.getStringArrayList(VIDEO_MEDIA_URL) as MutableList<String>
            videoMedia.title = savedInstanceState.get(VIDEO_MEDIA_TITLE) as String
            imageMedia.title = savedInstanceState.get(IMAGE_MEDIA_TITLE) as String
            takenImages = savedInstanceState.get(TAKEN_IMAGES) as Int
            takenVideo = savedInstanceState.getInt(TAKEN_VIDEOS)

            if (takenImages > 0) {
                Log.d(TAG, "taken images >1 in saved instance state")
                for (i in 0 until imageViews.size) {
                    addImageUI(imageViews[i])
                }
            }
            if (takenVideo > 0) {
                Log.d(TAG, "taken videos >1 in saved instance state")
                for (i in 0 until videoViews.size) {
                    addVideoUI(videoViews[i])
                }
            }
            Log.d(TAG, "saved instance value: $imageNames")
        }
    }

    private fun addImageUI(imageBitmap: Bitmap) {
        when (takenImages) {
            0 -> {
                binding.takenImage.visibility = View.GONE
                binding.takenImage1.visibility = View.GONE
                binding.takenImage2Text.visibility = View.GONE
                binding.takenImage2.visibility = View.GONE
                binding.deleteTakenImage.visibility = View.GONE
                binding.deleteTakenImage2.visibility = View.GONE
                binding.deleteTakenImage3.visibility = View.GONE
            }
            1 -> {
                binding.takenImage.setImageBitmap(imageBitmap)
                binding.takenImage.visibility = View.VISIBLE
                binding.deleteTakenImage.visibility = View.VISIBLE
                binding.takenImage1.visibility = View.GONE
                binding.takenImage2.visibility = View.GONE
                binding.takenImage2Text.visibility = View.GONE
                binding.deleteTakenImage2.visibility = View.GONE
                binding.deleteTakenImage3.visibility = View.GONE
            }
            2 -> {
                binding.takenImage1.setImageBitmap(imageBitmap)
                binding.takenImage1.visibility = View.VISIBLE
                binding.deleteTakenImage2.visibility = View.VISIBLE
                binding.takenImage2.visibility = View.GONE
                binding.takenImage2Text.visibility = View.GONE
                binding.deleteTakenImage3.visibility = View.GONE
            }
            3 -> {
                binding.takenImage2.setImageBitmap(imageBitmap)
                binding.takenImage2.visibility = View.VISIBLE
                binding.takenImage2Text.visibility = View.GONE
                binding.deleteTakenImage3.visibility = View.VISIBLE
            }
            else -> {
                binding.takenImage2Text.text = (takenImages-3).toString() + "+"
                binding.takenImage2Text.visibility = View.VISIBLE
                binding.deleteTakenImage3.visibility = View.GONE
            }
        }
    }

    private fun addVideoUI(videoUri: Uri) {
        when (takenVideo) {
            0 -> {
                binding.takenVideo.visibility = View.GONE
                binding.takenVideo1.visibility = View.GONE
                binding.takenVideo2.visibility = View.GONE
                binding.deleteTakenVideo1.visibility = View.GONE
                binding.deleteTakenVideo.visibility = View.GONE
                binding.deleteTakenVideo2.visibility = View.GONE

            }
            1 -> {
                binding.takenVideo.setVideoURI(videoUri)
                binding.takenVideo1.visibility = View.GONE
                binding.takenVideo2.visibility = View.GONE
                binding.takenVideo.visibility = View.VISIBLE
                binding.takenVideo2Text.visibility = View.GONE
                binding.deleteTakenVideo.visibility = View.VISIBLE
                binding.deleteTakenVideo1.visibility = View.GONE
                binding.deleteTakenVideo2.visibility = View.GONE
            }
            2 -> {
                binding.takenVideo1.setVideoURI(videoUri)
                binding.takenVideo1.visibility = View.VISIBLE
                binding.takenVideo2Text.visibility = View.GONE
                binding.takenVideo2.visibility = View.GONE
                binding.deleteTakenVideo1.visibility = View.VISIBLE
                binding.deleteTakenVideo2.visibility = View.GONE
            }
            3 -> {
                binding.takenVideo2.setVideoURI(videoUri)
                binding.takenVideo2Text.visibility = View.GONE
                binding.takenVideo2.visibility = View.VISIBLE
                binding.deleteTakenVideo2.visibility = View.VISIBLE
            }
            else -> {
                binding.takenVideo2Text.text = (takenVideo-3).toString() + "+"
                binding.takenVideo2Text.visibility = View.VISIBLE
                binding.deleteTakenVideo2.visibility = View.GONE
            }
        }
    }

    private fun onTakeVideoHit() {
        if (imageNames.isEmpty()) {
            dispatchTakeVideoIntent()
        } else {
            Snackbar.make(
                requireView(),
                "If you want to share a video, please delete previously taken images",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun onTakePictureHit() {
        if (videoNames.isEmpty()) {
            dispatchTakePictureIntent()
        } else {
            Snackbar.make(
                requireView(),
                "If you want to share a video, please delete previously taken images",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }


    private fun createUserFromCurrentUser(currentUser: FirebaseUser): User {
        return User(username = currentUser.displayName!!, id = currentUser.uid)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(IMAGE_VIEW, ArrayList(imageViews))
        outState.putParcelableArrayList(VIDEO_VIEW, ArrayList(videoViews))
        outState.putString(VIDEO_MEDIA_TITLE, videoMedia.title)
        outState.putString(IMAGE_MEDIA_TITLE, imageMedia.title)
        outState.putStringArrayList(VIDEO_MEDIA_URL, ArrayList(videoMedia.url))
        outState.putStringArrayList(IMAGE_MEDIA_URL, ArrayList(imageMedia.url))
        outState.putStringArrayList(IMAGE_NAME, ArrayList(imageNames))
        outState.putStringArrayList(VIDEO_NAME, ArrayList(videoNames))
        outState.putInt(TAKEN_IMAGES, takenImages)
        outState.putInt(TAKEN_VIDEOS, takenImages)

        super.onSaveInstanceState(outState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Log.d(TAG, "post values: $post")
            if (post.title.isNotEmpty() || post.body.isNotEmpty() || imageNames.isNotEmpty() || videoNames.isNotEmpty()){
                askBeforeQuitting()
            }else{
                navController.popBackStack(R.id.mainFragment, false)
            }
        }
        // Connect or add a new post according to login state
        // TODO uncomment following code
        /*if (!LoginUtil().observeAuthenticationState(loginViewModel, viewLifecycleOwner)){
            viewModel.navigateToLoginFragment()
        }*/
    }

    private fun askBeforeQuitting() {
        val alertDialog = AlertDialog.Builder(this.requireActivity())
        alertDialog.setTitle("Cancel post")
        alertDialog.setMessage("Do you want discard your post and leave ?")

        alertDialog
            .setPositiveButton("Discard") { _, _ ->
                navController.popBackStack(R.id.mainFragment, false)
            }
            .setNegativeButton("Go back") { _, _ ->
            }
            .setNeutralButton("Save") { _, _ ->
                // TODO save with on save instance bundle
            }
        alertDialog.create().show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Getting taken image
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val timeStamp: String = "IMG"+ SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())


            takenImages += 1
            Log.d(TAG, "taken images: $takenImages")
            // adding name of all media in that list
            imageNames.add(timeStamp)
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageViews.add(imageBitmap)
            addImageUI(imageBitmap)
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            // A video has been recorded, we have to create its name and add all information that will help us to save in onCreate
            if (data!=null) {
                val videoUri: Uri = data.data!!

                val vidTimestamp: String = "VID" + SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                takenVideo++
                videoNames.add(vidTimestamp)
                videoViews.add(videoUri)

                // Add the new video in our UI
                addVideoUI(videoUri)
            }
        }
    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(this.requireView(), "Unable to get your camera: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun dispatchTakeVideoIntent() {
        val packageManager = requireActivity().packageManager
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    private fun saveVideo(videoUri: Uri, videoReference: StorageReference){
        videoReference.putFile(videoUri)
            .addOnSuccessListener {
                Log.d(TAG, "video uploaded successfully")
            }.addOnFailureListener {
                saveVideo(videoUri, videoReference)
            }
    }

    private fun saveImage(
        imageBitmap: Bitmap,
        imageRef: StorageReference
    ) {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnCanceledListener {
            Snackbar.make(
                requireView(),
                contxt.getString(R.string.successfully_saved_image_message),
                Snackbar.LENGTH_SHORT
            ).show()
            Log.d(TAG, "photo saved on cloud but you can keep taking other")
        }.addOnCanceledListener {
            Snackbar.make(
                requireView(),
                contxt.getString(R.string.unsuccessfully_saved_image_message),
                Snackbar.LENGTH_SHORT
            ).show()
            Log.d(
                TAG,
                "Your latest taken image has not been successfully saved, please you may consider taking it again"
            )
        }.addOnSuccessListener {snapshot->
            Log.d(TAG, "successfully added images: $snapshot")
        }
    }
}
