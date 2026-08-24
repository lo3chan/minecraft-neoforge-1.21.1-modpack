package pl.skidam.automodpack_loader_core.screen;

import java.util.Optional;

public interface ScreenService {
   void download(Object... var1);

   void fetch(Object... var1);

   void changelog(Object... var1);

   void restart(Object... var1);

   void danger(Object... var1);

   void error(String... var1);

   void menu(Object... var1);

   void title(Object... var1);

   void validation(Object... var1);

   Optional<String> getScreenString();

   Optional<Object> getScreen();
}
