//    This file is part of Liquid Wars.
//
//    Copyright (C) 2013 Henry Shepperd (hshepperd@gmail.com)
//
//    Liquid Wars is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Liquid Wars is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Liquid Wars.  If not, see <http://www.gnu.org/licenses/>.

package com.xenris.liquidwarsos;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import java.io.*;
import java.util.UUID;

public class BluetoothServerSharer {
    public static final UUID uuid = new UUID(928409374, 170008956);
    private Activity gActivity;
    private Server gServer;
    private BluetoothAdapter gBluetoothAdapter;
    private AcceptThread gAcceptThread;
    public static final int SERVER_SHARER_BLUETOOTH_ENABLED = 1;

    public BluetoothServerSharer(Activity activity, Server server) {
        gActivity = activity;
        gServer = server;
        gBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void enableBluetoothAndShare() {
        if(gBluetoothAdapter != null) {
            if(!gBluetoothAdapter.isEnabled()) {
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                gActivity.startActivityForResult(enableBluetoothIntent, SERVER_SHARER_BLUETOOTH_ENABLED);
            } else {
                share();
            }
        }
    }

    public void share() {
        if(gBluetoothAdapter != null) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);
            gActivity.startActivity(discoverableIntent);
            gAcceptThread = new AcceptThread();
            gAcceptThread.start();
        }
    }

    public void close() {
        if(gAcceptThread != null) {
            gAcceptThread.close();
            gAcceptThread = null;
        }
    }

    public String getPublicName() {
        return gBluetoothAdapter.getName();
    }

    public boolean setPublicName(String name) {
        return gBluetoothAdapter.setName(name);
    }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket bluetoothServerSocket;
        public final String NAME = "BluetoothTest";

        public AcceptThread() {
            try {
                bluetoothServerSocket = gBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, uuid);
            } catch (IOException e) { }
        }

        public void run() {
            while(true) {
                try {
                    BluetoothSocket bluetoothSocket = bluetoothServerSocket.accept();
                    gServer.addClientConnection(new BluetoothClientConnection(bluetoothSocket));
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void close() {
            Util.close(bluetoothServerSocket);
        }
    }
}
