/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.item.TooltipFlag
 *  net.neoforged.neoforge.client.ClientTooltipFlag
 *  net.neoforged.neoforge.common.extensions.TooltipFlagExtension
 */
package mezz.jei.neoforge.platform;

import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.common.input.keys.IJeiKeyMappingCategoryBuilder;
import mezz.jei.common.platform.IPlatformInputHelper;
import mezz.jei.neoforge.input.ForgeJeiKeyMappingCategoryBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.ClientTooltipFlag;
import net.neoforged.neoforge.common.extensions.TooltipFlagExtension;

public class InputHelper
implements IPlatformInputHelper {
    @Override
    public boolean isActiveAndMatches(KeyMapping keyMapping, InputConstants.Key key) {
        return keyMapping.isActiveAndMatches(key);
    }

    @Override
    public IJeiKeyMappingCategoryBuilder createKeyMappingCategoryBuilder(String name) {
        return new ForgeJeiKeyMappingCategoryBuilder(name);
    }

    @Override
    public TooltipFlag getClientTooltipFlag(TooltipFlag tooltipFlag) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || tooltipFlag instanceof ClientTooltipFlag) {
            return tooltipFlag;
        }
        return ClientTooltipFlag.of((TooltipFlag)tooltipFlag);
    }

    @Override
    public TooltipFlag getSearchTooltipFlag(TooltipFlag tooltipFlag) {
        return new SearchTooltipFlag(tooltipFlag.isAdvanced(), tooltipFlag.isCreative());
    }

    private record SearchTooltipFlag(boolean advanced, boolean creative) implements TooltipFlag,
    TooltipFlagExtension
    {
        public boolean isAdvanced() {
            return this.advanced;
        }

        public boolean isCreative() {
            return this.creative;
        }

        public boolean shouldDisplayAllInformation() {
            return true;
        }
    }
}

