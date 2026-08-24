package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.entity.TheMoonflowerEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlocks;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import net.neoforged.neoforge.items.IItemHandler;

public class BlacpetalblockUpdateTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if ((new Object() {
         public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
            if (world instanceof ILevelExtension _ext) {
               IItemHandler _itemHandler = (IItemHandler)_ext.getCapability(ItemHandler.BLOCK, pos, null);
               if (_itemHandler != null) {
                  return _itemHandler.getStackInSlot(slotid).copy();
               }
            }

            return ItemStack.EMPTY;
         }
      }).getItemStack(world, BlockPos.containing(x, y, z), 12).getItem() == Items.DIAMOND) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiterator instanceof Player) {
               if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
               }

               if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT, 320, 1));
               }
            }
         }
      }

      if ((new Object() {
         public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
            if (world instanceof ILevelExtension _ext) {
               IItemHandler _itemHandler = (IItemHandler)_ext.getCapability(ItemHandler.BLOCK, pos, null);
               if (_itemHandler != null) {
                  return _itemHandler.getStackInSlot(slotid).copy();
               }
            }

            return ItemStack.EMPTY;
         }
      }).getItemStack(world, BlockPos.containing(x, y, z), 12).getItem() == Items.ROTTEN_FLESH) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiteratorx.getType().is(EntityTypeTags.UNDEAD) && !(entityiteratorx instanceof TheMoonflowerEntity)) {
               if (entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
               }

               if (entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT, 320, 1));
               }
            }
         }
      }

      if ((new Object() {
         public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
            if (world instanceof ILevelExtension _ext) {
               IItemHandler _itemHandler = (IItemHandler)_ext.getCapability(ItemHandler.BLOCK, pos, null);
               if (_itemHandler != null) {
                  return _itemHandler.getStackInSlot(slotid).copy();
               }
            }

            return ItemStack.EMPTY;
         }
      }).getItemStack(world, BlockPos.containing(x, y, z), 12).getItem() == Items.BONE) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiteratorxx instanceof TamableAnimal _tamEnt && _tamEnt.isTame()) {
               if (entityiteratorxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
               }

               if (entityiteratorxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT, 320, 1));
               }
            }
         }
      }

      if ((new Object() {
         public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
            if (world instanceof ILevelExtension _ext) {
               IItemHandler _itemHandler = (IItemHandler)_ext.getCapability(ItemHandler.BLOCK, pos, null);
               if (_itemHandler != null) {
                  return _itemHandler.getStackInSlot(slotid).copy();
               }
            }

            return ItemStack.EMPTY;
         }
      }).getItemStack(world, BlockPos.containing(x, y, z), 12).getItem() == Items.APPLE) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiteratorxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiteratorxxx instanceof LivingEntity) {
               if (entityiteratorxxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
               }

               if (entityiteratorxxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT, 320, 1));
               }
            }
         }
      }

      if ((new Object() {
         public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
            if (world instanceof ILevelExtension _ext) {
               IItemHandler _itemHandler = (IItemHandler)_ext.getCapability(ItemHandler.BLOCK, pos, null);
               if (_itemHandler != null) {
                  return _itemHandler.getStackInSlot(slotid).copy();
               }
            }

            return ItemStack.EMPTY;
         }
      }).getItemStack(world, BlockPos.containing(x, y, z), 12).getItem() == Items.STRING) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiteratorxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiteratorxxxx instanceof Monster && !(entityiteratorxxxx instanceof TheMoonflowerEntity)) {
               if (entityiteratorxxxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
               }

               if (entityiteratorxxxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT, 320, 1));
               }
            }
         }
      }

      if ((new Object() {
         public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
            if (world instanceof ILevelExtension _ext) {
               IItemHandler _itemHandler = (IItemHandler)_ext.getCapability(ItemHandler.BLOCK, pos, null);
               if (_itemHandler != null) {
                  return _itemHandler.getStackInSlot(slotid).copy();
               }
            }

            return ItemStack.EMPTY;
         }
      }).getItemStack(world, BlockPos.containing(x, y, z), 12).getItem() == ((Block)UndeadRevamp2ModBlocks.ARAPHOLIA.get()).asItem()) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiteratorxxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiteratorxxxxx instanceof TheMoonflowerEntity) {
               if (entityiteratorxxxxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
               }

               if (entityiteratorxxxxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT, 320, 1));
               }
            }
         }
      }
   }
}
