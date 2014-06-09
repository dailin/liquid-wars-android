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

import java.util.ArrayList;
import java.io.*;

public class Game {
    public final int countdownSeconds = 5;
    private ArrayList<Player> gPlayers;
    private int gSeed = 3; // TODO Get seed from time.
    private int gCountdown = -1; // Tenths of a second.

    public Game() {
        gPlayers = new ArrayList<Player>();
    }

    public Game(ArrayList<Player> players, int seed, int startCountdown) {
        gPlayers = players;
        gSeed = seed;
        gCountdown = startCountdown;
    }

    // Needs to be called ten times per second.
    // Internally this will step the game six times, resulting in 60 steps
    //  per second.
    public void step() {
        if(!gameStarted()) {
            manageCountdown();
        }

        // TODO
        Log.message("TODO: Game.step()");
        Util.sleep(2);
    }

    public void manageCountdown() {
        if(gCountdown != -1) {
            if(everyoneIsReady()) {
                if(gCountdown > 0) {
                    gCountdown--;
                } else {
                    // TODO Somehow start the game.
                }
            } else {
                cancelCountdown();
            }
        } else {
            if(everyoneIsReady()) {
                startCountdown();
            }
        }
    }

    public boolean everyoneIsReady() {
        if(gPlayers.size() == 0) {
            return false;
        }

        for(Player player : gPlayers) {
            if(!player.isReady()) {
                return false;
            }
        }

        return true;
    }

    public void startCountdown() {
        gCountdown = countdownSeconds * 10 - 1;
    }

    public void cancelCountdown() {
        gCountdown = -1;
    }

    public boolean gameStarted() {
        return gCountdown == 0;
    }

    public int getCountdownSeconds() {
        if(gCountdown == -1) {
            return -1;
        } else {
            return gCountdown / 10;
        }
    }

    public void addPlayer(Player player) {
        gPlayers.add(player);
    }

    public void removePlayer(int id) {
        // TODO If game is in play then player shouldn't be removed.
        Player player = findPlayerById(id);
        if(player != null) {
            gPlayers.remove(player);
        }
    }

    public Player findPlayerById(int id) {
        for(Player player : gPlayers) {
            if(player.getId() == id) {
                return player;
            }
        }

        return null;
    }

    public ArrayList<Player> getPlayers() {
        return gPlayers;
    }

    public void setPlayers(ArrayList<Player> players) {
        gPlayers = players;
    }

    public int getSeed() {
        return gSeed;
    }

    public void setSeed(int seed) {
        gSeed = seed;
    }

    public boolean send(DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.writeInt(gPlayers.size());

            for(Player player : gPlayers) {
                player.send(dataOutputStream);
            }

            dataOutputStream.writeInt(gSeed);
            dataOutputStream.writeInt(gCountdown);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Game createNew(DataInputStream dataInputStream) {
        try {
            final int playerCount = dataInputStream.readInt();

            ArrayList<Player> players = new ArrayList<Player>();

            for(int i = 0; i < playerCount; i++) {
                Player player = Player.createNew(dataInputStream);

                if(player != null) {
                    players.add(player);
                } else {
                    return null;
                }
            }

            final int seed = dataInputStream.readInt();
            final int startCountdown = dataInputStream.readInt();

            return new Game(players, seed, startCountdown);
        } catch (IOException e) { }

        return null;
    }
}
