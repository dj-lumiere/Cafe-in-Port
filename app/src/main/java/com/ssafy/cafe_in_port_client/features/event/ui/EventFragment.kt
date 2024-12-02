import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.databinding.FragmentEventBinding


class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 첫 번째 카드 클릭
        binding.event1Card.setOnClickListener {
            navigateToDetail(
                title = "달콤하고 폭신한 핫초코 겨울",
                detailImageResId = R.drawable.event_detail_image1 // 첫 번째 이벤트 상세 이미지
            )
        }

        // 두 번째 카드 클릭
        binding.event2Card.setOnClickListener {
            navigateToDetail(
                title = "따뜻한 겨울을 마시다",
                detailImageResId = R.drawable.event_detail_image2 // 두 번째 이벤트 상세 이미지
            )
        }
    }

    private fun navigateToDetail(title: String, detailImageResId: Int) {
        val detailFragment = EventDetailFragment()
        val bundle = Bundle().apply {
            putString("eventTitle", title)
            putInt("eventImage", detailImageResId)
        }
        detailFragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}