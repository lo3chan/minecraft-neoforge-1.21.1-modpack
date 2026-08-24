@file:JvmName(name = "AccessorWrappers")

package at.petrak.hexcasting.ktxt

import at.petrak.hexcasting.mixin.accessor.AccessorEntity
import at.petrak.hexcasting.mixin.accessor.AccessorLivingEntity
import at.petrak.hexcasting.mixin.accessor.AccessorUseOnContext
import at.petrak.hexcasting.mixin.accessor.AccessorVillager
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult

public final var lastHurt: Float
   public final get() {
      return (`$this$lastHurt` as AccessorLivingEntity).hex$getLastHurt();
   }

   public final set(value) {
      (`$this$lastHurt` as AccessorLivingEntity).hex$setLastHurt(value);
   }


public final val deathSoundAccessor: SoundEvent?
   public final get() {
      return (`$this$deathSoundAccessor` as AccessorLivingEntity).hex$getDeathSound();
   }


public final val soundVolumeAccessor: Float
   public final get() {
      return (`$this$soundVolumeAccessor` as AccessorLivingEntity).hex$getSoundVolume();
   }


public fun LivingEntity.playHurtSound(source: DamageSource) {
   (`$this$playHurtSound` as AccessorLivingEntity).hex$playHurtSound(source);
}

public fun LivingEntity.checkTotemDeathProtection(source: DamageSource): Boolean {
   return (`$this$checkTotemDeathProtection` as AccessorLivingEntity).hex$checkTotemDeathProtection(source);
}

public fun LivingEntity.setHurtWithStamp(source: DamageSource, stamp: Long): AccessorLivingEntity {
   val var4: AccessorLivingEntity = `$this$setHurtWithStamp` as AccessorLivingEntity;
   (`$this$setHurtWithStamp` as AccessorLivingEntity).hex$setLastDamageSource(source);
   var4.hex$setLastDamageStamp(stamp);
   return var4;
}

public fun Entity.markHurt() {
   (`$this$markHurt` as AccessorEntity).hex$markHurt();
}

public fun Villager.tellWitnessesThatIWasMurdered(murderer: Entity) {
   (`$this$tellWitnessesThatIWasMurdered` as AccessorVillager).hex$tellWitnessesThatIWasMurdered(murderer);
}

public fun UseOnContext(level: Level, player: Player?, hand: InteractionHand, stack: ItemStack, hitResult: BlockHitResult): UseOnContext {
   val var10000: UseOnContext = AccessorUseOnContext.hex$new(level, player, hand, stack, hitResult);
   return var10000;
}
