package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.extensions.IPlayerExtension;
import org.jetbrains.annotations.NotNull;

public record AbilityCondition(AbilityCondition.PlayerAbility ability) implements EntityCondition {
   public static final MapCodec<AbilityCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(AbilityCondition.PlayerAbility.CODEC.fieldOf("ability").forGetter(AbilityCondition::ability)).apply(i, AbilityCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Entity entity) {
      return entity instanceof Player player && this.ability.get(player);
   }

   public static enum PlayerAbility implements StringRepresentable {
      FLYING(p -> p.getAbilities().flying),
      INSTABUILD(p -> p.getAbilities().instabuild),
      INVULNERABLE(p -> p.getAbilities().invulnerable),
      MAY_BUILD(p -> p.getAbilities().mayBuild),
      MAYFLY(IPlayerExtension::mayFly);

      public static final Codec<AbilityCondition.PlayerAbility> CODEC = StringRepresentable.fromValues(AbilityCondition.PlayerAbility::values);
      private final Predicate<Player> getter;

      private PlayerAbility(Predicate<Player> getter) {
         this.getter = getter;
      }

      public boolean get(Player player) {
         return this.getter.test(player);
      }

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
