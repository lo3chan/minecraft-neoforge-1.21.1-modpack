package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.gui.widget.MenuEndSelection;
import cc.cosmetica.cosmetica.mixin.AbstractScrollContainerAccessor;
import cc.cosmetica.cosmetica.settings.Setting;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.AbstractScrollContainer;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Border;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.ResizableElement;
import cc.cosmetica.kupe.api.gui.SizedElement;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.AbstractScrollContainer.ScrollbarPosition;
import cc.cosmetica.kupe.api.gui.Border.BorderConfig;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.MinecraftBuiltinComponent;
import cc.cosmetica.kupe.impl.StateManagerImpl;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import gg.cloaks.javaclient.model.ExternalCapeSetting;
import gg.cloaks.javaclient.model.UpdateCloudSettingsDto;
import gg.cloaks.javaclient.model.UpdateExternalCapeSettingDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

public class ExternalCapesScreen extends Screen {
   private final Setting<List<ExternalCapeSetting>> oldSettings;
   private final State<List<Component>> servers = new State(ImmutableList.of());
   public static final ResourceKey ID = new ResourceKey("cosmetica", "cape_server_settings");

   public ExternalCapesScreen(Setting<List<ExternalCapeSetting>> externalCapeSettings) {
      super(ID);
      this.oldSettings = externalCapeSettings;
   }

   protected Component[] buildScreen() {
      List<ExternalCapeSetting> settings = this.oldSettings.acquire(this);
      this.servers
         .set(
            settings.stream()
               .map(setting -> new ExternalCapesScreen.CapeSetting(setting, this.oldSettings.getManagement() == Setting.Management.USER))
               .collect(Collectors.toList())
         );
      return new Component[]{
         new ExternalCapesScreen.CapeServerList(this.servers, this.oldSettings.getManagement() == Setting.Management.USER), new MenuEndSelection()
      };
   }

   public void unmount() {
      boolean isModified = false;
      List<Component> newSettings = (List<Component>)this.servers.peek();
      List<ExternalCapeSetting> oldSettings = this.oldSettings.get();

      for (int i = 0; i < oldSettings.size(); i++) {
         ExternalCapeSetting setting = oldSettings.get(i);
         ExternalCapesScreen.CapeSetting component = (ExternalCapesScreen.CapeSetting)newSettings.get(i);
         if (component.setting.getService() != setting.getService() || component.enabled.peek() != setting.isEnabled()) {
            isModified = true;
            break;
         }
      }

      if (isModified) {
         Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Updating external cape settings", new Object[0]);
         UpdateCloudSettingsDto dto = new UpdateCloudSettingsDto();
         List<UpdateExternalCapeSettingDto> newExternalCapes = new ArrayList<>();

         for (Component component : newSettings) {
            ExternalCapesScreen.CapeSetting capeSetting = (ExternalCapesScreen.CapeSetting)component;
            UpdateExternalCapeSettingDto dto1 = new UpdateExternalCapeSettingDto();
            dto1.setEnabled((Boolean)capeSetting.enabled.peek());
            dto1.setReplace(capeSetting.setting.isReplace());
            dto1.setService(capeSetting.setting.getService().getValue());
            newExternalCapes.add(dto1);
         }

         dto.setExternalCapes(newExternalCapes);
         CosmeticaAPI.settings().requestAsync(api -> api.setCloud(dto)).thenAcceptAsync(user -> {
            CosmeticaSettingsScreen.updateCosmeticsAndSettings(user);
            Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Updated external cape settings", new Object[0]);
         }, Minecraft.getInstance()).exceptionally(e -> {
            Logging.getInstance().error("Error updating external cape settings: ", e);
            return null;
         });
      }
   }

