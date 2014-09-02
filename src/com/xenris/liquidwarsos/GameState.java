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

import android.graphics.*;
import java.io.*;
import java.util.*;

public class GameState {
    public static final int MAIN_MENU = 1; // TODO Change this to GAME_MENU.
    public static final int COUNTDOWN = 2;
    public static final int IN_PLAY = 3;

    private ArrayList<ClientInfo> gClientInfos;
    private int gState = MAIN_MENU;
    private long gStepNumber = 0;

    public GameState() {
        gClientInfos = new ArrayList<ClientInfo>();
    }

    public GameState(DataInputStream dataInputStream) throws IOException {
        gClientInfos = new ArrayList<ClientInfo>();

        final int playerCount = dataInputStream.readInt();

        for(int i = 0; i < playerCount; i++) {
            gClientInfos.add(new ClientInfo(dataInputStream));
        }

        gState = dataInputStream.readInt();
        gStepNumber = dataInputStream.readLong();
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(gClientInfos.size());

        for(ClientInfo playerState : gClientInfos) {
            playerState.write(dataOutputStream);
        }

        dataOutputStream.writeInt(gState);
        dataOutputStream.writeLong(gStepNumber);
    }

    public void addClientInfo(int id) {
        gClientInfos.add(new ClientInfo(id));
    }

    public void removeClientInfo(int id) {
        final ClientInfo clientInfo = findClientInfoById(id);

        gClientInfos.remove(clientInfo);
    }

    public void updateClientInfo(ClientInfo newClientInfo) {
        final int id = newClientInfo.getId();
        final ClientInfo playerState = findClientInfoById(id);
        if(playerState != null) {
            playerState.setValues(newClientInfo);
        }
    }

    public ClientInfo findClientInfoById(int id) {
        for(ClientInfo playerState : gClientInfos) {
            if(playerState.getId() == id) {
                return playerState;
            }
        }

        return null;
    }

    // XXX This shouldn't really be here. Out of GameState's scope.
    public void updatePlayersList(PlayerListView playerListView) {
        playerListView.removeAllPlayers();

        for(ClientInfo clientInfo : gClientInfos) {
            playerListView.setPlayer(clientInfo.getId(), clientInfo.getColor(), clientInfo.isReady());
        }
    }

    public void preStep(DotSimulation dotSimulation) {
        if(dotSimulation != null) {
            final int numPlayers = gClientInfos.size();

            for(int i = 0; i < numPlayers; i++) {
                final ClientInfo clientInfo = gClientInfos.get(i);
                dotSimulation.setPlayerPosition(i, clientInfo.getX(), clientInfo.getY());
            }
        }
    }

    public void step(DotSimulation dotSimulation, boolean isServer) {
        if(isServer) {
            gStepNumber++;
        }

        if(dotSimulation != null) {
            if(dotSimulation.getStepNumber() < gStepNumber) {
                dotSimulation.step();
            }
        }
    }

    public int state() {
        return gState;
    }

    public void state(int state) {
        gState = state;
    }

    public long getStepNumber() {
        return gStepNumber;
    }

    public int getPlayerCount() {
        return gClientInfos.size();
    }

    public int[] getTeamColors() {
        final int playerCount = getPlayerCount();
        int[] colors = new int[playerCount];

        for(int i = 0; i < playerCount; i++) {
            colors[i] = gClientInfos.get(i).getColor();
        }

        return colors;
    }

    public int getTeamSize() {
        return 400;
    }
}
