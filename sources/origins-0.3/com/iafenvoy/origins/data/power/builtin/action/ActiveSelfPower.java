package com.iafenvoy.origins.data.power.builtin.action;

import com.google.common.collect.ImmutableSet.Builder;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.KeySettings;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.badge.PresetBadges;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ActiveSelfPower extends HasCooldownPower implements Toggleable {
   public static final MapCodec<ActiveSelfPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            HasCooldownPower.CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            KeySettings.CODEC.forGetter(ActiveSelfPower::getKey),
            EntityAction.CODEC.fieldOf("entity_action").forGetter(ActiveSelfPower::getEntityAction)
         )
         .apply(i, ActiveSelfPower::new)
   );
   private final KeySettings key;
   private final EntityAction entityAction;

   public ActiveSelfPower(Power.BaseSettings settings, HasCooldownPower.CooldownSettings cooldown, KeySettings key, EntityAction entityAction) {
      super(settings, cooldown);
      this.key = key;
      this.entityAction = entityAction;
   }

   @Override
   public KeySettings getKey() {
      return this.key;
   }

   public EntityAction getEntityAction() {
      return this.entityAction;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @Override
   public void collectBadges(Builder<Badge> builder) {
      super.collectBadges(builder);
      builder.add(PresetBadges.ACTIVE);
   }

   @Override
   public void toggle(@NotNull OriginDataHolder holder, String key) {
      if (this.key.match(key) && this.isActive(holder)) {
         this.getCooldownComponent(holder).useIfReady(() -> this.entityAction.execute(holder.getEntity()));
      }
   }
}
