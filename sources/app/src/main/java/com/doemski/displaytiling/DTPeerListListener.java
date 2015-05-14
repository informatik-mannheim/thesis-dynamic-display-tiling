package com.doemski.displaytiling;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.Iterator;


public class DTPeerListListener implements WifiP2pManager.PeerListListener {

    Activity activity;
    public  DTPeerListListener(Activity activity){
        this.activity = activity;
    }
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Log.d("Peers Available", "Yes");
        Iterator peersIterator = peers.getDeviceList().iterator();
        while(peersIterator.hasNext()){
            Log.d("Peer: ", peersIterator.next().toString());
        }


    }
}
