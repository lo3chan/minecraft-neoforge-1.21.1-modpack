package at.petrak.hexcasting.mixin.accessor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({LivingEntity.class})
public interface AccessorLivingEntity {
   @Accessor("lastHurt")
   float hex$getLastHurt();

   @Accessor("lastHurt")
   void hex$setLastHurt(float var1);

   @Invoker("playHurtSound")
   void hex$playHurtSound(DamageSource var1);

   @Invoker("checkTotemDeathProtection")
   boolean hex$checkTotemDeathProtection(DamageSource var1);

   @Invoker("getDeathSound")
   SoundEvent hex$getDeathSound();

   @Invoker("getSoundVolume")
   float hex$getSoundVolume();

   @Accessor("lastDamageSource")
   void hex$setLastDamageSource(DamageSource var1);

   @Accessor("lastDamageStamp")
   void hex$setLastDamageStamp(long var1);
}
