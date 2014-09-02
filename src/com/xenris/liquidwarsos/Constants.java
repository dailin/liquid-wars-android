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

import java.util.*;

public class Constants {
    public static final int REQUEST_ENABLE_BLUETOOTH = 1;
    public static final int START_SHARING = 2;

    public static final int START_GAME = 3;
    public static final int SHARE_SERVER = 4;
    public static final int FIND_SERVER = 5;
    public static final int SWITCH_TO_GAME_MENU = 6;
    public static final int SWITCH_TO_GAME_VIEW = 7;
    public static final int CONNECTION_MADE = 8;
    public static final int CONNECTION_FAILED = 9;
    public static final int UPDATE_UI = 10;

    public static final int BACK_PRESS_DELAY = 2000;
    public static final int BACK_PRESS_COUNT = 3;

    public static final int DISCOVERABLE_TIME = 30;

    public static final UUID uuid = new UUID(182741987, 289479128);

    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;

    // TODO These should be part of GameState so that the server can regulate
    //  game speed if one client is running too slow. Also could be used to
    //  slow down and speed up the game during play. Also also could be used to
    //  make sure the bluetooth isn't being overloaded.
    // Number of simulation steps processed per GameState from server.
    public static final int STEP_MULTIPLIER = 10;
    public static final int STEP_TIME_MS = 10;
}
