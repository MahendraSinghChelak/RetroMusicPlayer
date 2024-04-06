package code.name.monkey.retromusic.service

import code.name.monkey.retromusic.activities.MainActivity
import android.content.Context
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit
import java.util.concurrent.CountDownLatch

class GPSRecordService() : Service(), LocationListener {

    private val binder = LocalBinder()
    private lateinit var locationManager: LocationManager
    private val locationUpdateLatch = CountDownLatch(1)
    private var latitude = 0.0
    private var longitude = 0.0

    private var count = 1
    private val localStorageLimit = 20000000 // 20MB
    private val blockSize = 3
    private val recordInterval = 1L // 1 second

    inner class LocalBinder : Binder() {
        fun getService(): GPSRecordService = this@GPSRecordService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 0.001f, this)
        } catch (e: SecurityException) {
            Log.e("GPSRecordService", "Location permission not granted", e)
        }
        Thread {
            recordGPS()
        }.start()
        return START_STICKY
    }

    private fun recordGPS() {
        try {
            locationUpdateLatch.await()
        } catch (e: InterruptedException) {
            Log.e("GPSRecordService", "Interrupted while waiting for location update", e)
        }
        while (true) {
            val localFile = File(this.filesDir, "local file.dat")
            if (!localFile.exists()) {
                localFile.createNewFile()
            }

            var fileSize = localFile.length()
            while (fileSize < localStorageLimit) {
                val recordedValue =
                    ByteArray(blockSize * 24) // 24 [byte] = 8 [byte] for time + 8 [bytes] for latitude + 8 [byte] for longitude
                val buffer = ByteBuffer.wrap(recordedValue).order(ByteOrder.LITTLE_ENDIAN)

                for (i in 0 until blockSize) {
                    val currentTime = System.currentTimeMillis()
                    buffer.putLong(currentTime)
                    buffer.putDouble(latitude)
                    buffer.putDouble(longitude)
                    TimeUnit.SECONDS.sleep(recordInterval)
                }

                try {
                    FileOutputStream(localFile, true).use { fos ->
                        fos.write(recordedValue)
                    }
                } catch (e: IOException) {
                    Log.e("GPSRecordService", "Error writing to file", e)
                }

                fileSize = localFile.length()
            }

            print("uploading the local file")
            //file compressed_local_file = compress(local_file)
            //onedrive.upload(compressed_local_file, "onedrive:/Location Records/Recording Temporary/$count")
            //delete(local_file)
            //count++
        }
    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        locationUpdateLatch.countDown()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }

    override fun onProviderEnabled(provider: String) { }

    override fun onProviderDisabled(provider: String) { }

    override fun onDestroy() {
        super.onDestroy()
        try {
            locationManager.removeUpdates(this)
        } catch (e: SecurityException) {
            Log.e("GPSRecordService", "Failed to remove location updates", e)
        }
    }
}
