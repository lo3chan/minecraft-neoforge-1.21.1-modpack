package jeresources.neoforge;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Objects;
import jeresources.api.IJERAPI;
import jeresources.api.IJERPlugin;
import jeresources.platform.ILootTableHelper;
import jeresources.platform.IModList;
import jeresources.platform.IPlatformHelper;
import jeresources.proxy.CommonProxy;
import jeresources.util.LogHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;
import org.objectweb.asm.Type;

public class NeoForgePlatformHelper implements IPlatformHelper {
   @Override
   public String getPlatformName() {
      return "NeoForge";
   }

   @Override
   public boolean isClient() {
      return FMLEnvironment.dist == Dist.CLIENT;
   }

   @Override
   public CommonProxy getProxy() {
      return JEResources.PROXY;
   }

   @Override
   public IModList getModsList() {
      return new ModList(net.neoforged.fml.ModList.get());
   }

   @Override
   public void injectApi(IJERAPI instance) {
      Type pluginAnnotation = Type.getType(IJERPlugin.class);

      for (ModFileScanData scanData : net.neoforged.fml.ModList.get().getAllScanData()) {
         for (AnnotationData a : scanData.getAnnotations()) {
            if (Objects.equals(a.annotationType(), pluginAnnotation)) {
               try {
                  Class<?> clazz = Class.forName(a.clazz().getClassName());
                  IJERPlugin plugin = (IJERPlugin)clazz.getDeclaredConstructor().newInstance();
                  plugin.receive(instance);
               } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException | ClassNotFoundException var11) {
                  LogHelper.warn("Failed to set: {}" + a.clazz().getClassName() + "." + a.memberName());
               }
            }
         }
      }
   }

   @Override
   public boolean isCorrectToolForBlock(Block block, BlockState blockState, BlockGetter level, BlockPos blockPos, Player player) {
      return block.canHarvestBlock(blockState, level, blockPos, player);
   }

   @Override
   public Path getConfigDir() {
      return FMLPaths.CONFIGDIR.get();
   }

   @Override
   public ILootTableHelper getLootTableHelper() {
      return LootTableHelper.instance();
   }
}
