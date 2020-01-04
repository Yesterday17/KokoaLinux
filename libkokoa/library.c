//////////////////////////////// Include /////////////////////////////////
#include <stdio.h>
#include <stdbool.h>

#include <X11/X.h>
#include <X11/Xlib.h>
#include <X11/Xlocale.h>

///////////////////////////////// Types //////////////////////////////////

typedef long Pointer;
#define nullptr 0

///////////////////////////////// Debug //////////////////////////////////

bool DEBUG = false;

void setDebug(long debug) {
    if (DEBUG != (debug == 0 ? false : true)) {
        DEBUG = debug == 0 ? false : true;
        printf("[libkokoa/DEBUG] %sed debug mode\n", (DEBUG == true ? "enter" : "exit"));
        fflush(stdout);
    }
}

///////////////////////////////// Global /////////////////////////////////

/**
 * Display will never change when game is running
 * Window will change when switching between full screen mode and window mode
 */
Display *display = nullptr;
Window window = nullptr;
XIM xim = nullptr;
XIC xic = nullptr;

/////////////////////////////// Set Values ///////////////////////////////

void setDisplay(Pointer d) {
    display = (Display *) d;

    if (DEBUG) {
        printf("[libkokoa] display: %p\n", display);
        fflush(stdout);
    }
}

void setWindow(Pointer w) {
    window = (Window) w;
    if (DEBUG) {
        printf("[libkokoa] window: %ld\n", window);
        fflush(stdout);
    }
}

//////////////////////// Replace LWJGL Functions /////////////////////////

Pointer openIM() {
    xim = XOpenIM(display, NULL, NULL, NULL);
    if (DEBUG) {
        printf("[libkokoa/DEBUG] openIM: %p\n", xim);
        fflush(stdout);
    }
    return (Pointer) xim;
}

Pointer createIC() {
    xic = XCreateIC(
            xim,
            XNClientWindow,
            window,
            XNFocusWindow,
            window,
            XNInputStyle,
            XIMPreeditNothing | XIMStatusNothing,
            NULL);
    if (DEBUG) {
        printf("[libkokoa/DEBUG] createIC: %p\n", xic);
        fflush(stdout);
    }
    return (Pointer) xic;
}

void closeIM() {
    if (xim != nullptr) {
        XCloseIM(xim);
        if (DEBUG) {
            printf("[libkokoa/DEBUG]: XIM closed\n");
            fflush(stdout);
        }
    } else {
        printf("[libkokoa/ERROR]: XIM is nullptr\n");
        fflush(stdout);
    }
}

void destroyIC() {
    if (xic != nullptr) {
        XDestroyIC(xic);
        if (DEBUG) {
            printf("[libkokoa/DEBUG]: XIC destroyed\n");
            fflush(stdout);
        }
    } else {
        printf("[libkokoa/ERROR]: XIC is nullptr\n");
        fflush(stdout);
    }
}

///////////////////////////////// Kokoa //////////////////////////////////

Pointer toggleIC(long active) {
    (active ? XSetICFocus : XUnsetICFocus)(xic);

    if (DEBUG) {
        printf("[libkokoa/DEBUG] toggleIC: %s\n", (active == true ? "focus" : "unfocus"));
        fflush(stdout);
    }
    return (Pointer) xic;
}

void prepareLocale() {
    XSetLocaleModifiers("");
    setlocale(LC_CTYPE, "");
}
