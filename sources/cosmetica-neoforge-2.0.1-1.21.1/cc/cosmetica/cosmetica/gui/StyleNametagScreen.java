package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.ImageCosmetic;
import cc.cosmetica.core.builtin.manager.SelfCosmeticManager;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.gui.widget.IconSelector;
import cc.cosmetica.cosmetica.gui.widget.LoreSelector;
import cc.cosmetica.cosmetica.gui.widget.MenuEndSelection;
import cc.cosmetica.cosmetica.gui.widget.RotatableGUIPlayer;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.cosmetica.util.Lore;
import cc.cosmetica.cosmetica.util.NametagUtil;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import com.google.common.collect.ImmutableList;
import gg.cloaks.javaclient.ApiException;
import gg.cloaks.javaclient.api.IconsApi;
import gg.cloaks.javaclient.api.LoreApi;
import gg.cloaks.javaclient.api.UsersApi;
import gg.cloaks.javaclient.model.CosmeticaUser;
import gg.cloaks.javaclient.model.Icon;
import gg.cloaks.javaclient.model.LoreOptions;
import gg.cloaks.javaclient.model.PlayerResponse;
import gg.cloaks.javaclient.model.UpdateLoreDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class StyleNametagScreen extends Screen implements AnimatedTextureScreen {
   private final AtomicBoolean iconDirty = new AtomicBoolean(false);
   private final AtomicBoolean loreDirty = new AtomicBoolean(false);
   public static final ResourceKey ID = new ResourceKey("cosmetica", "name_tag");
   private static final LoreOptions UNLOADED = new LoreOptions();
   private static State<LoreOptions> availableLores = new State(UNLOADED);
   private static State<List<IconSelector.IconOption>> availableIcons = new State(ImmutableList.of());

   public StyleNametagScreen() {
      super(ID);
      CosmeticaAPI.lore().requestAsync(LoreApi::getLoreOptions).thenAcceptAsync(loreOptions -> availableLores.set(loreOptions), Minecraft.getInstance());
      CosmeticaAPI.icons().requestAsync(IconsApi::get).thenAcceptAsync(icons -> {
         List<IconSelector.IconOption> newAvailableIcons = new ArrayList<>();

         for (Icon icon : icons) {
            newAvailableIcons.add(new IconSelector.IconOption(ImageCosmetic.fromIcon(icon), icon.isUnlocked()));
         }

         Logging.getInstance().debug(CosmeticaLogCategory.GUI, "loaded {} available icons", new Object[]{newAvailableIcons.size()});
         availableIcons.set(newAvailableIcons);
      }, Minecraft.getInstance());
   }

   protected Component[] buildScreen() {
      NametagUtil.isSnipe = false;
      NametagUtil.extraSpaceTaken = 0;
      UUID self = Minecraft.getInstance().getUser().getProfileId();
      return new Component[]{
         new Div(new Component[]{new LoreSelector(this.loreDirty, availableLores).tag(new String[]{"flex-1"}), (new RotatableGUIPlayer(self, null) {
               private int nametag = -1;

               @Override
               public List<Component> build() {
                  Lore lore = (Lore)Cosmetica.SELECTED_LORE.acquire(this);
                  CachedImage icon = (CachedImage)Cosmetica.SELECTED_ICON.extract(this, ic -> !ic.getImage().isLoaded() ? null : ic.getImage());
                  this.icon(icon, false);
                  this.loreIcon(!lore.icon.isLoaded() ? null : lore.icon);
                  if (this.nametag == -1) {
                     this.nametag = this.createNametag(Text.literal(lore.formatted()), 0.75F);
                  } else {
                     this.updateNametag(this.nametag, Text.literal(lore.formatted()), 0.75F);
                  }

                  return super.build();
               }
            }).showNametag(true).tag(new String[]{"preview-player"}), new IconSelector(this.iconDirty, availableIcons).tag(new String[]{"flex-1"})})
            .tag(new String[]{"horizontal", "flex-1", "main-content"}),
         new MenuEndSelection()
      };
   }

   @NotNull
   public Stylesheet getStylesheet() {
      return super.getStylesheet()
         .tag(
            "preview-player",
            Style.create()
               .set(CommonProperties.WIDTH, CommonProperties.fixed(OptionalInt.of(50)))
               .set(CommonProperties.Z_INDEX, 10)
               .set(CommonProperties.ALIGN_SELF, Optional.of(Align.CENTRE))
         )
         .tag("horizontal", Style.create().set(CommonProperties.WIDTH, CommonProperties.percent(100.0F, 0.0F)).set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X))
         .tag("main-content", Style.create().set(Div.JUSTIFY_CONTENT, Justify.SPACE_AROUND).set(Div.ALIGN_ITEMS, Align.STRETCH_CENTRE))
         .tag("flex-1", Style.create().set(CommonProperties.FLEX, 1));
   }

   public void unmount() {
      if (this.loreDirty.compareAndSet(true, false)) {
         Lore selectedLore = (Lore)Cosmetica.SELECTED_LORE.peek();
         CosmeticaAPI.lore()
            .requestAsync(this.updateLoreFunction(selectedLore))
            .thenAcceptAsync(
               object -> {
                  if (object instanceof CosmeticaUser) {
                     SelfCosmeticManager.updateLoreAndIcon((CosmeticaUser)object);
                  } else {
                     CosmeticaAPI.users()
                        .requestAsync(UsersApi::getSelf)
                        .thenAcceptAsync(user -> SelfCosmeticManager.update(new PlayerResponse().isUser(true).user(user)), Minecraft.getInstance())
                        .exceptionally(ex -> {
                           Logging.getInstance().error("Failed to reload own cosmetics", new Object[0]);
                           return null;
                        });
                  }
               },
               Minecraft.getInstance()
            )
            .exceptionally(
               e -> {
                  Minecraft.getInstance().tell(() -> {
                     Lore old = ((Lore)Cosmetica.SELECTED_LORE.peek()).old;
                     if (old != null) {
                        Cosmetica.SELECTED_LORE.set(old);
                     }
                  });
                  Logging.getInstance().error("Could not set lore", e);
                  if (e instanceof CompletionException) {
                     e = e.getCause();
                  }

                  if (e instanceof ApiException) {
                     Cosmetica.showToast(
                        Text.translatable("toast.cosmetica.loreUpdateError", new String[0]), Text.literal("Error code " + ((ApiException)e).getCode())
                     );
                  } else {
                     Cosmetica.showToast(Text.translatable("toast.cosmetica.loreUpdateError", new String[0]), Text.literal(e.getClass().getSimpleName()));
                  }

                  return null;
               }
            );
      }

      if (this.iconDirty.compareAndSet(true, false)) {
         ImageCosmetic selectedIcon = (ImageCosmetic)Cosmetica.SELECTED_ICON.peek();
         Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Updating Icon to {}", new Object[]{selectedIcon.getName()});
         CosmeticaAPI.icons().requestAsync(api -> api.equip(selectedIcon.getId())).thenAcceptAsync(user -> {
            SelfCosmeticManager.updateLoreAndIcon(user);
            Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Equipped icon " + selectedIcon.getId(), new Object[0]);
         }, Minecraft.getInstance()).exceptionally(e -> {
            assert Cosmetica.OWN_COSMETICS.peek() != null;

            Logging.getInstance().error("Could not set icon", e);
            Minecraft.getInstance().execute(() -> Cosmetica.SELECTED_ICON.set(((Cosmetics)Cosmetica.OWN_COSMETICS.peek()).getNametag().getIcon()));
            return null;
         });
      }
   }

   private Function<LoreApi, ?> updateLoreFunction(Lore newLore) {
      if (newLore.isNoLore()) {
         Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Removing lore", new Object[0]);
         return LoreApi::removeLore;
      } else {
         Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Updating Lore to {}", new Object[]{newLore.value});
         UpdateLoreDto update = new UpdateLoreDto();
         update.content(newLore.value);
         update.color(newLore.colour);
         update.type(newLore.getType());
         return api -> api.updateLore(update);
      }
   }
}
