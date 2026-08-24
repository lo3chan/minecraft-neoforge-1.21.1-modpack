package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class SoundsGenerator {
   private final Map<String, SoundsGenerator.SoundGen> sounds = new HashMap<>();

   public void addSound(String path, Consumer<SoundsGenerator.SoundGen> consumer, boolean overlayExisting) {
      if (overlayExisting && this.sounds.containsKey(path)) {
         consumer.accept(this.sounds.get(path));
      } else {
         this.sounds.put(path, (SoundsGenerator.SoundGen)Util.make(new SoundsGenerator.SoundGen(), consumer));
      }
   }

   public void addSound(String path, Consumer<SoundsGenerator.SoundGen> consumer) {
      this.addSound(path, consumer, false);
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      this.sounds.forEach((path, gen) -> json.add(path, gen.toJson()));
      return json;
   }

   public static class SoundGen {
      private boolean replace = false;
      @Nullable
      private String subtitle;
      private final List<SoundsGenerator.SoundInstance> instances = new ArrayList<>();

      public SoundsGenerator.SoundGen replace(boolean b) {
         this.replace = b;
         return this;
      }

      public SoundsGenerator.SoundGen replace() {
         return this.replace(true);
      }

      public SoundsGenerator.SoundGen subtitle(@Nullable String subtitle) {
         this.subtitle = subtitle;
         return this;
      }

      public SoundsGenerator.SoundGen sound(String file) {
         this.instances.add(new SoundsGenerator.SoundInstance(file));
         return this;
      }

      public SoundsGenerator.SoundGen sounds(String... sounds) {
         this.instances.addAll(Stream.of(sounds).map(SoundsGenerator.SoundInstance::new).toList());
         return this;
      }

      public SoundsGenerator.SoundGen sound(String file, Consumer<SoundsGenerator.SoundInstance> consumer) {
         this.instances.add((SoundsGenerator.SoundInstance)Util.make(new SoundsGenerator.SoundInstance(file), consumer));
         return this;
      }

      public JsonObject toJson() {
         JsonObject json = new JsonObject();
         if (this.replace) {
            json.addProperty("replace", true);
         }

         if (this.subtitle != null) {
            json.addProperty("subtitle", this.subtitle);
         }

         if (!this.instances.isEmpty()) {
            JsonArray array = new JsonArray(this.instances.size());
            this.instances.forEach(inst -> array.add(inst.toJson()));
            json.add("sounds", array);
         }

         return json;
      }
   }

   public static class SoundInstance {
      private final String fileLocation;
      private boolean complex = false;
      private float volume = 1.0F;
      private float pitch = 1.0F;
      private int weight = 1;
      private boolean stream = false;
      private int attenuationDistance = 16;
      private boolean preload = false;
      private boolean isEventReference = false;

      public SoundInstance(String fileLocation) {
         this.fileLocation = fileLocation;
      }

      private SoundsGenerator.SoundInstance complex() {
         this.complex = true;
         return this;
      }

      public SoundsGenerator.SoundInstance volume(float f) {
         this.volume = Mth.clamp(f, 0.0F, 1.0F);
         return this.complex();
      }

      public SoundsGenerator.SoundInstance pitch(float f) {
         this.pitch = Mth.clamp(f, 0.0F, 1.0F);
         return this.complex();
      }

      public SoundsGenerator.SoundInstance weight(int i) {
         this.weight = i;
         return this.complex();
      }

      public SoundsGenerator.SoundInstance stream(boolean b) {
         this.stream = b;
         return this.complex();
      }

      public SoundsGenerator.SoundInstance stream() {
         return this.stream(true);
      }

      public SoundsGenerator.SoundInstance attenuationDistance(int i) {
         this.attenuationDistance = i;
         return this.complex();
      }

      public SoundsGenerator.SoundInstance preload(boolean b) {
         this.preload = b;
         return this.complex();
      }

      public SoundsGenerator.SoundInstance preload() {
         return this.preload(true);
      }

      public SoundsGenerator.SoundInstance asReferenceToEvent() {
         this.isEventReference = true;
         return this.complex();
      }

      public JsonElement toJson() {
         if (!this.complex) {
            return new JsonPrimitive(this.fileLocation);
         } else {
            JsonObject json = new JsonObject();
            json.addProperty("name", this.fileLocation);
            json.addProperty("volume", this.volume);
            json.addProperty("pitch", this.pitch);
            json.addProperty("weight", this.weight);
            json.addProperty("stream", this.stream);
            json.addProperty("attenuation_distance", this.attenuationDistance);
            json.addProperty("preload", this.preload);
            if (this.isEventReference) {
               json.addProperty("type", "event");
            }

            return json;
         }
      }
   }
}
