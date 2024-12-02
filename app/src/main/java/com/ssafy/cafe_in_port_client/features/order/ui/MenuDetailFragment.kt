package com.ssafy.cafe_in_port_client.features.order.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.common.Constants.MENU_IMGS_URL
import com.ssafy.cafe_in_port_client.common.enums.DrinkSize
import com.ssafy.cafe_in_port_client.common.enums.MainAction
import com.ssafy.cafe_in_port_client.common.enums.ProductType
import com.ssafy.cafe_in_port_client.common.enums.Temperature
import com.ssafy.cafe_in_port_client.common.util.CommonUtils
import com.ssafy.cafe_in_port_client.data.model.dto.MenuDetailUiData
import com.ssafy.cafe_in_port_client.data.model.dto.ProductDetail
import com.ssafy.cafe_in_port_client.data.model.dto.ShoppingCart
import com.ssafy.cafe_in_port_client.databinding.FragmentMenuDetailBinding
import com.ssafy.cafe_in_port_client.features.main.ui.MainActivity
import com.ssafy.cafe_in_port_client.features.comment.adapter.CommentAdapter
import com.ssafy.cafe_in_port_client.features.order.viewmodel.MenuDetailFragmentViewModel
import com.ssafy.cafe_in_port_client.ui.MainActivityViewModel

//메뉴 상세 화면 . Order탭 - 특정 메뉴 선택시 열림
private const val TAG = "MenuDetailFragment_싸피"

