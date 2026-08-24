package io.github.razordevs.deep_aether.event;

import com.aetherteam.aether.entity.AetherBossMob;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.monster.AechorPlant;
import com.aetherteam.aether.event.BossFightEvent.AddPlayer;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import io.github.razordevs.deep_aether.DeepAether;
import io.github.razordevs.deep_aether.DeepAetherConfig;
import io.github.razordevs.deep_aether.advancement.DAAdvancementTriggers;
import io.github.razordevs.deep_aether.datagen.tags.DATags;
import io.github.razordevs.deep_aether.init.DAItems;
import io.github.razordevs.deep_aether.init.DAMobEffects;
import io.github.razordevs.deep_aether.item.gear.DAEquipmentUtil;
import io.github.razordevs.deep_aether.item.gear.other.FloatyScarfItem;
import io.github.razordevs.deep_aether.item.gear.skyjade.SkyjadeWeapon;
import io.github.razordevs.deep_aether.networking.attachment.DAAttachments;
import io.github.razordevs.deep_aether.networking.attachment.DAPlayerAttachment;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;

@EventBusSubscriber(
   modid = "deep_aether"
)
public class DAGeneralEvents {
   public static final HashMap<EntityType<?>, Item> FLAWLESS_BOSS_DROP = new HashMap<>();
   private static int i = 0;

