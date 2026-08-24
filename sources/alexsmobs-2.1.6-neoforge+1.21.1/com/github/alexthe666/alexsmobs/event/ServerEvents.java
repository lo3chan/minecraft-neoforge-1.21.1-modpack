package com.github.alexthe666.alexsmobs.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.command.AMConfigCommand;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityFlyingFish;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquid;
import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.alexsmobs.entity.EntitySeaBear;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.github.alexthe666.alexsmobs.entity.EntitySnowLeopard;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.github.alexthe666.alexsmobs.entity.util.FlyingFishBootsUtil;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ILeftClick;
import com.github.alexthe666.alexsmobs.item.ItemGhostlyPickaxe;
import com.github.alexthe666.alexsmobs.message.MessageSwingArm;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTeleportQueue;
import com.github.alexthe666.alexsmobs.misc.EmeraldsForItemsTrade;
import com.github.alexthe666.alexsmobs.misc.ItemsForEmeraldsTrade;
import com.github.alexthe666.alexsmobs.world.AMWorldData;
import com.github.alexthe666.alexsmobs.world.BeachedCachalotWhaleSpawner;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent.Result;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.event.level.ExplosionEvent.Detonate;
import net.neoforged.neoforge.event.tick.LevelTickEvent.Post;
import net.neoforged.neoforge.event.tick.LevelTickEvent.Pre;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Triple;

public class ServerEvents {
   public static final UUID ALEX_UUID = UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c");
   public static final UUID CARRO_UUID = UUID.fromString("98905d4a-1cbc-41a4-9ded-2300404e2290");
   private static final ResourceLocation SAND_SPEED_MODIFIER = AMCompat.rl("alexsmobs", "roadrunner_speed");
   private static final ResourceLocation SNEAK_SPEED_MODIFIER = AMCompat.rl("alexsmobs", "frontier_cap_speed");
   private static final AttributeModifier SAND_SPEED_BONUS = AMCompat.attributeModifier(
      SAND_SPEED_MODIFIER, "roadrunner speed bonus", 0.10000000149011612, Operation.ADD_VALUE
   );
   private static final AttributeModifier SNEAK_SPEED_BONUS = AMCompat.attributeModifier(
      SNEAK_SPEED_MODIFIER, "frontier cap speed bonus", 0.10000000149011612, Operation.ADD_VALUE
   );
   private static final Map<ServerLevel, BeachedCachalotWhaleSpawner> BEACHED_CACHALOT_WHALE_SPAWNER_MAP = new HashMap<>();
   private static final Random RAND = new Random();

