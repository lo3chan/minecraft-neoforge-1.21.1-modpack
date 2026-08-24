package jeresources.platform;

import java.nio.file.Path;
import jeresources.api.IJERAPI;
import jeresources.proxy.CommonProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface IPlatformHelper {
   String getPlatformName();

   boolean isClient();

   CommonProxy getProxy();

   IModList getModsList();

   void injectApi(IJERAPI var1);

   boolean isCorrectToolForBlock(Block var1, BlockState var2, BlockGetter var3, BlockPos var4, Player var5);

   Path getConfigDir();

   ILootTableHelper getLootTableHelper();
}
