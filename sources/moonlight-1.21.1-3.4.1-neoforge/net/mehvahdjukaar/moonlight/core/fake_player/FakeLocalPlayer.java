package net.mehvahdjukaar.moonlight.core.fake_player;

import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FakeLocalPlayer extends AbstractClientPlayer {
   private static final Map<ClientLevel, Map<GameProfile, FakeLocalPlayer>> FAKE_PLAYERS = new MapMaker().weakKeys().makeMap();
   private final EntityDimensions dimensions = EntityDimensions.fixed(0.0F, 0.0F);

   static FakeLocalPlayer get(ClientLevel level, GameProfile username) {
      return FAKE_PLAYERS.computeIfAbsent(level, l -> new MapMaker().weakValues().makeMap())
         .computeIfAbsent(username, u -> new FakeLocalPlayer(level, username));
   }

   static void unloadLevel(LevelAccessor level) {
      FAKE_PLAYERS.keySet().removeIf(l -> l == level);
   }

   public FakeLocalPlayer(ClientLevel pClientLevel, GameProfile pGameProfile) {
      super(pClientLevel, pGameProfile);
      this.noPhysics = true;
   }

   public void playSound(SoundEvent pSound, float pVolume, float pPitch) {
   }

   @Nullable
   public MinecraftServer getServer() {
      return PlatHelper.getCurrentServer();
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return this.dimensions;
   }

   public void tick() {
   }

   public Vec3 position() {
      return new Vec3(this.getX(), this.getY(), this.getZ());
   }

   public BlockPos blockPosition() {
      return new BlockPos((int)this.getX(), (int)this.getY(), (int)this.getZ());
   }

   public void setXRot(float pXRot) {
      super.setXRot(pXRot);
      this.xRotO = pXRot;
   }

   public void setYRot(float pYRot) {
      super.setYRot(pYRot);
      this.yRotO = pYRot;
   }
}
