package com.example.relisapp.nam.database.Converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import kotlin.collections.isEmpty

/**
 * ✅ FIX LỖI 5: Utility class để convert giữa Bitmap và ByteArray
 * Dùng cho việc lưu/load avatar từ database
 */
object BitmapConverter {

    /**
     * Convert ByteArray từ database thành Bitmap
     *
     * @param bytes ByteArray từ Room database (avatar BLOB)
     * @return Bitmap nếu convert thành công, null nếu thất bại
     */
    fun byteArrayToBitmap(bytes: ByteArray?): Bitmap? {
        if (bytes == null || bytes.isEmpty()) {
            return null
        }

        return try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convert Bitmap thành ByteArray để lưu vào database
     *
     * @param bitmap Bitmap cần convert
     * @param format Format nén (mặc định JPEG)
     * @param quality Chất lượng nén 0-100 (mặc định 90)
     * @return ByteArray để lưu vào Room
     */
    fun bitmapToByteArray(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 90
    ): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)
        return stream.toByteArray()
    }

    /**
     * Resize bitmap trước khi lưu để tiết kiệm dung lượng
     *
     * @param bitmap Bitmap gốc
     * @param maxWidth Chiều rộng tối đa
     * @param maxHeight Chiều cao tối đa
     * @return Bitmap đã resize
     */
    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int = 512, maxHeight: Int = 512): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }

        val ratio = kotlin.comparisons.minOf(
            maxWidth.toFloat() / width,
            maxHeight.toFloat() / height
        )

        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Convert và resize bitmap để lưu vào database
     *
     * @param bitmap Bitmap gốc
     * @return ByteArray đã resize và nén
     */
    fun prepareAvatarForDatabase(bitmap: Bitmap): ByteArray {
        val resized = resizeBitmap(bitmap, maxWidth = 300, maxHeight = 300)
        return bitmapToByteArray(resized, quality = 85)
    }
}