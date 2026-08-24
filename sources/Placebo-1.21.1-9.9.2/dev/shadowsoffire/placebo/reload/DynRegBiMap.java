package dev.shadowsoffire.placebo.reload;

import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.util.AbstractBiMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import net.minecraft.resources.ResourceLocation;

public class DynRegBiMap<R extends CodecProvider<? super R>> extends AbstractBiMap<ResourceLocation, R> {
   public DynRegBiMap() {
      super(new HashMap<>(), new IdentityHashMap<>());
   }
}
