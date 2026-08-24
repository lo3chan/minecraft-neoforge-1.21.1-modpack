package com.aetherteam.aether.mixin.mixins.common.accessor;

import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet.Builder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({LootContextParamSets.class})
public interface LootContextParamSetsAccessor {
   @Invoker
   static LootContextParamSet callRegister(String registryName, Consumer<Builder> builderConsumer) {
      throw new AssertionError();
   }
}
