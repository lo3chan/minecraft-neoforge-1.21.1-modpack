package net.blay09.mods.balm.world.item;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public interface BalmItemRegistrar {
   default BalmItemRegistration register(String name, Function<Properties, Item> constructor) {
      return this.register(name, constructor, Properties::new);
   }

   default BalmItemRegistration register(String name, Function<Properties, Item> constructor, Function<Properties, Properties> propertiesBuilder) {
      return this.register(name, constructor, (Supplier<Properties>)(() -> propertiesBuilder.apply(new Properties())));
   }

   default BalmItemRegistration register(String name, Function<Properties, Item> constructor, Properties properties) {
      return this.register(name, constructor, (Supplier<Properties>)(() -> properties));
   }

   BalmItemRegistration register(String var1, Function<Properties, Item> var2, Supplier<Properties> var3);

   void addAlias(ResourceLocation var1, ResourceLocation var2);

   void addAlias(String var1, String var2);

   default <T> BalmDiscriminatedItemRegistration<T> registerDiscriminated(
      T[] values, Function<T, String> nameFunction, BiFunction<T, Properties, Item> constructor, Function<Properties, Properties> propertiesSupplier
   ) {
      return this.registerDiscriminated(Set.of(values), nameFunction, constructor, propertiesSupplier);
   }

   default <T> BalmDiscriminatedItemRegistration<T> registerDiscriminated(
      T[] values, Function<T, String> nameFunction, BiFunction<T, Properties, Item> constructor, BiFunction<T, Properties, Properties> propertiesSupplier
   ) {
      return this.registerDiscriminated(Set.of(values), nameFunction, constructor, propertiesSupplier);
   }

   default <T> BalmDiscriminatedItemRegistration<T> registerDiscriminated(
      Set<T> values, Function<T, String> nameFunction, BiFunction<T, Properties, Item> constructor, Function<Properties, Properties> propertiesSupplier
   ) {
      return this.registerDiscriminated(values, nameFunction, constructor, (discriminator, properties) -> propertiesSupplier.apply(properties));
   }

   <T> BalmDiscriminatedItemRegistration<T> registerDiscriminated(
      Set<T> var1, Function<T, String> var2, BiFunction<T, Properties, Item> var3, BiFunction<T, Properties, Properties> var4
   );
}
