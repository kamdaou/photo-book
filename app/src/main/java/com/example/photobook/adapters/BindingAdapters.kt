package com.example.photobook.adapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.photobook.R
import com.example.photobook.data.Image
import com.example.photobook.data.PostFirestore
import com.example.photobook.main.MainViewModel
import com.example.photobook.utils.Constants.IMAGE_NAME

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

@BindingAdapter("postToImage0", "viewModel")
fun displayImage0(view: ImageView, frameworkData: PostFirestore, viewModel: MainViewModel){
    if(frameworkData.media != null && frameworkData.media!!.url.isNotEmpty()){
        var mediaReference: Image? = null
        Log.i("BindingAdapter", "media is not empty: ${frameworkData.media!!.url.size}")
        view.visibility = View.VISIBLE
        if (frameworkData.media!!.media_type == IMAGE_NAME){
            mediaReference = viewModel.getImage(imageName = frameworkData.media!!.url.get(0))
        }
        mediaReference?.let {
            Glide
                .with(view.context)
                .load(it.image)
                .placeholder(R.drawable.ic_broken_image)
                .centerInside()
                .into(view)
        }
    }

}



@BindingAdapter("postToImage1", "viewModel")
fun displayImage1(view: ImageView, frameworkData: PostFirestore, viewModel: MainViewModel){
    if(frameworkData.media != null && frameworkData.media!!.url.size >= 2){
        var mediaReference: Image? = null
        Log.i("BindingAdapter", "media is not empty: ${frameworkData.media!!.url.size}")
        view.visibility = View.VISIBLE
        if (frameworkData.media!!.media_type == IMAGE_NAME){
            mediaReference = viewModel.getImage(imageName = frameworkData.media!!.url.get(1))
        }
        mediaReference?.let {
            Glide
                .with(view.context)
                .load(it.image)
                .placeholder(R.drawable.ic_broken_image)
                .centerInside()
                .into(view)
        }
    }

}



@BindingAdapter("postToImage2", "viewModel")
fun displayImage2(view: ImageView, frameworkData: PostFirestore, viewModel: MainViewModel){
    if(frameworkData.media != null && frameworkData.media!!.url.size >= 3){
        var mediaReference: Image? = null
        Log.i("BindingAdapter", "media is not empty: ${frameworkData.media!!.url.size}")
        view.visibility = View.VISIBLE
        if (frameworkData.media!!.media_type == IMAGE_NAME){
            mediaReference = viewModel.getImage(imageName = frameworkData.media!!.url.get(2))
        }
        mediaReference?.let {
            Glide
                .with(view.context)
                .load(it.image)
                .placeholder(R.drawable.ic_broken_image)
                .centerInside()
                .into(view)
        }
    }

}


@BindingAdapter("postToImage3", "viewModel")
fun displayImage3(view: ImageView, frameworkData: PostFirestore, viewModel: MainViewModel){
    if(frameworkData.media != null && frameworkData.media!!.url.size >=4){
        var mediaReference: Image? = null
        Log.i("BindingAdapter", "media is not empty: ${frameworkData.media!!.url.size}")
        view.visibility = View.VISIBLE
        if (frameworkData.media!!.media_type == IMAGE_NAME){
            mediaReference = viewModel.getImage(imageName = frameworkData.media!!.url.get(3))
        }
        mediaReference?.let {
            Glide
                .with(view.context)
                .load(it.image)
                .placeholder(R.drawable.ic_broken_image)
                .centerInside()
                .into(view)
        }
    }

}


@BindingAdapter("postToImage4", "viewModel")
fun displayImage4(view: ImageView, frameworkData: PostFirestore, viewModel: MainViewModel){
    if(frameworkData.media != null && frameworkData.media!!.url.size >= 5){
        var mediaReference: Image? = null
        Log.i("BindingAdapter", "media is not empty: ${frameworkData.media!!.url.size}")
        view.visibility = View.VISIBLE
        if (frameworkData.media!!.media_type == IMAGE_NAME){
            mediaReference = viewModel.getImage(imageName = frameworkData.media!!.url.get(4))
        }
        mediaReference?.let {
            Glide
                .with(view.context)
                .load(it.image)
                .placeholder(R.drawable.ic_broken_image)
                .centerInside()
                .into(view)
        }
    }

}


@BindingAdapter("postRemainingMedia")
fun remainMediaNumber(view: TextView, postFirestore: PostFirestore){
    if(postFirestore.media != null && postFirestore.media!!.url.size >= 6) {
        view.visibility = View.VISIBLE
        view.text = (postFirestore.media!!.url.size - 5).toString()
    }
}
