/*
 * Edited from https://gist.github.com/Axeryok/d9b7cabacce4aeb7c7e06107264f6bdd
 * Great thanks to https://gist.github.com/Axeryok
 */

#ifndef LIBKOKOA_LIBRARY_H
#define LIBKOKOA_LIBRARY_H

#include <stdio.h>

#include <X11/X.h>
#include <X11/Xlib.h>
#include <X11/XKBlib.h>
#include <X11/Xutil.h>
#include <X11/keysym.h>
#include <X11/Xlocale.h>

void setDisplayPositionCallback(int *(*c_draw)());

long createInactiveIC(long xim, long window);

long createActiveIC(long xim, long window);

void setLocale();

#endif //LIBKOKOA_LIBRARY_H