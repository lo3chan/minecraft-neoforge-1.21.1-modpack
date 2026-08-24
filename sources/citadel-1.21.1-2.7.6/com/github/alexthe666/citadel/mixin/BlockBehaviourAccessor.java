package com.github.alexthe666.citadel.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({BlockBehaviour.class})
public interface BlockBehaviourAccessor {
   @Invoker("canSurvive")
   boolean citadel_canSurvive(BlockState var1, LevelReader var2, BlockPos var3);
}
