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

#include "map.hpp"

Map::Map(int width, int height) {
    gWidth = width;
    gHeight = height;
    gMap = NULL;
}

Map::~Map() {
//    delete gMap;
}

bool Map::isWall(int x, int y) {
//    y = (HEIGHT-1) - y; // Invert y axis.

    if((x < 0) || (x >= gWidth)) {
        return true;
    }

    if((y < 0) || (y >= gHeight)) {
        return true;
    }

    return false;
//    return gMap[x][y];
}
