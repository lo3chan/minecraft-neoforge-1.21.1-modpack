package com.iafenvoy.origins.data.power.component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ComponentCollector {
   private final Builder<Class<? extends PowerComponent>, PowerComponent> builder = ImmutableMap.builder();

   protected ComponentCollector() {
   }

   public static ComponentCollector create() {
      return new ComponentCollector();
   }

   public void add(PowerComponent component) {
      this.builder.put(component.getClass(), component);
   }

   public ImmutableMap<Class<? extends PowerComponent>, PowerComponent> build() {
      return this.builder.buildOrThrow();
   }
}
