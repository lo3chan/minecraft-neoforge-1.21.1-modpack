package tannyjung.tanshugetrees_core.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;
import net.neoforged.fml.ModList;
import tannyjung.tanshugetrees.init.TanshugetreesModMenus;
import tannyjung.tanshugetrees_core.Core;
import tannyjung.tanshugetrees_core.outside.CacheManager;
import tannyjung.tanshugetrees_core.outside.FileManager;
import tannyjung.tanshugetrees_core.outside.OutsideUtils;

public class GameUtils {
   public static class Command {
      public static void run(ServerLevel level_server, Vec3 vec3, String command) {
         level_server.getServer()
            .getCommands()
            .performPrefixedCommand(
               new CommandSourceStack(CommandSource.NULL, vec3, Vec2.ZERO, level_server, 4, "", Component.literal(""), level_server.getServer(), null)
                  .withSuppressedOutput(),
               command
            );
      }

      public static void runEntity(Entity entity, String command) {
         if (entity.level() instanceof ServerLevel level_server) {
            level_server.getServer()
               .getCommands()
               .performPrefixedCommand(
                  new CommandSourceStack(
                     CommandSource.NULL,
                     entity.position(),
                     entity.getRotationVector(),
                     level_server,
                     4,
                     entity.getName().getString(),
                     entity.getDisplayName(),
                     level_server.getServer(),
                     entity
                  ),
                  command
               );
         }
      }

      public static boolean result(ServerLevel level_server, Vec3 vec3, String command) {
         final StringBuilder result = new StringBuilder();
         CommandSource data_consumer = new CommandSource() {
            public boolean acceptsSuccess() {
               result.append("pass");
               return true;
            }

            public void sendSystemMessage(Component component) {
            }

            public boolean acceptsFailure() {
               return false;
            }

            public boolean shouldInformAdmins() {
               return false;
            }
         };
         level_server.getServer()
            .getCommands()
            .performPrefixedCommand(
               new CommandSourceStack(data_consumer, vec3, Vec2.ZERO, level_server, 4, "", Component.literal(""), level_server.getServer(), null), command
            );
         return result.toString().equals("pass");
      }

      public static boolean resultEntity(Entity entity, String command) {
         final StringBuilder result = new StringBuilder();
         CommandSource data_consumer = new CommandSource() {
            public boolean acceptsSuccess() {
               result.append("pass");
               return true;
            }

            public void sendSystemMessage(Component component) {
            }

            public boolean acceptsFailure() {
               return false;
            }

            public boolean shouldInformAdmins() {
               return false;
            }
         };
         if (entity.level() instanceof ServerLevel level_server) {
            level_server.getServer()
               .getCommands()
               .performPrefixedCommand(
                  new CommandSourceStack(
                     data_consumer,
                     entity.position(),
                     entity.getRotationVector(),
                     level_server,
                     4,
                     entity.getName().getString(),
                     entity.getDisplayName(),
                     level_server.getServer(),
                     entity
                  ),
                  command
               );
         }

         return result.toString().equals("pass");
      }
   }

   public static class Data {
      public static CompoundTag convertJSONToTag(String data) {
         try {
            return TagParser.parseTag(data);
         } catch (Exception var2) {
            return new CompoundTag();
         }
      }

      public static MutableComponent convertJSONToComponent(String data) {
         try {
            return Serializer.fromJson(data, RegistryAccess.EMPTY);
         } catch (Exception var2) {
            OutsideUtils.exception(new Exception(), var2, "");
            return null;
         }
      }

      public static String convertFileToForgeData(String path) {
         StringBuilder data = new StringBuilder();
         String[] split = null;
         Set<String> normal = new HashSet<>();
         normal.add("-");
         normal.add("0");
         normal.add("1");
         normal.add("2");
         normal.add("3");
         normal.add("4");
         normal.add("5");
         normal.add("6");
         normal.add("7");
         normal.add("8");
         normal.add("9");

         for (String scan : FileManager.readTXT(path)) {
            if (!scan.isEmpty() && !scan.startsWith("---")) {
               split = scan.split(" = ");
               if (!split[1].isEmpty() && !split[1].equals("none")) {
                  if (normal.contains(split[1].substring(0, 1))) {
                     data.append(split[0]).append(":").append(split[1]);
                  } else if (!split[1].equals("true") && !split[1].equals("false")) {
                     data.append(split[0]).append(":\"").append(split[1]).append("\"");
                  } else {
                     data.append(split[0]).append(":").append(split[1]);
                  }

                  data.append(",");
               }
            }
         }

         return "{NeoForgeData:{" + Core.mod_id + ":{" + data + "}}}";
      }

