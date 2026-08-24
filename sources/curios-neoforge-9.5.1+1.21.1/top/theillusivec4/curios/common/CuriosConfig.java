package top.theillusivec4.curios.common;

import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

public class CuriosConfig {
   public static final ModConfigSpec SERVER_SPEC;
   public static final CuriosConfig.Server SERVER;
   public static final ModConfigSpec COMMON_SPEC;
   public static final CuriosConfig.Common COMMON;
   private static final String CONFIG_PREFIX = "gui.curios.config.";

   static {
      Pair<CuriosConfig.Server, ModConfigSpec> specPair = new Builder().configure(CuriosConfig.Server::new);
      SERVER_SPEC = (ModConfigSpec)specPair.getRight();
      SERVER = (CuriosConfig.Server)specPair.getLeft();
      Pair<CuriosConfig.Common, ModConfigSpec> cspecPair = new Builder().configure(CuriosConfig.Common::new);
      COMMON_SPEC = (ModConfigSpec)cspecPair.getRight();
      COMMON = (CuriosConfig.Common)cspecPair.getLeft();
   }

   public static class Common {
      public ConfigValue<List<? extends String>> slots;

      public Common(Builder builder) {
         this.slots = builder.comment(
               "List of slots to create or modify.\nSee documentation for syntax: https://docs.illusivesoulworks.com/curios/configuration#slot-configuration\n"
            )
            .translation("gui.curios.config.slots")
            .defineList("slots", List.of(), s -> s instanceof String);
         builder.build();
      }
   }

   public static enum KeepCurios {
      ON,
      DEFAULT,
      OFF;
   }

   public static class Server {
      public EnumValue<CuriosConfig.KeepCurios> keepCurios;
      public IntValue minimumColumns;
      public IntValue maxSlotsPerPage;

      public Server(Builder builder) {
         this.keepCurios = builder.comment(
               "Sets behavior for keeping Curios items on death.\nON - Curios items are kept on death\nDEFAULT - Curios items follow the keepInventory gamerule\nOFF - Curios items are dropped on death"
            )
            .translation("gui.curios.config.keepCurios")
            .defineEnum("keepCurios", CuriosConfig.KeepCurios.DEFAULT);
         builder.push("menu");
         this.minimumColumns = builder.comment("The minimum number of columns for the Curios menu.")
            .translation("gui.curios.config.minimumColumns")
            .defineInRange("minimumColumns", 1, 1, 8);
         this.maxSlotsPerPage = builder.comment("The maximum number of slots per page of the Curios menu.")
            .translation("gui.curios.config.maxSlotsPerPage")
            .defineInRange("maxSlotsPerPage", 48, 1, 48);
         builder.pop();
         builder.build();
      }
   }
}
