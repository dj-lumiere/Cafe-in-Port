package com.ssafy.cafe_in_port_client.features.login.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.data.model.dto.User
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.databinding.FragmentResetPwBinding
import com.ssafy.cafe_in_port_client.features.login.viewmodel.LoginFragmentViewModel
import kotlinx.coroutines.launch

private const val TAG = "ResetPwFragment"

class ResetPwFragment : BaseFragment<FragmentResetPwBinding>(
    FragmentResetPwBinding::bind, R.layout.fragment_reset_pw
) {
    private val viewModel: LoginFragmentViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editTextID.setText(viewModel.userId.value)
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        Log.e(TAG, "onViewCreated: ${viewModel.userId.value}")
        if (!viewModel.userId.value.isNullOrBlank()) {
            binding.editTextID.setText(viewModel.userId.value)
        }

        binding.btnResetPassword.setOnClickListener {
            val _id = binding.editTextID.text
            val _email = binding.editTextJoinEmail.text
            val t1 = binding.editTextNewPassword.text.toString().trim()
            val t2 = binding.editTextConfirmPassword.text.toString().trim()
            Log.e(TAG, "onViewCreated: $t1")
            Log.e(TAG, "onViewCreated: $t2")
            Log.e(TAG, "onViewCreated: ${t1 == t2}")
            if (t1.isBlank()) {
                Toast.makeText(requireContext(), "바꿀 비밀번호는 비어있을 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (t2.isBlank()) {
                Toast.makeText(requireContext(), "비밀번호 변경 확인란은 비어있을 수 없습니다.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (t1 != t2) {
                Toast.makeText(requireContext(), "비밀번호와 비밀번호 확인이 맞지 않습니다.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                runCatching {
                    val user2 = User().apply {
                        email = _email.toString()
                        id = _id.toString()
                    }
                    Log.e(TAG, "onViewCreated: $user2")
                    val user3 = RetrofitUtil.userService.checkValidUser(user2)
                    Log.e(TAG, "onViewCreated123123: $user3")
                    if (user3 == 0) {
                        Toast.makeText(requireContext(), "계정 확인에 실패했습니다.", Toast.LENGTH_SHORT)
                            .show()
                        return@launch
                    }
                    val user = User().apply {
                        userNo = user3
                        pass = t1
                    }
                    RetrofitUtil.userService.changePassword(user)
                    Toast.makeText(requireContext(), "비밀번호 변경에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }.onFailure {
                    Toast.makeText(requireContext(), "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}