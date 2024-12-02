package com.ssafy.cafe_in_port_client.features.main.ui

import EventFragment
import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.cafe_in_port_client.common.Constants.BEACON_LAYOUT
import com.ssafy.cafe_in_port_client.common.Constants.BEACON_UUID
import com.ssafy.cafe_in_port_client.common.Constants.BEACON_MAJOR
import com.ssafy.cafe_in_port_client.common.Constants.BEACON_MINOR
import com.ssafy.cafe_in_port_client.common.Constants.BLUETOOTH_ADDRESS
import com.ssafy.cafe_in_port_client.common.Constants.BEACON_DISTANCE
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.base.BaseActivity
import com.ssafy.cafe_in_port_client.databinding.ActivityMainBinding
import com.ssafy.cafe_in_port_client.common.enums.MainAction
import com.ssafy.cafe_in_port_client.ui.home.HomeFragment
import com.ssafy.cafe_in_port_client.features.mypage.ui.MyPageFragment
import com.ssafy.cafe_in_port_client.features.recent.ui.OrderDetailFragment
import com.ssafy.cafe_in_port_client.features.order.ui.MapFragment
import com.ssafy.cafe_in_port_client.features.order.ui.MenuDetailFragment
import com.ssafy.cafe_in_port_client.features.order.ui.OrderFragment
import com.ssafy.cafe_in_port_client.common.util.PermissionChecker
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import com.ssafy.cafe_in_port_client.features.comment.ui.CommentFragment
import com.ssafy.cafe_in_port_client.features.login.ui.ResetPwFragment
import com.ssafy.cafe_in_port_client.ui.MainActivityViewModel
import com.ssafy.cafe_in_port_client.features.order.ui.ShoppingListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.RangeNotifier
import org.altbeacon.beacon.Region
import com.google.android.gms.tasks.OnCompleteListener
import com.google.gson.Gson
import com.ssafy.cafe_in_port_client.features.notification.viewmodel.NotificationViewModel
import com.ssafy.cafe_in_port_client.common.Constants.OPENAI_API_KEY
import com.ssafy.cafe_in_port_client.data.model.dto.openai.ChatCompletionRequest
import com.ssafy.cafe_in_port_client.data.model.dto.openai.Message
import com.ssafy.cafe_in_port_client.data.remote.OpenAiApiClient
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private const val TAG = "MainActivity"

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val notificationViewModel: NotificationViewModel by viewModels()

    private var lastShownTime = 0L

    // Beacon의 Region 설정
    private val region = Region(
        "estimote", listOf(
            Identifier.parse(BEACON_UUID),
            Identifier.parse(BEACON_MAJOR),
            Identifier.parse(BEACON_MINOR)
        ), BLUETOOTH_ADDRESS
    )

    data class Quote(
        val content: String,
        val title: String
    )

    // NFC
    private lateinit var nAdapter: NfcAdapter
    private lateinit var pIntent: PendingIntent
    private lateinit var filters: Array<IntentFilter>

    private lateinit var beaconManager: BeaconManager
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    /** permission check **/
    private val checker = PermissionChecker(this, this)
    private val runtimePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS,
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    /** permission check **/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 가장 첫 화면은 홈 화면의 Fragment로 지정
        createNotificationChannel("ssafy_channel", "ssafy")

        // BeaconManager 지정
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_LAYOUT))
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        /* permission check */
        if (!checker.checkPermission(runtimePermissions)) {
            checker.setOnGrantedListener {
                // 퍼미션 획득 성공일 때
                startScan()
            }

            checker.requestPermissionLauncher.launch(runtimePermissions)
        } else { // 이미 전체 권한이 있는 경우
            startScan()
        }
        /* permission check */

        supportFragmentManager.beginTransaction().replace(R.id.frame_layout_main, HomeFragment())
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_page_1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, HomeFragment()).commit()
                    true
                }

                R.id.navigation_page_2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, OrderFragment()).commit()
                    true
                }

                R.id.navigation_page_3 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, EventFragment()).commit()
                    true
                }

                R.id.navigation_page_4 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, MyPageFragment()).commit()
                    true
                }

                else -> false
            }
        }

        binding.bottomNavigation.setOnItemReselectedListener { item ->
            // 재선택 시 다시 렌더링하지 않기 위해 수정
            if (binding.bottomNavigation.selectedItemId != item.itemId) {
                binding.bottomNavigation.selectedItemId = item.itemId
            }
        }

        nAdapter = NfcAdapter.getDefaultAdapter(this)

        // Intent 를 처리할 activity --> PendingIntent
        // Intent filter
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP // 내가 top에 있으면 재사용 --> onNewIntent
        }
        pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        val filter = IntentFilter().apply {
            addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            addCategory(Intent.CATEGORY_DEFAULT)
            addDataType("text/*")
        }

        val filter2 = IntentFilter().apply {
            addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            addCategory(Intent.CATEGORY_DEFAULT)
            addDataScheme("https")
        }

        val filter3 = IntentFilter().apply {
            addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            addCategory(Intent.CATEGORY_DEFAULT)
            addDataScheme("http")
        }

        filters = arrayOf(filter, filter2, filter3)
        initFCM()
        createNotificationChannel(channel_id, "Cafe in Port")
    }

    // NFC 태그 읽기 활성화
    override fun onResume() {
        super.onResume()
        nAdapter.enableForegroundDispatch(this, pIntent, filters, null)
    }

    // NFC 태그 읽기 비활성화
    override fun onPause() {
        super.onPause()
        nAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Toast.makeText(this, "onNewIntent", Toast.LENGTH_SHORT).show()
        val action = intent.action
        Log.d(TAG, "New Intent action : $action")
        parseData(intent)
    }

    private fun parseData(intent: Intent) {
        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED || intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage::class.java
                )
            } else {
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            }
            Log.d(TAG, "processIntent: ${tag?.size}")
            tag?.forEach {
                (it as NdefMessage).records.forEach {
                    Log.d(TAG, "processIntent: ${it.payload}")
                    mainActivityViewModel.nfcId.value = String(it.payload, 3, it.payload.size - 3)
                }
            }
        }
    }

    // 테이블 번호를 SharedPreferences에 저장
    private fun saveTableNumber(tableNumber: String) {
        getSharedPreferences("table_info", MODE_PRIVATE).edit().apply {
            putString("table_number", tableNumber)
            apply()
        }
    }

    fun openFragment(index: MainAction, key: String, value: Int) {
        moveFragment(index, key, value)
    }

    fun openFragment(index: MainAction) {
        moveFragment(index, "", 0)
    }

    private fun moveFragment(action: MainAction, key: String, value: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        val replaceTarget = R.id.frame_layout_main
        when (action) {

            // 장바구니
            MainAction.SHOPPING_LIST_FRAGMENT -> {
                // 재주문
                if (key.isNotBlank() && value != 0) {
                    transaction
                        .replace(replaceTarget, ShoppingListFragment.newInstance(value))
                        .addToBackStack(null)
                } else {
                    transaction
                        .replace(replaceTarget, ShoppingListFragment())
                        .addToBackStack(null)
                }
            }

            // 주문 상세 보기
            MainAction.ORDER_DETAIL_FRAGMENT -> {
                transaction
                    .replace(replaceTarget, OrderDetailFragment())
                    .addToBackStack(null)
            }

            // 메뉴 상세 보기
            MainAction.MENU_DETAIL_FRAGMENT -> {
                transaction
                    .replace(replaceTarget, MenuDetailFragment())
                    .addToBackStack(null)
            }

            // 지도 보기
            MainAction.MAP_FRAGMENT -> {
                transaction
                    .replace(replaceTarget, MapFragment())
                    .addToBackStack(null)
            }

            // 비밀번호 재설정
            MainAction.RESET_PW_FRAGMENT -> {
                transaction
                    .replace(replaceTarget, ResetPwFragment())
                    .addToBackStack(null)
            }

            MainAction.COMMENT_FRAGMENT -> {
                transaction
                    .replace(replaceTarget, CommentFragment())
                    .addToBackStack(null)
            }

            // 로그아웃
            MainAction.LOGOUT -> {
                logout()
            }
        }
        transaction.commit()
    }

    private fun logout() {
        // preference 지우기
        ApplicationClass.sharedPreferencesUtil.deleteUser()
        ApplicationClass.sharedPreferencesUtil.deleteUserCookie()

        // 화면 이동
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
    }

    fun hideBottomNav(state: Boolean) {
        if (state) binding.bottomNavigation.visibility = View.GONE
        else binding.bottomNavigation.visibility = View.VISIBLE
    }

    // NotificationChannel 설정
    private fun createNotificationChannel(id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance)

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 블루투스 ON/OFF 여부 확인 및 키도록 하는 함수
    private fun requestEnableBLE() {
        val callBLEEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestBLEActivity.launch(callBLEEnableIntent)
        Log.d(TAG, "requestEnableBLE: ")
    }

    private val requestBLEActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 사용자의 블루투스 사용이 가능한지 확인
        if (bluetoothAdapter.isEnabled) {
            startScan()
        }
    }

    // Beacon Scan 시작
    private fun startScan() {
        // 블루투스 Enable 확인
        if (!bluetoothAdapter.isEnabled) {
            requestEnableBLE()
            Log.d(TAG, "startScan: 블루투스가 켜지지 않았습니다.")
            return
        }
        Log.d(TAG, "startScan: ")

        // 리전에 비콘이 있는지 없는지 정보를 받는 클래스 지정
        beaconManager.addMonitorNotifier(monitorNotifier)
        beaconManager.startMonitoring(region)

        // 해당 region의 beacon 정보를 받는 클래스 지정
        beaconManager.addRangeNotifier(rangeNotifier)
        beaconManager.startRangingBeacons(region)

        handler.postDelayed({
            stopScan()
        }, 30_000)
    }

    private fun stopScan() {
        Log.d(TAG, "stopScan: !!")
        beaconManager.stopMonitoring(region)
        beaconManager.stopRangingBeacons(region)
    }

    // 꼭 Destroy를 시켜서 beacon scan을 중지 시켜야 한다.
    // beacon scan을 중지하지 않으면 일정 시간 이후 다시 scan이 가능하다.
    override fun onStop() {
        super.onStop()
        stopScan()
    }

    val handler = Handler(Looper.getMainLooper())

    var monitorNotifier: MonitorNotifier = object : MonitorNotifier {
        override fun didEnterRegion(region: Region) {
            Log.d(TAG, "비콘을 발견하였습니다.")
        }

        override fun didExitRegion(region: Region) {
            Log.d(TAG, "비콘을 찾을 수 없습니다.")
        }

        override fun didDetermineStateForRegion(i: Int, region: Region) {}
    }

    var rangeNotifier: RangeNotifier = object : RangeNotifier {
        override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
            Log.d(TAG, "didRangeBeaconsInRegion: $beacons")
            beacons?.run {
                forEach { beacon ->
                    // Major, Minor로 Beacon 구별
                    if (isYourBeacon(beacon)) {
                        val currentTime = System.currentTimeMillis()
                        // 한번만 띄우기 위한 조건
                        // 24시간 동안 한 번만 다이얼로그를 띄우는 조건
                        if (shouldShowDialog(currentTime)) {
                            fetchQuoteAndShowDialog()
                            saveDialogShownTime(currentTime) // 다이얼로그를 띄운 시간을 저장
                        }
                    }
                }
            }
        }

        private fun fetchQuoteAndShowDialog() {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val apiKey = OPENAI_API_KEY
                    val authorization = "Bearer $apiKey"

                    val messages = listOf(
                        Message(
                            role = "user",
                            content = "직장인들의 평안한 일상을 위해 책 속 명언 한 줄과 책이름을 알려줘. json 형식으로, content에 명언 한 줄, title에 책 제목을 적어줘."
                        )
                    )

                    val request = ChatCompletionRequest(
                        model = "gpt-4o-mini",
                        messages = messages,
                        temperature = 0.7
                    )

                    val response = withContext(Dispatchers.IO) {
                        OpenAiApiClient.openAiService.createChatCompletion(
                            authorization = authorization,
                            request = request
                        )
                    }

                    val content = response.choices[0].message.content.trim()

                    // 코드 블록 ```json ... ``` 제거
                    val jsonString = content.substringAfter("```json").substringBefore("```").trim()

                    // JSON 파싱
                    val gson = Gson()
                    val quote = gson.fromJson(jsonString, Quote::class.java)

                    showDialog(quote)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "문장을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        private fun showDialog(quote: Quote) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_store_event, null)

            val todayTxt = dialogView.findViewById<TextView>(R.id.today_txt)
            val bookTitle = dialogView.findViewById<TextView>(R.id.book_title)

            todayTxt.text = quote.content
            bookTitle.text = getString(R.string.book_title, quote.title)

            AlertDialog.Builder(this@MainActivity)
                .setView(dialogView)
                .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                .setCancelable(false)
                .show()
        }

        private fun shouldShowDialog(currentTime: Long): Boolean {
            val preferences = getSharedPreferences("dialog_prefs", MODE_PRIVATE)
            val lastShownTime = preferences.getLong("last_shown_time", 0L)

            return currentTime - lastShownTime >= TimeUnit.HOURS.toMillis(24)
        }

        private fun saveDialogShownTime(currentTime: Long) {
            val preferences = getSharedPreferences("dialog_prefs", MODE_PRIVATE)
            preferences.edit().putLong("last_shown_time", currentTime).apply()
        }

        // 찾고자 하는 Beacon이 맞는지, 정해둔 거리 내부인지 확인
        private fun isYourBeacon(beacon: Beacon): Boolean {
            return (beacon.id2.toString() == BEACON_MAJOR && beacon.id3.toString() == BEACON_MINOR && beacon.distance <= BEACON_DISTANCE)
        }
    }

    private fun initFCM() {
        // FCM 토큰 수신
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
                return@OnCompleteListener
            }
            // token log 남기기
            Log.d(TAG, "token: ${task.result ?: "task.result is null"}")
            if (task.result != null) {
                uploadToken(task.result!!)
            }
        })
    }

    companion object {
        const val channel_id = "ssafy_channel"
        fun uploadToken(token: String) {
            // 새로운 토큰 수신 시 서버로 전송
            val apiService = RetrofitUtil.apiService
            CoroutineScope(Dispatchers.Main).launch {
                val userNo = ApplicationClass.sharedPreferencesUtil.getUser().userNo
                val id = ApplicationClass.sharedPreferencesUtil.getUser().id
                Log.e(TAG, "uploadToken: yes")
                if (id.startsWith("google-")) {
                    return@launch
                }
                Log.e(TAG, "uploadToken: $id")
                Log.e(TAG, "uploadToken: $userNo")
                Log.e(TAG, "uploadToken: $token")
                apiService.updateFcmToken(userNo, token)
            }
        }
    }
}