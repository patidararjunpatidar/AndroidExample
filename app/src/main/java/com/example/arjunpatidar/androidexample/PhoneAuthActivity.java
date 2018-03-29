package com.example.arjunpatidar.androidexample;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private  static final String TAG = "PhoneAuth";

   private EditText phoneText,codeText;
   private Button verifyButton,sendButton,resendButton,signoutButton;
   private TextView statusText;

   private String phoneVerificationId;
   private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private FirebaseAuth fbAuth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        phoneText = (EditText) findViewById(R.id.phoneText);
        codeText = (EditText)findViewById(R.id.codeText);
        sendButton = (Button)findViewById(R.id.sendButton);
        resendButton = (Button) findViewById(R.id.resendButton);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        signoutButton = (Button) findViewById(R.id.signoutButton);
        statusText = (TextView) findViewById(R.id.statusText);

        verifyButton.setEnabled(false);
        resendButton.setEnabled(false);
        signoutButton.setEnabled(false);
        statusText.setText("Signed Out");

        fbAuth = FirebaseAuth.getInstance();

    }

    public void sendCode(View view){

        String phoneNumber = phoneText.getText().toString();

        setUpVerificationCallback();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,    // phone nuber to verified
                60,         // time duration
                TimeUnit.SECONDS,   // units of time
                this,    // Activity for callback
                verificationCallbacks);

    }

    private void setUpVerificationCallback() {

        verificationCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signoutButton.setEnabled(true);
                statusText.setText("Sign up");
                resendButton.setEnabled(true);
                verifyButton.setEnabled(true);
                codeText.setText("");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
               if (e instanceof FirebaseAuthInvalidCredentialsException){
                   Log.d(TAG,"Invalid credential: "+e.getLocalizedMessage());
               }else if(e instanceof FirebaseTooManyRequestsException){
                   Log.d(TAG,"SMS Quote exceeded: ");
               }
            }

            public void onCodeSend(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token){
                phoneVerificationId = verificationId;
                resendToken = token;

                verifyButton.setEnabled(true);
                sendButton.setEnabled(false);
                resendButton.setEnabled(true);

            }
        };
    }

    public void verifyCode(View view){
        String code = codeText.getText().toString();

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signoutButton.setEnabled(true);
                            codeText.setText("");
                            statusText.setText("Sign in");
                            resendButton.setEnabled(false);
                            verifyButton.setEnabled(false);
                            FirebaseUser user = (FirebaseUser) task.getResult().getUser();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                // verification code entered was invalid

                            }
                        }
                    }

                });

    }

    public void resendCode(View view ){
        String phoneNumber = phoneText.getText().toString();
        setUpVerificationCallback();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,    // phone nuber to verified
                60,         // time duration
                TimeUnit.MINUTES,   // units of time
                this,    // Activity for callback
                verificationCallbacks,
                resendToken
        );

    }

    public void signOut(View view){
        fbAuth.signOut();
        statusText.setText("Signed out");
        signoutButton.setEnabled(false);
        sendButton.setEnabled(true);

    }
}
