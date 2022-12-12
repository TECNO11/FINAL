package com.HP4.sensorapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class Sensor_NFC extends AppCompatActivity {

    public static final String Error_Detected = "No NFC tag Detectada";
    public static final String Write_Success = "Texto enviado Exitosamente !";
    public static final String Write_Error = "Error Durante el envio , Intentar Nuevamente !";

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] WritingTagFilters;
    boolean writeMode;
    Tag myTag;
    Context context;
    TextView edit_message;
    TextView nfc_content;
    Button ActivateButton, btnBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_nfc);
        edit_message = (TextView) findViewById(R.id.et);
        nfc_content = findViewById(R.id.tv_R);
        ActivateButton = findViewById(R.id.button);
        btnBackButton = findViewById(R.id.btnBackButton);
        context = this;

        btnBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (myTag == null) {
                        Toast.makeText(context, Error_Detected, Toast.LENGTH_LONG).show();
                    } else {
                        write(edit_message.getText().toString(), myTag);
                        Toast.makeText(context, Write_Success, Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(context, Write_Error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (FormatException e) {
                    Toast.makeText(context, Write_Error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter == null) {
                Toast.makeText(this, "Este dispositivo no soporta NFC", Toast.LENGTH_LONG).show();
                finish();

            }
        } catch (UnknownError e) {
            Log.e("eror", "erorr");
        }


        readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        WritingTagFilters = new IntentFilter[]{tagDetected};
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            BuildTagViews(msgs);
        }
    }

    private void BuildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;
        String text = "";

        //       String tagId = new String(msgs[0].getRecords ()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
// String languageCode = new String (payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("Unsupported Encoding", e.toString());
        }
        nfc_content.setText(text);
    }

    private void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];
        // set status byte (see NDEF spec for actual bits)

        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        return recordNFC;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        WrieModeOff();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WrieModeOn();
    }

    private void WrieModeOn() {
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, WritingTagFilters, null);
    }

    private void WrieModeOff() {
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

}