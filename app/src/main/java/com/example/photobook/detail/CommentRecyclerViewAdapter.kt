package com.example.photobook.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.photobook.data.Comment
import com.example.photobook.databinding.CommentItemBinding

class CommentRecyclerViewAdapter:
    ListAdapter<Comment,
            CommentRecyclerViewAdapter.ViewHolder>(
        FrameworkDiffCallback()
    )
{
    class ViewHolder private constructor(
        private val binding: CommentItemBinding
        ): RecyclerView.ViewHolder(
        binding.root
    )
    {
        fun bind(comment: Comment)
        {
            binding.comment = comment
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder
            {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: CommentItemBinding =
                    CommentItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class FrameworkDiffCallback:
        DiffUtil.ItemCallback<Comment>()
    {
        override fun areItemsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean
        {
            return oldItem.body == newItem.body
        }

        override fun areContentsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean
        {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder
    {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    )
    {
        val item = getItem(position)
        holder.bind(item)
    }
}
