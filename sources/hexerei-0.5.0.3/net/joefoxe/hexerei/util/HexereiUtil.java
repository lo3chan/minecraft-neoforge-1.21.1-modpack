package net.joefoxe.hexerei.util;

import com.mojang.blaze3d.shaders.FogShape;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;

public class HexereiUtil {
   public static final Vec3 CENTER_OF_ORIGIN = new Vec3(0.5, 0.5, 0.5);

   public static ResourceLocation getRegistryName(Item i) {
      return BuiltInRegistries.ITEM.getKey(i);
   }

   public static ResourceLocation getRegistryName(Fluid i) {
      return BuiltInRegistries.FLUID.getKey(i);
   }

   public static ResourceLocation getRegistryName(Block b) {
      return BuiltInRegistries.BLOCK.getKey(b);
   }

   public static Vec3 offsetRandomly(Vec3 vec, RandomSource r, float radius) {
      return new Vec3(
         vec.x + (r.nextFloat() - 0.5F) * 2.0F * radius, vec.y + (r.nextFloat() - 0.5F) * 2.0F * radius, vec.z + (r.nextFloat() - 0.5F) * 2.0F * radius
      );
   }

   public static Vec3 getCenterOf(Vec3i pos) {
      return pos.equals(Vec3i.ZERO) ? CENTER_OF_ORIGIN : Vec3.atLowerCornerOf(pos).add(0.5, 0.5, 0.5);
   }

   public static void destroyBlock(Level world, BlockPos pos, float effectChance) {
      destroyBlock(world, pos, effectChance, stack -> Block.popResource(world, pos, stack));
   }

   public static void destroyBlock(Level world, BlockPos pos, float effectChance, Consumer<ItemStack> droppedItemCallback) {
      destroyBlockAs(world, pos, null, ItemStack.EMPTY, effectChance, droppedItemCallback);
   }

   public static void destroyBlockAs(
      Level world, BlockPos pos, @Nullable Player player, ItemStack usedTool, float effectChance, Consumer<ItemStack> droppedItemCallback
   ) {
      FluidState fluidState = world.getFluidState(pos);
      BlockState state = world.getBlockState(pos);
      if (world.random.nextFloat() < effectChance) {
         world.levelEvent(2001, pos, Block.getId(state));
      }

      BlockEntity tileentity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
      if (player != null) {
         BreakEvent event = new BreakEvent(world, pos, state, player);
         NeoForge.EVENT_BUS.post(event);
         if (event.isCanceled()) {
            return;
         }

         usedTool.mineBlock(world, state, pos, player);
         player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
      }

      if (world instanceof ServerLevel
         && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)
         && !world.restoringBlockSnapshots
         && (player == null || !player.isCreative())) {
         for (ItemStack itemStack : Block.getDrops(state, (ServerLevel)world, pos, tileentity, player, usedTool)) {
            droppedItemCallback.accept(itemStack);
         }

         if (state.getBlock() instanceof IceBlock
            && usedTool.getEnchantmentLevel(player.level().holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH)) == 0) {
            if (world.dimensionType().ultraWarm()) {
               return;
            }

            BlockState blockState = world.getBlockState(pos.below());
            if (blockState.blocksMotion() || blockState.liquid()) {
               world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            }

            return;
         }

