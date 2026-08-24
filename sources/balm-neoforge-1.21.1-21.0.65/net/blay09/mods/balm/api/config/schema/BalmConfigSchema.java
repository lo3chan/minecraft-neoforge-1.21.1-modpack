package net.blay09.mods.balm.api.config.schema;

import java.util.Collection;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategory;
import net.blay09.mods.balm.api.config.schema.impl.ConfigSchemaImpl;
import net.minecraft.resources.ResourceLocation;

public interface BalmConfigSchema {
   static ConfigSchemaImpl create(ResourceLocation identifier) {
      return new ConfigSchemaImpl(identifier);
   }

   ResourceLocation identifier();

   LoadedConfig defaults();

   Collection<ConfiguredProperty<?>> rootProperties();

   Collection<ConfigCategory> categories();

   ConfiguredProperty<?> findProperty(String var1, String var2);

   ConfiguredProperty<?> findRootProperty(String var1);
}
