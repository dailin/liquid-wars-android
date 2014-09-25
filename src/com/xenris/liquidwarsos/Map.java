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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.*;
import javax.microedition.khronos.opengles.GL10;

public class Map {
    private final int gId;
    private final Context gContext;
    private Bitmap gBitmap;
    private final int gWidth;
    private final int gHeight;

    public Map(Context context, int id) {
        gId = id;
        gContext = context;

        InputStream inputStream = null;

        try {
            inputStream = gContext.getAssets().open(toAssetPath(id));
        } catch(IOException e) {
            Log.message("Failed to open asset named " + toAssetPath(id));
        }

        gBitmap = BitmapFactory.decodeStream(inputStream);
        gWidth = gBitmap.getWidth();
        gHeight = gBitmap.getHeight();
    }

    public boolean[] createWallMap() {
        boolean[] result = new boolean[gWidth * gHeight];

        int i = 0;
        for(int y = 0; y < gHeight; y++) {
            for(int x = 0; x < gWidth; x++) {
                result[i] = isWhite(gBitmap.getPixel(x, y));
                i++;
            }
        }

        return result;
    }

    private boolean isWhite(int pixel) {
        final int red = (pixel & 0xff0000) >> 16;
        final int green = (pixel & 0x00ff00) >> 8;
        final int blue = (pixel & 0x0000ff) >> 0;

        final int mid = 0xff / 6;

        return (red > mid) && (green > mid) && (blue > mid);
    }

    public Sprite createSprite(GL10 gl) {
        return new Sprite(gl, gBitmap);
    }

    public static String toAssetPath(int id) {
        return "maps/" + id + "-map.png";
    }

    public int getWidth() {
        return gWidth;
    }

    public int getHeight() {
        return gHeight;
    }
}
