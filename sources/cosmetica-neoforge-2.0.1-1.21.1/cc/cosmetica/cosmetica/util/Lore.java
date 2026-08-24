package cc.cosmetica.cosmetica.util;

import cc.cosmetica.core.api.CachedImage;
import gg.cloaks.javaclient.model.UpdateLoreDto.ColorEnum;
import gg.cloaks.javaclient.model.UpdateLoreDto.TypeEnum;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class Lore {
   public Lore old;
   public final String value;
   public final String displayText;
   public final ColorEnum colour;
   public final CachedImage icon;
   public final String service;
   public static final String PRONOUN_SERVICE = "pronoun";

   public Lore(String text, ColorEnum colour, @NotNull CachedImage icon, String service) {
      this(text, text, colour, icon, service);
   }

   public Lore(String value, String display, ColorEnum colour, @NotNull CachedImage icon, String service) {
      Objects.requireNonNull(icon, "Icon cannot be null! Use NO_TEXTURE.");
      this.value = value;
      this.displayText = display;
      this.colour = colour;
      this.icon = icon;
      this.service = service;
   }

   public boolean isNoLore() {
      return this.value.isEmpty();
   }

   public String formatted() {
      return "§" + switch (this.colour) {
         case BLACK -> "0";
         case DARK_BLUE -> "1";
         case DARK_GREEN -> "2";
         case DARK_AQUA -> "3";
         case DARK_RED -> "4";
         case DARK_PURPLE -> "5";
         case GOLD -> "6";
         case GRAY -> "7";
         case DARK_GRAY -> "8";
         case BLUE -> "9";
         case GREEN -> "a";
         case AQUA -> "b";
         case RED -> "c";
         case LIGHT_PURPLE -> "d";
         case YELLOW -> "e";
         default -> "f";
      } + this.displayText;
   }

   public TypeEnum getType() {
      if (this.service.isEmpty()) {
         return TypeEnum.TITLE;
      } else {
         return "pronoun".equals(this.service) ? TypeEnum.PRONOUNS : TypeEnum.CONNECTION;
      }
   }

   public static Lore none(ColorEnum colour) {
      return new Lore("", ColorEnum.WHITE, CachedImage.NO_TEXTURE, "");
   }
}
