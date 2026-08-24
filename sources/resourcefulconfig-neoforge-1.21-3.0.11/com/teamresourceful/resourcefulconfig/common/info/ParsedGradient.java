package com.teamresourceful.resourcefulconfig.common.info;

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigColor;
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigColorGradient;

public record ParsedGradient(String first, String second, String degree) implements ResourcefulConfigColorGradient {
   public static ResourcefulConfigColor of(Class<?> clazz) {
      ConfigInfo.Gradient gradient = clazz.getAnnotation(ConfigInfo.Gradient.class);
      return gradient != null ? new ParsedGradient(gradient.first(), gradient.second(), gradient.value()) : null;
   }
}
