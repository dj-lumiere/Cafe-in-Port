package com.ssafy.cafe_in_port_client.features.login.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.common.enums.LoginAction
import com.ssafy.cafe_in_port_client.databinding.FragmentLoginBinding
import com.ssafy.cafe_in_port_client.features.main.ui.LoginActivity
import com.ssafy.cafe_in_port_client.features.login.viewmodel.LoginFragmentViewModel

private const val TAG = "LoginFragment"
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind, R.layout.fragment_login
) {
    private lateinit var loginActivity: LoginActivity
    private val viewModel: LoginFragmentViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그인 구현
        registerObserver()
        binding.btnLogin.setOnClickListener {
            val id = binding.editTextLoginID.text.toString()
            val pass = binding.editTextLoginPW.text.toString()
            viewModel.login(id, pass)
        }

        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.textFindId.setOnClickListener {
            loginActivity.openFragment(LoginAction.FIND_ID_FRAGMENT)
        }

        binding.textResetPw.setOnClickListener {
            loginActivity.openFragment(LoginAction.RESET_PW_FRAGMENT)
        }
    }

    private fun registerObserver() {
        viewModel.user.observe(viewLifecycleOwner) {
            if (it.id.isEmpty()) {
                // not logged in.
                showToast("로그인에 실패하였습니다. id 혹은 password를 확인해주세요.")
            } else {
                //user 정보를 sharedPreferences에 먼저 쓰고 시작하기
                ApplicationClass.sharedPreferencesUtil.addUser(it)
                Log.e(TAG, "registerObserver: $it")
                showToast("로그인되었습니다.")
                loginActivity.openFragment(LoginAction.MAIN_ACTIVITY)
            }
        }
    }

}