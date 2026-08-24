package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import org.joml.Vector3f;

public interface ParticleOptionsWrapper {
   DustParticleOptions ERROR = new DustParticleOptions(new Vector3f(0.0F, 0.0F, 0.0F), 1.0F);

   static ParticleOptions wrap(Context cx, Object o) {
      if (o instanceof ParticleOptions po) {
         return po;
      } else if (o != null) {
         try {
            StringReader reader = new StringReader(o instanceof JsonElement j ? j.getAsString() : o.toString());
            return ParticleArgument.readParticle(reader, RegistryAccessContainer.of(cx).access());
         } catch (Exception var5) {
            throw new KubeRuntimeException("Failed to parse ParticleOptions from %s".formatted(o), var5).source(SourceLine.of(cx));
         }
      } else {
         return ERROR;
      }
   }

   static ParticleOptions create(ParticleOptions options) {
      return options;
   }
}
