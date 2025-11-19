package com.example.relisapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.repository.UserRepository
import com.example.relisapp.model.User
import com.example.relisapp.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // LOGIN STATE
    private val _loginState = MutableStateFlow<User?>(null)
    val loginState: StateFlow<User?> = _loginState

    // REGISTER STATE
    private val _registerSuccess = MutableStateFlow<Boolean?>(null)
    val registerSuccess: StateFlow<Boolean?> = _registerSuccess


    // ============================
    // LOGIN
    // ============================
    fun login(input: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(input, password)
            _loginState.value = user

            if (user != null) {
                sessionManager.saveLogin(user.userId)   // ⭐ CẦN THIẾT
            }
        }
    }


    // ============================
    // REGISTER (chuẩn hoá duy nhất 1 hàm)
    // ============================
    fun registerUser(username: String, phone: String, passwordHash: String) {
        viewModelScope.launch {
            val result = repository.register(
                User(
                    email = "",
                    username = username,
                    phoneNumber = phone,
                    passwordHash = passwordHash,
                    accountStatus = "active",
                    userRole = "user",
                    isVerified = true
                )
            )

            _registerSuccess.value = result > 0
        }
    }


    // ============================
    // CHECK EXIST USER
    // ============================
    suspend fun checkUserExists(username: String, phone: String): User? {
        val byUsername = repository.getUserByUsername(username)
        if (byUsername != null) return byUsername

        return repository.getUserByPhone(phone)
    }


    // ============================
    // GET USER
    // ============================
    suspend fun getUserById(id: Int): User? = repository.getUserById(id)


    // ============================
    // UPDATE PASSWORD
    // ============================
    fun updatePassword(userId: Int, newPassword: String) {
        viewModelScope.launch {
            repository.updatePassword(userId, newPassword)
        }
    }


    // ============================
    // UPDATE AVATAR (FIXED)
    // ============================
    fun updateAvatar(avatar: ByteArray?) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                repository.updateAvatar(userId, avatar)
            }
        }
    }


    // ============================
    // DELETE ACCOUNT
    // ============================
    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }


    // Reset StateFlow
    fun resetLoginState() = run { _loginState.value = null }
    fun resetRegisterState() = run { _registerSuccess.value = null }
}
