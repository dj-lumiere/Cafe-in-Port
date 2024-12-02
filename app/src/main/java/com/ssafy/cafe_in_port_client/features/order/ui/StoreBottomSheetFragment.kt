package com.ssafy.cafe_in_port_client.ui.order

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.cafe_in_port_client.databinding.FragmentStoreBottomSheetBinding


class StoreBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentStoreBottomSheetBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달된 데이터 처리
        arguments?.let {
            binding.storeName.text = it.getString("name", "알 수 없는 매장")
            binding.storeAddress.text = it.getString("address", "주소 정보 없음")
            binding.storeDistance.text = it.getString("distance", "거리 정보 없음")
            binding.storeWeekdayHours.text = it.getString("weekdayHours", "평일 영업시간 정보 없음")
            binding.storeWeekendHours.text = it.getString("weekendHours", "주말 영업시간 정보 없음")
        }

        // 버튼 동작 설정
        setupMarquee()
        setupButtonActions()
    }

    private fun setupMarquee() {
        binding.storeAddress.apply {
            isSelected = true // Ensure marquee starts
            ellipsize = TextUtils.TruncateAt.MARQUEE
            marqueeRepeatLimit = -1 // Infinite loop
            setSingleLine(true)
            isFocusable = true
            isFocusableInTouchMode = true
            setHorizontallyScrolling(true)
        }
    }

    private fun setupButtonActions() {
        // 길찾기 버튼 클릭 이벤트
        binding.directionsButton.setOnClickListener {
            val latitude = arguments?.getDouble("latitude", 0.0) ?: 0.0
            val longitude = arguments?.getDouble("longitude", 0.0) ?: 0.0
            openDirections(latitude, longitude)
        }

        // 전화걸기 버튼 클릭 이벤트
        binding.callButton.setOnClickListener {
            makePhoneCall("01012345678") // 샘플 전화번호
        }
    }

    private fun openDirections(latitude: Double, longitude: Double) {
        val destination = "$latitude,$longitude"
        try {
            // 구글맵 네비게이션 실행
            val gmmIntentUri = Uri.parse("google.navigation:q=$destination&mode=d")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            startActivity(mapIntent)
        } catch (e: Exception) {
            // 구글맵이 없을 경우 브라우저로 길찾기
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$destination")
            )
            startActivity(webIntent)
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        try {
            startActivity(intent)
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "전화 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
