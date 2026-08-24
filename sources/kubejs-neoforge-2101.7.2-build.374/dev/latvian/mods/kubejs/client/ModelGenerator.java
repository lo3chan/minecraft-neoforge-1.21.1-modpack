package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.AABBWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class ModelGenerator {
   private static final ResourceLocation CUBE = ResourceLocation.withDefaultNamespace("block/cube");
   private ResourceLocation parent = CUBE;
   private final Map<String, String> textures = new HashMap<>(1);
   private final List<ModelGenerator.Element> elements = new ArrayList<>();
   private final List<ModelGenerator.Override> overrides = new ArrayList<>();
   private Consumer<JsonObject> customJson = null;

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      if (this.parent != null) {
         json.addProperty("parent", this.parent.toString());
      }

      if (!this.textures.isEmpty()) {
         JsonObject o = new JsonObject();

         for (Entry<String, String> entry : this.textures.entrySet()) {
            o.addProperty(entry.getKey(), entry.getValue());
         }

         json.add("textures", o);
      }

      if (!this.elements.isEmpty()) {
         JsonArray a = new JsonArray();

         for (ModelGenerator.Element e : this.elements) {
            a.add(e.toJson());
         }

         json.add("elements", a);
      }

      if (!this.overrides.isEmpty()) {
         JsonArray a = new JsonArray();

         for (ModelGenerator.Override override : this.overrides) {
            a.add(override.toJson());
         }

         json.add("overrides", a);
      }

      if (this.customJson != null) {
         this.customJson.accept(json);
      }

      return json;
   }

   public void parent(@Nullable ResourceLocation s) {
      this.parent = s;
   }

   @HideFromJS
   public void texture(String name, String texture) {
      this.textures.put(name, texture);
   }

   public void texture(String[] name, String texture) {
      for (String n : name) {
         this.textures.put(n, texture);
      }
   }

   public void textures(Map<String, String> map) {
      this.textures.putAll(map);
   }

   public void element(Consumer<ModelGenerator.Element> consumer) {
      ModelGenerator.Element e = new ModelGenerator.Element();
      consumer.accept(e);
      this.elements.add(e);
   }

   public void override(ResourceLocation model, Consumer<ModelGenerator.Override> override) {
      ModelGenerator.Override o = new ModelGenerator.Override(model);
      override.accept(o);
      this.overrides.add(o);
   }

   public void custom(Consumer<JsonObject> json) {
      this.customJson = json;
   }

   public static class Element {
      private AABB size = AABBWrapper.CUBE;
      private final ModelGenerator.Face[] faces = new ModelGenerator.Face[6];

      public JsonObject toJson() {
         JsonObject json = new JsonObject();
         JsonArray f = new JsonArray();
         f.add(this.size.minX * 16.0);
         f.add(this.size.minY * 16.0);
         f.add(this.size.minZ * 16.0);
         json.add("from", f);
         JsonArray t = new JsonArray();
         t.add(this.size.maxX * 16.0);
         t.add(this.size.maxY * 16.0);
         t.add(this.size.maxZ * 16.0);
         json.add("to", t);
         JsonObject fc = new JsonObject();

         for (ModelGenerator.Face face : this.faces) {
            if (face != null) {
               fc.add(face.side.getSerializedName(), face.toJson());
            }
         }

         json.add("faces", fc);
         return json;
      }

      public ModelGenerator.Element size(AABB b) {
         this.size = b;
         return this;
      }

      public void allFaces(Consumer<ModelGenerator.Face> face) {
         this.faces(DirectionWrapper.VALUES, face);
      }

      public void faces(Direction[] sides, Consumer<ModelGenerator.Face> face) {
         for (Direction d : sides) {
            ModelGenerator.Face f = this.faces[d.ordinal()];
            if (f == null) {
               f = new ModelGenerator.Face(d);
               this.faces[d.ordinal()] = f;
            }

            face.accept(f);
         }
      }
   }

   public static class Face {
      public final Direction side;
      private String texture = "kubejs:block/unknown";
      private Direction cullface = null;
      private double[] uv = null;
      private int tintindex = -1;

      public Face(Direction side) {
         this.side = side;
      }

      public JsonObject toJson() {
         JsonObject json = new JsonObject();
         json.addProperty("texture", this.texture);
         if (this.cullface != null) {
            json.addProperty("cullface", this.cullface.getSerializedName());
         }

         if (this.uv != null) {
            JsonArray a = new JsonArray();
            a.add(this.uv[0]);
            a.add(this.uv[1]);
            a.add(this.uv[2]);
            a.add(this.uv[3]);
            json.add("uv", a);
         }

         if (this.tintindex >= 0) {
            json.addProperty("tintindex", this.tintindex);
         }

         return json;
      }

      public ModelGenerator.Face tex(String t) {
         this.texture = t;
         return this;
      }

      public ModelGenerator.Face cull(Direction d) {
         this.cullface = d;
         return this;
      }

      public ModelGenerator.Face cull() {
         return this.cull(this.side);
      }

      public ModelGenerator.Face uv(double u0, double v0, double u1, double v1) {
         this.uv = new double[]{u0, v0, u1, v1};
         return this;
      }

      public ModelGenerator.Face tintindex(int i) {
         this.tintindex = i;
         return this;
      }
   }

   public static class Override {
      private final ResourceLocation model;
      private final List<ModelGenerator.OverridePredicate> predicates = new ArrayList<>();

      public Override(ResourceLocation model) {
         this.model = model;
      }

      public JsonObject toJson() {
         JsonObject json = new JsonObject();
         json.addProperty("model", this.model.toString());
         JsonObject p = new JsonObject();

         for (ModelGenerator.OverridePredicate predicate : this.predicates) {
            predicate.toJson(p);
         }

         json.add("predicate", p);
         return json;
      }

      public void predicate(ResourceLocation property, float value) {
         this.predicates.add(new ModelGenerator.OverridePredicate(property, value));
      }
   }

   public record OverridePredicate(ResourceLocation property, float value) {
      public void toJson(JsonObject json) {
         json.addProperty(this.property.toString(), this.value);
      }
   }
}
