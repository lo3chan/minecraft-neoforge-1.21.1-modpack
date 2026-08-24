package alternate.current;

import alternate.current.command.AlternateCurrentCommand;
import alternate.current.util.profiler.Profiler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("alternate_current")
public class AlternateCurrentMod {
   public static final String MOD_ID = "alternate_current";
   public static final String MOD_NAME = "Alternate Current";
   public static final String MOD_VERSION = "1.9.0";
   public static final Logger LOGGER = LogManager.getLogger("Alternate Current");
   public static final boolean DEBUG = false;
   public static boolean on = true;

   public static Profiler createProfiler() {
      return Profiler.DUMMY;
   }

   @EventBusSubscriber(
      modid = "alternate_current"
   )
   public static class ModEvents {
      @SubscribeEvent
      public static void onRegisterCommands(RegisterCommandsEvent event) {
         AlternateCurrentCommand.register(event.getDispatcher());
      }
   }
}
