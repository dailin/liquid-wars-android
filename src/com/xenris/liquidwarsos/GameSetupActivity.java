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

import android.app.*;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnLongClickListener;
import android.widget.*;
import java.io.*;
import java.util.ArrayList;

import com.xenris.liquidwarsos.ColourPickerDialog.ColourPickerListener;

// TODO Make a single game setup screen with a button to share this game and a
//  button to search for other games.

public class GameSetupActivity extends Activity implements ServerConnection.Callbacks {
    private final int editTextId = 1221;
    private BluetoothServerSharer bluetoothServerSharer; // TODO Move this to Server.
    private ServerConnection serverConnection;
    private PlayerListView playerListView;
    private View playerColourView;
    private TextView publicNameTextView;
    private Game gGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_setup);

        publicNameTextView = (TextView)findViewById(R.id.public_name_textview);
        playerListView = (PlayerListView)findViewById(R.id.player_list_view);
        playerColourView = findViewById(R.id.player_colour_view);

        MyApplication application = (MyApplication)getApplicationContext();
        serverConnection = application.getServerConnection();

        // If creating a new game.
        if(serverConnection == null) {
            Server localServer = new Server();
            serverConnection = localServer.createLocalConnection();
            serverConnection.registerCallbacks(this);

            bluetoothServerSharer = new BluetoothServerSharer(this, localServer);
            bluetoothServerSharer.enableBluetoothAndShare();
            publicNameTextView.setVisibility(View.VISIBLE);
            publicNameTextView.setText(bluetoothServerSharer.getPublicName());
            publicNameTextView.setOnLongClickListener(new PublicNameLongClick());
        } else {
            serverConnection.registerCallbacks(this);
        }
    }

    public void onColourClicked(View view) {
        ColourPickerListener listener = new ColourPickerListener() {
                @Override
                public void onSelect(ColourPickerDialog dialog, int colour) {
                    serverConnection.setColour(colour);
                }
            };
        Rect displayRectangle = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        final int w = displayRectangle.width();
        final int h = displayRectangle.height();
        final int smallSide = (w < h) ? w : h;
        final int currentColour = serverConnection.getColour();
        final int dialogSize = (int)(smallSide * 0.8);
        ColourPickerDialog colourPickerDialog = new ColourPickerDialog(this, currentColour, dialogSize, listener);
        colourPickerDialog.show();
    }

    public void onReadyClicked(View view) {
        final ToggleButton toggleButton = (ToggleButton)view;
        final boolean ready = toggleButton.isChecked();
        serverConnection.setReadyState(ready);
    }

    public void onMapClicked(View view) {
        // TODO Bring up a map selection window (like a scrollable gallery of maps).
        Log.message("TODO: onMapClicked");
    }

    @Override
    public void onGameAvailable() {
        updateUiOnUiThread();
    }

    public void updateUiOnUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUi();
            }
        });
    }

    private void updateUi() {
        gGame = serverConnection.getNextGameState();
        if(gGame != null) {
            final int myId = serverConnection.getId();
            final Player me = gGame.findPlayerById(myId);
            final int colour = me.getColour();
            updatePlayersList(gGame.getPlayers());
            playerColourView.setBackgroundColor(colour);
            final int countdown = gGame.getCountdownSeconds();
            // TODO Use countdown to display a pretty fullscreen countdown to start.
        }
    }

    private void updatePlayersList(ArrayList<Player> players) {
        playerListView.removeAllPlayers();
        for(Player player : players) {
            playerListView.setPlayer(player.getId(), player.getColour(), player.isReady());
        }
    }

    public PlayerListView getPlayerListView() {
        return playerListView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(bluetoothServerSharer != null) {
            bluetoothServerSharer.close();
            bluetoothServerSharer = null;
        }
        if(serverConnection != null) {
            serverConnection.close();
            serverConnection = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothServerSharer.SERVER_SHARER_BLUETOOTH_ENABLED) {
            bluetoothServerSharer.share();
        }
    }

    private class PublicNameLongClick implements OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            EditText editText = new EditText(GameSetupActivity.this);
            editText.setId(editTextId);
            editText.setText(publicNameTextView.getText());

            new AlertDialog.Builder(GameSetupActivity.this)
                .setTitle(R.string.change_bluetooth_name)
                .setPositiveButton(R.string.done, new ClickChangeName())
                .setNegativeButton(R.string.cancel, null)
                .setView(editText)
                .show();

            return true;
        }

        private class ClickChangeName implements OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AlertDialog alertDialog = (AlertDialog)dialog;
                final EditText editText = (EditText)alertDialog.findViewById(editTextId);
                final String newPublicName = editText.getText().toString();
                if(newPublicName.length() > 1) {
                    if(bluetoothServerSharer.setPublicName(newPublicName)) {
                        publicNameTextView.setText(newPublicName);
                    }
                }
            }
        }
    }
}
