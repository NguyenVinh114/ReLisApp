    package com.example.relisapp.nam.redirect

    import android.content.Intent
    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import com.example.relisapp.MainActivity
    import com.example.relisapp.nam.ui.screens.admin.AdminDashboardActivity2
    import com.example.relisapp.nam.data.local.SessionManager
    import com.example.relisapp.nam.ui.screens.home.StartActivity

    class NotificationRedirectActivity : ComponentActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val session = SessionManager(this)

            val nextIntent = if (session.isLoggedIn()) {
                when (session.getRole()) {
                    "admin" -> Intent(this, AdminDashboardActivity2::class.java)
                    "user" -> Intent(this, MainActivity::class.java)
                    else -> Intent(this, StartActivity::class.java)
                }
            } else {
                Intent(this, StartActivity::class.java)
            }

            nextIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(nextIntent)
            finish()
        }
    }
