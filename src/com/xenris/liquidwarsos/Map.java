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
import android.graphics.Color;
import java.io.*;
import javax.microedition.khronos.opengles.GL10;

public class Map {
    private final int gId;
    private final Context gContext;
    private Bitmap gMapBitmap;
    private Bitmap gImageBitmap;
    private final int gWidth;
    private final int gHeight;

    public Map(Context context, int id) {
        gId = id;
        gContext = context;

        loadMap();
        loadImage();

        final Bitmap bitmap = (gMapBitmap != null) ? gMapBitmap : gImageBitmap;

        gWidth = bitmap.getWidth();
        gHeight = bitmap.getHeight();
    }

    private void loadMap() {
        InputStream inputStream = null;

        try {
            inputStream = gContext.getAssets().open(toMapPath(gId));
        } catch(IOException e) {
            gMapBitmap = null;
            return;
        }

        gMapBitmap = BitmapFactory.decodeStream(inputStream);

        try {
            inputStream.close();
        } catch(IOException e) { }
    }

    private void loadImage() {
        InputStream inputStream = null;

        try {
            inputStream = gContext.getAssets().open(toImagePath(gId));
        } catch(IOException e) {
            gImageBitmap = null;
            return;
        }

        gImageBitmap = BitmapFactory.decodeStream(inputStream);

        try {
            inputStream.close();
        } catch(IOException e) { }
    }

    public boolean[] createWallMap() {
        boolean[] result = new boolean[gWidth * gHeight];

        int i = 0;
        for(int y = 0; y < gHeight; y++) {
            for(int x = 0; x < gWidth; x++) {
                if(gMapBitmap != null) {
                    final int pixel = gMapBitmap.getPixel(x, y);
                    final int mid = 0xff / 2;
                    result[i] = Color.alpha(pixel) > mid;
                } else {
                    result[i] = false;
                }
                i++;
            }
        }

        return result;
    }

    public Sprite createSprite(GL10 gl) {
        final Bitmap bitmap = (gImageBitmap != null) ? gImageBitmap : gMapBitmap;
        return new Sprite(gl, bitmap);
    }

    public static String toMapPath(int id) {
        return "maps/" + id + "-map.png";
    }

    public static String toImagePath(int id) {
        return "maps/" + id + "-image.png";
    }

    public int getWidth() {
        return gWidth;
    }

    public int getHeight() {
        return gHeight;
    }
}
