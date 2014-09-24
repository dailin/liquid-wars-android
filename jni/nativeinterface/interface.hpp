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

#ifndef INTERFACE_HPP
#define INTERFACE_HPP

#include <jni.h>
#include <dotsimulation.hpp>

extern "C" {

JNIEXPORT jlong JNICALL Java_com_xenris_liquidwarsos_DotSimulation_newNative(
    JNIEnv* env, jobject jobj, jlong seed, jint numberOfPlayers, jintArray colors, jint teamSize, jint width, jint height, jbooleanArray wallMap);

JNIEXPORT void JNICALL Java_com_xenris_liquidwarsos_DotSimulation_deleteNative(
    JNIEnv* env, jobject jobj, jlong pointer);

JNIEXPORT void JNICALL Java_com_xenris_liquidwarsos_DotSimulation_drawNative(
    JNIEnv* env, jobject jobj, jlong pointer);

JNIEXPORT void JNICALL Java_com_xenris_liquidwarsos_DotSimulation_stepNative(
    JNIEnv* env, jobject jobj, jlong pointer);

JNIEXPORT void JNICALL Java_com_xenris_liquidwarsos_DotSimulation_setPlayerPositionNative(
    JNIEnv* env, jobject jobj, jlong pointer, jint playerId, jfloat x, jfloat y);
}

#endif
