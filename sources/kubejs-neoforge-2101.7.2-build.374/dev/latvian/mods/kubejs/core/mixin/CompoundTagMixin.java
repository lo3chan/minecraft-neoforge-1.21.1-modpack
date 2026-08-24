package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({CompoundTag.class})
public abstract class CompoundTagMixin implements CustomJavaToJsWrapper {
   @Unique
   private static final TypeInfo KJS$MAP_TYPE = TypeInfo.RAW_MAP.withParams(new TypeInfo[]{TypeInfo.STRING, TypeInfo.of(Tag.class)});
   @Shadow
   @Final
   public Map<String, Tag> tags;

   public Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo target) {
      return new NativeJavaMap(cx, scope, this, this.tags, KJS$MAP_TYPE);
   }

   @Shadow
   @HideFromJS
   public abstract void putByteArray(String key, List<Byte> value);

   @Shadow
   @HideFromJS
   public abstract void putIntArray(String key, List<Integer> value);

   @Shadow
   @HideFromJS
   public abstract void putLongArray(String key, List<Long> value);
}
