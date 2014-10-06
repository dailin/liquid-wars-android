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

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.graphics.*;
import android.opengl.GLSurfaceView;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.xenris.liquidwarsos.Bluetooth.CreateConnectionCallbacks;
import com.xenris.liquidwarsos.ColorPickerDialog.ColorPickerListener;
import java.util.*;

public class Client extends BaseActivity
    implements
        CreateConnectionCallbacks,
        Runnable,
        View.OnTouchListener {

    public Server gServer; // XXX Idealy this won't be needed. Just an extra thing to be null unexpectedly.
    private ServerConnection gServerConnection;
    private ClientInfo gMe;

    private boolean gRunning;
    private Thread gGameThread;

    private View gMenuView;
    private PlayerListView gPlayerListView;
    private GLSurfaceView gGLSurfaceView;
    private MyRenderer gRenderer;

    private DotSimulation gDotSimulation;

    private Bluetooth gBluetooth;

    private AlertDialog gSearchAlertDialog;
    private ArrayList<BluetoothDevice> gBluetoothDevices;

    private int backButtonCount = 0;
    private long backButtonPreviousTime = 0;
    private boolean backButtonMessageHasBeenShown = false;

    private boolean enableBluetoothMessageHasBeenShow = false;

    private int gCurrentMapId = 0;
    private Bitmap gMapBitmap;

    @Override
    public void onCreate() {
        super.onCreate();

        gGLSurfaceView = new GLSurfaceView(this);
        gRenderer = new MyRenderer(this);

        gGLSurfaceView.setRenderer(gRenderer);
        gGLSurfaceView.setVisibility(View.GONE);
        addView(gGLSurfaceView);

        gMenuView = addView(R.layout.game_menu);

        gGLSurfaceView.setOnTouchListener(this);

        setupHandlers();

        gServer = new Server(this);
        gServerConnection = gServer.createConnection();
        gServerConnection.start();
        gServer.start();
        gMe = new ClientInfo(gServerConnection.getConnectionId(), ColorUtil.randomColor(), true);
        gServerConnection.setClientInfoToSend(gMe);
        gRenderer.setClientInfoToDraw(gMe);

        gGameThread = new Thread(this);
        gGameThread.setName("Client Game Loop Thread");
        gGameThread.start();

        gBluetooth = new Bluetooth(this);

        setPublicNameTextView(gBluetooth.getPublicName());

        gPlayerListView = (PlayerListView)findViewById(R.id.player_list_view);
    }

    private void setupHandlers() {
        final MyApplication application = (MyApplication)getApplication();

        application.setUiHandler(new Handler(gUiHandlerCallback));
    }

    private Handler.Callback gUiHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch(message.what) {
                case Constants.SWITCH_TO_GAME_MENU:
                    gMenuView.setVisibility(View.VISIBLE);
                    gGLSurfaceView.setVisibility(View.GONE);
                    break;
                case Constants.SWITCH_TO_GAME_VIEW:
                    gMenuView.setVisibility(View.GONE);
                    gGLSurfaceView.setVisibility(View.VISIBLE);
                    break;
                case Constants.CONNECTION_MADE:
                    Toast.makeText(Client.this, "Connection made!", Toast.LENGTH_SHORT).show();
                    setPublicNameTextView((String)message.obj);
                    final Button shareButton = (Button)findViewById(R.id.share_button);
                    shareButton.setEnabled(false);
                    final Button findButton = (Button)findViewById(R.id.find_button);
                    findButton.setEnabled(false);
                    break;
                case Constants.CONNECTION_FAILED:
                    Toast.makeText(Client.this, "Connection failed", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.UPDATE_UI:
                    final GameState gameState = (GameState)message.obj;
                    final ClientInfo tempMe = gameState.findClientInfoById(gMe.getId());
                    setPlayerColorView(tempMe.getColor());
                    gameState.updatePlayersList(gPlayerListView);
                    gCurrentMapId = gameState.getMapId();
                    final ImageView imageView = (ImageView)findViewById(R.id.map_imageview);
                    if(gMapBitmap != null) {
                        gMapBitmap.recycle();
                    }
                    gMapBitmap = MapSelectorView.loadBitmap(Client.this, gCurrentMapId);
                    imageView.setImageBitmap(gMapBitmap);
            }

            return true;
        }
    };

    @Override
    public void onDestroy() {
        gBluetooth.stopSharing();

        gRunning = false;

        try {
            gGameThread.join();
        } catch(InterruptedException e) { }

        super.onDestroy();
    }

    public void buttonHandler(View view) {
        final int id = view.getId();

        if(id == R.id.share_button) {
            share();
        } else if(id == R.id.find_button) {
            find();
        } else if(id == R.id.ready_button) {
            final boolean ready = ((ToggleButton)view).isChecked();
            gMe.setReady(ready);
        } else if(id == R.id.player_color_view) {
            changeColor();
        } else if(id == R.id.map_imageview) {
            changeMap();
        }
    }

    @Override
    public void run() {
        gRunning = true;
        GameState gameState = null;
        final MyApplication application = (MyApplication)getApplication();
        final Handler uiHandler = application.getUiHandler();

        while(gRunning) {
            gameState = gServerConnection.getNextGameState();
            if(gameState == null) {
                Thread.yield();
                continue;
            }

            gRenderer.setGameStateToDraw(gameState);

            final int state = gameState.state();

            if(state == GameState.GAME_MENU) {
                if(!gameMenuIsVisible()) {
                    uiHandler.sendEmptyMessage(Constants.SWITCH_TO_GAME_MENU);
                }

                uiHandler.sendMessage(uiHandler.obtainMessage(Constants.UPDATE_UI, gameState));
            } else if(state == GameState.COUNTDOWN) {
                if(gDotSimulation == null) {
                    if(gServer != null) {
                        gDotSimulation = gServer.getDotSimulation();
                    } else {
                        final int playerCount = gameState.getPlayerCount();
                        final int[] colors = gameState.getTeamColors();
                        final int teamSize = gameState.getTeamSize();
                        final Map map = new Map(this, gameState.getMapId());

                        gDotSimulation = new DotSimulation(0, playerCount, colors, teamSize, map);

                    }

                    gRenderer.setMapToDraw(gDotSimulation.getMap());
                    gRenderer.setDotSimulationToDraw(gDotSimulation);
                }
            } else if(state == GameState.IN_PLAY) {
                if(gameMenuIsVisible()) {
                    uiHandler.sendEmptyMessage(Constants.SWITCH_TO_GAME_VIEW);
                    gMe.setReady(false);
                }

                if(gServer == null) {
                    gameState.step(gDotSimulation, false);
                }
            }

            Thread.yield();
//            Util.sleep(15); // XXX Bad time regulation.
        }

        if(gDotSimulation != null) {
            gDotSimulation.delete();
        }

        gServerConnection.close();
    }

    public boolean onTouch(View view, MotionEvent event) {
        final int action = event.getActionMasked();
        final float x = event.getX() / gGLSurfaceView.getWidth();
        final float y = event.getY() / gGLSurfaceView.getHeight();

        if(action == MotionEvent.ACTION_MOVE) {
            // XXX gMe probably needs to be synchronized.
            gMe.setX(x);
            gMe.setY(y);
        }

        return true;
    }

    private boolean gameMenuIsVisible() {
        return gMenuView.getVisibility() == View.VISIBLE;
    }

    private void share() {
        if(gBluetooth.isBluetoothEnabled()) {
            final Bluetooth.SharingCallbacks callbacks = new Bluetooth.SharingCallbacks() {
                @Override
                public void sharingSucceeded() {
                    final Button findButton = (Button)findViewById(R.id.find_button);
                    findButton.setEnabled(false);
                }

                @Override
                public void sharingFailed() {
                }
            };

            gBluetooth.startSharing(gServer, callbacks);
        } else {
            if(!enableBluetoothMessageHasBeenShow) {
                Toast.makeText(this, "Enable bluetooth first", Toast.LENGTH_SHORT).show();
                enableBluetoothMessageHasBeenShow = true;
            }
        }
    }

    private void find() {
        if(!gBluetooth.isBluetoothEnabled()) {
            if(!enableBluetoothMessageHasBeenShow) {
                Toast.makeText(this, "Enable bluetooth first", Toast.LENGTH_SHORT).show();
                enableBluetoothMessageHasBeenShow = true;
            }
            return;
        }

        final ServerFinderDialog.Callbacks callbacks = new ServerFinderDialog.Callbacks() {
            @Override
            public void onServerSelected(ServerFinderDialog dialog, BluetoothDevice device) {
                // TODO Show "connecting" progress dialog.
                gBluetooth.connect(device, Client.this);
            }

            @Override
            public void onNothingSelected(ServerFinderDialog dialog) {
            }
        };

        final ServerFinderDialog dialog = new ServerFinderDialog(this, callbacks, gBluetooth);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(gBluetooth.handleActivityResult(requestCode, resultCode, data)) {
            return;
        } else {
            // Handle other results.
        }
    }

    public void changeColor() {
        ColorPickerListener listener = new ColorPickerListener() {
                @Override
                public void onSelect(ColorPickerDialog dialog, int color) {
                    gMe.setColor(color);
                }

                @Override
                public void onChange(ColorPickerDialog dialog, int color) {
                    gMe.setColor(color);
                }
            };
        Rect displayRectangle = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        final int w = displayRectangle.width();
        final int h = displayRectangle.height();
        final int smallSide = (w < h) ? w : h;
        final int currentColor = gMe.getColor();
        final int dialogSize = (int)(smallSide * 0.8);
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, currentColor, dialogSize, listener);
        colorPickerDialog.show();
    }

    public void changeMap() {
        // Only have control over the map if this client started this game.
        if(gServer == null) {
            return;
        }

        MapSelectorView.MapSelectorCallbacks callback = new MapSelectorView.MapSelectorCallbacks() {
                @Override
                public void onChange(int mapId) {
                    gServer.setMap(mapId);
                }
            };
        Rect displayRectangle = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        final int w = (int)(displayRectangle.width() * 0.8);
        final int h = (int)(displayRectangle.height() * 0.8);
        final MapSelectorDialog mapSelectorDialog = new MapSelectorDialog(this, callback, w, h, gCurrentMapId);
        mapSelectorDialog.show();
    }

    @Override
    public void onConnectionMade(BluetoothServerConnection bluetoothServerConnection, String serverName) {
        gServer = null;

        gServerConnection.close();
        gServerConnection = bluetoothServerConnection;
        gServerConnection.start();
        gMe.setId(gServerConnection.getConnectionId());
        gServerConnection.setClientInfoToSend(gMe);
        gRenderer.setClientInfoToDraw(gMe);

        final MyApplication application = (MyApplication)getApplication();
        final Handler uiHandler = application.getUiHandler();
        final String str = "Connected to " + serverName;
        final Message message = uiHandler.obtainMessage(Constants.CONNECTION_MADE, str);
        uiHandler.sendMessage(message);
    }

    @Override
    public void onConnectionFailed() {
        final MyApplication application = (MyApplication)getApplication();
        final Handler uiHandler = application.getUiHandler();
        uiHandler.sendEmptyMessage(Constants.CONNECTION_FAILED);
    }

    @Override
    public void onBackPressed() {
        final long currentTime = System.currentTimeMillis();
        final long timeDiff = currentTime - backButtonPreviousTime;

        backButtonPreviousTime = currentTime;

        if((timeDiff < Constants.BACK_PRESS_DELAY) || (backButtonCount == 0)) {
            backButtonCount++;
        } else {
            backButtonCount = 1;
        }

        if(backButtonCount >= Constants.BACK_PRESS_COUNT) {
            finish();
        }

        if(!backButtonMessageHasBeenShown) {
            final String msg = "Press back " + Constants.BACK_PRESS_COUNT + " times to exit";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            backButtonMessageHasBeenShown = true;
        }
    }

    private void setPublicNameTextView(String string) {
        final TextView tv = (TextView)findViewById(R.id.public_name_textview);
        tv.setText(string);
    }

    private void setPlayerColorView(int color) {
        final View view = findViewById(R.id.player_color_view);
        view.setBackgroundColor(color);
    }
}
