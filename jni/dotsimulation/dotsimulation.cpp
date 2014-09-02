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

#include "dotsimulation.hpp"

// TODO Should really do some checks to make sure "new" doesn't return null.

DotSimulation::DotSimulation(unsigned int seed, int numberOfPlayers, int* colors, int teamSize, int width, int height) {
    gNumberOfPlayers = numberOfPlayers;
    gTeamSize = teamSize;
    gWidth = width;
    gHeight = height;

    gPlayers = new Player[gNumberOfPlayers];

    for(int i = 0; i < numberOfPlayers; i++) {
        gPlayers[i].color = colors[i];
    }

    gMap = new Map(gWidth, gHeight);

    gField = new Dot**[width];

    for(int w = 0; w < width; w++) {
        gField[w] = new Dot*[height];
        for(int h = 0; h < height; h++) {
            gField[w][h] = NULL;
        }
    }

    gDots.resize(gNumberOfPlayers * gTeamSize);
    gPoints.resize(gNumberOfPlayers * gTeamSize * 3);
    gColours.resize(gNumberOfPlayers * gTeamSize * 4);

    for(int i = 0; i < gNumberOfPlayers * gTeamSize * 4; i++) {
        gColours[i] = 1;
    }

    gRandom = new Random(seed);

    placeTeams();
}

DotSimulation::~DotSimulation() {
    delete gRandom;
    delete gPlayers;
    delete gMap;

    for(int i = 0; i < gNumberOfPlayers * gTeamSize; i++) {
        delete gDots[i];
    }
}

void DotSimulation::placeTeams() {
    float* xp = &gPoints[0];
    float* yp = &gPoints[1];
    float* rp = &gColours[0];
    float* gp = &gColours[1];
    float* bp = &gColours[2];

    for(int t = 0; t < gNumberOfPlayers; t++) {
        int sx = Info::playerStartPositionX(t, gWidth);
        int sy = Info::playerStartPositionY(t, gHeight);
        int x = sx;
        int y = sy;
        int c = 0;
        for(int i = 0; i < gTeamSize; i++) {
            while(gMap->isWall(x, y) || (gField[x][y] != NULL)) {
                Spiral::getNextXY(sx, sy, ++c, &x, &y);
            }
            *xp = x;
            *yp = y;
            *rp = gPlayers[t].getRedf();
            *gp = gPlayers[t].getGreenf();
            *bp = gPlayers[t].getBluef();
            Dot* dot = new Dot(xp, yp, rp, gp, bp, t);
            gDots[t*gTeamSize + i] = dot;
            gField[x][y] = dot;

            xp += 3;
            yp += 3;
            rp += 4;
            gp += 4;
            bp += 4;
        }
    }

    for(int i = 0; i < gNumberOfPlayers; i++) {
        gPlayers[i].x[0] = Info::playerStartPositionX(i, gWidth);
        gPlayers[i].y[0] = Info::playerStartPositionY(i, gHeight);
    }
}

void DotSimulation::draw() {
    glPushMatrix();

    glLoadIdentity();

    glScalef(1.0f/gWidth, 1.0f/gHeight, 1);

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_COLOR_ARRAY);

    glColorPointer(4, GL_FLOAT, 0, &gColours[0]);
    glVertexPointer(3, GL_FLOAT, 0, &gPoints[0]);

    glDrawArrays(GL_POINTS, 0, gNumberOfPlayers * gTeamSize);

    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_COLOR_ARRAY);

    glPopMatrix();
}

void DotSimulation::step() {
    for(int i = 0; i < gNumberOfPlayers * gTeamSize; i++) {
        Dot* dot = gDots[i];
        Player* player = &gPlayers[dot->team];
        moveDotToward(dot, player);
    }
}

void DotSimulation::setPlayerPosition(int playerId, float x, float y) {
    gPlayers[playerId].x[0] = x * gWidth;
    gPlayers[playerId].y[0] = y * gHeight;
}

