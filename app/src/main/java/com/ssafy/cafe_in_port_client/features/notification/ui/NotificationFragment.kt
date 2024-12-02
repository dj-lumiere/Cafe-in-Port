package com.ssafy.cafe_in_port_client.features.notification.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.data.model.dto.NotificationItem
import com.ssafy.cafe_in_port_client.databinding.FragmentNotificationBinding
import com.ssafy.cafe_in_port_client.features.notification.adapter.NotificationAdapter
import com.ssafy.cafe_in_port_client.features.notification.viewmodel.NotificationViewModel

private const val TAG = "NotificationFragment"

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(
    FragmentNotificationBinding::bind,
    R.layout.fragment_notification
) {

    private val notificationViewModel: NotificationViewModel by activityViewModels()

    private lateinit var adapter: NotificationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()
        // RecyclerView 및 어댑터 설정
        setupRecyclerView()

        // 뒤로가기 버튼 클릭 이벤트
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun registerObserver() {
        notificationViewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter(
            notificationViewModel.notifications.value ?: mutableListOf()
        ) { position ->
            handleDeleteNotification(position)
        }

        binding.notificationRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NotificationFragment.adapter
        }
    }

    private fun handleDeleteNotification(position: Int) {
        if (position in (notificationViewModel.notifications.value?.indices
                ?: (0..0))
        ) { // 유효한 인덱스인지 확인
            val removedItem = notificationViewModel.getNotification(position) // 삭제된 아이템 정보 저장
            Log.e(TAG, "handleDeleteNotification: ${notificationViewModel.notifications.value}")
            notificationViewModel.removeNotification(position) // 데이터 리스트에서 삭제
            Log.e(TAG, "handleDeleteNotification: ${notificationViewModel.notifications.value}")
            Toast.makeText(
                requireContext(),
                "${removedItem?.title} 알림이 삭제되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(requireContext(), "삭제할 알림을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}