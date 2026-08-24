package net.blay09.mods.balm.api.config.schema.builder;

import java.util.function.Function;
import net.blay09.mods.balm.api.config.schema.impl.ConfigCategoryImpl;

public interface ConfigCategoryBuilder extends PropertyHolderBuilder {
   ConfigCategoryImpl comment(String var1);

   <T> T via(Function<ConfigCategoryBuilder, T> var1);
}
