package net.mehvahdjukaar.moonlight.api.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;

public class SimpleTagBuilder extends TagBuilder {
   private final Set<String> uniqueKeys = new HashSet<>();
   private final ResourceLocation id;

   protected SimpleTagBuilder(ResourceLocation location) {
      this.id = location;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public String getTagString() {
      return "#" + this.id.toString();
   }

   public static SimpleTagBuilder of(ResourceLocation location) {
      return new SimpleTagBuilder(location);
   }

   public static SimpleTagBuilder of(TagKey<?> key) {
      return new SimpleTagBuilder(key.location());
   }

   public void merge(SimpleTagBuilder otherBuilder) {
      this.addAll(otherBuilder.build());
   }

   public SimpleTagBuilder addAll(Collection<TagEntry> entries) {
      entries.forEach(this::add);
      return this;
   }

   public TagBuilder add(TagEntry entry) {
      return (TagBuilder)(this.validateEntry(entry) ? super.add(entry) : this);
   }

   public SimpleTagBuilder add(String str) {
      return str.startsWith("#") ? this.addTag(ResourceLocation.parse(str.substring(1))) : this.add(ResourceLocation.parse(str));
   }

   public SimpleTagBuilder add(ResourceLocation entry) {
      super.addElement(entry);
      return this;
   }

   public TagBuilder addOptional(ResourceLocation elementLocation) {
      super.addOptionalElement(elementLocation);
      return this;
   }

   private boolean validateEntry(TagEntry entry) {
      if (this.uniqueKeys.contains(entry.toString())) {
         return false;
      } else {
         this.uniqueKeys.add(entry.toString());
         return true;
      }
   }

   public SimpleTagBuilder addTag(ResourceLocation pId) {
      super.addTag(pId);
      return this;
   }

   public SimpleTagBuilder addTag(TagKey<?> tagKey) {
      return this.addTag(tagKey.location());
   }

   public SimpleTagBuilder addOptionalTag(ResourceLocation pId) {
      super.addOptionalTag(pId);
      return this;
   }

   public SimpleTagBuilder addTag(SimpleTagBuilder otherBuilder) {
      return this.addTag(otherBuilder.getId());
   }

   public SimpleTagBuilder addEntries(Collection<?> entries) {
      entries.forEach(e -> this.add(Utils.getID(e)));
      return this;
   }

   public SimpleTagBuilder addEntry(Object entry) {
      if (entry instanceof ResourceLocation rl) {
         this.add(rl);
         return this;
      } else {
         this.add(Utils.getID(entry));
         return this;
      }
   }

   public JsonElement serializeToJson() {
      return (JsonElement)TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(this.build(), false)).getOrThrow();
   }

   public void addFromJson(JsonObject oldTag) {
      TagFile tagfile = (TagFile)TagFile.CODEC.parse(new Dynamic(JsonOps.INSTANCE, oldTag)).getOrThrow();
      if (tagfile.replace()) {
      }

      tagfile.entries().forEach(this::add);
   }
}
