package com.ssafy.cafe_in_port_client.features.recent.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.data.model.response.OrderDetailResponse
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.databinding.FragmentOrderDetailBinding
import com.ssafy.cafe_in_port_client.features.main.ui.MainActivity
import com.ssafy.cafe_in_port_client.features.recent.adapter.OrderDetailListAdapter
import com.ssafy.cafe_in_port_client.ui.MainActivityViewModel
import kotlinx.coroutines.launch

class OrderDetailFragment : BaseFragment<FragmentOrderDetailBinding>(
    FragmentOrderDetailBinding::bind,
    R.layout.fragment_order_detail
) {
    private lateinit var orderDetailListAdapter: OrderDetailListAdapter
    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        activityViewModel.myPageOrderId.observe(viewLifecycleOwner) {
//            initData(it)
        }

        // RecyclerView 설정
        val recyclerView = binding.recyclerViewOrderDetailList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val orderItems = listOf<OrderDetailResponse>()

        initAdapter(orderItems)
    }

    private fun initAdapter(orderItems: List<OrderDetailResponse>) {
        // 어댑터 설정
        val adapter = OrderDetailListAdapter(requireContext(), orderItems)
        binding.recyclerViewOrderDetailList.adapter = adapter

    }

    private fun initData(orderId: Int) {
        lifecycleScope.launch {
            orderDetailListAdapter.orderResponseList =
                RetrofitUtil.orderService.getOrderDetail(orderId).details
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }
}