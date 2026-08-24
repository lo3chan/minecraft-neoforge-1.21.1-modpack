package fabric.me.thosea.badoptimizations.mixin.accessors;

import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_746.class})
public interface PlayerAccessor {
   @Accessor("field_3917")
   int bo$underwaterVisibilityTicks();
}
