package com.example.relisapp.nam.viewmodel

import android.util.Log
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
    object Success : PasswordUpdateState()
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

    // ============================================================================================
    // COMMON LOADING STATE
    // ============================================================================================
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }

    // ============================================================================================
    // LOGIN
    // ============================================================================================
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(input: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            var user = repository.login(input, password)

            // thử hash nếu plaintext không đúng
            if (user == null) {
                user = repository.login(input, hashPassword(password))
            }

            if (user != null) {
                sessionManager.saveLogin(user.userId, user.role)
                _loginState.value = LoginState.Success(user)
            }
            else {
                _loginState.value = LoginState.Error("Sai tên đăng nhập hoặc mật khẩu!")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    // ============================================================================================
    // REGISTER USER
    // ============================================================================================
    private val _registerSuccess = MutableStateFlow<Boolean?>(null)
    val registerSuccess: StateFlow<Boolean?> = _registerSuccess

    fun registerUser(username: String, phone: String, hashedPassword: String) {
        viewModelScope.launch {
            val result = repository.register(
                User(
                    username = username,
                    password = hashedPassword,
                    phoneNumber = phone,
                    role = "user",
                    accountStatus = "active",
                    isVerified = true,
                    createdAt = DateUtils.getCurrentTimestamp() // ✅ Thêm timestamp
                )
            )
            _registerSuccess.value = result > 0
        }
    }

    fun resetRegisterState() {
        _registerSuccess.value = null
    }

    // ============================================================================================
    // GET USER
    // ============================================================================================
    suspend fun getUserById(id: Int): User? = repository.getUserById(id)
    suspend fun getUserByPhone(phone: String): User? = repository.getUserByPhone(phone)
    suspend fun checkUserExists(username: String, phone: String): User? {
        return repository.getUserByUsername(username)
            ?: repository.getUserByPhone(phone)
    }

    // ============================================================================================
    // CURRENT USER STATEFLOW
    // ============================================================================================
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val user = withContext(Dispatchers.IO) { repository.getUserById(userId) }

                _currentUser.value = user
                _isLoading.value = false
            } catch (e: Exception) {
                _currentUser.value = null
                _isLoading.value = false
            }
        }
    }

    fun clearCurrentUser() {
        _currentUser.value = null
    }

    // ============================================================================================
    // UPDATE PROFILE (ATOMIC TRANSACTION)
    // ============================================================================================
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

                val updated = withContext(Dispatchers.IO) {

                    // check trùng username
                    val exist = repository.getUserByUsername(username)
                    if (exist != null && exist.userId != userId) {
                        throw Exception("Tên đăng nhập đã tồn tại")
                    }

                    // ⭐ CẬP NHẬT HỒ SƠ CÓ THÊM TUỔI
                    repository.updateUserProfile(
                        userId = userId,
                        username = username,
                        fullName = fullName,
                        avatar = avatarBytes,
                        age = age
                    )

                    repository.getUserById(userId)
                }

                _currentUser.value = updated
                _isLoading.value = false
                _profileUpdateState.value = ProfileUpdateState.Success

            } catch (e: Exception) {
                _isLoading.value = false
                _profileUpdateState.value =
                    ProfileUpdateState.Error(e.message ?: "Lỗi cập nhật hồ sơ")
            }
        }
    }


    fun resetUpdateState() {
        _profileUpdateState.value = ProfileUpdateState.Idle
    }

    // ============================================================================================
    // UPDATE PASSWORD (SECURE)
    // ============================================================================================
    private val _passwordUpdateState =
        MutableStateFlow<PasswordUpdateState>(PasswordUpdateState.Idle)
    val passwordUpdateState: StateFlow<PasswordUpdateState> = _passwordUpdateState

    fun updatePasswordSecure(
        oldPassword: String,
        newPassword: String,
        currentUser: User
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // kiểm tra mật khẩu cũ
                if (hashPassword(oldPassword) != currentUser.password) {
                    _isLoading.value = false
                    _passwordUpdateState.value =
                        PasswordUpdateState.Error("Mật khẩu cũ không đúng")
                    return@launch
                }

                val newHash = hashPassword(newPassword)

                withContext(Dispatchers.IO) {
                    repository.updatePassword(currentUser.userId, newHash)
                }

                _isLoading.value = false
                _passwordUpdateState.value = PasswordUpdateState.Success

            } catch (e: Exception) {
                _isLoading.value = false
                _passwordUpdateState.value =
                    PasswordUpdateState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun resetPasswordState() {
        _passwordUpdateState.value = PasswordUpdateState.Idle
    }

    // ============================================================================================
    // AVATAR / USERNAME / FULLNAME (Legacy Methods)
    // ============================================================================================
    fun updateAvatar(avatar: ByteArray?) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                repository.updateAvatar(userId, avatar)
            }
        }
    }

    fun updateUsername(newName: String) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId != -1) repository.updateUsername(userId, newName)
        }
    }

    fun updateFullName(newName: String) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId != -1) repository.updateFullName(userId, newName)
        }
    }

    // ============================================================================================
    // UTIL
    // ============================================================================================
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Thêm dưới phần PasswordUpdateState & các hàm khác trong AuthViewModel

    fun resetPasswordWithoutOld(
        userId: Int,
        newPassword: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Hash mật khẩu mới
                val newHash = hashPassword(newPassword)

                // Cập nhật DB
                withContext(Dispatchers.IO) {
                    repository.updatePassword(userId, newHash)
                }

                _isLoading.value = false
                _passwordUpdateState.value = PasswordUpdateState.Success

            } catch (e: Exception) {
                _isLoading.value = false
                _passwordUpdateState.value =
                    PasswordUpdateState.Error(e.message ?: "Lỗi khi đặt lại mật khẩu")
            }
        }
    }

    fun registerUserFull(
        username: String,
        password: String,
        email: String? = null,
        phone: String? = null,
        fullName: String? = null,
        age: Int? = null
    ) {
        viewModelScope.launch {
            val result = repository.register(
                User(
                    username = username,
                    password = hashPassword(password),
                    email = email,
                    phoneNumber = phone,
                    fullName = fullName,
                    age = age,
                    role = "user",
                    accountStatus = "active",
                    isVerified = false, // Chưa xác thực
                    createdAt = DateUtils.getCurrentTimestamp() // ✅ Timestamp
                )
            )
            _registerSuccess.value = result > 0
        }
    }

    suspend fun validateRegistration(username: String, phone: String?): String? {
        // Check username exists
        val existingUser = repository.getUserByUsername(username)
        if (existingUser != null) {
            return "Tên đăng nhập đã tồn tại"
        }

        // Check phone exists
        if (!phone.isNullOrBlank()) {
            val existingPhone = repository.getUserByPhone(phone)
            if (existingPhone != null) {
                return "Số điện thoại đã được đăng ký"
            }
        }

        return null // Validation passed
    }


}
