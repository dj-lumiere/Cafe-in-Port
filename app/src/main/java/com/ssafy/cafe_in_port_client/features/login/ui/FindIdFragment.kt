package com.ssafy.cafe_in_port_client.features.login.ui

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.databinding.FragmentFindIdBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FindIdFragment : BaseFragment<FragmentFindIdBinding>(
    FragmentFindIdBinding::bind, R.layout.fragment_find_id
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnFind.setOnClickListener {
            val email = binding.editTextJoinEmail.text?.toString()?.trim()

            // 이메일 입력값 검증
            if (email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 아이디 찾기 함수 호출
            findId(email)
        }
    }

    private fun findId(email: String) {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                // 이메일로 아이디 조회
                RetrofitUtil.userService.findIdFromEmail(email)
            }.onSuccess { userId ->
                if (userId.isBlank()) {
                    // 실패: 가입된 아이디 없음
                    AlertDialog.Builder(requireActivity())
                        .setTitle("아이디 조회 실패")
                        .setMessage("해당하는 이메일로 가입된 아이디가 없습니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                        .show()
                } else {
                    // 성공: 아이디 조회 성공
                    AlertDialog.Builder(requireActivity())
                        .setTitle("아이디 조회 성공")
                        .setMessage("아이디는 ${userId}입니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                        .setNeutralButton("클립보드로 복사") { dialog, _ -> copyToClipboard(userId) }
                        .show()
                }
            }.onFailure {
                // 서버 요청 실패 처리
                Toast.makeText(
                    requireContext(),
                    "아이디 조회에 실패했습니다. 다시 시도해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun copyToClipboard(id: String) {
        // Get the Clipboard Manager
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Create ClipData with the text
        val clip = ClipData.newPlainText("label", id)

        // Set the ClipData to the clipboard
        clipboard.setPrimaryClip(clip)

        // Provide feedback to the user
        Toast.makeText(requireContext(), "클립보드에 복사했습니다.", Toast.LENGTH_SHORT).show()
    }
}