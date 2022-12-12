package com.HP4.sensorapp.Reco;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import com.HP4.sensorapp.R;

import java.util.concurrent.Executor;

public class RecoActivity extends AppCompatActivity {
    TextView txtBiometricManager;
    Button btnNewIntent;

    private BiometricPrompt biometricPrompt = null;
    private Executor executor = new MainThreadExecutor();

    private BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {

        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON && biometricPrompt != null)
                biometricPrompt.cancelAuthentication();
            txtBiometricManager.setText(errString.toString());
            btnNewIntent.setVisibility(View.GONE);
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            txtBiometricManager.setText("Autentificacion Exitosa");
            btnNewIntent.setVisibility(View.GONE);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            txtBiometricManager.setText("Autentificacion Fallida. Intenta Nuevamente!");
            btnNewIntent.setVisibility(View.VISIBLE);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reco_layout);
        txtBiometricManager = findViewById(R.id.txtBiometricManager);
        btnNewIntent = findViewById(R.id.btnNewIntent);

        if (biometricPrompt == null)
            biometricPrompt = new BiometricPrompt(this, executor, callback);

        BiometricPrompt.PromptInfo promptInfo = buildBiometricPrompt();
        biometricPrompt.authenticate(promptInfo);

        btnNewIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BiometricPrompt.PromptInfo promptInfo = buildBiometricPrompt();
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }

    private BiometricPrompt.PromptInfo buildBiometricPrompt() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Ingresa un metodo valido para acceder.")
                .setNegativeButtonText("Cancel")
                .build();
    }

}