      public static String createText(String data) {
         StringBuilder convert = new StringBuilder();
         String[] split = null;
         boolean first = false;

         for (String scan : data.split(" \\| ")) {
            if (!first) {
               first = true;
            } else {
               convert.append(",");
            }

            split = scan.split(" / ");
            if (split.length == 1) {
               convert.append("{\"text\":\"");
               convert.append(split[0]);
               convert.append("\",\"color\":\"white\"");
            } else {
               convert.append("{\"text\":\"");
               convert.append(split[0]);
               convert.append("\",\"color\":\"");
               convert.append(split[1]);
               convert.append("\"");
               if (split.length == 3) {
                  if (split[2].startsWith("https")) {
                     convert.append(",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"");
                     convert.append(split[2]);
                     convert.append("\"}");
                  } else if (split[2].startsWith("/")) {
                     convert.append(",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"");
                     convert.append(split[2]);
                     convert.append("\"}");
                  }

                  convert.append(",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"");
                  convert.append(split[2]);
                  convert.append("\"}");
               }
            }

            convert.append("}");
         }

         return convert.toString();
      }

      public static String createTextDoubleBackslash(String data) {
         return createText(data).replace("\"", "\\\"");
      }

      public static String createItem(String name, String lore, String custom_data, String forge_data) {
         String part_name = "custom_name:\"" + createTextDoubleBackslash(name) + "\",";
         String part_lore = "lore:[\"" + createTextDoubleBackslash(lore) + "\"],";
         String part_custom_data = "custom_data:{" + custom_data + "},";
         String part_forge_data = "block_entity_data:{id:\"\",NeoForgeData:{" + Core.mod_id + ":{" + forge_data + "}}},";
         StringBuilder write = new StringBuilder();
         if (!name.isEmpty()) {
            write.append(part_name);
         }

         if (!lore.isEmpty()) {
            write.append(part_lore);
         }

         if (!custom_data.isEmpty()) {
            write.append(part_custom_data);
         }

         if (!forge_data.isEmpty()) {
            write.append(part_forge_data);
         }

         return write.toString();
      }

      public static String getEntityText(Entity entity, String name) {
         return entity.getPersistentData().getCompound(Core.mod_id).getString(name);
      }

      public static Boolean getEntityLogic(Entity entity, String name) {
         return entity.getPersistentData().getCompound(Core.mod_id).getBoolean(name);
      }

      public static double getEntityNumber(Entity entity, String name) {
         return entity.getPersistentData().getCompound(Core.mod_id).getDouble(name);
      }

      public static double[] getEntityListNumber(Entity entity, String name) {
         ListTag list = entity.getPersistentData().getCompound(Core.mod_id).getList(name, 6);
         double[] convert = new double[list.size()];

         for (int count = 0; count <= list.size() - 1; count++) {
            convert[count] = list.getDouble(count);
         }

         return convert;
      }

      public static double[] getEntityListNumberFloat(Entity entity, String name) {
         ListTag list = entity.getPersistentData().getCompound(Core.mod_id).getList(name, 5);
         double[] convert = new double[list.size()];

         for (int count = 0; count <= list.size() - 1; count++) {
            convert[count] = list.getFloat(count);
         }

         return convert;
      }

      public static void setEntityText(Entity entity, String name, String value) {
         CompoundTag tag = new CompoundTag();
         CompoundTag tag_add = new CompoundTag();
         tag_add.putString(name, value);
         tag.put(Core.mod_id, tag_add);
         entity.getPersistentData().merge(tag);
      }

      public static void setEntityLogic(Entity entity, String name, boolean value) {
         CompoundTag tag = new CompoundTag();
         CompoundTag tag_add = new CompoundTag();
         tag_add.putBoolean(name, value);
         tag.put(Core.mod_id, tag_add);
         entity.getPersistentData().merge(tag);
      }

      public static void setEntityNumber(Entity entity, String name, double value) {
         CompoundTag tag = new CompoundTag();
         CompoundTag tag_add = new CompoundTag();
         tag_add.putDouble(name, value);
         tag.put(Core.mod_id, tag_add);
         entity.getPersistentData().merge(tag);
      }

      public static void addEntityNumber(Entity entity, String name, double value) {
         entity.getPersistentData().getCompound(Core.mod_id).putDouble(name, entity.getPersistentData().getCompound(Core.mod_id).getDouble(name) + value);
      }

      public static String getBlockText(LevelAccessor level_accessor, BlockPos pos, String name) {
         return (new Object() {
            public String getValue(LevelAccessor level_accessor, BlockPos posx, String namex) {
               BlockEntity block_entity = level_accessor.getBlockEntity(posx);
               return block_entity == null ? "" : block_entity.getPersistentData().getCompound(Core.mod_id).getString(namex);
            }
         }).getValue(level_accessor, pos, name);
      }

      public static double getBlockNumber(LevelAccessor level_accessor, BlockPos pos, String name) {
         return (new Object() {
            public double getValue(LevelAccessor level_accessor, BlockPos posx, String namex) {
               BlockEntity block_entity = level_accessor.getBlockEntity(posx);
               return block_entity == null ? 0.0 : block_entity.getPersistentData().getCompound(Core.mod_id).getDouble(namex);
            }
         }).getValue(level_accessor, pos, name);
      }

      public static boolean getBlockLogic(LevelAccessor level_accessor, BlockPos pos, String name) {
         return (new Object() {
            public boolean getValue(LevelAccessor level_accessor, BlockPos posx, String namex) {
               BlockEntity blockEntity = level_accessor.getBlockEntity(posx);
               return blockEntity != null ? blockEntity.getPersistentData().getCompound(Core.mod_id).getBoolean(namex) : false;
            }
         }).getValue(level_accessor, pos, name);
      }

      public static void setBlockText(LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, String name, String value) {
         BlockEntity block_entity = level_accessor.getBlockEntity(pos);
         if (block_entity != null) {
            CompoundTag tag = new CompoundTag();
            CompoundTag tag_add = new CompoundTag();
            tag_add.putString(name, value);
            tag.put(Core.mod_id, tag_add);
            block_entity.getPersistentData().merge(tag);
            BlockState block = level_accessor.getBlockState(pos);
            level_server.sendBlockUpdated(pos, block, block, 2);
         }
      }

      public static void setBlockLogic(LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, String name, boolean value) {
         BlockEntity block_entity = level_accessor.getBlockEntity(pos);
         if (block_entity != null) {
            CompoundTag tag = new CompoundTag();
            CompoundTag tag_add = new CompoundTag();
            tag_add.putBoolean(name, value);
            tag.put(Core.mod_id, tag_add);
            block_entity.getPersistentData().merge(tag);
            BlockState block = level_accessor.getBlockState(pos);
            level_server.sendBlockUpdated(pos, block, block, 2);
         }
      }

      public static void setBlockNumber(LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, String name, double value) {
         BlockEntity block_entity = level_accessor.getBlockEntity(pos);
         if (block_entity != null) {
            CompoundTag tag = new CompoundTag();
            CompoundTag tag_add = new CompoundTag();
            tag_add.putDouble(name, value);
            tag.put(Core.mod_id, tag_add);
            block_entity.getPersistentData().merge(tag);
            BlockState block = level_accessor.getBlockState(pos);
            level_server.sendBlockUpdated(pos, block, block, 2);
         }
      }

      public static void addBlockNumber(LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, String name, double value) {
         BlockEntity block_entity = level_accessor.getBlockEntity(pos);
         if (block_entity != null) {
            block_entity.getPersistentData()
               .getCompound(Core.mod_id)
               .putDouble(name, block_entity.getPersistentData().getCompound(Core.mod_id).getDouble(name) + value);
            BlockState block = level_accessor.getBlockState(pos);
            level_server.sendBlockUpdated(pos, block, block, 2);
         }
      }

      public static String getItemText(Entity entity, EquipmentSlot slot, String name) {
         return ((CustomData)GameUtils.Item.getSlot(entity, slot).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY))
            .copyTag()
            .getCompound(Core.mod_id)
            .getString(name);
      }

      public static void setItemText(Entity entity, EquipmentSlot slot, String name, String value) {
         CompoundTag tag = new CompoundTag();
         CompoundTag tag_add = new CompoundTag();
         tag_add.putString(name, value);
         tag.put(Core.mod_id, tag_add);
         CustomData.update(DataComponents.CUSTOM_DATA, GameUtils.Item.getSlot(entity, slot), create -> create.merge(tag));
      }
   }

   public static class Environment {
      public static boolean test(Holder<Biome> biome, String test) {
         if (test.isEmpty()) {
            return false;
         } else if (test.equals("all")) {
            return true;
         } else {
            String key = biome + " -> " + test;
            if (!CacheManager.DataLogic.existNormal("test_biome", key)) {
               boolean result = false;
               String biome_centerID = toID(biome);

               for (String split : test.split(" / ")) {
                  result = true;

                  for (String split2 : split.split(", ")) {
                     String split_get = split2.replaceAll("[#!]", "");
                     if (split2.startsWith("#") || split2.startsWith("!#")) {
                        try {
                           if (!biome.is(TagKey.create(Registries.BIOME, ResourceLocation.parse(split_get)))) {
                              result = false;
                           }
                        } catch (Exception var15) {
                           result = false;
                        }
                     } else if (!biome_centerID.equals(split_get)) {
                        result = false;
                     }

                     if (split2.startsWith("!")) {
                        result = !result;
                     }

                     if (!result) {
                        break;
                     }
                  }

                  if (result) {
                     break;
                  }
               }

               CacheManager.DataLogic.setNormal("test_biome", key, result);
            }

            Map<String, Boolean> test_biome = CacheManager.DataLogic.getNormal("test_biome");
            if (test_biome == null) {
               Core.logger.error("No Main -----> " + key);
            }

            if (test_biome.get(key) == null) {
               Core.logger.error("No Key -----> " + key);
            }

            return test_biome.get(key);
         }
      }

      public static String toID(Holder<Biome> biome) {
         String return_text = biome.toString().replace("Reference{ResourceKey[minecraft:worldgen/biome / ", "");
         return return_text.substring(0, return_text.indexOf("]"));
      }

      public static Holder<Biome> getAt(LevelAccessor level_accessor, BlockPos pos) {
         ChunkPos chunk_pos = new ChunkPos(pos);
         return GameUtils.Space.testChunkStatus(level_accessor, chunk_pos, "biomes")
            ? level_accessor.getChunk(chunk_pos.x, chunk_pos.z).getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2)
            : level_accessor.getUncachedNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2);
      }
   }

