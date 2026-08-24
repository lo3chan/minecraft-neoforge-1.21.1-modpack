package net.astralya.hexalia.item.custom;

import java.util.List;
import java.util.function.Predicate;
import net.astralya.hexalia.entity.boat.ModBoatEntity;
import net.astralya.hexalia.entity.boat.ModChestBoatEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
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

public class ModBoatItem extends Item {
   private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
   private final boolean hasChest;
   private final ModBoatEntity.Type type;

   public ModBoatItem(boolean hasChest, ModBoatEntity.Type type, Properties properties) {
      super(properties.stacksTo(1));
      this.hasChest = hasChest;
      this.type = type;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
      ItemStack itemStack = player.getItemInHand(usedHand);
      HitResult hitResult = getPlayerPOVHitResult(level, player, Fluid.ANY);
      if (hitResult.getType() == Type.MISS) {
         return InteractionResultHolder.pass(itemStack);
      } else {
         Vec3 viewVector = player.getViewVector(1.0F);
         List<Entity> entities = level.getEntities(player, player.getBoundingBox().expandTowards(viewVector.scale(5.0)).inflate(1.0), ENTITY_PREDICATE);
         if (!entities.isEmpty()) {
            Vec3 eyePosition = player.getEyePosition();

            for (Entity entity : entities) {
               AABB bounds = entity.getBoundingBox().inflate(entity.getPickRadius());
               if (bounds.contains(eyePosition)) {
                  return InteractionResultHolder.pass(itemStack);
               }
            }
         }

         if (hitResult.getType() != Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
         } else {
            Boat boat = this.getBoat(level, hitResult);
            if (boat instanceof ModChestBoatEntity chestBoat) {
               chestBoat.setVariant(this.type);
            } else if (boat instanceof ModBoatEntity modBoat) {
               modBoat.setVariant(this.type);
            }

            boat.setYRot(player.getYRot());
            if (!level.noCollision(boat, boat.getBoundingBox())) {
               return InteractionResultHolder.fail(itemStack);
            } else {
               if (!level.isClientSide) {
                  level.addFreshEntity(boat);
                  level.gameEvent(player, GameEvent.ENTITY_PLACE, hitResult.getLocation());
                  if (!player.getAbilities().instabuild) {
                     itemStack.shrink(1);
                  }
               }

               player.awardStat(Stats.ITEM_USED.get(this));
               return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
            }
         }
      }
   }

   private Boat getBoat(Level level, HitResult hitResult) {
      Vec3 location = hitResult.getLocation();
      return (Boat)(this.hasChest
         ? new ModChestBoatEntity(level, location.x, location.y, location.z)
         : new ModBoatEntity(level, location.x, location.y, location.z));
   }
}
