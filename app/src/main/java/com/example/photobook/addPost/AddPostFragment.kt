package com.example.photobook.addPost

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.photobook.R
import com.example.photobook.data.Post
import com.example.photobook.data.User
import com.example.photobook.databinding.FragmentAddPostBinding
import com.example.photobook.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
    private var post = Post(submitter_id = "")
    private lateinit var navController: NavController
    private lateinit var contxt: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        val viewModelFactory = AddPostViewModelFactory()
        val currentUser = FirebaseAuth.getInstance().currentUser

        binding = FragmentAddPostBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.post = post

        _viewModel = ViewModelProvider(this, viewModelFactory)[AddPostViewModel::class.java]
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

            _viewModel.savePost(post, user)
        }
        _viewModel.savingStatus.observe(viewLifecycleOwner) {
            if (it == Constants.Status.DONE) {
                binding.title.text.clear()
                binding.body.text.clear()
                navController.popBackStack()
            }
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contxt = context
    }



    private fun createUserFromCurrentUser(currentUser: FirebaseUser): User {
        return User(username = currentUser.displayName!!, id = currentUser.uid)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        alertDialog.create().show()
    }
}
