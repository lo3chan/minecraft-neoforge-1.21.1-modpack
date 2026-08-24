package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.Holder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.math.Fraction;

public interface GameplayContentContext {
   void registerFuel(Holder<? extends ItemLike> var1, Fraction var2);

   void registerFlammable(Holder<Block> var1, int var2, int var3);

   void registerCompostable(Holder<? extends ItemLike> var1, float var2);

   void registerStrippable(Holder<Block> var1, Holder<Block> var2);

   void registerFlattenable(Holder<Block> var1, Holder<Block> var2);

   void registerTillable(Holder<Block> var1, Holder<Block> var2);

   void registerOxidizable(Holder<Block> var1, Holder<Block> var2);

   void registerWaxable(Holder<Block> var1, Holder<Block> var2);
}
