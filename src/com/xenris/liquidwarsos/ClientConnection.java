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

public abstract class ClientConnection {
    private static int gNextId = 0;
    private int gId = -1;
    private Player gClientsPlayer = new Player(-1, 0, false);
    private DataOutputStream gDataOutputStream;
    private DataInputStream gDataInputStream;
    private boolean gIsActive = true;

    public ClientConnection() {
        // Extending classes need to call init manually.
    }

    public ClientConnection(InputStream inputStream, OutputStream outputStream) {
        init(inputStream, outputStream);
    }

    public void init(InputStream inputStream, OutputStream outputStream) {
        gDataOutputStream = new DataOutputStream(outputStream);
        gDataInputStream = new DataInputStream(inputStream);

        gId = gNextId++;

        try {
            gDataOutputStream.writeInt(gId);
        } catch (IOException e) {
            Log.message("Error: failed to write client id");
            close();
            return;
        }

        startReceiving();
    }

    private void startReceiving() {
        ReceivingThread thread = new ReceivingThread();
        thread.start();
    }

    private class ReceivingThread extends Thread {
        @Override
        public void run() {
            while(true) {
                synchronized (gClientsPlayer) {
                    if(!gClientsPlayer.read(gDataInputStream)) {
                        break;
                    }
                }
            }

            gIsActive = false;
            close();
        }
    }

    public void send(Game game) {
        game.send(gDataOutputStream);
    }

    public boolean isActive() {
        return gIsActive;
    }

    public Player getPlayer() {
        return gClientsPlayer;
    }

    public int getId() {
        return gId;
    }

    public void close() {
        Util.close(gDataInputStream);
        Util.close(gDataOutputStream);
    }
}
