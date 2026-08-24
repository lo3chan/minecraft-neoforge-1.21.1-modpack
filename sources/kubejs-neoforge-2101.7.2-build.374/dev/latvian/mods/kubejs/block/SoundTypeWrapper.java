package dev.latvian.mods.kubejs.block;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.runtime.SwitchBootstraps;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;

public class SoundTypeWrapper implements TypeWrapperFactory<SoundType> {
   public static final SoundTypeWrapper INSTANCE = new SoundTypeWrapper();
   private Map<String, SoundType> map;

   public Map<String, SoundType> getMap() {
      if (this.map == null) {
         this.map = new LinkedHashMap<>();
         this.map.put("empty", SoundType.EMPTY);

         try {
            for (Field field : SoundType.class.getFields()) {
               if (field.getType() == SoundType.class && Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                  try {
                     this.map.put(field.getName().toLowerCase(Locale.ROOT), (SoundType)field.get(null));
                  } catch (Exception var6) {
                     var6.printStackTrace();
                  }
               }
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }

      return this.map;
   }

   public SoundType wrap(Context cx, Object o, TypeInfo target) {
      Object var4 = o;
      byte var5 = 0;

      while (true) {
         SoundType var10000;
         switch (SwitchBootstraps.typeSwitch<"typeSwitch",SoundType,Undefined,Scriptable,JsonPrimitive,ResourceLocation,CharSequence>(var4, var5)) {
            case -1:
               throw new KubeRuntimeException("SoundType cannot be null!").source(SourceLine.of(cx));
            case 0:
               SoundType t = (SoundType)var4;
               var10000 = t;
               break;
            case 1:
               Undefined u = (Undefined)var4;
               throw new KubeRuntimeException("Cannot wrap undefined as SoundType!").source(SourceLine.of(cx));
            case 2:
               Scriptable s = (Scriptable)var4;
               if (Undefined.isUndefined(s)) {
                  throw new KubeRuntimeException("Cannot wrap undefined as SoundType!").source(SourceLine.of(cx));
               }

               var5 = 3;
               continue;
            case 3:
               JsonPrimitive j = (JsonPrimitive)var4;
               var10000 = this.wrap(cx, j.getAsString(), target);
               break;
            case 4:
               ResourceLocation id = (ResourceLocation)var4;
               var10000 = this.wrap(cx, id.toString(), target);
               break;
            case 5:
               CharSequence cs = (CharSequence)var4;
               SoundType soundType = this.getMap().get(cs.toString());
               if (soundType == null) {
                  throw new KubeRuntimeException("Unknown SoundType '%s'".formatted(o)).source(SourceLine.of(cx));
               }

               var10000 = soundType;
               break;
            default:
               throw new KubeRuntimeException("Don't know how to wrap %s as sound type!".formatted(o)).source(SourceLine.of(cx));
         }

         return var10000;
      }
   }
}