void DotSimulation::moveDotToward(Dot* dot, const Player* player) {
    const short currentX = (short)*dot->x;
    const short currentY = (short)*dot->y;
    short playerX = player->x[0];
    short playerY = player->y[0];
    int diffX = playerX - currentX;
    int diffY = playerY - currentY;
    float dist = sqrt(diffX*diffX + diffY*diffY);

    for(int i = 1; i < 5; i++) {
        if(player->x[i] == -1) {
            continue;
        }
        const short nextPlayerX = (short)player->x[i];
        const short nextPlayerY = (short)player->y[i];
        const int nextDiffX = nextPlayerX - currentX;
        const int nextDiffY = nextPlayerY - currentY;
        const float nextDist = sqrt(nextDiffX*nextDiffX + nextDiffY*nextDiffY);
        if(nextDist < dist) {
            playerX = nextPlayerX;
            playerY = nextPlayerY;
            dist = nextDist;
            diffX = nextDiffX;
            diffY = nextDiffY;
        }
    }

    if(diffY == 0)
        diffY = 1;
    if(diffX == 0)
        diffX = 1;
    const float absDiffX = abs(diffX);
    const float absDiffY = abs(diffY);
    const float xy = 10*absDiffX/absDiffY;
    const float yx = 10*absDiffY/absDiffX;

    const float sum = absDiffX + absDiffY + 1 + 1;

    const int randomInt = gRandom->nextInt() % 10;

    const float r = sum * randomInt / 10.0;

    int dx = 0;
    int dy = 0;

    if(r < absDiffX) {
        if(diffX > 0) {
            dx = 1;
        } else {
            dx = -1;
        }

        if(r < (absDiffX/xy)) {
            if(diffY > 0) {
                dy = 1;
            } else {
                dy = -1;
            }
        }
    } else if(r < (absDiffX + absDiffY)) {
        if(diffY > 0) {
            dy = 1;
        } else {
            dy = -1;
        }

        if(r < (absDiffX + absDiffY/yx)) {
            if(diffX > 0) {
                dx = 1;
            } else {
                dx = -1;
            }
        }
    } else if(r < (absDiffX + absDiffY + 1)) {
        if(diffX > 0) {
            dx = -1;
        } else {
            dx = 1;
        }

        if(r < (absDiffX + absDiffY + 1/xy)) {
            if(diffY > 0) {
                dy = -1;
            } else {
                dy = 1;
            }
        }
    } else {
        if(diffY > 0) {
            dy = -1;
        } else {
            dy = 1;
        }

        if(r < (absDiffX + absDiffY + 1 + 1/yx)) {
            if(diffX > 0) {
                dx = -1;
            } else {
                dx = 1;
            }
        }
    }

    short nx = (short)*dot->x + dx;
    short ny = (short)*dot->y + dy;
    if(!gMap->isWall(nx, ny) && (gField[nx][ny] == NULL)) {
        gField[nx][ny] = dot;
        gField[(short)*dot->x][(short)*dot->y] = NULL;
        *dot->x = nx;
        *dot->y = ny;
        return;
    }

    int d2x;
    int d2y;

    if((randomInt % 2) == 1) {
        turnClockwise(dx, dy, &d2x, &d2y);
    } else {
        turnAnticlockwise(dx, dy, &d2x, &d2y);
    }

    nx = *dot->x + d2x;
    ny = *dot->y + d2y;
    if(!gMap->isWall(nx, ny) && (gField[nx][ny] == NULL)) {
        gField[nx][ny] = dot;
        gField[(short)*dot->x][(short)*dot->y] = NULL;
        *dot->x = nx;
        *dot->y = ny;
        return;
    }

    int d3x;
    int d3y;

    if((randomInt % 2) == 0) {
        turnClockwise(dx, dy, &d3x, &d3y);
    } else {
        turnAnticlockwise(dx, dy, &d3x, &d3y);
    }

    nx = *dot->x + d3x;
    ny = *dot->y + d3y;
    if(!gMap->isWall(nx, ny) && (gField[nx][ny] == NULL)) {
        gField[nx][ny] = dot;
        gField[(short)*dot->x][(short)*dot->y] = NULL;
        *dot->x = nx;
        *dot->y = ny;
        return;
    }

    nx = *dot->x + dx;
    ny = *dot->y + dy;
    if(!gMap->isWall(nx, ny) && (gField[nx][ny]->team != dot->team)) {
        gField[nx][ny]->reduceHealth();
        if(gField[nx][ny]->health <= 0) {
            gPlayers[gField[nx][ny]->team].score--;
            gPlayers[dot->team].score++;
            gField[nx][ny]->swapTeamTo(dot);
        }
        return;
    }

    nx = *dot->x + d2x;
    ny = *dot->y + d2y;
    if(!gMap->isWall(nx, ny) && (gField[nx][ny]->team != dot->team)) {
        gField[nx][ny]->reduceHealth();
        if(gField[nx][ny]->health <= 0) {
            gPlayers[gField[nx][ny]->team].score--;
            gPlayers[dot->team].score++;
            gField[nx][ny]->swapTeamTo(dot);
        }
        return;
    }

    if((gRandom->nextInt() % 100) < CHANCE_OF_HEALING) {
        nx = *dot->x + dx;
        ny = *dot->y + dy;
        if(!gMap->isWall(nx, ny) && (gField[nx][ny]->team == dot->team)) {
            if(gField[nx][ny]->health < MAX_HEALTH) {
                    gField[nx][ny]->increaseHealth();
            }
            return;
        }
    }
}

void DotSimulation::turnClockwise(int dx, int dy, int* x, int* y) {
    *x = 0;
    *y = 0;
    if(dx == 0) {
        if(dy == 1) {
            *x = 1;
        } else {
            *x = -1;
        }

        return;
    }
    if(dy == 0) {
        if(dx == 1) {
            *y = -1;
        } else {
            *y = 1;
        }

        return;
    }
    if(dx == 1) {
        if(dy == 1) {
            *y = 0;
        } else {
            *x = 0;
        }

        return;
    }
    if(dx == -1) {
        if(dy == 1) {
            *x = 0;
        } else {
            *y = 0;
        }

        return;
    }
}

void DotSimulation::turnAnticlockwise(int dx, int dy, int* x, int* y) {
    *x = 0;
    *y = 0;
    if(dx == 0) {
        if(dy == 1) {
            *x = -1;
        } else {
            *x = 1;
        }

        return;
    }
    if(dy == 0) {
        if(dx == 1) {
            *y = 1;
        } else {
            *y = -1;
        }

        return;
    }
    if(dx == 1) {
        if(dy == 1) {
            *x = 0;
        } else {
            *y = 0;
        }

        return;
    }
    if(dx == -1) {
        if(dy == 1) {
            *y = 0;
        } else {
            *x = 0;
        }

        return;
    }
}
