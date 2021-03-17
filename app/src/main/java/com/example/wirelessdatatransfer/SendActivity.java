package com.example.wirelessdatatransfer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

import br.com.onimur.handlepathoz.HandlePathOz;
import br.com.onimur.handlepathoz.HandlePathOzListener;
import br.com.onimur.handlepathoz.model.PathOz;

public class SendActivity extends AppCompatActivity implements HandlePathOzListener.SingleUri{
    private static final int PICK_PDF_FILE = 2;
    private HandlePathOz handlePathOz;
//    private String HOST_IP = "3.16.23.208";
    private String HOST_IP = "192.168.8.102";


    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        handlePathOz.getRealPath(uri);
    }

    private void run(String fileURL) {
        FileDetails details;
        try {
            Socket kkSocket = getSocket();
            File file = new File(fileURL);
            byte[] data = new byte[4096]; // Here you can increase the size also which will send it faster
            details = new FileDetails();
            details.setDetails(file.getName(), file.length());
//            // Sending file details to the client
            sendFileDetails(details, kkSocket, file);
            // Sending File Data
            sendFileData(kkSocket, file, data);

            kkSocket.close();
        }
        catch (Exception e) {
            System.out.println("Error : " + e.toString());
        }
    }

    private void sendFileData(Socket kkSocket, File file, byte[] data) throws IOException {
        System.out.println("Sending file data...");
        FileInputStream fileStream = new FileInputStream(file);
        BufferedInputStream fileBuffer = new BufferedInputStream(fileStream);
        OutputStream out = kkSocket.getOutputStream();
        int count;
        while ((count = fileBuffer.read(data)) != -1) {
            System.out.println("Data Sent : " + count);
                out.write(data, 0, count);
            out.flush();
        }
        out.close();
        fileBuffer.close();
        fileStream.close();
    }

    private void sendFileDetails(FileDetails details, Socket kkSocket, File file) throws IOException {
        System.out.println("Sending file details... " + file.getName() + " - " + file.length());
        ObjectOutputStream sendDetails = new ObjectOutputStream(kkSocket.getOutputStream());
        sendDetails.writeObject(details);
        sendDetails.flush();
    }

    private Socket getSocket() throws IOException {
        System.out.println("Waiting for Client...");
        String host = HOST_IP;
        Socket kkSocket = new Socket(host, 4000);
        System.out.println("Connected to Client" +
                "...");
        return kkSocket;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        handlePathOz = new HandlePathOz(this, this);
        openFile();
    }

    @Override
    public void onRequestHandlePathOz(PathOz pathOz, Throwable throwable) {
        System.out.println("The real path is: " + pathOz.getPath() + "\n The type is: " + pathOz.getType());
        run(pathOz.getPath());

    }
}