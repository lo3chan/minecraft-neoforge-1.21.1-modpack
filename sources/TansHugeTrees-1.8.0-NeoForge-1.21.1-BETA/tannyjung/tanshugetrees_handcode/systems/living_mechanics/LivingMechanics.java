package tannyjung.tanshugetrees_handcode.systems.living_mechanics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import tannyjung.tanshugetrees.network.TanshugetreesModVariables;
import tannyjung.tanshugetrees_core.Core;
import tannyjung.tanshugetrees_core.game.GameUtils;
import tannyjung.tanshugetrees_core.outside.OutsideUtils;
import tannyjung.tanshugetrees_handcode.Handcode;
import tannyjung.tanshugetrees_handcode.systems.Caches;

public class LivingMechanics {
   public static List<Entity> list_tree_location = new ArrayList<>();
   public static List<Entity> list_falling_leaf = new ArrayList<>();
   public static List<Entity> list_leaf_litter_remover = new ArrayList<>();

   public static void start(Entity entity) {
      LevelAccessor level_accessor = entity.level();
      ServerLevel level_server = (ServerLevel)level_accessor;
      BlockPos center_pos = new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
      if (level_server.isLoaded(center_pos)) {
         if (Core.developer_mode) {
            GameUtils.Misc.spawnParticle(level_server, entity.position(), 0.0, 0.0, 0.0, 0.0, 1, "minecraft:flash");
            GameUtils.Misc.spawnParticle(level_server, entity.position().add(0.0, 100.0, 0.0), 0.0, 25.0, 0.0, 0.0, 300, "minecraft:totem_of_undying");
         }

         int[] rotation_mirrored = new int[]{(int)GameUtils.Data.getEntityNumber(entity, "rotation"), (int)GameUtils.Data.getEntityNumber(entity, "mirrored")};
         boolean have_center_block = !level_accessor.getBlockState(center_pos).isAir();
         String path_settings = GameUtils.Data.getEntityText(entity, "tree_settings");
         short[] leaves_types = Caches.TreeSettings.getLeavesType(path_settings);
         Map<Short, BlockState> blocks = Caches.TreeSettings.getBlock(level_server, path_settings);
         Set<Block> leaves = new HashSet<>();
         BlockState get = null;
         get = blocks.get((short)1201);
         if (get != null && !get.isAir()) {
            leaves.add(get.getBlock());
         }

         get = blocks.get((short)1202);
         if (get != null && !get.isAir()) {
            leaves.add(get.getBlock());
         }

         boolean can_leaves_decay = false;
         boolean can_leaves_drop = false;
         boolean can_leaves_regrow = false;
         Map<String, String> data_normal = Caches.TreeSettings.getNormal(path_settings);
         boolean var40 = data_normal.getOrDefault("can_leaves_decay", "false").equals("true");
         can_leaves_drop = data_normal.getOrDefault("can_leaves_drop", "false").equals("true");
         can_leaves_regrow = data_normal.getOrDefault("can_leaves_regrow", "false").equals("true");
         int biome_type = 0;
         Holder<Biome> biome = GameUtils.Environment.getAt(level_accessor, center_pos);
         if (GameUtils.Environment.test(biome, "#tanshugetrees:snowy_biomes")) {
            biome_type = 1;
         } else if (GameUtils.Environment.test(biome, "#tanshugetrees:tropical_biomes")) {
            biome_type = 2;
         }

         String path_storage = "";
         String chosen = "";
         String[] split = GameUtils.Data.getEntityText(entity, "file").split("\\|");

         try {
            var45 = split[0];
            chosen = split[1];
         } catch (Exception var36) {
            OutsideUtils.exception(new Exception(), var36, "");
            return;
         }

         short[] pre_block_data = new short[]{0, 0, 0, 0};
         String[] splitx = GameUtils.Data.getEntityText(entity, "pre_block").split("/");
         if (splitx.length > 1) {
            try {
               pre_block_data[0] = Short.parseShort(splitx[0]);
               pre_block_data[1] = Short.parseShort(splitx[1]);
               pre_block_data[2] = Short.parseShort(splitx[2]);
               pre_block_data[3] = Short.parseShort(splitx[3]);
            } catch (Exception var35) {
               OutsideUtils.exception(new Exception(), var35, "");
            }
         }

         int process = 0;
         int process_save_min = (int)GameUtils.Data.getEntityNumber(entity, "process_save");
         int process_save_max = process_save_min + Handcode.Config.living_mechanics_process_limit;
         BlockPos pre_pos = null;
         BlockState pre_block = null;
         BlockPos pos = null;
         BlockState block = null;
         int leaves_type = 0;
         boolean break_by_process = false;
         int loop = 0;
         short type = 0;
         short posX = 0;
         short posY = 0;
         short posZ = 0;

         for (short scan : Caches.TreeShape.getTreeShapeData(var45 + "|" + chosen)) {
            if (++loop == 1) {
               type = scan;
            } else if (loop == 2) {
               posX = scan;
            } else if (loop == 3) {
               posY = scan;
            } else {
               posZ = scan;
               loop = 0;
            }

            if (loop <= 0) {
               if (Handcode.Config.living_mechanics_process_limit > 0) {
                  if (++process < process_save_min) {
                     continue;
                  }

                  if (process_save_max <= process) {
                     GameUtils.Data.setEntityNumber(entity, "process_save", process);
                     break_by_process = true;
                     break;
                  }
               }

               if (OutsideUtils.Mathematics.isNumberStartWith(type, 1)) {
                  if (!OutsideUtils.Mathematics.isNumberStartWith(type, 120)) {
                     pre_block_data[0] = type;
                     pre_block_data[1] = posX;
                     pre_block_data[2] = posY;
                     pre_block_data[3] = posZ;
                  } else {
                     pre_pos = new BlockPos(pre_block_data[1], pre_block_data[2], pre_block_data[3]);
                     pre_pos = OutsideUtils.convertPosRotationMirrored(pre_pos, rotation_mirrored);
                     pre_pos = pre_pos.offset(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
                     pre_block = blocks.get(pre_block_data[0]);
                     if (pre_block == null) {
                        return;
                     }

                     if (!level_server.isLoaded(pre_pos)) {
                        return;
                     }

                     pos = new BlockPos(posX, posY, posZ);
                     pos = OutsideUtils.convertPosRotationMirrored(pos, rotation_mirrored);
                     pos = pos.offset(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
                     block = blocks.get(type);
                     if (block == null) {
                        return;
                     }

                     if (!level_server.isLoaded(pos)) {
                        return;
                     }

                     if (level_accessor.getBlockState(pre_pos).getBlock() != pre_block.getBlock()) {
                        if (var40 && level_accessor.getBlockState(pos).getBlock() == block.getBlock()) {
                           if (GameUtils.Tile.test(block, "#minecraft:leaves")) {
                              block = GameUtils.Tile.setPropertyLogic(block, "persistent", false);
                              GameUtils.Tile.set(level_accessor, pos, block, false);
                           } else {
                              level_accessor.destroyBlock(pos, false);
                           }
                        }
                     } else if (can_leaves_drop || can_leaves_regrow) {
                        byte var58;
                        if (type == 1201) {
                           var58 = 1;
                        } else {
                           var58 = 2;
                        }

                        update(
                           level_accessor, level_server, entity, leaves, pos, block, var58, biome_type, have_center_block, can_leaves_drop, can_leaves_regrow
                        );
                     }
                  }
               }
            }
         }

         if (break_by_process) {
            GameUtils.Data.setEntityText(entity, "pre_block", pre_block_data[0] + "/" + pre_block_data[1] + "/" + pre_block_data[2] + "/" + pre_block_data[3]);
         } else {
            GameUtils.Data.setEntityNumber(entity, "process_save", 0.0);
            if (GameUtils.Data.getEntityLogic(entity, "test_alive")) {
               GameUtils.Data.setEntityLogic(entity, "test_alive", false);
            } else if (have_center_block) {
               if (leaves_types[0] == 1 && leaves_types[1] == 1) {
                  if (TanshugetreesModVariables.MapVariables.get(level_accessor).season.equals("Summer")) {
                     entity.discard();
                  }
               } else if (Math.random() < 0.1) {
                  entity.discard();
               }
            } else {
               entity.discard();
            }
         }

         return;
      }
   }

   private static void update(
      LevelAccessor level_accessor,
      ServerLevel level_server,
      Entity entity,
      Set<Block> leaves,
      BlockPos pos,
      BlockState block,
      int leaves_type,
      int biome_type,
      boolean have_center_block,
      boolean can_leaves_drop,
      boolean can_leaves_regrow
   ) {
      BlockState block_test = level_accessor.getBlockState(pos);
      boolean is_leaves = leaves.contains(block_test.getBlock());
      boolean straighten = false;
      boolean can_pos_photosynthesis = false;
      if (GameUtils.Data.getEntityNumber(entity, "straighten_highestX") == pos.getX()
         && !(GameUtils.Data.getEntityNumber(entity, "straighten_highestY") < pos.getY())
         && GameUtils.Data.getEntityNumber(entity, "straighten_highestZ") == pos.getZ()) {
         straighten = true;
      } else {
         GameUtils.Data.setEntityNumber(entity, "straighten_highestX", pos.getX());
         GameUtils.Data.setEntityNumber(entity, "straighten_highestY", pos.getY());
         GameUtils.Data.setEntityNumber(entity, "straighten_highestZ", pos.getZ());
      }

      if (Handcode.Config.leaf_light_level_detection <= level_accessor.getBrightness(LightLayer.SKY, pos) + 1) {
         can_pos_photosynthesis = true;
      }

      if (is_leaves) {
         if (can_leaves_drop) {
            double chance = 0.0;
            if (straighten) {
               if (!leaves.contains(level_accessor.getBlockState(pos.atY((int)GameUtils.Data.getEntityNumber(entity, "straighten_highestY"))).getBlock())) {
                  chance = 1.0;
               }
            } else if (!can_pos_photosynthesis || !have_center_block) {
               chance = Handcode.Config.dead_leaf_drop_chance;
            } else if (leaves_type == 1) {
               if (biome_type == 0) {
                  String height_motion = TanshugetreesModVariables.MapVariables.get(level_accessor).season;

                  chance = switch (height_motion) {
                     case "Spring" -> Handcode.Config.leaf_drop_chance_spring;
                     case "Summer" -> Handcode.Config.leaf_drop_chance_summer;
                     case "Autumn" -> Handcode.Config.leaf_drop_chance_autumn;
                     case "Winter" -> Handcode.Config.leaf_drop_chance_winter;
                     default -> chance;
                  };
               } else if (biome_type == 1) {
                  chance = Handcode.Config.leaf_drop_chance_winter;
               } else if (biome_type == 2) {
                  chance = Handcode.Config.leaf_drop_chance_summer;
               }
            } else if (leaves_type == 2) {
               if (TanshugetreesModVariables.MapVariables.get(level_accessor).season.equals("Summer")) {
                  chance = Handcode.Config.leaf_drop_chance_coniferous;
               }
            } else {
               chance = Handcode.Config.leaf_drop_chance_summer;
            }

            if (Math.random() < chance) {
               GameUtils.Tile.remove(level_accessor, level_server, pos, false);
               if (Handcode.Config.leaf_litter) {
                  if (Math.random() < Handcode.Config.falling_leaf_chance) {
                     if (list_falling_leaf.size() < Handcode.Config.falling_leaf_count_limit
                        && GameUtils.Tile.test(level_accessor.getBlockState(pos.below()), "#tanshugetrees:passable_blocks")) {
                        Entity entity_summon = GameUtils.Misc.summonBlock(
                           level_server,
                           pos.getCenter(),
                           "Falling Leaf",
                           "TANSHUGETREES-falling_leaf",
                           0.0,
                           0.0,
                           0.0,
                           1.0,
                           1.0,
                           1.0,
                           0,
                           0,
                           GameUtils.Tile.toText(block)[0]
                        );
                        GameUtils.Data.setEntityText(entity_summon, "block", GameUtils.Tile.toText(block)[0] + GameUtils.Tile.toText(block)[1]);
                     }
                  } else {
                     int height_motion = GameUtils.Space.getHeight(level_accessor, pos.getX(), pos.getZ(), "MOTION_BLOCKING_NO_LEAVES");
                     if (height_motion != GameUtils.Space.getBuildHeight(level_accessor, false) && height_motion < pos.getY()) {
                        LeafLitter.create(level_accessor, level_server, pos.atY(height_motion), block, false);
                     }
                  }
               }
            }
         }
      } else if (have_center_block && block_test.isAir() && can_leaves_regrow) {
         double chancex = 0.0;
         if (straighten) {
            if (leaves.contains(level_accessor.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())))) {
               chancex = 1.0;
            }
         } else if (can_pos_photosynthesis) {
            if (leaves_type == 1) {
               if (biome_type == 0) {
                  String var22 = TanshugetreesModVariables.MapVariables.get(level_accessor).season;

                  chancex = switch (var22) {
                     case "Spring" -> Handcode.Config.leaf_regrowth_chance_spring;
                     case "Summer" -> Handcode.Config.leaf_regrowth_chance_summer;
                     case "Autumn" -> Handcode.Config.leaf_regrowth_chance_autumn;
                     case "Winter" -> Handcode.Config.leaf_regrowth_chance_winter;
                     default -> chancex;
                  };
               } else if (biome_type == 1) {
                  chancex = Handcode.Config.leaf_regrowth_chance_winter;
               } else if (biome_type == 2) {
                  chancex = Handcode.Config.leaf_regrowth_chance_summer;
               }
            } else if (leaves_type == 2) {
               chancex = Handcode.Config.leaf_regrowth_chance_coniferous;
            } else {
               chancex = Handcode.Config.leaf_regrowth_chance_summer;
            }
         }

         if (Math.random() < chancex) {
            GameUtils.Tile.set(level_accessor, pos, block, false);
         }
      }

      if (Handcode.Config.leaf_litter
         && Math.random() < Handcode.Config.leaf_litter_remover_chance
         && list_leaf_litter_remover.size() < Handcode.Config.leaf_litter_remover_count_limit) {
         GameUtils.Mob.summon(
            level_server,
            pos.getCenter(),
            "minecraft:marker",
            "Leaf Litter Remover",
            "TANSHUGETREES-leaf_litter_remover",
            "{NeoForgeData:{tanshugetrees:{block:\"" + GameUtils.Tile.toText(block)[0] + "\"}}}"
         );
      }

      if (have_center_block && is_leaves) {
         GameUtils.Data.setEntityLogic(entity, "test_alive", true);
      }
   }

