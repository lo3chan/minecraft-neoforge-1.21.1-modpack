package com.iafenvoy.jupiter.compat;

import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import java.util.Collection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface ExtraConfigHolder extends ConfigMetaProvider {
   Component getTitle();

   void save();

   @Nullable
   ResourceLocation getBackgroundTexture(boolean var1);

   Collection<? extends ConfigGroup> buildGroups();
}
