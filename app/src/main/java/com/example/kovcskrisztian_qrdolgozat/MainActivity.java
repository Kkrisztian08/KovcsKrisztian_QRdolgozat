package com.example.kovcskrisztian_qrdolgozat;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import java.io.FileOutputStream;
import android.util.Log;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



public class MainActivity extends AppCompatActivity {

    private TextView szovegki;
    private Button kiir, scan;
    private static final String fajlnev = "scannedCodes.csv";
    private boolean irasengedelyezese;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator i = new IntentIntegrator(MainActivity.this);
                i.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

                i.initiateScan();
            }
        });

        kiir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (irasengedelyezese) {
                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat formazas = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String fileSzoveg = szovegki.getText().toString() + ", " + formazas.format(date);

                    try {
                        FileOutputStream fileOutputStream = openFileOutput(fajlnev, MODE_PRIVATE);
                        fileOutputStream.write(fileSzoveg.getBytes());
                        Toast.makeText(MainActivity.this, "Sikeres mentés: " + getFilesDir() + "/scannedCodes.csv", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Engedélyezni kell a fájlhoz való hozzáférést a fájlbaíráshoz", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String szoveg = szovegki.getText().toString();
            szovegki.setText((intentResult.getContents()));
            try {
                Uri urimuri = Uri.parse(szoveg);
                Intent intent = new Intent(Intent.ACTION_VIEW, urimuri);
                startActivity(intent);
            } catch (Exception e) {
                Log.d("URI ERROR", e.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            irasengedelyezese = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void init() {
        kiir = findViewById(R.id.kiir);
        scan = findViewById(R.id.scan);
        szovegki = findViewById(R.id.szovegki);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            irasengedelyezese = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            irasengedelyezese = true;
        }
    }
}