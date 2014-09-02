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

#include "info.hpp"

short Info::playerStartPositionX(unsigned char t, int width) {
    const int xdistance = width / 16;

    switch(t) {
        case 0: return xdistance;
        case 1: return xdistance;
        case 2: return width / 2;
        case 3: return width / 2;
        case 4: return width - xdistance;
        case 5: return width - xdistance;
    }

    return 0;
}

short Info::playerStartPositionY(unsigned char t, int height) {
    const int ydistance = height / 16;

    switch(t) {
        case 0: return ydistance;
        case 1: return height - ydistance;
        case 2: return ydistance;
        case 3: return height - ydistance;
        case 4: return ydistance;
        case 5: return height - ydistance;
    }

    return 0;
}
