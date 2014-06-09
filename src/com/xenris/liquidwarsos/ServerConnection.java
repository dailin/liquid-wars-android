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

import java.io.*;
import java.util.*;

public abstract class ServerConnection {
    private int gId = -1;
    private LinkedList<Game> gGameBuffer = new LinkedList<Game>();
    private Player gLocalPlayer = new Player(-1);
    private DataOutputStream gDataOutputStream;
    private DataInputStream gDataInputStream;
    private Callbacks gCallbacks;

    public ServerConnection() {
        // Extending classes need to call init manually.
    }

    public ServerConnection(InputStream inputStream, OutputStream outputStream) {
        init(inputStream, outputStream);
    }

    public void init(InputStream inputStream, OutputStream outputStream) {
        gDataOutputStream = new DataOutputStream(outputStream);
        gDataInputStream = new DataInputStream(inputStream);

        startReceiving();
        startSending();
    }

    public void registerCallbacks(Callbacks callbacks) {
        gCallbacks = callbacks;
    }

    public void setColour(int colour) {
        synchronized (gLocalPlayer) {
            gLocalPlayer.setColour(colour);
        }
    }

    public int getColour() {
        return gLocalPlayer.getColour();
    }

    public void setReadyState(boolean ready) {
        synchronized (gLocalPlayer) {
            gLocalPlayer.isReady(ready);
        }
    }

    private void startReceiving() {
        ReceivingThread thread = new ReceivingThread();
        thread.start();
    }

    private void startSending() {
        SendingThread thread = new SendingThread();
        thread.start();
    }

    private class ReceivingThread extends Thread {
        @Override
        public void run() {
            try {
                gId = gDataInputStream.readInt();
            } catch (IOException e) {
                Log.message("Error: failed to get client id");
                return;
            }

            while(true) {
                final Game game = Game.createNew(gDataInputStream);

                if(game != null) {
                    // TODO check that this game is in squential order.
                    if(gGameBuffer.offer(game)) {
                        if(gCallbacks != null) {
                            gCallbacks.onGameAvailable();
                        }
                    } else {
                        Log.message("Error: failed to add game to game buffer");
                        break;
                    }
                } else {
                    break;
                }
            }

            Log.message("ServerConnection ReceivingThread closed");
        }
    }

    // XXX This might not need to be a thread....?
    private class SendingThread extends Thread {
        @Override
        public void run() {
            while(true) {
                boolean sendSuccess;

                synchronized (gLocalPlayer) {
                    sendSuccess = gLocalPlayer.send(gDataOutputStream);
                }

                if(!sendSuccess) {
                    break;
                }

                // Only send about ten times per second.
                Util.sleep(100);
            }

            Log.message("ServerConnection SendingThread closed");
        }
    }

    public int getId() {
        return gId;
    }

    public Game getNextGameState() {
        return gGameBuffer.poll();
    }

    public void close() {
        Util.close(gDataInputStream);
        Util.close(gDataOutputStream);
    }

    public interface Callbacks {
        void onGameAvailable();
    }
}
