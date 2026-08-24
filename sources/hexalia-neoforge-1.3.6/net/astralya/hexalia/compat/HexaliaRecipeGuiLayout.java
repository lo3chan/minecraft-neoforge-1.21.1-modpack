package net.astralya.hexalia.compat;

import net.minecraft.resources.ResourceLocation;

public enum HexaliaRecipeGuiLayout {
   MORTAR_AND_PESTLE(texture("mortar_gui.png"), 118, 80, 0, 0, 118, 80, new int[][]{{27, 30}, {3, 30}, {51, 30}}, new int[]{88, 30}),
   SMALL_CAULDRON(texture("category/small_cauldron_gui.png"), 89, 42, 14, 19, 89, 42, new int[][]{{0, 0}, {24, 0}, {0, 24}, {24, 24}}, new int[]{69, 11}),
   NATURES_RITUAL(texture("ritual_table_gui.png"), 118, 80, 0, 0, 118, 80, new int[][]{{27, 30}, {3, 30}, {51, 30}, {27, 6}, {27, 54}}, new int[]{88, 30}),
   CELESTIAL_INFUSION(texture("ritual_brazier_gui.png"), 118, 80, 0, 0, 118, 80, new int[][]{{27, 30}}, new int[]{88, 30}),
   MUTATION(texture("mutation_gui.png"), 118, 80, 0, 0, 118, 80, new int[][]{{47, 31}}, new int[]{88, 30});

   private final ResourceLocation texture;
   private final int width;
   private final int height;
   private final int textureU;
   private final int textureV;
   private final int textureWidth;
   private final int textureHeight;
   private final int[][] inputSlots;
   private final int[] outputSlot;

   private HexaliaRecipeGuiLayout(
      ResourceLocation texture, int width, int height, int textureU, int textureV, int textureWidth, int textureHeight, int[][] inputSlots, int[] outputSlot
   ) {
      this.texture = texture;
      this.width = width;
      this.height = height;
      this.textureU = textureU;
      this.textureV = textureV;
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.inputSlots = inputSlots;
      this.outputSlot = outputSlot;
   }

   public ResourceLocation texture() {
      return this.texture;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public int textureU() {
      return this.textureU;
   }

   public int textureV() {
      return this.textureV;
   }

   public int textureWidth() {
      return this.textureWidth;
   }

   public int textureHeight() {
      return this.textureHeight;
   }

   public int inputX(int index) {
      return this.inputSlots[Math.min(index, this.inputSlots.length - 1)][0];
   }

   public int inputY(int index) {
      return this.inputSlots[Math.min(index, this.inputSlots.length - 1)][1];
   }

   public int inputCount() {
      return this.inputSlots.length;
   }

   public int outputX() {
      return this.outputSlot[0];
   }

   public int outputY() {
      return this.outputSlot[1];
   }

   private static ResourceLocation texture(String path) {
      return ResourceLocation.fromNamespaceAndPath("hexalia", "textures/gui/" + path);
   }
}
