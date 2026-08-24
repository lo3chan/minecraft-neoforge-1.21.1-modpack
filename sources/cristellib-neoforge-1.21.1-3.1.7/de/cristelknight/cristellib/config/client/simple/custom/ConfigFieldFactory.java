package de.cristelknight.cristellib.config.client.simple.custom;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;

@FunctionalInterface
public interface ConfigFieldFactory<E extends AbstractFieldBuilder<?, ?, ?>> {
   E create(ConfigEntryBuilder var1, String var2, Object var3, Object var4);
}
