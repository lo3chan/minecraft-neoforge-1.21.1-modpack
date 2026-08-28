/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.BlockItemStateProperties
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Vector3f
 */
package net.irisshaders.iris.api.v0.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public interface IrisItemLightProvider {
    public static final Vector3f DEFAULT_LIGHT_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);

    default public int getLightEmission(Player player, ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            BlockItem item2 = (BlockItem)item;
            BlockState blockState = item2.getBlock().defaultBlockState();
            BlockItemStateProperties itemBlockState = (BlockItemStateProperties)stack.getComponents().get(DataComponents.BLOCK_STATE);
            if (itemBlockState != null) {
                blockState = itemBlockState.apply(blockState);
            }
            return blockState.getLightEmission();
        }
        return 0;
    }

    default public Vector3f getLightColor(Player player, ItemStack stack) {
        return DEFAULT_LIGHT_COLOR;
    }
}

