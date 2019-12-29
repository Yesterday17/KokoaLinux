#include "library.h"

int *(*getDisplayPosition)();

void emptyCallback(XIC xic, XPointer clientData, XPointer data) {
}

void preeditDraw(XIC xic, XPointer clientData, XIMPreeditDrawCallbackStruct *s) {
    int *array = getDisplayPosition();

    fflush(stdout);
    XPoint place;
    place.x = array[0];
    place.y = array[1];

    XVaNestedList attr = XVaCreateNestedList(0, XNSpotLocation, &place, NULL);;
    XSetICValues(xic, XNPreeditAttributes, attr, NULL);
    XFree(attr);
}

XICCallback empty, draw;

XVaNestedList preeditCallbacksList() {
    empty.client_data = NULL;
    empty.callback = emptyCallback;

    draw.client_data = NULL;
    draw.callback = preeditDraw;
    return XVaCreateNestedList(0, // DUMMY
                               XNPreeditStartCallback,
                               &empty,
                               XNPreeditDoneCallback,
                               &empty,
                               XNPreeditDrawCallback,
                               &draw,
                               XNPreeditCaretCallback,
                               &empty,
                               NULL); // FINAL
}

void setDisplayPositionCallback(int *(*c_draw)()) {
    getDisplayPosition = c_draw;
}

long createInactiveIC(long xim, long window) {
    XIC ic = XCreateIC(
            (XIM) xim,
            XNClientWindow,
            (Window) window,
            XNFocusWindow,
            (Window) window,
            XNInputStyle,
            XIMPreeditNone | XIMStatusNone,
            // XIMPreeditNothing|XIMStatusNothing,
            NULL);
    return (long) ic;
}

long createActiveIC(long xim, long window) {
    XIC ic = XCreateIC(
            (XIM) xim,
            XNClientWindow,
            (Window) window,
            XNFocusWindow,
            (Window) window,
            XNInputStyle,
            XIMPreeditCallbacks | XIMStatusNothing,
            XNPreeditAttributes,
            preeditCallbacksList(),
            NULL);
    return (long) ic;
}

void setLocale() {
    setlocale(LC_CTYPE, "");
}

//////////////////////// X11 ////////////////////////
/**
 * Clear Locale Modifier
 */
void setEmptyLocaleModifier() {
    XSetLocaleModifiers("");
}

/**
 * Destroy specified XIC
 * This function assumes xic is valid, and protects deleting NULL pointer.
 * @param xic Long form of XIC pointer
 */
void destroyIC(long xic) {
    if (xic != 0) {
        XDestroyIC((XIC) xic);
    }
}

/**
 * Open IM for specified Display
 * @param display
 * @return Long form of opened XIM pointer.
 */
long openIM(long display) {
    return (long) XOpenIM((Display *) display, NULL, NULL, NULL);
}

/**
 * Close specified XIM
 * This function assumes xim is valid, and protects deleting NULL pointer.
 * @param xim Long form of XIM pointer
 */
void closeIM(long xim) {
    if (xim != 0) {
        XCloseIM((XIM) xim);
    }
}
