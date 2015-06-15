package com.doemski.displaytiling;

/**
 * Classes which implement Updateable can listen to changes in the ConnectionState Singleton
 */
public interface Updateable {
    void connectionStateChanged(boolean isConnected);
}
