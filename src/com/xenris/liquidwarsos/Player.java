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

// TODO Make AIPlayer to extend Player, or add isAI variable.
//  Also, make player become AI if they disconnect.

import java.io.*;

public class Player {
    private int gId;
    private int gColour;
    private boolean gIsReady;

    public Player(int id, int colour) {
        gId = id;
        gColour = colour;
        gIsReady = false;
    }

    public Player(int id, int colour, boolean isReady) {
        gId = id;
        gColour = colour;
        gIsReady = isReady;
    }

    public Player(int id) {
        gId = id;
        gColour = Colour.randomColour();
        gIsReady = false;
    }

    public void setValues(Player player) {
        gColour = player.getColour();
        gIsReady = player.isReady();
    }

    public boolean read(DataInputStream dataInputStream) {
        try {
            gId = dataInputStream.readInt();
            gColour = dataInputStream.readInt();
            gIsReady = dataInputStream.readBoolean();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public int getId() {
        return gId;
    }

    public int getColour() {
        return gColour;
    }

    public void setColour(int colour) {
        gColour = colour;
    }

    public boolean isReady() {
        return gIsReady;
    }

    public void isReady(boolean isReady) {
        gIsReady = isReady;
    }

    public boolean send(DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.writeInt(gId);
            dataOutputStream.writeInt(gColour);
            dataOutputStream.writeBoolean(gIsReady);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Player createNew(DataInputStream dataInputStream) {
        try {
            final int id = dataInputStream.readInt();
            final int colour = dataInputStream.readInt();
            final boolean isReady = dataInputStream.readBoolean();

            return new Player(id, colour, isReady);
        } catch (IOException e) { }

        return null;
    }
}
