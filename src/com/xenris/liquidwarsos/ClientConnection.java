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

public class ClientConnection extends Thread {
    private int gConnectionId;
    private DataOutputStream gDataOutputStream;
    private DataInputStream gDataInputStream;
    private ClientInfo gClientInfoA;
    private ClientInfo gClientInfoB;
    private boolean gIsClosed = false;

    public ClientConnection() {
    }

    public ClientConnection(OutputStream outputStream, InputStream inputStream) {
        init(outputStream, inputStream);
    }

    public void init(OutputStream outputStream, InputStream inputStream) {
        gConnectionId = Util.getNextId();

        gDataOutputStream = new DataOutputStream(outputStream);
        gDataInputStream = new DataInputStream(inputStream);

        try {
            gDataOutputStream.writeInt(gConnectionId);
        } catch (IOException e) {
            Log.message(Log.tag, "Error: failed to write connection ID in ClientConnection");
            return;
        }

        start();
    }

    @Override
    public void run() {
        setName("ClientConnection");

        while(true) {
            try {
                gClientInfoB = new ClientInfo(gDataInputStream);
            } catch (IOException e) {
                break;
            }

            shiftClientInfos();
        }

        close();
    }

    public ClientInfo getClientInfo() {
        synchronized (this) {
            return gClientInfoA;
        }
    }

    private void shiftClientInfos() {
        synchronized (this) {
            gClientInfoA = gClientInfoB;
            gClientInfoB = null;
        }
    }

    public void sendGameState(GameState gameState) {
        try {
            gameState.write(gDataOutputStream);
        } catch (IOException e) {
            Log.message(Log.tag, "Error: failed to send game state in ClientConnection");
        }
    }

    public int getConnectionId() {
        return gConnectionId;
    }

    public void close() {
        gIsClosed = true;
        Util.close(gDataOutputStream);
        Util.close(gDataInputStream);
    }

    public boolean isClosed() {
        return gIsClosed;
    }
}
