package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public abstract class EntityMixin {
   @Shadow
   @Nullable
   public abstract Entity getVehicle();

   @Shadow
   public abstract int getId();

   @Inject(
      method = {"onBelowWorld"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onBelowWorld(CallbackInfo ci) {
      if (this instanceof ItemEntity itemEntity) {
         ItemStack stack = itemEntity.getItem();
         AtomicBoolean cancelled = new AtomicBoolean(false);
         InfusedPropertiesHelper.forEachProperty(stack, property -> {
            if (((Property)property.value()).onEntityItemBelowWorld(stack, itemEntity)) {
               cancelled.set(true);
            }
         });
         if (cancelled.get()) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"},
      at = {@At("HEAD")}
   )
   public void playSound(SoundEvent sound, float volume, float pitch, CallbackInfo ci, @Local(ordinal = 0,argsOnly = true) LocalFloatRef volumeRef) {
      boolean isPlayer = this instanceof Player;
      if (this instanceof LivingEntity living) {
         float muffleMod = 1.0F;

         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.isArmor() && InfusedPropertiesHelper.hasInfusedProperty(living.getItemBySlot(slot), AlchemancyProperties.MUFFLED)) {
               if (!isPlayer) {
                  muffleMod = 0.0F;
                  break;
               }

               muffleMod -= 0.25F;
            }
         }

         volumeRef.set(volume * muffleMod);
      }
   }
}
