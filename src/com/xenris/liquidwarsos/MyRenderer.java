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

import android.opengl.GLSurfaceView;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

public class MyRenderer implements GLSurfaceView.Renderer {
    private GameState gGameState;
    private ClientInfo gClientInfo;
    private float gWidth;
    private float gHeight;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        synchronized(this) {
        }
//        NativeInterface.onSurfaceCreated();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(0, 0, 1, 1);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        synchronized(this) {
        }
//        NativeInterface.onDrawFrame();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        synchronized(this) {
            gWidth = width;
            gHeight = height;
        }
//        NativeInterface.onSurfaceChanged(width, height);
    }

    public void setGameStateToDraw(GameState gameState) {
        synchronized(this) {
            gGameState = gameState;
        }
    }

    public void setClientInfoToDraw(ClientInfo clientInfo) {
        synchronized(this) {
            gClientInfo = clientInfo;
        }
    }
}
