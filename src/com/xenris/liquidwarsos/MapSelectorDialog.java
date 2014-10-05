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

import android.app.Dialog;
import android.content.*;
import android.view.Window;

public class MapSelectorDialog extends Dialog {
    public MapSelectorDialog(Context context, MapSelectorView.MapSelectorCallbacks callbacks, int width, int height, int currentMapId) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new MapSelectorView(context, callbacks, currentMapId));
        getWindow().setLayout(width, height);
        setCanceledOnTouchOutside(true);
    }
}
