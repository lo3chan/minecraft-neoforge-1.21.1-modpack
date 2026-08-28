/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import de.fabmax.physxjni.Loader;

public enum PxDebugColorEnum {
    eARGB_BLACK(PxDebugColorEnum.geteARGB_BLACK()),
    eARGB_RED(PxDebugColorEnum.geteARGB_RED()),
    eARGB_GREEN(PxDebugColorEnum.geteARGB_GREEN()),
    eARGB_BLUE(PxDebugColorEnum.geteARGB_BLUE()),
    eARGB_YELLOW(PxDebugColorEnum.geteARGB_YELLOW()),
    eARGB_MAGENTA(PxDebugColorEnum.geteARGB_MAGENTA()),
    eARGB_CYAN(PxDebugColorEnum.geteARGB_CYAN()),
    eARGB_WHITE(PxDebugColorEnum.geteARGB_WHITE()),
    eARGB_GREY(PxDebugColorEnum.geteARGB_GREY()),
    eARGB_DARKRED(PxDebugColorEnum.geteARGB_DARKRED()),
    eARGB_DARKGREEN(PxDebugColorEnum.geteARGB_DARKGREEN()),
    eARGB_DARKBLUE(PxDebugColorEnum.geteARGB_DARKBLUE());

    public final int value;

    private PxDebugColorEnum(int value) {
        this.value = value;
    }

    private static native int _geteARGB_BLACK();

    private static int geteARGB_BLACK() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_BLACK();
    }

    private static native int _geteARGB_RED();

    private static int geteARGB_RED() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_RED();
    }

    private static native int _geteARGB_GREEN();

    private static int geteARGB_GREEN() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_GREEN();
    }

    private static native int _geteARGB_BLUE();

    private static int geteARGB_BLUE() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_BLUE();
    }

    private static native int _geteARGB_YELLOW();

    private static int geteARGB_YELLOW() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_YELLOW();
    }

    private static native int _geteARGB_MAGENTA();

    private static int geteARGB_MAGENTA() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_MAGENTA();
    }

    private static native int _geteARGB_CYAN();

    private static int geteARGB_CYAN() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_CYAN();
    }

    private static native int _geteARGB_WHITE();

    private static int geteARGB_WHITE() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_WHITE();
    }

    private static native int _geteARGB_GREY();

    private static int geteARGB_GREY() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_GREY();
    }

    private static native int _geteARGB_DARKRED();

    private static int geteARGB_DARKRED() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_DARKRED();
    }

    private static native int _geteARGB_DARKGREEN();

    private static int geteARGB_DARKGREEN() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_DARKGREEN();
    }

    private static native int _geteARGB_DARKBLUE();

    private static int geteARGB_DARKBLUE() {
        Loader.load();
        return PxDebugColorEnum._geteARGB_DARKBLUE();
    }

    public static PxDebugColorEnum forValue(int value) {
        for (int i = 0; i < PxDebugColorEnum.values().length; ++i) {
            if (PxDebugColorEnum.values()[i].value != value) continue;
            return PxDebugColorEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxDebugColorEnum: " + value);
    }
}

