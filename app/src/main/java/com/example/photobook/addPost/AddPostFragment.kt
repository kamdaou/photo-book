package com.example.photobook.addPost

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.photobook.BuildConfig
import com.example.photobook.R
import com.example.photobook.data.Media
import com.example.photobook.data.Post
import com.example.photobook.data.User
import com.example.photobook.databinding.FragmentAddPostBinding
import com.example.photobook.utils.Constants
import com.example.photobook.utils.Constants.IMAGE_NAME
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

internal const val TAG = "AddPostFragment"
private const val IMAGE_VIEW = "imageView"
private const val TAKEN_IMAGES = "takenImage"
private const val IMAGE_MEDIA_URL = "imageMediaUrl"
private const val IMAGE_MEDIA_TITLE = "imageMediaTitle"
private const val REQUEST_CAMERA_CODE = 2
private const val REQUEST_IMAGE_CAPTURE = 12

class AddPostFragment : Fragment()
{

    private val _viewModel: AddPostViewModel by viewModel()
    private lateinit var binding: FragmentAddPostBinding
    private var post = Post(submitter_id = "")
    private lateinit var navController: NavController
    private lateinit var contxt: Context
    private var takenImages = 0
    private var imageNames:MutableList<String> = mutableListOf()
    private var imageViews:MutableList<Bitmap> = mutableListOf()
    private val imageMedia = Media(media_type = IMAGE_NAME, title="", url = listOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        /* val viewModelFactory = AddPostViewModelFactory() */
        val currentUser = FirebaseAuth.getInstance().currentUser

        binding = FragmentAddPostBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.post = post

        _viewModel.snackBarContain.observe(viewLifecycleOwner){
            if (it != null) {
                Snackbar.make(this.requireView(), contxt.getString(it), Snackbar.LENGTH_SHORT).show()
                _viewModel.onSnackBarShowed()
            }
        }

        binding.savePostButton.setOnClickListener{
            /* Closes the keyboard */
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            _viewModel.observable.setBody(post.body)
            _viewModel.observable.setTitle(post.title)
            /**
             * TODO replace the following line by post.submitter_id = currentUser.uid!!
             * after implementing login
             */
            post.submitter_id = "userId"
            // TODO add cty using location functions
            post.city = ""
            /**
             * TODO replace the following line by val user = createUserFromCurrentUser(currentUser)
             * after implementing login
             */
            val user = User("username", "userid")

            if (imageNames.isNotEmpty())
            {
                imageMedia.url = imageNames
                for (i in 0 until imageViews.size)
                    _viewModel.saveImage(imageBitmap = imageViews[i], imageName = imageNames[i])

                _viewModel.saveMedia(imageMedia)
                _viewModel.savePost(post = post, media = imageMedia, user = user)
            }
            else
            {
                _viewModel.savePost(post, user)
            }
        }
        _viewModel.savingStatus.observe(viewLifecycleOwner) {
            if (it == Constants.Status.DONE) {
                binding.title.text.clear()
                binding.body.text.clear()
                _viewModel.onSaved()
                navController.navigate(R.id.action_addPostFragment_to_mainFragment)
            }
        }

        /* Disabling the take picture view if device doesn't have camera */
        val pm  = requireActivity().packageManager
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
        {
            binding.takePicture.visibility = View.GONE
        }

        binding.takePicture.setOnClickListener {
            onTakePictureHit()
        }

        handleSavedInstanceBundle(savedInstanceState)
        deleteTakenImages()

        return binding.root
    }

    /**
     * deleteTakenImages - removes views in the ui when user clicks
     * on the cross as if we are deleting the pictures
     */
    private fun deleteTakenImages() {
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
    }

    /**
     * handleSavedInstanceBundle - Addes images in the ui
     *
     * @savedInstanceState - value of the bundle when the app was closed
     */
    private fun handleSavedInstanceBundle(savedInstanceState: Bundle?)
    {
        if (savedInstanceState != null)
        {
            imageNames = savedInstanceState.getStringArrayList(IMAGE_NAME) as MutableList<String>
            imageViews =
                savedInstanceState.getParcelableArrayList<Bitmap>(IMAGE_VIEW) as MutableList<Bitmap>
            imageMedia.url =
                savedInstanceState.getStringArrayList(IMAGE_MEDIA_URL) as MutableList<String>
            imageMedia.title = savedInstanceState.get(IMAGE_MEDIA_TITLE) as String
            takenImages = savedInstanceState.get(TAKEN_IMAGES) as Int
            if (takenImages > 0) {
                Log.d(TAG, "taken images >1 in saved instance state")
                for (i in 0 until imageViews.size) {
                    addImageUI(imageViews[i])
                }
            }
        }
            Log.d(TAG, "saved instance value: $imageNames")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (
            requestCode == REQUEST_IMAGE_CAPTURE &&
            resultCode == Activity.RESULT_OK
        )
        {
            val timeStamp: String = "IMG"+
                    SimpleDateFormat(
                        "yyyyMMdd_HHmmss",
                        Locale.getDefault()
                    )
                        .format(Date())
            val imageBitmap = data?.extras?.get("data") as Bitmap

            takenImages += 1
            imageNames.add(timeStamp)
            imageViews.add(imageBitmap)
            addImageUI(imageBitmap)
        }
    }

