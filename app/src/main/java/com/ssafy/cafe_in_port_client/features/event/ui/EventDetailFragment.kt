import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.databinding.FragmentEventDetailBinding


class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달받은 데이터 처리
        val eventTitle = arguments?.getString("eventTitle") ?: "제목 없음"
        val eventImageResId = arguments?.getInt("eventImage") ?: R.drawable.event_detail_image1

        // UI에 데이터 바인딩
        binding.eventTitle.text = eventTitle
        binding.eventImage.setImageResource(eventImageResId)

        // 뒤로가기 버튼 처리
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}