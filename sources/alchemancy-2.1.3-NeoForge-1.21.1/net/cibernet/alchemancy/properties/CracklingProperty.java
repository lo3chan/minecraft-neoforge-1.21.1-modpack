package net.cibernet.alchemancy.properties;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.network.S2CPlayFireworksPayload;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.FireworkExplosion.Shape;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class CracklingProperty extends Property implements IDataHolder<Fireworks> {
   private static final float timePerStar = 2.0F;
   private static final Fireworks DEFAULT = new Fireworks(0, List.of(new FireworkExplosion(Shape.SMALL_BALL, IntList.of(16777215), IntList.of(), false, false)));

   @Override
   public void onCriticalAttack(@Nullable Player user, ItemStack weapon, Entity target) {
      if (target.level() instanceof ServerLevel level) {
         Fireworks fireworks = this.getData(weapon);
         Vec3 targetPos = target.getEyePosition();
         float damage = 2.0F;
         if (fireworks != null) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new S2CPlayFireworksPayload(fireworks, targetPos), new CustomPacketPayload[0]);
            List<FireworkExplosion> list = fireworks.explosions();
            if (!list.isEmpty()) {
               damage = 5.0F + list.size() * 2;
            }
         } else {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new S2CPlayFireworksPayload(DEFAULT, targetPos), new CustomPacketPayload[0]);
         }

         for (LivingEntity livingentity : level.getEntitiesOfClass(LivingEntity.class, CommonUtils.boundingBoxAroundPoint(targetPos, 5.0F))) {
            if (target != user && !(targetPos.distanceToSqr(livingentity.position()) > 25.0)) {
               boolean flag = false;

               for (int i = 0; i < 2; i++) {
                  Vec3 vec31 = new Vec3(livingentity.getX(), livingentity.getY(0.5 * i), livingentity.getZ());
                  HitResult hitresult = level.clip(new ClipContext(targetPos, vec31, Block.COLLIDER, Fluid.NONE, target));
                  if (hitresult.getType() == Type.MISS) {
                     flag = true;
                     break;
                  }
               }

               if (flag) {
                  float localDamage = damage * (float)Math.sqrt((5.0 - targetPos.distanceTo(livingentity.position())) / 5.0);
                  livingentity.hurt(level.damageSources().source(DamageTypes.FIREWORKS, user, user), localDamage);
               }
            }
         }

         this.damageOrConsumeItem(target.level(), user, weapon, EquipmentSlot.MAINHAND, (int)(damage / 2.0F));
      }
   }

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      Fireworks stackFireworks = (Fireworks)propertySource.get(DataComponents.FIREWORKS);
      if (stackFireworks == null && propertySource.has(DataComponents.FIREWORK_EXPLOSION)) {
         stackFireworks = new Fireworks(0, List.of((FireworkExplosion)propertySource.get(DataComponents.FIREWORK_EXPLOSION)));
      }

      if (stackFireworks != null && stackFireworks.explosions().isEmpty()) {
         return false;
      } else {
         if (InfusedPropertiesHelper.hasInfusedProperty(stack, this.asHolder())) {
            List<? extends Object> stackExplosions = stackFireworks == null ? List.of() : stackFireworks.explosions();
            Fireworks currentFireworks = this.getData(stack);
            List<? extends Object> currentExplosions = currentFireworks == null ? List.of() : currentFireworks.explosions();
            if (stackExplosions == currentExplosions) {
               return false;
            }
         }

         if (consumeItem) {
            this.setData(stack, stackFireworks);
         }

         return true;
      }
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Fireworks fireworks = this.getData(stack);
      if (fireworks != null && !fireworks.explosions().isEmpty()) {
         List<FireworkExplosion> explosions = fireworks.explosions();
         int currentIndex = (int)Math.abs(System.currentTimeMillis() / 2000.0 % explosions.size());
         FireworkExplosion explosion = explosions.get(currentIndex);
         MutableComponent colors = Component.empty();

         for (int i = 0; i < explosion.colors().size(); i++) {
            colors = colors.append(
               Component.translatable("property.detail.star")
                  .withColor(
                     explosion.fadeColors().isEmpty()
                        ? explosion.colors().getInt(i)
                        : ColorUtils.interpolateColorsOverTime(
                           2.0F, explosion.colors().getInt(i), explosion.fadeColors().getInt(Math.min(i, explosion.fadeColors().size() - 1))
                        )
                  )
            );
         }

         return Component.translatable(
               "property.crackling_detail", new Object[]{super.getDisplayText(stack), colors, explosion.shape().getName().withStyle(ChatFormatting.WHITE)}
            )
            .withColor(this.getColor(stack));
      } else {
         return super.getDisplayText(stack);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(0.5F, 14035498, 16746632);
   }

   public Fireworks readData(CompoundTag tag) {
      return tag.contains("fireworks") ? (Fireworks)Fireworks.CODEC.parse(NbtOps.INSTANCE, tag.get("fireworks")).getOrThrow() : null;
   }

   public CompoundTag writeData(final Fireworks data) {
      return new CompoundTag() {
         {
            if (data != null) {
               this.put("fireworks", (Tag)Fireworks.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow());
            }
         }
      };
   }

   public Fireworks getDefaultData() {
      return DEFAULT;
   }
}
