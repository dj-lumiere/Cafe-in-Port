package com.ssafy.cafe_in_port_client.features.order.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.common.enums.OrderStatus
import com.ssafy.cafe_in_port_client.common.util.CommonUtils
import com.ssafy.cafe_in_port_client.data.model.dto.Order
import com.ssafy.cafe_in_port_client.data.model.dto.OrderDetail
import com.ssafy.cafe_in_port_client.data.model.dto.ShoppingCart
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil.orderService
import com.ssafy.cafe_in_port_client.databinding.FragmentShoppingListBinding
import com.ssafy.cafe_in_port_client.features.main.ui.MainActivity
import com.ssafy.cafe_in_port_client.features.order.adapter.ShoppingListAdapter
import com.ssafy.cafe_in_port_client.ui.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ShoppingListF_싸피"
const val ORDER_ID = "orderId"

//장바구니 Fragment
class ShoppingListFragment : BaseFragment<FragmentShoppingListBinding>(
    FragmentShoppingListBinding::bind, R.layout.fragment_shopping_list
) {
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var mainActivity: MainActivity
    private var isShop: Boolean = true
    private var reOrderId = -1

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private val _orderResponse = MutableLiveData<Boolean>()
    val orderResponse: LiveData<Boolean> get() = _orderResponse

    private lateinit var nfcDialog: AlertDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
        reOrderId = activityViewModel.myPageOrderId.value ?: -1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserver()
        binding.totalCount.text = getString(R.string.shopping_count, 0)
        binding.totalPrice.text =
            getString(R.string.shopping_money, CommonUtils.makeComma(0))
        if (reOrderId != -1) {
            activityViewModel.clearShoppingList()
            lifecycleScope.launch {
                runCatching {
                    val orderDetail = orderService.getOrderDetail(reOrderId).details
                    orderDetail.forEach {
                        val cartItem = ShoppingCart(
                            it.id,
                            it.productType,
                            it.temperature,
                            it.size,
                            it.productId,
                            it.productImg,
                            it.productName,
                            it.quantity,
                            it.unitPrice,
                            it.sumPrice
                        )
                        activityViewModel.addShoppingList(cartItem)
                    }
                }
            }
        }
        initAdapter()
        initEvent()

        binding.buttonDeleteAll.setOnClickListener {
            AlertDialog
                .Builder(requireContext())
                .setTitle("장바구니 전체 삭제")
                .setMessage("장바구니에 있는 모든 아이템을 삭제합니다. 진행하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("YES") { dialog, _ ->
                    activityViewModel.clearShoppingList()
                    dialog.dismiss()
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    // NFC 다이얼로그 표시 함수
    private fun showNfcDialog() {
        Log.e(TAG, "showNfcDialog: ")
        // NFC 태깅 요청을 위한 다이얼로그 생성 및 표시 로직 작성
        // 예: AlertDialog.Builder(context).setMessage("NFC 태깅을 진행해주세요.")
        nfcDialog =
            AlertDialog.Builder(requireContext()).setMessage("NFC 태깅을 진행해주세요.").setCancelable(false)
                .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerObserver() {
        Log.e(TAG, "registerObserver: ")

        activityViewModel.shoppingList.observe(viewLifecycleOwner) { shoppingList ->
            // Update the adapter with the new shopping list
            refreshList(shoppingList)
        }

        // NFC ID 변경사항 관찰
        activityViewModel.nfcId.observe(viewLifecycleOwner) { nfcId ->
            if (nfcId != null) {

                // 1. NFC ID(테이블 번호)를 서버로 전송
                sendTableInfoToServer(nfcId)

                // 2. NFC 다이얼로그 닫기
                nfcDialog.dismiss()

                // 3. 서버 응답을 관찰하여 주문 성공 시 Order 화면으로 이동
                activityViewModel.orderResponse.observe(viewLifecycleOwner) { orderSuccess ->
                    if (orderSuccess) {
                        // Order 화면으로 이동
                        activityViewModel.clearShoppingList()
                        navigateToOrderScreen()
                    } else {
                        // 주문 실패 시 에러 메시지 표시
                        Toast.makeText(
                            requireContext(), "주문에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    // 서버로 테이블 정보 전송 함수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendTableInfoToServer(nfcId: String) {
        Log.e(TAG, "sendTableInfoToServer: ")
        val user = ApplicationClass.sharedPreferencesUtil.getUser()
        val details: ArrayList<OrderDetail> = arrayListOf()
        activityViewModel.shoppingList.value!!.map { shopping ->
            details.add(OrderDetail().apply {
                productDetailId = shopping.menuDetailId
                quantity = shopping.menuCnt
                unitPrice = shopping.menuPrice
                img = shopping.menuImg
                productName = shopping.menuName
            })
        }
        lifecycleScope.launch {
            runCatching {
                orderService.makeOrder(
                    Order(
                        0,
                        user.userNo,
                        nfcId,
                        null,
                        null,
                        OrderStatus.PENDING,
                        details
                    )
                )
            }.onSuccess {
                Log.e(TAG, "sendTableInfoToServer: $it")
                if (isShop) nfcDialog.dismiss()
                activityViewModel.nfcId.value = null
                parentFragmentManager.popBackStack()
                activityViewModel.clearShoppingList()
                showToast("주문이 완료되었습니다.")
            }.onFailure {
                Log.e(TAG, "sendTableInfoToServer: $it")
                if (isShop) nfcDialog.dismiss()
                activityViewModel.nfcId.value = null
                parentFragmentManager.popBackStack()
                showToast("주문이 실패하였습니다.")
            }
        }

    }

    // Order 화면으로 이동하는 함수
    private fun navigateToOrderScreen() {
        Log.e(TAG, "navigateToOrderScreen: ")
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_page_2
    }


    private fun initAdapter() {
        Log.e(TAG, "initAdapter: ")
        shoppingListAdapter =
            ShoppingListAdapter(
                mutableListOf(),
                onDeleteClick = { idx -> deleteShoppingCart(idx) },
                onPlusClick = { idx -> activityViewModel.editCount(idx, 1) },
                onMinusClick = { idx -> activityViewModel.editCount(idx, -1) })

        binding.recyclerViewCart.apply {
            adapter = shoppingListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            //원래의 목록위치로 돌아오게함
            adapter?.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    private fun refreshList(shoppingList: MutableList<ShoppingCart>) {
        Log.e(TAG, "refreshList: ")
        var count = 0
        var price = 0
        shoppingList.forEach {
            count += it.menuCnt
            price += it.menuCnt * it.menuPrice
        }
        binding.totalCount.text = getString(R.string.shopping_count, count)
        binding.totalPrice.text =
            getString(R.string.shopping_money, CommonUtils.makeComma(price))
        shoppingListAdapter.list = shoppingList
        shoppingListAdapter.notifyDataSetChanged()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvent() {
        Log.e(TAG, "initEvent: ")
        binding.storeButton.setOnClickListener {
            binding.storeButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_color)
            binding.takeoutButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_non_color)
            isShop = true
        }
        binding.takeoutButton.setOnClickListener {
            binding.takeoutButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_color)
            binding.storeButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_non_color)
            isShop = false
        }
        binding.orderButton.setOnClickListener {
            if (isShop) {
                showDialogForOrderInShop()
            } else {
                // Replace 'true' with actual distance check
                if ((activityViewModel.distance.value ?: Long.MAX_VALUE) >= 200L) {
                    showDialogForOrderTakeoutOver200m()
                } else {
                    completedOrder("Take Out 주문")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    private fun showDialogForOrderInShop() {
        Log.e(TAG, "showDialogForOrderInShop: ")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "Table NFC를 먼저 찍어주세요.\n"
        )
        builder.setCancelable(true)

        builder.setNegativeButton(
            "취소"
        ) { dialog, _ ->
            dialog.cancel()
            showToast("주문이 취소되었습니다.")
        }
        builder.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialogForOrderTakeoutOver200m() {
        Log.e(TAG, "showDialogForOrderTakeoutOver200m: ")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "현재 고객님의 위치가 매장과 200m 이상 떨어져 있습니다.\n정말 주문하시겠습니까?"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("확인") { _, _ ->
            completedOrder("Take Out 주문")
        }
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
            showToast("주문이 취소되었습니다.")
        }
        builder.create().show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun completedOrder(table: String) {
        Log.e(TAG, "completedOrder: ")
        if ((activityViewModel.shoppingList.value?.size ?: 0) <= 0) {
            showToast("장바구니가 비어 있습니다.")
            return
        }
        val userNo = ApplicationClass.sharedPreferencesUtil.getUser().userNo
        Log.e(TAG, "completedOrder: $userNo")
        val orderTable = table
        val completed = OrderStatus.PENDING
        val details = activityViewModel.shoppingList.value?.map {
            val detail = OrderDetail(it.menuId, it.menuCnt)
            detail
        }
        val order = Order(
            0,
            userNo,
            orderTable,
            null,
            null,
            completed,
            details as ArrayList<OrderDetail>
        )
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                orderService.makeOrder(order)
            }.onSuccess {
                activityViewModel.clearShoppingList()
                showToast("주문이 완료되었습니다.")
            }.onFailure {
                showToast("주문이 실패하였습니다.")
            }
        }
    }

    private fun deleteShoppingCart(idx: Int) {
        Log.e(TAG, "deleteShoppingCart: ")
        activityViewModel.removeShoppingList(idx)
    }

    // 재주문할때는 parameter로 넘기기.
    companion object {
        @JvmStatic
        fun newInstance(param: Int) = ShoppingListFragment().apply {
            arguments = Bundle().apply {
                putInt(ORDER_ID, param)
            }
        }
    }
}