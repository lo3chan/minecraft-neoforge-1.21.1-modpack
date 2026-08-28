/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.BlockItemStateProperties
 *  net.minecraft.world.level.block.LightBlock
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class LightSubtypeInterpreter
implements ISubtypeInterpreter<ItemStack> {
    public static final LightSubtypeInterpreter INSTANCE = new LightSubtypeInterpreter();

    private LightSubtypeInterpreter() {
    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        BlockItemStateProperties properties = (BlockItemStateProperties)ingredient.get(DataComponents.BLOCK_STATE);
        if (properties == null) {
            return null;
        }
        return properties.get((Property)LightBlock.LEVEL);
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        BlockItemStateProperties properties = (BlockItemStateProperties)ingredient.get(DataComponents.BLOCK_STATE);
        if (properties == null) {
            return "";
        }
        Integer level = (Integer)properties.get((Property)LightBlock.LEVEL);
        if (level == null) {
            return "";
        }
        return level.toString();
    }
}

