package com.seibel.distanthorizons.common.wrappers.gui;

import com.seibel.distanthorizons.core.wrapperInterfaces.config.ILangWrapper;
import net.minecraft.client.resources.language.I18n;

public class LangWrapper_neoforge implements ILangWrapper {
   public static final LangWrapper_neoforge INSTANCE = new LangWrapper_neoforge();

   @Override
   public boolean langExists(String str) {
      return I18n.exists(str);
   }

   @Override
   public String getLang(String str) {
      return I18n.get(str, new Object[0]);
   }
}
