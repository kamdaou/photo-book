package com.example.photobook.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.photobook.R
import com.example.photobook.adapters.*
import com.example.photobook.data.Comment
import com.example.photobook.data.PostFirestore
import com.example.photobook.databinding.CommentItemBinding
import com.example.photobook.databinding.FragmentDetailBinding
import com.example.photobook.utils.GlideApp
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

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
    private lateinit var commentItemBinding: CommentItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        val comment = Comment()
        val viewModelFactory = DetailViewModelFactory(requireActivity().application)

        binding = FragmentDetailBinding.inflate(inflater)
        commentItemBinding = CommentItemBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.comment = comment
        selectedPost = DetailFragmentArgs.fromBundle(requireArguments()).post
        binding.post = selectedPost
        _viewModel = ViewModelProvider(this, viewModelFactory)[DetailViewModel::class.java]
        _viewModel.loadComments(selectedPost.id)

        bindingListener(comment)

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

    /**
     * bindingListener - Listens to click on the UI
     *
     * @comment: The comment that might be posted
     */
    private fun bindingListener(comment: Comment) {
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
        binding.commentThePost.setOnClickListener {
            Log.d(TAG, "comment the post clicked")
            showCommentEditText()
        }
        commentItemBinding.answerButton.setOnClickListener {
            showCommentEditText()
        }

        binding.postTheComment.setOnClickListener {
            Log.i(TAG, "post the comment clicked")
            postComment(_viewModel, comment, selectedPost)
            _viewModel.loadComments(selectedPost.id)
            //Closes the keyboard
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        commentItemBinding.postTheComment.setOnClickListener {
            Log.i(TAG, "post the comment clicked")
            postComment(_viewModel, comment, selectedPost)
            //Closes the keyboard
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    /**
     * postComment - posts a comment
     *
     * @viewModel: The detailViewModel
     * @comment: The comment to be posted
     * @selectedPost: Post that is being commented
     */
    private fun postComment(
        viewModel: DetailViewModel,
        comment: Comment,
        selectedPost: PostFirestore
    ) {
        Log.i(TAG, "checking user")
        binding.commentEditText.visibility = View.GONE
        binding.postTheComment.visibility = View.GONE
        viewModel.observable.setBody(comment.body)
        comment.post_id = selectedPost.id
        comment.user_id = FirebaseAuth.getInstance().currentUser?.uid!!
        if (comment.body != "")
        {
            Log.i(TAG, "saving comment")
            viewModel.saveComment(comment)
        }
        else
        {
            Snackbar.make(requireView(), getString(R.string.comment_empty_message), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMediaFragment(selecteMediaNumber: Int) {
        if (!selectedPost.media?.url.isNullOrEmpty()) {
            findNavController().navigate(
                DetailFragmentDirections.actionDetailFragmentToMediaFragment(
                    selectedPost, selecteMediaNumber
                )
            )
        }
    }

    /*
    *Shows the edit text for commenting a post or replying to comment
    *Shows also the button for saving comment
     */
    private fun showCommentEditText() {
        binding.commentEditText.visibility = View.VISIBLE
        binding.postTheComment.visibility = View.VISIBLE
    }

    /*
*Clear images showed with glide
 */
    private fun clearImages(image1: ImageView) {
        GlideApp
            .with(image1.context)
            .clear(image1)
    }

    override fun onResume() {
        super.onResume()
        displayImage0(binding.image1, selectedPost)
        displayImage1(binding.image2, selectedPost)
        displayImage2(binding.image3, selectedPost)
        displayImage3(binding.image4, selectedPost)
        displayImage4(binding.image5, selectedPost)
        remainMediaNumber(binding.image5Text, selectedPost)
    }

    override fun onPause() {
        super.onPause()
        clearImages(binding.image1)
        clearImages(binding.image2)
        clearImages(binding.image3)
        clearImages(binding.image4)
        clearImages(binding.image5)
    }

    override fun onStart() {
        super.onStart()
        val commentAdapter = CommentRecyclerViewAdapter()
        binding.comments.adapter = commentAdapter
        _viewModel.comments?.observe(viewLifecycleOwner){ commentList ->
            Log.i(TAG, "comment value changed: $commentList")
            commentAdapter.submitList(commentList.comment?.toMutableList()?.reversed())
        }
    }
}
