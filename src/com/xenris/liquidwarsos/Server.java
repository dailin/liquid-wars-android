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

import android.bluetooth.BluetoothSocket;
import java.util.ArrayList;
import java.io.*;

public class Server {
    private Game gGame = new Game();
    private ArrayList<ClientConnection> gClientConnections = new ArrayList<ClientConnection>();

    public Server() {
        GameThread gameThread = new GameThread();
        gameThread.start();
    }

    public ServerConnection createLocalConnection() {
        // s = for server
        // c = for client
        final PipedOutputStream oss = new PipedOutputStream();
        final PipedOutputStream osc = new PipedOutputStream();
        final PipedInputStream iss = new PipedInputStream();
        final PipedInputStream isc = new PipedInputStream();

        try {
            oss.connect(isc);
            iss.connect(osc);
        } catch (IOException e) {
            return null;
        }

        addClientConnection(new LocalClientConnection(iss, oss));

        return new LocalServerConnection(isc, osc);
    }

    public void addClientConnection(ClientConnection clientConnection) {
        synchronized (gClientConnections) {
            gClientConnections.add(clientConnection);
        }

        Player player = new Player(clientConnection.getId());
        gGame.addPlayer(player);
    }

    public void removeClientConnection(ClientConnection clientConnection) {
        if(clientConnection != null) {
            synchronized (gClientConnections) {
                gClientConnections.remove(clientConnection);
            }

            gGame.removePlayer(clientConnection.getId());
        }
    }

    // XXX Should this be in Game?
    public class GameThread extends Thread {
        @Override
        public void run() {
            // Wait for one minute for someone to join.
            int counter = 60 * 10;
            while(counter > 0) {
                if(gClientConnections.size() > 0) {
                    break;
                } else {
                    Util.sleep(100);
                }
            }

            while(gClientConnections.size() > 0) {
                for(ClientConnection clientConnection : gClientConnections) {
                    if(clientConnection.isActive()) {
                        Player player = gGame.findPlayerById(clientConnection.getId());
                        player.setValues(clientConnection.getPlayer());
                        clientConnection.send(gGame);
                    }
                }

                gGame.step();

                ClientConnection toBeRemoved = null;

                for(ClientConnection clientConnection : gClientConnections) {
                    if(clientConnection.isActive()) {
                        clientConnection.send(gGame);
                    } else {
                        toBeRemoved = clientConnection;
                    }
                }

                removeClientConnection(toBeRemoved);

                Util.sleep(100);
            }
        }
    }
}
