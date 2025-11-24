package com.example.relisapp.nam.utils

import com.example.relisapp.nam.database.entity.User

/**
 * Quản lý phân quyền trong ứng dụng
 * Hierarchy: admin > mod > user
 */
object PermissionManager {

    // Role constants
    const val ROLE_ADMIN = "admin"
    const val ROLE_MOD = "mod"
    const val ROLE_USER = "user"

    /**
     * Kiểm tra user có phải admin không
     */
    fun isAdmin(user: User?): Boolean {
        return user?.role == ROLE_ADMIN
    }

    /**
     * Kiểm tra user có phải mod hoặc cao hơn không
     */
    fun isMod(user: User?): Boolean {
        return user?.role == ROLE_MOD || user?.role == ROLE_ADMIN
    }

    /**
     * Kiểm tra user có quyền xem danh sách user không
     * Chỉ admin và mod mới xem được
     */
    fun canViewUserList(user: User?): Boolean {
        return isMod(user)
    }

    /**
     * Kiểm tra user có quyền khóa/mở khóa tài khoản không
     * Chỉ admin mới được
     */
    fun canLockUser(currentUser: User?, targetUser: User?): Boolean {
        if (!isAdmin(currentUser)) return false

        // Không thể tự khóa chính mình
        if (currentUser?.userId == targetUser?.userId) return false

        return true
    }

    /**
     * Kiểm tra user có quyền thay đổi role không
     * Chỉ admin mới được
     */
    fun canChangeRole(currentUser: User?, targetUser: User?): Boolean {
        if (!isAdmin(currentUser)) return false

        // Không thể thay đổi role của chính mình
        if (currentUser?.userId == targetUser?.userId) return false

        return true
    }

    /**
     * Kiểm tra user có quyền xóa user không
     * Chỉ admin mới được, và không được xóa chính mình
     */
    fun canDeleteUser(currentUser: User?, targetUser: User?): Boolean {
        if (!isAdmin(currentUser)) return false

        // Không thể tự xóa chính mình
        if (currentUser?.userId == targetUser?.userId) return false

        return true
    }

    /**
     * Kiểm tra user có quyền chỉnh sửa thông tin user khác không
     * Admin có thể sửa tất cả, mod không thể sửa admin
     */
    fun canEditUser(currentUser: User?, targetUser: User?): Boolean {
        if (targetUser == null) return false

        // User chỉ được sửa thông tin của chính mình
        if (currentUser?.userId == targetUser.userId) return true

        // Admin được sửa tất cả
        if (isAdmin(currentUser)) return true

        // Mod chỉ được sửa user thường
        if (isMod(currentUser) && targetUser.role == ROLE_USER) return true

        return false
    }

    /**
     * Kiểm tra user có thể nâng cấp lên role nào
     * Admin có thể nâng bất kỳ ai
     */
    fun canPromoteToRole(currentUser: User?, targetRole: String): Boolean {
        if (!isAdmin(currentUser)) return false
        return targetRole in listOf(ROLE_USER, ROLE_MOD, ROLE_ADMIN)
    }

    /**
     * Get role label để hiển thị
     */
    fun getRoleLabel(role: String?): String {
        return when (role) {
            ROLE_ADMIN -> "Quản trị viên"
            ROLE_MOD -> "Moderator"
            ROLE_USER -> "Người dùng"
            else -> "Không xác định"
        }
    }

    /**
     * Get role color
     */
    fun getRoleColor(role: String?): Long {
        return when (role) {
            ROLE_ADMIN -> 0xFF1565C0
            ROLE_MOD -> 0xFFE65100
            ROLE_USER -> 0xFF6A1B9A
            else -> 0xFF757575
        }
    }

    /**
     * Kiểm tra tài khoản có hoạt động không
     */
    fun isAccountActive(user: User?): Boolean {
        return user?.accountStatus == "active"
    }

    /**
     * Kiểm tra tài khoản có bị khóa không
     */
    fun isAccountLocked(user: User?): Boolean {
        return user?.accountStatus == "locked"
    }

    /**
     * Message khi không có quyền
     */
    fun getPermissionDeniedMessage(action: String): String {
        return "⚠️ Bạn không có quyền $action"
    }
}

// Extension functions cho User
fun User?.isAdmin(): Boolean = PermissionManager.isAdmin(this)
fun User?.isMod(): Boolean = PermissionManager.isMod(this)
fun User?.isActive(): Boolean = PermissionManager.isAccountActive(this)
fun User?.isLocked(): Boolean = PermissionManager.isAccountLocked(this)
fun User?.canLock(target: User?): Boolean = PermissionManager.canLockUser(this, target)
fun User?.canChangeRole(target: User?): Boolean = PermissionManager.canChangeRole(this, target)
fun User?.canDelete(target: User?): Boolean = PermissionManager.canDeleteUser(this, target)
fun User?.canEdit(target: User?): Boolean = PermissionManager.canEditUser(this, target)