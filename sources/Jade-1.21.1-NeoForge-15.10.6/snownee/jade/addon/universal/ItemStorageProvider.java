package snownee.jade.addon.universal;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.ScreenDirection;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.WailaCommonRegistration;
import snownee.jade.impl.ui.HorizontalLineElement;
import snownee.jade.util.ClientProxy;
import snownee.jade.util.CommonProxy;
import snownee.jade.util.WailaExceptionHandler;

public abstract class ItemStorageProvider<T extends Accessor<?>> implements IComponentProvider<T>, IServerDataProvider<T> {
   public static final Cache<Object, ItemCollector<?>> targetCache = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(60L, TimeUnit.SECONDS).build();
   public static final Cache<Object, ItemCollector<?>> containerCache = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(120L, TimeUnit.SECONDS).build();
   private static final StreamCodec<RegistryFriendlyByteBuf, Entry<ResourceLocation, List<ViewGroup<ItemStack>>>> STREAM_CODEC = ViewGroup.listCodec(
      ItemStack.OPTIONAL_STREAM_CODEC
   );

   public static ItemStorageProvider.ForBlock getBlock() {
      return ItemStorageProvider.ForBlock.INSTANCE;
   }

   public static ItemStorageProvider.ForEntity getEntity() {
      return ItemStorageProvider.ForEntity.INSTANCE;
   }

   public static void append(ITooltip tooltip, Accessor<?> accessor, IPluginConfig config) {
      if (!accessor.getServerData().contains(JadeIds.UNIVERSAL_ITEM_STORAGE.toString())) {
         if (accessor.getServerData().getBoolean("Loot")) {
            tooltip.add(Component.translatable("jade.loot_not_generated"));
         } else if (accessor.getServerData().getBoolean("Locked")) {
            tooltip.add(Component.translatable("jade.locked"));
         }
      } else {
         List<ClientViewGroup<ItemView>> groups = ClientProxy.mapToClientGroups(
            accessor, JadeIds.UNIVERSAL_ITEM_STORAGE, STREAM_CODEC, WailaClientRegistration.instance().itemStorageProviders::get, tooltip
         );
         if (groups != null && !groups.isEmpty()) {
            MutableBoolean showName = new MutableBoolean(true);
            MutableInt amountWidth = new MutableInt();
            int showNameAmount = config.getInt(JadeIds.UNIVERSAL_ITEM_STORAGE_SHOW_NAME_AMOUNT);
            int totalSize = 0;

            for (ClientViewGroup<ItemView> group : groups) {
               for (ItemView view : group.views) {
                  if (view.amountText != null) {
                     showName.setFalse();
                  }

                  if (!view.item.isEmpty()) {
                     if (++totalSize == showNameAmount) {
                        showName.setFalse();
                     }
                  }

                  if (showName.isTrue()) {
                     String s = IDisplayHelper.get().humanReadableNumber(view.item.getCount(), "", false, null);
                     amountWidth.setValue(Math.max(amountWidth.intValue(), Minecraft.getInstance().font.width(s)));
                  }
               }
            }

            IElementHelper helper = IElementHelper.get();
            boolean renderGroup = groups.size() > 1 || ((ClientViewGroup)groups.getFirst()).shouldRenderGroup();
            ClientViewGroup.tooltip(
               tooltip,
               groups,
               renderGroup,
               (theTooltip, groupx) -> {
                  if (renderGroup) {
                     theTooltip.add(new HorizontalLineElement());
                     if (groupx.title != null) {
                        theTooltip.append(helper.text(groupx.title).scale(0.5F));
                        theTooltip.append(new HorizontalLineElement());
                     }
                  }

                  if (groupx.views.isEmpty()) {
                     CompoundTag data = groupx.extraData;
                     if (data != null && data.contains("Collecting", 99)) {
                        float progress = data.getFloat("Collecting");
                        if (progress < 1.0F) {
                           MutableComponent component = Component.translatable("jade.collectingItems");
                           if (progress > 0.0F) {
                              component.append(" %s%%".formatted((int)(progress * 100.0F)));
                           }

                           theTooltip.add(component);
                        }
                     }
                  }

                  int drawnCount = 0;
                  int realSize = config.getInt(
                     accessor.showDetails() ? JadeIds.UNIVERSAL_ITEM_STORAGE_DETAILED_AMOUNT : JadeIds.UNIVERSAL_ITEM_STORAGE_NORMAL_AMOUNT
                  );
                  realSize = Math.min(groupx.views.size(), realSize);
                  List<IElement> elements = Lists.newArrayList();

                  for (int i = 0; i < realSize; i++) {
                     ItemView itemView = (ItemView)groupx.views.get(i);
                     ItemStack stack = itemView.item;
                     if (!stack.isEmpty()) {
                        if (i > 0 && (showName.isTrue() || drawnCount >= config.getInt(JadeIds.UNIVERSAL_ITEM_STORAGE_ITEMS_PER_LINE))) {
                           theTooltip.add(elements);
                           theTooltip.setLineMargin(-1, ScreenDirection.DOWN, 0);
                           elements.clear();
                           drawnCount = 0;
                        }

                        if (showName.isTrue()) {
                           if (itemView.description != null) {
                              elements.add(helper.smallItem(stack));
                              elements.addAll(itemView.description);
                           } else {
                              elements.add(helper.smallItem(stack).clearCachedMessage());
                              String sx = IDisplayHelper.get().humanReadableNumber(stack.getCount(), "", false, null);
                              int width = Minecraft.getInstance().font.width(sx);
                              if (width < amountWidth.intValue()) {
                                 elements.add(helper.spacer(amountWidth.intValue() - width, 0));
                              }

                              elements.add(
                                 helper.text(Component.literal(sx).append("× ").append(IDisplayHelper.get().stripColor(stack.getHoverName()))).message(null)
                              );
                           }
                        } else if (itemView.amountText != null) {
                           elements.add(helper.item(stack, 1.0F, itemView.amountText));
                        } else {
                           elements.add(helper.item(stack));
                        }

                        drawnCount++;
                     }
                  }

                  if (!elements.isEmpty()) {
                     theTooltip.add(elements);
                  }
               }
            );
         }
      }
   }

