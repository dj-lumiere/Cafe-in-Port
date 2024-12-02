package com.ssafy.cafe_in_port_client.features.comment.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe_in_port_client.data.model.dto.Comment
import com.ssafy.cafe_in_port_client.databinding.ListItemCommentBinding

private const val TAG = "CommentAdapter"

class CommentAdapter(
    var commentList: MutableList<Comment>,
    private val userNo: Int,
    private val onDeleteClick: (Comment) -> Unit,
    private val onEditClick: (Comment) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    // ViewHolder 정의
    inner class CommentViewHolder(private val binding: ListItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) {
            Log.d(TAG,"bind: Comment UserNo=${comment.userNo}, Current UserNo=$userNo")

            // 사용자 모드 설정
            if (comment.userNo != userNo) {
                setOtherCommentMode()
            } else {
                setMyCommentMode()
            }

            // 데이터 바인딩
            with(binding) {
                userNickname.text = comment.userName
                ratingBar.rating = comment.rating
                textNoticeContent.text = comment.comment

                // 삭제 버튼 클릭 이벤트
                ivDeleteComment.setOnClickListener {
                    onDeleteClick(comment)
                    commentList.removeAt(bindingAdapterPosition)
                    notifyItemRemoved(bindingAdapterPosition)
                }

                // 수정 버튼 클릭 이벤트
                ivModifyComment.setOnClickListener {
                    setCommentEditMode()
                    etCommentContent.setText(comment.comment)
                }

                // 수정 확인 버튼 클릭 이벤트
                ivModifyAcceptComment.setOnClickListener {
                    val updatedContent = etCommentContent.text.toString().trim()
                    if (updatedContent.isBlank()) {
                        Toast.makeText(binding.root.context, "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    comment.comment = updatedContent
                    onEditClick(comment)
                    notifyItemChanged(bindingAdapterPosition)
                    setMyCommentMode()
                }

                // 수정 취소 버튼 클릭 이벤트
                ivModifyCancelComment.setOnClickListener {
                    setMyCommentMode()
                }
            }
        }

        // 다른 사용자의 댓글 모드 설정
        private fun setOtherCommentMode() {
            with(binding) {
                ivModifyComment.visibility = View.GONE
                ivDeleteComment.visibility = View.GONE
                ivModifyAcceptComment.visibility = View.GONE
                ivModifyCancelComment.visibility = View.GONE
                textNoticeContent.visibility = View.VISIBLE
                etCommentContent.visibility = View.GONE
            }
        }

        // 현재 사용자의 댓글 모드 설정
        private fun setMyCommentMode() {
            with(binding) {
                ivModifyComment.visibility = View.VISIBLE
                ivDeleteComment.visibility = View.VISIBLE
                ivModifyAcceptComment.visibility = View.GONE
                ivModifyCancelComment.visibility = View.GONE
                textNoticeContent.visibility = View.VISIBLE
                etCommentContent.visibility = View.GONE
            }
        }

        // 댓글 수정 모드 설정
        private fun setCommentEditMode() {
            with(binding) {
                ivModifyComment.visibility = View.GONE
                ivDeleteComment.visibility = View.GONE
                ivModifyAcceptComment.visibility = View.VISIBLE
                ivModifyCancelComment.visibility = View.VISIBLE
                textNoticeContent.visibility = View.GONE
                etCommentContent.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ListItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}
