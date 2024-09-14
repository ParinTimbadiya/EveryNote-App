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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authService = AuthService()


        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val txtConfirmPassword = findViewById<EditText>(R.id.txtConfirmPassword)


        findViewById<TextView>(R.id.LoginLink).setOnClickListener {
            intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()
            val confirmpassword = txtConfirmPassword.text.toString()
            if (confirmpassword == password)
            {
                val user = User(email = email, password = password)
                CoroutineScope(Dispatchers.IO).launch {
                    val response = authService.register(user)
                    if (response.code == 201) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "$email Register Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Something Went Wrong, Please Try Again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            else
            {
                Toast.makeText(this,"Password and Confirm Password are not same",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }
}