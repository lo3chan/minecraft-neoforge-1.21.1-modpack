package com.seibel.distanthorizons.common.wrappers.gui;

import com.seibel.distanthorizons.core.wrapperInterfaces.config.ILangWrapper;
import net.minecraft.class_1074;

public class LangWrapper_fabric implements ILangWrapper {
   public static final LangWrapper_fabric INSTANCE = new LangWrapper_fabric();

   @Override
   public boolean langExists(String str) {
      return class_1074.method_4663(str);
   }

   @Override
   public String getLang(String str) {
      return class_1074.method_4662(str, new Object[0]);
   }
}
