package com.ssafy.cafe_in_port_client.features.mypage.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.databinding.FragmentMypageBinding
import com.ssafy.cafe_in_port_client.common.enums.MainAction
import com.ssafy.cafe_in_port_client.features.main.ui.MainActivity
import com.ssafy.cafe_in_port_client.common.util.CommonUtils
import com.ssafy.cafe_in_port_client.features.login.viewmodel.LoginFragmentViewModel
import com.ssafy.cafe_in_port_client.features.recent.adapter.OrderListAdapter
import com.ssafy.cafe_in_port_client.ui.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MyPageFragment"

class MyPageFragment : BaseFragment<FragmentMypageBinding>(
    FragmentMypageBinding::bind, R.layout.fragment_mypage
) {
    private var orderAdapter: OrderListAdapter = OrderListAdapter(emptyList())
    private lateinit var mainActivity: MainActivity

    private val loginFragmentViewModel: LoginFragmentViewModel by activityViewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserData()
        initAdapter()
        initEvent()
    }

    private fun initAdapter() {
        orderAdapter.setItemClickListener(object : OrderListAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, orderid: Int) {
                mainActivityViewModel.setOrderId(orderid)
                mainActivity.openFragment(MainAction.ORDER_DETAIL_FRAGMENT)
            }
        })

    }

    private fun initEvent() {
        binding.logout.setOnClickListener {
            promptLogout()
        }

        binding.logoutOption.setOnClickListener {
            promptLogout()
        }

        binding.levelsInfo.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_grade_info, null)

            val alertDialog = AlertDialog.Builder(requireActivity())
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.btnClose)?.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
    }

    private fun promptLogout() {
        AlertDialog.Builder(requireActivity())
            .setTitle("로그아웃")
            .setMessage("로그아웃하시겠습니까?")
            .setCancelable(false)
            .setPositiveButton("YES") { dialog, _ ->
                mainActivity.openFragment(MainAction.LOGOUT)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUserData() {
        val userID = sharedPreferencesUtil.getUser().id
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                val userInfo = RetrofitUtil.userService.getUserInfo(userID)
                Log.e(TAG, "getUserData111: $userInfo")
                loginFragmentViewModel.setUserId(userInfo.user.id)
                Log.e(TAG, "getUserData111: ${loginFragmentViewModel.userId.value}")
                binding.apply {
                    userJoinDate.text = getString(
                        R.string.join_date,
                        userInfo.user.registerTime?.let { CommonUtils.dateformatYMD(it) }
                    )
                    userLevelDescription.text =
                        when (userInfo.grade.title) {
                            "여행자" -> getString(
                                R.string.tier_perks,
                                getString(R.string.traveler_perks)
                            )

                            "항해사" -> getString(
                                R.string.tier_perks,
                                getString(R.string.navigator_perks)
                            )

                            "등대지기" -> getString(
                                R.string.tier_perks,
                                getString(R.string.lighthouse_keeper_perks)
                            )

                            "함장" -> getString(
                                R.string.tier_perks,
                                getString(R.string.captain_perks)
                            )

                            "포트 마스터" -> getString(
                                R.string.tier_perks,
                                getString(R.string.port_master_perks)
                            )

                            else -> ""
                        }
                    userLevelIcon.setImageResource(
                        when (userInfo.grade.title) {
                            "여행자" -> R.drawable.tier_traveler
                            "항해사" -> R.drawable.tier_navigator
                            "등대지기" -> R.drawable.tier_lighthouse_keeper
                            "함장" -> R.drawable.tier_captain
                            "포트마스터" -> R.drawable.cafe_logo
                            else -> R.drawable.ic_broken_image
                        }
                    )
                    userLevelText.text =
                        if (userInfo.grade.title == "포트마스터") {
                            getString(
                                R.string.tier_text,
                                userInfo.grade.title,
                                ""
                            )
                        } else {
                            getString(
                                R.string.tier_text,
                                userInfo.grade.title,
                                CommonUtils.tierNumberToRomanLetter(6 - userInfo.grade.step)
                            )
                        }
                    nextLevelInfo.text =
                        if (userInfo.grade.title == "포트마스터") {
                            getString(
                                R.string.max_level
                            )
                        } else {
                            getString(R.string.leftover, userInfo.grade.to)
                        }
                    userName.text = getString(R.string.name, userInfo.user.username)
                    userLevelProgress.progress =
                        if (userInfo.grade.title == "포트마스터") {
                            userLevelProgress.max
                        } else {
                            userLevelProgress.min + (userLevelProgress.max - userLevelProgress.min) / userInfo.grade.stepMax * (userInfo.grade.stepMax - userInfo.grade.to)
                        }

                    orderAdapter.list = userInfo.order.map { order ->
                        withContext(Dispatchers.Main) {
                            var result = RetrofitUtil.orderService.getOrderDetail(order.id)
                            result = CommonUtils.calcTotalPrice(result)
                            Log.d(TAG, "getUserData: $result")
                            result
                        }
                    }
                    if (userInfo.user.id.startsWith("google-")) {
                        rePw.visibility = View.GONE
                    }
                    rePw.setOnClickListener {
                        mainActivity.openFragment(MainAction.RESET_PW_FRAGMENT)
                    }
                }
            }
        }
    }
}