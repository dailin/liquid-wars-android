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
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.EditText;
import android.view.View;
import android.view.Window;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnCancelListener;
import android.text.InputType;
import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;

public class MultiplayerMenuActivity extends Activity implements ServerFinder.Callbacks {
    private ArrayAdapter serverList;
    private AlertDialog searchAlertDialog;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private ServerFinder serverFinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.multiplayer_menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(serverFinder != null) {
            serverFinder.close();
            serverFinder = null;
        }
    }

    @Override
    public void onDeviceFound(final BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bluetoothDevices.add(device);
                serverList.add(device.getName());
            }
        });
    }

    @Override
    public void onConnectionMade(ServerConnection serverConnection) {
        MyApplication application = (MyApplication)getApplicationContext();
        application.putServerConnection(serverConnection);
        Intent intent = new Intent(this, GameSetupActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed() {
        Log.message("onConnectionFailed()");
    }

    public void connectToGame(View view) {
        if(serverFinder != null) {
            serverFinder.close();
            serverFinder = null;
        }
        serverFinder = new ServerFinder(this, this);
        serverFinder.enableBluetoothAndSearch();
        serverList = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        ListView listview = new ListView(this);
        listview.setAdapter(serverList);
        listview.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    searchAlertDialog.cancel();
                    BluetoothDevice bluetoothDevice = bluetoothDevices.get(position);
                    serverFinder.close();
                    serverFinder.createServerConnection(bluetoothDevice);
                    // TODO Create a dialog to show that the connection is
                    //  currently being made or has failed.
                }
            });

        OnClickListener clicker = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    serverFinder.close();
                }
            };
        OnCancelListener cancelListener = new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    serverFinder.close();
                }
            };

        bluetoothDevices = new ArrayList<BluetoothDevice>();

        searchAlertDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.searching)
            .setNegativeButton(R.string.cancel, clicker)
            .setOnCancelListener(cancelListener)
            .setView(listview)
            .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ServerFinder.SERVER_FINDER_BLUETOOTH_ENABLED) {
            serverFinder.search();
        }
    }

    public void startNewGame(View view) {
        Intent intent = new Intent(this, GameSetupActivity.class);
        startActivity(intent);
    }
}
