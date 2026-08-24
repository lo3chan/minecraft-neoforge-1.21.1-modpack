package dev.isxander.yacl3.gui.image;

public interface ImageRendererFactory {
   ImageRendererFactory.ImageSupplier prepareImage() throws Exception;

   default boolean requiresOffThreadPreparation() {
      return true;
   }

   public interface ImageSupplier {
      ImageRenderer completeImage() throws Exception;
   }

   public interface OnThread extends ImageRendererFactory {
      @Override
      default boolean requiresOffThreadPreparation() {
         return false;
      }
   }
}
