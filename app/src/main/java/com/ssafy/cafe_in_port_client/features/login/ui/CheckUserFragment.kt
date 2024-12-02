package com.ssafy.cafe_in_port_client.features.login.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.common.enums.LoginAction
import com.ssafy.cafe_in_port_client.data.model.dto.User
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.databinding.FragmentCheckUserBinding
import com.ssafy.cafe_in_port_client.features.main.ui.LoginActivity
import com.ssafy.cafe_in_port_client.features.login.viewmodel.LoginFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckUserFragment : BaseFragment<FragmentCheckUserBinding>(
    FragmentCheckUserBinding::bind,
    R.layout.fragment_check_user
) {
    private lateinit var loginActivity: LoginActivity
    private val viewModel: LoginFragmentViewModel by viewModels()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnCheckCredentials.setOnClickListener {
            val id = binding.editTextID.text?.toString()?.trim()
            val email = binding.editTextJoinEmail.text?.toString()?.trim()

            // 입력값 검증
            if (id.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 본인 확인 함수 호출
            findUserNo(id, email)
        }
    }

    fun findUserNo(_id: String, _email: String) {
        val user = User().apply {
            id = _id
            email = _email
        }
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                val userNo = RetrofitUtil.userService.checkValidUser(user)
                if (userNo != 0) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle("본인 확인 실패")
                        .setMessage("이메일과 아이디를 확인해주세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                        .show()
                } else {
                    Toast.makeText(requireContext(), "본인 확인에 성공했습니다.", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.setUserNo(userNo)
                    viewModel.setUserId(_id)
                    loginActivity.openFragment(LoginAction.RESET_PW_FRAGMENT)
                }
            }.onFailure {
                Toast.makeText(requireContext(), "본인 확인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}