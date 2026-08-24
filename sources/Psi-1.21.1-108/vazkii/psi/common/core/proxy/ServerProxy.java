package vazkii.psi.common.core.proxy;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.psi.common.block.tile.TileProgrammer;

public class ServerProxy implements IProxy {
   @Override
   public Player getClientPlayer() {
      return null;
   }

   @Override
   public void sparkleFX(Level world, double x, double y, double z, float r, float g, float b, float motionX, float motionY, float motionZ, float size, int m) {
   }

   @Override
   public void sparkleFX(double x, double y, double z, float r, float g, float b, float motionX, float motionY, float motionZ, float size, int m) {
   }

   @Override
   public void wispFX(
      Level world, double x, double y, double z, float r, float g, float b, float size, float motionX, float motionY, float motionZ, float maxAgeMul
   ) {
   }

   @Override
   public void wispFX(double x, double y, double z, float r, float g, float b, float size, float motionX, float motionY, float motionZ, float maxAgeMul) {
   }

   @Override
   public void openProgrammerGUI(TileProgrammer programmer) {
   }

   @Override
   public void openFlashRingGUI(ItemStack stack) {
   }

   @Override
   public boolean hasAdvancement(ResourceLocation advancementLocation, Player playerEntity) {
      if (playerEntity instanceof ServerPlayer serverPlayer) {
         if (serverPlayer.getServer() == null) {
            return false;
         } else {
            ServerAdvancementManager advancements = serverPlayer.getServer().getAdvancements();
            AdvancementHolder advancement = advancements.get(advancementLocation);
            return advancement != null && serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
         }
      } else {
         return false;
      }
   }
}
