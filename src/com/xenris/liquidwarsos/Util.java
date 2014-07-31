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

import android.os.*;
import java.io.*;

public class Util {
    private static int gNextId = 0;

    public static int getNextId() {
        return gNextId++;
    }

    public static void join(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.message(Log.tag, "Error: Util.join(): " + e.getMessage());
        }
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.message(Log.tag, "Error: Util.sleep(): " + e.getMessage());
        }
    }

    public static boolean close(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
                return true;
            } catch (IOException e) { }
        }

        return false;
    }
}
