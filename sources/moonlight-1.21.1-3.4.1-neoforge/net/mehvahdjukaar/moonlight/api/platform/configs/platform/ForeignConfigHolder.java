package net.mehvahdjukaar.moonlight.api.platform.configs.platform;

import java.io.InputStream;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigCategory;
import net.mehvahdjukaar.moonlight.core.client.config.MoonlightConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

public class ForeignConfigHolder extends ModConfigHolder {
   private final ModConfigSpec spec;
   private final ConfigCategory root;
   private final Component readableName;

   ForeignConfigHolder(ResourceLocation id, ConfigType type, ModConfigSpec spec, ConfigCategory root, Component readableName) {
      super(id, "toml", FMLPaths.CONFIGDIR.get(), type, null, false);
      this.spec = spec;
      this.root = root;
      this.readableName = readableName;
   }

   @Override
   public Component getReadableName() {
      return this.readableName;
   }

   @Override
   public void forceLoad() {
   }

   @Override
   protected void saveToDisk() {
      this.spec.save();
   }

   @Override
   public ConfigCategory getConfigRoot() {
      return this.root;
   }

   @Override
   public void loadFromBytes(InputStream stream, boolean readOnly) {
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   @Override
   public Screen makeScreen(Screen parent, @Nullable ResourceLocation background) {
      return new MoonlightConfigScreen(this, this.root, parent, background);
   }
}
