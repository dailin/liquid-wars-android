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

import android.content.*;
import android.graphics.*;
import android.view.*;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameState gGameState;
    private ClientInfo gClientInfo;
    private RenderThread gRenderThread;
    private float gWidth;
    private float gHeight;

    public GameView(Context context) {
        super(context);

        getHolder().addCallback(this);
    }

    private class RenderThread extends Thread {
        private boolean running;

        @Override
        public void run() {
            setName("RenderThread");

            running = true;

            while(running) {
                synchronized (this) {
                    if(gGameState != null) {
                        final Canvas canvas = getHolder().lockCanvas();
                        if(canvas != null) {
                            onDraw(canvas);
                            getHolder().unlockCanvasAndPost(canvas);
                        }
                    }
                }

                Thread.yield();
            }
        }

        public void close() {
            running = false;
        }
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

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        gRenderThread = new RenderThread();
        gRenderThread.start();
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        gWidth = width;
        gHeight = height;
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        gRenderThread.close();
        Util.join(gRenderThread);
    }

    public void onDraw(Canvas canvas) {
        final float sw = gWidth / 800;
        final float sh = gHeight / 480;
        canvas.scale(sw, sh, 0, 0);

        if(gGameState != null) {
            canvas.drawColor(Color.GREEN);
            gGameState.draw(canvas);
        } else {
            canvas.drawColor(Color.RED);
        }

        if(gClientInfo != null) {
            gClientInfo.draw(canvas);
        } else {
            canvas.drawColor(Color.YELLOW);
        }
    }
}
