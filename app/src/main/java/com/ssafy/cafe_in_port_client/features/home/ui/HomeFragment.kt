package com.ssafy.cafe_in_port_client.ui.home

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.base.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.databinding.FragmentHomeBinding
import com.ssafy.cafe_in_port_client.common.enums.MainAction
import com.ssafy.cafe_in_port_client.features.main.ui.MainActivity
import com.ssafy.cafe_in_port_client.features.recent.ui.RecentOrderFragment
import com.ssafy.cafe_in_port_client.features.order.ui.MapFragment
import com.ssafy.cafe_in_port_client.common.util.CommonUtils
import com.ssafy.cafe_in_port_client.data.model.dto.User
import com.ssafy.cafe_in_port_client.databinding.DialogPlaylistRequestBinding
import com.ssafy.cafe_in_port_client.features.home.adapter.RecommendItemListAdapter
import com.ssafy.cafe_in_port_client.features.home.adapter.SliderAdapter
import com.ssafy.cafe_in_port_client.features.notification.ui.NotificationFragment
import com.ssafy.cafe_in_port_client.ui.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind, R.layout.fragment_home
) {

    private var recommendItemListAdapter: RecommendItemListAdapter =
        RecommendItemListAdapter(emptyList())
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initUserData()
        setupSlider()

        binding.noti.setOnClickListener {
            navigateToNotificationFragment()
        }

        binding.findStoreBtn.setOnClickListener {
            navigateToMapFragment()
        }

        binding.orderHistoryBtn.setOnClickListener {
            navigateToRecentFragment()
        }

        binding.requestSongBtn.setOnClickListener {
            showPlaylistRequestDialog()
        }
    }

    private fun setupSlider() {
        val sliderImages = listOf(
            R.drawable.event_image1,
            R.drawable.event_image2
        )

        val sliderAdapter = SliderAdapter(sliderImages)
        binding.viewPager.adapter = sliderAdapter

        // TabLayout과 ViewPager2를 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
    }

    private fun initUserData() {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                val userID = sharedPreferencesUtil.getUser().id
                val userNo = sharedPreferencesUtil.getUser().userNo
                Log.e(TAG, "initUserData: $userID")
                Log.e(TAG, "initUserData: $userNo")
                // 구글 로그인 할 때 쿠키 값이 설정이 달라져서 그냥 재로그인 시킴. 마감이 너무 급해서 남긴 결정이었음.
                if (userID.startsWith("google-")) {
                    RetrofitUtil.userService.login(
                        User().apply {
                            id = userID
                            pass = "XXXX"
                        }
                    )
                }
                val userInfo = RetrofitUtil.userService.getUserInfo(userID)
                val orderFrequency = RetrofitUtil.orderService.getLastMonthOrderItem(userNo)
                val recommendedItem = CommonUtils.pickWeightedRandomKeys(orderFrequency, 5)
                Log.e(TAG, "getUserData1: $userInfo")
                Log.e(TAG, "initUserData2: $orderFrequency")
                Log.e(TAG, "initUserData3: $recommendedItem")
                binding.apply {
                    userNameText.text = getString(R.string.name, userInfo.user.username)
                    recommendationRecyclerView.visibility = View.VISIBLE
                    recommendItemListAdapter.list = recommendedItem.map { item ->
                        Log.e(TAG, "initUserData4: $item")
                        val result = RetrofitUtil.productService.selectProductById(item)
                        result
                    }
                    Log.e(TAG, "5: ${recommendItemListAdapter.list}")
                    recommendItemListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun initAdapter() {
        recommendItemListAdapter.setItemClickListener(object :
            RecommendItemListAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, productId: Int) {
                Log.e(TAG, "onClick: yes, $productId")
                activityViewModel.setProductId(productId)
                mainActivity.openFragment(MainAction.MENU_DETAIL_FRAGMENT)
            }
        })

        binding.recommendationRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendItemListAdapter
        }
    }

    private fun navigateToNotificationFragment() {
        val notificationFragment = NotificationFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, notificationFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToMapFragment() {
        val mapFragment = MapFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, mapFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToRecentFragment() {
        val recentOrderFragment = RecentOrderFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, recentOrderFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showPlaylistRequestDialog() {
        val dialogBinding = DialogPlaylistRequestBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.btnSubmit.setOnClickListener {
            val songName = dialogBinding.etSongName.text.toString().trim()
            val artistName = dialogBinding.etArtistName.text.toString().trim()
            val reason = dialogBinding.etReason.text.toString().trim()

            if (songName.isEmpty() || artistName.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "곡 이름과 가수 이름은 필수 입력 사항입니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireContext(), "신청곡이 등록되었습니다!", Toast.LENGTH_SHORT).show()

                dialogBinding.etSongName.text?.clear()
                dialogBinding.etArtistName.text?.clear()
                dialogBinding.etReason.text?.clear()

                dialog.dismiss()
            }
        }

        dialog.show()
    }
}