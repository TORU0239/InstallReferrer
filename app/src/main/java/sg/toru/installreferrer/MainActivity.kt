package sg.toru.installreferrer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RemoteException
import android.widget.Toast
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import sg.toru.installreferrer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var referrerClient: InstallReferrerClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        referrerClient = InstallReferrerClient.newBuilder(this@MainActivity).build()
        referrerClient.startConnection(object: InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            val response = referrerClient.installReferrer
                            val referrerClickTime = response.referrerClickTimestampSeconds
                            val appInstallTime = response.installBeginTimestampSeconds

                            val referrer = response.installReferrer
                            binding.txtReferral.text = "referrer url: $referrer, referrer click time: $referrerClickTime, app install time: $appInstallTime"
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                            binding.txtReferral.text = "Exception occurred!"
                        }
                    }

                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        binding.txtReferral.text = "Feature not supported!"
                    }

                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        binding.txtReferral.text = "Service unavailable!"
                    }

                    InstallReferrerClient.InstallReferrerResponse.PERMISSION_ERROR -> {
                        binding.txtReferral.text = "Permission Error!"
                    }

                    else -> {
                        binding.txtReferral.text = "Error occurred!"
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                Toast.makeText(this@MainActivity, "Service disconnected.", Toast.LENGTH_SHORT).show()
            }

        })
    }
}