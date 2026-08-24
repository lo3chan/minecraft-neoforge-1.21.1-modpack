package top.theillusivec4.curios.common.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityEvent.EntityConstructing;
import net.neoforged.neoforge.event.entity.living.EnderManAngerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StartTracking;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotAttribute;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioDropsEvent;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.ICuriosMenu;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.CuriosConfig;
import top.theillusivec4.curios.common.CuriosRegistry;
import top.theillusivec4.curios.common.data.CuriosEntityManager;
import top.theillusivec4.curios.common.data.CuriosSlotManager;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;
import top.theillusivec4.curios.common.network.server.SPacketSetIcons;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncCurios;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncData;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncModifiers;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncStack;

public class CuriosEventHandler {
   public static boolean dirtyTags = false;
   static Map<UUID, Pair<Long, Boolean>> enderManMaskCache = new HashMap<>();

   private static void handleDrops(
      String identifier,
      LivingEntity livingEntity,
      List<Tuple<Predicate<ItemStack>, ICurio.DropRule>> dropRules,
      NonNullList<Boolean> renders,
      IDynamicStackHandler stacks,
      boolean cosmetic,
      Collection<ItemEntity> drops,
      boolean keepInventory,
      LivingDropsEvent evt
   ) {
      for (int i = 0; i < stacks.getSlots(); i++) {
         ItemStack stack = stacks.getStackInSlot(i);
         SlotContext slotContext = new SlotContext(identifier, livingEntity, i, cosmetic, renders.size() > i && (Boolean)renders.get(i));
         if (!stack.isEmpty()) {
            ICurio.DropRule dropRuleOverride = null;

            for (Tuple<Predicate<ItemStack>, ICurio.DropRule> override : dropRules) {
               if (((Predicate)override.getA()).test(stack)) {
                  dropRuleOverride = (ICurio.DropRule)override.getB();
               }
            }

            ICurio.DropRule dropRule = dropRuleOverride != null
               ? dropRuleOverride
               : CuriosApi.getCurio(stack).map(curio -> curio.getDropRule(slotContext, evt.getSource(), evt.isRecentlyHit())).orElse(ICurio.DropRule.DEFAULT);
            if (dropRule == ICurio.DropRule.DEFAULT) {
               dropRule = CuriosApi.getSlot(identifier, livingEntity.level()).map(ISlotType::getDropRule).orElse(ICurio.DropRule.DEFAULT);
            }

            if ((dropRule != ICurio.DropRule.DEFAULT || !keepInventory) && dropRule != ICurio.DropRule.ALWAYS_KEEP) {
               if (!EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP) && dropRule != ICurio.DropRule.DESTROY) {
                  drops.add(getDroppedItem(stack, livingEntity));
               }

               stacks.setStackInSlot(i, ItemStack.EMPTY);
            }
         }
      }
   }

   private static ItemEntity getDroppedItem(ItemStack droppedItem, LivingEntity livingEntity) {
      double d0 = livingEntity.getY() - 0.30000001192092896 + livingEntity.getEyeHeight();
      ItemEntity entityitem = new ItemEntity(livingEntity.level(), livingEntity.getX(), d0, livingEntity.getZ(), droppedItem);
      entityitem.setPickUpDelay(40);
      float f = livingEntity.level().random.nextFloat() * 0.5F;
      float f1 = livingEntity.level().random.nextFloat() * 6.2831855F;
      entityitem.setDeltaMovement(-Mth.sin(f1) * f, 0.20000000298023224, Mth.cos(f1) * f);
      return entityitem;
   }

   private static boolean handleMending(Player player, IDynamicStackHandler stacks, PickupXp evt) {
      Holder<Enchantment> mendingHolder = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING);

      for (int i = 0; i < stacks.getSlots(); i++) {
         ItemStack stack = stacks.getStackInSlot(i);
         if (!stack.isEmpty() && stack.getEnchantmentLevel(mendingHolder) > 0 && stack.isDamaged()) {
            evt.setCanceled(true);
            ExperienceOrb orb = evt.getOrb();
            player.takeXpDelay = 2;
            player.take(orb, 1);
            int toRepair = Math.min(orb.value * 2, stack.getDamageValue());
            orb.value -= toRepair / 2;
            stack.setDamageValue(stack.getDamageValue() - toRepair);
            if (orb.value > 0) {
               player.giveExperiencePoints(orb.value);
            }

            orb.remove(RemovalReason.KILLED);
            return true;
         }
      }

      return false;
   }

   @SubscribeEvent
   public void playerLoggedIn(PlayerLoggedInEvent evt) {
      Player playerEntity = evt.getEntity();
      if (playerEntity instanceof ServerPlayer serverPlayer) {
         Collection<ISlotType> slotTypes = CuriosApi.getPlayerSlots(playerEntity).values();
         Map<String, ResourceLocation> icons = new HashMap<>();
         slotTypes.forEach(type -> icons.put(type.getIdentifier(), type.getIcon()));
         PacketDistributor.sendToPlayer(serverPlayer, new SPacketSetIcons(icons), new CustomPacketPayload[0]);
      }
   }

   @SubscribeEvent
   public void onDatapackSync(OnDatapackSyncEvent evt) {
      if (evt.getPlayer() == null) {
         PlayerList playerList = evt.getPlayerList();

         for (ServerPlayer player : playerList.getPlayers()) {
            PacketDistributor.sendToPlayer(
               player, new SPacketSyncData(CuriosSlotManager.getSyncPacket(), CuriosEntityManager.getSyncPacket()), new CustomPacketPayload[0]
            );
            CuriosApi.getCuriosInventory(player)
               .ifPresent(
                  handler -> {
                     Tag tag = handler.writeTag();

                     for (Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
                        ICurioStacksHandler stacks = entry.getValue();

                        for (int i = 0; i < stacks.getSlots(); i++) {
                           stacks.getStacks().setStackInSlot(i, ItemStack.EMPTY);
                           stacks.getCosmeticStacks().setStackInSlot(i, ItemStack.EMPTY);
                        }
                     }

                     handler.readTag(tag);
                     PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                        player, new SPacketSyncCurios(player.getId(), handler.getCurios()), new CustomPacketPayload[0]
                     );
                     if (player.containerMenu instanceof ICuriosMenu curiosContainer) {
                        curiosContainer.resetSlots();
                     }
                  }
               );
            Collection<ISlotType> slotTypes = CuriosApi.getPlayerSlots(player).values();
            Map<String, ResourceLocation> icons = new HashMap<>();
            slotTypes.forEach(type -> icons.put(type.getIdentifier(), type.getIcon()));
            PacketDistributor.sendToPlayer(player, new SPacketSetIcons(icons), new CustomPacketPayload[0]);
         }
      } else {
         ServerPlayer mp = evt.getPlayer();
         PacketDistributor.sendToPlayer(
            mp, new SPacketSyncData(CuriosSlotManager.getSyncPacket(), CuriosEntityManager.getSyncPacket()), new CustomPacketPayload[0]
         );
         CuriosApi.getCuriosInventory(mp).ifPresent(handler -> {
            Tag tag = handler.writeTag();

            for (Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
               ICurioStacksHandler stacks = entry.getValue();

               for (int i = 0; i < stacks.getSlots(); i++) {
                  stacks.getStacks().setStackInSlot(i, ItemStack.EMPTY);
                  stacks.getCosmeticStacks().setStackInSlot(i, ItemStack.EMPTY);
               }
            }

            handler.readTag(tag);
            PacketDistributor.sendToPlayer(mp, new SPacketSyncCurios(mp.getId(), handler.getCurios()), new CustomPacketPayload[0]);
            if (mp.containerMenu instanceof ICuriosMenu curiosContainer) {
               curiosContainer.resetSlots();
            }
         });
         Collection<ISlotType> slotTypes = CuriosApi.getPlayerSlots(mp).values();
         Map<String, ResourceLocation> icons = new HashMap<>();
         slotTypes.forEach(type -> icons.put(type.getIdentifier(), type.getIcon()));
         PacketDistributor.sendToPlayer(mp, new SPacketSetIcons(icons), new CustomPacketPayload[0]);
      }
   }

   @SubscribeEvent
   public void entityConstructing(EntityConstructing evt) {
      if (evt.getEntity() instanceof LivingEntity livingEntity) {
         CuriosApi.getCuriosInventory(livingEntity).ifPresent(inv -> {
            Tag tag = inv.writeTag();

            for (Entry<String, ICurioStacksHandler> entry : inv.getCurios().entrySet()) {
               ICurioStacksHandler stacks = entry.getValue();

               for (int i = 0; i < stacks.getSlots(); i++) {
                  stacks.getStacks().setStackInSlot(i, ItemStack.EMPTY);
                  stacks.getCosmeticStacks().setStackInSlot(i, ItemStack.EMPTY);
               }
            }

            inv.readTag(tag);
         });
      }
   }

   @SubscribeEvent
   public void entityJoinWorld(EntityJoinLevelEvent evt) {
      Entity entity = evt.getEntity();
      if (entity instanceof ServerPlayer serverPlayerEntity) {
         CuriosApi.getCuriosInventory(serverPlayerEntity).ifPresent(handler -> {
            ServerPlayer mp = (ServerPlayer)entity;
            PacketDistributor.sendToPlayer(mp, new SPacketSyncCurios(mp.getId(), handler.getCurios()), new CustomPacketPayload[0]);
         });
      }
   }

   @SubscribeEvent
   public void playerStartTracking(StartTracking evt) {
      Entity target = evt.getTarget();
      if (evt.getEntity() instanceof ServerPlayer serverPlayer && target instanceof LivingEntity livingBase) {
         CuriosApi.getCuriosInventory(livingBase)
            .ifPresent(
               handler -> PacketDistributor.sendToPlayer(serverPlayer, new SPacketSyncCurios(target.getId(), handler.getCurios()), new CustomPacketPayload[0])
            );
      }
   }

   @SubscribeEvent
   public void playerClone(Clone evt) {
      Player player = evt.getEntity();
      Player oldPlayer = evt.getOriginal();
      Optional<ICuriosItemHandler> oldHandler = CuriosApi.getCuriosInventory(oldPlayer);
      Optional<ICuriosItemHandler> newHandler = CuriosApi.getCuriosInventory(player);
      oldHandler.ifPresent(oldCurios -> newHandler.ifPresent(newCurios -> newCurios.readTag(oldCurios.writeTag())));
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void playerDrops(LivingDropsEvent evt) {
      LivingEntity livingEntity = evt.getEntity();
      if (!livingEntity.isSpectator()) {
         CuriosApi.getCuriosInventory(livingEntity)
            .ifPresent(
               handler -> {
                  Collection<ItemEntity> drops = evt.getDrops();
                  Collection<ItemEntity> curioDrops = new ArrayList<>();
                  Map<String, ICurioStacksHandler> curios = handler.getCurios();
                  DropRulesEvent dropRulesEvent = new DropRulesEvent(livingEntity, handler, evt.getSource(), 0, evt.isRecentlyHit());
                  NeoForge.EVENT_BUS.post(dropRulesEvent);
                  List<Tuple<Predicate<ItemStack>, ICurio.DropRule>> dropRules = dropRulesEvent.getOverrides();
                  boolean keepInventory = false;
                  if (livingEntity instanceof Player) {
                     keepInventory = livingEntity.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
                     if (CuriosConfig.SERVER.keepCurios.get() != CuriosConfig.KeepCurios.DEFAULT) {
                        keepInventory = CuriosConfig.SERVER.keepCurios.get() == CuriosConfig.KeepCurios.ON;
                     }
                  }

                  boolean finalKeepInventory = keepInventory;
                  curios.forEach(
                     (id, stacksHandler) -> {
                        handleDrops(
                           id, livingEntity, dropRules, stacksHandler.getRenders(), stacksHandler.getStacks(), false, curioDrops, finalKeepInventory, evt
                        );
                        handleDrops(
                           id,
                           livingEntity,
                           dropRules,
                           stacksHandler.getRenders(),
                           stacksHandler.getCosmeticStacks(),
                           true,
                           curioDrops,
                           finalKeepInventory,
                           evt
                        );
                     }
                  );
                  CurioDropsEvent dropsEvent = (CurioDropsEvent)NeoForge.EVENT_BUS
                     .post(new CurioDropsEvent(livingEntity, handler, evt.getSource(), curioDrops, 0, evt.isRecentlyHit()));
                  if (!dropsEvent.isCanceled()) {
                     drops.addAll(curioDrops);
                  }
               }
            );
      }
   }

   @SubscribeEvent
   public void playerXPPickUp(PickupXp evt) {
      Player player = evt.getEntity();
      if (!player.level().isClientSide) {
         CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            Map<String, ICurioStacksHandler> curios = handler.getCurios();

            for (ICurioStacksHandler stacksHandler : curios.values()) {
               if (handleMending(player, stacksHandler.getStacks(), evt) || handleMending(player, stacksHandler.getCosmeticStacks(), evt)) {
                  return;
               }
            }
         });
      }
   }

   @SubscribeEvent
   public void curioRightClick(RightClickItem evt) {
      Player player = evt.getEntity();
      ItemStack stack = evt.getItemStack();
      CuriosApi.getCurio(stack).ifPresent(curio -> CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
         Map<String, ICurioStacksHandler> curios = handler.getCurios();
         Tuple<IDynamicStackHandler, SlotContext> firstSlot = null;

         for (Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {
            IDynamicStackHandler stackHandler = entry.getValue().getStacks();
            NonNullList<Boolean> activeStates = entry.getValue().getActiveStates();

            for (int i = 0; i < stackHandler.getSlots(); i++) {
               boolean active = activeStates.size() > i && (Boolean)activeStates.get(i);
               if (active) {
                  String id = entry.getKey();
                  NonNullList<Boolean> renderStates = entry.getValue().getRenders();
                  SlotContext slotContext = new SlotContext(id, player, i, false, renderStates.size() > i && (Boolean)renderStates.get(i));
                  if (stackHandler.isItemValid(i, stack) && curio.canEquipFromUse(slotContext)) {
                     ItemStack present = stackHandler.getStackInSlot(i);
                     if (present.isEmpty()) {
                        stackHandler.setStackInSlot(i, stack.copy());
                        curio.onEquipFromUse(slotContext);
                        if (!player.isCreative()) {
                           int count = stack.getCount();
                           stack.shrink(count);
                        }

                        evt.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide()));
                        evt.setCanceled(true);
                        return;
                     }

                     if (firstSlot == null && stackHandler.extractItem(i, stack.getMaxStackSize(), true).getCount() == stack.getCount()) {
                        firstSlot = new Tuple(stackHandler, slotContext);
                     }
                  }
               }
            }
         }

         if (firstSlot != null) {
            IDynamicStackHandler stackHandler = (IDynamicStackHandler)firstSlot.getA();
            SlotContext slotContext = (SlotContext)firstSlot.getB();
            int ix = slotContext.index();
            ItemStack presentx = stackHandler.getStackInSlot(ix);
            stackHandler.setStackInSlot(ix, stack.copy());
            curio.onEquipFromUse(slotContext);
            player.setItemInHand(evt.getHand(), presentx.copy());
            evt.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide()));
            evt.setCanceled(true);
         }
      }));
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onBreakBlock(BlockDropsEvent event) {
      AtomicInteger experience = new AtomicInteger(event.getDroppedExperience());
      if (experience.get() > 0 && event.getBreaker() instanceof LivingEntity entity) {
         CuriosApi.getCuriosInventory(entity)
            .ifPresent(
               handler -> {
                  for (Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
                     IDynamicStackHandler stacks = entry.getValue().getStacks();
                     NonNullList<Boolean> renderStates = entry.getValue().getRenders();

                     for (int i = 0; i < stacks.getSlots(); i++) {
                        SlotContext context = new SlotContext(entry.getKey(), entity, i, false, renderStates.size() > i && (Boolean)renderStates.get(i));
                        experience.addAndGet(
                           EnchantmentHelper.processBlockExperience(
                              event.getLevel(),
                              event.getTool(),
                              CuriosApi.getCurio(stacks.getStackInSlot(i)).map(curio -> curio.getFortuneLevel(context, null)).orElse(0)
                           )
                        );
                     }
                  }
               }
            );
         event.setDroppedExperience(experience.get());
      }
   }

   @SubscribeEvent
   public void enderManAnger(EnderManAngerEvent evt) {
      if (enderManMaskCache.size() > 500) {
         enderManMaskCache.clear();
      }

      Player player = evt.getPlayer();
      long gameTime = player.level().getGameTime();
      if (enderManMaskCache.containsKey(player.getUUID())) {
         Pair<Long, Boolean> pair = enderManMaskCache.get(player.getUUID());
         if ((Long)pair.getFirst() == gameTime) {
            evt.setCanceled((Boolean)pair.getSecond());
            return;
         }
      }

      CuriosApi.getCuriosInventory(player)
         .ifPresent(
            handler -> {
               for (Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
                  IDynamicStackHandler stacks = entry.getValue().getStacks();

                  for (int i = 0; i < stacks.getSlots(); i++) {
                     int index = i;
                     NonNullList<Boolean> renderStates = entry.getValue().getRenders();
                     boolean hasMask = CuriosApi.getCurio(stacks.getStackInSlot(i))
                        .map(
                           curio -> curio.isEnderMask(
                              new SlotContext(entry.getKey(), player, index, false, renderStates.size() > index && (Boolean)renderStates.get(index)),
                              evt.getEntity()
                           )
                        )
                        .orElse(false);
                     if (hasMask) {
                        enderManMaskCache.put(player.getUUID(), Pair.of(gameTime, true));
                        evt.setCanceled(true);
                        return;
                     }
                  }
               }
            }
         );
      enderManMaskCache.put(player.getUUID(), Pair.of(gameTime, false));
   }

   @SubscribeEvent
   public void tick(Post evt) {
      if (evt.getEntity() instanceof LivingEntity livingEntity) {
         if (livingEntity instanceof Player player && player.containerMenu instanceof CuriosContainer curiosContainer) {
            curiosContainer.checkQuickMove();
         }

         CuriosApi.getCuriosInventory(livingEntity)
            .ifPresent(
               handler -> {
                  handler.clearCachedSlotModifiers();
                  handler.handleInvalidStacks();
                  Map<String, ICurioStacksHandler> curios = handler.getCurios();

                  for (Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {
                     ICurioStacksHandler stacksHandler = entry.getValue();
                     String identifier = entry.getKey();
                     IDynamicStackHandler stackHandler = stacksHandler.getStacks();
                     IDynamicStackHandler cosmeticStackHandler = stacksHandler.getCosmeticStacks();
                     NonNullList<Boolean> renderStates = stacksHandler.getRenders();

                     for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        stacksHandler.updateActiveState(i);
                        NonNullList<Boolean> activeStates = stacksHandler.getActiveStates();
                        boolean functional = activeStates.size() > i && (Boolean)activeStates.get(i);
                        SlotContext slotContext = new SlotContext(identifier, livingEntity, i, false, renderStates.size() > i && (Boolean)renderStates.get(i));
                        ItemStack stack = stackHandler.getStackInSlot(i);
                        Optional<ICurio> currentCurio = CuriosApi.getCurio(stack);
                        if (functional && !stack.isEmpty()) {
                           stack.inventoryTick(livingEntity.level(), livingEntity, -1, false);
                           currentCurio.ifPresent(curio -> curio.curioTick(slotContext));
                        }

                        if (!livingEntity.level().isClientSide) {
                           ItemStack prevStack = stackHandler.getPreviousStackInSlot(i);
                           if (!ItemStack.matches(stack, prevStack)) {
                              Optional<ICurio> prevCurio = CuriosApi.getCurio(prevStack);
                              syncCurios(
                                 livingEntity,
                                 stack,
                                 currentCurio,
                                 prevCurio,
                                 identifier,
                                 i,
                                 false,
                                 renderStates.size() > i && (Boolean)renderStates.get(i),
                                 SPacketSyncStack.HandlerType.EQUIPMENT
                              );
                              if (functional) {
                                 NeoForge.EVENT_BUS.post(new CurioChangeEvent(livingEntity, identifier, i, prevStack, stack));
                                 ResourceLocation id = CuriosApi.getSlotId(slotContext);
                                 AttributeMap attributeMap = livingEntity.getAttributes();
                                 if (!prevStack.isEmpty()) {
                                    Multimap<Holder<Attribute>, AttributeModifier> map = CuriosApi.getAttributeModifiers(slotContext, id, prevStack);
                                    Multimap<String, AttributeModifier> slots = HashMultimap.create();
                                    Set<Holder<Attribute>> toRemove = new HashSet<>();

                                    for (Holder<Attribute> attribute : map.keySet()) {
                                       if (attribute.value() instanceof SlotAttribute wrapper) {
                                          slots.putAll(wrapper.getIdentifier(), map.get(attribute));
                                          toRemove.add(attribute);
                                       }
                                    }

                                    for (Holder<Attribute> attributex : toRemove) {
                                       map.removeAll(attributex);
                                    }

                                    map.forEach((key, value) -> {
                                       AttributeInstance attInst = attributeMap.getInstance(key);
                                       if (attInst != null) {
                                          attInst.removeModifier(value);
                                       }
                                    });
                                    handler.removeSlotModifiers(slots);
                                    prevCurio.ifPresent(curio -> curio.onUnequip(slotContext, stack));
                                 }

                                 if (!stack.isEmpty()) {
                                    Multimap<Holder<Attribute>, AttributeModifier> map = CuriosApi.getAttributeModifiers(slotContext, id, stack);
                                    Multimap<String, AttributeModifier> slots = HashMultimap.create();
                                    Set<Holder<Attribute>> toRemove = new HashSet<>();

                                    for (Holder<Attribute> attributex : map.keySet()) {
                                       if (attributex.value() instanceof SlotAttribute wrapper) {
                                          slots.putAll(wrapper.getIdentifier(), map.get(attributex));
                                          toRemove.add(attributex);
                                       }
                                    }

                                    for (Holder<Attribute> attributexx : toRemove) {
                                       map.removeAll(attributexx);
                                    }

                                    map.forEach((key, value) -> {
                                       AttributeInstance attInst = attributeMap.getInstance(key);
                                       if (attInst != null) {
                                          attInst.addOrUpdateTransientModifier(value);
                                       }
                                    });
                                    handler.addTransientSlotModifiers(slots);
                                    currentCurio.ifPresent(curio -> curio.onEquip(slotContext, prevStack));
                                    if (livingEntity instanceof ServerPlayer) {
                                       CuriosRegistry.EQUIP_TRIGGER.get().trigger(slotContext, (ServerPlayer)livingEntity, stack);
                                    }
                                 }
                              }

                              stackHandler.setPreviousStackInSlot(i, stack.copy());
                           }

                           ItemStack cosmeticStack = cosmeticStackHandler.getStackInSlot(i);
                           ItemStack prevCosmeticStack = cosmeticStackHandler.getPreviousStackInSlot(i);
                           if (!ItemStack.matches(cosmeticStack, prevCosmeticStack)) {
                              syncCurios(
                                 livingEntity,
                                 cosmeticStack,
                                 CuriosApi.getCurio(cosmeticStack),
                                 CuriosApi.getCurio(prevCosmeticStack),
                                 identifier,
                                 i,
                                 true,
                                 true,
                                 SPacketSyncStack.HandlerType.COSMETIC
                              );
                              cosmeticStackHandler.setPreviousStackInSlot(i, cosmeticStack.copy());
                           }
                        }
                     }
                  }

                  if (!livingEntity.level().isClientSide()) {
                     Set<ICurioStacksHandler> updates = handler.getUpdatingInventories();
                     if (!updates.isEmpty()) {
                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                           livingEntity, new SPacketSyncModifiers(livingEntity.getId(), updates), new CustomPacketPayload[0]
                        );
                        updates.clear();
                     }
                  }
               }
            );
      }
   }

   @SubscribeEvent
   public void livingEquipmentChange(LivingEquipmentChangeEvent evt) {
      CuriosApi.getCuriosInventory(evt.getEntity()).ifPresent(inv -> {
         ItemStack from = evt.getFrom();
         ItemStack to = evt.getTo();
         EquipmentSlot slot = evt.getSlot();
         if (!from.isEmpty()) {
            Multimap<String, AttributeModifier> slots = HashMultimap.create();
            from.forEachModifier(slot, (att, modifier) -> {
               if (att.value() instanceof SlotAttribute wrapper) {
                  slots.putAll(wrapper.getIdentifier(), Collections.singleton(modifier));
               }
            });
            inv.removeSlotModifiers(slots);
         }

         if (!to.isEmpty()) {
            Multimap<String, AttributeModifier> slots = HashMultimap.create();
            to.forEachModifier(slot, (att, modifier) -> {
               if (att.value() instanceof SlotAttribute wrapper) {
                  slots.putAll(wrapper.getIdentifier(), Collections.singleton(modifier));
               }
            });
            inv.addTransientSlotModifiers(slots);
         }
      });
   }

   private static void syncCurios(
      LivingEntity livingEntity,
      ItemStack stack,
      Optional<ICurio> currentCurio,
      Optional<ICurio> prevCurio,
      String identifier,
      int index,
      boolean cosmetic,
      boolean visible,
      SPacketSyncStack.HandlerType type
   ) {
      SlotContext slotContext = new SlotContext(identifier, livingEntity, index, cosmetic, visible);
      boolean syncable = currentCurio.<Boolean>map(curio -> curio.canSync(slotContext)).orElse(false)
         || prevCurio.<Boolean>map(curio -> curio.canSync(slotContext)).orElse(false);
      CompoundTag syncTag = syncable ? currentCurio.<CompoundTag>map(curio -> curio.writeSyncData(slotContext)).orElse(new CompoundTag()) : new CompoundTag();
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(
         livingEntity, new SPacketSyncStack(livingEntity.getId(), identifier, index, stack, type.ordinal(), syncTag), new CustomPacketPayload[0]
      );
   }
}
