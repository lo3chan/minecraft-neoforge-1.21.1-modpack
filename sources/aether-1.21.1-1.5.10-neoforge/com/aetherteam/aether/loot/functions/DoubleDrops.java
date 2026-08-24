package com.aetherteam.aether.loot.functions;

import com.aetherteam.aether.item.tools.abilities.SkyrootTool;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class DoubleDrops extends LootItemConditionalFunction {
   public static final MapCodec<DoubleDrops> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, DoubleDrops::new));

   protected DoubleDrops(List<LootItemCondition> conditions) {
      super(conditions);
   }

   protected ItemStack run(ItemStack stack, LootContext context) {
      Level level = context.getLevel();
      ItemStack toolStack = (ItemStack)context.getParamOrNull(LootContextParams.TOOL);
      BlockState blockState = (BlockState)context.getParamOrNull(LootContextParams.BLOCK_STATE);
      return toolStack != null && toolStack.getItem() instanceof SkyrootTool skyrootTool ? skyrootTool.doubleDrops(level, stack, toolStack, blockState) : stack;
   }

   public static Builder<?> builder() {
      return LootItemConditionalFunction.simpleBuilder(DoubleDrops::new);
   }

   public LootItemFunctionType<DoubleDrops> getType() {
      return (LootItemFunctionType<DoubleDrops>)AetherLootFunctions.DOUBLE_DROPS.get();
   }
}
