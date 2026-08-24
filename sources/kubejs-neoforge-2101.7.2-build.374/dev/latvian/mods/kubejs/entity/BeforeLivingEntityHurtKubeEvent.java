package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

@Info("Invoked before an entity is hurt by a damage source.\n")
public class BeforeLivingEntityHurtKubeEvent implements KubeLivingEntityEvent {
   private final Pre event;

   public BeforeLivingEntityHurtKubeEvent(Pre event) {
      this.event = event;
   }

   @Info("The entity that was hurt.")
   @Override
   public LivingEntity getEntity() {
      return this.event.getEntity();
   }

   @Info("The damage source.")
   public DamageSource getSource() {
      return this.event.getContainer().getSource();
   }

   @Info("The amount of damage.")
   public float getDamage() {
      return this.event.getContainer().getNewDamage();
   }

   public void setDamage(float damage) {
      this.event.getContainer().setNewDamage(damage);
   }
}
