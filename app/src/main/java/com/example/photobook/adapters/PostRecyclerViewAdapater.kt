package com.example.photobook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.photobook.data.PostFirestore
import com.example.photobook.databinding.PostItemBinding

class PostRecyclerViewAdapter(
    private val clickListener: PostListener
): ListAdapter<PostFirestore, PostRecyclerViewAdapter.ViewHolder>(FrameworkDiffCallback()) {


    class ViewHolder private constructor(private val binding: PostItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: PostListener, item: PostFirestore){
            binding.post = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: PostItemBinding =
                    PostItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class FrameworkDiffCallback: DiffUtil.ItemCallback<PostFirestore>() {
        override fun areItemsTheSame(oldItem: PostFirestore, newItem: PostFirestore): Boolean {
            return oldItem.body == newItem.body && oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: PostFirestore, newItem: PostFirestore): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }
}

class PostListener (val clickListener: (body:String)->Unit){
    fun onClick(frameworkData: PostFirestore) = clickListener(frameworkData.id)
}