    /**
     * addImageUI - Shows taken image in the ui
     *
     * @imageBitmap - Bitmap of the image that should be showed
     */
    private fun addImageUI(imageBitmap: Bitmap)
    {
        when (takenImages)
        {
            0 ->
            {
                binding.takenImage.visibility = View.GONE
                binding.takenImage1.visibility = View.GONE
                binding.takenImage2Text.visibility = View.GONE
                binding.takenImage2.visibility = View.GONE
                binding.deleteTakenImage.visibility = View.GONE
                binding.deleteTakenImage2.visibility = View.GONE
                binding.deleteTakenImage3.visibility = View.GONE
            }
            1 ->
            {
                binding.takenImage.setImageBitmap(imageBitmap)
                binding.takenImage.visibility = View.VISIBLE
                binding.deleteTakenImage.visibility = View.VISIBLE
                binding.takenImage1.visibility = View.GONE
                binding.takenImage2.visibility = View.GONE
                binding.takenImage2Text.visibility = View.GONE
                binding.deleteTakenImage2.visibility = View.GONE
                binding.deleteTakenImage3.visibility = View.GONE
            }
            2 ->
            {
                binding.takenImage1.setImageBitmap(imageBitmap)
                binding.takenImage1.visibility = View.VISIBLE
                binding.deleteTakenImage2.visibility = View.VISIBLE
                binding.takenImage2.visibility = View.GONE
                binding.takenImage2Text.visibility = View.GONE
                binding.deleteTakenImage3.visibility = View.GONE
            }
            3 ->
            {
                binding.takenImage2.setImageBitmap(imageBitmap)
                binding.takenImage2.visibility = View.VISIBLE
                binding.takenImage2Text.visibility = View.GONE
                binding.deleteTakenImage3.visibility = View.VISIBLE
            }
            else ->
            {
                binding.takenImage2Text.text = (takenImages-3).toString() + "+"
                binding.takenImage2Text.visibility = View.VISIBLE
                binding.deleteTakenImage3.visibility = View.GONE
            }
        }
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        contxt = context
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putParcelableArrayList(IMAGE_VIEW, ArrayList(imageViews))
        outState.putString(IMAGE_MEDIA_TITLE, imageMedia.title)
        outState.putStringArrayList(IMAGE_MEDIA_URL, ArrayList(imageMedia.url))
        outState.putStringArrayList(IMAGE_NAME, ArrayList(imageNames))
        outState.putInt(TAKEN_IMAGES, takenImages)

        super.onSaveInstanceState(outState)
    }

    /**
     * createUserFromCurrentUser - Create a User using our data model
     *
     * @currentUser: The user that is connected to firebase
     *
     * Return: User
     */
    private fun createUserFromCurrentUser(currentUser: FirebaseUser): User
    {
        return User(username = currentUser.displayName!!, id = currentUser.uid)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Log.d(TAG, "post values: $post")
            if (post.title.isNotEmpty() || post.body.isNotEmpty()){
                askBeforeQuitting()
            }else{
                navController.popBackStack(R.id.mainFragment, false)
            }
        }
    }

    /**
     * askBeforeQuitting - Asks user via an alertDialog if he
     * wants to discard his post before leaving the fragment
     */
    private fun askBeforeQuitting()
    {
        val alertDialog = AlertDialog.Builder(this.requireActivity())
        alertDialog.setTitle(getString(R.string.discard_post))
        alertDialog.setMessage(getString(R.string.do_you_want_to_leave))

        alertDialog
            .setPositiveButton(getString(R.string.discard)) { _, _ ->
                navController.popBackStack(R.id.mainFragment, false)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
        alertDialog.create().show()
    }

    /**
     * onTakePictureHit - Asks camera permissions by calling
     * requestCameraPermission if permission are not approved
     * or call dispatchTakePictureIntent otherwise
     */
    private fun onTakePictureHit()
    {
        if (cameraPermissionApproved())
            dispatchTakePictureIntent()
        else
            requestCameraPermission()
    }

    /**
     * dispatchTakePictureIntent - opens the camera application of the
     * phone to allow user to take pictures.
     */
    @Deprecated("Deprecated in Java")
    private fun dispatchTakePictureIntent()
    {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
        catch (e: ActivityNotFoundException) {
            Snackbar.make(this.requireView(), buildString {
        append(getString(R.string.unable_to_get_your_camera))
        append(e.message)
    }, Snackbar.LENGTH_LONG).show()
        }
    }

    /**
     * cameraPermissionApproved - Checks if camera permission is approved
     *
     * Return - true if permission is approved, false otherwise
     */
    private fun cameraPermissionApproved():Boolean
    {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat
            .checkSelfPermission(
                contxt,
                Manifest.permission.CAMERA
            )
    }

    /**
     * requestCameraPermission - Checks if camera permission are granted.
     * If not, requests camera permission
     */
    @Deprecated("Deprecated in Java")
    private fun requestCameraPermission()
    {
        if (cameraPermissionApproved())
            return
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_CODE)
        {
            if (grantResults.isEmpty() ||
                grantResults.first() ==
                PackageManager.PERMISSION_DENIED)
            {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                {
                    Snackbar.make(
                        binding.root,
                        R.string.camera_permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else
                {
                    Snackbar.make(
                        binding.root,
                        R.string.camera_permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(R.string.settings) {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }
                        .setAction(R.string.ok) {
                            requestCameraPermission()
                        }
                        .show()
                }
            }
            else
            {
                dispatchTakePictureIntent()
            }
        }
    }
}
