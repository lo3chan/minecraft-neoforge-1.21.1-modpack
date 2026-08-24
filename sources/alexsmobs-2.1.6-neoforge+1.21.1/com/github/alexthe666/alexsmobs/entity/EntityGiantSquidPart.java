package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.message.MessageInteractMultipart;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

public class EntityGiantSquidPart extends PartEntity<EntityGiantSquid> implements IHurtableMultipart {
   private final EntityDimensions size;
   public float scale = 1.0F;
   private boolean collisionOnly = false;

   public EntityGiantSquidPart(EntityGiantSquid parent, float sizeX, float sizeY) {
      super(parent);
      AMCompat.assignClientPartId(this);
      this.size = EntityDimensions.scalable(sizeX, sizeY);
      this.refreshDimensions();
   }

   public EntityGiantSquidPart(EntityGiantSquid parent, float sizeX, float sizeY, boolean collisionOnly) {
      this(parent, sizeX, sizeY);
      this.collisionOnly = collisionOnly;
   }

   public boolean fireImmune() {
      return true;
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, this.getEyeHeight() * 0.15000000596046448, this.getBbWidth() * 0.1F);
   }

   protected void collideWithNearbyEntities() {
   }

   public InteractionResult interact(Player player, InteractionHand hand) {
      if (this.level().isClientSide() && this.getParent() != null) {
         AlexsMobs.sendMSGToServer(new MessageInteractMultipart(((EntityGiantSquid)this.getParent()).getId(), hand == InteractionHand.OFF_HAND));
      }

      return this.getParent() == null ? InteractionResult.PASS : ((EntityGiantSquid)this.getParent()).mobInteract(player, hand);
   }

   public boolean canBeCollidedWith() {
      return !this.collisionOnly;
   }

   protected void collideWithEntity(Entity entityIn) {
      if (!this.collisionOnly) {
         entityIn.push(this);
      }
   }

   public boolean isPickable() {
      return !this.collisionOnly;
   }

   @Nullable
   public ItemStack getPickResult() {
      Entity parent = this.getParent();
      return parent != null ? parent.getPickResult() : ItemStack.EMPTY;
   }

   public boolean hurt(DamageSource source, float amount) {
      if (this.level().isClientSide() && this.getParent() != null && !AMCompat.isInvulnerableTo(this.getParent(), source) && !this.collisionOnly) {
         AlexsMobs.sendMSGToServer(new MessageHurtMultipart(this.getId(), ((EntityGiantSquid)this.getParent()).getId(), amount, source.getMsgId()));
      }

      return !this.collisionOnly && !AMCompat.isInvulnerableTo(this, source) && ((EntityGiantSquid)this.getParent()).attackEntityPartFrom(this, source, amount);
   }

   public boolean is(Entity entityIn) {
      return this == entityIn || this.getParent() == entityIn;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      throw new UnsupportedOperationException();
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return this.size == null ? EntityDimensions.scalable(0.0F, 0.0F) : this.size.scale(this.scale);
   }

   protected void defineSynchedData(Builder builder) {
   }

   public void tick() {
      super.tick();
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
   }

   @Override
   public void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource) {
      parent.hurt(damageSource, damage);
   }
}
