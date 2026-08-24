package DistantHorizons.libraries.electronwill.nightconfig.core;

@Deprecated
public class ConcurrentConfigSpec extends ConfigSpec {
   public ConcurrentConfigSpec() {
      super(Config.inMemoryUniversalConcurrent());
   }
}
