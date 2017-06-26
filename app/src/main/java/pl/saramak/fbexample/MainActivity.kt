package pl.saramak.fbexample

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.ResultCodes
import com.firebase.ui.auth.IdpResponse
import android.content.Intent
import android.support.design.widget.Snackbar



class MainActivity : AppCompatActivity() {

    lateinit var  signInButton: View
    private val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        signInButton =  findViewById(R.id.sign_in);
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            signInButton.visibility = View.INVISIBLE
            startActivity(createIntent(this, null));
            finish()
        } else {
            signInButton.visibility = View.VISIBLE
            signInButton.setOnClickListener {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                .setAvailableProviders(
                                        listOf(
                                                AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                .build(),
                        RC_SIGN_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                startActivity(createIntent(this, response))
                finish()
                return
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled)
                    return
                }

                if (response.errorCode == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection)
                    return
                }

                if (response.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error)
                    return
                }
            }

            showSnackbar(R.string.unknown_sign_in_response)
        }
    }

    private fun createIntent(mainActivity: MainActivity, response: IdpResponse?) :Intent{
        val intent = Intent(mainActivity, SignedInActivity::class.java)
        return intent;
    }

    private fun showSnackbar(msg: Int) {
        Snackbar.make(signInButton, msg, Snackbar.LENGTH_LONG).show();
    }

}

