package vazkii.psi.common.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.Psi;

public class EntitySpellProjectile extends ThrowableProjectile {
   protected static final EntityDataAccessor<Optional<UUID>> ATTACKTARGET_UUID = SynchedEntityData.defineId(
      EntitySpellProjectile.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final String TAG_COLORIZER = "colorizer";
   private static final String TAG_BULLET = "bullet";
   private static final String TAG_TIME_ALIVE = "timeAlive";
   private static final String TAG_LAST_MOTION_X = "lastMotionX";
   private static final String TAG_LAST_MOTION_Y = "lastMotionY";
   private static final String TAG_LAST_MOTION_Z = "lastMotionZ";
   private static final EntityDataAccessor<ItemStack> COLORIZER_DATA = SynchedEntityData.defineId(EntitySpellProjectile.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<ItemStack> BULLET_DATA = SynchedEntityData.defineId(EntitySpellProjectile.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<Optional<UUID>> CASTER_UUID = SynchedEntityData.defineId(
      EntitySpellProjectile.class, EntityDataSerializers.OPTIONAL_UUID
   );
   public SpellContext context;
   public int timeAlive;

   public EntitySpellProjectile(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
      super(type, worldIn);
   }

   protected EntitySpellProjectile(EntityType<? extends ThrowableProjectile> type, Level world, LivingEntity thrower) {
      super(type, thrower, world);
      this.setOwner(thrower);
      this.setRot(thrower.getYRot() + 180.0F, -thrower.getXRot());
      float f = 1.5F;
      double mx = Mth.sin(this.getYRot() / 180.0F * 3.1415927F) * Mth.cos(this.getXRot() / 180.0F * 3.1415927F) * f / 2.0;
      double mz = -(Mth.cos(this.getYRot() / 180.0F * 3.1415927F) * Mth.cos(this.getXRot() / 180.0F * 3.1415927F) * f) / 2.0;
      double my = Mth.sin(this.getXRot() / 180.0F * 3.1415927F) * f / 2.0;
      this.push(mx, my, mz);
   }

   public EntitySpellProjectile(Level world, LivingEntity thrower) {
      this(ModEntities.spellProjectile, world, thrower);
   }

   public EntitySpellProjectile setInfo(Player player, ItemStack colorizer, ItemStack bullet) {
      this.entityData.set(COLORIZER_DATA, colorizer);
      this.entityData.set(BULLET_DATA, bullet.copy());
      this.entityData.set(CASTER_UUID, Optional.of(player.getUUID()));
      this.entityData.set(ATTACKTARGET_UUID, Optional.empty());
      return this;
   }

   protected void defineSynchedData(Builder pBuilder) {
      pBuilder.define(COLORIZER_DATA, ItemStack.EMPTY);
      pBuilder.define(BULLET_DATA, ItemStack.EMPTY);
      pBuilder.define(CASTER_UUID, Optional.empty());
      pBuilder.define(ATTACKTARGET_UUID, Optional.empty());
   }

   public void addAdditionalSaveData(@NotNull CompoundTag tagCompound) {
      super.addAdditionalSaveData(tagCompound);
      Tag colorizerCmp = new CompoundTag();
      ItemStack colorizer = (ItemStack)this.entityData.get(COLORIZER_DATA);
      if (!colorizer.isEmpty()) {
         colorizerCmp = colorizer.save(this.registryAccess(), colorizerCmp);
      }

      tagCompound.put("colorizer", colorizerCmp);
      Tag bulletCmp = new CompoundTag();
      ItemStack bullet = (ItemStack)this.entityData.get(BULLET_DATA);
      if (!bullet.isEmpty()) {
         bulletCmp = bullet.save(this.registryAccess(), bulletCmp);
      }

      tagCompound.put("bullet", bulletCmp);
      tagCompound.putInt("timeAlive", this.timeAlive);
      tagCompound.putDouble("lastMotionX", this.getDeltaMovement().x());
      tagCompound.putDouble("lastMotionY", this.getDeltaMovement().y());
      tagCompound.putDouble("lastMotionZ", this.getDeltaMovement().z());
   }

   public void readAdditionalSaveData(@NotNull CompoundTag tagCompound) {
      super.readAdditionalSaveData(tagCompound);
      CompoundTag colorizerCmp = tagCompound.getCompound("colorizer");
      ItemStack colorizer = ItemStack.parseOptional(this.registryAccess(), colorizerCmp);
      this.entityData.set(COLORIZER_DATA, colorizer);
      CompoundTag bulletCmp = tagCompound.getCompound("bullet");
      ItemStack bullet = ItemStack.parseOptional(this.registryAccess(), bulletCmp);
      this.entityData.set(BULLET_DATA, bullet);
      Entity thrower = this.getOwner();
      if (thrower instanceof Player) {
         this.entityData.set(CASTER_UUID, Optional.of(thrower.getUUID()));
      }

      this.timeAlive = tagCompound.getInt("timeAlive");
      double lastMotionX = tagCompound.getDouble("lastMotionX");
      double lastMotionY = tagCompound.getDouble("lastMotionY");
      double lastMotionZ = tagCompound.getDouble("lastMotionZ");
      this.setDeltaMovement(lastMotionX, lastMotionY, lastMotionZ);
   }

   public void tick() {
      super.tick();
      int timeAlive = this.tickCount;
      if (timeAlive > this.getLiveTime()) {
         this.remove(RemovalReason.DISCARDED);
      }

      ItemStack colorizer = (ItemStack)this.entityData.get(COLORIZER_DATA);
      int colorVal = Psi.proxy.getColorForColorizer(colorizer);
      float r = PsiRenderHelper.r(colorVal) / 255.0F;
      float g = PsiRenderHelper.g(colorVal) / 255.0F;
      float b = PsiRenderHelper.b(colorVal) / 255.0F;
      double x = this.getX();
      double y = this.getY();
      double z = this.getZ();
      Vector3 lookOrig = new Vector3(this.getDeltaMovement()).normalize();

      for (int i = 0; i < this.getParticleCount(); i++) {
         Vector3 look = lookOrig.copy();
         double spread = 0.6;
         double dist = 0.15;
         if (this instanceof EntitySpellGrenade) {
            look.y++;
            dist = 0.05;
         }

         look.x = look.x + (Math.random() - 0.5) * spread;
         look.y = look.y + (Math.random() - 0.5) * spread;
         look.z = look.z + (Math.random() - 0.5) * spread;
         look.normalize().multiply(dist);
         if (this.level().isClientSide()) {
            Psi.proxy.sparkleFX(x, y, z, r, g, b, (float)look.x, (float)look.y, (float)look.z, 1.2F, 12);
         }
      }
   }

   public int getLiveTime() {
      return 600;
   }

   public int getParticleCount() {
      return 5;
   }

   protected void onHit(@NotNull HitResult pos) {
      if (pos instanceof EntityHitResult && ((EntityHitResult)pos).getEntity() instanceof LivingEntity) {
         this.cast(context -> {
            if (context != null) {
               context.attackedEntity = (LivingEntity)((EntityHitResult)pos).getEntity();
            }
         });
      } else {
         this.cast();
      }
   }

   public void cast() {
      this.cast(null);
   }

   public void cast(Consumer<SpellContext> callback) {
      Entity thrower = this.getOwner();
      boolean canCast = false;
      if (thrower instanceof Player) {
         ItemStack spellContainer = (ItemStack)this.entityData.get(BULLET_DATA);
         if (!spellContainer.isEmpty() && ISpellAcceptor.isContainer(spellContainer)) {
            Spell spell = ISpellAcceptor.acceptor(spellContainer).getSpell();
            if (spell != null) {
               canCast = true;
               if (this.context == null) {
                  this.context = new SpellContext().setPlayer((Player)thrower).setFocalPoint(this).setSpell(spell);
               }

               this.context.setFocalPoint(this);
            }
         }
      }

      if (callback != null) {
         callback.accept(this.context);
      }

      if (canCast && this.context != null) {
         this.context.cspell.safeExecute(this.context);
      }

      this.remove(RemovalReason.DISCARDED);
   }

   public Entity getOwner() {
      Entity superThrower = super.getOwner();
      return superThrower != null
         ? superThrower
         : (Entity)((Optional)this.entityData.get(CASTER_UUID)).<Player>map(u -> this.getCommandSenderWorld().getPlayerByUUID(u)).orElse(null);
   }

   public LivingEntity getAttackTarget() {
      double radiusVal = 32.0;
      Vector3 positionVal = Vector3.fromVec3d(this.position());
      AABB axis = new AABB(
         positionVal.x - radiusVal,
         positionVal.y - radiusVal,
         positionVal.z - radiusVal,
         positionVal.x + radiusVal,
         positionVal.y + radiusVal,
         positionVal.z + radiusVal
      );
      return ((Optional)this.entityData.get(ATTACKTARGET_UUID)).<LivingEntity>map(u -> {
         List<LivingEntity> a = this.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, axis, e -> e.getUUID().equals(u));
         return !a.isEmpty() ? (LivingEntity)a.getFirst() : null;
      }).orElse(null);
   }

   protected double getDefaultGravity() {
      return 0.0;
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }
}