class MenuDetailFragment : BaseFragment<FragmentMenuDetailBinding>(
    FragmentMenuDetailBinding::bind, R.layout.fragment_menu_detail
) {
    private lateinit var mainActivity: MainActivity
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var productDetails: List<ProductDetail>
    var userId: String = ""

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: MenuDetailFragmentViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.productId = activityViewModel.productId.value!!
        mainActivity.hideBottomNav(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = ApplicationClass.sharedPreferencesUtil.getUser().id
        registerObserver()
        viewModel.getProductDetails()
        viewModel.getProductInfo()
        initListener()

        binding.showAllComments.setOnClickListener {
            mainActivity.openFragment(MainAction.COMMENT_FRAGMENT)
        }
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun registerObserver() {
        viewModel.menuDetailUiData.observe(viewLifecycleOwner) {
            Log.e(TAG, "registerObserver: $it")
            // 화면 정보 갱신
            if (it != null) {
                // Both data are available
                updateUI(it)
            }
        }
    }

    // 초기 화면 설정
    private fun updateUI(
        menuDetailUiData: MenuDetailUiData
    ) {
        var productInfo = menuDetailUiData.productInfo
        var productDetails = menuDetailUiData.productDetails
        var count = menuDetailUiData.count
        var temperature = menuDetailUiData.temperature
        var drinkSize = menuDetailUiData.drinkSize
        var productDetailId = menuDetailUiData.productDetailId
        var unitPrice = menuDetailUiData.unitPrice
        Log.d(
            TAG,
            "setScreen: productInfo=$productInfo productDetails=$productDetails count=$count"
        )
        if (productInfo != null) {
            Glide.with(this).load("${MENU_IMGS_URL}${productInfo.productImg}")
                .into(binding.productImage)
        }
        if (productInfo == null) {
            return
        }
        if (productDetails == null) {
            return
        }
        val disableHot = productDetails.none { it.temperature == Temperature.HOT }
        val disableIce = productDetails.none { it.temperature == Temperature.COLD }
        val disablePetite = productDetails.none { it.size == DrinkSize.PETITE }
        val disableRegular = productDetails.none { it.size == DrinkSize.REGULAR }
        val disableGrande = productDetails.none { it.size == DrinkSize.GRANDE }

        Log.e(
            TAG,
            "updateUI1: $disableHot $disableIce $disablePetite $disableRegular $disableGrande"
        )

        binding.apply {
            productName.text = productInfo.productName
            ratingBar.rating = productInfo.productRatingAvg.toFloat()

            // Set default temperature
            if (temperature == null) {
                Log.e(TAG, "updateUI: temp not set")
                when {
                    !disableHot -> {
                        viewModel.setTemperature(Temperature.HOT)
                    }

                    !disableIce -> {
                        viewModel.setTemperature(Temperature.COLD)
                    }

                    else -> viewModel.setTemperature(Temperature.NO)
                }
                Log.e(TAG, "updateUI: ${viewModel.menuDetailUiData.value?.temperature}")
            }
            Log.e(
                TAG,
                "updateUI2: ${hotButton.isSelected} ${iceButton.isSelected} ${petiteSize.isSelected} ${regularSize.isSelected} ${grandeSize.isSelected}"
            )

            // Set default size
            if (drinkSize == null) {
                Log.e(TAG, "updateUI: size not set")
                when {
                    !disablePetite -> {
                        viewModel.setDrinkSize(DrinkSize.PETITE)
                    }

                    !disableRegular -> {
                        viewModel.setDrinkSize(DrinkSize.REGULAR)
                    }

                    !disableGrande -> {
                        viewModel.setDrinkSize(DrinkSize.GRANDE)
                    }
                }
            } else if (disablePetite && disableRegular && disableGrande) {
                viewModel.setDrinkSize(DrinkSize.NO)
            }

            Log.e(
                TAG,
                "updateUI3: ${hotButton.isSelected} ${iceButton.isSelected} ${petiteSize.isSelected} ${regularSize.isSelected} ${grandeSize.isSelected}"
            )

            hotButton.isSelected = temperature == Temperature.HOT
            iceButton.isSelected = temperature == Temperature.COLD
            petiteSize.isSelected = drinkSize == DrinkSize.PETITE
            regularSize.isSelected = drinkSize == DrinkSize.REGULAR
            grandeSize.isSelected = drinkSize == DrinkSize.GRANDE

            decreaseButton.isEnabled = count != 1

            quantityText.text = count.toString()

            Log.e(
                TAG,
                "updateUI4: ${hotButton.isSelected} ${iceButton.isSelected} ${petiteSize.isSelected} ${regularSize.isSelected} ${grandeSize.isSelected}"
            )

            hotButton.visibility = if (disableHot) {
                View.GONE
            } else {
                View.VISIBLE
            }
            iceButton.visibility = if (disableIce) {
                View.GONE
            } else {
                View.VISIBLE
            }
            petiteSize.visibility = if (disablePetite) {
                View.GONE
            } else {
                View.VISIBLE
            }
            regularSize.visibility = if (disableRegular) {
                View.GONE
            } else {
                View.VISIBLE
            }
            grandeSize.visibility = if (disableGrande) {
                View.GONE
            } else {
                View.VISIBLE
            }

            Log.e(
                TAG,
                "updateUI5: ${hotButton.isSelected} ${iceButton.isSelected} ${petiteSize.isSelected} ${regularSize.isSelected} ${grandeSize.isSelected}"
            )
            // Handle visibility if no options are available
            if (disableHot && disableIce) {
                viewModel.setTemperature(Temperature.NO)
                noTemperaturePlaceholder.visibility = View.VISIBLE
            } else {
                noTemperaturePlaceholder.visibility = View.GONE
            }

            if (disablePetite && disableRegular && disableGrande) {
                viewModel.setDrinkSize(DrinkSize.NO)
                noSizePlaceholder.visibility = View.VISIBLE
            } else {
                noSizePlaceholder.visibility = View.GONE
            }

            Log.e(
                TAG,
                "updateUI6: ${hotButton.isSelected} ${iceButton.isSelected} ${petiteSize.isSelected} ${regularSize.isSelected} ${grandeSize.isSelected}"
            )

            productPrice.text = CommonUtils.makeComma(unitPrice * count)
        }
    }

    private fun initListener() {
        binding.apply {
            increaseButton.setOnClickListener {
                val currentCount = viewModel.menuDetailUiData.value?.count ?: 1
                val newCount = currentCount + 1
                viewModel.setCount(newCount)

            }

            decreaseButton.setOnClickListener {
                val currentCount = viewModel.menuDetailUiData.value?.count ?: 1
                if (currentCount > 1) {
                    val newCount = currentCount - 1
                    viewModel.setCount(newCount)
                }
            }

            hotButton.setOnClickListener {
                Log.e(TAG, "initListener: hot button clicked")
                viewModel.setTemperature(Temperature.HOT)
                viewModel.setCount(1)
            }

            iceButton.setOnClickListener {
                Log.e(TAG, "initListener: ice button clicked")
                viewModel.setTemperature(Temperature.COLD)
                viewModel.setCount(1)
            }

            petiteSize.setOnClickListener {
                viewModel.setDrinkSize(DrinkSize.PETITE)
                viewModel.setCount(1)
            }

            regularSize.setOnClickListener {
                viewModel.setDrinkSize(DrinkSize.REGULAR)
                viewModel.setCount(1)
            }

            grandeSize.setOnClickListener {
                viewModel.setDrinkSize(DrinkSize.GRANDE)
                viewModel.setCount(1)
            }

            btnAddList.setOnClickListener {
                viewModel.menuDetailUiData.value?.let { item ->
                    ShoppingCart(
                        menuId = viewModel.productId,
                        menuType = item.productInfo?.type ?: ProductType.NO,
                        temperature = item.temperature ?: Temperature.NO,
                        size = item.drinkSize ?: DrinkSize.NO,
                        menuDetailId = item.productDetailId,
                        menuImg = item.productInfo?.productImg ?: "",
                        menuName = item.productInfo?.productName ?: "",
                        menuCnt = item.count,
                        menuPrice = item.unitPrice,
                        totalPrice = item.unitPrice * item.count,
                    )
                }?.apply {
                    activityViewModel.addShoppingList(this)
                    showToast("상품이 장바구니에 담겼습니다.")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "onDestroyView: called")
        viewModel.reset()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: called")
        mainActivity.hideBottomNav(false)
    }

}