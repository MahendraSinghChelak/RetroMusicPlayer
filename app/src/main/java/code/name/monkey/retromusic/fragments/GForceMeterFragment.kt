package code.name.monkey.retromusic.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import code.name.monkey.retromusic.service.AcceleroValueListener
import code.name.monkey.retromusic.service.GPSRecordService
import code.name.monkey.retromusic.databinding.FragmentGForceMeterBinding
import code.name.monkey.retromusic.helper.MusicPlayerRemote

class GForceMeterFragment : Fragment(), AcceleroValueListener {

    private var _binding: FragmentGForceMeterBinding? = null
    private val binding get() = _binding!!

    private var delayMilliSecond = 1000L
    private var maxScaleValue = 7.0f
    private var viewWidth = 0
    private var viewHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGForceMeterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            viewWidth = view.width
            viewHeight = view.height
        }
    }

    fun setScale(scale: Float){
        maxScaleValue = scale
    }

    fun registerAcceleroListener(gpsRecordService: GPSRecordService){
        gpsRecordService.registerAcceleroListener(this)
    }

    fun unregisterAcceleroListener(gpsRecordService: GPSRecordService){
        gpsRecordService.unregisterAcceleroListener()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun updateAcceleroTextView(x: Float, y: Float) {
        Handler(Looper.getMainLooper()).postDelayed({
            if(_binding != null) {
                updateMeterGraphic(x, y)
            }
        }, delayMilliSecond)
    }

    fun updateMeterGraphic(x: Float, y: Float){
        binding.GMeterGraphic.updateMeterText(x, y)

        val scaledX = x * viewWidth / maxScaleValue /2
        val scaledY = y * viewHeight / maxScaleValue /2
        binding.GMeterGraphic.updateMeterPosition(scaledX,scaledY)
        binding.leftScaleMark.updateMeterPosition(scaledX, scaledY)
        binding.topScaleMark.updateMeterPosition(scaledX, scaledY)
        binding.rightScaleMark.updateMeterPosition(scaledX, scaledY)
        binding.bottomScaleMark.updateMeterPosition(scaledX, scaledY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

