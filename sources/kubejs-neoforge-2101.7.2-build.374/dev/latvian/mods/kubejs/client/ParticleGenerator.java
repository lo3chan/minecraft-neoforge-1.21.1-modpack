package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class ParticleGenerator {
   public transient List<String> textures = new ArrayList<>();

   public ParticleGenerator texture(String texture) {
      this.textures.add(texture);
      return this;
   }

   public ParticleGenerator textures(List<String> textures) {
      this.textures = textures;
      return this;
   }

   public JsonObject toJson() {
      JsonArray array = new JsonArray(this.textures.size());
      this.textures.forEach(array::add);
      JsonObject json = new JsonObject();
      json.add("textures", array);
      return json;
   }
}
