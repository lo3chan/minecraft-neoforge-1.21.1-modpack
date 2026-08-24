package net.blay09.mods.balm.world.level.block;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public interface BalmBlockRegistrar {
   default BalmBlockRegistration register(String name, Function<Properties, Block> constructor, Function<Properties, Properties> propertiesBuilder) {
      return this.register(name, constructor, (Supplier<Properties>)(() -> propertiesBuilder.apply(Properties.of())));
   }

   default BalmBlockRegistration register(String name, Function<Properties, Block> constructor, Properties properties) {
      return this.register(name, constructor, (Supplier<Properties>)(() -> properties));
   }

   BalmBlockRegistration register(String var1, Function<Properties, Block> var2, Supplier<Properties> var3);

   void addAlias(ResourceLocation var1, ResourceLocation var2);

   void addAlias(String var1, String var2);

   default <T> BalmDiscriminatedBlockRegistration<T> registerDiscriminated(
      T[] values, Function<T, String> nameFunction, BiFunction<T, Properties, Block> constructor, Function<Properties, Properties> propertiesSupplier
   ) {
      return this.registerDiscriminated(Set.of(values), nameFunction, constructor, propertiesSupplier);
   }

   default <T> BalmDiscriminatedBlockRegistration<T> registerDiscriminated(
      T[] values, Function<T, String> nameFunction, BiFunction<T, Properties, Block> constructor, BiFunction<T, Properties, Properties> propertiesSupplier
   ) {
      return this.registerDiscriminated(Set.of(values), nameFunction, constructor, propertiesSupplier);
   }

   default <T> BalmDiscriminatedBlockRegistration<T> registerDiscriminated(
      Set<T> values, Function<T, String> nameFunction, BiFunction<T, Properties, Block> constructor, Function<Properties, Properties> propertiesSupplier
   ) {
      return this.registerDiscriminated(values, nameFunction, constructor, (discriminator, properties) -> propertiesSupplier.apply(properties));
   }

   <T> BalmDiscriminatedBlockRegistration<T> registerDiscriminated(
      Set<T> var1, Function<T, String> var2, BiFunction<T, Properties, Block> var3, BiFunction<T, Properties, Properties> var4
   );
}
