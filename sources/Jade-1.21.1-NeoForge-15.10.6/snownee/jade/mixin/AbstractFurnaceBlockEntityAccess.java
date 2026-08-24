package snownee.jade.mixin;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({AbstractFurnaceBlockEntity.class})
public interface AbstractFurnaceBlockEntityAccess {
   @Accessor
   int getCookingProgress();

   @Accessor
   int getCookingTotalTime();
}
