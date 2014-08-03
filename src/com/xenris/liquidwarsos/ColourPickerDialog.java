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

import android.app.Dialog;
import android.content.*;
import android.os.Bundle;
import android.view.Window;

public class ColourPickerDialog extends Dialog implements ColourPickerView.Callbacks {
    private ColourPickerListener gListener;
    private int gInitialColour;

    public ColourPickerDialog(Context context, int initialColour, int dialogSize, ColourPickerListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        gListener = listener;
        gInitialColour = initialColour;
        setContentView(new ColourPickerView(this, context, initialColour));
        getWindow().setLayout(dialogSize, dialogSize);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void onSelect(int colour) {
        dismiss();
        gListener.onSelect(this, colour);
    }

    @Override
    public void onChange(int colour) {
        gListener.onChange(this, colour);
    }

    public interface ColourPickerListener {
        public void onSelect(ColourPickerDialog dialog, int colour);
        public void onChange(ColourPickerDialog dialog, int colour);
    }
}
