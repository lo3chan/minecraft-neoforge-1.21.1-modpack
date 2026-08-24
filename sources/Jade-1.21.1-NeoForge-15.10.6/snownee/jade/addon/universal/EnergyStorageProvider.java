package snownee.jade.addon.universal;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.ProgressStyle;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.WailaCommonRegistration;
import snownee.jade.impl.ui.ElementHelper;
import snownee.jade.util.CommonProxy;
import snownee.jade.util.WailaExceptionHandler;

public abstract class EnergyStorageProvider<T extends Accessor<?>> implements IComponentProvider<T>, IServerDataProvider<T> {
   public static EnergyStorageProvider.ForBlock getBlock() {
      return EnergyStorageProvider.ForBlock.INSTANCE;
   }

   public static EnergyStorageProvider.ForEntity getEntity() {
      return EnergyStorageProvider.ForEntity.INSTANCE;
   }

   public static void append(ITooltip tooltip, Accessor<?> accessor, IPluginConfig config) {
      if (accessor.showDetails() || !config.get(JadeIds.UNIVERSAL_ENERGY_STORAGE_DETAILED)) {
         if (accessor.getServerData().contains("JadeEnergyStorage")) {
            IClientExtensionProvider<CompoundTag, EnergyView> provider = Optional.ofNullable(
                  ResourceLocation.tryParse(accessor.getServerData().getString("JadeEnergyStorageUid"))
               )
               .map(WailaClientRegistration.instance().energyStorageProviders::get)
               .orElse(null);
            if (provider != null) {
               List<ClientViewGroup<EnergyView>> groups;
               try {
                  groups = provider.getClientGroups(accessor, ViewGroup.readList(accessor.getServerData(), "JadeEnergyStorage", Function.identity()));
               } catch (Exception var7) {
                  WailaExceptionHandler.handleErr(var7, provider, tooltip::add);
                  return;
               }

               if (!groups.isEmpty()) {
                  IElementHelper helper = IElementHelper.get();
                  boolean renderGroup = groups.size() > 1 || ((ClientViewGroup)groups.getFirst()).shouldRenderGroup();
                  ClientViewGroup.tooltip(
                     tooltip,
                     groups,
                     renderGroup,
                     (theTooltip, group) -> {
                        if (renderGroup) {
                           group.renderHeader(theTooltip);
                        }

                        for (EnergyView view : group.views) {
                           IWailaConfig.HandlerDisplayStyle style = config.getEnum(JadeIds.UNIVERSAL_ENERGY_STORAGE_STYLE);
                           Component text;
                           if (view.overrideText != null) {
                              text = view.overrideText;
                           } else {
                              String current = view.current;
                              if (style == IWailaConfig.HandlerDisplayStyle.PROGRESS_BAR) {
                                 current = ChatFormatting.WHITE + current;
                              }

                              text = Component.translatable("jade.fe", new Object[]{current, view.max});
                           }

                           switch (style) {
                              case PLAIN_TEXT:
                                 theTooltip.add(Component.translatable("jade.energy.text", new Object[]{text}));
                                 break;
                              case ICON:
                                 theTooltip.add(
                                    helper.sprite(JadeIds.JADE("energy"), 10, 10)
                                       .size(ElementHelper.SMALL_ITEM_SIZE)
                                       .translate(ElementHelper.SMALL_ITEM_OFFSET)
                                 );
                                 theTooltip.append(text);
                                 break;
                              case PROGRESS_BAR:
                                 ProgressStyle progressStyle = helper.progressStyle().color(-5636096, -10092544);
                                 theTooltip.add(helper.progress(view.ratio, text, progressStyle, BoxStyle.getNestedBox(), true));
                           }
                        }
                     }
                  );
               }
            }
         }
      }
   }

   public static void putData(Accessor<?> accessor) {
      Entry<ResourceLocation, List<ViewGroup<CompoundTag>>> entry = CommonProxy.getServerExtensionData(
         accessor, WailaCommonRegistration.instance().energyStorageProviders
      );
      if (entry != null) {
         CompoundTag tag = accessor.getServerData();
         ViewGroup.saveList(tag, "JadeEnergyStorage", entry.getValue(), Function.identity());
         tag.putString("JadeEnergyStorageUid", entry.getKey().toString());
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.UNIVERSAL_ENERGY_STORAGE;
   }

   @Override
   public int getDefaultPriority() {
      return 1000;
   }

   @Override
   public void appendTooltip(ITooltip tooltip, T accessor, IPluginConfig config) {
      append(tooltip, accessor, config);
   }

   @Override
   public void appendServerData(CompoundTag data, T accessor) {
      putData(accessor);
   }

   @Override
   public boolean shouldRequestData(T accessor) {
      return !accessor.showDetails() && IWailaConfig.get().getPlugin().get(JadeIds.UNIVERSAL_ENERGY_STORAGE_DETAILED)
         ? false
         : WailaCommonRegistration.instance().energyStorageProviders.hitsAny(accessor, IServerExtensionProvider::shouldRequestData);
   }

   public static enum Extension implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, EnergyView> {
      INSTANCE;

      @Override
      public ResourceLocation getUid() {
         return JadeIds.UNIVERSAL_ENERGY_STORAGE_DEFAULT;
      }

      @Override
      public List<ClientViewGroup<EnergyView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
         return groups.stream().map($ -> {
            String unit = $.getExtraData().getString("Unit");
            return new ClientViewGroup<>($.views.stream().map(tag -> EnergyView.read(tag, unit)).filter(Objects::nonNull).toList());
         }).toList();
      }

      @Nullable
      @Override
      public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
         return CommonProxy.wrapEnergyStorage(accessor);
      }

      @Override
      public boolean shouldRequestData(Accessor<?> accessor) {
         return CommonProxy.hasDefaultEnergyStorage(accessor);
      }

      @Override
      public int getDefaultPriority() {
         return 9999;
      }
   }

   public static class ForBlock extends EnergyStorageProvider<BlockAccessor> {
      private static final EnergyStorageProvider.ForBlock INSTANCE = new EnergyStorageProvider.ForBlock();
   }

   public static class ForEntity extends EnergyStorageProvider<EntityAccessor> {
      private static final EnergyStorageProvider.ForEntity INSTANCE = new EnergyStorageProvider.ForEntity();
   }
}
