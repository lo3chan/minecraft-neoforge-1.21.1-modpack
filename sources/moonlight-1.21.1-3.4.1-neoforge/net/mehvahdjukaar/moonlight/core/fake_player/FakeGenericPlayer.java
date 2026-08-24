package net.mehvahdjukaar.moonlight.core.fake_player;

import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

public class FakeGenericPlayer extends Player {
   private static final Map<Level, Map<GameProfile, FakeGenericPlayer>> FAKE_PLAYERS = new MapMaker().weakKeys().makeMap();

   public static FakeGenericPlayer get(Level level, GameProfile username) {
      return FAKE_PLAYERS.computeIfAbsent(level, l -> new MapMaker().weakValues().makeMap())
         .computeIfAbsent(username, u -> new FakeGenericPlayer(level, username));
   }

   public static void unloadLevel(LevelAccessor level) {
      FAKE_PLAYERS.keySet().removeIf(l -> l == level);
   }

   public FakeGenericPlayer(Level level, GameProfile gameProfile) {
      super(level, BlockPos.ZERO, 0.0F, gameProfile);
   }

   public boolean isSpectator() {
      return false;
   }

   public boolean isCreative() {
      return false;
   }

   public void displayClientMessage(Component chatComponent, boolean actionBar) {
   }

   public void awardStat(Stat stat, int increment) {
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return true;
   }

   public boolean canHarmPlayer(Player other) {
      return false;
   }

   public void die(DamageSource damageSource) {
   }

   public void tick() {
   }

   @Nullable
   public MinecraftServer getServer() {
      return PlatHelper.getCurrentServer();
   }
}
