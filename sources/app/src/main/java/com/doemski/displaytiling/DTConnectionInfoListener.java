package com.doemski.displaytiling;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;


public class DTConnectionInfoListener implements WifiP2pManager.ConnectionInfoListener{

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

        // InetAddress from WifiP2pInfo struct.
        //InetAddress groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
        String hostAdress = info.groupOwnerAddress.getHostAddress();
       // String hostName = info.groupOwnerAddress.getHostName();


        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting
            // incoming connections.

            Log.d("is Group Owner", "true");
            new ConnectionBuilder().execute();

        } else if (info.groupFormed) {
            // The other device acts as the group owner. In this case,
            // you'll want to create a client thread that connects to the group
            // owner.
            Log.d("is Group Owner", "false");
            new ClientAsyncTask().execute(hostAdress);

        }
    }
}
