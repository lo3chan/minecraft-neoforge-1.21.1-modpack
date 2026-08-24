package tannyjung.tanshugetrees_handcode.systems.tree_generator;

import java.io.File;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import tannyjung.tanshugetrees.network.TanshugetreesModVariables;
import tannyjung.tanshugetrees_core.Core;
import tannyjung.tanshugetrees_core.game.GameUtils;
import tannyjung.tanshugetrees_core.outside.OutsideUtils;
import tannyjung.tanshugetrees_core.outside.TXTFunction;
import tannyjung.tanshugetrees_handcode.Handcode;

public class TreeGenerator {
   public static Entity create(LevelAccessor level_accessor, ServerLevel level_server, Player player, BlockPos pos, String path) {
      File file = null;
      String path_type = "";
      StringBuilder path_extracted = new StringBuilder();
      boolean first = false;
      boolean second = false;

      for (String scan : path.split("/")) {
         if (!first) {
            first = true;
            path_extracted.append(scan);
         } else if (!second) {
            second = true;
            path_extracted.insert(0, scan + "/");
         } else {
            path_extracted.append("/").append(scan);
         }
      }

      file = new File(Core.path_config + "/custom_packs/" + path_extracted + ".txt");
      if (file.exists()) {
         path_type = "Extracted";
      } else {
         file = new File(Core.path_config + "/dev/temporary/" + path + ".txt");
         if (file.exists()) {
            path_type = "Unextracted";
         } else if (player != null) {
            GameUtils.Misc.sendChatMessagePrivate(player, "Path Not Found / red");
         }
      }

      if (!path_type.isEmpty()) {
         Entity entity_summon = GameUtils.Mob.summon(
            level_server,
            pos.getCenter(),
            "minecraft:marker",
            "Tree Generator",
            "TANSHUGETREES-tree_generator",
            GameUtils.Data.convertFileToForgeData(file.getPath())
         );
         if (entity_summon != null) {
            if (TanshugetreesModVariables.MapVariables.get(level_accessor).shape_file_converter) {
               GameUtils.Data.setEntityText(entity_summon, "path_type", path_type);
            } else {
               GameUtils.Misc.sendChatMessage(
                  level_server,
                  "Summoned a tree generator at "
                     + pos.getX()
                     + " "
                     + pos.getY()
                     + " "
                     + pos.getZ()
                     + "  / gray | [?] / dark_gray / "
                     + path
                     + " ("
                     + path_type
                     + ")"
               );
            }

            GameUtils.Data.setEntityText(entity_summon, "name", file.getName().substring(0, file.getName().length() - ".txt".length()));
            return entity_summon;
         }
      }

      return null;
   }

   public static void run(LevelAccessor level_accessor, Entity entity) {
      ServerLevel level_server = (ServerLevel)level_accessor;
      GameUtils.Misc.spawnParticle(level_server, entity.position(), 0.0, 0.0, 0.0, 0.0, 1, "minecraft:composter");
      if (!GameUtils.Data.getEntityLogic(entity, "start")) {
         GameUtils.Data.setEntityLogic(entity, "start", true);
         beforeRunSystem(level_accessor, level_server, entity);
      } else {
         GameUtils.Data.addEntityNumber(entity, "tree_generator_speed_tick_test", 1.0);
         if (GameUtils.Data.getEntityNumber(entity, "tree_generator_speed_tick_test") >= GameUtils.Data.getEntityNumber(entity, "tree_generator_speed_tick")) {
            GameUtils.Data.setEntityNumber(entity, "tree_generator_speed_tick_test", 0.0);
            runSystem(level_accessor, level_server, entity);
         }
      }
   }

   private static void beforeRunSystem(LevelAccessor level_accessor, ServerLevel level_server, Entity entity) {
      String id = entity.getUUID().toString();
      GameUtils.Data.setEntityText(entity, "id", id);
      entity.addTag("TANSHUGETREES-" + id);
      GameUtils.Data.setEntityText(entity, "gen_type", "taproot");
      GameUtils.Data.setEntityText(entity, "gen_step", "summon");
      GameUtils.Data.setEntityNumber(
         entity,
         "taproot_count",
         Mth.nextInt(
            RandomSource.create(),
            (int)GameUtils.Data.getEntityNumber(entity, "taproot_count_min"),
            (int)GameUtils.Data.getEntityNumber(entity, "taproot_count_max")
         )
      );
      GameUtils.Data.setEntityNumber(
         entity,
         "trunk_count",
         Mth.nextInt(
            RandomSource.create(),
            (int)GameUtils.Data.getEntityNumber(entity, "trunk_count_min"),
            (int)GameUtils.Data.getEntityNumber(entity, "trunk_count_max")
         )
      );
      if (GameUtils.Data.getEntityNumber(entity, "taproot_count") == 0.0) {
         GameUtils.Data.setEntityText(entity, "gen_type", "trunk");
      }

      if (TanshugetreesModVariables.MapVariables.get(level_accessor).shape_file_converter) {
         ShapeFileConverter.whenTreeStart(level_server, entity);
      } else {
         if (GameUtils.Data.getEntityLogic(entity, "debug_mode")) {
            GameUtils.Data.setEntityText(entity, "taproot_outer", "purple_concrete");
            GameUtils.Data.setEntityText(entity, "taproot_inner", "purple_terracotta");
            GameUtils.Data.setEntityText(entity, "taproot_core", "purple_stained_glass");
            GameUtils.Data.setEntityText(entity, "secondary_root_outer", "magenta_concrete");
            GameUtils.Data.setEntityText(entity, "secondary_root_inner", "magenta_terracotta");
            GameUtils.Data.setEntityText(entity, "secondary_root_core", "magenta_stained_glass");
            GameUtils.Data.setEntityText(entity, "tertiary_root_outer", "pink_concrete");
            GameUtils.Data.setEntityText(entity, "tertiary_root_inner", "pink_terracotta");
            GameUtils.Data.setEntityText(entity, "tertiary_root_core", "pink_stained_glass");
            GameUtils.Data.setEntityText(entity, "fine_root_outer", "light_blue_concrete");
            GameUtils.Data.setEntityText(entity, "fine_root_inner", "light_blue_terracotta");
            GameUtils.Data.setEntityText(entity, "fine_root_core", "light_blue_stained_glass");
            GameUtils.Data.setEntityText(entity, "trunk_outer", "red_concrete");
            GameUtils.Data.setEntityText(entity, "trunk_inner", "red_terracotta");
            GameUtils.Data.setEntityText(entity, "trunk_core", "red_stained_glass");
            GameUtils.Data.setEntityText(entity, "bough_outer", "orange_concrete");
            GameUtils.Data.setEntityText(entity, "bough_inner", "orange_terracotta");
            GameUtils.Data.setEntityText(entity, "bough_core", "orange_stained_glass");
            GameUtils.Data.setEntityText(entity, "branch_outer", "yellow_concrete");
            GameUtils.Data.setEntityText(entity, "branch_inner", "yellow_terracotta");
            GameUtils.Data.setEntityText(entity, "branch_core", "yellow_stained_glass");
            GameUtils.Data.setEntityText(entity, "limb_outer", "lime_concrete");
            GameUtils.Data.setEntityText(entity, "limb_inner", "lime_terracotta");
            GameUtils.Data.setEntityText(entity, "limb_core", "lime_stained_glass");
            GameUtils.Data.setEntityText(entity, "twig_outer", "green_concrete");
            GameUtils.Data.setEntityText(entity, "twig_inner", "green_terracotta");
            GameUtils.Data.setEntityText(entity, "twig_core", "green_stained_glass");
            GameUtils.Data.setEntityText(entity, "sprig_outer", "white_concrete");
            GameUtils.Data.setEntityText(entity, "sprig_inner", "white_terracotta");
            GameUtils.Data.setEntityText(entity, "sprig_core", "white_stained_glass");
            GameUtils.Data.setEntityText(entity, "leaves1", "white_stained_glass");
            GameUtils.Data.setEntityText(entity, "leaves2", "black_stained_glass");
         }

         Entity entity_countdown = GameUtils.Mob.getAtAreaOne(
            level_server, entity.position().add(0.0, 1.0, 0.0), 1, true, "minecraft:text_display", "TANSHUGETREES-tree_countdown"
         );
         if (entity_countdown != null) {
            entity_countdown.discard();
         }

         entity.setPos(entity.getX(), entity.getY() + GameUtils.Data.getEntityNumber(entity, "start_height"), entity.getZ());
         GameUtils.Misc.summonText(level_server, entity.position(), "TANSHUGETREES-" + id + " / TANSHUGETREES-tree_generator_status", 20.0, "");
         TXTFunction.run(level_accessor, level_server, entity.blockPosition(), GameUtils.Data.getEntityText(entity, "function_start"), true);
      }
   }

