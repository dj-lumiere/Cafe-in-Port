package com.ssafy.cafe_in_port_client.features.main.manager

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.data.model.dto.User
import com.ssafy.cafe_in_port_client.features.main.ui.MainActivity
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.features.main.ui.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoogleLoginManager(
    val context: Context,
    val activity: LoginActivity,
) {

    private val TAG = "GoogleLoginManager"

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }

    fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleSignInResult(data: Intent?) {
        Log.e(TAG, "handleSignInResult: yes")
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.e(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed", e)
            Toast.makeText(activity, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.e(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.e(TAG, "firebaseAuthWithGoogle: ${user}")
                    if (user != null) {
                        Log.e(TAG, "firebaseAuthWithGoogle1: ${user.uid}")
                        Log.e(TAG, "firebaseAuthWithGoogle2: ${user.email}")
                    }
                    user?.let {
                        Log.e(TAG, "firebaseAuthWithGoogle2: ${user.uid}")
                        Log.e(TAG, "firebaseAuthWithGoogle3: ${user.email}")
                        Log.e(TAG, "firebaseAuthWithGoogle4: ${user.displayName}")
                        // Save user information if needed
                        // For example:
                        ApplicationClass.sharedPreferencesUtil.addUser(
                            User().apply {
                                id = "google-${it.uid}"
                                email = it.email ?: ""
                                username = it.displayName ?: ""
                                pass = "XXXX"
                            }
                        )
                        Log.e(
                            TAG,
                            "firebaseAuthWithGoogle: ${ApplicationClass.sharedPreferencesUtil.getUser()}",
                        )
                        CoroutineScope(Dispatchers.Main).launch {
                            uploadFcmToken()
                            navigateToMainActivity()
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(activity, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private suspend fun uploadFcmToken() {
        // Retrieve FCM token and upload to server
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d(TAG, "FCM Token: $token")

            val user = ApplicationClass.sharedPreferencesUtil.getUser()
            if (user.id.isEmpty()) {
                Log.e(TAG, "uploadFcmToken: User ID is empty.")
                return@addOnCompleteListener
            }
            // Upload token to server
            token?.let {
                val apiService = RetrofitUtil.apiService
                val userService = RetrofitUtil.userService
                CoroutineScope(Dispatchers.Main).launch {
                    Log.e(TAG, "uploadFcmToken: yes")
                    Log.e(TAG, "uploadToken00: $user")
                    Log.e(TAG, "uploadFcmToken0: ${user.id}")
                    var _userNo: Int
                    var isEmailDuplicate = userService.isEmailDuplicate(user.email)
                    Log.e(TAG, "uploadFcmToken1: $isEmailDuplicate")
                    if (!isEmailDuplicate) {
                        Log.e(TAG, "uploadFcmToken2: Email is available. Inserting user.")
                        val insertSuccess = userService.insert(user)
                        Log.e(TAG, "uploadFcmToken: $insertSuccess")
                        if (insertSuccess) {
                            Log.e(TAG, "uploadFcmToken3: User inserted successfully.")
//                            withContext(Dispatchers.Main) {
//                                Toast.makeText(
//                                    activity,
//                                    context.getString(
//                                        R.string.google_register_complete,
//                                        user.username
//                                    ),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
                        } else {
                            Log.e(TAG, "uploadFcmToken4: Failed to insert user.")
//                            withContext(Dispatchers.Main) {
//                                Toast.makeText(
//                                    activity,
//                                    context.getString(R.string.google_login_failed),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
                            return@launch
                        }
                    } else {
                        Log.e(TAG, "uploadFcmToken5: Email already exists. Proceeding to login.")
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(
//                                activity,
//                                context.getString(R.string.google_login_complete, user.username),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    }
                    val loginResult = userService.login(User().apply {
                        id = user.id
                        pass = "XXXX"
                    })
                    Log.e(TAG, "uploadFcmToken: $loginResult")
                    val targetUserResponse = userService.getUserInfo(loginResult.body()!!.id)
                    Log.e(TAG, "uploadToken6: $targetUserResponse")
                    _userNo = targetUserResponse.user.userNo
                    ApplicationClass.sharedPreferencesUtil.deleteUser()
                    ApplicationClass.sharedPreferencesUtil.addUser(user.apply {
                        userNo = _userNo
                    })
                    Log.e(
                        TAG,
                        "uploadFcmToken: ${ApplicationClass.sharedPreferencesUtil.getUser()}",
                    )
//                    Log.e(TAG, "uploadToken7: $_userNo")
//                    Log.e(TAG, "uploadToken8: $token")
//                    apiService.updateFcmToken(_userNo, token)
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(
            activity,
            LoginActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(activity) {
            // Update UI after sign-out if necessary
            Log.d(TAG, "User signed out")
            Toast.makeText(activity, "Signed out successfully.", Toast.LENGTH_SHORT).show()
        }
    }
}
