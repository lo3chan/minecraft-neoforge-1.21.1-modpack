/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 */
package me.flashyreese.mods.sodiumextra.compat;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class IrisCompat {
    private static boolean irisPresent;
    private static MethodHandle handleRenderingShadowPass;
    private static MethodHandle handleShaderPackInUse;
    private static Object apiInstance;
    private static VertexFormat terrainFormat;

    public static boolean isRenderingShadowPass() {
        if (irisPresent) {
            try {
                return handleRenderingShadowPass.invoke(apiInstance);
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isShaderPackInUse() {
        if (irisPresent) {
            try {
                return handleShaderPackInUse.invoke(apiInstance);
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return false;
    }

    public static VertexFormat getTerrainFormat() {
        return terrainFormat;
    }

    public static boolean isIrisPresent() {
        return irisPresent;
    }

    static {
        try {
            Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            apiInstance = api.cast(api.getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]));
            handleRenderingShadowPass = MethodHandles.lookup().findVirtual(api, "isRenderingShadowPass", MethodType.methodType(Boolean.TYPE));
            handleShaderPackInUse = MethodHandles.lookup().findVirtual(api, "isShaderPackInUse", MethodType.methodType(Boolean.TYPE));
            Class<?> irisVertexFormatsClass = Class.forName("net.irisshaders.iris.vertices.IrisVertexFormats");
            Field terrainField = irisVertexFormatsClass.getDeclaredField("TERRAIN");
            terrainFormat = (VertexFormat)terrainField.get(null);
            irisPresent = true;
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
            irisPresent = false;
        }
    }
}

