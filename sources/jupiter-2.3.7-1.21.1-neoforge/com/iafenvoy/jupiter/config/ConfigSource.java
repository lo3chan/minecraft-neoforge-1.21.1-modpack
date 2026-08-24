package com.iafenvoy.jupiter.config;

import com.iafenvoy.jupiter.util.TextUtil;
import net.minecraft.network.chat.Component;

public record ConfigSource(Component name, int color, boolean jupiterCapability) {
   public static final ConfigSource NONE = new ConfigSource(TextUtil.empty(), -8421505, false);
   public static final ConfigSource JUPITER = new ConfigSource(TextUtil.translatable("jupiter.config_source.jupiter"), -2436684, false);
   public static final ConfigSource NIGHT_CONFIG = new ConfigSource(TextUtil.translatable("jupiter.config_source.night_config"), -23296, true);
   public static final ConfigSource CLOTH_CONFIG = new ConfigSource(TextUtil.translatable("jupiter.config_source.cloth_config"), -6632142, true);
}
