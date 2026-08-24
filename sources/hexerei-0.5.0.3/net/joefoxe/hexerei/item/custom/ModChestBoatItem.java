package net.joefoxe.hexerei.item.custom;

import java.util.List;
import java.util.function.Predicate;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModChestBoatEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class ModChestBoatItem extends Item {
   private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
   private final ModChestBoatEntity.Type type;
   private final boolean hasChest;

   public ModChestBoatItem(boolean pHasChest, ModChestBoatEntity.Type pType, Properties pProperties) {
      super(pProperties);
      this.hasChest = pHasChest;
      this.type = pType;
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      HitResult hitresult = getPlayerPOVHitResult(pLevel, pPlayer, Fluid.ANY);
      if (hitresult.getType() == Type.MISS) {
         return InteractionResultHolder.pass(itemstack);
      } else {
         Vec3 vec3 = pPlayer.getViewVector(1.0F);
         double d0 = 5.0;
         List<Entity> list = pLevel.getEntities(pPlayer, pPlayer.getBoundingBox().expandTowards(vec3.scale(5.0)).inflate(1.0), ENTITY_PREDICATE);
         if (!list.isEmpty()) {
            Vec3 vec31 = pPlayer.getEyePosition();

            for (Entity entity : list) {
               AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
               if (aabb.contains(vec31)) {
                  return InteractionResultHolder.pass(itemstack);
               }
            }
         }

         if (hitresult.getType() == Type.BLOCK) {
            ModChestBoatEntity boat = this.getBoat(pLevel, hitresult);
            boat.setType(this.type);
            boat.setYRot(pPlayer.getYRot());
            if (!pLevel.noCollision(boat, boat.getBoundingBox())) {
               return InteractionResultHolder.fail(itemstack);
            } else {
               if (!pLevel.isClientSide) {
                  pLevel.addFreshEntity(boat);
                  pLevel.gameEvent(pPlayer, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                  if (!pPlayer.getAbilities().instabuild) {
                     itemstack.shrink(1);
                  }
               }

               pPlayer.awardStat(Stats.ITEM_USED.get(this));
               return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
            }
         } else {
            return InteractionResultHolder.pass(itemstack);
         }
      }
   }

   private ModChestBoatEntity getBoat(Level p_220017_, HitResult p_220018_) {
      ModChestBoatEntity boat = new ModChestBoatEntity((EntityType<ModChestBoatEntity>)ModEntityTypes.HEXEREI_CHEST_BOAT.get(), p_220017_);
      boat.setPos(p_220018_.getLocation().x, p_220018_.getLocation().y, p_220018_.getLocation().z);
      return boat;
   }
}
