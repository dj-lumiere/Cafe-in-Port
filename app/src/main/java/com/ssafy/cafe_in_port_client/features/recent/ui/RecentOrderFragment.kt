package com.ssafy.cafe_in_port_client.features.recent.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.common.enums.MainAction
import com.ssafy.cafe_in_port_client.data.model.response.OrderResponse
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.databinding.FragmentRecentOrderBinding
import com.ssafy.cafe_in_port_client.features.main.ui.MainActivity
import com.ssafy.cafe_in_port_client.features.order.viewmodel.MenuDetailFragmentViewModel
import com.ssafy.cafe_in_port_client.features.recent.adapter.LatestOrderListAdapter
import com.ssafy.cafe_in_port_client.features.recent.viewmodel.RecentOrderViewModel
import com.ssafy.cafe_in_port_client.ui.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "RecentOrderFragment"

class RecentOrderFragment : BaseFragment<FragmentRecentOrderBinding>(
    FragmentRecentOrderBinding::bind,
    R.layout.fragment_recent_order
) {

    private lateinit var adapter: LatestOrderListAdapter
    private var orderList = listOf<OrderResponse>() // 주문 데이터 리스트
    private val recentOrderViewModel: RecentOrderViewModel by activityViewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userNo = ApplicationClass.sharedPreferencesUtil.getUser().userNo
        setupRecyclerView()
        observeViewModel()
        fetchOrderData(userNo)
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    private fun setupRecyclerView() {
        adapter = LatestOrderListAdapter(orderList) { order ->
            // 주문하기 버튼 클릭 시 장바구니 페이지로 이동
            mainActivityViewModel.setOrderId(order.orderId)
            (requireActivity() as MainActivity).openFragment(MainAction.SHOPPING_LIST_FRAGMENT)
        }

        binding.recyclerViewOrderDetailList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecentOrderFragment.adapter
        }
    }

    private fun observeViewModel() {
        recentOrderViewModel.orderList.observe(viewLifecycleOwner) { orders ->
            adapter.updateData(orders)
        }

        recentOrderViewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                binding.emptyTextView.visibility = View.VISIBLE
                binding.recyclerViewOrderDetailList.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                binding.recyclerViewOrderDetailList.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchOrderData(userNo: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Fetch data from server
                val fetchedOrderList = RetrofitUtil.orderService.getLast6MonthOrder(userNo).map {
                    RetrofitUtil.orderService.getOrderDetail(it.orderId).apply {
                        totalPrice = it.details.sumOf { it.sumPrice }
                        orderCount = it.details.sumOf { it.quantity }
                    }
                }
                recentOrderViewModel.setOrderList(fetchedOrderList)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching orders: ${e.message}")
                // Optionally, show an error message to the user
                recentOrderViewModel.setOrderList(emptyList())
            }
        }
    }
}