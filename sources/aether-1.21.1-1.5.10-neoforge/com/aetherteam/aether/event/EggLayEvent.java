package com.aetherteam.aether.event;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class EggLayEvent extends EntityEvent implements ICancellableEvent {
   @Nullable
   private ItemStack item;
   @Nullable
   private SoundEvent sound;
   private float volume;
   private float pitch;

   public EggLayEvent(Entity entity, @Nullable SoundEvent sound, float volume, float pitch, @Nullable ItemStack item) {
      super(entity);
      this.sound = sound;
      this.volume = volume;
      this.pitch = pitch;
      this.item = item;
   }

   @Nullable
   public ItemStack getItem() {
      return this.item;
   }

   public void setItem(@Nullable ItemStack item) {
      this.item = item;
   }

   @Nullable
   public SoundEvent getSound() {
      return this.sound;
   }

   public void setSound(@Nullable SoundEvent sound) {
      this.sound = sound;
   }

   public float getVolume() {
      return this.volume;
   }

   public void setVolume(float volume) {
      this.volume = volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float pitch) {
      this.pitch = pitch;
   }
}
