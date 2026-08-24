package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityEnderiophageRocket extends FireworkRocketEntity {
   private int phageAge = 0;

   public EntityEnderiophageRocket(EntityType p_i50164_1_, Level p_i50164_2_) {
      super(p_i50164_1_, p_i50164_2_);
   }

   public EntityEnderiophageRocket(Level worldIn, double x, double y, double z, ItemStack givenItem) {
      super(AMEntityRegistry.ENDERIOPHAGE_ROCKET.get(), worldIn);
      this.setPos(x, y, z);
      if (!givenItem.isEmpty() && AMCompat.hasTag(givenItem)) {
         this.entityData.set(DATA_ID_FIREWORKS_ITEM, givenItem.copy());
      }

      this.setDeltaMovement(this.random.nextGaussian() * 0.001, 0.05, this.random.nextGaussian() * 0.001);
      this.lifetime = 18 + this.random.nextInt(14);
   }

   public EntityEnderiophageRocket(
      Level p_i231581_1_, @Nullable Entity p_i231581_2_, double p_i231581_3_, double p_i231581_5_, double p_i231581_7_, ItemStack p_i231581_9_
   ) {
      this(p_i231581_1_, p_i231581_3_, p_i231581_5_, p_i231581_7_, p_i231581_9_);
      this.setOwner(p_i231581_2_);
   }

   public EntityEnderiophageRocket(Level p_i47367_1_, ItemStack p_i47367_2_, LivingEntity p_i47367_3_) {
      this(p_i47367_1_, p_i47367_3_, p_i47367_3_.getX(), p_i47367_3_.getY(), p_i47367_3_.getZ(), p_i47367_2_);
      this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.of(p_i47367_3_.getId()));
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   public void tick() {
      super.tick();
      this.phageAge++;
      if (this.level().isClientSide()) {
         this.level()
            .addParticle(
               ParticleTypes.END_ROD,
               this.getX(),
               this.getY() - 0.3,
               this.getZ(),
               this.random.nextGaussian() * 0.05,
               -this.getDeltaMovement().y * 0.5,
               this.random.nextGaussian() * 0.05
            );
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 17) {
         this.level()
            .addParticle(
               ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05
            );

         for (int i = 0; i < this.random.nextInt(15) + 30; i++) {
            this.level()
               .addParticle(
                  (ParticleOptions)AMParticleRegistry.DNA.get(),
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  this.random.nextGaussian() * 0.25,
                  this.random.nextGaussian() * 0.25,
                  this.random.nextGaussian() * 0.25
               );
         }

         for (int i = 0; i < this.random.nextInt(15) + 15; i++) {
            this.level()
               .addParticle(
                  ParticleTypes.END_ROD,
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  this.random.nextGaussian() * 0.15,
                  this.random.nextGaussian() * 0.15,
                  this.random.nextGaussian() * 0.15
               );
         }

         SoundEvent soundEvent = AlexsMobs.PROXY.isFarFromCamera(this.getX(), this.getY(), this.getZ())
            ? SoundEvents.FIREWORK_ROCKET_BLAST
            : SoundEvents.FIREWORK_ROCKET_BLAST_FAR;
         this.level()
            .playLocalSound(this.getX(), this.getY(), this.getZ(), soundEvent, SoundSource.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
      } else {
         super.handleEntityEvent(id);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      return new ItemStack((ItemLike)AMItemRegistry.ENDERIOPHAGE_ROCKET.get());
   }
}
