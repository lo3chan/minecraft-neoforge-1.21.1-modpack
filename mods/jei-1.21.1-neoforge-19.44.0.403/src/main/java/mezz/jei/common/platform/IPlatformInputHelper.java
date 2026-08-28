/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.world.item.TooltipFlag
 */
package mezz.jei.common.platform;

import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.common.input.keys.IJeiKeyMappingCategoryBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.TooltipFlag;

public interface IPlatformInputHelper {
    public boolean isActiveAndMatches(KeyMapping var1, InputConstants.Key var2);

    public IJeiKeyMappingCategoryBuilder createKeyMappingCategoryBuilder(String var1);

    default public TooltipFlag getClientTooltipFlag(TooltipFlag tooltipFlag) {
        return tooltipFlag;
    }

    default public TooltipFlag getSearchTooltipFlag(TooltipFlag tooltipFlag) {
        return tooltipFlag;
    }
}

