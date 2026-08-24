package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.message.MessageInteractMultipart;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.entity.PartEntity;

public class EntityCachalotPart extends PartEntity<EntityCachalotWhale> {
   private final EntityDimensions size;
   public float scale = 1.0F;

   public EntityCachalotPart(EntityCachalotWhale parent, float sizeX, float sizeY) {
      super(parent);
      AMCompat.assignClientPartId(this);
      this.size = EntityDimensions.scalable(sizeX, sizeY);
      this.refreshDimensions();
   }

   public EntityCachalotPart(EntityCachalotWhale entityCachalotWhale, float sizeX, float sizeY, EntityDimensions size) {
      super(entityCachalotWhale);
      AMCompat.assignClientPartId(this);
      this.size = size;
   }

   protected void collideWithNearbyEntities() {
      List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2));
      Entity parent = this.getParent();
      if (parent != null) {
         entities.stream()
            .filter(
               entity -> entity != parent
                  && (!(entity instanceof EntityCachalotPart) || ((EntityCachalotPart)entity).getParent() != parent)
                  && entity.isPushable()
            )
            .forEach(entity -> entity.push(parent));
      }
   }

   public InteractionResult interact(Player player, InteractionHand hand) {
      if (this.level().isClientSide() && this.getParent() != null) {
         AlexsMobs.sendMSGToServer(new MessageInteractMultipart(((EntityCachalotWhale)this.getParent()).getId(), hand == InteractionHand.OFF_HAND));
      }

      return this.getParent() == null ? InteractionResult.PASS : ((EntityCachalotWhale)this.getParent()).mobInteract(player, hand);
   }

   protected void collideWithEntity(Entity entityIn) {
      entityIn.push(this);
   }

   public boolean isPickable() {
      return true;
   }

   @Nullable
   public ItemStack getPickResult() {
      Entity parent = this.getParent();
      return parent != null ? parent.getPickResult() : ItemStack.EMPTY;
   }

   public boolean hurt(DamageSource source, float amount) {
      if (this.level().isClientSide() && this.getParent() != null && !AMCompat.isInvulnerableTo(this.getParent(), source)) {
         ResourceLocation key = ((Registry)this.level().registryAccess().registry(Registries.DAMAGE_TYPE).get()).getKey(source.type());
         if (key != null) {
            AlexsMobs.sendMSGToServer(new MessageHurtMultipart(this.getId(), ((EntityCachalotWhale)this.getParent()).getId(), amount, key.toString()));
         }
      }

      return !AMCompat.isInvulnerableTo(this, source) && ((EntityCachalotWhale)this.getParent()).attackEntityPartFrom(this, source, amount);
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
}
