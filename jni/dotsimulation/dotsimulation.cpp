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

#include "dotsimulation.hpp"

DotSimulation::DotSimulation() {
    g = 0;
    direction = 1;
}

DotSimulation::~DotSimulation() {

}

void DotSimulation::draw() {
    glClearColor(0, g, 0, 1);
    glClear(GL_COLOR_BUFFER_BIT);
}

void DotSimulation::step() {
    g += (float)direction * 0.01;

    if(g > 1) {
        g = 1;
        direction = -1;
    } else if(g < 0) {
        g = 0;
        direction = 1;
    }
}
