package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.entity.User
import com.example.relisapp.nam.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest


// ================================================================================================
// LOGIN STATE
// ================================================================================================
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

// ================================================================================================
// PASSWORD UPDATE STATE
// ================================================================================================
sealed class PasswordUpdateState {
    object Idle : PasswordUpdateState()
    data class Success(val message: String) : PasswordUpdateState()
    data class Error(val message: String) : PasswordUpdateState()
}



// ================================================================================================
// PROFILE UPDATE STATE
// ================================================================================================
sealed class ProfileUpdateState {
    object Idle : ProfileUpdateState()
    object Success : ProfileUpdateState()
    data class Error(val message: String) : ProfileUpdateState()
}

// ================================================================================================
// VIEWMODEL
// ================================================================================================
class AuthViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Loading State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // LOGIN
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(input: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            var user = repository.login(input, password)

            if (user == null) {
                user = repository.login(input, hashPassword(password))
            }

            if (user != null) {
                sessionManager.saveLogin(user.userId, user.role)
                _loginState.value = LoginState.Success(user)
            } else {
                _loginState.value = LoginState.Error("Sai tên đăng nhập hoặc mật khẩu!")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    // REGISTER FULL
    private val _registerSuccess = MutableStateFlow<Boolean?>(null)
    val registerSuccess: StateFlow<Boolean?> = _registerSuccess

    fun registerUser(
        username: String,
        phone: String?,
        password: String
    ) {
        viewModelScope.launch {
            val result = repository.register(
                User(
                    username = username,
                    password = hashPassword(password),
                    phoneNumber = phone,
                    isVerified = 1,
                    role = "user",
                    accountStatus = "active",
                    createdAt = DateUtils.getCurrentTimestamp()
                )
            )
            _registerSuccess.value = result > 0
        }
    }

    fun resetRegisterState() {
        _registerSuccess.value = null
    }

    // CURRENT USER STATE
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val user = withContext(Dispatchers.IO) {
                    repository.getUserById(userId)
                }
                _currentUser.value = user
            } catch (_: Exception) {
                _currentUser.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCurrentUser() {
        _currentUser.value = null
    }

    // UPDATE PROFILE
    private val _profileUpdateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val profileUpdateState: StateFlow<ProfileUpdateState> = _profileUpdateState

    fun updateProfile(
        userId: Int,
        username: String,
        fullName: String,
        avatarBytes: ByteArray?,
        age: Int?
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Check trùng Username
                val exist = repository.getUserByUsername(username)
                if (exist != null && exist.userId != userId) {
                    throw Exception("Tên đăng nhập đã tồn tại!")
                }

                repository.updateUserProfile(
                    userId,
                    username,
                    fullName,
                    avatarBytes,
                    age
                )

                _currentUser.value = repository.getUserById(userId)
                _profileUpdateState.value = ProfileUpdateState.Success

            } catch (e: Exception) {
                _profileUpdateState.value =
                    ProfileUpdateState.Error(e.message ?: "Lỗi cập nhật hồ sơ")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetProfileUpdate() {
        _profileUpdateState.value = ProfileUpdateState.Idle
    }

    // UPDATE PASSWORD (SECURE)
    private val _passwordUpdateState =
        MutableStateFlow<PasswordUpdateState>(PasswordUpdateState.Idle)
    val passwordUpdateState: StateFlow<PasswordUpdateState> = _passwordUpdateState

    fun updatePasswordSecure(oldPassword: String, newPassword: String, currentUser: User) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                if (hashPassword(oldPassword) != currentUser.password) {
                    _passwordUpdateState.value =
                        PasswordUpdateState.Error("Mật khẩu cũ không đúng")
                    return@launch
                }

                val newHash = hashPassword(newPassword)

                withContext(Dispatchers.IO) {
                    repository.updatePassword(currentUser.userId, newHash)
                }

                _passwordUpdateState.value = PasswordUpdateState.Success("Đổi mật khẩu thành công!")


            } catch (e: Exception) {
                _passwordUpdateState.value =
                    PasswordUpdateState.Error(e.message ?: "Lỗi đổi mật khẩu")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPasswordState() {
        _passwordUpdateState.value = PasswordUpdateState.Idle
    }

    // UTIL
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun checkUserExists(username: String, phone: String): User? {
        val userByUsername = repository.getUserByUsername(username)
        if (userByUsername != null) return userByUsername

        val userByPhone = repository.getUserByPhone(phone)
        if (userByPhone != null) return userByPhone

        return null
    }

    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun resetUpdateState() {
        _profileUpdateState.value = ProfileUpdateState.Idle
    }

    fun resetPasswordWithoutOld(userId: Int, newPassword: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val hashed = hashPassword(newPassword)

                repository.updatePassword(userId, hashed)

                _passwordUpdateState.value = PasswordUpdateState.Success("Đặt lại mật khẩu thành công!")
            } catch (e: Exception) {
                _passwordUpdateState.value = PasswordUpdateState.Error(e.message ?: "Lỗi không xác định")
            } finally {
                _isLoading.value = false
            }
        }
    }


    suspend fun getUserByPhone(phone: String): User? {
        return repository.getUserByPhone(phone)
    }



}