   private static class CustomEntityUpdate {
      public static void runDrop(Entity entity) {
         LevelAccessor level_accessor = entity.level();
         ServerLevel level_server = (ServerLevel)level_accessor;
         if (level_server.isLoaded(entity.blockPosition())) {
            BlockPos pos = BlockPos.containing(entity.position().add(0.0, -0.6, 0.0));
            boolean is_passable = GameUtils.Tile.isPassable(level_accessor, pos)
               || GameUtils.Tile.test(level_accessor.getBlockState(pos), "#tanshugetrees:passable_blocks");
            if (is_passable && !level_accessor.isWaterAt(pos)) {
               entity.setPos(entity.getX(), entity.getY() - 0.1, entity.getZ());
            } else {
               LeafLitter.create(
                  level_server, level_server, pos.above(), GameUtils.Tile.fromText(level_server, GameUtils.Data.getEntityText(entity, "block")), false
               );
               entity.discard();
            }
         }
      }

      public static void runLitterRemover(Entity entity) {
         LevelAccessor level_accessor = entity.level();
         ServerLevel level_server = (ServerLevel)level_accessor;
         if (level_server.isLoaded(entity.blockPosition())) {
            BlockPos pos = entity.blockPosition().below();
            boolean is_passable = GameUtils.Tile.isPassable(level_accessor, pos)
               || GameUtils.Tile.test(level_accessor.getBlockState(pos), "#tanshugetrees:passable_blocks");
            if (is_passable && !level_accessor.isWaterAt(pos)) {
               entity.setPos(entity.getX(), entity.getY() - 1.0, entity.getZ());
            } else {
               LeafLitter.create(
                  level_server, level_server, pos.above(), GameUtils.Tile.fromText(level_server, GameUtils.Data.getEntityText(entity, "block")), true
               );
               entity.discard();
            }
         }
      }
   }

