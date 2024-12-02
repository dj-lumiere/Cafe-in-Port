package com.ssafy.cafe_in_port_client.features.login.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.common.enums.LoginAction
import com.ssafy.cafe_in_port_client.data.model.dto.User
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.databinding.FragmentLoginMainBinding
import com.ssafy.cafe_in_port_client.features.main.ui.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "LoginMainFragment"
class LoginMainFragment : BaseFragment<FragmentLoginMainBinding>(
    FragmentLoginMainBinding::bind, R.layout.fragment_login_main
) {
    private lateinit var loginActivity: LoginActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGoogleLogin.setOnClickListener {
            loginActivity.googleLoginManager.signIn()
        }

        binding.btnLogin.setOnClickListener {
            loginActivity.openFragment(LoginAction.LOGIN_FRAGMENT)
        }

        binding.textSignUpButton.setOnClickListener {
            loginActivity.openFragment(LoginAction.JOIN_FRAGMENT)
        }
    }

}