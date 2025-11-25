package com.example.relisapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.dao.*
import com.example.relisapp.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val lessonDao: LessonDao,
    private val favoriteLessonDao: FavoriteLessonDao,
    private val userDao: UserDao
) : ViewModel() {

    private val HARDCODED_USER_ID = 6

    init {
        loadUserInfo() // Chạy ngay khi mở app
    }

    // --- CÁC STATE FLOW ---
    private val _allLessonsFromDb = lessonDao.getAllLessonsFlow()
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private val _isNewestFirst = MutableStateFlow(true)
    val isNewestFirst = _isNewestFirst.asStateFlow()

    // 1. State cho Level (A1, B1...)
    private val _selectedLevel = MutableStateFlow<String?>(null)
    val selectedLevel = _selectedLevel.asStateFlow()



    // 2. State cho Type (Chỉ có "listening" hoặc "reading")
    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType = _selectedType.asStateFlow()

    // --- LOGIC LỌC (COMBINE) ---
    val filteredLessons: StateFlow<List<Lessons>> = combine(
        _allLessonsFromDb,
        _searchText,
        _selectedLevel,
        _selectedType, // Thêm biến Type vào bộ lọc
        _isNewestFirst

    ) { lessons, text, level, type, newest ->

        var result = lessons

        // Lọc Search
        if (text.isNotBlank()) {
            result = result.filter { it.title.contains(text, true) }
        }

        // Lọc Level (A1, A2...)
        if (level != null) {
            result = result.filter { it.level.equals(level, ignoreCase = true) }
        }

        // Lọc Type ("listening" hoặc "reading")
        if (type != null) {
            result = result.filter { it.type.equals(type, ignoreCase = true) }
        }

        // Sắp xếp
        if (newest) result.sortedByDescending { it.lessonId } else result.sortedBy { it.lessonId }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- CÁC HÀM UI GỌI ---

    fun onSearchTextChange(text: String) { _searchText.value = text }
    fun onSortToggle() { _isNewestFirst.value = !_isNewestFirst.value }

    fun onLevelSelected(level: String?) {
        // Ấn lại thì bỏ chọn, ấn mới thì chọn
        _selectedLevel.value = if (_selectedLevel.value == level) null else level
    }

    fun onTypeSelected(type: String?) {
        // Ấn lại thì bỏ chọn ("reading" -> null -> tất cả)
        _selectedType.value = if (_selectedType.value == type) null else type
    }

    // --- USER LOGIC & TỰ ĐỘNG CHỌN LEVEL ---

    val favoriteLessons = favoriteLessonDao
        .getFavoritesByUserId(HARDCODED_USER_ID)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val favoriteLessonsDetail: StateFlow<List<Lessons>> =
        favoriteLessonDao.getFavoriteLessonsDetail(HARDCODED_USER_ID)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _currentUser = MutableStateFlow<Users?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun loadUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.getUserById(HARDCODED_USER_ID).collect { user ->
                _currentUser.value = user

                // LOGIC TỰ ĐỘNG CHỌN LEVEL CỦA USER
                // Nếu user tồn tại VÀ user có level VÀ hiện tại chưa ai chọn level nào cả
                /*if (user != null && user.level != null && _selectedLevel.value == null) {
                    _selectedLevel.value = user.level // Tự động set C1, B2... tùy user
                }*/
            }
        }
    }

    // ... (Giữ nguyên toggleFavorite và Factory) ...
    fun toggleFavorite(lessonId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = favoriteLessonDao.getFavorite(HARDCODED_USER_ID, lessonId)
            val favorite = FavoriteLessons(userId = HARDCODED_USER_ID, lessonId = lessonId)
            if (existing != null) favoriteLessonDao.deleteFavorite(favorite)
            else favoriteLessonDao.insertFavorite(favorite)
        }
    }

    class Factory(private val lessonDao: LessonDao, private val favoriteLessonDao: FavoriteLessonDao, private val userDao: UserDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(lessonDao, favoriteLessonDao, userDao) as T
    }
}