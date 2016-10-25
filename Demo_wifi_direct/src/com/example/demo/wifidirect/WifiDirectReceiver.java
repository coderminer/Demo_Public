package com.example.demo.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import com.example.demo.R;

public class WifiDirectReceiver extends BroadcastReceiver{

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WiFiDirectActivity mActivity;
    
    public WifiDirectReceiver(WifiP2pManager manager,Channel channel,WiFiDirectActivity activity){
        super();
        this.mActivity = activity;
        this.mChannel = channel;
        this.mManager = manager;
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(WiFiDirectActivity.TAG, "=============p2p action: "+action);
        if(action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            Log.e(WiFiDirectActivity.TAG, "=============p2p state: "+state);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                mActivity.setIsWifiP2pEnabled(true);
            }else{
                mActivity.setIsWifiP2pEnabled(false);
                mActivity.resetData();
            }
        }else if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){
            if(mManager != null){
                mManager.requestPeers(mChannel, (PeerListListener)mActivity.getFragmentManager().findFragmentById(R.id.frag_list));
            }
        }else if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){
            if(mManager == null)return;
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()){
                DeviceDetailFragment fragment  = (DeviceDetailFragment)mActivity.getFragmentManager().findFragmentById(R.id.frag_detail);
                mManager.requestConnectionInfo(mChannel, fragment);
            }
        }else if(action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){
            DeviceListFragment fragment = (DeviceListFragment) mActivity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }
    
}