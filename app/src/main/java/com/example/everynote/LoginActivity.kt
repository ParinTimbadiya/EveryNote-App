package com.example.everynote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.everynote.models.User
import com.example.everynote.services.AuthService
import com.example.everynote.utils.Utilities
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (!Utilities.checkForInternet(this))
        {
            Toast.makeText(this, "Please Connect To Internet!!", Toast.LENGTH_LONG).show()
            return
        }

        val sharedPrefs = getSharedPreferences("myapp_prefs", MODE_PRIVATE)
        authService = AuthService()

        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)

        findViewById<TextView>(R.id.registerLink).setOnClickListener {
            intent = Intent(this@LoginActivity,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {

            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()
            var user = User(email = email, password = password)

            CoroutineScope(Dispatchers.IO).launch {
                val response = authService.login(user)
                if(response.code != 200)
                {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity,"UserName or Password Are Wrong",Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    user = Gson().fromJson(response.message, User::class.java)

                    val spEditor = sharedPrefs.edit()
                    spEditor.putString("UserId",user.id.toString())
                    spEditor.apply()

                    withContext(Dispatchers.Main) {
                        intent = Intent(this@LoginActivity,SplashActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}