   @SubscribeEvent
   public static void onEntityJoin(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof Player player) {
         ((DAPlayerAttachment)player.getData(DAAttachments.PLAYER)).onJoinLevel(player);
      }
   }

   @SubscribeEvent
   public static void playerLoggedOutEvent(PlayerLoggedOutEvent event) {
      Player player = event.getEntity();
      SlotEntryReference reference = DAEquipmentUtil.getFloatyScarf(player);
      if (reference != null) {
         FloatyScarfItem.discardGentleWind(reference.stack(), player.level());
      }
   }

   @SubscribeEvent
   public static void onDungeonPlayerAdded(AddPlayer event) {
      event.getPlayer().setData((AttachmentType)DAAttachments.PLAYER_BOSS_FIGHT.get(), false);
   }

   @SubscribeEvent
   public static void onLivingEntityDeath(LivingDeathEvent event) {
      LivingEntity entity = event.getEntity();
      if (entity.getType() == AetherEntityTypes.SLIDER.get() && DeepAether.IsHalloweenContentEnabled()) {
         entity.spawnAtLocation(new ItemStack((ItemLike)DAItems.SPOOKY_RING.get(), 1));
      }

      if (entity instanceof AetherBossMob<?> bossMob) {
         Level level = entity.level();
         if (!event.getEntity().isAlive() && !level.isClientSide() && bossMob.getDungeon() != null) {
            List<UUID> uuids = bossMob.getDungeon().dungeonPlayers();
            List<Player> players = new ArrayList<>();

            for (UUID uuid : uuids) {
               Player player = level.getPlayerByUUID(uuid);
               if (player != null) {
                  if ((Boolean)player.getData(DAAttachments.PLAYER_BOSS_FIGHT)) {
                     return;
                  }

                  players.add(player);
               }
            }

            for (Player player : players) {
               ((KilledTrigger)DAAdvancementTriggers.FLAWLESS_TRIGGER.get()).trigger((ServerPlayer)player, entity, event.getSource());
            }

            if (FLAWLESS_BOSS_DROP.get(entity.getType()) != null) {
               ItemStack stack = new ItemStack((ItemLike)FLAWLESS_BOSS_DROP.get(entity.getType()));
               ItemEntity itementity = new ItemEntity(level, entity.getX(), entity.getY() + 0.0, entity.getZ(), stack);
               itementity.setDefaultPickUpDelay();
               level.addFreshEntity(itementity);
            }
         }
      }
   }

   @SubscribeEvent
   public static void applyValkyrieValorRes(Pre event) {
      if (event.getSource().getEntity() instanceof LivingEntity undead
         && event.getEntity().hasEffect(DAMobEffects.VALKYRIE_VALOR)
         && !event.getSource().is(DamageTypeTags.BYPASSES_RESISTANCE)
         && undead.getType().is(EntityTypeTags.UNDEAD)) {
         int j = 10;
         float f = event.getNewDamage() * j;
         float f1 = event.getNewDamage();
         event.setNewDamage(Math.max(f / 25.0F, 0.0F));
         float f2 = f1 - event.getNewDamage();
         if (f2 > 0.0F && f2 < 3.4028235E37F) {
            if (event.getEntity() instanceof ServerPlayer player) {
               player.awardStat(Stats.CUSTOM.get(Stats.DAMAGE_RESISTED), Math.round(f2 * 10.0F));
            } else if (event.getSource().getEntity() instanceof ServerPlayer player) {
               player.awardStat(Stats.CUSTOM.get(Stats.DAMAGE_DEALT_RESISTED), Math.round(f2 * 10.0F));
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
      if (event.getEntity() instanceof Player player) {
         DAPlayerAttachment attachment = (DAPlayerAttachment)player.getData(DAAttachments.PLAYER);
         Optional<SlotEntryReference> stack = EquipmentUtil.findFirstAccessory(player, (Item)DAItems.WIND_SHIELD.get());
         if (stack.isPresent()
            && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)
            && attachment.getWindShieldCooldown() <= 0
            && DAEquipmentUtil.hasWindShield(player)) {
            ((DAPlayerAttachment)player.getData(DAAttachments.PLAYER)).setSynched(player.getId(), Direction.CLIENT, "setWindShieldCooldown", 1200);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.level().isClientSide()) {
               stack.get().stack().hurtAndBreak(1, (ServerLevel)player.level(), player, item -> {});
               player.invulnerableTime = 20;
            }

            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onLivingEntityHurt(Pre event) {
      if (event.getEntity() instanceof ServerPlayer player && !event.getEntity().isDamageSourceBlocked(event.getSource())) {
         player.setData(DAAttachments.PLAYER_BOSS_FIGHT, true);
      }

      if (event.getSource().getDirectEntity() != null
         && event.getSource().getDirectEntity() instanceof LivingEntity target
         && DAEquipmentUtil.hasFullStormforgedSet(event.getEntity())) {
         target.knockback(0.5, event.getEntity().getX() - target.getX(), event.getEntity().getZ() - target.getZ());
         if (target instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
         }
      }
   }

   @SubscribeEvent
   public static void onEquip(LivingEquipmentChangeEvent event) {
      if ((Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get() && event.getEntity() instanceof Player player) {
         DAPlayerAttachment attachment = (DAPlayerAttachment)player.getData(DAAttachments.PLAYER);
         boolean skyjade = DAEquipmentUtil.hasFullSkyjadeSet(player);
         boolean enabled = attachment.isSkyjadeAbilityActivated();
         attachment.setSynched(player.getId(), Direction.CLIENT, "hasSkyjadeSet", skyjade);
         DAEquipmentUtil.updateSkyjadeBehavior(player, skyjade && enabled);
      }
   }

   @SubscribeEvent
   public static void livingVisibilityModification(LivingVisibilityEvent event) {
      if ((Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get() && event.getLookingEntity() instanceof LivingEntity living) {
         boolean enabled = true;
         if (living instanceof Player player) {
            enabled = ((DAPlayerAttachment)player.getData(DAAttachments.PLAYER)).isSkyjadeAbilityActivated();
         }

         if (enabled && DAEquipmentUtil.hasFullSkyjadeSet(living)) {
            event.modifyVisibility(event.getVisibilityModifier() * 0.5);
         }
      }
   }

   @SubscribeEvent
   public static void onModifyAttributes(ItemAttributeModifierEvent event) {
      ItemAttributeModifiers modifiers = event.getDefaultModifiers();
      ItemStack itemStack = event.getItemStack();
      if (itemStack.getItem() instanceof SkyjadeWeapon zaniteWeapon) {
         Entry attributeEntry = zaniteWeapon.increaseDamage(modifiers, itemStack);
         event.replaceModifier(attributeEntry.attribute(), attributeEntry.modifier(), attributeEntry.slot());
      }
   }

   @SubscribeEvent
   public static void mobInteract(EntityInteractSpecific event) {
      if (event.getTarget().getType() == AetherEntityTypes.AECHOR_PLANT.get()) {
         AechorPlant aechorPlant = (AechorPlant)event.getTarget();
         Player player = event.getEntity();
         ItemStack itemStack = player.getItemInHand(event.getHand());
         if (itemStack.is(Items.BUCKET) && aechorPlant.getPoisonRemaining() > 0) {
            aechorPlant.setPoisonRemaining(aechorPlant.getPoisonRemaining() - 1);
            ItemStack itemStack1 = ItemUtils.createFilledResult(itemStack, player, ((Item)DAItems.PLACEABLE_POISON_BUCKET.get()).getDefaultInstance());
            player.setItemInHand(event.getHand(), itemStack1);
            player.swing(event.getHand());
            event.setCancellationResult(InteractionResult.SUCCESS);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerUpdate(Post event) {
      if (event.getEntity().hasData(DAAttachments.PLAYER)) {
         ((DAPlayerAttachment)event.getEntity().getData(DAAttachments.PLAYER)).onUpdate((Player)event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      ((DAPlayerAttachment)event.getEntity().getData(DAAttachments.PLAYER)).onLogin(event.getEntity());
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onTooltipAdd(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      TooltipFlag flag = event.getFlags();
      List<Component> itemTooltips = event.getToolTip();
      if (flag.isCreative()) {
         int position = itemTooltips.size();
         Component itemName = stack.getItem().getName(stack);

         for (int i = 0; i < position; i++) {
            Component component = itemTooltips.get(i);
            if (component.getString().equals(itemName.getString())) {
               position = i + 1;
               break;
            }
         }

         if (stack.is(DATags.Items.BRASS_DUNGEON_LOOT)) {
            itemTooltips.add(position, DAItems.BRASS_DUNGEON_TOOLTIP);
         }
      }

      if (stack.is(DATags.Items.FLAWLESS_ITEMS)) {
         flawlessComponent(itemTooltips, DAGeneralEvents.i);
         DAGeneralEvents.i = DAGeneralEvents.i < 80 ? DAGeneralEvents.i + 1 : 0;
      }
   }

   private static void flawlessComponent(List<Component> tagTooltips, int i) {
      if (i > 70) {
         printComponent(tagTooltips, 1);
      } else if (i > 60) {
         printComponent(tagTooltips, 2);
      } else if (i > 50) {
         printComponent(tagTooltips, 3);
      } else if (i > 40) {
         printComponent(tagTooltips, 4);
      } else if (i > 30) {
         printComponent(tagTooltips, 5);
      } else if (i > 20) {
         printComponent(tagTooltips, 6);
      } else if (i > 10) {
         printComponent(tagTooltips, 7);
      } else {
         printComponent(tagTooltips, 8);
      }
   }

   private static void printComponent(List<Component> tagTooltips, int i) {
      tagTooltips.add(1, Component.translatable("gui.deep_aether.flawless_tier_" + i));
   }
}
