package com.aetherteam.aether.integration.rei;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public record FuelRecipe(List<ItemStack> inputItems, int burnTime, Block usageBlock) {
}
