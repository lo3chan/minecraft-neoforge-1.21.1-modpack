package net.cibernet.alchemancy.properties.voidborn;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.jetbrains.annotations.Nullable;

public class VoidtouchProperty extends Property {
   public static final ResourceKey<DamageType> VOIDTOUCH_DAMAGE_KEY = ResourceKey.create(
      Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("alchemancy", "voidtouch")
   );

   @Override
   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
      this.destroyEntity(target, user);
      this.consumeItem(user, weapon, EquipmentSlot.MAINHAND);
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (rayTraceResult instanceof EntityHitResult entityHitResult) {
         this.destroyEntity(entityHitResult.getEntity(), projectile.getOwner(), projectile);
         if (event.isCanceled()) {
            this.consumeItem(projectile, stack, EquipmentSlot.MAINHAND);
         }

         projectile.discard();
      }
   }

   public static DamageSource voidDamage(DamageSources damageSources, @Nullable Entity source, @Nullable Entity directEntity, Vec3 position) {
      return new DamageSource(damageSources.damageTypes.getHolderOrThrow(VOIDTOUCH_DAMAGE_KEY), source, directEntity, position);
   }

   private void destroyEntity(Entity target, @Nullable Entity user) {
      this.destroyEntity(target, user, user);
   }

   private void destroyEnderDragon(EnderDragon enderDragon, @Nullable Entity user, @Nullable Entity directSource) {
      enderDragon.hurt(
         voidDamage(enderDragon.damageSources(), user, directSource, directSource == null ? enderDragon.position() : directSource.position()), 3.4028235E38F
      );
      if (enderDragon.getDragonFight() != null) {
         enderDragon.getDragonFight().setDragonKilled(enderDragon);
      }

      enderDragon.discard();
   }

   private void destroyEntity(Entity target, @Nullable Entity user, @Nullable Entity directSource) {
      if (!target.level().isClientSide()
         && !(target instanceof LivingEntity living && InfusedPropertiesHelper.hasItemWithProperty(living, AlchemancyProperties.VOIDBORN, true))) {
         switch (target) {
            case EnderDragon enderDragon:
               this.destroyEnderDragon(enderDragon, user, directSource);
               break;
            case EnderDragonPart enderDragonPart:
               this.destroyEnderDragon(enderDragonPart.parentMob, user, directSource);
               break;
            case Player player:
               target.hurt(
                  voidDamage(target.damageSources(), user, directSource, directSource == null ? target.position() : directSource.position()), 3.4028235E38F
               );
               if (player.isDeadOrDying()) {
                  player.discard();
               }
               break;
            case null:
            default:
               target.discard();
         }
      } else {
         VoidbornProperty.playEffects(target);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(1.0F, 2556072, 4849778);
   }
}
