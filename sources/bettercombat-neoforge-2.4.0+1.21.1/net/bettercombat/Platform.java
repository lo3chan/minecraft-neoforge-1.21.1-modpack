package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import java.util.Collection;
import net.bettercombat.neoforge.PlatformImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Platform {
   public static final boolean Fabric = getPlatformType() == Platform.Type.FABRIC;
   public static final boolean Forge = getPlatformType() == Platform.Type.FORGE;
   public static final boolean NeoForge = getPlatformType() == Platform.Type.NEOFORGE;

   @ExpectPlatform
   @Transformed
   protected static Platform.Type getPlatformType() {
      return PlatformImpl.getPlatformType();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isModLoaded(String modid) {
      return PlatformImpl.isModLoaded(modid);
   }

   @ExpectPlatform
   @Transformed
   public static boolean isCastingSpell(Player player) {
      return PlatformImpl.isCastingSpell(player);
   }

   @ExpectPlatform
   @Transformed
   public static FriendlyByteBuf createByteBuffer() {
      return PlatformImpl.createByteBuffer();
   }

   @ExpectPlatform
   @Transformed
   public static Collection<ServerPlayer> tracking(ServerPlayer player) {
      return PlatformImpl.tracking(player);
   }

   @ExpectPlatform
   @Transformed
   public static Collection<ServerPlayer> around(ServerLevel world, Vec3 origin, double distance) {
      return PlatformImpl.around(world, origin, distance);
   }

   @ExpectPlatform
   @Transformed
   public static boolean networkS2C_CanSend(ServerPlayer player, ResourceLocation packetId) {
      return PlatformImpl.networkS2C_CanSend(player, packetId);
   }

   @ExpectPlatform
   @Transformed
   public static void networkS2C_Send(ServerPlayer player, CustomPacketPayload payload) {
      PlatformImpl.networkS2C_Send(player, payload);
   }

   @ExpectPlatform
   @Transformed
   public static void networkC2S_Send(CustomPacketPayload payload) {
      PlatformImpl.networkC2S_Send(payload);
   }

   public static enum Type {
      FABRIC,
      FORGE,
      NEOFORGE;
   }
}
