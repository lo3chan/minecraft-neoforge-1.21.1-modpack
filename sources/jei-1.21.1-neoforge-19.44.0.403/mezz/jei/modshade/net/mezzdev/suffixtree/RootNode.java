package mezz.jei.modshade.net.mezzdev.suffixtree;

public class RootNode<T> extends Node<T> {
   public RootNode() {
      super(new SubString(""));
   }

   @Override
   protected boolean contains(T value) {
      return true;
   }

   @Override
   protected void addValue(T value) {
   }
}