   private static void runSystem(LevelAccessor level_accessor, ServerLevel level_server, Entity entity) {
      String id = GameUtils.Data.getEntityText(entity, "id");
      String gen_type = GameUtils.Data.getEntityText(entity, "gen_type");
      String gen_step = GameUtils.Data.getEntityText(entity, "gen_step");
      String[] type_pre_next = null;
      Entity entity_status = GameUtils.Mob.getAtEverywhereOne(
         level_server, "minecraft:text_display", "TANSHUGETREES-" + id + " / TANSHUGETREES-tree_generator_status"
      );
      if (entity_status != null) {
         String command = "§b ↓ Generating a tree here ↓ \n\n§fTotal Process : "
            + (int)GameUtils.Data.getEntityNumber(entity, "total_processes")
            + "\n§fStep : "
            + gen_step
            + "\n§fType : "
            + gen_type
            + "\n\n§fCount Left : "
            + (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_count")
            + "\n§fLength : "
            + (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_length")
            + " / "
            + (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_length_save")
            + "\n§fThickness : "
            + OutsideUtils.Mathematics.shorterDouble(GameUtils.Data.getEntityNumber(entity, gen_type + "_thickness"), 3)
            + " / "
            + GameUtils.Data.getEntityNumber(entity, gen_type + "_thickness_start");
         double build_centerY = GameUtils.Data.getEntityNumber(entity, "build_centerY");
         if (build_centerY != 0.0 && entity_status.getY() - 25.0 < build_centerY) {
            entity_status.setPos(entity_status.getX(), build_centerY + 25.0, entity_status.getZ());
         }

         GameUtils.Command.runEntity(entity_status, "data merge entity @s {text:'{\"text\":\"" + command + "\",\"color\":\"white\"}'}");
      }

      if (Handcode.Config.tree_generator_speed_global && GameUtils.Data.getEntityLogic(entity, "tree_generator_speed_global")) {
         GameUtils.Data.setEntityNumber(entity, "tree_generator_speed_tick", Handcode.Config.tree_generator_speed_tick);
         GameUtils.Data.setEntityNumber(entity, "tree_generator_speed_repeat", Handcode.Config.tree_generator_speed_repeat);
         GameUtils.Data.setEntityNumber(entity, "tree_generator_tp_limit", Handcode.Config.tree_generator_tp_limit);
      }

      while (true) {
         GameUtils.Data.addEntityNumber(entity, "total_processes", 1.0);
         gen_type = GameUtils.Data.getEntityText(entity, "gen_type");
         gen_step = GameUtils.Data.getEntityText(entity, "gen_step");
         type_pre_next = getTypePreNext(gen_type);
         if (gen_step.equals("summon")) {
            if (!TreeGenerator.Step.summon(level_server, entity, id, gen_type, type_pre_next)) {
               return;
            }
         } else if (gen_step.equals("calculation")) {
            if (!TreeGenerator.Step.calculation(level_accessor, level_server, entity, id, gen_type, type_pre_next)) {
               return;
            }
         } else {
            if (!gen_step.equals("build")) {
               TreeGenerator.Step.end(level_accessor, level_server, entity, id);
               return;
            }

            if (!TreeGenerator.Step.build(level_accessor, level_server, entity, id, gen_type, type_pre_next)) {
               return;
            }
         }

         if (!TanshugetreesModVariables.MapVariables.get(level_accessor).shape_file_converter
            && GameUtils.Data.getEntityNumber(entity, "tree_generator_speed_repeat") != 0.0) {
            if (!(
               GameUtils.Data.getEntityNumber(entity, "tree_generator_speed_repeat_test")
                  < GameUtils.Data.getEntityNumber(entity, "tree_generator_speed_repeat")
            )) {
               GameUtils.Data.setEntityNumber(entity, "tree_generator_speed_repeat_test", 0.0);
               return;
            }

            GameUtils.Data.addEntityNumber(entity, "tree_generator_speed_repeat_test", 1.0);
         }
      }
   }

   private static String[] getTypePreNext(String gen_type) {
      String[] return_text = new String[2];
      if (gen_type.equals("taproot")) {
         return_text[0] = "trunk";
         return_text[1] = "secondary_root";
      } else if (gen_type.equals("secondary_root")) {
         return_text[0] = "taproot";
         return_text[1] = "tertiary_root";
      } else if (gen_type.equals("tertiary_root")) {
         return_text[0] = "secondary_root";
         return_text[1] = "fine_root";
      } else if (gen_type.equals("fine_root")) {
         return_text[0] = "tertiary_root";
         return_text[1] = "";
      }

      if (gen_type.equals("trunk")) {
         return_text[0] = "";
         return_text[1] = "bough";
      } else if (gen_type.equals("bough")) {
         return_text[0] = "trunk";
         return_text[1] = "branch";
      } else if (gen_type.equals("branch")) {
         return_text[0] = "bough";
         return_text[1] = "limb";
      } else if (gen_type.equals("limb")) {
         return_text[0] = "branch";
         return_text[1] = "twig";
      } else if (gen_type.equals("twig")) {
         return_text[0] = "limb";
         return_text[1] = "sprig";
      } else if (gen_type.equals("sprig")) {
         return_text[0] = "twig";
         return_text[1] = "leaves";
      } else if (gen_type.equals("leaves")) {
         return_text[0] = "sprig";
         return_text[1] = "";
      }

      return return_text;
   }

   private static class Step {
      private static boolean summon(ServerLevel level_server, Entity entity, String id, String gen_type, String[] type_pre_next) {
         boolean is_taproot_trunk = gen_type.equals("taproot") || gen_type.equals("trunk");
         if (!gen_type.equals("leaves")) {
            double length = Mth.nextInt(
               RandomSource.create(),
               (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_length_min"),
               (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_length_max")
            );
            length = Math.ceil(length * summonReduction(entity, gen_type, "length_reduce"));
            GameUtils.Data.setEntityNumber(entity, gen_type + "_length", length);
            GameUtils.Data.setEntityNumber(entity, gen_type + "_length_save", length);
            double thickness = GameUtils.Data.getEntityNumber(entity, gen_type + "_thickness_start")
               - GameUtils.Data.getEntityNumber(entity, gen_type + "_thickness_end");
            thickness *= summonReduction(entity, gen_type, "thickness_reduce");
            thickness = GameUtils.Data.getEntityNumber(entity, gen_type + "_thickness_end") + thickness;
            GameUtils.Data.setEntityNumber(entity, gen_type + "_thickness", thickness);
            if (!type_pre_next[1].equals("leaves")) {
               int count = Mth.nextInt(
                  RandomSource.create(),
                  (int)GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_count_min"),
                  (int)GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_count_max")
               );
               int var28 = (int)Math.ceil(count * summonReduction(entity, type_pre_next[1], "count_reduce"));
               GameUtils.Data.setEntityNumber(entity, type_pre_next[1] + "_count", var28);
               GameUtils.Data.setEntityNumber(entity, type_pre_next[1] + "_count_save", var28);
               if (GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_count") > 0.0) {
                  length = 0.0;
                  if (GameUtils.Data.getEntityLogic(entity, type_pre_next[1] + "_chance_auto")) {
                     thickness = GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_count_save")
                        - GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_min_last_count");
                     if (thickness > 0.0) {
                        double length_percent = Math.ceil(
                           GameUtils.Data.getEntityNumber(entity, gen_type + "_length_save")
                              * (GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_chance_percent") * 0.01)
                        );
                        length = length_percent / thickness;
                     }
                  }

                  GameUtils.Data.setEntityNumber(entity, type_pre_next[1] + "_chance_auto_distance", length);
                  GameUtils.Data.setEntityNumber(entity, type_pre_next[1] + "_chance_distance_left", 0.0);
               }
            } else {
               GameUtils.Data.setEntityNumber(entity, "leaves_count", 1.0);
               GameUtils.Data.setEntityNumber(entity, "leaves_length", 1.0);
               GameUtils.Data.setEntityNumber(entity, "leaves_length_save", 1.0);
               length = Mth.nextDouble(
                  RandomSource.create(), GameUtils.Data.getEntityNumber(entity, "leaves_size_min"), GameUtils.Data.getEntityNumber(entity, "leaves_size_max")
               );
               length *= summonReduction(entity, "leaves", "size_reduce");
               GameUtils.Data.setEntityNumber(entity, "leaves_size", length);
            }

            if (!is_taproot_trunk) {
               length = GameUtils.Data.getEntityNumber(entity, gen_type + "_continue_chance");
               length *= summonReduction(entity, gen_type, "continue_reduce");
               length = 1.0 - length;
               if (Math.random() < length) {
                  GameUtils.Data.setEntityNumber(entity, type_pre_next[1] + "_count", 0.0);
               }
            }
         }

         Entity entity_at = null;
         Vec3 vec3 = null;
         if (is_taproot_trunk) {
            entity_at = GameUtils.Mob.getAtEverywhereOne(level_server, "minecraft:marker", "TANSHUGETREES-" + id + " / TANSHUGETREES-tree_generator");
            if (entity_at == null) {
               return false;
            }

            vec3 = entity_at.position();
         } else {
            entity_at = GameUtils.Mob.getAtEverywhereOne(
               level_server, "minecraft:marker", "TANSHUGETREES-" + id + " / TANSHUGETREES-generator_" + type_pre_next[0]
            );
            if (entity_at == null) {
               return false;
            }

            if (gen_type.equals("leaves")) {
               vec3 = entity_at.position();
            } else {
               double vertical = 0.0;
               double horizontal = 0.0;
               double forward = 0.0;
               double height = 0.0;
               String center_direction_from = GameUtils.Data.getEntityText(entity, gen_type + "_center_direction_from");
               if (center_direction_from.isEmpty()) {
                  if (GameUtils.Data.getEntityNumber(entity, gen_type + "_min_last_count") > 0.0
                     && GameUtils.Data.getEntityNumber(entity, gen_type + "_count") <= 1.0) {
                     forward = 1.0;
                  } else {
                     vertical = GameUtils.Data.getEntityNumber(entity, gen_type + "_start_vertical");
                     horizontal = GameUtils.Data.getEntityNumber(entity, gen_type + "_start_horizontal");
                     height = Mth.nextDouble(
                        RandomSource.create(),
                        GameUtils.Data.getEntityNumber(entity, gen_type + "_start_height_min"),
                        GameUtils.Data.getEntityNumber(entity, gen_type + "_start_height_max")
                     );
                     forward = Mth.nextDouble(
                        RandomSource.create(),
                        GameUtils.Data.getEntityNumber(entity, gen_type + "_start_forward_min"),
                        GameUtils.Data.getEntityNumber(entity, gen_type + "_start_forward_max")
                     );
                  }
               } else {
                  int lengthx = (int)GameUtils.Data.getEntityNumber(entity, type_pre_next[0] + "_length");
                  int length_save = (int)GameUtils.Data.getEntityNumber(entity, type_pre_next[0] + "_length_save");
                  double center = GameUtils.Data.getEntityNumber(entity, gen_type + "_center_direction_center") * 0.01;
                  int length_below = (int)(length_save * center);
                  int length_above = length_save - length_below;
                  double percent = 0.0;
                  String above_or_below = "";
                  if (1.0 - center >= (double)lengthx / length_save) {
                     above_or_below = "above";
                     percent = 1.0 - (double)lengthx / length_above;
                  } else {
                     above_or_below = "below";
                     percent = (double)(lengthx - length_above) / length_below;
                  }

                  vertical = 1.0 + (GameUtils.Data.getEntityNumber(entity, gen_type + "_center_direction_vertical_" + above_or_below) - 1.0) * percent;
                  horizontal = 1.0 + (GameUtils.Data.getEntityNumber(entity, gen_type + "_center_direction_horizontal_" + above_or_below) - 1.0) * percent;
                  height = GameUtils.Data.getEntityNumber(entity, gen_type + "_center_direction_height_" + above_or_below) * percent;
                  forward = GameUtils.Data.getEntityNumber(entity, gen_type + "_center_direction_forward_" + above_or_below) * percent;
               }

               vertical = Mth.nextDouble(RandomSource.create(), -vertical, vertical);
               horizontal = Mth.nextDouble(RandomSource.create(), -horizontal, horizontal);
               vec3 = GameUtils.Space.getPosLook(entity_at, horizontal, vertical, forward);
               vec3 = vec3.add(0.0, height, 0.0);
            }
         }

         Entity entity_summon = GameUtils.Mob.summon(
            level_server, vec3, "minecraft:marker", "Tree Generator (" + gen_type + ")", "TANSHUGETREES-" + id + " / TANSHUGETREES-generator_" + gen_type, ""
         );
         if (entity_summon == null) {
            return false;
         } else {
            if (is_taproot_trunk) {
               entity_summon.setXRot(
                  Mth.nextInt(
                     RandomSource.create(),
                     (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_start_gravity_max"),
                     (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_start_gravity_min")
                  )
               );
               entity_summon.setYRot(
                  Mth.nextInt(
                     RandomSource.create(),
                     (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_start_direction_min"),
                     (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_start_direction_max")
                  )
               );
            } else {
               entity_summon.lookAt(Anchor.FEET, entity_at.position());
               entity_summon.setPos(entity_at.position());
               entity_summon.lookAt(Anchor.FEET, GameUtils.Space.getPosLook(entity_summon, 0.0, 0.0, -1.0));
            }

            if (is_taproot_trunk) {
               GameUtils.Data.setEntityText(entity, "gen_step", "build");
            } else {
               GameUtils.Data.setEntityText(entity, "gen_step", "calculation");
            }

            GameUtils.Data.addEntityNumber(entity, gen_type + "_count", -1.0);
            return true;
         }
      }

      private static double summonReduction(Entity entity, String gen_type, String gen_step) {
         String from = GameUtils.Data.getEntityText(entity, gen_type + "_" + gen_step + "_from");
         if (from.isEmpty()) {
            return 1.0;
         } else {
            double length = GameUtils.Data.getEntityNumber(entity, from + "_length");
            double length_save = GameUtils.Data.getEntityNumber(entity, from + "_length_save");
            double center = GameUtils.Data.getEntityNumber(entity, gen_type + "_" + gen_step + "_center") * 0.01;
            double length_below = length_save * center;
            double length_above = length_save - length_below;
            String above_below = "";
            double percent = 0.0;
            if (1.0 - center >= length / length_save) {
               above_below = "above";
               percent = length / length_above;
            } else {
               above_below = "below";
               percent = 1.0 - (length - length_above) / length_below;
            }

            double start = GameUtils.Data.getEntityNumber(entity, gen_type + "_" + gen_step + "_" + above_below + "_start");
            double end = GameUtils.Data.getEntityNumber(entity, gen_type + "_" + gen_step + "_" + above_below + "_end");
            double reduce = (start - end) * percent;
            return (end + reduce) * 0.01;
         }
      }

      private static boolean calculation(
         LevelAccessor level_accessor, ServerLevel level_server, Entity entity, String id, String gen_type, String[] type_pre_next
      ) {
         Entity entity_current = GameUtils.Mob.getAtEverywhereOne(
            level_server, "minecraft:marker", "TANSHUGETREES-" + id + " / TANSHUGETREES-generator_" + gen_type
         );
         if (entity_current == null) {
            return false;
         } else {
            GameUtils.Data.setEntityNumber(entity, "previous_posX", entity_current.position().x);
            GameUtils.Data.setEntityNumber(entity, "previous_posY", entity_current.position().y);
            GameUtils.Data.setEntityNumber(entity, "previous_posZ", entity_current.position().z);
            boolean go_next = false;
            if (GameUtils.Data.getEntityNumber(entity, gen_type + "_length") > 0.0) {
               if (GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_count")
                     > GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_min_last_count")
                  && GameUtils.Data.getEntityNumber(entity, gen_type + "_length") / GameUtils.Data.getEntityNumber(entity, gen_type + "_length_save")
                     <= GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_chance_percent") * 0.01) {
                  if (GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_chance_distance_left") > 0.0) {
                     GameUtils.Data.addEntityNumber(entity, type_pre_next[1] + "_chance_distance_left", -1.0);
                  } else {
                     GameUtils.Data.setEntityNumber(
                        entity, type_pre_next[1] + "_chance_distance_left", GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_chance_auto_distance")
                     );
                     if (Math.random() < GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_chance")) {
                        go_next = true;
                     }
                  }
               }
            } else {
               go_next = true;
            }

            if (go_next) {
               if (GameUtils.Data.getEntityNumber(entity, type_pre_next[1] + "_count") > 0.0) {
                  GameUtils.Data.setEntityText(entity, "gen_type", type_pre_next[1]);
                  GameUtils.Data.setEntityText(entity, "gen_step", "summon");
               } else {
                  entity_current.discard();
                  if (gen_type.equals("taproot")) {
                     if (GameUtils.Data.getEntityNumber(entity, gen_type + "_count") > 0.0) {
                        GameUtils.Data.setEntityText(entity, "gen_step", "summon");
                     } else if (GameUtils.Data.getEntityNumber(entity, type_pre_next[0] + "_count") > 0.0) {
                        GameUtils.Data.setEntityText(entity, "gen_step", "summon");
                        GameUtils.Data.setEntityText(entity, "gen_type", type_pre_next[0]);
                     } else {
                        GameUtils.Data.setEntityText(entity, "gen_step", "end");
                     }
                  } else if (gen_type.equals("trunk")) {
                     if (GameUtils.Data.getEntityNumber(entity, gen_type + "_count") > 0.0) {
                        GameUtils.Data.setEntityText(entity, "gen_step", "summon");
                     } else {
                        GameUtils.Data.setEntityText(entity, "gen_step", "end");
                     }
                  } else {
                     GameUtils.Data.setEntityText(entity, "gen_type", type_pre_next[0]);
                  }
               }
            } else {
               if (!gen_type.equals("leaves")) {
                  float vertical = (float)GameUtils.Data.getEntityNumber(entity, gen_type + "_curvature_vertical");
                  float horizontal = (float)GameUtils.Data.getEntityNumber(entity, gen_type + "_curvature_horizontal");
                  entity_current.setXRot(entity_current.getXRot() + Mth.nextFloat(RandomSource.create(), -vertical, vertical));
                  entity_current.setYRot(entity_current.getYRot() + Mth.nextFloat(RandomSource.create(), -horizontal, horizontal));
                  boolean gravity_run = false;
                  horizontal = (float)GameUtils.Data.getEntityNumber(entity, gen_type + "_gravity_weightiness");
                  if (horizontal != 0.0F) {
                     float set = 0.0F;
                     if (entity_current.getXRot() >= GameUtils.Data.getEntityNumber(entity, gen_type + "_gravity_min")) {
                        set = entity_current.getXRot() - horizontal;
                        if (set < -90.0F) {
                           set = -90.0F;
                        }

                        entity_current.setXRot(set);
                        gravity_run = true;
                     }

                     if (entity_current.getXRot() <= GameUtils.Data.getEntityNumber(entity, gen_type + "_gravity_max")) {
                        set = entity_current.getXRot() + horizontal;
                        if (set > 90.0F) {
                           set = 90.0F;
                        }

                        entity_current.setXRot(set);
                        gravity_run = true;
                     }
                  }

                  if (!gravity_run) {
                     double centripetal = GameUtils.Data.getEntityNumber(entity, gen_type + "_centripetal") * 0.01;
                     if (centripetal != 0.0) {
                        Entity entity_trunk = GameUtils.Mob.getAtEverywhereOne(
                           level_server, "minecraft:marker", "TANSHUGETREES-" + id + " / TANSHUGETREES-generator_trunk"
                        );
                        if (entity_trunk == null) {
                           return false;
                        }

                        Vec3 vec3 = entity_current.position();
                        entity_current.setPos(GameUtils.Space.getPosLook(entity_current, 0.0, 0.0, 1.0));
                        entity_current.lookAt(Anchor.FEET, entity_trunk.position());
                        entity_current.setPos(GameUtils.Space.getPosLook(entity_current, 0.0, 0.0, -centripetal));
                        entity_current.lookAt(Anchor.FEET, vec3);
                        entity_current.setPos(vec3);
                        entity_current.lookAt(Anchor.FEET, GameUtils.Space.getPosLook(entity_current, 0.0, 0.0, -1.0));
                     }
                  }

                  double thickness = GameUtils.Data.getEntityNumber(entity, gen_type + "_thickness_start")
                     - GameUtils.Data.getEntityNumber(entity, gen_type + "_thickness_end");
                  double length_percent = GameUtils.Data.getEntityNumber(entity, gen_type + "_length")
                     / GameUtils.Data.getEntityNumber(entity, gen_type + "_length_save");
                  double var17 = GameUtils.Data.getEntityNumber(entity, gen_type + "_thickness_end") + thickness * length_percent;
                  GameUtils.Data.setEntityNumber(entity, gen_type + "_thickness", var17);
               }

               entity_current.setPos(GameUtils.Space.getPosLook(entity_current, 0.0, 0.0, 1.0));
               GameUtils.Data.addEntityNumber(entity, gen_type + "_length", -1.0);
               GameUtils.Data.setEntityText(entity, "gen_step", "build");
               if (!TanshugetreesModVariables.MapVariables.get(level_accessor).shape_file_converter
                  && GameUtils.Data.getEntityNumber(entity, "tree_generator_tp_limit") != 0.0) {
                  if (!(
                     GameUtils.Data.getEntityNumber(entity, "tree_generator_tp_limit_test") < GameUtils.Data.getEntityNumber(entity, "tree_generator_tp_limit")
                  )) {
                     GameUtils.Data.setEntityNumber(entity, "tree_generator_tp_limit_test", 0.0);
                     return false;
                  }

                  GameUtils.Data.addEntityNumber(entity, "tree_generator_tp_limit_test", 1.0);
               }
            }

            return true;
         }
      }

      private static boolean build(LevelAccessor level_accessor, ServerLevel level_server, Entity entity, String id, String gen_type, String[] type_pre_next) {
         double thickness = GameUtils.Data.getEntityNumber(entity, gen_type + "_thickness");
         double size = 0.0;
         if (!gen_type.equals("leaves")) {
            size = thickness;
         } else {
            size = GameUtils.Data.getEntityNumber(entity, "leaves_size");
         }

         if (size == 0.0) {
            return true;
         } else {
            if (--size < 0.0) {
               size = 0.0;
            }

            double radius = size * 0.5;
            double radius_ceil = Math.ceil(radius);
            String generator_type = GameUtils.Data.getEntityText(entity, gen_type + "_generator_type");
            if (!GameUtils.Data.getEntityLogic(entity, "still_building")) {
               GameUtils.Data.setEntityLogic(entity, "still_building", true);
               Entity entity_current = GameUtils.Mob.getAtEverywhereOne(
                  level_server, "minecraft:marker", "TANSHUGETREES-" + id + " / TANSHUGETREES-generator_" + gen_type
               );
               if (entity_current == null) {
                  return false;
               }

               GameUtils.Data.setEntityNumber(entity, "build_centerX", entity_current.position().x);
               GameUtils.Data.setEntityNumber(entity, "build_centerY", entity_current.position().y);
               GameUtils.Data.setEntityNumber(entity, "build_centerZ", entity_current.position().z);
               if (generator_type.equals("sphere_zone")) {
                  entity_current = GameUtils.Mob.getAtEverywhereOne(
                     level_server, "minecraft:marker", "TANSHUGETREES-" + id + " / TANSHUGETREES-generator_" + type_pre_next[0]
                  );
                  if (entity_current == null) {
                     return false;
                  }

                  double yaw = Math.toRadians((entity_current.getYRot() + 180.0F + 90.0F) % 360.0F);
                  double pitch = entity_current.getXRot();
                  int pitch_min = (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_sphere_zone_pitch_min");
                  if (pitch > pitch_min) {
                     pitch = pitch_min;
                  }

                  pitch = Math.toRadians(pitch);
                  int sphere_zone_size = (int)GameUtils.Data.getEntityNumber(entity, gen_type + "_sphere_zone_size");
                  GameUtils.Data.setEntityNumber(entity, "sphere_zone_posX", radius * Math.cos(pitch) * Math.cos(yaw));
                  GameUtils.Data.setEntityNumber(entity, "sphere_zone_posY", radius * Math.sin(pitch));
                  GameUtils.Data.setEntityNumber(entity, "sphere_zone_posZ", radius * Math.cos(pitch) * Math.sin(yaw));
                  double var53 = radius - sphere_zone_size;
                  GameUtils.Data.addEntityNumber(entity, "build_centerX", var53 * Math.cos(pitch) * Math.cos(yaw));
                  GameUtils.Data.addEntityNumber(entity, "build_centerY", var53 * Math.sin(pitch));
                  GameUtils.Data.addEntityNumber(entity, "build_centerZ", var53 * Math.cos(pitch) * Math.sin(yaw));
                  double sphere_zone_area = size - sphere_zone_size;
                  if (sphere_zone_area < 0.0) {
                     sphere_zone_area = 0.0;
                  }

                  GameUtils.Data.setEntityNumber(entity, "sphere_zone_area", sphere_zone_area * sphere_zone_area);
               }

               GameUtils.Data.setEntityNumber(entity, "build_saveX", -radius);
               GameUtils.Data.setEntityNumber(entity, "build_saveY", -radius);
               GameUtils.Data.setEntityNumber(entity, "build_saveZ", -radius);
            }

            double sphere_zone_area = 0.0;
            double[] sphere_zone_pos = null;
            if (generator_type.equals("sphere_zone")) {
               sphere_zone_area = GameUtils.Data.getEntityNumber(entity, "sphere_zone_area");
               sphere_zone_pos = new double[]{
                  GameUtils.Data.getEntityNumber(entity, "sphere_zone_posX"),
                  GameUtils.Data.getEntityNumber(entity, "sphere_zone_posY"),
                  GameUtils.Data.getEntityNumber(entity, "sphere_zone_posZ")
               };
            }

            double build_centerX = GameUtils.Data.getEntityNumber(entity, "build_centerX");
            double build_centerY = GameUtils.Data.getEntityNumber(entity, "build_centerY");
            double build_centerZ = GameUtils.Data.getEntityNumber(entity, "build_centerZ");
            boolean replace = GameUtils.Data.getEntityLogic(entity, gen_type + "_replace");
            double sphere_area = (radius + 0.35) * (radius + 0.35);
            double scan_change = radius / radius_ceil;
            if (radius == 0.0) {
               scan_change = 1.0;
            }

            double scan_start = radius + scan_change;
            double scan_end = radius + scan_change;
            double build_area = 0.0;
            double build_saveX = 0.0;
            double build_saveY = 0.0;
            double build_saveZ = 0.0;
            BlockPos pos = null;

            while (true) {
               if (!TanshugetreesModVariables.MapVariables.get(level_accessor).shape_file_converter
                  && GameUtils.Data.getEntityNumber(entity, "tree_generator_speed_repeat") != 0.0) {
                  if (!(
                     GameUtils.Data.getEntityNumber(entity, "tree_generator_speed_repeat_test")
                        < GameUtils.Data.getEntityNumber(entity, "tree_generator_speed_repeat")
                  )) {
                     GameUtils.Data.setEntityNumber(entity, "tree_generator_speed_repeat_test", 0.0);
                     return false;
                  }

                  GameUtils.Data.addEntityNumber(entity, "tree_generator_speed_repeat_test", 1.0);
               }

               build_saveX = GameUtils.Data.getEntityNumber(entity, "build_saveX");
               build_saveY = GameUtils.Data.getEntityNumber(entity, "build_saveY");
               build_saveZ = GameUtils.Data.getEntityNumber(entity, "build_saveZ");
               if (build_saveY > scan_end) {
                  GameUtils.Data.setEntityLogic(entity, "still_building", false);
                  GameUtils.Data.setEntityText(entity, "gen_step", "calculation");
                  return true;
               }

               if (build_saveX > scan_end) {
                  GameUtils.Data.setEntityNumber(entity, "build_saveX", -scan_start);
                  GameUtils.Data.addEntityNumber(entity, "build_saveY", scan_change);
               } else if (build_saveZ > scan_end) {
                  GameUtils.Data.setEntityNumber(entity, "build_saveZ", -scan_start);
                  GameUtils.Data.addEntityNumber(entity, "build_saveX", scan_change);
               } else {
                  GameUtils.Data.addEntityNumber(entity, "build_saveZ", scan_change);
                  build_area = build_saveX * build_saveX + build_saveY * build_saveY + build_saveZ * build_saveZ;
                  if (!(build_area > sphere_area)
                     && (
                        !generator_type.equals("sphere_zone")
                           || !(
                              Math.pow(sphere_zone_pos[0] - build_saveX, 2.0)
                                    + Math.pow(sphere_zone_pos[1] - build_saveY, 2.0)
                                    + Math.pow(sphere_zone_pos[2] - build_saveZ, 2.0)
                                 < sphere_zone_area
                           )
                     )) {
                     pos = new BlockPos((int)(build_centerX + build_saveX), (int)(build_centerY + build_saveY), (int)(build_centerZ + build_saveZ));
                     if (gen_type.equals("leaves")) {
                        if (Math.random() < GameUtils.Data.getEntityNumber(entity, "leaves_density") * 0.01) {
                           String previous_block = "";
                           String block_type = "";
                           BlockPos pos_leaves = null;
                           int deep = 0;
                           if (Math.random() < GameUtils.Data.getEntityNumber(entity, "leaves_straighten_chance")) {
                              deep = Mth.nextInt(
                                 RandomSource.create(),
                                 (int)GameUtils.Data.getEntityNumber(entity, "leaves_straighten_min"),
                                 (int)GameUtils.Data.getEntityNumber(entity, "leaves_straighten_max")
                              );
                           }

                           for (int deep_test = 0; deep_test <= deep; deep_test++) {
                              pos_leaves = pos.below(deep_test);
                              previous_block = buildGetPreviousBlock(level_accessor, pos_leaves);
                              block_type = buildGetBlockType(entity, gen_type, previous_block, radius, build_area);
                              if (!buildPlaceBlock(level_accessor, level_server, entity, pos_leaves, gen_type, block_type, previous_block, replace)) {
                                 break;
                              }
                           }
                        }
                     } else {
                        if (size < 1.0 && build_saveX == 0.0 && build_saveY == 0.0 && build_saveZ == 0.0) {
                           buildBlockConnector(
                              level_accessor, level_server, entity, build_centerX, build_centerY, build_centerZ, pos, gen_type, radius, build_area, replace
                           );
                        }

                        String previous_block = buildGetPreviousBlock(level_accessor, pos);
                        String block_type = buildGetBlockType(entity, gen_type, previous_block, radius, build_area);
                        if (!buildPlaceBlock(level_accessor, level_server, entity, pos, gen_type, block_type, previous_block, replace)) {
                        }
                     }
                  }
               }
            }
         }
      }

      private static void buildBlockConnector(
         LevelAccessor level_accessor,
         ServerLevel level_server,
         Entity entity,
         double center_posX,
         double center_posY,
         double center_posZ,
         BlockPos pos,
         String gen_type,
         double radius,
         double build_area,
         boolean replace
      ) {
         double block_connector_posX = GameUtils.Data.getEntityNumber(entity, "previous_posX");
         double block_connector_posY = GameUtils.Data.getEntityNumber(entity, "previous_posY");
         double block_connector_posZ = GameUtils.Data.getEntityNumber(entity, "previous_posZ");
         int testX = (int)(Math.floor(center_posX) - Math.floor(block_connector_posX));
         int testY = (int)(Math.floor(center_posY) - Math.floor(block_connector_posY));
         int testZ = (int)(Math.floor(center_posZ) - Math.floor(block_connector_posZ));
         if (Math.abs(testX) == 1 || Math.abs(testY) == 1 || Math.abs(testZ) == 1) {
            if (Math.abs(testX) == 1 && Math.abs(testZ) == 1) {
               if (center_posX - block_connector_posX > center_posZ - block_connector_posZ) {
                  pos = pos.offset(-testX, 0, 0);
               } else {
                  pos = pos.offset(0, 0, -testZ);
               }

               String previous_block = buildGetPreviousBlock(level_accessor, pos);
               String block_type = buildGetBlockType(entity, gen_type, previous_block, radius, build_area);
               if (!buildPlaceBlock(level_accessor, level_server, entity, pos, gen_type, block_type, previous_block, replace)) {
                  return;
               }
            }

            if ((Math.abs(testX) == 1 || Math.abs(testZ) == 1) && Math.abs(testY) == 1) {
               pos = pos.offset(0, -testY, 0);
               String previous_block = buildGetPreviousBlock(level_accessor, pos);
               String block_type = buildGetBlockType(entity, gen_type, previous_block, radius, build_area);
               buildPlaceBlock(level_accessor, level_server, entity, pos, gen_type, block_type, previous_block, replace);
            }
         }
      }

      private static String buildGetBlockType(Entity entity, String gen_type, String previous_block, double radius, double build_area) {
         String block = "";
         if (!gen_type.equals("leaves")) {
            double outer_level = GameUtils.Data.getEntityNumber(entity, gen_type + "_outer_level");
            double inner_level = GameUtils.Data.getEntityNumber(entity, gen_type + "_inner_level");
            double outer_level_area = outer_level;
            double inner_level_area = inner_level;
            if (outer_level < 1.0) {
               outer_level_area = 1.0;
            }

            if (inner_level < 1.0) {
               inner_level_area = 1.0;
            }

            double outer_area = radius - outer_level_area;
            double inner_area = outer_area - inner_level_area;
            if (outer_area > 0.0) {
               outer_area *= outer_area;
            } else {
               outer_area = 0.0;
            }

            if (inner_area > 0.0) {
               inner_area *= inner_area;
            } else {
               inner_area = 0.0;
            }

            if (build_area < inner_area) {
               block = "core";
            } else if (build_area < outer_area) {
               if (!(inner_level >= 1.0) && !(Math.random() < inner_level)) {
                  block = "core";
               } else {
                  block = "inner";
               }
            } else if (outer_level >= 1.0 || Math.random() < outer_level) {
               block = "outer";
            } else if (!(inner_level >= 1.0) && !(Math.random() < inner_level)) {
               block = "core";
            } else {
               block = "inner";
            }

            if (!previous_block.isEmpty()) {
               String type_short = gen_type.substring(0, 2);
               String previous_block_short = previous_block.substring(0, 2);
               boolean is_same_type = type_short.equals(previous_block_short);
               boolean is_core = previous_block.endsWith("c");
               boolean is_blacklist = isBlacklist(type_short, previous_block_short);
               if (block.equals("core")) {
                  if (is_core) {
                     if (is_same_type) {
                        block = "";
                     } else if (is_blacklist) {
                        block = "";
                     }
                  }
               } else if (is_same_type) {
                  if (is_core) {
                     block = "";
                  } else {
                     boolean is_same_type_outer = previous_block.endsWith("o");
                     boolean is_same_type_inner = previous_block.endsWith("i");
                     if (block.equals("outer")) {
                        if (is_same_type_outer || is_same_type_inner) {
                           block = "";
                        }
                     } else if (block.equals("inner") && is_same_type_inner) {
                        block = "";
                     }
                  }
               } else if (is_blacklist) {
                  block = "";
               }
            }
         } else if (Math.random() < GameUtils.Data.getEntityNumber(entity, "leaves2_chance")) {
            block = "2";
         } else {
            block = "1";
         }

         return block;
      }

      private static boolean isBlacklist(String type_short, String previous_block_short) {
         if (type_short.equals("se")) {
            return "ta".contains(previous_block_short);
         } else if (type_short.equals("te")) {
            return "ta/se".contains(previous_block_short);
         } else if (type_short.equals("fi")) {
            return "ta/se/te".contains(previous_block_short);
         } else if (type_short.equals("tr")) {
            return "ta/se/te/fi".contains(previous_block_short);
         } else if (type_short.equals("bo")) {
            return "ta/se/te/fi/tr".contains(previous_block_short);
         } else if (type_short.equals("br")) {
            return "ta/se/te/fi/tr/bo".contains(previous_block_short);
         } else if (type_short.equals("li")) {
            return "ta/se/te/fi/tr/bo/br".contains(previous_block_short);
         } else if (type_short.equals("tw")) {
            return "ta/se/te/fi/tr/bo/br/li".contains(previous_block_short);
         } else {
            return type_short.equals("sp") ? "ta/se/te/fi/tr/bo/br/li/tw".contains(previous_block_short) : false;
         }
      }

      private static String buildGetPreviousBlock(LevelAccessor level_accessor, BlockPos pos) {
         String previous_block = "";
         if (TanshugetreesModVariables.MapVariables.get(level_accessor).shape_file_converter) {
            String key = "B" + pos.getX() + "/" + pos.getY() + "/" + pos.getZ();
            previous_block = ShapeFileConverter.export_data.getOrDefault(key, "");
         } else {
            BlockState block = level_accessor.getBlockState(pos);
            if (!block.canBeReplaced()) {
               previous_block = GameUtils.Tile.toText(block)[0];
               if (previous_block.startsWith("tanshugetrees:block_placer_")) {
                  previous_block = previous_block.substring("tanshugetrees:block_placer_".length());
                  String gen_type = previous_block.substring(0, 2);
                  if (previous_block.endsWith("outer")) {
                     previous_block = "o";
                  } else if (previous_block.endsWith("inner")) {
                     previous_block = "i";
                  } else if (previous_block.endsWith("core")) {
                     previous_block = "c";
                  }

                  previous_block = gen_type + previous_block;
               }
            }
         }

         return previous_block;
      }

      private static boolean buildPlaceBlock(
         LevelAccessor level_accessor,
         ServerLevel level_server,
         Entity entity,
         BlockPos pos,
         String gen_type,
         String block_type,
         String previous_block,
         boolean replace
      ) {
         if (!block_type.isEmpty()) {
            if (!previous_block.isEmpty() && !replace) {
               return false;
            }

            String type_short = gen_type.substring(0, 2) + block_type.charAt(0);
            String block_placer = gen_type + "_" + block_type;
            String block = block_placer;
            if (gen_type.equals("leaves")) {
               block = "leaves" + block_type;
            }

            if (!GameUtils.Data.getEntityText(entity, block).isEmpty()) {
               GameUtils.Misc.spawnParticle(level_server, pos.getCenter(), 0.0, 0.0, 0.0, 0.0, 1, "minecraft:flash");
               String[] function = buildGetWayFunction(entity, gen_type);
               if (!TanshugetreesModVariables.MapVariables.get(level_accessor).shape_file_converter) {
                  GameUtils.Tile.set(level_accessor, pos, GameUtils.Tile.fromText(level_server, "tanshugetrees:block_placer_" + block_placer), false);
                  GameUtils.Data.setBlockText(level_accessor, level_server, pos, "block", GameUtils.Data.getEntityText(entity, block));
                  GameUtils.Data.setBlockText(level_accessor, level_server, pos, "function", function[1]);
                  GameUtils.Data.setBlockText(level_accessor, level_server, pos, "function_style", function[2]);
               } else {
                  String key = pos.getX() + "/" + pos.getY() + "/" + pos.getZ();
                  if (!previous_block.isEmpty() && replace) {
                     ShapeFileConverter.export_data.remove("B" + key);
                  }

                  ShapeFileConverter.export_data.put("B" + key, type_short);
                  if (!function[0].isEmpty()) {
                     if (!previous_block.isEmpty() && replace) {
                        ShapeFileConverter.export_data.remove("F" + key);
                     }

                     ShapeFileConverter.export_data.put("F" + key, function[0]);
                  }
               }

               return true;
            }
         }

         return false;
      }

      private static String[] buildGetWayFunction(Entity entity, String gen_type) {
         String[] return_text = new String[]{"", "", ""};
         String function = "";
         String path = "";
         String at_type = "";
         String style = "";

         for (int number = 1; number <= 9; number++) {
            function = "function_way" + number;
            if (Math.random() < GameUtils.Data.getEntityNumber(entity, function + "_chance")) {
               path = GameUtils.Data.getEntityText(entity, function);
               at_type = GameUtils.Data.getEntityText(entity, function + "_type");
               style = GameUtils.Data.getEntityText(entity, function + "_style");
               if (!path.isEmpty() && at_type.equals(gen_type) && GameUtils.Data.getEntityNumber(entity, function + "_max") >= 0.0) {
                  double range_min = GameUtils.Data.getEntityNumber(entity, function + "_range_min") * 0.01;
                  double range_max = GameUtils.Data.getEntityNumber(entity, function + "_range_max") * 0.01;
                  double length_percent = 1.0;
                  if (GameUtils.Data.getEntityNumber(entity, at_type + "_length_save") > 0.0) {
                     length_percent = 1.0
                        - GameUtils.Data.getEntityNumber(entity, at_type + "_length") / GameUtils.Data.getEntityNumber(entity, at_type + "_length_save");
                  }

                  if (range_min <= length_percent && length_percent <= range_max) {
                     if (GameUtils.Data.getEntityNumber(entity, function + "_max") > 0.0) {
                        GameUtils.Data.addEntityNumber(entity, function + "_max", -1.0);
                        if (GameUtils.Data.getEntityNumber(entity, function + "_max") == 0.0) {
                           GameUtils.Data.setEntityNumber(entity, function + "_max", -1.0);
                        }
                     }

                     return_text[0] = "f" + number;
                     return_text[1] = path;
                     return_text[2] = style;
                     break;
                  }
               }
            }
         }

         return return_text;
      }

      private static void end(LevelAccessor level_accessor, ServerLevel level_server, Entity entity, String id) {
         if (TanshugetreesModVariables.MapVariables.get(level_accessor).shape_file_converter) {
            ShapeFileConverter.whenTreeEnd(level_accessor, level_server, entity);
         } else {
            TXTFunction.run(level_accessor, level_server, entity.blockPosition(), GameUtils.Data.getEntityText(entity, "function_end"), true);
         }

         GameUtils.Mob.summon(
            level_server,
            entity.position().add(20.0, 10.0, 20.0),
            "minecraft:firework_rocket",
            "",
            "",
            "{LifeTime:40,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:2,Explosions:[{Type:4,Flicker:1,Trail:1,Colors:[I;3887386,4312372],FadeColors:[I;3887386,4312372]}]}}}}"
         );
         GameUtils.Mob.summon(
            level_server,
            entity.position().add(20.0, 10.0, -20.0),
            "minecraft:firework_rocket",
            "",
            "",
            "{LifeTime:40,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:2,Explosions:[{Type:4,Flicker:1,Trail:1,Colors:[I;3887386,4312372],FadeColors:[I;3887386,4312372]}]}}}}"
         );
         GameUtils.Mob.summon(
            level_server,
            entity.position().add(-20.0, 10.0, 20.0),
            "minecraft:firework_rocket",
            "",
            "",
            "{LifeTime:40,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:2,Explosions:[{Type:4,Flicker:1,Trail:1,Colors:[I;3887386,4312372],FadeColors:[I;3887386,4312372]}]}}}}"
         );
         GameUtils.Mob.summon(
            level_server,
            entity.position().add(-20.0, 10.0, -20.0),
            "minecraft:firework_rocket",
            "",
            "",
            "{LifeTime:40,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:2,Explosions:[{Type:4,Flicker:1,Trail:1,Colors:[I;3887386,4312372],FadeColors:[I;3887386,4312372]}]}}}}"
         );

         for (Entity scan : GameUtils.Mob.getAtEverywhere(level_server, "", "TANSHUGETREES-" + id)) {
            scan.discard();
         }
      }
   }
}
