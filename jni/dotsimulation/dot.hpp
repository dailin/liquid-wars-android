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

#ifndef DOT_HPP
#define DOT_HPP

#define MAX_HEALTH 1.0f
#define INITIAL_HEALTH 0.5f
#define CHANCE_OF_HEALING 7

class Dot {
    public:
        float* x;
        float* y;
        float* r;
        float* g;
        float* b;
        float red;
        float green;
        float blue;
        int team;
        float health;

        Dot(float* x, float* y, float* r, float* g, float* b, int team);
        void reduceHealth();
        void increaseHealth();
        void refreshColor();
        void swapTeamTo(Dot* otherDot);
};

#endif
