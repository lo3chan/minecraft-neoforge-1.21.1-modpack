package net.irisshaders.iris.shaderpack.programs;

public interface ProgramSetInterface {
   public static class Empty implements ProgramSetInterface {
      public static final ProgramSetInterface INSTANCE = new ProgramSetInterface.Empty();
   }
}
