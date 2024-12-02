package com.ssafy.cafe_in_port_client.features.comment.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.base.BaseFragment
import com.ssafy.cafe_in_port_client.data.model.dto.Comment
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.databinding.FragmentCommentBinding
import com.ssafy.cafe_in_port_client.features.comment.adapter.CommentAdapter
import com.ssafy.cafe_in_port_client.features.main.ui.MainActivity
import com.ssafy.cafe_in_port_client.features.order.viewmodel.MenuDetailFragmentViewModel
import com.ssafy.cafe_in_port_client.ui.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

private const val TAG = "CommentFragment"

class CommentFragment : BaseFragment<FragmentCommentBinding>(
    FragmentCommentBinding::bind, R.layout.fragment_comment
) {
    private lateinit var mainActivity: MainActivity
    private lateinit var commentAdapter: CommentAdapter
    val userNo = ApplicationClass.sharedPreferencesUtil.getUser().userNo

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

        registerObserver()
        viewModel.getProductInfo()
        Log.e(TAG, "onViewCreated: $userNo")
        commentAdapter = CommentAdapter(commentList = mutableListOf(),
            userNo = userNo,
            onDeleteClick = { comment -> handleDeleteComment(comment) },
            onEditClick = { updatedComment -> handleUpdateComment(updatedComment) })
        binding.btnCreateComment.setOnClickListener {
            val content = binding.etCreateComment.text.toString()
            if (content.isBlank()) {
                return@setOnClickListener
            }
            showDialogRatingStar(content)
        }
        binding.reviewList.apply {
            adapter = commentAdapter
        }
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun registerObserver() {
        viewModel.menuDetailUiData.observe(viewLifecycleOwner) {
            Log.e(TAG, "registerObserver: $it")
            if (it.productInfo != null) {
                binding.commentTitle.text = it.productInfo.productName
                commentAdapter.commentList = it.productInfo.comments.toMutableList()
                binding.tvAverage.text =
                    "${DecimalFormat("#.#").format(it.productInfo.productRatingAvg)} 점"
                binding.ratingBar.rating = it.productInfo.productRatingAvg.toFloat()
                commentAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun showDialogRatingStar(content: String) {
        val productId = activityViewModel.productId.value ?: 0
        // Inflate the custom layout
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_menu_comment, null)


        // Optionally, initialize views within the dialog
        val ratingBarMenuDialogComment =
            dialogView.findViewById<RatingBar>(R.id.ratingBarMenuDialogComment)
        ratingBarMenuDialogComment.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (fromUser) {
                if (rating < 1) {
                    ratingBar.rating = 1F
                }
            }
        }
        // Build the dialog
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).setCancelable(false)
            .setPositiveButton("작성") { dialogInterface, which ->
                CoroutineScope(Dispatchers.Main).launch {
                    runCatching {
                        val comment = Comment(
                            -1, userNo, productId, ratingBarMenuDialogComment.rating, content, ""
                        )
                        RetrofitUtil.commentService.insert(comment)
                    }.onSuccess {
                        viewModel.getProductInfo(productId)
                        Toast.makeText(requireContext(), "등록 성공", Toast.LENGTH_SHORT).show()
                        binding.etCreateComment.setText("")
                    }.onFailure {
                        Log.e(TAG, "showDialogRatingStar: $it")
                    }
                }
            }.setNegativeButton("취소") { dialogInterface, which ->
                dialogInterface.dismiss()
            }.create()
        dialog.show()
    }

    private fun handleDeleteComment(comment: Comment) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                RetrofitUtil.commentService.delete(comment.commentId)
            }.onSuccess { response ->
                if (response) {
                    Toast.makeText(requireContext(), "삭제 성공", Toast.LENGTH_SHORT).show()
                    viewModel.getProductInfo(comment.productId)
                } else {
                    Toast.makeText(requireContext(), "삭제 실패: $response", Toast.LENGTH_SHORT)
                        .show()
                }
            }.onFailure { throwable ->
                Toast.makeText(
                    requireContext(), "에러 발생: ${throwable.localizedMessage}", Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "Error deleting comment", throwable)
            }
        }
    }

    private fun handleUpdateComment(updatedComment: Comment) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                RetrofitUtil.commentService.update(updatedComment)
            }.onSuccess { response ->
                if (response) {
                    Toast.makeText(requireContext(), "수정 성공", Toast.LENGTH_SHORT).show()
                    viewModel.getProductInfo(updatedComment.productId)
                } else {
                    Toast.makeText(requireContext(), "수정 실패: ${response}", Toast.LENGTH_SHORT)
                        .show()
                }
            }.onFailure { throwable ->
                Toast.makeText(
                    requireContext(), "에러 발생: ${throwable.localizedMessage}", Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "Error updating comment", throwable)
            }
        }
    }
}