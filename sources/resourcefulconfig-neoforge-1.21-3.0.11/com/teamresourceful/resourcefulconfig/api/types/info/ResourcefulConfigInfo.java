package com.teamresourceful.resourcefulconfig.api.types.info;

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue;

public interface ResourcefulConfigInfo {
   TranslatableValue title();

   TranslatableValue description();

   String icon();

   ResourcefulConfigColor color();

   ResourcefulConfigLink[] links();

   default ResourcefulConfigInfoButton[] buttons() {
      return new ResourcefulConfigInfoButton[0];
   }

   boolean isHidden();
}
