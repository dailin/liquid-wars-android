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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.MotionEvent;
import android.graphics.SweepGradient;

public class ColorPickerView extends View {
    private Callbacks gCallbacks;
    private int gInitialColor;
    private int gWidth;
    private int gHeight;
    private Paint gCirclePaint;
    private Paint gCenterPaint;
    private boolean gPickingColor = false;

    public ColorPickerView(Callbacks callbacks, Context context, int initialColor) {
        super(context);
        gCallbacks = callbacks;
        gInitialColor = initialColor;

        gCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        SweepGradient sweepGradient = new SweepGradient(0, 0, ColorUtil.colorWheel(), null);

        gCirclePaint.setShader(sweepGradient);
        gCirclePaint.setStyle(Paint.Style.STROKE);

        gCenterPaint.setColor(initialColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        final int centerWidth = gWidth / 2;
        final int centerHeight = gHeight / 2;
        final int centerRadius = gHeight / 6;
        final int circleStrokeWidth = gWidth / 8;

        canvas.translate(centerWidth, centerHeight);

        gCirclePaint.setStrokeWidth(circleStrokeWidth);

        canvas.drawCircle(0, 0, centerRadius, gCenterPaint);
        canvas.drawCircle(0, 0, (gWidth / 2) - circleStrokeWidth, gCirclePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        final int action = motionEvent.getActionMasked();
        final int centerWidth = gWidth / 2;
        final int centerHeight = gHeight / 2;
        final int centerRadius = gHeight / 6;

        final int x = (int)motionEvent.getX() - centerWidth;
        final int y = (int)motionEvent.getY() - centerHeight;

        final int touchDistance = (int)Math.sqrt(x*x + y*y);

        if(action == MotionEvent.ACTION_DOWN) {
            if(touchDistance >= centerRadius) {
                gPickingColor = true;
                updateColor(x, y);
            } else {
                gPickingColor = false;
            }
        } else if(action == MotionEvent.ACTION_UP) {
            if((touchDistance < centerRadius) && !gPickingColor) {
                gCallbacks.onSelect(gCenterPaint.getColor());
            }
        } else if(action == MotionEvent.ACTION_MOVE) {
            if((touchDistance > centerRadius) && gPickingColor) {
                updateColor(x, y);
            }
        }

        return true;
    }

    private void updateColor(int x, int y) {
        final int color = calculateColor(x, y);
        gCenterPaint.setColor(color);
        gCallbacks.onChange(color);
        invalidate();
    }

    private int calculateColor(int x, int y) {
        final float r = (float)Math.atan2(y, -x);
        final float hue = (((r / (float)Math.PI) + 1.0f) / 2.0f) * 360.0f;
        return ColorUtil.hueToColor(hue);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        gWidth = w;
        gHeight = h;
    }

    public interface Callbacks {
        public void onSelect(int color);
        public void onChange(int color);
    }
}