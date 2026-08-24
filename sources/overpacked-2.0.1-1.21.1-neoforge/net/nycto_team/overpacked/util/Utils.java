package net.nycto_team.overpacked.util;

import java.util.List;
import java.util.function.Predicate;
import net.bobophones.bobolib.util.BU;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.fml.ModList;
import net.nycto_team.overpacked.entity.GiantBackpack;
import net.nycto_team.overpacked.item.GiantBackpackItem;
import net.nycto_team.overpacked.registry.ModEntities;
import net.nycto_team.overpacked.registry.ModTags;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class Utils {
   private static final double to_rad = 0.017453292519943295;
   public static final List<String> beds = List.of(
      "",
      "minecraft:white_bed",
      "minecraft:orange_bed",
      "minecraft:magenta_bed",
      "minecraft:light_blue_bed",
      "minecraft:yellow_bed",
      "minecraft:lime_bed",
      "minecraft:pink_bed",
      "minecraft:gray_bed",
      "minecraft:light_gray_bed",
      "minecraft:cyan_bed",
      "minecraft:purple_bed",
      "minecraft:blue_bed",
      "minecraft:brown_bed",
      "minecraft:green_bed",
      "minecraft:red_bed",
      "minecraft:black_bed",
      "dye_depot:maroon_bed",
      "dye_depot:rose_bed",
      "dye_depot:coral_bed",
      "dye_depot:indigo_bed",
      "dye_depot:navy_bed",
      "dye_depot:slate_bed",
      "dye_depot:olive_bed",
      "dye_depot:amber_bed",
      "dye_depot:beige_bed",
      "dye_depot:teal_bed",
      "dye_depot:mint_bed",
      "dye_depot:aqua_bed",
      "dye_depot:verdant_bed",
      "dye_depot:forest_bed",
      "dye_depot:ginger_bed",
      "dye_depot:tan_bed"
   );
   public static final List<String> comfort_sleeping_bags = List.of(
      "",
      "comforts:sleeping_bag_white",
      "comforts:sleeping_bag_orange",
      "comforts:sleeping_bag_magenta",
      "comforts:sleeping_bag_light_blue",
      "comforts:sleeping_bag_yellow",
      "comforts:sleeping_bag_lime",
      "comforts:sleeping_bag_pink",
      "comforts:sleeping_bag_gray",
      "comforts:sleeping_bag_light_gray",
      "comforts:sleeping_bag_cyan",
      "comforts:sleeping_bag_purple",
      "comforts:sleeping_bag_blue",
      "comforts:sleeping_bag_brown",
      "comforts:sleeping_bag_green",
      "comforts:sleeping_bag_red",
      "comforts:sleeping_bag_black"
   );
   public static final List<String> upgrade_aquatic_sleeping_bags = List.of(
      "upgrade_aquatic:bedroll",
      "upgrade_aquatic:white_bedroll",
      "upgrade_aquatic:orange_bedroll",
      "upgrade_aquatic:magenta_bedroll",
      "upgrade_aquatic:light_blue_bedroll",
      "upgrade_aquatic:yellow_bedroll",
      "upgrade_aquatic:lime_bedroll",
      "upgrade_aquatic:pink_bedroll",
      "upgrade_aquatic:gray_bedroll",
      "upgrade_aquatic:light_gray_bedroll",
      "upgrade_aquatic:cyan_bedroll",
      "upgrade_aquatic:purple_bedroll",
      "upgrade_aquatic:blue_bedroll",
      "upgrade_aquatic:brown_bedroll",
      "upgrade_aquatic:green_bedroll",
      "upgrade_aquatic:red_bedroll",
      "upgrade_aquatic:black_bedroll"
   );
   private static final Predicate<Entity> place_predicate = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
   public static final boolean comforts = ModList.get().isLoaded("comforts");
   public static final boolean upgrade_aquatic = ModList.get().isLoaded("upgrade_aquatic");
   public static final boolean dye_depot = ModList.get().isLoaded("dye_depot");

   public static float rad(float value) {
      return value * 0.017453292F;
   }

   public static boolean is_backpack_equipped(LivingEntity entity) {
      return CuriosApi.getCuriosHelper().findEquippedCurio(item -> item.is(ModTags.Items.giant_backpacks), entity).isPresent();
   }

   public static ResourceLocation item_id(Item value) {
      return BuiltInRegistries.ITEM.getKey(value);
   }

   public static int get_sleeping_bag_color(ItemStack stack) {
      for (String id : beds) {
         if (item_id(stack.getItem()).toString().equals(id)) {
            return beds.indexOf(id);
         }
      }

      if (comforts) {
         for (String idx : comfort_sleeping_bags) {
            if (item_id(stack.getItem()).toString().equals(idx)) {
               return comfort_sleeping_bags.indexOf(idx);
            }
         }
      }

      if (upgrade_aquatic) {
         for (String idxx : upgrade_aquatic_sleeping_bags) {
            if (item_id(stack.getItem()).toString().equals(idxx)) {
               return upgrade_aquatic_sleeping_bags.indexOf(idxx);
            }
         }
      }

      return -1;
   }

   public static InteractionResultHolder<ItemStack> PlaceBackpack(Level level, ItemStack stack, Player player, boolean curios) {
      HitResult hit = Item.getPlayerPOVHitResult(level, player, Fluid.ANY);
      if (hit.getType() != Type.MISS) {
         Vec3 look = player.getViewVector(1.0F);
         List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(look.scale(5.0)).inflate(1.0), place_predicate);
         if (!list.isEmpty()) {
            for (Entity entity : list) {
               if (entity.getBoundingBox().inflate(entity.getPickRadius()).contains(player.getEyePosition())) {
                  return InteractionResultHolder.pass(stack);
               }
            }
         }

         if (hit.getType() == Type.BLOCK) {
            Vec3 pos = hit.getLocation();
            Entity entityx = ModEntities.giant_backpack.get().create(level);
            entityx.moveTo(pos);
            entityx.setYRot(player.getYRot() + 180.0F);
            if (!level.noCollision(entityx, entityx.getBoundingBox())) {
               return InteractionResultHolder.fail(stack);
            }

            if (entityx instanceof GiantBackpack backpack) {
               backpack.SetColor(((GiantBackpackItem)stack.getItem()).color);
               CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
               if (data != null) {
                  CompoundTag tag = data.copyTag();
                  backpack.Load(tag);
                  if (stack.has(DataComponents.CUSTOM_NAME)) {
                     backpack.SetName(((Component)stack.get(DataComponents.CUSTOM_NAME)).getString());
                  }
               }
            }

            if (!level.isClientSide()) {
               level.addFreshEntity(entityx);
               level.gameEvent(player, GameEvent.ENTITY_PLACE, hit.getLocation());
               if (curios) {
                  stack.shrink(1);
                  player.swing(InteractionHand.MAIN_HAND, true);
                  player.level()
                     .playSound(
                        null,
                        player.position().x,
                        player.position().y,
                        player.position().z,
                        (SoundEvent)SoundEvents.ARMOR_EQUIP_LEATHER.value(),
                        SoundSource.PLAYERS
                     );
               } else {
                  BU.ShrinkCreative(player, stack);
               }
            }

            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
         }
      }

      return InteractionResultHolder.pass(stack);
   }

   public static ItemStack get_curio_backpack(LivingEntity entity) {
      if (entity != null) {
         ICurioStacksHandler handler = (ICurioStacksHandler)((ICuriosItemHandler)CuriosApi.getCuriosHelper().getCuriosHandler(entity).orElse(null))
            .getStacksHandler("back")
            .get();

         for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStacks().getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof GiantBackpackItem) {
               return stack;
            }
         }
      }

      return ItemStack.EMPTY;
   }
}
