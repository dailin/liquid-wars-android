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

public class ClientInfo {
    private static final Paint gPaint = new Paint();
    private int gId;
    private float gX = 0.5f;
    private float gY = 0.5f;
    private int gColor;
    private boolean gReady = false;
    private boolean gIsClients = false;

    public ClientInfo(int id) {
        gId = id;
        gColor = Color.BLUE;
    }

    public ClientInfo(int id, int colour) {
        gId = id;
        gColor = colour;
    }

    public ClientInfo(int id, int colour, boolean isClients) {
        gId = id;
        gColor = colour;
        gIsClients = isClients;
    }

    public int getId() {
        return gId;
    }

    public void setId(int id) {
        synchronized(this) {
            gId = id;
        }
    }

    public float getX() {
        return gX;
    }

    public void setX(float x) {
        synchronized(this) {
            gX = x;
        }
    }

    public float getY() {
        return gY;
    }

    public void setY(float y) {
        synchronized(this) {
            gY = y;
        }
    }

    public int getColor() {
        return gColor;
    }

    public void setColor(int color) {
        synchronized(this) {
            gColor = color;
        }
    }

    public boolean isReady() {
        return gReady;
    }

    public void setReady(boolean ready) {
        synchronized(this) {
            gReady = ready;
        }
    }

    public ClientInfo(DataInputStream dataInputStream) throws IOException {
        synchronized(this) {
            gId = dataInputStream.readInt();
            gX = dataInputStream.readFloat();
            gY = dataInputStream.readFloat();
            gColor = dataInputStream.readInt();
            gReady = dataInputStream.readBoolean();
        }
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        synchronized(this) {
            dataOutputStream.writeInt(gId);
            dataOutputStream.writeFloat(gX);
            dataOutputStream.writeFloat(gY);
            dataOutputStream.writeInt(gColor);
            dataOutputStream.writeBoolean(gReady);
        }
    }

    public void setValues(ClientInfo otherClientInfo) {
        synchronized(this) {
            gX = otherClientInfo.getX();
            gY = otherClientInfo.getY();
            gColor = otherClientInfo.getColor();
            gReady = otherClientInfo.isReady();
        }
    }
}
