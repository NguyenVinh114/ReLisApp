package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.Comments
import com.example.relisapp.phat.repository.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentViewModel(private val repo: CommentRepository) : ViewModel() {

    private val _comments = MutableStateFlow<List<Comments>>(emptyList())
    val comments: StateFlow<List<Comments>> = _comments

    fun loadComments() {
        viewModelScope.launch {
            _comments.value = repo.getComments()
        }
    }

    fun addComment(comments: Comments) {
        viewModelScope.launch {
            repo.addComment(comments)
            loadComments()
        }
    }
}
