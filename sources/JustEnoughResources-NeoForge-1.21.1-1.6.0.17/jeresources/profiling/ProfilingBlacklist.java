package jeresources.profiling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import jeresources.platform.Services;
import net.minecraft.world.level.block.state.BlockState;

public class ProfilingBlacklist {
   private static final String scanBlacklistName = "scan-blacklist.txt";
   private List<String> blacklist = new LinkedList<>();

   public static File getScanBlacklistFile() {
      return Services.PLATFORM.getConfigDir().resolve("scan-blacklist.txt").toFile();
   }

   public ProfilingBlacklist() {
      File scanBlacklistFile = getScanBlacklistFile();
      if (scanBlacklistFile.exists()) {
         try {
            this.blacklist = Files.readAllLines(scanBlacklistFile.toPath());
         } catch (IOException var3) {
         }
      }
   }

   public boolean contains(BlockState blockState) {
      String blockString = blockState.toString();
      return this.blacklist.stream().anyMatch(blockString::startsWith);
   }
}
