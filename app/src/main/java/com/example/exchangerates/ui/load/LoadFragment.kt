package com.example.exchangerates.ui.load

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.exchangerates.MainActivity
import com.example.exchangerates.R
import com.example.exchangerates.ui.home.HomeViewModel
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal
import kotlin.system.exitProcess


class LoadFragment : Fragment() {

    lateinit var viewModel: HomeViewModel

    private val TAG = "LoadFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.addLiveData(requireContext()).observe(viewLifecycleOwner) {
            check(it, container)
        }
        return inflater.inflate(R.layout.fragment_load, container, false)
    }

    override fun onStart() {
        Log.d(TAG, "onStart: ")
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    private fun check(it: Boolean, container: ViewGroup?) {
        if (it) findNavController().navigate(R.id.action_loadFragment_to_nav_home)
        else {
            NoInternetDialogSignal.Builder(
                requireActivity(),
                lifecycle
            ).apply {
                dialogProperties.apply {
                    connectionCallback = object : ConnectionCallback {
                        override fun hasActiveConnection(hasActiveConnection: Boolean) {
                            if (hasActiveConnection) {
                                val mStartActivity = Intent(context, MainActivity::class.java)
                                val mPendingIntentId = 123456
                                val mPendingIntent = PendingIntent.getActivity(
                                    context,
                                    mPendingIntentId,
                                    mStartActivity,
                                    PendingIntent.FLAG_CANCEL_CURRENT
                                )
                                val mgr =
                                    context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] =
                                    mPendingIntent
                                exitProcess(0)
                            }
                        }
                    }

                    cancelable = false // Optional
                    noInternetConnectionTitle = "No Internet" // Optional
                    noInternetConnectionMessage =
                        "Check your Internet connection and try again." // Optional
                    showInternetOnButtons = true // Optional
                    pleaseTurnOnText = "Please turn on" // Optional
                    wifiOnButtonText = "Wifi" // Optional
                    mobileDataOnButtonText = "Mobile data" // Optional

                    onAirplaneModeTitle = "No Internet" // Optional
                    onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
                    pleaseTurnOffText = "Please turn off" // Optional
                    airplaneModeOffButtonText = "Airplane mode" // Optional
                    showAirplaneModeOffButtons = true // Optional
                }
            }.build()
        }
    }


}