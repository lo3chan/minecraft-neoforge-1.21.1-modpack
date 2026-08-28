/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.KeyMapping
 */
package mezz.jei.api.gui.inputs;

import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.runtime.IJeiKeyMapping;
import net.minecraft.client.KeyMapping;

public interface IJeiUserInput {
    public InputConstants.Key getKey();

    public int getModifiers();

    public boolean isSimulate();

    public boolean is(KeyMapping var1);

    public boolean is(IJeiKeyMapping var1);
}

