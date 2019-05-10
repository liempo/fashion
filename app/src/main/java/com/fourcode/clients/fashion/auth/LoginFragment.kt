package com.fourcode.clients.fashion.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fourcode.clients.fashion.AuthActivity
import com.fourcode.clients.fashion.MainActivity
import com.fourcode.clients.fashion.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = (activity as AuthActivity).auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_button.setOnClickListener {

            val email = email_input.text.toString().trim()
            val password = password_input.text.toString().trim()

            // Check if email is valid
            email_text_input_layout.apply {

                if (email.isEmpty()) {
                    error = getString(R.string.error_required)
                    isErrorEnabled = true
                } else if (email.contains("@").not()) {
                    error = getString(R.string.error_email_invalid)
                    isErrorEnabled = true
                }

            }

            // Check if password is valid
            password_text_input_layout.apply {
                if (password.isEmpty()) {
                    error = getString(R.string.error_required)
                    isErrorEnabled = true
                }
            }

            // Firebase
            if (email_text_input_layout.isErrorEnabled.not() && password_text_input_layout.isErrorEnabled.not())
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        context?.startActivity<MainActivity>()
                    }
                    .addOnFailureListener {
                        context?.toast(R.string.error_auth_failed)?.show()
                    }
        }
    }

    companion object {
        @JvmStatic fun newInstance() = LoginFragment()
    }
}
