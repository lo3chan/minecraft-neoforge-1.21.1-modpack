package net.caffeinemc.mods.sodium.client.compatibility.workarounds;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.caffeinemc.mods.sodium.client.compatibility.environment.OsUtils;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.amd.AmdWorkarounds;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.intel.IntelWorkarounds;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.nvidia.NvidiaWorkarounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Workarounds {
   private static final Logger LOGGER = LoggerFactory.getLogger("Sodium-Workarounds");
   private static final AtomicReference<Set<Workarounds.Reference>> ACTIVE_WORKAROUNDS = new AtomicReference<>(EnumSet.noneOf(Workarounds.Reference.class));

   public static void init() {
      Set<Workarounds.Reference> workarounds = findNecessaryWorkarounds();
      if (!workarounds.isEmpty()) {
         LOGGER.warn(
            "Sodium has applied one or more workarounds to prevent crashes or other issues on your system: [{}]",
            workarounds.stream().map(Enum::name).collect(Collectors.joining(", "))
         );
         LOGGER.warn(
            "This is not necessarily an issue, but it may result in certain features or optimizations being disabled. You can sometimes fix these issues by upgrading your graphics driver."
         );
      }

      ACTIVE_WORKAROUNDS.set(workarounds);
   }

   private static Set<Workarounds.Reference> findNecessaryWorkarounds() {
      EnumSet<Workarounds.Reference> workarounds = EnumSet.noneOf(Workarounds.Reference.class);
      OsUtils.OperatingSystem operatingSystem = OsUtils.getOs();
      if (NvidiaWorkarounds.isNvidiaGraphicsCardPresent()) {
         workarounds.add(Workarounds.Reference.NVIDIA_THREADED_OPTIMIZATIONS_BROKEN);
      }

      if (AmdWorkarounds.isAmdGraphicsCardPresent() && operatingSystem == OsUtils.OperatingSystem.WIN) {
         workarounds.add(Workarounds.Reference.AMD_GAME_OPTIMIZATION_BROKEN);
      }

      if (IntelWorkarounds.isUsingIntelGen8OrOlder()) {
         workarounds.add(Workarounds.Reference.INTEL_FRAMEBUFFER_BLIT_CRASH_WHEN_UNFOCUSED);
         workarounds.add(Workarounds.Reference.INTEL_DEPTH_BUFFER_COMPARISON_UNRELIABLE);
      }

      if (operatingSystem == OsUtils.OperatingSystem.LINUX) {
         workarounds.add(Workarounds.Reference.NO_ERROR_CONTEXT_UNSUPPORTED);
      }

      return Collections.unmodifiableSet(workarounds);
   }

   public static boolean isWorkaroundEnabled(Workarounds.Reference id) {
      return ACTIVE_WORKAROUNDS.get().contains(id);
   }

   public static enum Reference {
      NVIDIA_THREADED_OPTIMIZATIONS_BROKEN,
      NO_ERROR_CONTEXT_UNSUPPORTED,
      INTEL_FRAMEBUFFER_BLIT_CRASH_WHEN_UNFOCUSED,
      INTEL_DEPTH_BUFFER_COMPARISON_UNRELIABLE,
      AMD_GAME_OPTIMIZATION_BROKEN;
   }
}
