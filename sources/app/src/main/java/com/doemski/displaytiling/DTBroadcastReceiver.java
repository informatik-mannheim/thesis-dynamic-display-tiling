package com.doemski.displaytiling;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;


public class DTBroadcastReceiver extends BroadcastReceiver{

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private Activity activity;
    private WifiP2pManager.PeerListListener peerListListener;

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
                    Toast.makeText(context, "Wifi Direct is disabled",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                if (wifiP2pManager != null) {
                    wifiP2pManager.requestPeers(channel, peerListListener);
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:

                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:

                break;
        }
    }
}
