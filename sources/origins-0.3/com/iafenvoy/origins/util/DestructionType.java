package com.iafenvoy.origins.util;

import com.mojang.serialization.Codec;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Explosion.BlockInteraction;
import org.jetbrains.annotations.NotNull;

public enum DestructionType implements StringRepresentable {
   NONE(BlockInteraction.KEEP),
   BREAK(BlockInteraction.DESTROY_WITH_DECAY),
   DESTROY(BlockInteraction.DESTROY);

   public static final Codec<DestructionType> CODEC = StringRepresentable.fromEnum(DestructionType::values);
   private final BlockInteraction interaction;

   private DestructionType(BlockInteraction interaction) {
      this.interaction = interaction;
   }

   public BlockInteraction getInteraction() {
      return this.interaction;
   }

   @NotNull
   public String getSerializedName() {
      return this.name().toLowerCase(Locale.ROOT);
   }
}
