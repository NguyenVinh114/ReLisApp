package com.example.relisapp.nam.model

enum class StreakMilestone(
    val days: Int,
    val emoji: String,
    val title: String,
    val description: String
) {
    DAY_3(
        days = 3,
        emoji = "🔥",
        title = "3 ngày liên tiếp",
        description = "Khởi đầu tuyệt vời!"
    ),
    DAY_7(
        days = 7,
        emoji = "🏅",
        title = "1 tuần liên tiếp",
        description = "Bạn đã duy trì suốt 7 ngày!"
    ),
    DAY_14(
        days = 14,
        emoji = "🌟",
        title = "2 tuần liên tiếp",
        description = "Độ bền tuyệt vời!"
    ),
    DAY_30(
        days = 30,
        emoji = "🏆",
        title = "1 tháng liên tiếp",
        description = "1 tháng kiên trì – đỉnh cao!"
    ),
    DAY_60(
        days = 60,
        emoji = "💎",
        title = "2 tháng liên tiếp",
        description = "Rất rất bền bỉ!"
    ),
    DAY_100(
        days = 100,
        emoji = "🔥🔥",
        title = "100 ngày liên tiếp",
        description = "Bạn thật sự khác biệt!"
    ),
    DAY_365(
        days = 365,
        emoji = "👑",
        title = "1 năm liên tiếp",
        description = "Đỉnh cao kiên trì!"
    );
}