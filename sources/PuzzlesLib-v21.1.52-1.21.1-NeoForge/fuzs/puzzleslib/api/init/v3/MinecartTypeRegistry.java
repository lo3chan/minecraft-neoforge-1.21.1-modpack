package fuzs.puzzleslib.api.init.v3;

import fuzs.puzzleslib.impl.init.MinecartTypeRegistryImpl;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecart.Type;
import net.minecraft.world.level.Level;

public interface MinecartTypeRegistry {
   MinecartTypeRegistry INSTANCE = new MinecartTypeRegistryImpl();

   void register(Type var1, MinecartTypeRegistry.Factory var2);

   public interface Factory {
      AbstractMinecart create(Level var1, double var2, double var4, double var6);
   }
}
