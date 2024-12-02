package com.ssafy.cafe_in_port_client.features.main.ui

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.base.BaseActivity
import com.ssafy.cafe_in_port_client.databinding.ActivityLoginBinding
import com.ssafy.cafe_in_port_client.common.enums.LoginAction
import com.ssafy.cafe_in_port_client.common.util.PermissionChecker
import com.ssafy.cafe_in_port_client.common.util.PermissionChecker.Companion.runtimePermissions
import com.ssafy.cafe_in_port_client.features.login.ui.CheckUserFragment
import com.ssafy.cafe_in_port_client.features.login.ui.FindIdFragment
import com.ssafy.cafe_in_port_client.features.login.ui.JoinFragment
import com.ssafy.cafe_in_port_client.features.login.ui.LoginFragment
import com.ssafy.cafe_in_port_client.features.login.ui.LoginMainFragment
import com.ssafy.cafe_in_port_client.features.login.ui.ResetPwFragment
import com.ssafy.cafe_in_port_client.features.main.manager.GoogleLoginManager

private const val TAG = "LoginActivity"
class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    lateinit var googleLoginManager: GoogleLoginManager
    private val checker = PermissionChecker(this, this)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleLoginManager = GoogleLoginManager(this, this)
        googleLoginManager.configureGoogleSignIn()
        if (!checker.checkPermission(runtimePermissions)) {
            checker.setOnGrantedListener {
                // 퍼미션 획득 성공일 때
                handleLogin()
            }
            checker.requestPermissionLauncher.launch(runtimePermissions)
        } else {
            handleLogin()
        }

    }

    fun handleLogin() {
        //로그인 된 상태인지 확인
        val user = ApplicationClass.sharedPreferencesUtil.getUser()
        Log.e(TAG, "handleLogin: $user")

        //로그인 상태 확인. id가 있다면 로그인 된 상태
        if (user.id != "") {
            openFragment(LoginAction.MAIN_ACTIVITY)
        } else {
            // 가장 첫 화면은 홈 화면의 Fragment로 지정
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_login, LoginMainFragment()).commit()
        }
    }

    fun openFragment(action: LoginAction) {
        val transaction = supportFragmentManager.beginTransaction()
        val replaceTarget = R.id.frame_layout_login
        when (action) {
            // MainActivity로 이동
            LoginAction.MAIN_ACTIVITY -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            // 로그인 안내 페이지로 이동
            LoginAction.LOGIN_MAIN_FRAGMENT -> {
                supportFragmentManager.popBackStack()
                transaction
                    .replace(replaceTarget, LoginMainFragment())
                    .addToBackStack(null)
            }

            // 회원가입 페이지로 이동
            LoginAction.JOIN_FRAGMENT -> {
                transaction
                    .replace(replaceTarget, JoinFragment())
                    .addToBackStack(null)
            }

            // 로그인 페이지로 이동
            LoginAction.LOGIN_FRAGMENT -> {
                // 회원가입한 뒤 돌아오면, 2번에서 addToBackStack해 놓은게 남아 있어서,
                // stack을 날려 줘야 한다. stack날리기.
                supportFragmentManager.popBackStack()
                transaction
                    .replace(replaceTarget, LoginFragment())
                    .addToBackStack(null)
            }

            // 비밀번호 변경 전 본인확인
            LoginAction.CHECK_USER_FRAGMENT -> {
                transaction
                    .replace(replaceTarget, CheckUserFragment())
                    .addToBackStack(null)
            }

            // 아이디 찾기
            LoginAction.FIND_ID_FRAGMENT -> {
                transaction
                    .replace(replaceTarget, FindIdFragment())
                    .addToBackStack(null)
            }

            // 비밀번호 재설정
            LoginAction.RESET_PW_FRAGMENT -> {
                transaction
                    .replace(replaceTarget, ResetPwFragment())
                    .addToBackStack(null)
            }
        }
        transaction.commit()
    }

    // Handle the result from Google Sign-In intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleLoginManager.handleSignInResult(data)
    }
}