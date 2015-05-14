package com.doemski.displaytiling;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

public class WifiDirectManager{

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;
    DTBroadcastReceiver receiver;
    Activity mainActivity;
    WifiP2pManager.PeerListListener peerListListener;

    public WifiDirectManager(WifiP2pManager wifiP2pManager, final Activity mainActivity){
        this.wifiP2pManager = wifiP2pManager;
        this.mainActivity = mainActivity;
        this.channel = wifiP2pManager.initialize(mainActivity, Looper.getMainLooper(), null);
        this.peerListListener = new DTPeerListListener(mainActivity);
        this.receiver = new DTBroadcastReceiver(wifiP2pManager, channel, mainActivity, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                if(peers.getDeviceList().size()>=1){

                    String[] peerArray = new String[peers.getDeviceList().size()];
                    int i=0;
                    for (WifiP2pDevice peer : peers.getDeviceList()) {
                        peerArray[i++] = peer.deviceName;

                    /*if(device.deviceAddress.equals("somedevice")){
                        Toast.makeText(ctx, "Server  Name "+device.deviceName,Toast.LENGTH_LONG).show();
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = device.deviceAddress;
                    }*/

                    }
                    new MaterialDialog.Builder(mainActivity)
                            .title(R.string.p2pConnectionDialogueTitle)
                            .items(peerArray)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                }
                            })
                            .show();
                }
            }
        });
    }

    public DTBroadcastReceiver getReceiver() {
        return receiver;
    }

    public void discoverPeers(){
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d("discover p2p peers","failure: " + reasonCode);
            }
        });
    }
}
