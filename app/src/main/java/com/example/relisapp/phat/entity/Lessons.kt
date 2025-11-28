package com.example.relisapp.phat.entity
import androidx.room.*
@Entity(
    foreignKeys = [
        ForeignKey(entity = Categories::class, parentColumns = ["categoryId"], childColumns = ["categoryId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Users::class, parentColumns = ["userId"], childColumns = ["createdBy"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class Lessons(
    @PrimaryKey(autoGenerate = true) val lessonId: Int = 0,
    var categoryId: Int,
    var title: String,
    var type: String,               // nghe / doc
    var content: String? = null,    // nullable
    var audioPath: String? = null,  // nullable
    var transcript: String? = null,
    var level: String? = null,
    var createdBy: Int? = null,
    var createdAt: String? = null,
    val isLocked: Int = 0

)

