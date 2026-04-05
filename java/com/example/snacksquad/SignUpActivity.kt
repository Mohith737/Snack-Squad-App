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
import com.example.snacksquad.databinding.ActivitySignUpBinding
import com.google.android.material.textfield.TextInputLayout

class SignUpActivity : AppCompatActivity() {
    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private lateinit var usernameInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        usernameInputLayout = wrapWithTextInputLayout(
            editText = binding.eTName,
            inputLayoutId = R.id.signUpUsernameInputLayout
        )
        passwordInputLayout = wrapWithTextInputLayout(
            editText = binding.eTMail,
            inputLayoutId = R.id.signUpPasswordInputLayout
        )
        confirmPasswordInputLayout = wrapWithTextInputLayout(
            editText = binding.eTPass,
            inputLayoutId = R.id.signUpConfirmPasswordInputLayout
        )
        db = DBHelper(this)

        setupValidation()

        binding.tValreadyAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            val username = binding.eTName.text.toString().trim()
            val password = binding.eTMail.text.toString()

            if (!validateForm(showErrors = true)) {
                return@setOnClickListener
            }

            val savedata = db.insertdata(username, password)
            if (savedata) {
                clearInputErrors()
                Toast.makeText(
                    this,
                    getString(R.string.account_created_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                usernameInputLayout.error = getString(R.string.error_user_exists)
            }
        }
    }

    override fun onDestroy() {
        db.closeDatabase()
        super.onDestroy()
    }

    private fun setupValidation() {
        binding.eTName.doAfterTextChanged {
            validateForm(showErrors = true)
        }
        binding.eTMail.doAfterTextChanged {
            validateForm(showErrors = true)
        }
        binding.eTPass.doAfterTextChanged {
            validateForm(showErrors = true)
        }
        validateForm(showErrors = false)
    }

    private fun validateForm(showErrors: Boolean): Boolean {
        val username = binding.eTName.text.toString().trim()
        val password = binding.eTMail.text.toString()
        val confirmPassword = binding.eTPass.text.toString()

        val usernameError = getUsernameError(username)
        val passwordError = getPasswordError(password)
        val confirmPasswordError = getConfirmPasswordError(password, confirmPassword)

        usernameInputLayout.error = if (showErrors) usernameError else null
        passwordInputLayout.error = if (showErrors) passwordError else null
        confirmPasswordInputLayout.error = if (showErrors) confirmPasswordError else null

        val isValid = usernameError == null && passwordError == null && confirmPasswordError == null
        binding.btnSignUp.isEnabled = isValid
        return isValid
    }

    private fun clearInputErrors() {
        usernameInputLayout.error = null
        passwordInputLayout.error = null
        confirmPasswordInputLayout.error = null
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

    private fun getConfirmPasswordError(password: String, confirmPassword: String): String? {
        if (confirmPassword.isBlank()) {
            return getString(R.string.error_confirm_password_required)
        }
        if (password != confirmPassword) {
            return getString(R.string.error_passwords_do_not_match)
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
