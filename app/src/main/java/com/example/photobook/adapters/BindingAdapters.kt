package com.example.photobook.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.photobook.data.PostFirestore
import com.example.photobook.network.RemoteRepository

/**
 * postToSubmitter - Takes a post and change
 * it in the name of the submitter
 */
@BindingAdapter("submitter")
fun postToSubmitter(
    textView: TextView,
    postFirestore: PostFirestore
)
{
    textView.text = postFirestore.user?.username
}

/**
 * postToTitle - Takes a post and change it to title
 */
@BindingAdapter("postTitle")
fun postToTitle(
    textView: TextView,
    postFirestore: PostFirestore
)
{
    if (postFirestore.title.length <= 40)
        textView.text = postFirestore.title
    else
        textView.text = postFirestore.title.substring(0, 40).plus("...")
}

/**
 * postToBody - takes a post and changed it to body
 */
@BindingAdapter("postBody")
fun postToBody(
    textView: TextView,
    postFirestore: PostFirestore
)
{
    if (postFirestore.body.length <= 200)
        textView.text = postFirestore.body
    else
        textView.text = postFirestore.body.substring(0, 200).plus("...")
}

/**
 * postToTitle - Takes a post and change it to title
 */
@BindingAdapter("postTitleDetail")
fun postToTitleDetail(
    textView: TextView,
    postFirestore: PostFirestore
)
{
    textView.text = postFirestore.title
}

/**
 * postToBody - takes a post and changed it to body
 */
@BindingAdapter("postBodyDetail")
fun postToBodyDetail(
    textView: TextView,
    postFirestore: PostFirestore
)
{
    textView.text = postFirestore.body
}

/**
 * postToLocation - takes a post and changed it into location
 */
@BindingAdapter("postLocation")
fun postToLocation(textView: TextView, postFirestore: PostFirestore)
{
    textView.text = postFirestore.city
}

/**
 * postToDate - takes a post and transform
 * it into the date when it has been posted
 */
@BindingAdapter("postedAt")
fun postToDate(
    textView: TextView,
    postFirestore: PostFirestore
)
{
    textView.text = postFirestore.inserted_at?.toDate().toString()
}