   public static class GUI {
      public static String getTextBox(Player player, String name) {
         return player.containerMenu instanceof TanshugetreesModMenus.MenuAccessor menu ? menu.getMenuState(0, name, "") : "";
      }

      public static void setTextBox(Player player, String name, String value) {
         if (player.containerMenu instanceof TanshugetreesModMenus.MenuAccessor menu) {
            menu.sendMenuStateUpdate(player, 0, name, value, true);
         }
      }
   }

   public static class Item {
      public static ItemStack getSlot(Entity entity, EquipmentSlot equipment_slot) {
         return entity instanceof LivingEntity living_entity ? living_entity.getItemBySlot(equipment_slot) : ItemStack.EMPTY;
      }

      public static boolean isTaggedAs(ItemStack item, String tag) {
         return item.is(ItemTags.create(ResourceLocation.parse(tag)));
      }

      public static void setCount(Entity entity, EquipmentSlot equipment_slot, int value) {
         if (entity instanceof LivingEntity living_entity) {
            ItemStack item = living_entity.getItemBySlot(equipment_slot);
            item.setCount(value);
         }
      }

      public static void addCount(Entity entity, EquipmentSlot equipment_slot, int value) {
         if (entity instanceof LivingEntity living_entity) {
            ItemStack item = living_entity.getItemBySlot(equipment_slot);
            item.setCount(item.getCount() + value);
         }
      }

