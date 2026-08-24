package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnProjectileHitPower extends HasCooldownPower {
   public static final MapCodec<ActionOnProjectileHitPower> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            HasCooldownPower.CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ActionOnProjectileHitPower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnProjectileHitPower::getBiEntityCondition),
            BiEntityAction.optionalCodec("owner_bientity_action").forGetter(ActionOnProjectileHitPower::getOwnerBiEntityAction),
            BiEntityCondition.optionalCodec("owner_bientity_condition").forGetter(ActionOnProjectileHitPower::getOwnerBiEntityCondition)
         )
         .apply(instance, ActionOnProjectileHitPower::new)
   );
   private final BiEntityAction bientityAction;
   private final BiEntityCondition bientityCondition;
   private final BiEntityAction ownerBientityAction;
   private final BiEntityCondition ownerBientityCondition;

   public ActionOnProjectileHitPower(
      Power.BaseSettings settings,
      HasCooldownPower.CooldownSettings cooldown,
      BiEntityAction bientityAction,
      BiEntityCondition bientityCondition,
      BiEntityAction ownerBientityAction,
      BiEntityCondition ownerBientityCondition
   ) {
      super(settings, cooldown);
      this.bientityAction = bientityAction;
      this.bientityCondition = bientityCondition;
      this.ownerBientityAction = ownerBientityAction;
      this.ownerBientityCondition = ownerBientityCondition;
   }

   public BiEntityAction getBiEntityAction() {
      return this.bientityAction;
   }

   public BiEntityCondition getBiEntityCondition() {
      return this.bientityCondition;
   }

   public BiEntityAction getOwnerBiEntityAction() {
      return this.ownerBientityAction;
   }

   public BiEntityCondition getOwnerBiEntityCondition() {
      return this.ownerBientityCondition;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @SubscribeEvent
   public static void onImpact(ProjectileImpactEvent event) {
      if (event.getRayTraceResult() instanceof EntityHitResult hit) {
         Projectile projectile = event.getProjectile();
         Entity owner = projectile.getOwner();
         if (owner != null) {
            Entity target = hit.getEntity();
            PowerHelper.get(owner)
               .execute(
                  ActionOnProjectileHitPower.class,
                  power -> power.bientityCondition.test(projectile, target) || power.ownerBientityCondition.test(owner, target),
                  (holder, power) -> power.getCooldownComponent(holder).useIfReady(() -> {
                     if (power.bientityCondition.test(projectile, target)) {
                        power.bientityAction.execute(projectile, target);
                     }

                     if (power.ownerBientityCondition.test(owner, target)) {
                        power.ownerBientityAction.execute(owner, target);
                     }
                  })
               );
         }
      }
   }
}
