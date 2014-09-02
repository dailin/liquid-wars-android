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
import android.os.Bundle;
import android.view.Window;

public class ColorPickerDialog extends Dialog implements ColorPickerView.Callbacks {
    private ColorPickerListener gListener;
    private int gInitialColor;

    public ColorPickerDialog(Context context, int initialColor, int dialogSize, ColorPickerListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        gListener = listener;
        gInitialColor = initialColor;
        setContentView(new ColorPickerView(this, context, initialColor));
        getWindow().setLayout(dialogSize, dialogSize);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void onSelect(int color) {
        dismiss();
        gListener.onSelect(this, color);
    }

    @Override
    public void onChange(int color) {
        gListener.onChange(this, color);
    }

    public interface ColorPickerListener {
        public void onSelect(ColorPickerDialog dialog, int color);
        public void onChange(ColorPickerDialog dialog, int color);
    }
}
