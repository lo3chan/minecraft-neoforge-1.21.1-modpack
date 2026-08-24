package net.cibernet.alchemancy.properties;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class CapturingProperty extends Property implements IDataHolder<CapturingProperty.EntityData<?>> {
   private static final CapturingProperty.EntityData<?> DEFAULT = new CapturingProperty.EntityData(null, new CompoundTag(), Component.empty());

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (rayTraceResult.getType() == Type.ENTITY
         && rayTraceResult instanceof EntityHitResult entityHitResult
         && this.captureMob(entityHitResult.getEntity(), stack)) {
         projectile.spawnAtLocation(stack);
         projectile.discard();
         event.setCanceled(true);
      } else if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.LOYAL)) {
         this.releaseMob(projectile.level(), stack, projectile.position(), projectile.getDeltaMovement().scale(-1.0));
      }
   }

   @Override
   public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource) {
      if (!stack.has(DataComponents.INTANGIBLE_PROJECTILE) && !(itemEntity instanceof AbstractArrow arrow && arrow.pickup == Pickup.DISALLOWED)) {
         this.releaseMob(itemEntity.level(), stack, itemEntity.position(), itemEntity.getDeltaMovement().scale(-1.0));
      }
   }

   @Override
   public void onActivation(Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      Vec3 lookVec = new Vec3(0.0, 1.0, 0.0);
      if (source == null) {
         source = target;
      } else {
         lookVec = source.getLookAngle();
      }

      this.releaseMob(source.level(), stack, source.position().add(lookVec), lookVec);
   }

   @Override
   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      if (this.releaseMob(
         blockSource.level(),
         stack,
         blockSource.pos().relative(direction).getCenter(),
         new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ())
      )) {
         InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
         return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
      } else {
         ServerLevel serverlevel = blockSource.level();
         BlockPos blockpos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
         List<Entity> list = serverlevel.getEntitiesOfClass(Entity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS);
         if (!list.isEmpty() && this.captureMob((Entity)list.getFirst(), stack)) {
            InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
            return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
         } else {
            return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
         }
      }
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      Vec3 lookVec = event.getEntity().getLookAngle();
      if (!event.isCanceled()) {
         if (this.getData(event.getItemStack()).equals(this.getDefaultData())) {
            if (this.releaseMob(event.getLevel(), event.getItemStack(), event.getEntity().position().add(lookVec), lookVec)) {
               event.setCancellationResult(InteractionResult.SUCCESS);
               event.setCanceled(true);
            }
         } else {
            EntityHitResult hitResult = getPlayerPOVHitResult(event.getLevel(), event.getEntity());
            if (hitResult != null) {
               if (this.captureMob(hitResult.getEntity(), event.getItemStack())) {
                  event.setCancellationResult(InteractionResult.SUCCESS);
                  event.setCanceled(true);
               } else {
                  event.getEntity()
                     .displayClientMessage(
                        Component.translatable("property.alchemancy.capturing.cannot_store", new Object[]{hitResult.getEntity().getDisplayName()})
                           .withStyle(ChatFormatting.RED),
                        true
                     );
               }
            }
         }
      }
   }

   public static EntityHitResult getPlayerPOVHitResult(Level level, Player player) {
      Vec3 vec3 = player.getEyePosition();
      Vec3 vec31 = vec3.add(player.calculateViewVector(player.getXRot(), player.getYRot()).scale(player.entityInteractionRange()));
      return ProjectileUtil.getEntityHitResult(level, player, player.position(), vec31, player.getBoundingBox(), entity -> true);
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      Vec3i faceNormal = event.getFace().getNormal();
      if (this.releaseMob(
         event.getLevel(),
         event.getItemStack(),
         event.getPos().relative(event.getFace()).getCenter(),
         new Vec3(faceNormal.getX(), faceNormal.getY(), faceNormal.getZ())
      )) {
         event.setCancellationResult(ItemInteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public void onRightClickEntity(EntityInteractSpecific event) {
      if (this.captureMob(event.getTarget(), event.getItemStack())) {
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      } else {
         event.getEntity()
            .displayClientMessage(
               Component.translatable("property.alchemancy.capturing.cannot_store", new Object[]{event.getTarget().getDisplayName()})
                  .withStyle(ChatFormatting.RED),
               true
            );
      }
   }

   public boolean releaseMob(Level level, ItemStack stack, Vec3 position, @Nullable Vec3 direction) {
      CapturingProperty.EntityData<?> data = this.getData(stack);
      if (!level.isClientSide() && !data.equals(this.getDefaultData())) {
         Entity entity = this.getEntityFromData((CapturingProperty.EntityData<Entity>)data, level);
         if (entity == null) {
            return false;
         } else {
            entity.setPos(position);
            if (direction != null) {
               setMobDirection(entity, direction);
            }

            if (((ServerLevel)level).getEntity(entity.getUUID()) != null) {
               entity.setUUID(Mth.createInsecureUUID(entity.getRandom()));
            }

            level.addFreshEntity(entity);
            this.setData(stack, this.getDefaultData());
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean captureMob(@Nonnull Entity target, ItemStack stack) {
      if (this.getData(stack).equals(this.getDefaultData()) && !target.getType().is(AlchemancyTags.EntityTypes.CANNOT_CAPTURE)) {
         if (!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.NULLIFIER)) {
            this.setData(stack, this.getDataFromEntity(target));
         }

         target.discard();
         return true;
      } else {
         return false;
      }
   }

   public static void setMobDirection(@Nonnull Entity target, Vec3 direction) {
      target.setDeltaMovement(direction.normalize().scale(target.getDeltaMovement().length()));
      target.hurtMarked = true;
      target.hasImpulse = true;
   }

   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 2452267;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack);
      CapturingProperty.EntityData<?> entityData = this.getData(stack);
      return (Component)(!entityData.equals(this.getDefaultData())
         ? Component.translatable("property.detail", new Object[]{name, entityData.name}).withColor(this.getColor(stack))
         : name);
   }

   public CapturingProperty.EntityData<?> getDataFromEntity(final Entity entity) {
      return new CapturingProperty.EntityData(entity.getType(), new CompoundTag() {
         {
            entity.save(this);
         }
      }, entity.getName());
   }

   @Nullable
   public <T extends Entity> T getEntityFromData(CapturingProperty.EntityData<T> data, Level level) {
      if (data.entityType != null) {
         T entity = (T)data.entityType.create(level);
         if (entity != null) {
            entity.load(data.data);
            return entity;
         }
      }

      return null;
   }

   public CapturingProperty.EntityData<?> readData(CompoundTag tag) {
      if (tag.contains("entity_id", 8)) {
         Optional<Reference<EntityType<?>>> entityType = CommonUtils.registryAccessStatic()
            .lookupOrThrow(Registries.ENTITY_TYPE)
            .get(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(tag.getString("entity_id"))));
         if (entityType.isPresent()) {
            return new CapturingProperty.EntityData(
               (EntityType)entityType.get().value(),
               tag.getCompound("data"),
               tag.contains("name", 8) ? Serializer.fromJson(tag.getString("name"), CommonUtils.registryAccessStatic()) : Component.empty()
            );
         }
      }

      return this.getDefaultData();
   }

   public CompoundTag writeData(final CapturingProperty.EntityData<?> data) {
      return new CompoundTag() {
         {
            if (data.entityType != null) {
               this.putString("entity_id", BuiltInRegistries.ENTITY_TYPE.getKey(data.entityType).toString());
               if (!data.data.isEmpty()) {
                  this.put("data", data.data);
               }

               this.putString("name", Serializer.toJson(data.name, CommonUtils.registryAccessStatic()));
            }
         }
      };
   }

   public CapturingProperty.EntityData<?> getDefaultData() {
      return DEFAULT;
   }

   public record EntityData<T extends Entity>(@Nullable EntityType<T> entityType, CompoundTag data, Component name) {
   }
}
