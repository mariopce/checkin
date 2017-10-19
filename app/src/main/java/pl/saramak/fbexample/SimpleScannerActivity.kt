package pl.saramak.fbexample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class SimpleScannerActivity : Activity(), ZXingScannerView.ResultHandler {
    lateinit var mScannerView: ZXingScannerView


    override fun onCreate(state: Bundle?) {
        super.onCreate(state)


        mScannerView = ZXingScannerView(this)   // Programmatically initialize the scanner view
        setContentView(mScannerView)                // Set the scanner view as the content view


    }

    override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }

    val TAG: String = "SimpleScannerActivity"

    override fun handleResult(rawResult: Result) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()) // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()) // Prints the scan format (qrcode, pdf417 etc.)

        val intent = Intent()
        intent.putExtra("RESULT", rawResult.getText());
        setResult(Activity.RESULT_OK, intent)
        finish()

        // If you would like to resume scanning, call this method below:
//        mScannerView!!.resumeCameraPreview(this)
    }
}