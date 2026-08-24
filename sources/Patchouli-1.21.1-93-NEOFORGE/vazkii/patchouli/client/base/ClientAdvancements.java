package vazkii.patchouli.client.base;

import java.util.Map;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.mixin.client.AccessorClientAdvancements;

public class ClientAdvancements {
   private static boolean gotFirstAdvPacket = false;

   public static void onClientPacket() {
      if (!gotFirstAdvPacket) {
         ClientBookRegistry.INSTANCE.reload();
         gotFirstAdvPacket = true;
      } else {
         ClientBookRegistry.INSTANCE.reloadLocks(false);
      }
   }

   public static boolean hasDone(String advancement) {
      ResourceLocation id = ResourceLocation.tryParse(advancement);
      if (id != null) {
         ClientPacketListener conn = Minecraft.getInstance().getConnection();
         if (conn != null) {
            net.minecraft.client.multiplayer.ClientAdvancements cm = conn.getAdvancements();
            AdvancementHolder adv = cm.get(id);
            if (adv != null) {
               Map<AdvancementHolder, AdvancementProgress> progressMap = ((AccessorClientAdvancements)cm).getProgress();
               AdvancementProgress progress = progressMap.get(adv);
               return progress != null && progress.isDone();
            }
         }
      }

      return false;
   }

   public static void playerLogout() {
      gotFirstAdvPacket = false;
   }

   public static void sendBookToast(Book book) {
      ToastComponent gui = Minecraft.getInstance().getToasts();
      if (gui.getToast(ClientAdvancements.LexiconToast.class, book) == null) {
         gui.addToast(new ClientAdvancements.LexiconToast(book));
      }
   }

   public static class LexiconToast implements Toast {
      private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
      private final Book book;

      public LexiconToast(Book book) {
         this.book = book;
      }

      @NotNull
      public Book getToken() {
         return this.book;
      }

      @NotNull
      public Visibility render(GuiGraphics graphics, ToastComponent toastGui, long delta) {
         graphics.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
         Font font = toastGui.getMinecraft().font;
         graphics.drawString(font, Component.translatable(this.book.name), 30, 7, -1048336, false);
         graphics.drawString(font, Component.translatable("patchouli.gui.lexicon.toast.info"), 30, 17, -1, false);
         graphics.renderItem(this.book.getBookItem(), 8, 8);
         graphics.renderItemDecorations(font, this.book.getBookItem(), 8, 8);
         return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
      }
   }
}
