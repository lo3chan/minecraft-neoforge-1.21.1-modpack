package com.aetherteam.aether.attachment;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.portal.PortalClientUtil;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.miscellaneous.CloudMinion;
import com.aetherteam.aether.entity.miscellaneous.Parachute;
import com.aetherteam.aether.entity.passive.Aerbunny;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.miscellaneous.ParachuteItem;
import com.aetherteam.aether.network.packet.AetherPlayerSyncPacket;
import com.aetherteam.aether.network.packet.clientbound.CloudMinionPacket;
import com.aetherteam.aether.network.packet.clientbound.RemountAerbunnyPacket;
import com.aetherteam.aether.perk.CustomizationsOptions;
import com.aetherteam.aether.perk.data.ClientDeveloperGlowPerkData;
import com.aetherteam.aether.perk.data.ClientHaloPerkData;
import com.aetherteam.aether.perk.data.ClientMoaSkinPerkData;
import com.aetherteam.aether.perk.data.ServerPerkData;
import com.aetherteam.nitrogen.attachment.INBTSynchable;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Type;
import com.aetherteam.nitrogen.network.packet.SyncPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Triple;

public class AetherPlayerAttachment implements INBTSynchable {
   private static final ResourceLocation LIFE_SHARD_HEALTH_ID = ResourceLocation.fromNamespaceAndPath("aether", "life_shard_max_health");
   private boolean canGetPortal = true;
   private boolean canSpawnInAether = true;
   public float portalIntensity;
   public float oPortalIntensity;
   private boolean isHitting;
   private boolean isMoving;
   private boolean isJumping;
   private boolean isGravititeJumpActive;
   private boolean seenSunSpiritDialogue;
   private int goldenDartCount;
   private int poisonDartCount;
   private int enchantedDartCount;
   private int removeGoldenDartTime;
   private int removePoisonDartTime;
   private int removeEnchantedDartTime;
   private int remedyStartDuration;
   private int impactedMaximum;
   private int impactedTimer;
   private boolean performVampireHealing;
   @Nullable
   private Aerbunny mountedAerbunny;
   private Optional<CompoundTag> mountedAerbunnyTag = Optional.empty();
   private Optional<UUID> lastRiddenMoa = Optional.empty();
   private final List<CloudMinion> cloudMinions = new ArrayList<>(2);
   private int wingRotationO;
   private int wingRotation;
   private int invisibilityAttackCooldown;
   private boolean attackedWithInvisibility;
   private boolean invisibilityEnabled = true;
   private boolean wearingInvisibilityCloak;
   private static final int FLIGHT_TIMER_MAX = 52;
   private static final float FLIGHT_MODIFIER_MAX = 15.0F;
   private int flightTimer;
   private float flightModifier = 1.0F;
   private double neptuneSubmergeLength;
   private double phoenixSubmergeLength;
   private static final int OBSIDIAN_TIMER_MAX = 20;
   private int obsidianConversionTime;
   private float savedHealth = 0.0F;
   private int lifeShards;
   private static final ResourceLocation LOGOMARKS = ResourceLocation.fromNamespaceAndPath("aether", "logomarks");
   private static final Style DISCORD = Style.EMPTY
      .withColor(5793266)
      .withUnderlined(true)
      .withClickEvent(new ClickEvent(Action.OPEN_URL, "https://discord.gg/aethermod"))
      .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal("https://discord.gg/aethermod")));
   private static final Style PATREON = Style.EMPTY
      .withColor(16728653)
      .withUnderlined(true)
      .withClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/TheAetherTeam"))
      .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal("https://www.patreon.com/TheAetherTeam")));
   private boolean canShowPatreonMessage = true;
   private int loginsUntilPatreonMessage = -1;
   private final Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> synchableFunctions = Map.ofEntries(
      Map.entry("setHitting", Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setHitting((Boolean)object), this::isHitting)),
      Map.entry("setMoving", Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setMoving((Boolean)object), this::isMoving)),
      Map.entry("setJumping", Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setJumping((Boolean)object), this::isJumping)),
      Map.entry(
         "setGravititeJumpActive",
         Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setGravititeJumpActive((Boolean)object), this::isGravititeJumpActive)
      ),
      Map.entry("setGoldenDartCount", Triple.of(Type.INT, (Consumer<Object>)object -> this.setGoldenDartCount((Integer)object), this::getGoldenDartCount)),
      Map.entry("setPoisonDartCount", Triple.of(Type.INT, (Consumer<Object>)object -> this.setPoisonDartCount((Integer)object), this::getPoisonDartCount)),
      Map.entry(
         "setEnchantedDartCount", Triple.of(Type.INT, (Consumer<Object>)object -> this.setEnchantedDartCount((Integer)object), this::getEnchantedDartCount)
      ),
      Map.entry(
         "setRemedyStartDuration", Triple.of(Type.INT, (Consumer<Object>)object -> this.setRemedyStartDuration((Integer)object), this::getRemedyStartDuration)
      ),
      Map.entry(
         "setAttackedWithInvisibility",
         Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setAttackedWithInvisibility((Boolean)object), this::attackedWithInvisibility)
      ),
      Map.entry(
         "setInvisibilityEnabled",
         Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setInvisibilityEnabled((Boolean)object), this::isInvisibilityEnabled)
      ),
      Map.entry(
         "setWearingInvisibilityCloak",
         Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setWearingInvisibilityCloak((Boolean)object), this::isWearingInvisibilityCloak)
      ),
      Map.entry("setLifeShardCount", Triple.of(Type.INT, (Consumer<Object>)object -> this.setLifeShardCount((Integer)object), this::getLifeShardCount)),
      Map.entry("setLastRiddenMoa", Triple.of(Type.UUID, (Consumer<Object>)object -> this.setLastRiddenMoa((UUID)object), this::getLastRiddenMoa)),
      Map.entry(
         "setShouldSyncBetweenClients",
         Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setShouldSyncBetweenClients((Boolean)object), this::shouldSyncBetweenClients)
      )
   );
   private boolean shouldSyncAfterJoin;
   private boolean shouldSyncBetweenClients;
   public static final Codec<AetherPlayerAttachment> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.BOOL.fieldOf("can_get_portal").forGetter(AetherPlayerAttachment::canGetPortal),
            Codec.BOOL.fieldOf("can_spawn_in_aether").forGetter(AetherPlayerAttachment::canSpawnInAether),
            Codec.FLOAT.fieldOf("saved_health").forGetter(AetherPlayerAttachment::getSavedHealth),
            Codec.INT.fieldOf("life_shard_count").forGetter(AetherPlayerAttachment::getLifeShardCount),
            Codec.BOOL.fieldOf("seen_sun_spirit").forGetter(AetherPlayerAttachment::hasSeenSunSpiritDialogue),
            Codec.INT.fieldOf("remedy_start_duration").forGetter(AetherPlayerAttachment::getRemedyStartDuration),
            CompoundTag.CODEC.optionalFieldOf("mounted_aerbunny").forGetter(AetherPlayerAttachment::getMountedAerbunnyTag),
            UUIDUtil.CODEC.optionalFieldOf("last_ridden_moa").forGetter(o -> o.lastRiddenMoa),
            Codec.BOOL.fieldOf("show_patreon_message").forGetter(o -> o.canShowPatreonMessage),
            Codec.INT.fieldOf("logins_until_patreon_message").forGetter(o -> o.loginsUntilPatreonMessage)
         )
         .apply(instance, AetherPlayerAttachment::new)
   );

   public AetherPlayerAttachment() {
   }

   public AetherPlayerAttachment(
      boolean portal,
      boolean spawnInAether,
      float savedHealth,
      int lifeShards,
      boolean seenSunSpirit,
      int remedyDuration,
      Optional<CompoundTag> mountedAerbunnyTag,
      Optional<UUID> lastRiddenMoa,
      boolean showPatreonMessage,
      int loginUntilMessage
   ) {
      this.setCanGetPortal(portal);
      this.setCanSpawnInAether(spawnInAether);
      this.setSavedHealth(savedHealth);
      this.setLifeShardCount(lifeShards);
      this.setSeenSunSpiritDialogue(seenSunSpirit);
      this.setRemedyStartDuration(remedyDuration);
      this.setMountedAerbunnyTag(mountedAerbunnyTag);
      this.setLastRiddenMoa(lastRiddenMoa.orElse(null));
      this.canShowPatreonMessage = showPatreonMessage;
      this.loginsUntilPatreonMessage = loginUntilMessage;
   }

   public Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> getSynchableFunctions() {
      return this.synchableFunctions;
   }

   public void onLogout(Player player) {
      this.handleLogoutSavedHealth(player);
   }

   public void onLogin(Player player) {
      this.handleGivePortal(player);
      this.remountAerbunny(player);
      this.handlePatreonMessage(player);
      this.shouldSyncAfterJoin = true;
      ServerPerkData.MOA_SKIN_INSTANCE.syncFromServer(player);
      ServerPerkData.HALO_INSTANCE.syncFromServer(player);
      ServerPerkData.DEVELOPER_GLOW_INSTANCE.syncFromServer(player);
   }

   public void onJoinLevel(Player player) {
      if (player.level().isClientSide() && player.isLocalPlayer()) {
         CustomizationsOptions.INSTANCE.load();
         this.setSynched(player.getId(), Direction.SERVER, "setShouldSyncBetweenClients", true);
      }
   }

   public void handleRespawn(boolean wasDeath) {
      if (wasDeath) {
         this.setRemedyStartDuration(0);
      }

      this.shouldSyncAfterJoin = true;
   }

   public void onUpdate(Player player) {
      this.syncAfterJoin(player);
      this.syncClients(player);
      this.handleAetherPortal(player);
      this.activateParachute(player);
      this.handleRemoveDarts(player);
      this.removeRemedyDuration(player);
      this.tickDownProjectileImpact(player);
      this.handleWingRotation(player);
      this.handleAttackCooldown(player);
      this.handleVampireHealing(player);
      this.checkToRemoveAerbunny(player);
      this.checkToRemoveCloudMinions();
      this.handleSavedHealth(player);
      this.handleLifeShardModifier(player);
      ClientMoaSkinPerkData.INSTANCE.syncFromClient(player);
      ClientHaloPerkData.INSTANCE.syncFromClient(player);
      ClientDeveloperGlowPerkData.INSTANCE.syncFromClient(player);
   }

   private void syncAfterJoin(Player player) {
      if (this.shouldSyncAfterJoin) {
         this.forceSync(player.getId(), Direction.CLIENT);
         this.shouldSyncAfterJoin = false;
      }
   }

   private void syncClients(Player player) {
      if (this.shouldSyncBetweenClients()) {
         if (!player.level().isClientSide()) {
            MinecraftServer server = player.level().getServer();
            if (server != null) {
               PlayerList playerList = server.getPlayerList();

               for (ServerPlayer serverPlayer : playerList.getPlayers()) {
                  if (!serverPlayer.getUUID().equals(player.getUUID())) {
                     ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).forceSync(player.getId(), Direction.CLIENT);
                  }
               }
            }
         }

         this.setShouldSyncBetweenClients(false);
      }
   }

   private void handleAetherPortal(Player player) {
      if (player.level().isClientSide()) {
         PortalClientUtil.handleAetherPortal(player, this);
      }
   }

   public void setCanSpawnInAether(boolean canSpawnInAether) {
      this.canSpawnInAether = canSpawnInAether;
   }

   public boolean canSpawnInAether() {
      return this.canSpawnInAether;
   }

   public float getPortalIntensity() {
      return this.portalIntensity;
   }

   public float getOldPortalIntensity() {
      return this.oPortalIntensity;
   }

   private void handleGivePortal(Player player) {
      if ((Boolean)AetherConfig.COMMON.start_with_portal.get()) {
         this.givePortalItem(player);
      } else {
         this.setCanGetPortal(false);
      }
   }

   private void activateParachute(Player player) {
      Inventory inventory = player.getInventory();
      Level level = player.level();
      if (!player.isCreative()
         && !player.isShiftKeyDown()
         && !player.isFallFlying()
         && !player.isPassenger()
         && player.getDeltaMovement().y() < -1.5
         && inventory.contains(AetherTags.Items.DEPLOYABLE_PARACHUTES)) {
         for (ItemStack stack : inventory.items) {
            if (stack.getItem() instanceof ParachuteItem parachuteItem) {
               Parachute parachute = (Parachute)parachuteItem.getParachuteEntity().get().create(level);
               if (parachute != null) {
                  parachute.setPos(player.getX(), player.getY() - 1.0, player.getZ());
                  parachute.setDeltaMovement(player.getDeltaMovement());
                  if (!level.isClientSide()) {
                     level.addFreshEntity(parachute);
                     player.startRiding(parachute);
                     stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                  }

                  parachute.spawnExplosionParticle();
                  break;
               }
            }
         }
      }
   }

   private void handleRemoveDarts(Player player) {
      if (!player.level().isClientSide()) {
         if (this.getGoldenDartCount() > 0) {
            if (this.removeGoldenDartTime <= 0) {
               this.removeGoldenDartTime = 20 * (30 - this.getGoldenDartCount());
            }

            this.removeGoldenDartTime--;
            if (this.removeGoldenDartTime <= 0) {
               this.setSynched(player.getId(), Direction.CLIENT, "setGoldenDartCount", this.getGoldenDartCount() - 1);
            }
         }

         if (this.getPoisonDartCount() > 0) {
            if (this.removePoisonDartTime <= 0) {
               this.removePoisonDartTime = 20 * (30 - this.getPoisonDartCount());
            }

            this.removePoisonDartTime--;
            if (this.removePoisonDartTime <= 0) {
               this.setSynched(player.getId(), Direction.CLIENT, "setPoisonDartCount", this.getPoisonDartCount() - 1);
            }
         }

         if (this.getEnchantedDartCount() > 0) {
            if (this.removeEnchantedDartTime <= 0) {
               this.removeEnchantedDartTime = 20 * (30 - this.getEnchantedDartCount());
            }

            this.removeEnchantedDartTime--;
            if (this.removeEnchantedDartTime <= 0) {
               this.setSynched(player.getId(), Direction.CLIENT, "setEnchantedDartCount", this.getEnchantedDartCount() - 1);
            }
         }
      }
   }

   private void removeRemedyDuration(Player player) {
      if (this.remedyStartDuration > 0 && !player.hasEffect(AetherEffects.REMEDY)) {
         this.remedyStartDuration = 0;
      }
   }

   private void tickDownProjectileImpact(Player player) {
      if (player.level().isClientSide()) {
         if (this.getProjectileImpactedTimer() > 0) {
            this.setProjectileImpactedTimer(this.getProjectileImpactedTimer() - 1);
         } else {
            this.setProjectileImpactedMaximum(0);
            this.setProjectileImpactedTimer(0);
         }
      }
   }

   private void handleWingRotation(Player player) {
      if (player.level().isClientSide()) {
         this.wingRotationO = this.getWingRotation();
         if (EquipmentUtil.hasFullValkyrieSet(player)) {
            this.wingRotation = player.tickCount;
         } else {
            this.wingRotation = 0;
         }
      }
   }

   private void handleAttackCooldown(Player player) {
      if (!player.level().isClientSide()) {
         if (this.attackedWithInvisibility()) {
            this.invisibilityAttackCooldown--;
            if (this.invisibilityAttackCooldown <= 0) {
               this.setSynched(player.getId(), Direction.CLIENT, "setAttackedWithInvisibility", false);
            }
         } else {
            this.invisibilityAttackCooldown = (Integer)AetherConfig.SERVER.invisibility_visibility_time.get();
         }
      }
   }

   private void handleVampireHealing(Player player) {
      if (!player.level().isClientSide() && this.performVampireHealing()) {
         player.heal(1.0F);
         this.setVampireHealing(false);
      }
   }

   private void checkToRemoveAerbunny(Player player) {
      if (this.getMountedAerbunny() != null && (!this.getMountedAerbunny().isAlive() || !player.isAlive())) {
         this.setMountedAerbunny(null);
      }
   }

   public void removeAerbunny() {
      if (this.getMountedAerbunny() != null) {
         Aerbunny aerbunny = this.getMountedAerbunny();
         CompoundTag nbt = new CompoundTag();
         aerbunny.saveAsPassenger(nbt);
         this.setMountedAerbunnyTag(Optional.of(nbt));
         aerbunny.stopRiding();
         aerbunny.setRemoved(RemovalReason.UNLOADED_WITH_PLAYER);
      }
   }

   public void remountAerbunny(Player player) {
      if (this.getMountedAerbunnyTag().isPresent()) {
         if (!player.level().isClientSide()) {
            Aerbunny aerbunny = new Aerbunny((EntityType<? extends Aerbunny>)AetherEntityTypes.AERBUNNY.get(), player.level());
            aerbunny.load(this.getMountedAerbunnyTag().get());
            player.level().addFreshEntity(aerbunny);
            aerbunny.startRiding(player);
            this.setMountedAerbunny(aerbunny);
            if (player instanceof ServerPlayer serverPlayer) {
               PacketDistributor.sendToPlayer(serverPlayer, new RemountAerbunnyPacket(player.getId(), aerbunny.getId()), new CustomPacketPayload[0]);
            }
         }

         this.setMountedAerbunnyTag(Optional.empty());
      }
   }

   private void checkToRemoveCloudMinions() {
      this.getCloudMinions().removeIf(cloudMinion -> !cloudMinion.isAlive());
   }

   private void handleSavedHealth(Player player) {
      if (this.getSavedHealth() > 0.0F) {
         AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
         if (health != null && health.hasModifier(this.getLifeShardHealthAttributeModifier().id())) {
            player.setHealth(Math.min(this.getSavedHealth(), player.getMaxHealth()));
            this.setSavedHealth(0.0F);
         }
      }
   }

   private void handleLifeShardModifier(Player player) {
      if (!player.level().isClientSide()) {
         AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
         AttributeModifier lifeShardHealth = this.getLifeShardHealthAttributeModifier();
         if (health != null) {
            if (health.hasModifier(lifeShardHealth.id())) {
               health.removeModifier(lifeShardHealth.id());
            }

            health.addTransientModifier(lifeShardHealth);
         }
      }
   }

   private void handleLogoutSavedHealth(Player player) {
      AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
      if (health != null && health.hasModifier(this.getLifeShardHealthAttributeModifier().id())) {
         this.setSavedHealth(player.getHealth());
      }
   }

   private void handlePatreonMessage(Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         if ((Boolean)AetherConfig.COMMON.show_patreon_message.get() && this.canShowPatreonMessage) {
            if (this.loginsUntilPatreonMessage < 0
               && serverPlayer.level().dimension() == AetherDimensions.AETHER_LEVEL
               && (
                  serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)AetherEntityTypes.SLIDER.get())) > 0
                     || serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)AetherEntityTypes.VALKYRIE_QUEEN.get())) > 0
                     || serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)AetherEntityTypes.SUN_SPIRIT.get())) > 0
               )) {
               this.loginsUntilPatreonMessage = serverPlayer.getRandom().nextInt(2);
            }

            if (this.loginsUntilPatreonMessage == 0) {
               this.sendPatreonMessage(serverPlayer);
               this.canShowPatreonMessage = false;
               AetherConfig.COMMON.show_patreon_message.set(false);
               AetherConfig.COMMON.show_patreon_message.save();
            } else if (this.loginsUntilPatreonMessage > 0) {
               this.loginsUntilPatreonMessage--;
            }
         } else if (!(Boolean)AetherConfig.COMMON.show_patreon_message.get()) {
            this.canShowPatreonMessage = false;
         }
      }
   }

   private void sendPatreonMessage(ServerPlayer serverPlayer) {
      Component component = Component.translatable("gui.aether.patreon.message");
      List<String> unlinkedBodyArray = Arrays.stream(component.getString().split("(?=(%s1))|(?<=(%s1))|(?=(%s2))|(?<=(%s2))|(?=(%s3))|(?<=(%s3))")).toList();
      List<MutableComponent> bodyArray = unlinkedBodyArray.stream()
         .map(
            string -> {
               return switch (string) {
                  case "%s1" -> Component.literal("The Aether").setStyle(Style.EMPTY.withColor(8445183).withItalic(true));
                  case "%s2" -> Component.literal("")
                     .append(Component.literal("! ").setStyle(DISCORD.withFont(LOGOMARKS)))
                     .append(Component.literal("Discord").setStyle(DISCORD));
                  case "%s3" -> Component.literal("")
                     .append(Component.literal(", ").setStyle(PATREON.withFont(LOGOMARKS)))
                     .append(Component.literal("Patreon").setStyle(PATREON));
                  default -> Component.literal(string);
               };
            }
         )
         .toList();
      MutableComponent message = Component.literal("");
      bodyArray.forEach(message::append);
      serverPlayer.sendSystemMessage(message);
      Component note = Component.translatable("gui.aether.patreon.note").setStyle(Style.EMPTY.withColor(7631988).withItalic(true));
      serverPlayer.sendSystemMessage(note);
   }

   public void givePortalItem(Player player) {
      if (this.canGetPortal()) {
         player.addItem(new ItemStack((ItemLike)AetherItems.AETHER_PORTAL_FRAME.get()));
         this.setCanGetPortal(false);
      }
   }

   public void setCanGetPortal(boolean canGetPortal) {
      this.canGetPortal = canGetPortal;
   }

   public boolean canGetPortal() {
      return this.canGetPortal;
   }

   public void setHitting(boolean isHitting) {
      this.isHitting = isHitting;
   }

   public boolean isHitting() {
      return this.isHitting;
   }

   public void setMoving(boolean isMoving) {
      this.isMoving = isMoving;
   }

   public boolean isMoving() {
      return this.isMoving;
   }

   public void setJumping(boolean isJumping) {
      this.isJumping = isJumping;
   }

   public boolean isJumping() {
      return this.isJumping;
   }

   public void setGravititeJumpActive(boolean isGravititeJumpActive) {
      this.isGravititeJumpActive = isGravititeJumpActive;
   }

   public boolean isGravititeJumpActive() {
      return this.isGravititeJumpActive;
   }

   public void setSeenSunSpiritDialogue(boolean seenDialogue) {
      this.seenSunSpiritDialogue = seenDialogue;
   }

   public boolean hasSeenSunSpiritDialogue() {
      return this.seenSunSpiritDialogue;
   }

   public void setGoldenDartCount(int count) {
      this.goldenDartCount = count;
   }

   public int getGoldenDartCount() {
      return this.goldenDartCount;
   }

   public void setPoisonDartCount(int count) {
      this.poisonDartCount = count;
   }

   public int getPoisonDartCount() {
      return this.poisonDartCount;
   }

   public void setEnchantedDartCount(int count) {
      this.enchantedDartCount = count;
   }

   public int getEnchantedDartCount() {
      return this.enchantedDartCount;
   }

   public void setRemedyStartDuration(int duration) {
      this.remedyStartDuration = duration;
   }

   public int getRemedyStartDuration() {
      return this.remedyStartDuration;
   }

   public void setProjectileImpactedMaximum(int projectileImpactedMaximum) {
      this.impactedMaximum = projectileImpactedMaximum;
   }

   public int getProjectileImpactedMaximum() {
      return this.impactedMaximum;
   }

   public void setProjectileImpactedTimer(int projectileImpactedTimer) {
      this.impactedTimer = projectileImpactedTimer;
   }

   public int getProjectileImpactedTimer() {
      return this.impactedTimer;
   }

   public void setVampireHealing(boolean performVampireHealing) {
      this.performVampireHealing = performVampireHealing;
   }

   public boolean performVampireHealing() {
      return this.performVampireHealing;
   }

   public void setMountedAerbunny(@Nullable Aerbunny mountedAerbunny) {
      this.mountedAerbunny = mountedAerbunny;
   }

   @Nullable
   public Aerbunny getMountedAerbunny() {
      return this.mountedAerbunny;
   }

   public void setMountedAerbunnyTag(Optional<CompoundTag> mountedAerbunnyTag) {
      this.mountedAerbunnyTag = mountedAerbunnyTag;
   }

   public Optional<CompoundTag> getMountedAerbunnyTag() {
      return this.mountedAerbunnyTag;
   }

   public void setLastRiddenMoa(@Nullable UUID lastRiddenMoa) {
      this.lastRiddenMoa = Optional.ofNullable(lastRiddenMoa);
   }

   @Nullable
   public UUID getLastRiddenMoa() {
      return this.lastRiddenMoa.orElse(null);
   }

   public void setCloudMinions(Player player, CloudMinion cloudMinionRight, CloudMinion cloudMinionLeft) {
      this.sendCloudMinionPacket(player, cloudMinionRight, cloudMinionLeft);
      this.cloudMinions.add(0, cloudMinionRight);
      this.cloudMinions.add(1, cloudMinionLeft);
   }

   public List<CloudMinion> getCloudMinions() {
      return this.cloudMinions;
   }

   public int getWingRotationO() {
      return this.wingRotationO;
   }

   public int getWingRotation() {
      return this.wingRotation;
   }

   public void setAttackedWithInvisibility(boolean attacked) {
      this.attackedWithInvisibility = attacked;
   }

   public boolean attackedWithInvisibility() {
      return this.attackedWithInvisibility;
   }

   public void setInvisibilityEnabled(boolean enabled) {
      this.invisibilityEnabled = enabled;
   }

   public boolean isInvisibilityEnabled() {
      return this.invisibilityEnabled;
   }

   public void setWearingInvisibilityCloak(boolean wearing) {
      this.wearingInvisibilityCloak = wearing;
   }

   public boolean isWearingInvisibilityCloak() {
      return this.wearingInvisibilityCloak;
   }

   public int getFlightTimerMax() {
      return 52;
   }

   public float getFlightModifierMax() {
      return 15.0F;
   }

   public void setFlightTimer(int timer) {
      this.flightTimer = timer;
   }

   public int getFlightTimer() {
      return this.flightTimer;
   }

   public void setFlightModifier(float modifier) {
      this.flightModifier = modifier;
   }

   public float getFlightModifier() {
      return this.flightModifier;
   }

   public void setSavedHealth(float health) {
      this.savedHealth = health;
   }

   public float getSavedHealth() {
      return this.savedHealth;
   }

   public void setNeptuneSubmergeLength(double length) {
      this.neptuneSubmergeLength = length;
   }

   public double getNeptuneSubmergeLength() {
      return this.neptuneSubmergeLength;
   }

   public void setPhoenixSubmergeLength(double length) {
      this.phoenixSubmergeLength = length;
   }

   public double getPhoenixSubmergeLength() {
      return this.phoenixSubmergeLength;
   }

   public int getObsidianConversionTimerMax() {
      return 20;
   }

   public void setObsidianConversionTime(int time) {
      this.obsidianConversionTime = time;
   }

   public int getObsidianConversionTime() {
      return this.obsidianConversionTime;
   }

   public void setLifeShardCount(int amount) {
      this.lifeShards = amount;
   }

   public int getLifeShardCount() {
      return this.lifeShards;
   }

   public int getLifeShardLimit() {
      return (Integer)AetherConfig.SERVER.maximum_life_shards.get();
   }

   public AttributeModifier getLifeShardHealthAttributeModifier() {
      return new AttributeModifier(LIFE_SHARD_HEALTH_ID, this.getLifeShardCount() * 2.0F, Operation.ADD_VALUE);
   }

   private void sendCloudMinionPacket(Player player, CloudMinion cloudMinionRight, CloudMinion cloudMinionLeft) {
      if (player instanceof ServerPlayer serverPlayer && !player.level().isClientSide) {
         PacketDistributor.sendToPlayer(
            serverPlayer, new CloudMinionPacket(player.getId(), cloudMinionRight.getId(), cloudMinionLeft.getId()), new CustomPacketPayload[0]
         );
      }
   }

   private boolean shouldSyncBetweenClients() {
      return this.shouldSyncBetweenClients;
   }

   private void setShouldSyncBetweenClients(boolean shouldSyncBetweenClients) {
      this.shouldSyncBetweenClients = shouldSyncBetweenClients;
   }

   public SyncPacket getSyncPacket(int entityID, String key, Type type, Object value) {
      return new AetherPlayerSyncPacket(entityID, key, type, value);
   }
}
