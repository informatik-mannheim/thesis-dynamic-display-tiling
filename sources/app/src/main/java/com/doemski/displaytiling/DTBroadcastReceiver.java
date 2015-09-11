package com.doemski.displaytiling;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;


public class DTBroadcastReceiver extends BroadcastReceiver{

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private Activity activity;
    private WifiP2pManager.PeerListListener peerListListener;
    private DTConnectionInfoListener connectionInfoListener;

    public DTBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Activity activity, WifiP2pManager.PeerListListener peerListListener) {
        super();
        this.wifiP2pManager = manager;
        this.channel = channel;
        this.activity = activity;
        this.peerListListener = peerListListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action){
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi Direct is enabled

                } else {
                    // Wi-Fi Direct is not enabled
                    Log.d("WifiP2p", "Disabled");
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                if (wifiP2pManager != null) {
                    Log.d("DTBROADCASTREC", "PEERS_CHANGED");
                    wifiP2pManager.requestPeers(channel, peerListListener);
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                Log.d("DTBROADCASTREC", "CONNECTION_CHANGED");
                if (wifiP2pManager == null) {
                    return;
                }
                NetworkInfo networkInfo =  intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                ConnectionState conState = ConnectionState.getInstance();
                if (networkInfo.isConnected()) {
                    // We are connected with the other device, request connection
                    // info to find group owner IP
                    conState.setConnected(true);
                    if(connectionInfoListener==null) {
                        connectionInfoListener = new DTConnectionInfoListener(context);
                    }
                    wifiP2pManager.requestConnectionInfo(channel, connectionInfoListener);
                }else{
                        conState.setConnected(false);
                }

                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:

                break;
        }
    }
}
