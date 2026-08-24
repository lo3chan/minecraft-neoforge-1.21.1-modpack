package de.cristelknight.cristellib.config.client.structure;

import de.cristelknight.cristellib.config.structure.toggle.ToggleConfig;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;

public record ClientEDConfig(Map<String, BooleanListEntry> structures) {
   public ToggleConfig toED() {
      return new ToggleConfig(this.structures.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getValue())));
   }
}
