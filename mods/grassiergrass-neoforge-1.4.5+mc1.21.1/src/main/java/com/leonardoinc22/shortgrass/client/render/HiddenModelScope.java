/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.chunk.SectionCompiler
 */
package com.leonardoinc22.shortgrass.client.render;

import net.minecraft.client.renderer.chunk.SectionCompiler;

public final class HiddenModelScope {
    private static final String VANILLA_MESHER = SectionCompiler.class.getName();
    private static final String VANILLA_MESHER_INNER = VANILLA_MESHER + "$";
    private static final String SODIUM_MESHER_PACKAGE = ".sodium.client.render.chunk.";
    private static final StackWalker STACK_WALKER = StackWalker.getInstance();

    public static boolean shouldExposeHiddenModels() {
        return STACK_WALKER.walk(frames -> frames.map(StackWalker.StackFrame::getClassName).noneMatch(HiddenModelScope::isTerrainMesher));
    }

    private static boolean isTerrainMesher(String className) {
        return className.equals(VANILLA_MESHER) || className.startsWith(VANILLA_MESHER_INNER) || className.contains(SODIUM_MESHER_PACKAGE);
    }

    private HiddenModelScope() {
    }
}