   @SubscribeEvent
   public void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
      AMEffectRegistry.registerBrewingRecipes(event.getBuilder());
   }

   @SubscribeEvent
   public void onServerTickPre(Pre tick) {
      this.onLevelTick(tick.getLevel());
   }

   @SubscribeEvent
   public void onServerTickPost(Post tick) {
      this.onLevelTick(tick.getLevel());
   }

   private void onLevelTick(Level level) {
      if (!level.isClientSide() && level instanceof ServerLevel serverWorld) {
         BEACHED_CACHALOT_WHALE_SPAWNER_MAP.computeIfAbsent(serverWorld, k -> new BeachedCachalotWhaleSpawner(serverWorld));
         BeachedCachalotWhaleSpawner spawner = BEACHED_CACHALOT_WHALE_SPAWNER_MAP.get(serverWorld);
         spawner.tick();
         if (!AMTeleportQueue.PLAYERS.isEmpty()) {
            ObjectListIterator var4 = AMTeleportQueue.PLAYERS.iterator();

            while (var4.hasNext()) {
               Triple<ServerPlayer, ServerLevel, BlockPos> triple = (Triple<ServerPlayer, ServerLevel, BlockPos>)var4.next();
               ServerPlayer player = (ServerPlayer)triple.getLeft();
               ServerLevel endpointWorld = (ServerLevel)triple.getMiddle();
               BlockPos endpoint = (BlockPos)triple.getRight();
               int heightFromMap = endpointWorld.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, endpoint.getX(), endpoint.getZ());
               endpoint = new BlockPos(endpoint.getX(), Math.max(heightFromMap, endpoint.getY()), endpoint.getZ());
               player.teleportTo(endpointWorld, endpoint.getX() + 0.5, endpoint.getY() + 0.5, endpoint.getZ() + 0.5, player.getYRot(), player.getXRot());
               ChunkPos chunkpos = new ChunkPos(endpoint);
               endpointWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, player.getId());
               player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
            }

            AMTeleportQueue.PLAYERS.clear();
         }
      }

      AMWorldData data = AMWorldData.get(level);
      if (data != null) {
         data.tickPupfish();
      }
   }

   protected static BlockHitResult rayTrace(Level worldIn, Player player, Fluid fluidMode) {
      float x = player.getXRot();
      float y = player.getYRot();
      Vec3 vector3d = player.getEyePosition(1.0F);
      float f0 = -y * 0.017453292F - 3.1415927F;
      float f1 = -x * 0.017453292F;
      float f2 = Mth.cos(f0);
      float f3 = Mth.sin(f0);
      float f4 = -Mth.cos(f1);
      float f5 = Mth.sin(f1);
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      Holder<Attribute> reachAttribute = AMPlatform.blockReach();
      AttributeInstance reachInstance = reachAttribute == null ? null : player.getAttribute(reachAttribute);
      double d0 = reachInstance == null ? 5.0 : reachInstance.getValue();
      Vec3 vector3d1 = vector3d.add(f6 * d0, f5 * d0, f7 * d0);
      return worldIn.clip(new ClipContext(vector3d, vector3d1, Block.OUTLINE, fluidMode, player));
   }

   @SubscribeEvent
   public void onItemUseLast(Finish event) {
      if (event.getItem().getItem() == Items.CHORUS_FRUIT
         && RAND.nextInt(3) == 0
         && event.getEntity().hasEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()))) {
         event.getEntity().removeEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()));
      }
   }

   @SubscribeEvent
   public void onExplosionDetonate(Detonate event) {
      event.getAffectedEntities()
         .removeIf(affected -> affected instanceof ItemEntity itemEntity && itemEntity.getItem().is(AMBlockRegistry.TRANSMUTATION_TABLE.get().asItem()));
   }

   @SubscribeEvent
   public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (AMConfig.giveBookOnStartup) {
         CompoundTag playerData = event.getEntity().getPersistentData();
         CompoundTag data = AMCompat.getCompound(playerData, "PlayerPersisted");
         if (data != null && !AMCompat.getBoolean(data, "alexsmobs_has_book")) {
            ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack((ItemLike)AMItemRegistry.ANIMAL_DICTIONARY.get()));
            boolean isAlex = Objects.equals(event.getEntity().getUUID(), ALEX_UUID);
            if (isAlex || Objects.equals(event.getEntity().getUUID(), CARRO_UUID)) {
               ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack((ItemLike)AMItemRegistry.BEAR_DUST.get()));
            }

            if (isAlex) {
               ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack((ItemLike)AMItemRegistry.NOVELTY_HAT.get()));
            }

            data.putBoolean("alexsmobs_has_book", true);
            playerData.put("PlayerPersisted", data);
         }
      }
   }

   @SubscribeEvent
   public void onPlayerLeftClick(LeftClickEmpty event) {
      boolean flag = false;
      ItemStack leftItem = event.getEntity().getOffhandItem();
      ItemStack rightItem = event.getEntity().getMainHandItem();
      if (leftItem.getItem() instanceof ILeftClick iLeftClick) {
         iLeftClick.onLeftClick(leftItem, event.getEntity());
         flag = true;
      }

      if (rightItem.getItem() instanceof ILeftClick iLeftClick) {
         iLeftClick.onLeftClick(rightItem, event.getEntity());
         flag = true;
      }

      if (flag && event.getLevel().isClientSide()) {
         AlexsMobs.sendMSGToServer(MessageSwingArm.INSTANCE);
      }
   }

   @SubscribeEvent
   public void onStruckByLightning(EntityStruckByLightningEvent event) {
      if (event.getEntity().getType() == EntityType.SQUID && !event.getEntity().level().isClientSide()) {
         ServerLevel level = (ServerLevel)event.getEntity().level();
         event.setCanceled(true);
         EntityGiantSquid squid = AMCompat.create(AMEntityRegistry.GIANT_SQUID.get(), level);
         squid.moveTo(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity().getYRot(), event.getEntity().getXRot());
         squid.finalizeSpawn(level, level.getCurrentDifficultyAt(squid.blockPosition()), MobSpawnType.CONVERSION, null);
         if (event.getEntity().hasCustomName()) {
            squid.setCustomName(event.getEntity().getCustomName());
            squid.setCustomNameVisible(event.getEntity().isCustomNameVisible());
         }

         squid.setBlue(true);
         squid.setPersistenceRequired();
         level.addFreshEntityWithPassengers(squid);
         event.getEntity().discard();
      }
   }

   @SubscribeEvent
   public void onProjectileHit(ProjectileImpactEvent event) {
      if (event.getRayTraceResult() instanceof EntityHitResult hitResult
         && hitResult.getEntity() instanceof EntityEmu emu
         && !event.getEntity().level().isClientSide()) {
         if (event.getEntity() instanceof AbstractArrow arrow) {
            arrow.setPierceLevel((byte)0);
         }

         if ((emu.getAnimation() == EntityEmu.ANIMATION_DODGE_RIGHT || emu.getAnimation() == EntityEmu.ANIMATION_DODGE_LEFT) && emu.getAnimationTick() < 7) {
            event.setCanceled(true);
         }

         if (emu.getAnimation() != EntityEmu.ANIMATION_DODGE_RIGHT && emu.getAnimation() != EntityEmu.ANIMATION_DODGE_LEFT) {
            boolean left = true;
            Vec3 arrowPos = event.getEntity().position();
            Vec3 rightVector = emu.getLookAngle().yRot(1.5707964F).add(emu.position());
            Vec3 leftVector = emu.getLookAngle().yRot(-1.5707964F).add(emu.position());
            if (arrowPos.distanceTo(rightVector) < arrowPos.distanceTo(leftVector)) {
               left = false;
            } else if (arrowPos.distanceTo(rightVector) > arrowPos.distanceTo(leftVector)) {
               left = true;
            } else {
               left = emu.getRandom().nextBoolean();
            }

            Vec3 vector3d2 = event.getEntity().getDeltaMovement().yRot((float)((left ? -0.5F : 0.5F) * 3.141592653589793)).normalize();
            emu.setAnimation(left ? EntityEmu.ANIMATION_DODGE_LEFT : EntityEmu.ANIMATION_DODGE_RIGHT);
            emu.hasImpulse = true;
            if (!emu.horizontalCollision) {
               emu.move(MoverType.SELF, new Vec3(vector3d2.x() * 0.25, 0.10000000149011612, vector3d2.z() * 0.25));
            }

            if (!event.getEntity().level().isClientSide()
               && event.getEntity() instanceof Projectile projectile
               && projectile.getOwner() instanceof ServerPlayer serverPlayer) {
               AMAdvancementTriggerRegistry.EMU_DODGE.trigger(serverPlayer);
            }

            emu.setDeltaMovement(emu.getDeltaMovement().add(vector3d2.x() * 0.5, 0.3199999928474426, vector3d2.z() * 0.5));
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public void onEntityDespawnAttempt(MobDespawnEvent event) {
      if (event.getEntity().hasEffect(AMCompat.effect(AMEffectRegistry.DEBILITATING_STING.get()))
         && event.getEntity().getEffect(AMCompat.effect(AMEffectRegistry.DEBILITATING_STING.get())) != null
         && event.getEntity().getEffect(AMCompat.effect(AMEffectRegistry.DEBILITATING_STING.get())).getAmplifier() > 0) {
         event.setResult(Result.DENY);
      }
   }

   @SubscribeEvent
   public void onTradeSetup(VillagerTradesEvent event) {
      if (event.getType() == VillagerProfession.FISHERMAN) {
         ItemListing ambergrisTrade = new EmeraldsForItemsTrade((ItemLike)AMItemRegistry.AMBERGRIS.get(), 20, 3, 4);
         List<ItemListing> list = (List<ItemListing>)event.getTrades().get(2);
         list.add(ambergrisTrade);
         event.getTrades().put(2, list);
      }
   }

   private static List<ItemListing> amGenericPool(WandererTradesEvent event) {
      return event.getGenericTrades();
   }

   private static List<ItemListing> amRarePool(WandererTradesEvent event) {
      return event.getRareTrades();
   }

   @SubscribeEvent
   public void onWanderingTradeSetup(WandererTradesEvent event) {
      if (AMConfig.wanderingTraderOffers) {
         List<ItemListing> genericTrades = amGenericPool(event);
         List<ItemListing> rareTrades = amRarePool(event);
         genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.ANIMAL_DICTIONARY.get(), 4, 1, 2, 1));
         genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.ACACIA_BLOSSOM.get(), 3, 2, 2, 1));
         if (AMConfig.cockroachSpawnWeight > 0) {
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.COCKROACH_OOTHECA.get(), 2, 1, 2, 1));
         }

         if (AMConfig.blobfishSpawnWeight > 0) {
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.BLOBFISH_BUCKET.get(), 4, 1, 3, 1));
         }

         if (AMConfig.crocodileSpawnWeight > 0) {
            genericTrades.add(new ItemsForEmeraldsTrade(AMBlockRegistry.CROCODILE_EGG.get().asItem(), 6, 1, 2, 1));
         }

         genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.BEAR_FUR.get(), 1, 1, 2, 1));
         genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.CROCODILE_SCUTE.get(), 5, 1, 2, 1));
         genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.ROADRUNNER_FEATHER.get(), 1, 2, 2, 2));
         genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.MOSQUITO_LARVA.get(), 1, 3, 5, 1));
         rareTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.SOMBRERO.get(), 20, 1, 1, 1));
         rareTrades.add(new ItemsForEmeraldsTrade(AMBlockRegistry.BANANA_PEEL.get(), 1, 2, 1, 1));
         rareTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.BLOOD_SAC.get(), 5, 2, 3, 1));
      }
   }

   @SubscribeEvent
   public void onUseItem(RightClickItem event) {
      Player player = event.getEntity();
      if (event.getItemStack().getItem() == Items.WHEAT
         && player.getVehicle() instanceof EntityElephant elephant
         && elephant.triggerCharge(event.getItemStack())) {
         player.swing(event.getHand());
         if (!player.isCreative()) {
            event.getItemStack().shrink(1);
         }
      }

      if (event.getItemStack().getItem() == Items.GLASS_BOTTLE && AMConfig.lavaBottleEnabled) {
         HitResult raytraceresult = rayTrace(event.getLevel(), player, Fluid.SOURCE_ONLY);
         if (raytraceresult.getType() == Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)raytraceresult).getBlockPos();
            if (event.getLevel().mayInteract(player, blockpos) && event.getLevel().getFluidState(blockpos).is(FluidTags.LAVA)) {
               player.gameEvent(GameEvent.ITEM_INTERACT_START);
               event.getLevel().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
               player.awardStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
               player.igniteForSeconds(6.0F);
               if (!player.addItem(new ItemStack((ItemLike)AMItemRegistry.LAVA_BOTTLE.get()))) {
                  AMCompat.spawnAtLocation(player, new ItemStack((ItemLike)AMItemRegistry.LAVA_BOTTLE.get()));
               }

               player.swing(event.getHand());
               if (!player.isCreative()) {
                  event.getItemStack().shrink(1);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onInteractWithEntity(EntityInteract event) {
      this.interactWithEntity(event.getEntity(), event.getItemStack(), event.getLevel(), event.getTarget(), () -> {
         event.setCanceled(true);
         event.setCancellationResult(InteractionResult.SUCCESS);
      });
   }

   @SubscribeEvent
   public void onInteractWithEntitySpecific(EntityInteractSpecific event) {
      this.interactWithEntity(event.getEntity(), event.getItemStack(), event.getLevel(), event.getTarget(), () -> {
         event.setCanceled(true);
         event.setCancellationResult(InteractionResult.SUCCESS);
      });
   }

   private void interactWithEntity(Player player, ItemStack held, Level level, Entity targeted, Runnable consume) {
      if (targeted instanceof LivingEntity living) {
         if (!player.isShiftKeyDown() && VineLassoUtil.hasLassoData(living)) {
            if (!player.level().isClientSide()) {
               AMCompat.spawnAtLocation(targeted, new ItemStack((ItemLike)AMItemRegistry.VINE_LASSO.get()));
            }

            VineLassoUtil.lassoTo(null, living);
            consume.run();
         }

         if (!(targeted instanceof Player)
            && !(targeted instanceof EntityEndergrade)
            && living.hasEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()))
            && held.getItem() == Items.CHORUS_FRUIT) {
            if (!player.isCreative()) {
               held.shrink(1);
            }

            targeted.gameEvent(GameEvent.EAT);
            targeted.playSound(SoundEvents.GENERIC_EAT, 1.0F, 0.5F + player.getRandom().nextFloat());
            if (player.getRandom().nextFloat() < 0.4F) {
               living.removeEffect(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()));
               Items.CHORUS_FRUIT.finishUsingItem(held.copy(), level, living);
            }

            consume.run();
         }

         if (RainbowUtil.getRainbowType(living) > 0 && held.getItem() == Items.SPONGE) {
            consume.run();
            RainbowUtil.setRainbowType(living, 0);
            if (!player.isCreative()) {
               held.shrink(1);
            }

            ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
            if (!player.addItem(wetSponge)) {
               player.drop(wetSponge, true);
            }
         }

         if (living instanceof Rabbit rabbit && held.getItem() == AMItemRegistry.MUNGAL_SPORES.get() && AMConfig.bunfungusTransformation) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            if (!player.level().isClientSide() && random.nextFloat() < 0.15F) {
               EntityBunfungus bunfungus = (EntityBunfungus)rabbit.convertTo(AMEntityRegistry.BUNFUNGUS.get(), true);
               if (bunfungus != null) {
                  player.level().addFreshEntity(bunfungus);
                  bunfungus.setTransformsIn(50);
               }
            } else {
               for (int i = 0; i < 2 + random.nextInt(2); i++) {
                  double d0 = random.nextGaussian() * 0.02;
                  double d1 = 0.05000000074505806 + random.nextGaussian() * 0.02;
                  double d2 = random.nextGaussian() * 0.02;
                  targeted.level()
                     .addParticle(
                        (ParticleOptions)AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get(),
                        targeted.getRandomX(0.699999988079071),
                        targeted.getY(0.6000000238418579),
                        targeted.getRandomZ(0.699999988079071),
                        d0,
                        d1,
                        d2
                     );
               }
            }

            if (!player.isCreative()) {
               held.shrink(1);
            }

            consume.run();
         }
      }
   }

   @SubscribeEvent
   public void onUseItemAir(RightClickEmpty event) {
      ItemStack stack = event.getEntity().getItemInHand(event.getHand());
      if (stack.isEmpty()) {
         stack = event.getEntity().getItemBySlot(EquipmentSlot.MAINHAND);
      }

      if (RainbowUtil.getRainbowType(event.getEntity()) > 0 && stack.is(Items.SPONGE)) {
         event.getEntity().swing(InteractionHand.MAIN_HAND);
         RainbowUtil.setRainbowType(event.getEntity(), 0);
         if (!event.getEntity().isCreative()) {
            stack.shrink(1);
         }

         ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
         if (!event.getEntity().addItem(wetSponge)) {
            event.getEntity().drop(wetSponge, true);
         }
      }
   }

   @SubscribeEvent
   public void onUseItemOnBlock(RightClickBlock event) {
      if (AlexsMobs.isAprilFools() && event.getItemStack().is(Items.STICK) && !AMCompat.isOnCooldown(event.getEntity().getCooldowns(), Items.STICK)) {
         BlockState state = event.getEntity().level().getBlockState(event.getPos());
         boolean flag = false;
         if (state.is(Blocks.SAND)) {
            flag = true;
            event.getEntity().level().setBlockAndUpdate(event.getPos(), AMBlockRegistry.SAND_CIRCLE.get().defaultBlockState());
         } else if (state.is(Blocks.RED_SAND)) {
            flag = true;
            event.getEntity().level().setBlockAndUpdate(event.getPos(), AMBlockRegistry.RED_SAND_CIRCLE.get().defaultBlockState());
         }

         if (flag) {
            event.setCanceled(true);
            event.getEntity().gameEvent(GameEvent.BLOCK_PLACE);
            event.getEntity().playSound(SoundEvents.SAND_BREAK, 1.0F, 1.0F);
            AMCompat.addCooldown(event.getEntity().getCooldowns(), Items.STICK, 30);
            event.setCancellationResult(InteractionResult.SUCCESS);
         }
      }
   }

   @SubscribeEvent
   public void onEntityDrops(LivingDropsEvent event) {
      if (VineLassoUtil.hasLassoData(event.getEntity())) {
         VineLassoUtil.lassoTo(null, event.getEntity());
         event.getDrops()
            .add(
               new ItemEntity(
                  event.getEntity().level(),
                  event.getEntity().getX(),
                  event.getEntity().getY(),
                  event.getEntity().getZ(),
                  new ItemStack((ItemLike)AMItemRegistry.VINE_LASSO.get())
               )
            );
      }
   }

   @SubscribeEvent
   public void onEntityFinalizeSpawn(FinalizeSpawnEvent event) {
      Mob entity = event.getEntity();
      if (entity instanceof WanderingTrader trader && AMConfig.elephantTraderSpawnChance > 0.0) {
         Biome biome = (Biome)event.getLevel().getBiome(entity.blockPosition()).value();
         if (RAND.nextFloat() <= AMConfig.elephantTraderSpawnChance && (!AMConfig.limitElephantTraderBiomes || biome.getBaseTemperature() >= 1.0F)) {
            ChunkPos chunkPos = new ChunkPos(trader.blockPosition());
            if (event.getLevel().getChunkSource().getChunkNow(chunkPos.x, chunkPos.z) != null) {
               EntityElephant elephant = AMCompat.create(AMEntityRegistry.ELEPHANT.get(), trader.level());
               elephant.copyPosition(trader);
               if (elephant.canSpawnWithTraderHere()) {
                  elephant.setTrader(true);
                  elephant.setChested(true);
                  if (!event.getLevel().isClientSide()) {
                     trader.level().addFreshEntity(elephant);
                     AMCompat.startRiding(trader, elephant, true);
                  }

                  elephant.addElephantLoot(null, RAND.nextInt());
               }
            }
         }
      }

      try {
         if (AMConfig.spidersAttackFlies && entity instanceof Spider spider) {
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal(spider, EntityFly.class, 1, true, false, null));
         } else if (AMConfig.wolvesAttackMoose && entity instanceof Wolf wolf) {
            wolf.targetSelector.addGoal(6, new NonTameRandomTargetGoal(wolf, EntityMoose.class, false, null));
         } else if (AMConfig.polarBearsAttackSeals && entity instanceof PolarBear bear) {
            bear.targetSelector.addGoal(6, new NearestAttackableTargetGoal(bear, EntitySeal.class, 15, true, true, null));
         } else if (entity instanceof Creeper creeper) {
            creeper.targetSelector.addGoal(3, new AvoidEntityGoal(creeper, EntitySnowLeopard.class, 6.0F, 1.0, 1.2));
            creeper.targetSelector.addGoal(3, new AvoidEntityGoal(creeper, EntityTiger.class, 6.0F, 1.0, 1.2));
         } else if (!AMConfig.catsAndFoxesAttackJerboas || !(entity instanceof Fox) && !(entity instanceof Cat) && !(entity instanceof Ocelot)) {
            if (AMConfig.bunfungusTransformation && entity instanceof Rabbit rabbit) {
               rabbit.goalSelector.addGoal(3, new TemptGoal(rabbit, 1.0, Ingredient.of(new ItemLike[]{(ItemLike)AMItemRegistry.MUNGAL_SPORES.get()}), false));
            } else if (AMConfig.dolphinsAttackFlyingFish && entity instanceof Dolphin dolphin) {
               dolphin.targetSelector.addGoal(2, new NearestAttackableTargetGoal(dolphin, EntityFlyingFish.class, 70, true, true, null));
            }
         } else {
            entity.targetSelector.addGoal(6, new NearestAttackableTargetGoal(entity, EntityJerboa.class, 45, true, true, null));
         }
      } catch (Exception var10) {
         AlexsMobs.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
      }
   }

   @SubscribeEvent
   public void onPlayerAttackEntityEvent(AttackEntityEvent event) {
      if (event.getTarget() instanceof LivingEntity living) {
         if (event.getEntity().getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.MOOSE_HEADGEAR.get()) {
            AMCompat.knockback(living, 1.0, Mth.sin(event.getEntity().getYRot() * 0.017453292F), -Mth.cos(event.getEntity().getYRot() * 0.017453292F));
         }

         if (event.getEntity().hasEffect(AMCompat.effect(AMEffectRegistry.TIGERS_BLESSING.get()))
            && !event.getTarget().isAlliedTo(event.getEntity())
            && !(event.getTarget() instanceof EntityTiger)) {
            AABB bb = new AABB(
               event.getEntity().getX() - 32.0,
               event.getEntity().getY() - 32.0,
               event.getEntity().getZ() - 32.0,
               event.getEntity().getZ() + 32.0,
               event.getEntity().getY() + 32.0,
               event.getEntity().getZ() + 32.0
            );

            for (EntityTiger tiger : event.getEntity().level().getEntitiesOfClass(EntityTiger.class, bb, EntitySelector.ENTITY_STILL_ALIVE)) {
               if (!tiger.isBaby()) {
                  tiger.setTarget(living);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onLivingDamageEvent(LivingIncomingDamageEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         if (event.getAmount() > 0.0F
            && attacker.hasEffect(AMCompat.effect(AMEffectRegistry.SOULSTEAL.get()))
            && attacker.getEffect(AMCompat.effect(AMEffectRegistry.SOULSTEAL.get())) != null) {
            int level = attacker.getEffect(AMCompat.effect(AMEffectRegistry.SOULSTEAL.get())).getAmplifier() + 1;
            if (attacker.getHealth() < attacker.getMaxHealth() && ThreadLocalRandom.current().nextFloat() < 0.25F + level * 0.25F) {
               attacker.heal(Math.min(event.getAmount() / 2.0F * level, (float)(2 + 2 * level)));
            }
         }

         if (event.getEntity() instanceof Player player) {
            if (attacker instanceof EntityMimicOctopus octupus && octupus.isOwnedBy(player)) {
               event.setCanceled(true);
               return;
            }

            if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL.get()
               && attacker.distanceTo(player) < attacker.getBbWidth() + player.getBbWidth() + 0.5F) {
               attacker.hurt(attacker.damageSources().thorns(player), 1.0F);
               AMCompat.knockback(attacker, 0.5, Mth.sin((attacker.getYRot() + 180.0F) * 0.017453292F), -Mth.cos((attacker.getYRot() + 180.0F) * 0.017453292F));
            }
         }
      }

      if (!event.getEntity().getItemBySlot(EquipmentSlot.LEGS).isEmpty()
         && event.getEntity().getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.EMU_LEGGINGS.get()
         && event.getSource().is(DamageTypeTags.IS_PROJECTILE)
         && event.getEntity().getRandom().nextFloat() < AMConfig.emuPantsDodgeChance) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void onLivingSetTargetEvent(LivingChangeTargetEvent event) {
      if (event.getNewAboutToBeSetTarget() != null && event.getEntity() instanceof Mob mob) {
         if (AMCompat.isArthropod(mob)
            && event.getNewAboutToBeSetTarget().hasEffect(AMCompat.effect(AMEffectRegistry.BUG_PHEROMONES.get()))
            && event.getEntity().getLastHurtByMob() != event.getNewAboutToBeSetTarget()) {
            event.setCanceled(true);
            return;
         }

         if (AMCompat.isUndead(mob)
            && !mob.getType().builtInRegistryHolder().is(AMTagRegistry.IGNORES_KIMONO)
            && event.getNewAboutToBeSetTarget().getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.UNSETTLING_KIMONO.get())
            && event.getEntity().getLastHurtByMob() != event.getNewAboutToBeSetTarget()) {
            event.setCanceled(true);
            return;
         }
      }
   }

   @SubscribeEvent
   public void onLivingUpdateEvent(net.neoforged.neoforge.event.tick.EntityTickEvent.Pre event) {
      if (event.getEntity() instanceof LivingEntity living) {
         this.onLivingUpdate(living);
      }
   }

   private void onLivingUpdate(LivingEntity entity) {
      if (entity instanceof Player player) {
         if (player.getEyeHeight() < player.getBbHeight() * 0.5) {
            player.refreshDimensions();
         }

         if (entity.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
            AttributeInstance attributes = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (player.getItemBySlot(EquipmentSlot.FEET).getItem() == AMItemRegistry.ROADDRUNNER_BOOTS.get()
               || AMCompat.hasModifier(attributes, SAND_SPEED_MODIFIER)) {
               boolean sand = player.level().getBlockState(this.getDownPos(player.blockPosition(), player.level())).is(BlockTags.SAND);
               if (sand && !AMCompat.hasModifier(attributes, SAND_SPEED_MODIFIER)) {
                  attributes.addPermanentModifier(SAND_SPEED_BONUS);
               }

               if (player.tickCount % 25 == 0
                  && (player.getItemBySlot(EquipmentSlot.FEET).getItem() != AMItemRegistry.ROADDRUNNER_BOOTS.get() || !sand)
                  && AMCompat.hasModifier(attributes, SAND_SPEED_MODIFIER)) {
                  attributes.removeModifier(SAND_SPEED_MODIFIER);
               }
            }

            if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.FRONTIER_CAP.get()
               || AMCompat.hasModifier(attributes, SNEAK_SPEED_MODIFIER)) {
               boolean shift = player.isShiftKeyDown();
               if (shift && !AMCompat.hasModifier(attributes, SNEAK_SPEED_MODIFIER)) {
                  attributes.addPermanentModifier(SNEAK_SPEED_BONUS);
               }

               if ((!shift || player.getItemBySlot(EquipmentSlot.HEAD).getItem() != AMItemRegistry.FRONTIER_CAP.get())
                  && AMCompat.hasModifier(attributes, SNEAK_SPEED_MODIFIER)) {
                  attributes.removeModifier(SNEAK_SPEED_MODIFIER);
               }
            }
         }

         if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL.get() && !player.isEyeInFluid(FluidTags.WATER)) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 310, 0, false, false, true));
         }
      }

      ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
      if (!boots.isEmpty()
         && AMCompat.hasTag(boots)
         && AMCompat.getOrCreateTag(boots).contains("BisonFur")
         && AMCompat.getBoolean(AMCompat.getOrCreateTag(boots), "BisonFur")) {
         BlockPos posBelow = new BlockPos((int)entity.getX(), (int)(entity.getBoundingBox().minY - 0.10000000149011612), (int)entity.getZ());
         if (entity.level().getBlockState(posBelow).is(Blocks.POWDER_SNOW)) {
            entity.setOnGround(true);
            entity.setTicksFrozen(0);
            entity.setPos(entity.getX(), Math.max(entity.getY(), (double)(posBelow.getY() + 1.0F)), entity.getZ());
         }

         if (entity.isInPowderSnow) {
            entity.setOnGround(true);
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.10000000149011612, 0.0));
         }
      }

      if (entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.CENTIPEDE_LEGGINGS.get() && entity.horizontalCollision && !entity.isInWater()) {
         entity.fallDistance = 0.0F;
         Vec3 motion = entity.getDeltaMovement();
         double d2 = 0.1;
         if (entity.isShiftKeyDown() || !entity.getBlockStateOn().isScaffolding(entity) && entity.isSuppressingSlidingDownLadder()) {
            d2 = 0.0;
         }

         motion = new Vec3(Mth.clamp(motion.x, -0.15000000596046448, 0.15000000596046448), d2, Mth.clamp(motion.z, -0.15000000596046448, 0.15000000596046448));
         entity.setDeltaMovement(motion);
      }

      if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SOMBRERO.get()
         && !entity.level().isClientSide()
         && AlexsMobs.isAprilFools()
         && entity.isInWaterOrBubble()) {
         RandomSource random = entity.getRandom();
         if (random.nextInt(245) == 0 && !EntitySeaBear.isMobSafe(entity)) {
            int dist = 32;
            List<EntitySeaBear> nearbySeabears = entity.level().getEntitiesOfClass(EntitySeaBear.class, entity.getBoundingBox().inflate(32.0, 32.0, 32.0));
            if (nearbySeabears.isEmpty()) {
               EntitySeaBear bear = AMCompat.create(AMEntityRegistry.SEA_BEAR.get(), entity.level());
               BlockPos at = entity.blockPosition();
               BlockPos farOff = null;

               for (int i = 0; i < 15; i++) {
                  int f1 = (int)Math.signum(random.nextInt() - 0.5F);
                  int f2 = (int)Math.signum(random.nextInt() - 0.5F);
                  BlockPos pos1 = at.offset(f1 * (10 + random.nextInt(22)), random.nextInt(1), f2 * (10 + random.nextInt(22)));
                  if (entity.level().isWaterAt(pos1)) {
                     farOff = pos1;
                  }
               }

               if (farOff != null) {
                  bear.setPos(farOff.getX() + 0.5F, farOff.getY() + 0.5F, farOff.getZ() + 0.5F);
                  bear.setYRot(random.nextFloat() * 360.0F);
                  bear.setTarget(entity);
                  entity.level().addFreshEntity(bear);
               }
            } else {
               for (EntitySeaBear bear : nearbySeabears) {
                  bear.setTarget(entity);
               }
            }
         }
      }

      if (VineLassoUtil.hasLassoData(entity)) {
         VineLassoUtil.tickLasso(entity);
      }

      if (RockyChestplateUtil.isWearing(entity)) {
         RockyChestplateUtil.tickRockyRolling(entity);
      }

      if (FlyingFishBootsUtil.isWearing(entity)) {
         FlyingFishBootsUtil.tickFlyingFishBoots(entity);
      }
   }

   private BlockPos getDownPos(BlockPos entered, LevelAccessor world) {
      for (int i = 0; world.isEmptyBlock(entered) && i < 3; i++) {
         entered = entered.below();
      }

      return entered;
   }

   @SubscribeEvent
   public void onFOVUpdate(ComputeFovModifierEvent event) {
      if (event.getPlayer().hasEffect(AMCompat.effect(AMEffectRegistry.FEAR.get()))
         || event.getPlayer().hasEffect(AMCompat.effect(AMEffectRegistry.POWER_DOWN.get()))) {
         event.setNewFovModifier(1.0F);
      }
   }

   @SubscribeEvent
   public void onLivingAttack(LivingIncomingDamageEvent event) {
      if (!event.getEntity().getUseItem().isEmpty()
         && event.getSource() != null
         && event.getSource().getEntity() != null
         && event.getEntity().getUseItem().getItem() == AMItemRegistry.SHIELD_OF_THE_DEEP.get()
         && event.getSource().getEntity() instanceof LivingEntity living) {
         boolean flag = false;
         if (living.distanceTo(event.getEntity()) <= 4.0F && !living.hasEffect(AMCompat.effect(AMEffectRegistry.EXSANGUINATION.get()))) {
            living.addEffect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.EXSANGUINATION.get()), 60, 2));
            flag = true;
         }

         if (event.getEntity().isInWaterOrBubble()) {
            event.getEntity().setAirSupply(Math.min(event.getEntity().getMaxAirSupply(), event.getEntity().getAirSupply() + 150));
            flag = true;
         }

         if (flag) {
            AMCompat.hurtAndBreak(event.getEntity().getUseItem(), 1, event.getEntity(), event.getEntity().getUsedItemHand());
         }
      }
   }

   @SubscribeEvent
   public void onTooltip(ItemTooltipEvent event) {
      CompoundTag tag = AMCompat.getTag(event.getItemStack());
      if (tag != null && AMCompat.contains(tag, "BisonFur") && AMCompat.getBoolean(tag, "BisonFur")) {
         event.getToolTip().add(Component.translatable("item.alexsmobs.insulated_with_fur").withStyle(ChatFormatting.AQUA));
      }
   }

   @SubscribeEvent
   public void onAddReloadListener(AddReloadListenerEvent event) {
      AlexsMobs.LOGGER.info("Adding datapack listener capsid_recipes");
      event.addListener(AlexsMobs.PROXY.getCapsidRecipeManager());
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onHarvestCheck(HarvestCheck event) {
      if (event.getEntity() != null
         && event.getEntity().isHolding(AMItemRegistry.GHOSTLY_PICKAXE.get())
         && ItemGhostlyPickaxe.shouldStoreInGhost(event.getEntity(), event.getEntity().getMainHandItem())) {
         event.setCanHarvest(false);
      }
   }

   @SubscribeEvent
   public void onRegisterCommands(RegisterCommandsEvent event) {
      AMConfigCommand.register(event.getDispatcher());
   }
}