         state.spawnAfterBreak((ServerLevel)world, pos, ItemStack.EMPTY, true);
      }

      world.setBlockAndUpdate(pos, fluidState.createLegacyBlock());
   }

   public static <V> ResourceLocation getKeyOrThrow(Block value) {
      DefaultedRegistry<Block> registry = BuiltInRegistries.BLOCK;
      ResourceLocation key = registry.getKey(value);
      if (key == null) {
         throw new IllegalArgumentException("Could not get key for value " + value + "!");
      } else {
         return key;
      }
   }

   public static <V> ResourceLocation getKeyOrThrow(Item value) {
      DefaultedRegistry<Item> registry = BuiltInRegistries.ITEM;
      ResourceLocation key = registry.getKey(value);
      if (key == null) {
         throw new IllegalArgumentException("Could not get key for value " + value + "!");
      } else {
         return key;
      }
   }

   public static String getModNameForModId(String modId) {
      ModList modList = ModList.get();
      return modList.getModContainerById(modId)
         .map(ModContainer::getModInfo)
         .<String>map(IModInfo::getDisplayName)
         .orElseGet(() -> StringUtils.capitalize(modId));
   }

   public static float lerpAngle(float startAngle, float endAngle, float alpha) {
      startAngle = normalizeAngle(startAngle);
      endAngle = normalizeAngle(endAngle);
      float difference = endAngle - startAngle;
      if (difference > 180.0F) {
         difference -= 360.0F;
      } else if (difference < -180.0F) {
         difference += 360.0F;
      }

      return normalizeAngle(startAngle + alpha * difference);
   }

   public static float normalizeAngle(float angle) {
      return normalizeAngle(angle, 180.0F, -180.0F);
   }

   public static float normalizeAngle(float angle, float max, float min) {
      while (angle > max) {
         angle -= 360.0F;
      }

      while (angle < min) {
         angle += 360.0F;
      }

      return angle;
   }

   public static double angleDifference(double angle1, double angle2) {
      double diff = (angle2 - angle1 + 180.0) % 360.0 - 180.0;
      return diff < -180.0 ? diff + 360.0 : diff;
   }

   public static float easeInOutCubic(float x) {
      double c1 = 1.70158;
      double c2 = c1 * 1.525;
      return (float)(
         x < 0.5
            ? Math.pow(2.0F * x, 2.0) * ((c2 + 1.0) * 2.0 * x - c2) / 2.0
            : (Math.pow(2.0F * x - 2.0F, 2.0) * ((c2 + 1.0) * (x * 2.0F - 2.0F) + c2) + 2.0) / 2.0
      );
   }

   public static float moveTo(float input, float movedTo, float speed) {
      float distance = movedTo - input;
      if (Math.abs(distance) <= speed) {
         return movedTo;
      } else {
         if (distance > 0.0F) {
            input += speed;
         } else {
            input -= speed;
         }

         return input;
      }
   }

   public static float moveToAngle(float input, float movedTo, float speed) {
      float distance = movedTo - input;
      if (Math.abs(distance) <= speed) {
         return movedTo;
      } else {
         if (distance > 0.0F) {
            if (Math.abs(distance) < 180.0F) {
               input += speed;
            } else {
               input -= speed;
            }
         } else if (Math.abs(distance) < 180.0F) {
            input -= speed;
         } else {
            input += speed;
         }

         if (input < -90.0F) {
            input += 360.0F;
         }

         if (input > 270.0F) {
            input -= 360.0F;
         }

         return input;
      }
   }

   public static String intToRoman(int number) {
      String[] thousands = new String[]{"", "M", "MM", "MMM"};
      String[] hundreds = new String[]{"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
      String[] tens = new String[]{"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
      String[] units = new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
      return thousands[number / 1000] + hundreds[number % 1000 / 100] + tens[number % 100 / 10] + units[number % 10];
   }

   public static <T extends Enum<?>> T readEnum(CompoundTag nbt, String key, Class<T> enumClass) {
      T[] enumConstants = (T[])enumClass.getEnumConstants();
      if (enumConstants == null) {
         throw new IllegalArgumentException("Non-Enum class passed to readEnum: " + enumClass.getName());
      } else {
         if (nbt.contains(key, 8)) {
            String name = nbt.getString(key);

            for (T t : enumConstants) {
               if (t.name().equals(name)) {
                  return t;
               }
            }
         }

         return enumConstants[0];
      }
   }

   public static <T extends Enum<?>> T readEnum(String string, Class<T> enumClass) {
      T[] enumConstants = (T[])enumClass.getEnumConstants();
      if (enumConstants == null) {
         throw new IllegalArgumentException("Non-Enum class passed to readEnum: " + enumClass.getName());
      } else {
         if (!string.isEmpty()) {
            for (T t : enumConstants) {
               if (t.name().equals(string)) {
                  return t;
               }
            }
         }

         return enumConstants[0];
      }
   }

   public static <T extends Enum<?>> void writeEnum(CompoundTag nbt, String key, T enumConstant) {
      nbt.putString(key, enumConstant.name());
   }

   public static float getAngle(Vec3 pos2, Vec3 pos) {
      float angle = (float)Math.toDegrees(Math.atan2(pos.z() - pos2.z(), pos.x() - pos2.z()));
      if (angle < 0.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   public static int getColorValue(DyeColor color) {
      return color == null ? 0 : color.getTextureDiffuseColor();
   }

   public static int getColorValue(float r, float g, float b) {
      int r2 = (int)(r * 255.0F);
      int g2 = (int)(g * 255.0F);
      int b2 = (int)(b * 255.0F);
      return r2 << 16 | g2 << 8 | b2;
   }

   public static int getColorValueAlpha(float r, float g, float b, float a) {
      int r2 = (int)(r * 255.0F);
      int g2 = (int)(g * 255.0F);
      int b2 = (int)(b * 255.0F);
      int a2 = (int)(a * 255.0F);
      return a2 << 24 | r2 << 16 | g2 << 8 | b2;
   }

   public static int getDyeColor(ItemStack stack, int fallback) {
      DyedItemColor color = (DyedItemColor)stack.get(DataComponents.DYED_COLOR);
      return 0xFF000000 | (color != null ? color.rgb() : fallback);
   }

   public static DyedItemColor getDyeColor(ItemStack stack, DyedItemColor fallback) {
      return (DyedItemColor)stack.getOrDefault(DataComponents.DYED_COLOR, fallback);
   }

   public static int getDyeColor(ItemStack stack) {
      return getDyeColor(stack, 4337438);
   }

   public static DyeColor getDyeColorNamed(String name) {
      return getDyeColorNamed(name, 0);
   }

   public static DyeColor getDyeColorNamed(String name, int offset) {
      return getDyeColorNamed(name, offset, 0);
   }

   public static DyeColor getDyeColorNamed(String name, int offset, int offset2) {
      if (name.equals("jeb_")) {
         return DyeColor.byId((int)(((ClientEvents.getClientTicks() + offset2) / 40.0F + offset) % 16.0F));
      } else if (name.equals("les_")) {
         return DyeColor.byId(switch ((int)(((ClientEvents.getClientTicks() + offset2) / 40.0F + offset) % 5.0F)) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 6;
            case 4 -> 14;
            default -> 0;
         });
      } else if (name.equals("bi_")) {
         return DyeColor.byId(switch ((int)(((ClientEvents.getClientTicks() + offset2) / 40.0F + offset) % 3.0F)) {
            case 1 -> 10;
            case 2 -> 11;
            default -> 2;
         });
      } else if (name.equals("trans_")) {
         return DyeColor.byId(switch ((int)(((ClientEvents.getClientTicks() + offset2) / 40.0F + offset) % 3.0F)) {
            case 1 -> 3;
            case 2 -> 0;
            default -> 6;
         });
      } else if (name.equals("joe_")) {
         return DyeColor.byId(switch ((int)(((ClientEvents.getClientTicks() + offset2) / 40.0F + offset) % 4.0F)) {
            case 1, 3 -> 3;
            case 2 -> 9;
            default -> 11;
         });
      } else {
         return null;
      }
   }

   public static float getMaxHeadXRot(ModelPart head) {
      return Mth.clamp(head.xRot, -1.2566371F, 1.0471976F);
   }

   public static <T extends LivingEntity> void animateHands(HumanoidModel<T> model, T entity, boolean leftHand) {
      ModelPart mainHand = leftHand ? model.leftArm : model.rightArm;
      ModelPart offHand = leftHand ? model.rightArm : model.leftArm;
      Vec3 bx = new Vec3(1.0, 0.0, 0.0);
      Vec3 by = new Vec3(0.0, 1.0, 0.0);
      Vec3 bz = new Vec3(0.0, 0.0, 1.0);
      float downFacingRot = Mth.clamp(model.head.xRot, 0.0F, 0.8F);
      float xRot = getMaxHeadXRot(model.head) - (entity.isCrouching() ? 1.0F : 0.0F) - 0.3F + downFacingRot * 0.5F;
      bx = bx.xRot(xRot);
      by = by.xRot(xRot);
      bz = bz.xRot(xRot);
      Vec3 armVec = new Vec3(0.0, 0.0, 1.0);
      float mirror = leftHand ? -1.0F : 1.0F;
      armVec = armVec.yRot(-0.99F * mirror);
      Vec3 newV = bx.scale(armVec.x).add(by.scale(armVec.y)).add(bz.scale(armVec.z));
      float yaw = (float)Math.atan2(-newV.x, newV.z);
      float len = (float)newV.length();
      float pitch = (float)Math.asin(newV.y / len);
      mainHand.yRot = yaw + model.head.yRot * 1.4F - 0.1F * mirror - 0.5F * downFacingRot * mirror;
      mainHand.xRot = (float)(pitch - 1.5707963267948966);
      offHand.yRot = (float)Mth.clamp((mainHand.yRot - 1.0F * mirror) * 0.2, -0.15, 0.15) + 1.1F * mirror;
      offHand.xRot = mainHand.xRot - 0.06F;
      float offset = leftHand ? -Mth.clamp(model.head.yRot, -1.0F, 0.0F) : Mth.clamp(model.head.yRot, 0.0F, 1.0F);
      mainHand.z = -offset;
      AnimationUtils.bobModelPart(model.leftArm, entity.tickCount, 1.0F);
      AnimationUtils.bobModelPart(model.rightArm, entity.tickCount, -1.0F);
   }

   public static float[] rgbIntToFloatArray(int rgbInt) {
      int r = rgbInt >> 16 & 0xFF;
      int g = rgbInt >> 8 & 0xFF;
      int b = rgbInt >> 0 & 0xFF;
      return new float[]{r / 255.0F, g / 255.0F, b / 255.0F};
   }

   public static float[] rgbaIntToFloatArray(int rgbInt) {
      int a = rgbInt >> 24 & 0xFF;
      int r = rgbInt >> 16 & 0xFF;
      int g = rgbInt >> 8 & 0xFF;
      int b = rgbInt >> 0 & 0xFF;
      return new float[]{r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F};
   }

   public static int[] rgbIntToIntArray(int rgbInt) {
      int r = rgbInt >> 16 & 0xFF;
      int g = rgbInt >> 8 & 0xFF;
      int b = rgbInt >> 0 & 0xFF;
      return new int[]{r, g, b};
   }

   public static boolean entityIsHostile(Entity entity) {
      return entity.getType().getCategory().equals(MobCategory.MONSTER);
   }

   public static List<BlockPos> getAllTileEntityPositionsNearby(BlockEntityType<?> te, Integer radius, Level world, Entity entity) {
      BlockPos entitypos = entity.blockPosition();
      List<BlockPos> nearby = new ArrayList<>();

      for (BlockEntity tile : getTileEntitiesAroundPosition(world, entitypos, radius)) {
         BlockEntityType<?> tileType = tile.getType();
         if (tileType != null && tileType.equals(te)) {
            BlockPos tilePos = tile.getBlockPos();
            if (tilePos.closerThan(new Vec3i((int)entity.position().x, (int)entity.position().y, (int)entity.position().z), radius.intValue())) {
               nearby.add(tile.getBlockPos());
            }
         }
      }

      return nearby;
   }

   public static List<BlockPos> getAllToggledCofferAndHerbJarPositionsNearby(Integer radius, Level world, Entity entity) {
      BlockPos entitypos = entity.blockPosition();
      List<BlockPos> nearby = new ArrayList<>();

      for (BlockEntity tile : getTileEntitiesAroundPosition(world, entitypos, radius)) {
         if (tile instanceof CofferTile cofferTile) {
            BlockPos tilePos = tile.getBlockPos();
            if (tilePos.closerThan(new Vec3i((int)entity.position().x, (int)entity.position().y, (int)entity.position().z), radius.intValue())
               && cofferTile.buttonToggled != 0) {
               nearby.add(tile.getBlockPos());
            }
         } else if (tile instanceof HerbJarTile herbJarTile) {
            BlockPos tilePos = tile.getBlockPos();
            if (tilePos.closerThan(new Vec3i((int)entity.position().x, (int)entity.position().y, (int)entity.position().z), radius.intValue())
               && herbJarTile.buttonToggled != 0) {
               nearby.add(tile.getBlockPos());
            }
         }
      }

      return nearby;
   }

   private static List<BlockEntity> getTileEntitiesAroundPosition(Level world, BlockPos pos, Integer radius) {
      List<BlockEntity> blockentities = new ArrayList<>();
      int chunkradius = (int)Math.ceil(radius.intValue() / 16.0) + 1;
      int chunkPosX = pos.getX() >> 4;
      int chunkPosZ = pos.getZ() >> 4;

      for (int x = chunkPosX - chunkradius; x < chunkPosX + chunkradius; x++) {
         for (int z = chunkPosZ - chunkradius; z < chunkPosZ + chunkradius; z++) {
            if (world.hasChunk(x, z)) {
               for (BlockEntity be : world.getChunk(x, z).getBlockEntities().values()) {
                  if (!blockentities.contains(be)) {
                     blockentities.add(be);
                  }
               }
            }
         }
      }

      return blockentities;
   }

   public static ResourceLocation getResource(String name) {
      return getResource("hexerei", name);
   }

   public static ResourceLocation getResource(String modId, String name) {
      return ResourceLocation.fromNamespaceAndPath(modId, name);
   }

   public static String getResourcePath(String name) {
      return getResourcePath("hexerei", name);
   }

   public static String getResourcePath(String modId, String name) {
      return getResource(modId, name).toString();
   }

   public static FriendlyByteBuf createBuf() {
      return new FriendlyByteBuf(Unpooled.buffer());
   }

   public static <T> T make(Supplier<T> supplier) {
      return supplier.get();
   }

   public static <T> T make(T object, Consumer<T> consumer) {
      consumer.accept(object);
      return object;
   }

   public static BlockState getBlockStateWithExistingProperties(BlockState oldState, BlockState newState) {
      BlockState finalState = newState;

      for (Property<?> property : oldState.getProperties()) {
         if (newState.hasProperty(property)) {
            finalState = newStateWithOldProperty(oldState, finalState, property);
         }
      }

      return finalState;
   }

   public static BlockState setBlockStateWithExistingProperties(Level level, BlockPos pos, BlockState newState, int flags) {
      BlockState oldState = level.getBlockState(pos);
      BlockState finalState = getBlockStateWithExistingProperties(oldState, newState);
      level.sendBlockUpdated(pos, oldState, finalState, flags);
      level.setBlock(pos, finalState, flags);
      return finalState;
   }

   public static <T extends Comparable<T>> BlockState newStateWithOldProperty(BlockState oldState, BlockState newState, Property<T> property) {
      return (BlockState)newState.setValue(property, oldState.getValue(property));
   }

   public static <T> Optional<T> acceptOrElse(Optional<T> opt, Consumer<T> consumer, Runnable orElse) {
      if (opt.isPresent()) {
         consumer.accept(opt.get());
      } else {
         orElse.run();
      }

      return opt;
   }

   public static <T> boolean allMatch(Iterable<T> input, Predicate<T> matcher) {
      Objects.requireNonNull(matcher);

      for (T e : input) {
         if (!matcher.test(e)) {
            return false;
         }
      }

      return true;
   }

   public static <T> boolean anyMatch(Iterable<T> input, Predicate<T> matcher) {
      Objects.requireNonNull(matcher);

      for (T e : input) {
         if (matcher.test(e)) {
            return true;
         }
      }

      return false;
   }

   public static float[] rgbToHsl(float r, float g, float b) {
      float[] hsl = new float[3];
      float max = Math.max(r, Math.max(g, b));
      float min = Math.min(r, Math.min(g, b));
      float delta = max - min;
      hsl[2] = (max + min) / 2.0F;
      if (delta == 0.0F) {
         hsl[0] = 0.0F;
         hsl[1] = 0.0F;
      } else {
         hsl[1] = hsl[2] < 0.5F ? delta / (max + min) : delta / (2.0F - max - min);
         if (max == r) {
            hsl[0] = (g - b) / delta + (g < b ? 6.0F : 0.0F);
         } else if (max == g) {
            hsl[0] = (b - r) / delta + 2.0F;
         } else {
            hsl[0] = (r - g) / delta + 4.0F;
         }

         hsl[0] /= 6.0F;
      }

      return hsl;
   }

   public static int hslToRgb(float h, float s, float l) {
      int r;
      int g;
      int b;
      if (s == 0.0F) {
         r = g = b = (int)(l * 255.0F);
      } else {
         float q = l < 0.5F ? l * (1.0F + s) : l + s - l * s;
         float p = 2.0F * l - q;
         r = (int)(hueToRgb(p, q, h + 0.33333334F) * 255.0F);
         g = (int)(hueToRgb(p, q, h) * 255.0F);
         b = (int)(hueToRgb(p, q, h - 0.33333334F) * 255.0F);
      }

      return r << 16 | g << 8 | b;
   }

   public static float hueToRgb(float p, float q, float t) {
      if (t < 0.0F) {
         t++;
      }

      if (t > 1.0F) {
         t--;
      }

      if (t < 0.16666667F) {
         return p + (q - p) * 6.0F * t;
      } else if (t < 0.5F) {
         return q;
      } else {
         return t < 0.6666667F ? p + (q - p) * (0.6666667F - t) * 6.0F : p;
      }
   }

   public static class FogData {
      public final FogMode mode;
      public float start;
      public float end;
      public FogShape shape = FogShape.SPHERE;

      public FogData(FogMode p_234204_) {
         this.mode = p_234204_;
      }
   }
}
