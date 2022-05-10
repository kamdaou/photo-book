package com.example.photobook.addPost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.photobook.R
import com.example.photobook.data.Post
import com.example.photobook.databinding.FragmentAddPostBinding

class AddPostFragment : Fragment() {

    private lateinit var binding: FragmentAddPostBinding
    private var takenImages = 0
    private var takenVideo = 0
    private var post = Post(submitter_id = "")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }
}
