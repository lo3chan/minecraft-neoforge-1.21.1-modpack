package com.nyfaria.nyfsspiders.registration.specialised;

import com.nyfaria.nyfsspiders.registration.RegistrationProvider;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public interface BlockRegistrationProvider extends RegistrationProvider<Block> {
   static BlockRegistrationProvider get(String modId) {
      return RegistrationProvider.Factory.INSTANCE.block(modId);
   }

   <B extends Block> BlockRegistryObject<B> register(String var1, Supplier<? extends B> var2);

   default <B extends Block> BlockRegistryObject<B> register(String name, Properties properties) {
      return this.register(name, properties);
   }

   default <B extends Block> BlockRegistryObject<B> register(String name, Properties properties, Function<Properties, ? extends B> func) {
      return this.register(name, (Supplier<? extends B>)(() -> func.apply(properties)));
   }
}
