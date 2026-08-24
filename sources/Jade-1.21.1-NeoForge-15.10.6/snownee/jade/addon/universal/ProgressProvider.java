package snownee.jade.addon.universal;

import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.ScreenDirection;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ProgressView;
import snownee.jade.api.view.ViewGroup;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.WailaCommonRegistration;
import snownee.jade.util.CommonProxy;
import snownee.jade.util.WailaExceptionHandler;

public abstract class ProgressProvider<T extends Accessor<?>> implements IComponentProvider<T>, IServerDataProvider<T> {
   public static ProgressProvider.ForBlock getBlock() {
      return ProgressProvider.ForBlock.INSTANCE;
   }

   public static ProgressProvider.ForEntity getEntity() {
      return ProgressProvider.ForEntity.INSTANCE;
   }

   public static void append(ITooltip tooltip, Accessor<?> accessor, IPluginConfig config) {
      if (accessor.getServerData().contains("JadeProgress")) {
         IClientExtensionProvider<CompoundTag, ProgressView> provider = Optional.ofNullable(
               ResourceLocation.tryParse(accessor.getServerData().getString("JadeProgressUid"))
            )
            .map(WailaClientRegistration.instance().progressProviders::get)
            .orElse(null);
         if (provider != null) {
            List<ClientViewGroup<ProgressView>> groups;
            try {
               groups = provider.getClientGroups(accessor, ViewGroup.readList(accessor.getServerData(), "JadeProgress", Function.identity()));
            } catch (Exception var8) {
               WailaExceptionHandler.handleErr(var8, provider, tooltip::add);
               return;
            }

            if (!groups.isEmpty()) {
               IElementHelper helper = IElementHelper.get();
               boolean renderGroup = groups.size() > 1 || ((ClientViewGroup)groups.getFirst()).shouldRenderGroup();
               BoxStyle.GradientBorder boxStyle = BoxStyle.getTransparent().clone();
               boxStyle.bgColor = 1157627903;
               ClientViewGroup.tooltip(tooltip, groups, renderGroup, (theTooltip, group) -> {
                  if (renderGroup) {
                     group.renderHeader(theTooltip);
                  }

                  for (ProgressView view : group.views) {
                     if (view.text != null) {
                        theTooltip.add(helper.text(view.text).scale(0.75F));
                        theTooltip.setLineMargin(-1, ScreenDirection.DOWN, 0);
                     }

                     theTooltip.add(helper.progress(view.progress, null, view.style, boxStyle, false).size(new Vec2(10.0F, 2.0F)));
                  }
               });
            }
         }
      }
   }

   public static void putData(Accessor<?> accessor) {
      Entry<ResourceLocation, List<ViewGroup<CompoundTag>>> entry = CommonProxy.getServerExtensionData(
         accessor, WailaCommonRegistration.instance().progressProviders
      );
      if (entry != null) {
         CompoundTag tag = accessor.getServerData();
         ViewGroup.saveList(tag, "JadeProgress", entry.getValue(), Function.identity());
         tag.putString("JadeProgressUid", entry.getKey().toString());
      }
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
      return WailaCommonRegistration.instance().progressProviders.hitsAny(accessor, IServerExtensionProvider::shouldRequestData);
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.UNIVERSAL_PROGRESS;
   }

   @Override
   public int getDefaultPriority() {
      return 1000;
   }

   @Override
   public boolean isRequired() {
      return true;
   }

   public static class ForBlock extends ProgressProvider<BlockAccessor> {
      private static final ProgressProvider.ForBlock INSTANCE = new ProgressProvider.ForBlock();
   }

   public static class ForEntity extends ProgressProvider<EntityAccessor> {
      private static final ProgressProvider.ForEntity INSTANCE = new ProgressProvider.ForEntity();
   }
}
