package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;

@Deprecated
@FunctionalInterface
public interface CreativeModeTabContext {
   void registerCreativeModeTab(CreativeModeTabConfigurator var1);
}