      public static void setCooldown(Entity entity, ItemStack item, int tick) {
         if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(item.getItem(), tick);
         }
      }

      public static void addDamage(ItemStack item, int value) {
         item.setDamageValue(item.getDamageValue() + value);
         if (item.getMaxDamage() < item.getDamageValue()) {
            item.shrink(1);
         }
      }

      public static void spawn(ServerLevel level_server, Vec3 vec3, ItemStack item) {
         ItemEntity entityToSpawn = new ItemEntity(level_server, vec3.x, vec3.y, vec3.z, item);
         level_server.addFreshEntity(entityToSpawn);
      }

      public static String toID(ItemStack item) {
         String id = item.getDescriptionId();
         return id.substring(id.indexOf(".") + 1).replace(".", ":");
      }

      public static ItemStack fromID(ServerLevel level_server, String id) {
         return ((net.minecraft.world.item.Item)level_server.registryAccess().registryOrThrow(Registries.ITEM).get(ResourceLocation.parse(id)))
            .getDefaultInstance();
      }
   }

   public static class Misc {
      public static boolean isModLoaded(String id) {
         return ModList.get().isLoaded(id);
      }

      public static String testVariant(String test) {
         if (!test.isEmpty()) {
            String[] split = null;

            for (String variant : test.split(" \\| ")) {
               split = variant.split(" / ");
               if (Math.random() < Double.parseDouble(split[0])) {
                  return split[1];
               }
            }
         }

         return "";
      }

      public static void sendChatMessage(ServerLevel level_server, String data) {
         String[] split = data.split(" \\| ")[0].split(" / ");
         String prefix_color = "white";
         if (split.length > 1) {
            prefix_color = split[1];
         }

         GameUtils.Command.run(
            level_server,
            Vec3.ZERO,
            "tellraw @a [{\"text\":\"\"},"
               + GameUtils.Data.createText(
                  "[" + Core.mod_id_short + "] / " + prefix_color + " / This message was sent from " + Core.mod_name + " mod (Global) |   | " + data
               )
               + "]"
         );
      }

      public static void sendChatMessagePrivate(Player player, String data) {
         String[] split = data.split(" \\| ")[0].split(" / ");
         String prefix_color = "white";
         if (split.length > 1) {
            prefix_color = split[1];
         }

         GameUtils.Command.runEntity(
            player,
            "tellraw @s [{\"text\":\"\"},"
               + GameUtils.Data.createText(
                  "[" + Core.mod_id_short + "] / " + prefix_color + " / This message was sent from " + Core.mod_name + " mod (Private) |   | " + data
               )
               + "]"
         );
      }

      public static void spawnParticle(ServerLevel level_server, Vec3 vec3, double spreadX, double spreadY, double spreadZ, double speed, int count, String id) {
         ParticleType<?> particle = (ParticleType<?>)level_server.registryAccess().registryOrThrow(Registries.PARTICLE_TYPE).get(ResourceLocation.parse(id));
         if (particle != null) {
            for (ServerPlayer player : level_server.players()) {
               level_server.sendParticles(player, (ParticleOptions)particle, true, vec3.x, vec3.y, vec3.z, count, spreadX, spreadY, spreadZ, speed);
            }
         }
      }

      public static void playSound(ServerLevel level_server, BlockPos pos, double volume, double pitch, String id) {
         SoundEvent sound = (SoundEvent)level_server.registryAccess().registryOrThrow(Registries.SOUND_EVENT).get(ResourceLocation.parse(id));
         if (sound != null) {
            level_server.playSound(null, pos, sound, SoundSource.NEUTRAL, (float)volume, (float)pitch);
         }
      }

      public static Entity summonText(ServerLevel level_server, Vec3 vec3, String tag, double size, String data) {
         return GameUtils.Mob.summon(
            level_server,
            vec3,
            "minecraft:text_display",
            "Display Text",
            Core.mod_id_big + "-display_text / " + tag,
            "{billboard:vertical,alignment:\"center\",see_through:true,brightness:{block:15, sky:15},text_opacity:0,line_width:1000,transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:["
               + size
               + "f,"
               + size
               + "f,"
               + size
               + "f]},text:'"
               + GameUtils.Data.createText(data)
               + "'}"
         );
      }

      public static Entity summonTextTemporary(ServerLevel level_server, Vec3 vec3, String tag, double size, String data) {
         Entity entity = summonText(level_server, vec3, tag, size, data);
         Core.DelayedWork.create(false, 200, () -> {
            for (Entity scan : GameUtils.Mob.getAtArea(level_server, vec3, 1, true, 0, "minecraft:text_display", Core.mod_id_big + "-display_text")) {
               scan.discard();
            }
         });
         return entity;
      }

      public static Entity summonBlock(
         ServerLevel level_server,
         Vec3 vec3,
         String name,
         String tag,
         double offsetX,
         double offsetY,
         double offsetZ,
         double sizeX,
         double sizeY,
         double sizeZ,
         int rotate_horizontal,
         int rotate_vertical,
         String id
      ) {
         offsetX -= sizeX / 2.0;
         offsetZ -= sizeZ / 2.0;
         offsetY -= 0.5;
         return GameUtils.Mob.summon(
            level_server,
            vec3,
            "minecraft:block_display",
            name,
            tag,
            "{transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],translation:["
               + offsetX
               + "f,"
               + offsetY
               + "f,"
               + offsetZ
               + "f],scale:["
               + sizeX
               + "f,"
               + sizeY
               + "f,"
               + sizeZ
               + "f]},Rotation:["
               + rotate_horizontal
               + "f,"
               + rotate_vertical
               + "f],block_state:{Name:\""
               + id
               + "\"}}"
         );
      }
   }

   public static class Mob {
      public static List<Entity> getAtArea(ServerLevel level_server, Vec3 vec3, int distance, boolean is_box, int count, String id, String tag) {
         List<String> tags = Arrays.stream(tag.split(" / ")).toList();
         List<Entity> entities = level_server.getEntitiesOfClass(
            Entity.class,
            new AABB(vec3, vec3).inflate(distance),
            entity -> {
               boolean test = false;
               if ((is_box || entity.position().distanceTo(vec3) <= distance)
                  && (id.isEmpty() || EntityType.getKey(entity.getType()).toString().equals(id))
                  && (tag.isEmpty() || entity.getTags().containsAll(tags))) {
                  test = true;
               }

               return test;
            }
         );
         if (distance > 0) {
            entities = entities.stream().sorted(Comparator.comparingDouble(entity -> entity.position().distanceTo(vec3))).toList();
         }

         if (count > 0 && entities.size() > count) {
            entities = entities.subList(0, count);
         }

         return entities;
      }

      public static List<Entity> getAtEverywhere(ServerLevel level_server, String id, String tag) {
         List<Entity> entities = new ArrayList<>();
         List<String> tags = Arrays.stream(tag.split(" / ")).toList();
         level_server.getAllEntities().forEach(entity -> {
            if ((id.isEmpty() || EntityType.getKey(entity.getType()).toString().equals(id)) && (tag.isEmpty() || entity.getTags().containsAll(tags))) {
               entities.add(entity);
            }
         });
         return entities;
      }

      public static Entity getAtAreaOne(ServerLevel level_server, Vec3 vec3, int distance, boolean is_box, String id, String tag) {
         List<Entity> entities = getAtArea(level_server, vec3, distance, is_box, 1, id, tag);
         return !entities.isEmpty() ? entities.get(0) : null;
      }

      public static Entity getAtEverywhereOne(ServerLevel level_server, String id, String tag) {
         List<Entity> entities = getAtEverywhere(level_server, id, tag);
         return !entities.isEmpty() ? entities.get(0) : null;
      }

      public static Entity summon(ServerLevel level_server, Vec3 vec3, String id, String name, String tag, String custom) {
         EntityType<?> type = (EntityType<?>)level_server.registryAccess().registryOrThrow(Registries.ENTITY_TYPE).get(ResourceLocation.parse(id));
         if (type == null) {
            return null;
         } else {
            Entity entity = type.create(level_server);
            if (entity == null) {
               return null;
            } else {
               if (!custom.isEmpty()) {
                  entity.load(GameUtils.Data.convertJSONToTag(custom));
               }

               MutableComponent component = GameUtils.Data.convertJSONToComponent("[" + GameUtils.Data.createText(name) + "]");
               if (component == null) {
                  return null;
               } else {
                  entity.setCustomName(component);
                  if (name.contains(" / ")) {
                     entity.setCustomNameVisible(true);
                  }

                  entity.addTag("TANNYJUNG");
                  entity.addTag(Core.mod_id_big);

                  for (String get : tag.split(" / ")) {
                     entity.addTag(get);
                  }

                  entity.setPos(vec3);
                  level_server.addFreshEntity(entity);
                  return entity;
               }
            }
         }
      }

      public static void summonWorldGen(ServerLevel level_server, Vec3 vec3, String id, String name, String tag, String custom) {
         level_server.getServer().execute(() -> summon(level_server, vec3, id, name, tag, custom));
      }

      public static boolean isCreativeMode(Entity entity) {
         return entity instanceof Player player ? player.getAbilities().instabuild : false;
      }

      public static boolean isSneaking(Entity entity) {
         return entity instanceof Player player ? player.isShiftKeyDown() : false;
      }
   }

   public static class Score {
      public static void create(ServerLevel level_server, String name) {
         Scoreboard scoreboard = level_server.getServer().getScoreboard();
         Objective objective = scoreboard.getObjective(name);
         if (objective == null) {
            scoreboard.addObjective(name, ObjectiveCriteria.DUMMY, Component.literal(name), RenderType.INTEGER, true, null);
         }
      }

      public static int get(ServerLevel level_server, String objective, String player) {
         ServerScoreboard score = level_server.getServer().getScoreboard();
         Objective objective_test = score.getObjective(objective);
         return objective_test == null ? 0 : score.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player), objective_test, false).get();
      }

      public static void set(ServerLevel level_server, String objective, String player, int value) {
         ServerScoreboard score = level_server.getServer().getScoreboard();
         Objective objective_test = score.getObjective(objective);
         if (objective_test != null) {
            score.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player), objective_test, false).set(value);
         }
      }

      public static void add(ServerLevel level_server, String objective, String player, int value) {
         ServerScoreboard score = level_server.getServer().getScoreboard();
         Objective objective_test = score.getObjective(objective);
         if (objective_test != null) {
            int old_value = get(level_server, objective, player);
            score.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player), objective_test, false).set(old_value + value);
         }
      }
   }

   public static class Space {
      public static String getDimensionID(ServerLevel level_server) {
         return level_server.dimension().location().toString();
      }

      public static BlockPos getWorldSpawnPos(LevelAccessor level_accessor) {
         return level_accessor.getLevelData().getSpawnPos();
      }

      public static int getBuildHeight(LevelAccessor level_accessor, boolean highest) {
         return highest ? level_accessor.getMaxBuildHeight() - 1 : level_accessor.getMinBuildHeight() + 1;
      }

      public static boolean testChunkStatus(LevelAccessor level_accessor, ChunkPos chunk_pos, String status) {
         return level_accessor.hasChunk(chunk_pos.x, chunk_pos.z)
            && level_accessor.getChunk(chunk_pos.x, chunk_pos.z).getHighestGeneratedStatus().isOrAfter(ChunkStatus.byName(status));
      }

      public static void placeFeature(LevelAccessor level_accessor, BlockPos pos, String id) {
         WorldGenLevel level_world_gen = (WorldGenLevel)level_accessor;
         RegistryLookup<ConfiguredFeature<?, ?>> lookup = level_world_gen.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
         ResourceKey<ConfiguredFeature<?, ?>> key = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.parse(id));
         ChunkGenerator chunk_generator = level_world_gen.getLevel().getChunkSource().getGenerator();
         RandomSource random = level_world_gen.getRandom();
         ((ConfiguredFeature)lookup.getOrThrow(key).value()).place(level_world_gen, chunk_generator, random, pos);
      }

      public static Vec3 getPosLook(Entity entity, double offsetX, double offsetY, double offsetZ) {
         Vec3 vec3_forward = Vec3.directionFromRotation(entity.getXRot(), entity.getYRot());
         Vec3 vec3_vertical = null;
         if (Math.abs(vec3_forward.y) > 0.999) {
            vec3_vertical = new Vec3(0.0, 0.0, 1.0);
         } else {
            vec3_vertical = new Vec3(0.0, 1.0, 0.0);
         }

         Vec3 vec3_horizontal = vec3_forward.cross(vec3_vertical).normalize();
         Vec3 vec3_vertical_adjust = vec3_horizontal.cross(vec3_forward).normalize();
         return entity.position().add(vec3_horizontal.scale(offsetX)).add(vec3_vertical_adjust.scale(offsetY)).add(vec3_forward.scale(offsetZ));
      }

      public static Vec3 getPosRay(Entity entity, double distance) {
         return entity.level()
            .clip(
               new ClipContext(
                  entity.getEyePosition(1.0F), entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(distance)), Block.OUTLINE, Fluid.NONE, entity
               )
            )
            .getLocation();
      }

      public static int getHeight(LevelAccessor level_accessor, int posX, int posZ, String type) {
         return level_accessor.getHeight(Types.valueOf(type), posX, posZ);
      }

      public static int getHeightWorldGen(
         LevelAccessor level_accessor, ServerLevel level_server, ChunkGenerator chunk_generator, int posX, int posZ, String type_normal, String type_outside
      ) {
         BlockPos pos = new BlockPos(posX, 0, posZ);
         ChunkPos chunk_pos = new ChunkPos(pos);
         if (level_accessor.hasChunk(chunk_pos.x, chunk_pos.z)) {
            if (!(level_accessor.getChunk(pos) instanceof ProtoChunk)) {
               return getHeight(level_accessor, posX, posZ, type_normal);
            }

            if (testChunkStatus(level_accessor, chunk_pos, "carvers")) {
               return level_accessor.getChunk(chunk_pos.x, chunk_pos.z).getHeight(Types.valueOf(type_normal), pos.getX(), pos.getZ()) + 1;
            }
         }

         return chunk_generator.getBaseHeight(posX, posZ, Types.valueOf(type_outside), level_accessor, level_server.getChunkSource().randomState());
      }
   }

   public static class Tile {
      public static boolean test(BlockState block, String test) {
         if (test.isEmpty()) {
            return false;
         } else if (test.equals("all")) {
            return true;
         } else {
            String key = block + " -> " + test;
            if (!CacheManager.DataLogic.existNormal("test_block", key)) {
               boolean result = false;
               String[] data = toText(block);
               String block_id = data[0];
               List<String> properties = new ArrayList<>();
               if (!data[1].isEmpty()) {
                  properties = Arrays.stream(data[1].substring(1, data[1].length() - 1).split(",")).toList();
               }

               String value = "";
               int index = 0;
               String block_test = "";

               for (String split : test.split(" / ")) {
                  result = true;

                  for (String split2 : split.split(", ")) {
                     value = split2.replaceAll("[#!]", "");
                     if (!split2.startsWith("#") && !split2.startsWith("!#")) {
                        index = value.indexOf("[");
                        if (index == -1) {
                           if (!block_id.equals(value)) {
                              result = false;
                           }
                        } else {
                           block_test = value.substring(0, index);
                           if (!block_id.equals(block_test)) {
                              result = false;
                           } else {
                              for (String property : value.substring(index + 1, value.length() - 1).split(",")) {
                                 if (!properties.contains(property)) {
                                    result = false;
                                    break;
                                 }
                              }
                           }
                        }
                     } else {
                        try {
                           if (!block.is(BlockTags.create(ResourceLocation.parse(value)))) {
                              result = false;
                           }
                        } catch (Exception var22) {
                           result = false;
                        }
                     }

                     if (split2.startsWith("!")) {
                        result = !result;
                     }

                     if (!result) {
                        break;
                     }
                  }

                  if (result) {
                     break;
                  }
               }

               CacheManager.DataLogic.setNormal("test_block", key, result);
            }

            return CacheManager.DataLogic.getNormal("test_block").get(key);
         }
      }

      public static void set(LevelAccessor level_accessor, BlockPos pos, BlockState block, boolean is_world_gen) {
         if (GameUtils.Space.getBuildHeight(level_accessor, false) <= pos.getY()) {
            if (GameUtils.Space.getBuildHeight(level_accessor, true) >= pos.getY()) {
               if (level_accessor.isWaterAt(pos)) {
                  block = setPropertyLogic(block, "waterlogged", true);
               }

               int type = 0;
               if (!is_world_gen) {
                  type = 2;
               }

               level_accessor.setBlock(pos, block, type);
            }
         }
      }

      public static void remove(LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, boolean is_world_gen) {
         if (GameUtils.Space.getBuildHeight(level_accessor, false) <= pos.getY()) {
            if (GameUtils.Space.getBuildHeight(level_accessor, true) >= pos.getY()) {
               BlockState block = null;
               if (level_accessor.isWaterAt(pos)) {
                  block = Blocks.WATER.defaultBlockState();
               } else {
                  block = Blocks.AIR.defaultBlockState();
               }

               set(level_accessor, pos, block, is_world_gen);
               if (!is_world_gen) {
                  level_server.neighborChanged(pos.above(), level_server.getBlockState(pos.above()).getBlock(), pos);
               }
            }
         }
      }

      public static void removeDrop(LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos) {
         GameUtils.Item.spawn(level_server, pos.getCenter(), level_accessor.getBlockState(pos).getBlock().asItem().getDefaultInstance());
         remove(level_accessor, level_server, pos, false);
      }

      public static BlockState fromText(ServerLevel level_server, String data) {
         BlockState block = null;
         net.minecraft.world.level.block.Block get = null;
         String id = data;
         if (data.endsWith("}")) {
            id = data.substring(0, data.indexOf("{"));
         }

         if (id.endsWith("]")) {
            id = id.substring(0, id.indexOf("["));
         }

         get = (net.minecraft.world.level.block.Block)level_server.registryAccess().registryOrThrow(Registries.BLOCK).get(ResourceLocation.parse(id));
         if (get == null) {
            return Blocks.AIR.defaultBlockState();
         } else {
            block = get.defaultBlockState();
            if (data.endsWith("}")) {
               data = data.substring(0, data.indexOf("{"));
            }

            if (data.endsWith("]")) {
               String[] properties = data.substring(data.indexOf("[") + 1, data.length() - 1).split(",");

               for (String scan : properties) {
                  String[] getx = scan.split("=");
                  Property<?> test = block.getBlock().getStateDefinition().getProperty(getx[0]);
                  if (test instanceof BooleanProperty) {
                     block = setPropertyLogic(block, getx[0], Boolean.parseBoolean(getx[1]));
                  } else if (test instanceof IntegerProperty) {
                     block = setPropertyNumber(block, getx[0], Integer.parseInt(getx[1]));
                  } else if (test instanceof EnumProperty) {
                     block = setPropertyCustom(block, getx[0], getx[1]);
                  }
               }
            }

            return block;
         }
      }

      public static String[] toText(BlockState block) {
         String[] split = block.toString().substring("Block{".length()).split("}");
         if (split.length == 1) {
            split = new String[]{split[0], ""};
         }

         return split;
      }

      public static BlockState randomRotation(BlockState block) {
         if (Math.random() < 0.25) {
            return setPropertyCustom(block, "facing", "north");
         } else if (Math.random() < 0.25) {
            return setPropertyCustom(block, "facing", "west");
         } else {
            return Math.random() < 0.25 ? setPropertyCustom(block, "facing", "east") : setPropertyCustom(block, "facing", "south");
         }
      }

      public static void setScheduleTick(ServerLevel level_server, BlockPos pos, int value) {
         level_server.scheduleTick(pos, level_server.getBlockState(pos).getBlock(), value);
      }

      public static boolean isPassable(LevelAccessor level_accessor, BlockPos pos) {
         return level_accessor.getBlockState(pos).getCollisionShape(level_accessor, pos).isEmpty();
      }

      public static boolean getPropertyLogic(BlockState block, String name) {
         Property<?> property = block.getBlock().getStateDefinition().getProperty(name);
         return property instanceof BooleanProperty ? Boolean.parseBoolean(block.getValue(property).toString()) : false;
      }

      public static int getPropertyNumber(BlockState block, String name) {
         Property<?> property = block.getBlock().getStateDefinition().getProperty(name);
         return property instanceof IntegerProperty ? Integer.parseInt(block.getValue(property).toString()) : 0;
      }

      public static String getPropertyCustom(BlockState block, String name) {
         Property<?> property = block.getBlock().getStateDefinition().getProperty(name);
         return property instanceof EnumProperty ? block.getValue(property).toString() : "";
      }

      public static BlockState setPropertyLogic(BlockState block, String name, boolean value) {
         Property<?> property = block.getBlock().getStateDefinition().getProperty(name);
         if (block.hasProperty(property)
            && property instanceof BooleanProperty property_instance
            && property_instance.getValue(String.valueOf(value)).isPresent()) {
            block = (BlockState)block.setValue(property_instance, value);
         }

         return block;
      }

      public static BlockState setPropertyNumber(BlockState block, String name, int value) {
         Property<?> property = block.getBlock().getStateDefinition().getProperty(name);
         if (block.hasProperty(property)
            && property instanceof IntegerProperty property_instance
            && property_instance.getValue(String.valueOf(value)).isPresent()) {
            block = (BlockState)block.setValue(property_instance, value);
         }

         return block;
      }

      public static BlockState setPropertyCustom(BlockState block, String name, String value) {
         if (block.getBlock().getStateDefinition().getProperty(name) instanceof EnumProperty property_instance && property_instance.getValue(value).isPresent()
            )
          {
            block = (BlockState)block.setValue(property_instance, (Enum)property_instance.getValue(value).get());
         }

         return block;
      }
   }
}