   @NotNull
   public Stylesheet getStylesheet() {
      return super.getStylesheet()
         .component(
            ExternalCapesScreen.CapeServerList.class,
            Style.create()
               .set(AbstractScrollContainer.SCROLLBAR_POSITION, ScrollbarPosition.OUTSIDE)
               .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(30, 0, 0, 0)))
               .set(CommonProperties.HEIGHT, CommonProperties.screen(0.0F, 72.0F))
         )
         .tag(
            "cape-server",
            Style.create()
               .set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
               .set(Div.JUSTIFY_CONTENT, Justify.SPACE_BETWEEN)
               .set(Div.ALIGN_ITEMS, Align.STRETCH_CENTRE)
               .set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(0, 6)))
               .set(CommonProperties.HEIGHT, CommonProperties.fixedSize(40))
               .set(CommonProperties.WIDTH, CommonProperties.screen(60.0F, 0.0F))
         )
         .tag(
            "cape-server-editable",
            Style.create().set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(8750469)).set(CommonProperties.BORDER, GuiUtils.POPOUT_BORDER)
         )
         .tag(
            "cape-server-disabled",
            Style.create()
               .set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(5855577))
               .set(CommonProperties.BORDER, GuiUtils.SHADE_POPOUT_BORDER)
               .set(CommonProperties.TOOLTIP, Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.modpack_managed", new String[0]))))
         )
         .tag("inner-wrapper", Style.create().set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X))
         .tag("cape-server-button", Style.create().set(CommonProperties.WIDTH, CommonProperties.fixedSize(100)))
         .tag("padding-right", Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(0, 6, 0, 0))))
         .component(Image.class, Style.create().set(CommonProperties.HEIGHT, CommonProperties.fixedSize(24)));
   }

   private static class CapeServerList extends AbstractScrollContainer {
      private State<List<Component>> children;
      private final Component ghost;
      private boolean editable;
      @Nullable
      private Component dragging = null;
      private int draggingOffset;
      private int clickY;
      private int rootY;
      private int elementHeight;

      CapeServerList(State<List<Component>> children, boolean editable) {
         this.children = children;
         this.ghost = new Div(new Component[0])
            .withStyle(
               Style.create()
                  .set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(3552822))
                  .set(CommonProperties.BORDER, Border.create(BorderConfig.split(1, 6316128, 2302755)))
            );
         this.editable = editable;
      }

      public Dimensions minimumSize(List<? extends SizedElement> children, Margins padding, int vw, int vh) {
         return this.size(children, SizedElement::getMinimumSize, padding);
      }

      public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
         return this.size(children, SizedElement::getPreferredSize, padding);
      }

      private Dimensions size(List<? extends SizedElement> children, Function<SizedElement, Dimensions> getDimensions, Margins padding) {
         int elementWidth = 0;
         int elementHeight = 0;

         for (SizedElement element : children) {
            if (element.getComponent() != this.ghost) {
               Dimensions dimensions = getDimensions.apply(element);
               if (dimensions.getHeight() > elementHeight) {
                  elementHeight = dimensions.getHeight();
               }

               if (dimensions.getWidth() > elementWidth) {
                  elementWidth = dimensions.getWidth();
               }
            }
         }

         return new Dimensions(elementWidth + padding.horizontal(), elementHeight * children.size() + padding.vertical());
      }

      protected boolean hasVerticalOverflow() {
         return this.overflow;
      }

      public List<Component> build() {
         List<Component> result = new ArrayList<>((Collection<? extends Component>)this.children.acquire(this));
         result.add(this.ghost);
         return result;
      }

      public void resize(Region contentRegion, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
         int elementHeight = 0;
         ResizableElement ghostElement = null;

         for (ResizableElement element : children) {
            if (element.getComponent() == this.ghost) {
               ghostElement = element;
            } else {
               Dimensions dimensions = element.getPreferredSize();
               if (dimensions.getHeight() > elementHeight) {
                  elementHeight = dimensions.getHeight();
               }
            }
         }

         if (ghostElement == null) {
            throw new IllegalArgumentException("Ghost element cannot be null for cape server list");
         } else {
            this.elementHeight = elementHeight;
            ghostElement.setRenderRegion(new Region(0, 0, 0, 0));
            int y = contentRegion.getY();

            for (ResizableElement elementx : children) {
               if (elementx.getComponent() != this.ghost) {
                  int width = Math.min(
                     Math.max(Math.min(elementx.getPreferredSize().getWidth(), elementx.getMaximumSize().getWidth()), elementx.getMinimumSize().getWidth()),
                     contentRegion.getWidth()
                  );
                  int height = Math.min(
                     Math.max(Math.min(elementx.getPreferredSize().getHeight(), elementx.getMaximumSize().getHeight()), elementx.getMinimumSize().getHeight()),
                     elementHeight
                  );
                  if (elementx.getComponent() == this.dragging) {
                     elementx.setRenderRegion(
                        new Region(
                              contentRegion.getX(),
                              Math.min(
                                 Math.max(y + this.draggingOffset, contentRegion.getY()),
                                 contentRegion.getY() + (((List)this.children.peek()).size() - 1) * this.elementHeight
                              ),
                              width,
                              height
                           )
                           .shrinkMargins(elementx.getPadding())
                     );
                     ghostElement.setRenderRegion(new Region(contentRegion.getX(), y, contentRegion.getWidth(), height));
                  } else {
                     elementx.setRenderRegion(new Region(contentRegion.getX(), y, width, height).shrinkMargins(elementx.getPadding()));
                  }

                  y += elementHeight;
               }
            }

            this.overflow = y > contentRegion.getEndY();
            this.maxScroll = y - contentRegion.getEndY();
            if (this.maxScroll < 0.0F) {
               this.maxScroll = 0.0F;
            }

            this.grabbed = false;
         }
      }

      protected void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
         this.rootY = region.getY();
         if (this.dragging != null) {
            int newDraggingOffset = this.getInnerClickY(mouseY) - this.clickY;
            if (newDraggingOffset != this.draggingOffset) {
               this.draggingOffset = newDraggingOffset;
               int newIndex = this.getInnerClickY(mouseY) / this.elementHeight;
               if (newIndex < 0) {
                  newIndex = 0;
               } else if (newIndex >= ((List)this.children.peek()).size()) {
                  newIndex = ((List)this.children.peek()).size() - 1;
               }

               this.draggingOffset = this.draggingOffset + this.elementHeight * (this.clickY / this.elementHeight - newIndex);
               int oldIndex = ((List)this.children.peek()).indexOf(this.dragging);
               if (oldIndex != newIndex) {
                  List<Component> newOrder = new ArrayList<>((Collection<? extends Component>)this.children.peek());
                  newOrder.remove(this.dragging);
                  newOrder.add(newIndex, this.dragging);
                  this.children.set(newOrder);
               } else {
                  StateManagerImpl.scheduleResize();
               }
            }
         }

         super.paint(canvas, region, mouseX, mouseY);
      }

      public void paintDecorations(Canvas canvas, Region region, Region scissorRegion, int mouseX, int mouseY) {
         super.paintDecorations(canvas, region, scissorRegion, mouseX, mouseY);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      public void mouseClicked(Element target, double x, double y, int button) {
         super.mouseClicked(target, x, y, button);
         if (this.editable && !this.grabbed && !(target.getComponent() instanceof MinecraftBuiltinComponent)) {
            this.clickY = this.getInnerClickY((int)y);
            if (this.clickY > 0) {
               int index = this.clickY / this.elementHeight;
               if (index < ((List)this.children.peek()).size()) {
                  this.draggingOffset = 0;
                  this.dragging = (Component)((List)this.children.peek()).get(index);
                  this.dragging.withStyle(Style.create().set(CommonProperties.Z_INDEX, 10));
                  this.children.set((List)this.children.peek());
               }
            }
         }
      }

      private int getInnerClickY(int outerY) {
         return (int)(outerY - this.rootY + this.maxScroll * ((AbstractScrollContainerAccessor)this).getScrollPercent());
      }

      public void mouseReleased(double x, double y, int button) {
         super.mouseReleased(x, y, button);
         if (this.dragging != null) {
            this.dragging.withStyle(Style.create());
            this.dragging = null;
            StateManagerImpl.scheduleResize();
         }
      }
   }

   private static class CapeSetting extends Div {
      private final ExternalCapeSetting setting;
      private final State<Boolean> enabled;
      private final boolean editable;
      boolean useMinecraftText = !Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase(Locale.ROOT).startsWith("en")
         && "Enabled".equals(I18n.get("button.cosmetica.enabled", new Object[0]));

      CapeSetting(ExternalCapeSetting capeServerSetting, boolean editable) {
         super(new Component[0]);
         this.tag(new String[]{"cape-server", editable ? "cape-server-editable" : "cape-server-disabled"});
         this.setting = capeServerSetting;
         this.enabled = new State(this.setting.isEnabled());
         this.editable = editable;
      }

      public List<Component> build() {
         boolean settingEnabled = (Boolean)this.enabled.acquire(this);
         return ImmutableList.of(
            new Div(new Component[]{new Label(Text.literal(this.setting.getName()))}).tag(new String[]{"inner-wrapper"}),
            new Div(
                  new Component[]{
                     new Button(
                           settingEnabled
                              ? (this.useMinecraftText ? Text.GUI_YES : Text.translatable("button.cosmetica.enabled", new String[0]))
                              : (this.useMinecraftText ? Text.GUI_NO : Text.translatable("button.cosmetica.disabled", new String[0])),
                           () -> this.enabled.set(!settingEnabled)
                        )
                        .setDisabled(!this.editable)
                        .tag(new String[]{"cape-server-button", "padding-right"}),
                     new Image(new ResourceKey("cosmetica", "textures/grabbable.png")).setTransparent(1.0F)
                  }
               )
               .tag(new String[]{"inner-wrapper"})
         );
      }
   }
}
