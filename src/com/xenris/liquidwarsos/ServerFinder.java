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
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.io.*;

public class ServerFinder {
    private Activity activity;
    private BluetoothAdapter bluetoothAdapter;
    private Callbacks callbacks;
    public static final int SERVER_FINDER_BLUETOOTH_ENABLED = 2;

    public ServerFinder(Activity activity, Callbacks callbacks) {
        this.activity = activity;
        this.callbacks = callbacks;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void enableBluetoothAndSearch() {
        if(bluetoothAdapter != null) {
            if(!bluetoothAdapter.isEnabled()) {
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBluetoothIntent, SERVER_FINDER_BLUETOOTH_ENABLED);
            } else {
                search();
            }
        }
    }

    public void search() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                callbacks.onDeviceFound(device);
            }
        }
    };

    public void createServerConnection(BluetoothDevice device) {
        CreateConnection createConnection = new CreateConnection(device);
        createConnection.start();
    }

    public void close() {
        bluetoothAdapter.cancelDiscovery();
        try {
            activity.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) { }
    }

    private class CreateConnection extends Thread {
        private BluetoothDevice device;

        public CreateConnection(BluetoothDevice device) {
            this.device = device;
        }

        @Override
        public void run() {
            if(device != null) {
                try {
                    BluetoothSocket bluetoothSocket = device.createRfcommSocketToServiceRecord(BluetoothServerSharer.uuid);
                    bluetoothSocket.connect();
                    callbacks.onConnectionMade(new BluetoothServerConnection(bluetoothSocket));
                    return;
                } catch (IOException e) { }
            }

            callbacks.onConnectionFailed();
        }
    }

    public interface Callbacks {
        void onDeviceFound(BluetoothDevice device);
        void onConnectionMade(ServerConnection serverConnection);
        void onConnectionFailed();
    }
}
