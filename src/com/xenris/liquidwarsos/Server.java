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

import android.content.Context;
import java.io.*;
import java.util.*;

// Note: Need to call start on the server AFTER the first connection
//  is made otherwise the server will simply exit.

public class Server extends Thread {
    private ArrayList<ClientConnection> gClientConnections = new ArrayList<ClientConnection>();
    private GameState gGameState = new GameState();
    private DotSimulation gDotSimulation;
    private final Context gContext;

    public Server(Context context) {
        gContext = context;
    }

    @Override
    public void run() {
        setName("Server Game Loop Thread");

        while(gClientConnections.size() > 0) {
            getClientInfo();

            updateGameState();

            sendGameState();

//            if(gGameState.state() == GameState.MAIN_MENU) {
                // FIXME Need to be able to remove closed connections at all times
                //  but only remove players when the game isn't in play.
                removeClosedConnections();
//            }
        }
    }

    private void getClientInfo() {
        for(ClientConnection clientConnection : gClientConnections) {
            final ClientInfo clientInfo = clientConnection.getClientInfo();
            if(clientInfo != null) {
                gGameState.updateClientInfo(clientInfo);
            }
        }
    }

    private void updateGameState() {
        final int state = gGameState.state();

        if(state == GameState.IN_PLAY) {
            gGameState.preStep(gDotSimulation);
            for(int i = 0; i < Constants.STEP_MULTIPLIER; i++) {
                gGameState.step(gDotSimulation, true);
                // XXX This needs some sort of time management.
                Util.sleep(Constants.STEP_TIME_MS);
            }
        } else if(state == GameState.COUNTDOWN) {
            gGameState.state(GameState.IN_PLAY);

            Util.sleep(Constants.STEP_TIME_MS * Constants.STEP_MULTIPLIER);
        } else if(state == GameState.MAIN_MENU) {
            if(gDotSimulation != null) {
                gDotSimulation.delete();
                gDotSimulation = null;
            }

            // Check if everyone is ready to start the game.
            boolean everyoneIsReady = true;

            for(ClientConnection clientConnection : gClientConnections) {
                final ClientInfo clientInfo = clientConnection.getClientInfo();
                if(clientInfo != null) {
                    everyoneIsReady = everyoneIsReady && clientInfo.isReady();
                } else {
                    everyoneIsReady = false;
                }
            }

            if(everyoneIsReady) {
                gGameState.state(GameState.COUNTDOWN);
                final int playerCount = gGameState.getPlayerCount();
                final int[] colors = gGameState.getTeamColors();
                final int teamSize = gGameState.getTeamSize();
                final Map map = new Map(gContext, gGameState.getMapId());

                gDotSimulation = new DotSimulation(0, playerCount, colors, teamSize, map);
            }

            Util.sleep(Constants.STEP_TIME_MS * Constants.STEP_MULTIPLIER);
        }
    }

    public void sendGameState() {
        for(ClientConnection clientConnection : gClientConnections) {
            clientConnection.sendGameState(gGameState);
        }
    }

    public void removeClosedConnections() {
        ClientConnection toRemove = null;

        for(ClientConnection clientConnection : gClientConnections) {
            if(clientConnection.isClosed()) {
                toRemove = clientConnection;
                break;
            }
        }

        if(toRemove != null) {
            gGameState.removeClientInfo(toRemove.getConnectionId());
            gClientConnections.remove(toRemove);
            toRemove.close();
        }
    }

    public ServerConnection createConnection() {
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

        addClientConnection(new ClientConnection(oss, iss));

        return new ServerConnection(osc, isc);
    }

    public void addClientConnection(ClientConnection clientConnection) {
        final int id = clientConnection.getConnectionId();
        gGameState.addClientInfo(id);
        gClientConnections.add(clientConnection);
    }

    public DotSimulation getDotSimulation() {
        return gDotSimulation;
    }
}
