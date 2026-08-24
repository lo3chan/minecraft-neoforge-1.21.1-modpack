package net.Pandarix;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import java.util.function.Supplier;
import net.Pandarix.neoforge.PlatformImpl;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public class Platform {
   @ExpectPlatform
   @Transformed
   public static Supplier<PoiType> registerPoiType(String name, Supplier<Block> block) {
      return PlatformImpl.registerPoiType(name, block);
   }

   @ExpectPlatform
   @Transformed
   public static Supplier<VillagerProfession> registerProfession(String name, Supplier<VillagerProfession> profession) {
      return PlatformImpl.registerProfession(name, profession);
   }

   @ExpectPlatform
   @Transformed
   public static boolean hasSoaringWinds(Player player) {
      return PlatformImpl.hasSoaringWinds(player);
   }
}
