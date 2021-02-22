package com.odogwudev.instagramclone.ui.main.fragments

import android.os.Bundle
import android.util.EventLog
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.odogwudev.instagramclone.R
import com.odogwudev.instagramclone.others.Event
import com.odogwudev.instagramclone.ui.main.viewmodels.BasePostViewModel
import com.odogwudev.instagramclone.ui.main.viewmodels.ProfileViewModel
import com.odogwudev.instagramclone.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class ProfileFragment : BasePostFragment(R.layout.fragment_profile) {

    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: ProfileViewModel by viewModels()
            return vm
        }

    protected val viewModel: ProfileViewModel
        get() = basePostViewModel as ProfileViewModel

    protected open val uid: String
        get() = FirebaseAuth.getInstance().uid!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subsribeToObservers()

        btnToggleFollow.isVisible = false
        viewModel.loadProfile(uid)

        lifecycleScope.launch {
            viewModel.getPagingFlow(uid).collect {
                postAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            postAdapter.loadStateFlow.collectLatest {
                profilePostsProgressBar?.isVisible = it.refresh is LoadState.Loading ||
                        it.append is LoadState.Loading
            }
        }
    }

    private fun setupRecyclerView() = rvPosts.apply {
        adapter = postAdapter
        itemAnimator = null
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subsribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                profileMetaProgressBar.isVisible = false
                snackbar(it)
            },
            onLoading = {
                profileMetaProgressBar.isVisible = true
            }
        ) { user ->
            profileMetaProgressBar.isVisible = false
            tvUsername.text = user.username
            tvProfileDescription.text = if (user.description.isEmpty()) {
                requireContext().getString(R.string.no_description)
            } else user.description
            glide.load(user.profilePictureUrl).into(ivProfileImage)
        })

        basePostViewModel.deletePostStatus.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                snackbar(it)
            }
        ) { deletedPost ->
            postAdapter.refresh()
        })
    }

}