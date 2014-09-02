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

#ifndef DOTSIMULATION_HPP
#define DOTSIMULATION_HPP

#include <GLES/gl.h>
#include <vector>
#include "dot.hpp"
#include "random.hpp"
#include "player.hpp"
#include "info.hpp"
#include "spiral.hpp"
#include "map.hpp"

using namespace std;

class DotSimulation {
    private:
        vector<Dot*> gDots;
        Player* gPlayers;
        Dot*** gField;
        Map* gMap;
        vector<float> gPoints;
        vector<float> gColours;
        Random* gRandom;
        int gNumberOfPlayers;
        int gTeamSize;
        int gWidth;
        int gHeight;

    public:
        DotSimulation(unsigned int seed, int numberOfPlayers, int* colors, int teamSize, int width, int height);
        ~DotSimulation();
        void placeTeams();
        void draw();
        void step();
        void setPlayerPosition(int playerId, float x, float y);
        void moveDotToward(Dot* dot, const Player* player);
        void turnClockwise(int dx, int dy, int* x, int* y);
        void turnAnticlockwise(int dx, int dy, int* x, int* y);
};

#endif
