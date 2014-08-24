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

#include "dot.hpp"

Dot::Dot(float* x, float* y, float* r, float* g, float* b, int team) {
    this->x = x;
    this->y = y;
    this->r = r;
    this->g = g;
    this->b = b;
    this->team = team;

    red = *r;
    green = *g;
    blue = *b;

    health = INITIAL_HEALTH;

    refreshColor();
}

void Dot::reduceHealth() {
    if(health > 0) {
        health -= 0.1;

        if(health < 0) {
            health = 0;
        }

        refreshColor();
    }
}

void Dot::increaseHealth() {
    if(health < MAX_HEALTH) {
        health += 0.1;

        if(health > MAX_HEALTH) {
            health = MAX_HEALTH;
        }

        refreshColor();
    }
}

void Dot::refreshColor() {
    *r = health * red;
    *g = health * green;
    *b = health * blue;
}

void Dot::swapTeamTo(Dot* otherDot) {
    team = otherDot->team;
    red = otherDot->red;
    green = otherDot->green;
    blue = otherDot->blue;
    refreshColor();
    health = MAX_HEALTH / 2;
}
