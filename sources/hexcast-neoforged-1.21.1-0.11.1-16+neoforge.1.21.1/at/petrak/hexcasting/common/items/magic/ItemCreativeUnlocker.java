package at.petrak.hexcasting.common.items.magic;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.DiscoveryHandlers;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.ItemLoreFragment;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexSounds;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ItemCreativeUnlocker extends Item implements MediaHolderItem {
   public static final String DISPLAY_MEDIA = "media";
   public static final String DISPLAY_PATTERNS = "patterns";
   public static final String TAG_EXTRACTIONS = "extractions";
   public static final String TAG_INSERTIONS = "insertions";

   public static boolean isDebug(ItemStack stack) {
      return isDebug(stack, null);
   }

   public static boolean isDebug(ItemStack stack, String flag) {
      if (stack.is(HexItems.CREATIVE_UNLOCKER) && stack.has(DataComponents.CUSTOM_NAME)) {
         List<String> keywords = Arrays.asList(stack.getHoverName().getString().toLowerCase(Locale.ROOT).split(" "));
         return !keywords.contains("debug") ? false : flag == null || keywords.contains(flag);
      } else {
         return false;
      }
   }

   public static Component infiniteMedia(Level level) {
      String prefix = "item.hexcasting.creative_unlocker.";
      String emphasis = Language.getInstance().getOrDefault(prefix + "for_emphasis");
      MutableComponent emphasized = Component.empty();

      for (int i = 0; i < emphasis.length(); i++) {
         emphasized.append(rainbow(Component.literal(emphasis.charAt(i) + ""), i, level));
      }

      return emphasized;
   }

   public ItemCreativeUnlocker(Properties properties) {
      super(properties);
   }

   @Override
   public long getMedia(ItemStack stack) {
      return 9223372036854775807L;
   }

   @Override
   public long getMaxMedia(ItemStack stack) {
      return 9223372036854775807L;
   }

   @Override
   public void setMedia(ItemStack stack, long media) {
   }

   @Override
   public boolean canProvideMedia(ItemStack stack) {
      return true;
   }

   @Override
   public boolean canRecharge(ItemStack stack) {
      return true;
   }

   public static void addToIntArray(ItemStack stack, String tag, int n) {
      int[] arr = NBTHelper.getIntArray(stack, tag);
      if (arr == null) {
         arr = new int[0];
      }

      int[] newArr = Arrays.copyOf(arr, arr.length + 1);
      newArr[newArr.length - 1] = n;
      NBTHelper.putIntArray(stack, tag, newArr);
   }

   public static void addToLongArray(ItemStack stack, String tag, long n) {
      long[] arr = NBTHelper.getLongArray(stack, tag);
      if (arr == null) {
         arr = new long[0];
      }

      long[] newArr = Arrays.copyOf(arr, arr.length + 1);
      newArr[newArr.length - 1] = n;
      NBTHelper.putLongArray(stack, tag, newArr);
   }

   @Override
   public long withdrawMedia(ItemStack stack, long cost, boolean simulate) {
      if (!simulate && isDebug(stack, "media")) {
         addToLongArray(stack, "extractions", cost);
      }

      return cost < 0L ? this.getMedia(stack) : cost;
   }

   @Override
   public long insertMedia(ItemStack stack, long amount, boolean simulate) {
      if (!simulate && isDebug(stack, "media")) {
         addToLongArray(stack, "insertions", amount);
      }

      return amount < 0L ? this.getMaxMedia(stack) : amount;
   }

   public boolean isFoil(ItemStack stack) {
      return super.isFoil(stack) || isDebug(stack);
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
      if (isDebug(stack, "media") && !level.isClientSide) {
         this.debugDisplay(stack, "extractions", "withdrawn", "all_media", entity);
         this.debugDisplay(stack, "insertions", "inserted", "infinite_media", entity);
      }
   }

   private void debugDisplay(ItemStack stack, String tag, String langKey, String allKey, Entity entity) {
      long[] arr = NBTHelper.getLongArray(stack, tag);
      if (arr != null) {
         NBTHelper.remove(stack, tag);

         for (long i : arr) {
            if (i < 0L) {
               entity.sendSystemMessage(
                  Component.translatable(
                        "hexcasting.debug.media_" + langKey,
                        new Object[]{stack.getDisplayName(), Component.translatable("hexcasting.debug." + allKey).withStyle(ChatFormatting.GRAY)}
                     )
                     .withStyle(ChatFormatting.LIGHT_PURPLE)
               );
            } else {
               entity.sendSystemMessage(
                  Component.translatable(
                        "hexcasting.debug.media_" + langKey + ".with_dust",
                        new Object[]{
                           stack.getDisplayName(),
                           Component.literal(i + "").withStyle(ChatFormatting.WHITE),
                           Component.literal(String.format("%.2f", i * 1.0 / 10000.0)).withStyle(ChatFormatting.WHITE)
                        }
                     )
                     .withStyle(ChatFormatting.LIGHT_PURPLE)
               );
            }
         }
      }
   }

   public InteractionResult useOn(UseOnContext context) {
      if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof BlockEntityAbstractImpetus impetus) {
         impetus.setInfiniteMedia();
         context.getLevel().playSound(null, context.getClickedPos(), HexSounds.SPELL_CIRCLE_FIND_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
      if (level instanceof ServerLevel slevel && consumer instanceof ServerPlayer player) {
         ArrayList<ResourceLocation> names = new ArrayList<>(ItemLoreFragment.NAMES);
         names.add(0, HexAPI.modLoc("root"));

         for (ResourceLocation name : names) {
            AdvancementHolder rootAdv = slevel.getServer().getAdvancements().get(name);
            if (rootAdv != null) {
               ArrayList<AdvancementHolder> children = new ArrayList<>();
               AdvancementNode rootNode = slevel.getServer().getAdvancements().tree().get(rootAdv);
               if (rootNode != null) {
                  addChildren(rootNode, children);
               }

               PlayerAdvancements adman = player.getAdvancements();

               for (AdvancementHolder kid : children) {
                  AdvancementProgress progress = adman.getOrStartProgress(kid);
                  if (!progress.isDone()) {
                     for (String crit : progress.getRemainingCriteria()) {
                        adman.award(kid, crit);
                     }
                  }
               }
            }
         }
      }

      ItemStack copy = stack.copy();
      super.finishUsingItem(stack, level, consumer);
      return copy;
   }

   private static MutableComponent rainbow(MutableComponent component, int shift, Level level) {
      return level == null
         ? component.withStyle(ChatFormatting.WHITE)
         : component.withStyle(s -> s.withColor(TextColor.fromRgb(Mth.hsvToRgb((float)((level.getGameTime() + shift) * 2L % 360L) / 360.0F, 1.0F, 1.0F))));
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
      Component emphasized = infiniteMedia(null);
      MutableComponent modName = Component.translatable("item.hexcasting.creative_unlocker.mod_name").withStyle(s -> s.withColor(ItemMediaHolder.HEX_COLOR));
      tooltipComponents.add(Component.translatable("hexcasting.spelldata.onitem", new Object[]{emphasized}).withStyle(ChatFormatting.GRAY));
      tooltipComponents.add(Component.translatable("item.hexcasting.creative_unlocker.tooltip", new Object[]{modName}).withStyle(ChatFormatting.GRAY));
   }

   private static void addChildren(AdvancementNode root, List<AdvancementHolder> out) {
      out.add(root.holder());

      for (AdvancementNode kiddo : root.children()) {
         addChildren(kiddo, out);
      }
   }

   static {
      DiscoveryHandlers.addDebugItemDiscoverer((player, type) -> {
         for (ItemStack item : player.getInventory().items) {
            if (isDebug(item, type)) {
               return item;
            }
         }

         for (ItemStack itemx : player.getInventory().armor) {
            if (isDebug(itemx, type)) {
               return itemx;
            }
         }

         for (ItemStack itemxx : player.getInventory().offhand) {
            if (isDebug(itemxx, type)) {
               return itemxx;
            }
         }

         return ItemStack.EMPTY;
      });
   }
}
