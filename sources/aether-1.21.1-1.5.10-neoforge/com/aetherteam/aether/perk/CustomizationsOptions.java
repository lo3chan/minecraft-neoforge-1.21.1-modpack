package com.aetherteam.aether.perk;

import com.aetherteam.aether.Aether;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Map.Entry;

public class CustomizationsOptions {
   private static final File CUSTOMIZATIONS_FILE = new File(Aether.DIRECTORY.toString(), "aether_customizations.txt");
   private final LinkedHashMap<String, Object> customizations = new LinkedHashMap<>();
   public static final CustomizationsOptions INSTANCE = new CustomizationsOptions();

   public CustomizationsOptions() {
      if (CUSTOMIZATIONS_FILE.exists()) {
         this.load();
      } else {
         this.customizations.put("haloEnabled", true);
         this.customizations.put("haloColor", "");
         this.customizations.put("developerGlowEnabled", false);
         this.customizations.put("developerGlowColor", "");
         this.customizations.put("moaSkin", "");
         this.save();
      }
   }

   public void load() {
      try {
         Scanner reader = new Scanner(CUSTOMIZATIONS_FILE);

         while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] split = line.split(":");
            if (split.length > 1) {
               String key = split[0];
               String value = split[1];
               if (Boolean.parseBoolean(value)) {
                  this.set(key, Boolean.valueOf(value));
               } else {
                  this.set(key, value);
               }
            }
         }
      } catch (IOException var6) {
         Aether.LOGGER.warn("Failed to load Aether perk customizations: ", var6);
      }
   }

   public void save() {
      try {
         FileWriter writer = new FileWriter(CUSTOMIZATIONS_FILE);

         for (Entry<String, Object> entry : this.customizations.entrySet()) {
            writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
         }

         writer.flush();
         writer.close();
      } catch (IOException var4) {
         Aether.LOGGER.warn("Failed to save Aether perk customizations: ", var4);
      }
   }

   public boolean isHaloEnabled() {
      return this.get("haloEnabled") instanceof Boolean bool ? bool : false;
   }

   public void setIsHaloEnabled(boolean value) {
      this.set("haloEnabled", value);
   }

   public String getHaloHex() {
      return this.get("haloColor") instanceof String string ? string : "";
   }

   public void setHaloColor(String value) {
      this.set("haloColor", value);
   }

   public boolean isDeveloperGlowEnabled() {
      return this.get("developerGlowEnabled") instanceof Boolean bool ? bool : false;
   }

   public void setIsDeveloperGlowEnabled(boolean value) {
      this.set("developerGlowEnabled", value);
   }

   public String getDeveloperGlowHex() {
      return this.get("developerGlowColor") instanceof String string ? string : "";
   }

   public void setDeveloperGlowColor(String value) {
      this.set("developerGlowColor", value);
   }

   public String getMoaSkin() {
      return this.get("moaSkin") instanceof String string ? string : "";
   }

   public void setMoaSkin(String value) {
      this.set("moaSkin", value);
   }

   public Object get(String string) {
      return this.customizations.get(string);
   }

   public void set(String string, Object object) {
      if (this.customizations.containsKey(string)) {
         this.customizations.replace(string, object);
      } else {
         this.customizations.put(string, object);
      }
   }
}
