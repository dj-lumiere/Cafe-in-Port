package com.ssafy.cafe_in_port_client.features.login.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.common.enums.LoginAction
import com.ssafy.cafe_in_port_client.data.model.dto.User
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.databinding.FragmentJoinBinding
import com.ssafy.cafe_in_port_client.features.main.ui.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "JoinFragment"

class JoinFragment : BaseFragment<FragmentJoinBinding>(
    FragmentJoinBinding::bind, R.layout.fragment_join
) {
    private lateinit var loginActivity: LoginActivity
    private var checkedId = false
    private var isUnique = false
    private var checkedEmail = false
    private var isUniqueEmail = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        //id 중복 확인 버튼
        binding.btnConfirm.setOnClickListener {
            val id = binding.editTextJoinID.text.toString()
            CoroutineScope(Dispatchers.Main).launch {
                runCatching {
                    RetrofitUtil.userService.isUsedId(id)
                }.onSuccess {
                    if (it) {
                        Log.w(TAG, "onViewCreated: duplicate id detected.")
                        binding.apply {
                            textInputLayoutJoinID.error = "중복된 아이디입니다."
                        }
                        checkedId = false
                        isUnique = false
                        showToast("중복된 ID입니다.")
                    } else {
                        Log.i(TAG, "onViewCreated: usable id.")
                        binding.apply {
                            textInputLayoutJoinID.error = null
                        }
                        checkedId = true
                        isUnique = true
                        showToast("사용 가능한 ID입니다.")
                    }
                }.onFailure {
                    Log.e(TAG, "onViewCreated: id checking failed.")
                }
            }
        }

        //id 중복 확인 버튼
        binding.btnEmailConfirm.setOnClickListener {
            val email = binding.editTextJoinEmail.text.toString()
            CoroutineScope(Dispatchers.Main).launch {
                runCatching {
                    RetrofitUtil.userService.isUsedId(email)
                }.onSuccess {
                    if (it) {
                        Log.w(TAG, "onViewCreated: duplicate email detected.")
                        binding.apply {
                            textInputLayoutJoinID.error = "중복된 이메일입니다."
                        }
                        checkedEmail = false
                        isUniqueEmail = false
                        showToast("중복된 Email입니다.")
                    } else {
                        Log.i(TAG, "onViewCreated: usable email.")
                        binding.apply {
                            textInputLayoutJoinID.error = null
                        }
                        checkedEmail = true
                        isUniqueEmail = true
                        showToast("사용 가능한 Email입니다.")
                    }
                }.onFailure {
                    Log.e(TAG, "onViewCreated: email checking failed.")
                }
            }
        }


        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            val _id = binding.editTextJoinID.text.toString()
            val _pw = binding.editTextJoinPW.text.toString()
            val _name = binding.editTextJoinName.text.toString()
            val _email = binding.editTextJoinEmail.text.toString()

            if (_id.isBlank()) {
                showToast("ID가 비어있습니다.")
                return@setOnClickListener
            }
            if (!checkedId) {
                showToast("ID 중복확인을 해주세요.")
                return@setOnClickListener
            }
            if (!isUnique) {
                showToast("중복된 ID는 사용하실 수 없습니다.")
                return@setOnClickListener
            }
            if (_pw.isBlank()) {
                showToast("비밀번호가 비어있습니다.")
                return@setOnClickListener
            }
            if (_name.isBlank()) {
                showToast("이름이 비어있습니다.")
                return@setOnClickListener
            }
            if (_email.isBlank()) {
                showToast("이메일이 비어있습니다.")
            }
            CoroutineScope(Dispatchers.Main).launch {
                runCatching {
                    RetrofitUtil.userService.insert(User().apply {
                        id = _id
                        username = _name
                        email = _email
                        pass = _pw
                    })
                }.onSuccess {
                    if (it) {
                        Log.i(TAG, "onViewCreated: join user success")
                        showToast("회원가입에 성공하였습니다. 다시 로그인 해주세요.")
                        loginActivity.openFragment(LoginAction.LOGIN_FRAGMENT)
                    } else {
                        Log.e(TAG, "onViewCreated: join user failed")
                        showToast("회원가입에 실패했습니다.")
                    }
                }.onFailure {
                    Log.e(TAG, "onViewCreated: join user failed")
                    showToast("회원가입에 실패했습니다.")
                }
            }
        }
    }
}