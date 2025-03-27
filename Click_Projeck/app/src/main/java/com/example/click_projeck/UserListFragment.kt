package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.click_projeck.databinding.FragmentUserListBinding
import data.AppDatabase
import data.User
import kotlinx.coroutines.launch

class UserListFragment : Fragment() {
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserAdapter { user ->
            findNavController().navigate(
                R.id.action_userListFragment_to_userDetailFragment,
                bundleOf("userId" to (user.id ?: -1))
            )
        }

        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@UserListFragment.adapter
        }

        loadUsers()
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val users = db.userDao().getAllUsers()
            adapter.submitList(users)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}