   public static class Loop {
      private static int tick = 0;

      public static void runTick() {
         if (Handcode.Config.living_mechanics) {
            if (!LivingMechanics.list_tree_location.isEmpty()) {
               tick++;
               if (tick >= Handcode.Config.living_mechanics_tick) {
                  tick = 0;
                  if (Math.random() < (double)LivingMechanics.list_tree_location.size() / Handcode.Config.living_mechanics_simulation) {
                     LivingMechanics.start(
                        LivingMechanics.list_tree_location.get(Mth.nextInt(RandomSource.create(), 0, LivingMechanics.list_tree_location.size() - 1))
                     );
                  }
               }
            }

            if (!LivingMechanics.list_falling_leaf.isEmpty()) {
               for (Entity entity : LivingMechanics.list_falling_leaf) {
                  if (!entity.isRemoved()) {
                     LivingMechanics.CustomEntityUpdate.runDrop(entity);
                  }
               }
            }

            if (!LivingMechanics.list_leaf_litter_remover.isEmpty()) {
               for (Entity entityx : LivingMechanics.list_leaf_litter_remover) {
                  if (!entityx.isRemoved()) {
                     LivingMechanics.CustomEntityUpdate.runLitterRemover(entityx);
                  }
               }
            }
         }
      }

      public static void runSecond(ServerLevel level_server) {
         if (Handcode.Config.living_mechanics) {
            LivingMechanics.list_tree_location = GameUtils.Mob.getAtEverywhere(level_server, "minecraft:marker", "TANSHUGETREES-tree_location");
            LivingMechanics.list_falling_leaf = GameUtils.Mob.getAtEverywhere(level_server, "minecraft:block_display", "TANSHUGETREES-falling_leaf");
            LivingMechanics.list_leaf_litter_remover = GameUtils.Mob.getAtEverywhere(level_server, "minecraft:marker", "TANSHUGETREES-leaf_litter_remover");
         }
      }
   }
}
