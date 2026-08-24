package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaList;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import java.util.List;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({CollectionTag.class})
public abstract class CollectionTagMixin implements CustomJavaToJsWrapper {
   @Unique
   private static final TypeInfo TAGS_TYPE_INFO = TypeInfo.RAW_LIST.withParams(new TypeInfo[]{TypeInfo.of(Tag.class)});

   public Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo target) {
      return new NativeJavaList(cx, scope, this, (List)this, TAGS_TYPE_INFO);
   }
}
