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

import android.graphics.Color;
import java.util.Random;

public class ColorUtil {
    private static final Random gRandom = new Random();
    private static final float gSaturation = 0.7f;
    private static final float gValue = 0.85f;
    private static final int[] gColorWheel;

    static {
        gColorWheel = new int[7];

        for(int i = 0; i < 7; i++) {
            final float hue = 360.0f - ((float)i / 6.0f) * 360.0f;
            gColorWheel[i] = hueToColor(hue);
        }
    }

    public static int randomColor() {
        final int hue = gRandom.nextInt(360);
        return hueToColor(hue);
    }

    public static int[] colorWheel() {
        return gColorWheel;
    }

    public static int hueToColor(float hue) {
        final float[] hsv = {hue, gSaturation, gValue};
        return Color.HSVToColor(hsv);
    }
}
