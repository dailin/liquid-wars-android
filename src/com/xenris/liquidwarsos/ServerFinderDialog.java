//    This file is part of Liquid Wars.
//
//    Copyright (C) 2013-2014 Henry Shepperd (hshepperd@gmail.com)
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

import android.app.AlertDialog;
import android.bluetooth.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import java.util.*;

public class ServerFinderDialog extends AlertDialog
    implements
        OnItemClickListener,
        OnCancelListener,
        Bluetooth.Callbacks {

    private ArrayList<BluetoothDevice> gBluetoothDevices;
    private ArrayAdapter serverList;
    private Callbacks gCallbacks;
    private Bluetooth gBluetooth;

    public ServerFinderDialog(Context context, Callbacks callbacks, Bluetooth bluetooth) {
        super(context);

        gCallbacks = callbacks;
        gBluetooth = bluetooth;

        if(bluetooth.isBluetoothEnabled()) {
            setTitle(context.getResources().getString(R.string.searching));
            setupDialog(context);
            bluetooth.startSearching(this);
        } else {
            setTitle(context.getResources().getString(R.string.enable_bluetooth_first));
        }
    }

    public void setupDialog(Context context) {
        gBluetoothDevices = gBluetooth.getDevices();

        final ArrayList<String> stringList = new ArrayList<String>();
        final int simple = android.R.layout.simple_list_item_1;
        serverList = new ArrayAdapter(context, simple, stringList);
        for(BluetoothDevice device : gBluetoothDevices) {
            stringList.add(device.getName());
        }

        final ListView listView = new ListView(context);
        listView.setAdapter(serverList);
        listView.setOnItemClickListener(this);

        setOnCancelListener(this);
        setView(listView);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        gBluetooth.stopsearching();
        gCallbacks.onNothingSelected(this);
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        gBluetooth.stopsearching();
        dismiss();
        final BluetoothDevice bluetoothDevice = gBluetoothDevices.get(position);
        gCallbacks.onServerSelected(this, bluetoothDevice);
    }

    public void onDeviceFound(final BluetoothDevice bluetoothDevice) {
        gBluetoothDevices.add(bluetoothDevice);
        serverList.add(bluetoothDevice.getName());
    }

    public interface Callbacks {
        public void onServerSelected(ServerFinderDialog dialog, BluetoothDevice device);
        public void onNothingSelected(ServerFinderDialog dialog);
    }
}
