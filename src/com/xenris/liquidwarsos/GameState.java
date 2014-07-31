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

import android.graphics.*;
import java.io.*;
import java.util.*;

public class GameState {
    public static final int MAIN_MENU = 1;
    public static final int COUNTDOWN = 2;
    public static final int IN_PLAY = 3;

    private ArrayList<ClientInfo> gClientInfos;
    private int gState = MAIN_MENU;

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
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(gClientInfos.size());

        for(ClientInfo playerState : gClientInfos) {
            playerState.write(dataOutputStream);
        }

        dataOutputStream.writeInt(gState);
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

    public void updatePlayersList(PlayerListView playerListView) {
        playerListView.removeAllPlayers();

        for(ClientInfo clientInfo : gClientInfos) {
            playerListView.setPlayer(clientInfo.getId(), clientInfo.getColor(), clientInfo.isReady());
        }
    }

    public void draw(Canvas canvas) {
        for(ClientInfo playerState : gClientInfos) {
            playerState.draw(canvas);
        }
    }

    public int state() {
        return gState;
    }

    public void state(int state) {
        gState = state;
    }
}
