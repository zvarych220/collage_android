package com.example.click_projeck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import data.User
import com.example.click_projeck.R

class UserAdapter(private val onClick: (User) -> Unit) :
    ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(itemView: View, val onClick: (User) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        private val ivProfile: ImageView = itemView.findViewById(R.id.ivUserProfile)
        private val tvRole: TextView = itemView.findViewById(R.id.tvUserRole)
        private var currentUser: User? = null

        init {
            itemView.setOnClickListener {
                currentUser?.let { onClick(it) }
            }
        }

        fun bind(user: User) {
            currentUser = user
            tvName.text = user.name
            tvEmail.text = user.email
            tvRole.text = if (user.isAdmin) "Admin" else "User"

            // Load profile image here if available
            // Glide.with(itemView.context).load(user.profileImage).into(ivProfile)
        }
    }

    companion object {
        private val UserDiffCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }
}