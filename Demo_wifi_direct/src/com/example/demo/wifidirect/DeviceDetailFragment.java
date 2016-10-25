package com.example.demo.wifidirect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.demo.R;
import com.example.demo.wifidirect.DeviceListFragment.DeviceActionListener;

public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener{

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "Connecting to: "+device.deviceAddress,
                        true,true);
                ((DeviceActionListener)getActivity()).connect(config);
            }
        });
        
        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ((DeviceActionListener)getActivity()).disconnect();
            }
        });
        
        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
            }
        });
        return mContentView;
    }
    
    public void showDetails(WifiP2pDevice device){
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView)mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView)mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());
    }
    
    public void resetViews(){
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView)mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView)mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView)mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView)mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        TextView statusText = (TextView)mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: "+uri);
        Intent i = new Intent(getActivity(), FileTransferService.class);
        i.setAction(FileTransferService.ACTION_SEND_FILE);
        i.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        i.putExtra(FileTransferService.EXTRAS_GROUP_ADDRESS, info.groupOwnerAddress.getHostAddress());
        i.putExtra(FileTransferService.EXTRAS_GROUP_PORT, 8988);
        getActivity().startService(i);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
        
        TextView view = (TextView)mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)+
                ((info.isGroupOwner == true)?getResources().getString(R.string.yes):
                    getResources().getString(R.string.no)));
        view = (TextView)mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - "+info.groupOwnerAddress.getHostAddress());
        if(info.groupFormed && info.isGroupOwner){
            new FileServerAsnycTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
        }else if(info.groupFormed){
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView)mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }
            
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }
    
    public static class FileServerAsnycTask extends AsyncTask<Void,Void,String>{

        private Context context;
        private TextView statusText;
        
        public FileServerAsnycTask(Context context,View statusText){
            this.context  = context;
            this.statusText = (TextView)statusText;
        }
        
        @Override
        protected String doInBackground(Void... params) {
            try{
                ServerSocket socket = new ServerSocket(8988);
                Socket client = socket.accept();
                final File f = new File(Environment.getExternalStorageDirectory()+"/"+
                context.getPackageName()+"/wifishared-"+System.currentTimeMillis()+".jpg");
                File dirs = new File(f.getParent());
                if(!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();
                InputStream is = client.getInputStream();
                copyFile(is, new FileOutputStream(f));
                socket.close();
                return f.getAbsolutePath();
            }catch(Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null){
                statusText.setText("File copied - "+result);
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.parse("file//"+result), "image/*");
                context.startActivity(i);
            }
        }
        
        
    }
    
    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }
    
}