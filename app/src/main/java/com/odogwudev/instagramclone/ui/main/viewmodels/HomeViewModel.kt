package com.odogwudev.instagramclone.ui.main.viewmodels


import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.odogwudev.instagramclone.data.pagingsource.FollowPostsPagingSource
import com.odogwudev.instagramclone.repositories.MainRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class HomeViewModel @ViewModelInject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : BasePostViewModel(repository, dispatcher) {

    val pagingFlow = Pager(PagingConfig(PAGE_SIZE)) {
        FollowPostsPagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)

}