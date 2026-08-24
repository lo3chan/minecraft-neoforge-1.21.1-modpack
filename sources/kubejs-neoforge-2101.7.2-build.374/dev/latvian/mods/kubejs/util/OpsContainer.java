package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import dev.latvian.mods.rhino.Context;
import java.util.Map;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class OpsContainer {
   public static final OpsContainer DEFAULT = new OpsContainer(NbtOps.INSTANCE, JsonOps.INSTANCE, JavaOps.INSTANCE);
   private final DynamicOps<Tag> nbt;
   private final DynamicOps<JsonElement> json;
   private final DynamicOps<Object> java;

   public OpsContainer(DynamicOps<Tag> nbt, DynamicOps<JsonElement> json, DynamicOps<Object> java) {
      this.nbt = nbt;
      this.json = json;
      this.java = java;
   }

   public DynamicOps<Tag> nbt() {
      return this.nbt;
   }

   public DynamicOps<JsonElement> json() {
      return this.json;
   }

   public DynamicOps<Object> java() {
      return this.java;
   }

   public <T> T decode(Context cx, Codec<T> codec, Object o) {
      return (T)((Pair)(switch (o) {
         case Tag tag -> codec.decode(this.nbt, tag);
         case Map<?, ?> map -> codec.decode(this.java, map);
         default -> codec.decode(this.json, JsonUtils.of(cx, o));
      }).getOrThrow()).getFirst();
   }

   public <T> T decodeMap(Context cx, MapCodec<T> codec, Object o) {
      return (T)(switch (o) {
         case Tag tag -> codec.decode(this.nbt, (MapLike)this.nbt.getMap(tag).getOrThrow());
         case Map<?, ?> map -> codec.decode(this.java, (MapLike)this.java.getMap(map).getOrThrow());
         default -> codec.decode(this.json, (MapLike)this.json.getMap(JsonUtils.of(cx, o)).getOrThrow());
      }).getOrThrow();
   }
}
