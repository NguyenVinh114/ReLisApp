package com.example.relisapp.data.local.entity.model

import androidx.room.Embedded
import com.example.relisapp.data.local.entity.Comments

data class CommentWithUser(
    // @Embedded giúp Room nhét tất cả các cột của bảng Comments vào biến này
    @Embedded val comment: Comments,

    // Hai cột này lấy từ bảng Users nhờ lệnh JOIN
    val username: String?,
    val fullName: String?
)