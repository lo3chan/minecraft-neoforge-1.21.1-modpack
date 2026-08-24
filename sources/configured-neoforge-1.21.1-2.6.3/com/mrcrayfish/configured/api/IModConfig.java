package com.mrcrayfish.configured.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface IModConfig {
   ConfigType getType();

   String getFileName();

   String getModId();

   default boolean isReadOnly() {
      return false;
   }

   @Nullable
   default String getTranslationKey() {
      return null;
   }

   IConfigEntry createRootEntry();

   ActionResult update(IConfigEntry var1);

   default boolean isChanged() {
      return false;
   }

   default void startEditing() {
   }

   default void stopEditing(boolean updated) {
   }

   default ActionResult loadWorldConfig(Path path) throws IOException {
      return ActionResult.fail();
   }

   default Optional<Runnable> restoreDefaultsTask() {
      return Optional.empty();
   }

   default Optional<Runnable> requestFromServerTask() {
      return Optional.empty();
   }

   default ActionResult canPlayerEdit(@Nullable Player player) {
      return ActionResult.fail();
   }

   default ActionResult showSaveConfirmation(Player player) {
      return ActionResult.fail();
   }
}
