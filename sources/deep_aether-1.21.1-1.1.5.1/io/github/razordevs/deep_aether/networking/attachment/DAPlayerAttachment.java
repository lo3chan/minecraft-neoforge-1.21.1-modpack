package io.github.razordevs.deep_aether.networking.attachment;

import com.aetherteam.nitrogen.attachment.INBTSynchable;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Type;
import com.aetherteam.nitrogen.network.packet.SyncPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.razordevs.deep_aether.networking.packet.DAPlayerSyncPacket;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Triple;

public class DAPlayerAttachment implements INBTSynchable {
   private boolean isSliderSlamActivated;
   private boolean hasSkyjadeSet;
   private boolean isSkyjadeAbilityActivated;
   private int bladeOfLuckDamage;
   private int oldBladeOfLuckDamage = 6;
   public boolean changeBladeOfLuckState;
   public int windShieldCooldown;
   private boolean isFloatyScarfWrappedAroundNeck;
   private boolean shouldSyncBetweenClients;
   private boolean shouldSyncAfterJoin;
   private final Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> synchableFunctions = Map.ofEntries(
      Map.entry(
         "setSliderSlamActivated",
         Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setSliderSlamActivated((Boolean)object), this::isSliderSlamActivated)
      ),
      Map.entry("hasSkyjadeSet", Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setHasSkyjadeSet((Boolean)object), this::hasSkyjadeSet)),
      Map.entry(
         "setSkyjadeAbilityActivated",
         Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setSkyjadeAbilityActivated((Boolean)object), this::isSkyjadeAbilityActivated)
      ),
      Map.entry("setBladeOfLuckDamage", Triple.of(Type.INT, (Consumer<Object>)object -> this.setBladeOfLuckDamage((Integer)object), this::getBladeOfLuckDamage)),
      Map.entry(
         "setWindShieldCooldown", Triple.of(Type.INT, (Consumer<Object>)object -> this.setWindShieldCooldown((Integer)object), this::getWindShieldCooldown)
      ),
      Map.entry(
         "setFloatyScarfWrappedAroundNeck",
         Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setFloatyScarfWrappedAroundNeck((Boolean)object), this::isFloatyScarfWrappedAroundNeck)
      ),
      Map.entry(
         "setShouldSyncBetweenClients",
         Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setShouldSyncBetweenClients((Boolean)object), this::shouldSyncBetweenClients)
      )
   );
   public static final Codec<DAPlayerAttachment> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.BOOL.fieldOf("is_slider_slam_activated").forGetter(DAPlayerAttachment::isSliderSlamActivated),
            Codec.BOOL.fieldOf("has_skyjade_set").forGetter(DAPlayerAttachment::isSliderSlamActivated),
            Codec.BOOL.fieldOf("is_skyjade_ability_activated").forGetter(DAPlayerAttachment::isSkyjadeAbilityActivated),
            Codec.INT.fieldOf("get_blade_of_luck_damage").forGetter(DAPlayerAttachment::getBladeOfLuckDamage),
            Codec.INT.fieldOf("get_wind_shield_cooldown").forGetter(DAPlayerAttachment::getWindShieldCooldown),
            Codec.BOOL.fieldOf("get_wind_shield_cooldown").forGetter(DAPlayerAttachment::isFloatyScarfWrappedAroundNeck)
         )
         .apply(instance, DAPlayerAttachment::new)
   );

   private DAPlayerAttachment(
      boolean isSliderSlamActivated,
      boolean hasSkyjadeSet,
      boolean isSkyjadeAbilityActivated,
      int bladeOfLuckDamage,
      int windShieldCooldown,
      boolean isFloatyScarfWrappedAroundNeck
   ) {
      this.setSliderSlamActivated(isSliderSlamActivated);
      this.setHasSkyjadeSet(hasSkyjadeSet);
      this.setSkyjadeAbilityActivated(isSkyjadeAbilityActivated);
      this.setBladeOfLuckDamage(bladeOfLuckDamage);
      this.setWindShieldCooldown(windShieldCooldown);
      this.setFloatyScarfWrappedAroundNeck(isFloatyScarfWrappedAroundNeck);
   }

   public DAPlayerAttachment() {
      this(false, false, false, 0, 0, false);
   }

   public boolean isSliderSlamActivated() {
      return this.isSliderSlamActivated;
   }

   public void setSliderSlamActivated(boolean var1) {
      this.isSliderSlamActivated = var1;
   }

   public boolean hasSkyjadeSet() {
      return this.hasSkyjadeSet;
   }

   public void setHasSkyjadeSet(boolean var1) {
      this.hasSkyjadeSet = var1;
   }

   public boolean isSkyjadeAbilityActivated() {
      return this.isSkyjadeAbilityActivated;
   }

   public void setSkyjadeAbilityActivated(boolean var1) {
      this.isSkyjadeAbilityActivated = var1;
   }

   public int getBladeOfLuckDamage() {
      return this.bladeOfLuckDamage;
   }

   public int getOldBladeOfLuckDamage() {
      return this.oldBladeOfLuckDamage;
   }

   public void setBladeOfLuckDamage(int bladeOfLuckDamage) {
      this.oldBladeOfLuckDamage = this.getBladeOfLuckDamage();
      this.changeBladeOfLuckState = true;
      this.bladeOfLuckDamage = bladeOfLuckDamage;
   }

   public void setWindShieldCooldown(int windShieldCooldown) {
      this.windShieldCooldown = windShieldCooldown;
   }

   public int getWindShieldCooldown() {
      return this.windShieldCooldown;
   }

   public boolean isFloatyScarfWrappedAroundNeck() {
      return this.isFloatyScarfWrappedAroundNeck;
   }

   public void setFloatyScarfWrappedAroundNeck(boolean bool) {
      this.isFloatyScarfWrappedAroundNeck = bool;
   }

   private boolean shouldSyncBetweenClients() {
      return this.shouldSyncBetweenClients;
   }

   private void setShouldSyncBetweenClients(boolean shouldSyncBetweenClients) {
      this.shouldSyncBetweenClients = shouldSyncBetweenClients;
   }

   public void onLogin(Player player) {
      this.shouldSyncAfterJoin = true;
   }

   public void onJoinLevel(Player player) {
      if (player.level().isClientSide()) {
         this.setSynched(player.getId(), Direction.SERVER, "setShouldSyncBetweenClients", true);
      }
   }

   public void onUpdate(Player player) {
      this.syncAfterJoin(player);
      this.syncClients(player);
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
                     ((DAPlayerAttachment)player.getData(DAAttachments.PLAYER)).forceSync(player.getId(), Direction.CLIENT);
                  }
               }
            }
         }

         this.setShouldSyncBetweenClients(false);
      }
   }

   public Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> getSynchableFunctions() {
      return this.synchableFunctions;
   }

   public SyncPacket getSyncPacket(int entityID, String key, Type type, Object value) {
      return new DAPlayerSyncPacket(entityID, key, type, value);
   }
}
