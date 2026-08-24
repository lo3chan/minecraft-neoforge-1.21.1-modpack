package cc.cosmetica.cosmetica.util;

import cc.cosmetica.core.impl.LoggingCategory;

public interface CosmeticaLogCategory {
   LoggingCategory CACHE = new LoggingCategory("cache");
   LoggingCategory EVENTS = new LoggingCategory("events");
   LoggingCategory GUI = new LoggingCategory("gui");
   LoggingCategory KEYBINDS = new LoggingCategory("keybinds");
   LoggingCategory LOGIN = new LoggingCategory("login");
   LoggingCategory SETTINGS = new LoggingCategory("settings");
}
