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

import android.content.*;
import android.view.*;

public class MainMenuActivity extends BaseActivity {
    @Override
    public void onCreate() {
        super.onCreate();

        addView(R.layout.main_menu);
    }

    public void buttonHandler(View view) {
        final int id = view.getId();

        if(id == R.id.play_button) {
            play();
        } else if(id == R.id.settings_button) {
            settings();
        } else if(id == R.id.about_button) {
            about();
        }
    }

    private void play() {
        startActivity(new Intent(this, Client.class));
    }

    private void settings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void about() {
        startActivity(new Intent(this, AboutActivity.class));
    }
}
