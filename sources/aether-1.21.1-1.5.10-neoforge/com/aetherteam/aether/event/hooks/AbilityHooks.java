package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.attachment.LightningTrackerAttachment;
import com.aetherteam.aether.attachment.PhoenixArrowAttachment;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.entity.projectile.PoisonNeedle;
import com.aetherteam.aether.entity.projectile.dart.EnchantedDart;
import com.aetherteam.aether.entity.projectile.dart.GoldenDart;
import com.aetherteam.aether.entity.projectile.dart.PoisonDart;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.accessories.abilities.ZaniteAccessory;
import com.aetherteam.aether.item.tools.abilities.HolystoneTool;
import com.aetherteam.aether.item.tools.abilities.ZaniteTool;
import com.aetherteam.aether.loot.AetherLoot;
import com.aetherteam.aether.loot.AetherLootContexts;
import com.aetherteam.aether.network.packet.clientbound.ToolDebuffPacket;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.AtomicDouble;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.network.PacketDistributor;

public class AbilityHooks {
   public static class AccessoryHooks {
      public static void damageGloves(Player player) {
         SlotEntryReference slotResult = EquipmentUtil.getGloves(player);
         if (slotResult != null && player.level() instanceof ServerLevel serverLevel) {
            slotResult.stack().hurtAndBreak(1, serverLevel, player, item -> AccessoriesAPI.breakStack(slotResult.reference()));
         }
      }

      public static void damageZaniteRing(LivingEntity entity, LevelAccessor level, BlockState state, BlockPos pos) {
         for (SlotEntryReference slotResult : EquipmentUtil.getZaniteRings(entity)) {
            if (slotResult != null
               && state.getDestroySpeed(level, pos) > 0.0F
               && entity.getRandom().nextInt(6) == 0
               && entity.level() instanceof ServerLevel serverLevel) {
               slotResult.stack().hurtAndBreak(1, serverLevel, entity, item -> AccessoriesAPI.breakStack(slotResult.reference()));
            }
         }
      }

      public static void damageZanitePendant(LivingEntity entity, LevelAccessor level, BlockState state, BlockPos pos) {
         SlotEntryReference slotResult = EquipmentUtil.getZanitePendant(entity);
         if (slotResult != null
            && state.getDestroySpeed(level, pos) > 0.0F
            && entity.getRandom().nextInt(6) == 0
            && entity.level() instanceof ServerLevel serverLevel) {
            slotResult.stack().hurtAndBreak(1, serverLevel, entity, item -> AccessoriesAPI.breakStack(slotResult.reference()));
         }
      }

      public static float handleZaniteRingAbility(LivingEntity entity, float speed) {
         float newSpeed = speed;

         for (SlotEntryReference slotResult : EquipmentUtil.getZaniteRings(entity)) {
            if (slotResult != null) {
               newSpeed = ZaniteAccessory.handleMiningSpeed(newSpeed, slotResult.stack());
            }
         }

         return newSpeed;
      }

      public static float handleZanitePendantAbility(LivingEntity entity, float speed) {
         SlotEntryReference slotResult = EquipmentUtil.getZanitePendant(entity);
         if (slotResult != null) {
            speed = ZaniteAccessory.handleMiningSpeed(speed, slotResult.stack());
         }

         return speed;
      }

      public static boolean preventTargeting(LivingEntity target, @Nullable Entity lookingEntity) {
         if (target instanceof Player player) {
            AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
            return lookingEntity != null
               && !lookingEntity.getType().is(AetherTags.Entities.IGNORE_INVISIBILITY)
               && data.isWearingInvisibilityCloak()
               && data.isInvisibilityEnabled()
               && !data.attackedWithInvisibility();
         } else {
            return lookingEntity != null && !lookingEntity.getType().is(AetherTags.Entities.IGNORE_INVISIBILITY) && EquipmentUtil.hasInvisibilityCloak(target);
         }
      }

