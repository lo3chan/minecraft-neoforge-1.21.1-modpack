package jeresources.api.render;

public enum TextModifier {
   black("§0"),
   darkBlue("§1"),
   darkGreen("§2"),
   darkCyan("§3"),
   darkRed("§4"),
   purple("§5"),
   orange("§6"),
   lightGrey("§7"),
   darkGrey("§8"),
   lilac("§9"),
   lightGreen("§a"),
   lightCyan("§b"),
   lightRed("§c"),
   pink("§d"),
   yellow("§e"),
   white("§f"),
   obfuscated("§k"),
   bold("§l"),
   strikethrough("§m"),
   underline("§n"),
   italic("§o"),
   reset("§r");

   private String prefix;

   private TextModifier(String prefix) {
      this.prefix = prefix;
   }

   @Override
   public String toString() {
      return this.prefix;
   }
}
