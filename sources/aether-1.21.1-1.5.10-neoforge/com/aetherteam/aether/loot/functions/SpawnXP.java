package com.aetherteam.aether.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public class SpawnXP extends LootItemConditionalFunction {
   public static final MapCodec<SpawnXP> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, SpawnXP::new));

   protected SpawnXP(List<LootItemCondition> conditions) {
      super(conditions);
   }

   protected ItemStack run(ItemStack stack, LootContext context) {
      ServerLevel serverLevel = context.getLevel();
      Vec3 originVec = (Vec3)context.getParamOrNull(LootContextParams.ORIGIN);
      if (originVec != null) {
         int randomNumber = (int)(4.0 * serverLevel.getRandom().nextDouble() + 6.0);

         while (randomNumber > 0) {
            int i = ExperienceOrb.getExperienceValue(randomNumber);
            randomNumber -= i;
            serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, originVec.x(), originVec.y(), originVec.z(), i));
         }
      }

      return stack;
   }

   public static Builder<?> builder() {
      return LootItemConditionalFunction.simpleBuilder(SpawnXP::new);
   }

   public LootItemFunctionType<SpawnXP> getType() {
      return (LootItemFunctionType<SpawnXP>)AetherLootFunctions.SPAWN_XP.get();
   }
}
