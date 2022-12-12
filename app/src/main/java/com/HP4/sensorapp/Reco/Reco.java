package com.HP4.sensorapp.Reco;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import com.HP4.sensorapp.R;

import java.util.concurrent.Executor;

public class Reco extends AppCompatActivity {

    private BiometricPrompt biometricPrompt = null;
    private Executor executor = new MainThreadExecutor();

    private BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
        @SuppressLint("RestrictedApi")
        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON && biometricPrompt != null)
                biometricPrompt.cancelAuthentication();
            toast(errString.toString());
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            toast("Autentificacion Exitosa");
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            toast("Autentificacion Fallida. Intenta Nuevamente!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (biometricPrompt == null)
            biometricPrompt = new BiometricPrompt(this, executor, callback);
        BiometricPrompt.PromptInfo promptInfo = buildBiometricPrompt();
        biometricPrompt.authenticate(promptInfo);
    }

    private BiometricPrompt.PromptInfo buildBiometricPrompt() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Ingresa un metodo valido para acceder.")
                .setNegativeButtonText("Cancel")
                .build();
    }

}