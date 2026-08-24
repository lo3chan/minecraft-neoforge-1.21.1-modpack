package snownee.jade.addon.harvest;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.JadeIds;

public class ShearsToolHandler extends SimpleToolHandler {
   private static final ShearsToolHandler INSTANCE = new ShearsToolHandler();
   private final Set<Block> shearableBlocks = Sets.newIdentityHashSet();

   public static ShearsToolHandler getInstance() {
      return INSTANCE;
   }

   public ShearsToolHandler() {
      super(JadeIds.JADE("shears"), List.of(Items.SHEARS.getDefaultInstance()), true);
   }

   @Override
   public ItemStack test(BlockState state, Level world, BlockPos pos) {
      return !state.is(Blocks.TRIPWIRE) && !this.shearableBlocks.contains(state.getBlock()) ? super.test(state, world, pos) : (ItemStack)this.tools.getFirst();
   }

   public void setShearableBlocks(Collection<Block> blocks) {
      this.shearableBlocks.clear();
      this.shearableBlocks.addAll(blocks);
   }
}
