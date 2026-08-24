package dev.latvian.mods.kubejs.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import java.util.function.UnaryOperator;
import net.minecraft.resources.ResourceLocation;

public record KubeResourceLocation(ResourceLocation wrapped) {
   public static final Codec<KubeResourceLocation> CODEC = KubeJSCodecs.KUBEJS_ID.xmap(KubeResourceLocation::new, KubeResourceLocation::wrapped);

   public static KubeResourceLocation wrap(Object from) {
      return new KubeResourceLocation(ID.kjs(from));
   }

   @Override
   public String toString() {
      return this.wrapped.toString();
   }

   public KubeResourceLocation withPath(String path) {
      return new KubeResourceLocation(this.wrapped.withPath(path));
   }

   public KubeResourceLocation withPath(UnaryOperator<String> path) {
      return new KubeResourceLocation(this.wrapped.withPath(path));
   }
}
