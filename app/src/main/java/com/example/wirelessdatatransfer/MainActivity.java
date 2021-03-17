package com.example.wirelessdatatransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;



public class MainActivity extends AppCompatActivity {
    SharedPreferences UniqueID;
    private Button btnSend;
    private TextView idTxt;
    private static Context context;
//    private String HOST = "3.16.23.208";
    private String HOST = "192.168.8.102";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSend = (Button) findViewById(R.id.btnSend);
        idTxt = (TextView) findViewById(R.id.idTxt);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String UniqueID=SharedPreferencesClass.retrieveData(getContext(),"key_1");
        if (UniqueID == ""){
            UniqueID = getRandomNumberString();
            SharedPreferencesClass.insertData(getContext(),"key_1",UniqueID);
            idTxt.setText(UniqueID);
            try {
                addDevicetoServer(UniqueID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            idTxt.setText(UniqueID);
        }


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSendPG();
            }
        });

    }


    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    private Context getContext() {
        MainActivity.context = getApplicationContext();
        return MainActivity.context;
    }


    private void openSendPG() {
        Intent intent = new Intent(MainActivity.this, SendActivity.class);
        startActivity(intent);
    }

    private void addDevicetoServer(String UniqueID) throws IOException {
        String output = String.format("!add " + UniqueID);
        Socket socket = new Socket(HOST, 3000);
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(output);
        dataOutputStream.flush();
        dataOutputStream.close();
        socket.close();


    }


}