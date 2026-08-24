package com.mcwwindows.kikoz.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Key extends Hammer {
   public Key(Properties properties) {
      super(properties);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public MutableComponent getDescription() {
      return Component.translatable("mcwwindows.key.desc");
   }
}
