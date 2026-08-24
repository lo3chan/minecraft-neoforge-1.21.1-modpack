package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.PointerEvents;
import cc.cosmetica.kupe.api.gui.GUIPlayer.Attachment;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Region;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RotatableGUIPlayer extends GUIPlayer {
   @Nullable
   private final State<Boolean> showingElytra;
   private boolean drag = false;
   private double xStart = 0.0;
   private float yawStart = 0.0F;
   private boolean offline = false;
   @Nullable
   public CachedImage loreIcon;
   @Nullable
   public CachedImage icon;

   public RotatableGUIPlayer(@NotNull UUID uuid, @Nullable State<Boolean> showingElytra) {
      super(uuid, true);
      this.showingElytra = showingElytra;
   }

   public RotatableGUIPlayer icon(@Nullable CachedImage icon, boolean offlineIcon) {
      this.icon = icon;
      this.offline = offlineIcon;
      return this;
   }

   public RotatableGUIPlayer loreIcon(@Nullable CachedImage icon) {
      this.loreIcon = icon;
      return this;
   }

   public void setYaw(float yaw) {
      this.pose.yRotBody = this.pose.yRotHead = yaw;
   }

   public boolean hasTransparentIcon() {
      return this.offline;
   }

   public List<Component> build() {
      if (this.showingElytra != null) {
         boolean showElytra = (Boolean)this.showingElytra.acquire(this);
         this.hideAttachments(new Attachment[]{showElytra ? CAPE : ELYTRA});
         this.showAttachments(new Attachment[]{showElytra ? ELYTRA : CAPE});
      }

      return super.build();
   }

   public void mouseClicked(Element target, double x, double y, int button) {
      if (target.getComponent() == this) {
         this.xStart = x;
         this.yawStart = this.pose.yRotBody;
         this.drag = true;
      }
   }

   public void mouseReleased(double x, double y, int button) {
      this.drag = false;
   }

   public void unmount() {
      this.drag = false;
   }

   public void mouseMoved(Region region, double x, double y) {
      if (this.drag) {
         this.pose.yRotBody = this.pose.yRotHead = this.yawStart - (float)(x - this.xStart);
      }
   }

   @Nullable
   public Stylesheet getStylesheet() {
      return new Stylesheet().self(Style.create().set(CommonProperties.POINTER_EVENTS, PointerEvents.ALL));
   }
}