   public static void putData(Accessor<?> accessor) {
      CompoundTag tag = accessor.getServerData();
      Object target = accessor.getTarget();
      Player player = accessor.getPlayer();
      Entry<ResourceLocation, List<ViewGroup<ItemStack>>> entry = CommonProxy.getServerExtensionData(
         accessor, WailaCommonRegistration.instance().itemStorageProviders
      );
      if (entry != null) {
         for (ViewGroup<ItemStack> group : entry.getValue()) {
            if (group.views.size() > 54) {
               group.views = group.views.subList(0, 54);
            }
         }

         tag.put(JadeIds.UNIVERSAL_ITEM_STORAGE.toString(), accessor.encodeAsNbt(STREAM_CODEC, entry));
      } else {
         if (target instanceof RandomizableContainer containerEntity && containerEntity.getLootTable() != null) {
            tag.putBoolean("Loot", true);
         } else if (!player.isCreative() && !player.isSpectator() && target instanceof BaseContainerBlockEntity te && te.lockKey != LockCode.NO_LOCK) {
            tag.putBoolean("Locked", true);
         }
      }
   }

   @Override
   public void appendTooltip(ITooltip tooltip, T accessor, IPluginConfig config) {
      if (!(accessor.getTarget() instanceof AbstractFurnaceBlockEntity)) {
         append(tooltip, accessor, config);
      }
   }

   @Override
   public void appendServerData(CompoundTag tag, T accessor) {
      if (!(accessor.getTarget() instanceof AbstractFurnaceBlockEntity)) {
         putData(accessor);
      }
   }

   @Override
   public boolean shouldRequestData(T accessor) {
      if (accessor.getTarget() instanceof AbstractFurnaceBlockEntity) {
         return false;
      } else {
         int amount;
         if (accessor.showDetails()) {
            amount = IWailaConfig.get().getPlugin().getInt(JadeIds.UNIVERSAL_ITEM_STORAGE_DETAILED_AMOUNT);
         } else {
            amount = IWailaConfig.get().getPlugin().getInt(JadeIds.UNIVERSAL_ITEM_STORAGE_NORMAL_AMOUNT);
         }

         return amount == 0 ? false : WailaCommonRegistration.instance().itemStorageProviders.hitsAny(accessor, IServerExtensionProvider::shouldRequestData);
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.UNIVERSAL_ITEM_STORAGE;
   }

   @Override
   public int getDefaultPriority() {
      return 1000;
   }

   public static enum Extension implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
      INSTANCE;

      @Override
      public ResourceLocation getUid() {
         return JadeIds.UNIVERSAL_ITEM_STORAGE_DEFAULT;
      }

      @Nullable
      @Override
      public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
         Object target = accessor.getTarget();
         if (target == null) {
            return CommonProxy.createItemCollector(accessor, ItemStorageProvider.containerCache).update(accessor);
         } else if (target instanceof RandomizableContainer te && te.getLootTable() != null) {
            return null;
         } else if (target instanceof ContainerEntity containerEntity && containerEntity.getLootTable() != null) {
            return null;
         } else {
            Player player = accessor.getPlayer();
            if (!player.isCreative() && !player.isSpectator() && target instanceof BaseContainerBlockEntity te && te.lockKey != LockCode.NO_LOCK) {
               return null;
            } else if (target instanceof EnderChestBlockEntity) {
               PlayerEnderChestContainer inventory = player.getEnderChestInventory();
               return new ItemCollector<>(new ItemIterator.ContainerItemIterator($ -> inventory, 0)).update(accessor);
            } else {
               ItemCollector<?> itemCollector;
               try {
                  itemCollector = (ItemCollector<?>)ItemStorageProvider.targetCache
                     .get(target, () -> CommonProxy.createItemCollector(accessor, ItemStorageProvider.containerCache));
               } catch (ExecutionException var6) {
                  WailaExceptionHandler.handleErr(var6, this, null);
                  return null;
               }

               return itemCollector.update(accessor);
            }
         }
      }

      @Override
      public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
         return ClientViewGroup.map(groups, ItemView::new, null);
      }

      @Override
      public boolean shouldRequestData(Accessor<?> accessor) {
         Object target = accessor.getTarget();
         return !(target instanceof EnderChestBlockEntity) && !(target instanceof Container) ? CommonProxy.hasDefaultItemStorage(accessor) : true;
      }

      @Override
      public int getDefaultPriority() {
         return 9999;
      }
   }

   public static class ForBlock extends ItemStorageProvider<BlockAccessor> {
      private static final ItemStorageProvider.ForBlock INSTANCE = new ItemStorageProvider.ForBlock();
   }

   public static class ForEntity extends ItemStorageProvider<EntityAccessor> {
      private static final ItemStorageProvider.ForEntity INSTANCE = new ItemStorageProvider.ForEntity();
   }
}
