package tannyjung.tanshugetrees_handcode.systems;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import tannyjung.tanshugetrees_core.game.CommandMaker;
import tannyjung.tanshugetrees_handcode.systems.living_mechanics.Seasons;
import tannyjung.tanshugetrees_handcode.systems.tree_generator.PresetFixer;
import tannyjung.tanshugetrees_handcode.systems.tree_generator.SaplingTrader;
import tannyjung.tanshugetrees_handcode.systems.tree_generator.ShapeFileConverter;
import tannyjung.tanshugetrees_handcode.systems.tree_generator.TreeGenerator;

public class Commands {
   public static void registry(Object event) {
      CommandMaker.create(event, 2, "TANSHUGETREES / command / preset_fixer", Commands.run.command::preset_fixer);
      CommandMaker.create(event, 2, "TANSHUGETREES / command / seasons / get", Commands.run.command.seasons::get);
      CommandMaker.create(event, 2, "TANSHUGETREES / command / seasons / set / autumn", Commands.run.command.seasons.set::autumn);
      CommandMaker.create(event, 2, "TANSHUGETREES / command / seasons / set / spring", Commands.run.command.seasons.set::spring);
      CommandMaker.create(event, 2, "TANSHUGETREES / command / seasons / set / summer", Commands.run.command.seasons.set::summer);
      CommandMaker.create(event, 2, "TANSHUGETREES / command / seasons / set / winter", Commands.run.command.seasons.set::winter);
      CommandMaker.create(event, 2, "TANSHUGETREES / command / shape_file_converter / start / <number>", Commands.run.command.shape_file_converter::start);
      CommandMaker.create(event, 2, "TANSHUGETREES / command / shape_file_converter / stop", Commands.run.command.shape_file_converter::stop);
      CommandMaker.create(event, 2, "TANSHUGETREES / command / summon_tree / <text>", Commands.run.command::summon_tree);
      CommandMaker.create(event, 2, "TANSHUGETREES / command / summon_sapling_trader", Commands.run.command::summon_sapling_trader);
   }

   private static class run {
      private static class command {
         private static void preset_fixer(CommandContext<CommandSourceStack> data) {
            ServerLevel level_server = ((CommandSourceStack)data.getSource()).getLevel();
            PresetFixer.start(level_server);
         }

         private static void summon_tree(CommandContext<CommandSourceStack> data) {
            LevelAccessor level_accessor = ((CommandSourceStack)data.getSource()).getLevel();
            ServerLevel level_server = ((CommandSourceStack)data.getSource()).getLevel();
            Entity entity = ((CommandSourceStack)data.getSource()).getEntity();
            Vec3 vec3 = ((CommandSourceStack)data.getSource()).getPosition();
            String variable_text = CommandMaker.Argument.getText(data);
            if (entity != null) {
               Player player = null;
               if (entity instanceof Player) {
                  player = (Player)entity;
               }

               TreeGenerator.create(level_accessor, level_server, player, BlockPos.containing(vec3), variable_text);
            }
         }

         private static void summon_sapling_trader(CommandContext<CommandSourceStack> data) {
            ServerLevel level_server = ((CommandSourceStack)data.getSource()).getLevel();
            Vec3 vec3 = ((CommandSourceStack)data.getSource()).getPosition();
            SaplingTrader.summonTrader(level_server, BlockPos.containing(vec3));
         }

         private static class seasons {
            private static void get(CommandContext<CommandSourceStack> data) {
               ServerLevel level_server = ((CommandSourceStack)data.getSource()).getLevel();
               Entity entity = ((CommandSourceStack)data.getSource()).getEntity();
               if (entity != null) {
                  Player player = null;
                  if (entity instanceof Player) {
                     player = (Player)entity;
                  }

                  Seasons.get(level_server, player);
               }
            }

            private static class set {
               private static void autumn(CommandContext<CommandSourceStack> data) {
                  ServerLevel level_server = ((CommandSourceStack)data.getSource()).getLevel();
                  Seasons.set(level_server, "Autumn");
               }

               private static void spring(CommandContext<CommandSourceStack> data) {
                  ServerLevel level_server = ((CommandSourceStack)data.getSource()).getLevel();
                  Seasons.set(level_server, "Spring");
               }

               private static void summer(CommandContext<CommandSourceStack> data) {
                  ServerLevel level_server = ((CommandSourceStack)data.getSource()).getLevel();
                  Seasons.set(level_server, "Summer");
               }

               private static void winter(CommandContext<CommandSourceStack> data) {
                  ServerLevel level_server = ((CommandSourceStack)data.getSource()).getLevel();
                  Seasons.set(level_server, "Winter");
               }
            }
         }

         private static class shape_file_converter {
            private static void start(CommandContext<CommandSourceStack> data) {
               LevelAccessor level_accessor = ((CommandSourceStack)data.getSource()).getLevel();
               int variable_number = CommandMaker.Argument.getNumber(data);
               ShapeFileConverter.start(level_accessor, variable_number);
            }

            private static void stop(CommandContext<CommandSourceStack> data) {
               LevelAccessor level_accessor = ((CommandSourceStack)data.getSource()).getLevel();
               ShapeFileConverter.stop(level_accessor);
            }
         }
      }
   }
}
