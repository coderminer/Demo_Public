package com.example.demo.wifidirect;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.demo.R;

public class DeviceListFragment extends ListFragment implements PeerListListener{

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peersList) {
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        peers.clear();
        peers.addAll(peersList.getDeviceList());
        ((WiFiPeerListAdapter)getListAdapter()).notifyDataSetChanged();
        if(peers.size() == 0){
            Log.e(WiFiDirectActivity.TAG, "the peers size is 0");
            return;
        }
    }
    
    public WifiP2pDevice getDevice(){
        return device;
    }
    
    private static String getDeviceStatus(int deviceStatus){
        Log.e(WiFiDirectActivity.TAG, "Peer status: "+deviceStatus);
        switch(deviceStatus){
        case WifiP2pDevice.AVAILABLE:
            return "Avaiable";
        case WifiP2pDevice.INVITED:
            return "Invited";
        case WifiP2pDevice.CONNECTED:
            return "Conntend";
        case WifiP2pDevice.FAILED:
            return "Failed";
        case WifiP2pDevice.UNAVAILABLE:
            return "Unavailable";
        default:
            return "Unkonw";
        }
    }
    
    
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice)getListAdapter().getItem(position);
        ((DeviceActionListener)getActivity()).showDetail(device);
    }

    public void updateThisDevice(WifiP2pDevice device){
        this.device = device;
        TextView view = (TextView)mContentView.findViewById(R.id.my_name);
        view.setText(device.deviceName);
        view = (TextView)mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(device.status));
    }
    
    public void clearPeers(){
        peers.clear();
        ((WiFiPeerListAdapter)getListAdapter()).notifyDataSetChanged();
    }
    
    public void onInitiateDiscovery(){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(getActivity(), "Press back to channel", "finding peers",true,true,
                new DialogInterface.OnCancelListener() {
                    
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        
                    }
                });
    }

    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice>{

        private List<WifiP2pDevice> items;
        public WiFiPeerListAdapter(Context context,
                int textViewResourceId, List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v ==  null){
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }
            WifiP2pDevice device = items.get(position);
            if(device != null){
                TextView top = (TextView)v.findViewById(R.id.device_name);
                TextView bottom = (TextView)v.findViewById(R.id.device_details);
                if(null != top){
                    top.setText(device.deviceName);
                }
                if(null != bottom){
                    bottom.setText(device.deviceAddress);
                }
            }
            return v;
        }
        
        
        
    }
    
    public interface DeviceActionListener{
        void showDetail(WifiP2pDevice device);
        void cancelDisconnect();
        void connect(WifiP2pConfig config);
        void disconnect();
    }
    
}