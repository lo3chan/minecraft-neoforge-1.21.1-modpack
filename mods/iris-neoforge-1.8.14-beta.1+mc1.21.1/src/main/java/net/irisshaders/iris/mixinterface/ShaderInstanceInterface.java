/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.resources.ResourceProvider
 */
package net.irisshaders.iris.mixinterface;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import net.minecraft.server.packs.resources.ResourceProvider;

public interface ShaderInstanceInterface {
    public void iris$createExtraShaders(ResourceProvider var1, String var2) throws IOException;

    public void setShouldSkip(MethodHandle var1);
}

