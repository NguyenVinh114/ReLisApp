package com.example.relisapp.phat.entity
import androidx.room.*
@Entity
data class Categories(
    @PrimaryKey(autoGenerate = true) val categoryId: Int = 0,
    val categoryName: String,
    val type: String              // nghe / doc
)