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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import java.util.HashMap;

public class PlayerListView extends View {
    private HashMap<Integer, Player> players = new HashMap<Integer, Player>();
    private Paint paint;
    private int width;
    private int height;

    public PlayerListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public void setPlayer(int id, int colour, boolean ready) {
        Player player = new Player(colour, ready);
        players.put(id, player);
        invalidate();
    }

    public void removePlayer(int id) {
        players.remove(id);
        invalidate();
    }

    public void removeAllPlayers() {
        players.clear();
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int radius = height / 3;
        int x = height / 2;
        int y = height / 2;
        int diff = height / 20;

        for(HashMap.Entry<Integer, Player> entry : players.entrySet()) {
            paint.setColor(entry.getValue().ready ? Color.GREEN : Color.RED);
            canvas.drawCircle(x, y, radius + diff, paint);

            paint.setColor(Color.BLACK);
            canvas.drawCircle(x, y, radius, paint);

            paint.setColor(entry.getValue().colour);
            canvas.drawCircle(x, y, radius - diff, paint);

            x += height;
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        width = w;
        height = h;
    }

    private class Player {
        public int colour;
        public boolean ready;

        public Player(int colour, boolean ready) {
            this.colour = colour;
            this.ready = ready;
        }
    }
}
