package com.ssafy.cafe_in_port_client.features.order.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.common.Constants.HUMAN_VELOCITY_PER_MINUTE
import com.ssafy.cafe_in_port_client.common.Constants.LONG_DISTANCE_THRESHOLD
import com.ssafy.cafe_in_port_client.databinding.FragmentOrderBinding
import com.ssafy.cafe_in_port_client.common.enums.MainAction
import com.ssafy.cafe_in_port_client.data.model.dto.Store
import com.ssafy.cafe_in_port_client.features.main.ui.MainActivity
import com.ssafy.cafe_in_port_client.features.order.adapter.MenuAdapter
import com.ssafy.cafe_in_port_client.features.order.viewmodel.OrderViewModel
import com.ssafy.cafe_in_port_client.ui.MainActivityViewModel

// 하단 주문 탭
private const val TAG = "OrderFragment_싸피"

class OrderFragment :
    BaseFragment<FragmentOrderBinding>(FragmentOrderBinding::bind, R.layout.fragment_order) {
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var mainActivity: MainActivity

    lateinit var manager: LocationManager
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    val storeLocations = listOf(
        Store(LatLng(36.1030, 128.4150), "삼성코닝점"), // 예시 좌표1
        Store(LatLng(36.1080, 128.4190), "인동가산로점"), // 예시 좌표2
        Store(LatLng(36.1090, 128.4210), "인동중앙로점")  // 예시 좌표3
    )

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val orderViewModel: OrderViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.textviewDistance.text = getString(R.string.calculating_nearby_store_distance)

        binding.emptyTextView.visibility = View.GONE
        binding.recyclerViewMenu.visibility = View.VISIBLE

        menuAdapter = MenuAdapter(arrayListOf()) { productId ->
            activityViewModel.setProductId(productId)
            mainActivity.openFragment(MainAction.MENU_DETAIL_FRAGMENT)
        }

        binding.recyclerViewMenu.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = menuAdapter
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        initObservers()
        fetchProducts()
        getCurrentLocation()
        initEvent()
        setupSearchView()
    }

    private fun fetchProducts() {
        orderViewModel.fetchProducts()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    val (nearestStoreLength, nearestStore) = findNearestStoreAndDistance(
                        currentLatLng,
                        storeLocations
                    )
                    orderViewModel.setNearestStore(nearestStore)
                    activityViewModel.setDistance(nearestStoreLength)
                    val eta =
                        ((nearestStoreLength + HUMAN_VELOCITY_PER_MINUTE - 1) / HUMAN_VELOCITY_PER_MINUTE) // ceiling division
                    binding.storeDistance.text =
                        if (nearestStoreLength >= LONG_DISTANCE_THRESHOLD) {
                            getString(
                                R.string.nearby_store_distance_long,
                                nearestStoreLength / 1000f,
                                eta / 60,
                                eta % 60
                            )
                        } else {
                            getString(
                                R.string.nearby_store_distance,
                                nearestStoreLength,
                                eta
                            )
                        }
                    binding.storeName.text = nearestStore.storeName
                }
            }
        }
    }

    fun findNearestStoreAndDistance(
        myLocation: LatLng,
        storeLocation: List<Store>
    ): Pair<Long, Store> {
        var result = Long.MAX_VALUE
        val results = FloatArray(1)
        var nearestStore = Store(LatLng(0.0, 0.0), "")
        storeLocation.map {
            Location.distanceBetween(
                myLocation.latitude,
                myLocation.longitude,
                it.location.latitude,
                it.location.longitude,
                results
            )
            val distanceInMeters = results[0].toLong()
            if (result > distanceInMeters) {
                result = distanceInMeters
                nearestStore = it
            }
        }
        return result to nearestStore
    }

    private fun initEvent() {
        binding.floatingBtn.setOnClickListener {
            //장바구니 이동
            mainActivity.openFragment(MainAction.SHOPPING_LIST_FRAGMENT)
        }

        binding.btnMap.setOnClickListener {
            mainActivity.openFragment(MainAction.MAP_FRAGMENT)
        }
    }

    private fun initObservers() {
        orderViewModel.filteredProductList.observe(viewLifecycleOwner) { filteredList ->
            menuAdapter.productList = (filteredList)
            binding.emptyTextView.visibility =
                if (filteredList.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewMenu.visibility =
                if (filteredList.isEmpty()) View.GONE else View.VISIBLE
            menuAdapter.notifyDataSetChanged()
        }

        orderViewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                binding.emptyTextView.visibility = View.VISIBLE
                binding.recyclerViewMenu.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                binding.recyclerViewMenu.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSearchView() {
        val searchView = binding.menuSearchview
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.e(TAG, "onQueryTextSubmit: yes")
                // Optionally handle search button press
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.e(TAG, "onQueryTextChange: yes")
                orderViewModel.filterProducts(newText)
                return true
            }
        })
    }
}