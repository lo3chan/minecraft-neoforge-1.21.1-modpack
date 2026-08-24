package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityStraddleboard;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class ItemStraddleboard extends Item {
   private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

   public ItemStraddleboard(Properties properties) {
      super(properties);
   }

   public int getColor(ItemStack p_200886_1_) {
      return DyedItemColor.getOrDefault(p_200886_1_, 11387863);
   }

   public int getEnchantmentValue() {
      return 1;
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, Fluid.ANY);
      if (raytraceresult.getType() == Type.MISS) {
         return AMCompat.pass(itemstack);
      } else {
         Vec3 vector3d = playerIn.getViewVector(1.0F);
         double d0 = 5.0;
         List<Entity> list = worldIn.getEntities(playerIn, playerIn.getBoundingBox().expandTowards(vector3d.scale(5.0)).inflate(1.0), ENTITY_PREDICATE);
         if (!list.isEmpty()) {
            Vec3 vector3d1 = playerIn.getEyePosition(1.0F);

            for (Entity entity : list) {
               AABB axisalignedbb = entity.getBoundingBox().inflate(entity.getPickRadius());
               if (axisalignedbb.contains(vector3d1)) {
                  return AMCompat.pass(itemstack);
               }
            }
         }

         if (raytraceresult.getType() == Type.BLOCK) {
            EntityStraddleboard boatentity = new EntityStraddleboard(
               worldIn, raytraceresult.getLocation().x, raytraceresult.getLocation().y, raytraceresult.getLocation().z
            );
            boatentity.setDefaultColor(!AMCompat.hasCustomColor(itemstack));
            boatentity.setItemStack(itemstack.copy());
            boatentity.setColor(this.getColor(itemstack));
            boatentity.setYRot(playerIn.getYRot());
            if (!worldIn.noCollision(boatentity, boatentity.getBoundingBox().inflate(-0.1))) {
               return AMCompat.fail(itemstack);
            } else {
               if (!worldIn.isClientSide()) {
                  worldIn.addFreshEntity(boatentity);
                  if (!playerIn.getAbilities().instabuild) {
                     itemstack.shrink(1);
                  }
               }

               playerIn.awardStat(Stats.ITEM_USED.get(this));
               return AMCompat.sidedSuccess(itemstack, worldIn.isClientSide());
            }
         } else {
            return AMCompat.pass(itemstack);
         }
      }
   }
}
