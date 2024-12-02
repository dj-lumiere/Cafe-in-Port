package com.ssafy.cafe_in_port_client.features.order.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.common.util.PermissionChecker
import com.ssafy.cafe_in_port_client.databinding.FragmentMapBinding
import com.ssafy.cafe_in_port_client.features.order.viewmodel.OrderViewModel
import com.ssafy.cafe_in_port_client.ui.order.StoreBottomSheetFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale


// Order 탭 - 지도 화면
private const val TAG = "MapFragment"

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var permissionChecker: PermissionChecker
    private var currentLatLng: LatLng? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val orderViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        // PermissionChecker 초기화
        permissionChecker = PermissionChecker(requireContext(), this)

        // 지도 초기화
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view_map_google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 초기 위치 설정
        setDefaultLocation()

        permissionChecker.setOnGrantedListener {
            startLocationUpdates()
        }

        // 권한 체크 및 위치 업데이트
        if (!permissionChecker.checkPermission(PermissionChecker.runtimePermissions)) {
            permissionChecker.requestPermissionLauncher.launch(PermissionChecker.runtimePermissions)
        } else {
            startLocationUpdates()
        }

        // 마커 클릭 리스너 설정
        mMap.setOnMarkerClickListener { marker ->
            Log.e(TAG, "onMapReady: YES")
            Log.e(TAG, "onMapReady: ${marker.title}")
            if (marker.title?.contains(getString(R.string.self)) == true) {
                openBottomSheet(marker)
            }
            true
        }

        // 예제 마커 추가
        addSampleMarker()
    }

    private fun setDefaultLocation() {
        // 서울 중심부로 초기 위치 설정
        val seoul = LatLng(37.5665, 126.9780)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15f))
    }

    //현재위치 업데이트
    private fun startLocationUpdates() {
        // 위치 권한이 허용되었는지 확인
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 15f))
            } else {
                Toast.makeText(requireContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "위치 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "위치 정보 가져오기 오류", exception)
        }
    }

    private fun findDistance(target: LatLng): Float {
        if (currentLatLng == null) {
            return Float.POSITIVE_INFINITY
        }
        val results = FloatArray(1)
        Location.distanceBetween(
            currentLatLng!!.latitude,
            currentLatLng!!.longitude,
            target.latitude,
            target.longitude,
            results
        )
        return results[0]
    }

    suspend fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
        if (!Geocoder.isPresent()) {
            Log.e("Geocoder", "Geocoder services are not available on this device.")
            return ""
        }

        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            // Geocoder returns a list of addresses; you can specify the maximum number you want
            val addresses = withContext(Dispatchers.IO) {
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            }

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                // You can format the address as needed
                address.getAddressLine(0) // Returns the full address as a single string
            } else {
                Log.e("Geocoder", "No address found for the given LatLng.")
                ""
            }
        } catch (e: IOException) {
            Log.e("Geocoder", "Network or other I/O issues: ${e.message}")
            ""
        } catch (e: IllegalArgumentException) {
            Log.e("Geocoder", "Invalid latitude or longitude values: ${e.message}")
            ""
        }
    }

    private fun openBottomSheet(marker: Marker) {
        val storeBottomSheetFragment = StoreBottomSheetFragment()

        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                val address = getAddressFromLatLng(requireContext(), marker.position)
                val distance = findDistance(marker.position).toInt()
                val args = Bundle().apply {
                    putString("name", marker.title)
                    putString("address", address)
                    putString("distance", "${distance}m")
                    putString("weekdayHours", "08:00 ~ 22:00")
                    putString("weekendHours", "08:00 ~ 23:00")
                    putDouble("latitude", marker.position.latitude)
                    putDouble("longitude", marker.position.longitude)
                }
                storeBottomSheetFragment.arguments = args
                storeBottomSheetFragment.show(parentFragmentManager, "StoreBottomSheet")
            }
        }
    }

    private fun addSampleMarker() {
        Log.e(TAG, "addSampleMarker: yes")
        // 샘플 마커 추가
        val markerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        val store = orderViewModel.nearestStore.value
        val samplePosition =
            store?.location ?: LatLng(36.107001, 128.419976)
        // 포항 카페  위도(Latitude) : 36.0563237118688 / 경도(Longitude) : 129.375218215025
        mMap.addMarker(
            MarkerOptions()
                .position(samplePosition)
                .title("Cafe in Port ${store?.storeName ?: ""}")
                .icon(markerIcon)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}