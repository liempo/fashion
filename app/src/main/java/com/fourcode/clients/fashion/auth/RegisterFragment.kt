package com.fourcode.clients.fashion.auth


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fourcode.clients.fashion.AuthActivity
import com.fourcode.clients.fashion.MainActivity
import kotlinx.android.synthetic.main.fragment_register.*


import com.fourcode.clients.fashion.R
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class RegisterFragment : Fragment() {

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
            R.layout.fragment_register,
            container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_button.setOnClickListener {
            activity?.supportFragmentManager?.
                beginTransaction()?.
                replace(R.id.container, LoginFragment.newInstance())?.
                commit()
        }

        register_button.setOnClickListener {

            val email = email_input.text.toString()
            val password = password_input.text.toString()
            val confirm = confirm_input.text.toString()

            // Check if fields are valid
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

            // Check if password is valid
            confirm_text_input_layout.apply {
                if (confirm.isEmpty()) {
                    error = getString(R.string.error_required)
                    isErrorEnabled = true
                }
            }

            // Firebase stuff (create account)
            if (email_text_input_layout.isErrorEnabled.not() &&
                password_text_input_layout.isErrorEnabled.not() &&
                confirm_text_input_layout.isErrorEnabled.not())

                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        context?.startActivity<MainActivity>(
                            MainActivity.ARG_UID to it.user.uid
                        )
                    }
                    .addOnFailureListener {
                        context?.toast(R.string.error_auth_failed)?.show()
                    }
        }
    }

    companion object {
        @JvmStatic fun newInstance() = RegisterFragment()
    }
}
