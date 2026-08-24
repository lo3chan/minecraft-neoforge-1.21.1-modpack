package net.mehvahdjukaar.moonlight.api.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.mehvahdjukaar.moonlight.api.resources.assets.LangBuilder;
import org.jetbrains.annotations.Nullable;

public class AfterLanguageLoadEvent implements SimpleEvent {
   private final Map<String, String> languageLines;
   private final List<String> languageInfo;
   private final Map<String, String> extraLanguageLines = new HashMap<>();

   public AfterLanguageLoadEvent(Map<String, String> lines, List<String> info) {
      this.languageInfo = info;
      this.languageLines = lines;
   }

   public Collection<String> getLanguageInfo() {
      return new ArrayList<>(this.languageInfo);
   }

   public Collection<String> getAllEntries() {
      return this.languageLines.values();
   }

   @Nullable
   public String getEntry(String key) {
      return this.languageLines.get(key);
   }

   public void addEntry(String key, String translation) {
      this.languageLines.computeIfAbsent(key, k -> {
         this.extraLanguageLines.put(key, translation);
         return translation;
      });
   }

   public void addEntries(LangBuilder builder) {
      builder.entries().forEach(this::addEntry);
   }

   public Map<String, String> getExtraLanguageLines() {
      return this.extraLanguageLines;
   }

   public boolean isDefault() {
      return this.languageInfo.stream().anyMatch(l -> l.equals("en_us"));
   }
}
