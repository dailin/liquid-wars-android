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
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.MotionEvent;
import java.io.*;

public class MapSelectorView extends View {
    private Context gContext;
    private MapSelectorCallbacks gCallbacks;
    private int gWidth;
    private int gHeight;
    private int gCurrentMapId;
    private Bitmap gCurrentBitmap;
    private Bitmap gLeftBitmap;
    private Bitmap gRightBitmap;
    private Rect bitmapDestRect = new Rect();
    private Paint gPaint;
    private float gPosition = 0; // range -1 to 1. 1 = next; -1 = previous
    private float gVelocity = 0;
    private final int VELOCITIES_COUNT = 3;
    private float[] gVelocities = new float[VELOCITIES_COUNT];
    private int gVelocitiesPointer = 0;
    private float gPreviousX;

    public MapSelectorView(Context context, MapSelectorCallbacks callbacks, int currentMapId) {
        super(context);

        gContext = context;
        gCallbacks = callbacks;
        gCurrentMapId = currentMapId;
        gPaint = new Paint();

        gLeftBitmap = loadBitmap(gContext, currentMapId - 1);
        gCurrentBitmap = loadBitmap(gContext, currentMapId);
        gRightBitmap = loadBitmap(gContext, currentMapId + 1);
    }

    public static Bitmap loadBitmap(Context context, int mapId) {
        if(mapId < 0) {
            return null;
        }

        InputStream inputStream = null;

        try {
            inputStream = context.getAssets().open(Map.toImagePath(mapId));
        } catch(IOException e) {
            try {
                inputStream = context.getAssets().open(Map.toMapPath(mapId));
            } catch(IOException e2) {
                return null;
            }
        }

        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        try {
            inputStream.close();
        } catch(IOException e) { }

        return bitmap;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        if(gLeftBitmap == null) {
            if(gPosition > 0) {
                gPosition = 0;
                gVelocity = 0;
                invalidate();
            }
        }

        if(gRightBitmap == null) {
            if(gPosition < 0) {
                gPosition = 0;
                gVelocity = 0;
                invalidate();
            }
        }

        bitmapDestRect.offsetTo((int)(gPosition * gWidth), 0);
        if(gCurrentBitmap != null) {
            canvas.drawBitmap(gCurrentBitmap, null, bitmapDestRect, gPaint);
        }

        bitmapDestRect.offsetTo((int)(gPosition * gWidth - gWidth), 0);
        if(gLeftBitmap != null) {
            canvas.drawBitmap(gLeftBitmap, null, bitmapDestRect, gPaint);
        }

        bitmapDestRect.offsetTo((int)(gPosition * gWidth + gWidth), 0);
        if(gRightBitmap != null) {
            canvas.drawBitmap(gRightBitmap, null, bitmapDestRect, gPaint);
        }

        if(gVelocity != 0) {
            gPosition += gVelocity;
            gVelocity /= 1.04;

            if(Math.abs(gVelocity) < 0.0001) {
                gVelocity = 0;
            }

            invalidate();
        }

        if(gPosition < -0.5f) {
            loadNextImage();
        } else if(gPosition > 0.5f) {
            loadPreviousImage();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        final int action = motionEvent.getActionMasked();

        final float x = motionEvent.getX();
        final float y = motionEvent.getY();

        final float xDiff = x - gPreviousX;

        if(action == MotionEvent.ACTION_DOWN) {
            gPreviousX = x;
            for(int i = 0; i < VELOCITIES_COUNT; i++) {
                gVelocities[i] = 0;
            }
        } else if(action == MotionEvent.ACTION_UP) {
            gVelocity = maxVelocity();

            gPreviousX = 0;
        } else if(action == MotionEvent.ACTION_MOVE) {
            gPosition += xDiff / (float)gWidth;
            gVelocity = 0;

            gVelocities[gVelocitiesPointer] = xDiff / (float)gWidth;
            gVelocitiesPointer = (gVelocitiesPointer + 1) % VELOCITIES_COUNT;

            gPreviousX = x;
        }

        invalidate();

        return true;
    }

    private void loadNextImage() {
        gCurrentMapId++;

        if(gLeftBitmap != null) {
            gLeftBitmap.recycle();
        }

        gLeftBitmap = gCurrentBitmap;
        gCurrentBitmap = gRightBitmap;
        gRightBitmap = loadBitmap(gContext, gCurrentMapId + 1);

        gPosition += 1f;

        gCallbacks.onChange(gCurrentMapId);
    }

    private void loadPreviousImage() {
        if(gCurrentMapId <= 0) {
            return;
        }

        gCurrentMapId--;

        if(gRightBitmap != null) {
            gRightBitmap.recycle();
        }

        gRightBitmap = gCurrentBitmap;
        gCurrentBitmap = gLeftBitmap;
        gLeftBitmap = loadBitmap(gContext, gCurrentMapId - 1);

        gPosition -= 1f;

        gCallbacks.onChange(gCurrentMapId);
    }

    private float maxVelocity() {
        float max = 0;

        for(int i = 0; i < VELOCITIES_COUNT; i++) {
            if(Math.abs(max) < Math.abs(gVelocities[i])) {
                max = gVelocities[i];
            }
        }

        return max;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        gWidth = w;
        gHeight = h;
        bitmapDestRect.set(0, 0, w, h);
    }

    public interface MapSelectorCallbacks {
        public void onChange(int mapId);
    }
}
