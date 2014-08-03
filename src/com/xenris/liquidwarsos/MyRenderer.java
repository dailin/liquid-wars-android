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

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

public class MyRenderer implements GLSurfaceView.Renderer {
    private GameState gGameState;
    private ClientInfo gClientInfo;
    private DotSimulation gDotSimulation;
    private float gWidth;
    private float gHeight;
    private Sprite touchSprite;
    private Context gContext;

    public MyRenderer(Context context) {
        gContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_BLEND);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        touchSprite = new Sprite(gl, gContext, R.drawable.touch);
        touchSprite.scale(10, 10);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized(this) {
            gl.glClearColor(0, 0, 1, 1);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            gl.glLoadIdentity();

            if(gClientInfo != null) {
                touchSprite.draw(gl, gClientInfo.getX(), gClientInfo.getY()); // TODO Draw at each finger touch point.
            }

            if(gDotSimulation != null) {
                gDotSimulation.draw();
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gWidth = width;
        gHeight = height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glOrthof(0, Constants.WIDTH, Constants.HEIGHT, 0, -10, 10);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
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

    public void setDotSimulationToDraw(DotSimulation dotSimulation) {
        synchronized(this) {
            gDotSimulation = dotSimulation;
        }
    }
}
