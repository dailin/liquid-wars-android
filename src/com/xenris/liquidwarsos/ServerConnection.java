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

public class ServerConnection extends Thread {
    private int gConnectionId;
    private DataOutputStream gDataOutputStream;
    private DataInputStream gDataInputStream;
    private LinkedList<GameState> gGameStateQueue = new LinkedList<GameState>();

    public ServerConnection() {
    }

    public ServerConnection(OutputStream outputStream, InputStream inputStream) {
        init(outputStream, inputStream);
    }

    protected void init(OutputStream outputStream, InputStream inputStream) {
        gDataOutputStream = new DataOutputStream(outputStream);
        gDataInputStream = new DataInputStream(inputStream);

        try {
            gConnectionId = gDataInputStream.readInt();
        } catch (IOException e) {
            Log.message(Log.tag, "Error: failed to get connection ID in ServerConnection");
            return;
        }
    }

    @Override
    public void run() {
        setName("ServerConnection");

        while(true) {
            GameState gameState = null;

            try {
                gameState = new GameState(gDataInputStream);
            } catch (IOException e) {
                break;
            }

            gGameStateQueue.add(gameState);
        }

        close();
    }

    public void sendClientInfo(ClientInfo clientInfo) {
        try {
            clientInfo.write(gDataOutputStream);
        } catch (IOException e) {
//            Log.message(Log.tag, "Error: failed to send player state in ServerConnection");
        }
    }

    public int getConnectionId() {
        return gConnectionId;
    }

    public void close() {
        Util.close(gDataOutputStream);
        Util.close(gDataInputStream);
    }

    public GameState getNextGameState() {
        return gGameStateQueue.poll();
    }
}
