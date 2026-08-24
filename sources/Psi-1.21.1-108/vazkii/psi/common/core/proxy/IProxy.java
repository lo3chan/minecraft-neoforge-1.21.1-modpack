package vazkii.psi.common.core.proxy;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import vazkii.psi.common.block.tile.TileProgrammer;

public interface IProxy {
   default void registerHandlers(IEventBus bus) {
   }

   Player getClientPlayer();

   default Level getClientWorld() {
      return null;
   }

   default void addParticleForce(Level world, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
   }

   default boolean hasAdvancement(ResourceLocation advancement, Player playerEntity) {
      return false;
   }

   default int getColorForCAD(ItemStack cadStack) {
      return -1;
   }

   default int getColorForColorizer(ItemStack colorizer) {
      return -1;
   }

   void sparkleFX(
      Level var1, double var2, double var4, double var6, float var8, float var9, float var10, float var11, float var12, float var13, float var14, int var15
   );

   default void sparkleFX(double x, double y, double z, float r, float g, float b, float gravity, float size, int m) {
      this.sparkleFX(x, y, z, r, g, b, 0.0F, -gravity, 0.0F, size, m);
   }

   void sparkleFX(double var1, double var3, double var5, float var7, float var8, float var9, float var10, float var11, float var12, float var13, int var14);

   void wispFX(
      Level var1, double var2, double var4, double var6, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15
   );

   default void wispFX(double x, double y, double z, float r, float g, float b, float size, float gravity) {
      this.wispFX(x, y, z, r, g, b, size, gravity, 1.0F);
   }

   default void wispFX(double x, double y, double z, float r, float g, float b, float size, float gravity, float maxAgeMul) {
      this.wispFX(x, y, z, r, g, b, size, 0.0F, -gravity, 0.0F, maxAgeMul);
   }

   void wispFX(double var1, double var3, double var5, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14);

   void openProgrammerGUI(TileProgrammer var1);

   void openFlashRingGUI(ItemStack var1);
}
