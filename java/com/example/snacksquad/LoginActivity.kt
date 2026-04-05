package com.example.snacksquad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import com.example.snacksquad.R
import com.example.snacksquad.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var usernameInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var dbh: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        usernameInputLayout = wrapWithTextInputLayout(
            editText = binding.editTextTextEmailAddress,
            inputLayoutId = R.id.loginUsernameInputLayout
        )
        passwordInputLayout = wrapWithTextInputLayout(
            editText = binding.editTextTextPassword,
            inputLayoutId = R.id.loginPasswordInputLayout
        )
        dbh = DBHelper(this)

        setupValidation()

        binding.tVnotAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.bTLogin.setOnClickListener {
            val usertxt = binding.editTextTextEmailAddress.text.toString().trim()
            val passtxt = binding.editTextTextPassword.text.toString()

            if (!validateForm(showErrors = true)) {
                return@setOnClickListener
            }

            if (dbh.needsPasswordReset(usertxt)) {
                usernameInputLayout.error = getString(R.string.error_password_reset_required)
                return@setOnClickListener
            }

            val checkuser = dbh.checkuserpass(usertxt, passtxt)
            if (checkuser) {
                clearInputErrors()
                Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                passwordInputLayout.error = getString(R.string.error_invalid_credentials)
                Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        dbh.closeDatabase()
        super.onDestroy()
    }

    private fun setupValidation() {
        binding.editTextTextEmailAddress.doAfterTextChanged {
            validateForm(showErrors = true)
        }
        binding.editTextTextPassword.doAfterTextChanged {
            validateForm(showErrors = true)
        }
        validateForm(showErrors = false)
    }

    private fun validateForm(showErrors: Boolean): Boolean {
        val username = binding.editTextTextEmailAddress.text.toString().trim()
        val password = binding.editTextTextPassword.text.toString()

        val usernameError = getUsernameError(username)
        val passwordError = getPasswordError(password)

        usernameInputLayout.error = if (showErrors) usernameError else null
        passwordInputLayout.error = if (showErrors) passwordError else null

        val isValid = usernameError == null && passwordError == null
        binding.bTLogin.isEnabled = isValid
        return isValid
    }

    private fun clearInputErrors() {
        usernameInputLayout.error = null
        passwordInputLayout.error = null
    }

    private fun getUsernameError(username: String): String? {
        if (username.isBlank()) {
            return getString(R.string.error_username_required)
        }
        if (username.length !in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH) {
            return getString(R.string.error_username_length)
        }
        if (!USERNAME_REGEX.matches(username)) {
            return getString(R.string.error_username_alphanumeric)
        }
        return null
    }

    private fun getPasswordError(password: String): String? {
        if (password.isBlank()) {
            return getString(R.string.error_password_required)
        }
        if (password.length !in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH) {
            return getString(R.string.error_password_length)
        }
        return null
    }

    private fun wrapWithTextInputLayout(editText: EditText, inputLayoutId: Int): TextInputLayout {
        val parent = editText.parent as ViewGroup
        val childIndex = parent.indexOfChild(editText)
        val originalLayoutParams = editText.layoutParams
        val inputLayoutParams = ConstraintLayout.LayoutParams(originalLayoutParams).apply {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }

        parent.removeView(editText)
        editText.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val textInputLayout = TextInputLayout(this).apply {
            id = inputLayoutId
            layoutParams = inputLayoutParams
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_NONE
            isHintEnabled = false
        }
        textInputLayout.addView(editText)
        parent.addView(textInputLayout, childIndex)
        return textInputLayout
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val MAX_USERNAME_LENGTH = 30
        private const val MIN_PASSWORD_LENGTH = 6
        private const val MAX_PASSWORD_LENGTH = 50
        private val USERNAME_REGEX = Regex("^[A-Za-z0-9]+$")
    }
}
