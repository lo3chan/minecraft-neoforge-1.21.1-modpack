package cc.cosmetica.cosmetica;

import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.kupe.api.State;
import org.jetbrains.annotations.Nullable;

public interface StateHolder {
   State<Cosmetics> cosmetica$getCosmeticState();

   void cosmetica$setCosmeticState(@Nullable Cosmetics var1);
}
