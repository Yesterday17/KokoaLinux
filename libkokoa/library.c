#include "library.h"

void (*callbackDone)();

int *(*callbackDraw)(int, int, int, short, int, char *, wchar_t *, int, int, int);

void setCallback(int *(*c_draw)(int, int, int, short, int, char *, wchar_t *, int, int, int), void(*c_done)()) {
    callbackDraw = c_draw;
    callbackDone = c_done;
}

void preeditCalet(XIC xic, XPointer clientData, XPointer data) {

}

void preeditStart(XIC xic, XPointer clientData, XPointer data) {

}

void preeditDone(XIC xic, XPointer clientData, XPointer data) {
    callbackDone();
}

void preeditDraw(XIC xic, XPointer clientData, XIMPreeditDrawCallbackStruct *structure) {
    int *array;
    int secondary = 0;
    int length = 0;
    if (structure->text) {
        int i = 0;
        int secondary_determined = 0;
        for (i = 0; i != structure->text->length; i++) {
            if (!secondary_determined && (structure->text->feedback[i] & XIMSecondary) != 0) {
                secondary = i;
                secondary_determined = 1;
            }
            if (secondary_determined &&
                (structure->text->feedback[i] == 0 || (structure->text->feedback[i] & XIMSecondary) != 0)) {
                length++;
            } else if (secondary_determined)break;
        }
    }

    if (structure->text != NULL) {
        array = callbackDraw(
                structure->caret,
                structure->chg_first,
                structure->chg_length,
                structure->text->length,
                structure->text->encoding_is_wchar,
                structure->text->encoding_is_wchar ? "" : structure->text->string.multi_byte,
                structure->text->encoding_is_wchar ? structure->text->string.wide_char : L"",
                0,
                secondary,
                secondary + length
        );
    } else {
        array = callbackDraw(
                structure->caret,
                structure->chg_first,
                structure->chg_length,
                0,
                0,
                "",
                L"",
                0,
                0,
                0
        );
    }

    fflush(stdout);
    XVaNestedList attr;
    XPoint place;
    place.x = array[0];
    place.y = array[1];
    attr = XVaCreateNestedList(0, XNSpotLocation, &place, NULL);
    XSetICValues(xic, XNPreeditAttributes, attr, NULL);
    XFree(attr);
}


XICCallback calet, start, done, draw;

XVaNestedList preeditCallbacksList() {
    start.client_data = NULL;
    start.callback = preeditStart;

    done.client_data = NULL;
    done.callback = preeditDone;

    draw.client_data = NULL;
    draw.callback = preeditDraw;

    calet.client_data = NULL;
    calet.callback = preeditCalet;
    return XVaCreateNestedList(0, // DUMMY
                               XNPreeditStartCallback,
                               &start,
                               XNPreeditDoneCallback,
                               &done,
                               XNPreeditDrawCallback,
                               &draw,
                               XNPreeditCaretCallback,
                               &calet,
                               NULL); // FINAL
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