      public static boolean recentlyAttackedWithInvisibility(LivingEntity target, Entity lookingEntity) {
         if (!(target instanceof Player player)) {
            return false;
         } else {
            AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
            return !lookingEntity.getType().is(AetherTags.Entities.IGNORE_INVISIBILITY)
               && data.isWearingInvisibilityCloak()
               && data.isInvisibilityEnabled()
               && data.attackedWithInvisibility();
         }
      }

      public static void setAttack(DamageSource source) {
         if (source.getEntity() instanceof Player player) {
            ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).setAttackedWithInvisibility(true);
         }
      }

      public static boolean preventMagmaDamage(LivingEntity entity, DamageSource source) {
         return source == entity.level().damageSources().hotFloor() && EquipmentUtil.hasFreezingAccessory(entity);
      }
   }

   public static class ArmorHooks {
      public static boolean fallCancellation(LivingEntity entity) {
         return EquipmentUtil.hasSentryBoots(entity) || EquipmentUtil.hasFullGravititeSet(entity) || EquipmentUtil.hasFullValkyrieSet(entity);
      }
   }

   public static class ToolHooks {
      public static final Map<Block, Block> STRIPPABLES = new Builder()
         .put((Block)AetherBlocks.SKYROOT_LOG.get(), (Block)AetherBlocks.STRIPPED_SKYROOT_LOG.get())
         .put((Block)AetherBlocks.GOLDEN_OAK_LOG.get(), (Block)AetherBlocks.STRIPPED_SKYROOT_LOG.get())
         .put((Block)AetherBlocks.SKYROOT_WOOD.get(), (Block)AetherBlocks.STRIPPED_SKYROOT_WOOD.get())
         .put((Block)AetherBlocks.GOLDEN_OAK_WOOD.get(), (Block)AetherBlocks.STRIPPED_SKYROOT_WOOD.get())
         .build();
      public static final Map<Block, Block> FLATTENABLES = new Builder()
         .put((Block)AetherBlocks.AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_DIRT_PATH.get())
         .put((Block)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_DIRT_PATH.get())
         .put((Block)AetherBlocks.AETHER_DIRT.get(), (Block)AetherBlocks.AETHER_DIRT_PATH.get())
         .build();
      public static final Map<Block, Block> TILLABLES = new Builder()
         .put((Block)AetherBlocks.AETHER_DIRT.get(), (Block)AetherBlocks.AETHER_FARMLAND.get())
         .put((Block)AetherBlocks.AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_FARMLAND.get())
         .put((Block)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_FARMLAND.get())
         .put((Block)AetherBlocks.AETHER_DIRT_PATH.get(), (Block)AetherBlocks.AETHER_FARMLAND.get())
         .build();
      public static boolean debuffTools;

      public static BlockState setupItemAbilities(LevelAccessor accessor, BlockPos pos, BlockState old, ItemAbility action) {
         Block oldBlock = old.getBlock();
         if (action == ItemAbilities.AXE_STRIP) {
            if (STRIPPABLES.containsKey(oldBlock)) {
               return STRIPPABLES.get(oldBlock).withPropertiesOf(old);
            }
         } else if (action == ItemAbilities.SHOVEL_FLATTEN) {
            if (FLATTENABLES.containsKey(oldBlock)) {
               return FLATTENABLES.get(oldBlock).withPropertiesOf(old);
            }
         } else if (action == ItemAbilities.HOE_TILL && accessor.getBlockState(pos.above()).isAir() && TILLABLES.containsKey(oldBlock)) {
            return TILLABLES.get(oldBlock).withPropertiesOf(old);
         }

         return old;
      }

      public static void handleHolystoneToolAbility(Player player, Level level, BlockPos pos, ItemStack stack, BlockState blockState) {
         if (stack.getItem() instanceof HolystoneTool holystoneTool) {
            holystoneTool.dropAmbrosium(player, level, pos, stack, blockState);
         }
      }

      public static float handleZaniteToolAbility(ItemStack stack, float speed) {
         return stack.getItem() instanceof ZaniteTool zaniteTool ? zaniteTool.increaseSpeed(stack, speed) : speed;
      }

      public static float reduceToolEffectiveness(Player player, BlockState state, ItemStack stack, float speed) {
         if (debuffTools
            && (state.getBlock().getDescriptionId().startsWith("block.aether.") || state.is(AetherTags.Blocks.TREATED_AS_AETHER_BLOCK))
            && !state.is(AetherTags.Blocks.TREATED_AS_VANILLA_BLOCK)
            && !stack.isEmpty()
            && stack.isCorrectToolForDrops(state)
            && !stack.getItem().getDescriptionId().startsWith("item.aether.")
            && !stack.is(AetherTags.Items.TREATED_AS_AETHER_ITEM)) {
            speed = (float)Math.max(Math.pow(speed, speed > 1.0 ? -0.5 : 1.5), 1.0);
         }

         return speed;
      }

      public static void setDebuffToolsState(ServerPlayer player) {
         if (debuffTools) {
            PacketDistributor.sendToPlayer(player, new ToolDebuffPacket(true), new CustomPacketPayload[0]);
         } else if ((Boolean)AetherConfig.SERVER.tools_debuff.get()) {
            debuffTools = true;
            PacketDistributor.sendToAllPlayers(new ToolDebuffPacket(true), new CustomPacketPayload[0]);
         }
      }

      public static void resetDebuffToolsState() {
         debuffTools = false;
      }

      public static void stripGoldenOak(LevelAccessor accessor, BlockState state, ItemStack stack, ItemAbility action, UseOnContext context) {
         if (action == ItemAbilities.AXE_STRIP
            && accessor instanceof Level level
            && state.is(AetherTags.Blocks.GOLDEN_OAK_LOGS)
            && stack.is(AetherTags.Items.GOLDEN_AMBER_HARVESTERS)
            && level.getServer() != null
            && level instanceof ServerLevel serverLevel) {
            Vec3 vector = context.getClickLocation();
            LootParams parameters = new net.minecraft.world.level.storage.loot.LootParams.Builder(serverLevel)
               .withParameter(LootContextParams.TOOL, stack)
               .create(AetherLootContexts.STRIPPING);
            LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(AetherLoot.STRIP_GOLDEN_OAK);

            for (ItemStack itemStack : lootTable.getRandomItems(parameters)) {
               ItemEntity itemEntity = new ItemEntity(level, vector.x(), vector.y(), vector.z(), itemStack);
               itemEntity.setDefaultPickUpDelay();
               level.addFreshEntity(itemEntity);
            }
         }
      }
   }

   public static class WeaponHooks {
      public static void stickDart(LivingEntity entity, DamageSource source) {
         if (entity instanceof Player player && !player.level().isClientSide()) {
            Entity sourceEntity = source.getDirectEntity();
            AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
            if (sourceEntity instanceof GoldenDart) {
               data.setSynched(player.getId(), Direction.CLIENT, "setGoldenDartCount", data.getGoldenDartCount() + 1);
            } else if (sourceEntity instanceof PoisonDart || sourceEntity instanceof PoisonNeedle) {
               data.setSynched(player.getId(), Direction.CLIENT, "setPoisonDartCount", data.getPoisonDartCount() + 1);
            } else if (sourceEntity instanceof EnchantedDart) {
               data.setSynched(player.getId(), Direction.CLIENT, "setEnchantedDartCount", data.getEnchantedDartCount() + 1);
            }
         }
      }

      public static void phoenixArrowHit(HitResult result, Projectile projectile) {
         if (result instanceof EntityHitResult entityHitResult && projectile instanceof AbstractArrow abstractArrow) {
            Entity impactedEntity = entityHitResult.getEntity();
            if (impactedEntity.getType() == EntityType.ENDERMAN) {
               return;
            }

            if (abstractArrow.hasData(AetherDataAttachments.PHOENIX_ARROW)) {
               PhoenixArrowAttachment data = (PhoenixArrowAttachment)abstractArrow.getData(AetherDataAttachments.PHOENIX_ARROW);
               if (data.isPhoenixArrow() && data.getFireTime() > 0) {
                  impactedEntity.igniteForSeconds(data.getFireTime());
               }
            }
         }
      }

      public static boolean lightningTracking(Entity entity, LightningBolt lightning) {
         if (entity instanceof LivingEntity livingEntity && lightning.hasData(AetherDataAttachments.LIGHTNING_TRACKER)) {
            LightningTrackerAttachment tracker = (LightningTrackerAttachment)lightning.getData(AetherDataAttachments.LIGHTNING_TRACKER);
            Entity owner = tracker.getOwner(lightning.level());
            if (owner != null) {
               return livingEntity == owner || livingEntity == owner.getVehicle() || owner.getPassengers().contains(livingEntity);
            }
         }

         return false;
      }

      public static float reduceWeaponEffectiveness(LivingEntity target, Entity source, float damage) {
         if ((Boolean)AetherConfig.SERVER.tools_debuff.get() && !target.level().isClientSide()) {
            double pow = Math.max(Math.pow(damage, damage > 1.0 ? 0.6 : 1.6), 1.0);
            if (source instanceof LivingEntity livingEntity) {
               ItemStack stack = livingEntity.getMainHandItem();
               if ((target.getType().getDescriptionId().startsWith("entity.aether") || target.getType().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY))
                  && !target.getType().is(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY)
                  && !stack.isEmpty()) {
                  AtomicDouble value = new AtomicDouble();
                  stack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
                     if (attribute.is(Attributes.ATTACK_DAMAGE)) {
                        value.set(value.get() + modifier.amount());
                     }
                  });
                  if (livingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null
                     && value.get() > livingEntity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE)
                     && !stack.getItem().getDescriptionId().startsWith("item.aether.")
                     && !stack.is(AetherTags.Items.TREATED_AS_AETHER_ITEM)) {
                     damage = (float)pow;
                  }
               }
            } else if (source instanceof Projectile
               && (target.getType().getDescriptionId().startsWith("entity.aether") || target.getType().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY))
               && !target.getType().is(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY)
               && !source.getType().getDescriptionId().startsWith("entity.aether")
               && !source.getType().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY)
               && !(
                  source instanceof AbstractArrow abstractArrow
                     && abstractArrow.hasData(AetherDataAttachments.PHOENIX_ARROW)
                     && ((PhoenixArrowAttachment)abstractArrow.getData(AetherDataAttachments.PHOENIX_ARROW)).isPhoenixArrow()
               )) {
               damage = (float)pow;
            }
         }

         return damage;
      }

      public static float reduceArmorEffectiveness(LivingEntity target, @Nullable Entity source, float damage) {
         if (source != null
            && (
               source.getType().getDescriptionId().startsWith("entity.aether")
                  || source.getType().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY) && !source.getType().is(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY)
            )) {
            for (ItemStack stack : target.getArmorSlots()) {
               if (stack.getItem() instanceof ArmorItem armorItem
                  && !stack.getItem().getDescriptionId().startsWith("item.aether.")
                  && !stack.is(AetherTags.Items.TREATED_AS_AETHER_ITEM)) {
                  AtomicDouble value = new AtomicDouble();
                  stack.forEachModifier(armorItem.getEquipmentSlot(), (attribute, modifier) -> {
                     if (attribute.is(Attributes.ARMOR)) {
                        value.set(value.get() + modifier.amount() / 15.0);
                     }
                  });
                  damage += (float)value.get();
               }
            }
         }

         return damage;
      }
   }
}
