package net.bettercombat.mixin.player;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({LivingEntity.class})
public interface LivingEntityAccessor {
   @Accessor
   int getAttackStrengthTicker();

   @Accessor("attackStrengthTicker")
   void setLastAttackedTicks(int var1);

   @Invoker("tickHeadTurn")
   float invokeTurnHead(float var1, float var2);
}
