package com.example.demo.wifidirect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FileTransferService extends IntentService{

    public FileTransferService(String name) {
        super(name);
    }
    
    public FileTransferService(){
        super("FileTransferService");
    }

    private static final int SOCKET_TIMEOUT = 500;
    public static final String ACTION_SEND_FILE = "com.example.demo.wifidirect.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_ADDRESS = "host";
    public static final String EXTRAS_GROUP_PORT = "port";
    
    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        if(intent.getAction().equals(ACTION_SEND_FILE)){
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_GROUP_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_PORT);
            try{
                socket.bind(null);
                socket.connect(new InetSocketAddress(host, port),SOCKET_TIMEOUT);
                OutputStream os = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                
                is = cr.openInputStream(Uri.parse(fileUri));
                
                DeviceDetailFragment.copyFile(is, os);
            }catch(Exception ex){
                
            }finally{
                if(socket != null){
                    if(socket.isConnected()){
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
}