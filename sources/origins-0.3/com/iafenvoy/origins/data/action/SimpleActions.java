package com.iafenvoy.origins.data.action;

import com.iafenvoy.origins.util.function.TriConsumer;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class SimpleActions {
   public static MapCodec<? extends BiEntityAction> createBiEntity(final BiConsumer<Entity, Entity> action) {
      return (new BiEntityAction() {
         final MapCodec<? extends BiEntityAction> codec = MapCodec.unit(this);

         @NotNull
         @Override
         public MapCodec<? extends BiEntityAction> codec() {
            return this.codec;
         }

         @Override
         public void execute(@NotNull Entity source, @NotNull Entity target) {
            action.accept(source, target);
         }
      }).codec();
   }

   public static MapCodec<? extends BlockAction> createBlock(final TriConsumer<Level, BlockPos, Optional<Direction>> action) {
      return (new BlockAction() {
         final MapCodec<? extends BlockAction> codec = MapCodec.unit(this);

         @NotNull
         @Override
         public MapCodec<? extends BlockAction> codec() {
            return this.codec;
         }

         @Override
         public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
            action.accept(level, pos, direction);
         }
      }).codec();
   }

   public static MapCodec<? extends EntityAction> createEntity(final Consumer<Entity> action) {
      return (new EntityAction() {
         final MapCodec<? extends EntityAction> codec = MapCodec.unit(this);

         @NotNull
         @Override
         public MapCodec<? extends EntityAction> codec() {
            return this.codec;
         }

         @Override
         public void execute(@NotNull Entity source) {
            action.accept(source);
         }
      }).codec();
   }

   public static MapCodec<? extends ItemAction> createItem(final TriConsumer<Level, Entity, SlotAccess> action) {
      return (new ItemAction() {
         final MapCodec<? extends ItemAction> codec = MapCodec.unit(this);

         @NotNull
         @Override
         public MapCodec<? extends ItemAction> codec() {
            return this.codec;
         }

         @Override
         public void execute(@NotNull Level level, @NotNull Entity source, @NotNull SlotAccess access) {
            action.accept(level, source, access);
         }
      }).codec();
   }
}
