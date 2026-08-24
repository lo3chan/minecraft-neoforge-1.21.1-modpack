package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.util.DestructionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public record ExplodeAction(float power, DestructionType destructionType, Optional<BlockCondition> indestructible, boolean createFire) implements BlockAction {
   public static final MapCodec<ExplodeAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Codec.FLOAT.fieldOf("power").forGetter(ExplodeAction::power),
            DestructionType.CODEC.optionalFieldOf("destruction_type", DestructionType.BREAK).forGetter(ExplodeAction::destructionType),
            BlockCondition.CODEC.optionalFieldOf("indestructible").forGetter(ExplodeAction::indestructible),
            Codec.BOOL.optionalFieldOf("create_fire", false).forGetter(ExplodeAction::createFire)
         )
         .apply(i, ExplodeAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BlockAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull final Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
      if (!level.isClientSide()) {
         ExplosionDamageCalculator calculator = this.indestructible().isEmpty()
            ? new ExplosionDamageCalculator()
            : new ExplosionDamageCalculator() {
               @NotNull
               public Optional<Float> getBlockExplosionResistance(
                  @NotNull Explosion explosion, @NotNull BlockGetter world, @NotNull BlockPos blockPos, @NotNull BlockState state, @NotNull FluidState fluid
               ) {
                  Optional<Float> def = super.getBlockExplosionResistance(explosion, world, blockPos, state, fluid);
                  Optional<Float> ovr = ExplodeAction.this.indestructible.<Boolean>map(x -> x.test(level, blockPos)).filter(x -> x).map(x -> 100.0F);
                  return ovr.isPresent() ? (def.isPresent() ? (def.get() > ovr.get() ? def : ovr) : ovr) : def;
               }
            };
         level.explode(
            null,
            level.damageSources().explosion(null, null),
            calculator,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            this.power,
            this.createFire,
            ExplosionInteraction.MOB
         );
      }
   }
}
