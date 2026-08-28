/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.irisshaders.iris.shaderpack.programs.ProgramSource
 */
package net.diebuddies.physics.ocean;

import net.irisshaders.iris.shaderpack.programs.ProgramSource;

public interface ProgramSetOcean {
    public ProgramSource getOceanSource();

    public ProgramSource getOceanShadowSource();

    public ProgramSource getLiquidsSource();

    public ProgramSource getLiquidsShadowSource();
}

