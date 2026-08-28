/*
 * Decompiled with CFR 0.152.
 */
package de.fabmax.physxjni;

import de.fabmax.physxjni.Loader;
import de.fabmax.physxjni.NativeLib;

public enum Platform {
    LINUX("de.fabmax.physxjni.linux.NativeLibLinux"),
    WINDOWS("de.fabmax.physxjni.windows.NativeLibWindows"),
    MACOS("de.fabmax.physxjni.macos.NativeLibMacos"),
    MACOS_ARM64("de.fabmax.physxjni.macosarm.NativeLibMacosArm64");

    private final String metaClassName;

    private Platform(String metaClassName) {
        this.metaClassName = metaClassName;
    }

    public NativeLib getLib() throws ReflectiveOperationException {
        Class<?> libImpl = Loader.class.getClassLoader().loadClass(this.metaClassName);
        return (NativeLib)libImpl.getConstructor(new Class[0]).newInstance(new Object[0]);
    }

    public static Platform getPlatform() {
        String vendor = System.getProperty("java.vendor", "unknown").toLowerCase();
        String osName = System.getProperty("os.name", "unknown").toLowerCase();
        String arch = System.getProperty("os.arch", "unknown");
        if (vendor.contains("android")) {
            throw new IllegalStateException("Android environment detected. Use 'physx-jni-android' library instead of regular 'physx-jni'");
        }
        if (osName.contains("windows")) {
            return WINDOWS;
        }
        if (osName.contains("linux")) {
            return LINUX;
        }
        if (osName.contains("mac os x") || osName.contains("darwin") || osName.contains("osx")) {
            if ("aarch64".equals(arch)) {
                return MACOS_ARM64;
            }
            return MACOS;
        }
        throw new IllegalStateException("Unsupported OS: " + osName);
    }
}

