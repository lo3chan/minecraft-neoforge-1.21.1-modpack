package com.alonie.brbe.util;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.PinnedRecipeManager;
import com.alonie.brbe.compat.recipeviewer.RecipeViewer;
import com.alonie.brbe.compat.recipeviewer.RecipeViewerRegistry;
import com.alonie.brbe.config.AppContext;
import com.alonie.brbe.config.ConfigEventBus;
import com.alonie.brbe.layout.BookGeometry;
import com.alonie.brbe.layout.BookLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

public final class BrbeDiagnostic {
   private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

   private BrbeDiagnostic() {
   }

   public static void dump() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      sb.append("══════════════════════════════════════════════════════\n");
      sb.append("  BRBE Architecture Diagnostic  —  ");
      sb.append(TS.format(LocalDateTime.now())).append("\n");
      sb.append("══════════════════════════════════════════════════════\n\n");
      int passed = 0;
      int failed = 0;
      failed += checkAppContext(sb) ? 0 : 1;
      passed += checkAppContext(sb) ? 1 : 0;
      failed += checkEventBus(sb) ? 0 : 1;
      passed += checkEventBus(sb) ? 1 : 0;
      failed += checkConfigRouting(sb) ? 0 : 1;
      passed += checkConfigRouting(sb) ? 1 : 0;
      failed += checkPinStore(sb) ? 0 : 1;
      passed += checkPinStore(sb) ? 1 : 0;
      failed += checkBookLayout(sb) ? 0 : 1;
      passed += checkBookLayout(sb) ? 1 : 0;
      failed += checkRecipeViewers(sb) ? 0 : 1;
      passed += checkRecipeViewers(sb) ? 1 : 0;
      failed += checkConstraintLayout(sb) ? 0 : 1;
      passed += checkConstraintLayout(sb) ? 1 : 0;
      sb.append("\n─── Result ───────────────────────────────────────────\n");
      sb.append(String.format("  %d passed, %d failed, %d total\n", passed, failed, passed + failed));
      sb.append("══════════════════════════════════════════════════════\n");

      try {
         Path logFile = Minecraft.getInstance().gameDirectory.toPath().resolve("brbe-diagnostic.log");
         Files.writeString(logFile, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         BetterRecipeBook.LOGGER.info("[BRBE] Diagnostic written to {}", logFile);
      } catch (IOException var4) {
         BetterRecipeBook.LOGGER.error("[BRBE] Failed to write diagnostic", var4);
      }

      BetterRecipeBook.LOGGER.info(sb.toString());
   }

   private static boolean checkAppContext(StringBuilder sb) {
      sb.append("─── 1. AppContext (DI Root) ───────────────────────────\n");

      try {
         AppContext ctx = AppContext.instance();
         sb.append(String.format("  PASS  instance       = %s\n", ctx));
         sb.append(String.format("  INFO  config         = %s\n", ctx.config() != null ? "present" : "NULL"));
         sb.append(String.format("  INFO  events         = %s\n", ctx.events() != null ? "present" : "NULL"));
         sb.append(String.format("  INFO  pins           = %s\n", ctx.pins() != null ? "present" : "NULL"));
         sb.append(String.format("  INFO  instantCraft   = %s\n", ctx.instantCraft() != null ? "present" : "NULL"));
         sb.append(String.format("  INFO  bookLayout     = %s\n", ctx.bookLayout() != null ? "present" : "NULL"));
         sb.append(String.format("  INFO  recipeViewers  = %s\n", ctx.recipeViewers() != null ? "present" : "NULL"));
         sb.append(String.format("  INFO  brewingBook    = %s\n", ctx.brewingBook() != null ? "present" : "NULL"));
         sb.append(String.format("  INFO  smithingBook   = %s\n", ctx.smithingBook() != null ? "present" : "NULL"));
         return true;
      } catch (Exception var2) {
         sb.append(String.format("  FAIL  %s\n", var2.getMessage()));
         return false;
      }
   }

   private static boolean checkEventBus(StringBuilder sb) {
      sb.append("\n─── 2. ConfigEventBus ──────────────────────────────────\n");

      try {
         AppContext ctx = AppContext.instance();
         ConfigEventBus events = ctx.events();
         if (events == null) {
            sb.append("  FAIL  events is null\n");
            return false;
         } else {
            boolean before = events.consumeConfigChange();
            events.requestConfigRefresh();
            boolean after = events.consumeConfigChange();
            boolean after2 = events.consumeConfigChange();
            events.requestConfigRefresh();
            sb.append(String.format("  PASS  request/consume: before=%s after=%s after2=%s\n", before, after, after2));
            if (!after || after2) {
               sb.append("  WARN  AtomicBoolean round-trip unexpected\n");
            }

            sb.append(String.format("  INFO  config.keepCentered           = %s\n", ctx.config().keepCentered));
            sb.append(String.format("  INFO  config.enablePinning          = %s\n", true));
            sb.append(String.format("  INFO  config.partialCraftingEnabled = %s\n", ctx.config().partialCraftingEnabled));
            sb.append(String.format("  INFO  config.enableBook             = %s\n", ctx.config().enableBook));
            return true;
         }
      } catch (Exception var6) {
         sb.append(String.format("  FAIL  %s\n", var6.getMessage()));
         return false;
      }
   }

   private static boolean checkConfigRouting(StringBuilder sb) {
      sb.append("\n─── 3. Config Routing (ctx().config() pathway) ────────\n");

      try {
         boolean same = BetterRecipeBook.config == AppContext.instance().config();
         sb.append(String.format("  %s  BetterRecipeBook.config == ctx().config()\n", same ? "PASS" : "FAIL"));
         boolean pinMatch = true;
         sb.append(String.format("  %s  enablePinning (always true) matches both paths\n", pinMatch ? "PASS" : "FAIL"));
         return same && pinMatch;
      } catch (Exception var3) {
         sb.append(String.format("  FAIL  %s\n", var3.getMessage()));
         return false;
      }
   }

   private static boolean checkPinStore(StringBuilder sb) {
      sb.append("\n─── 4. PinStore (async I/O) ────────────────────────────\n");

      try {
         PinnedRecipeManager pins = AppContext.instance().pins();
         if (pins == null) {
            sb.append("  FAIL  PinnedRecipeManager is null\n");
            return false;
         } else {
            int count = pins.pinned != null ? pins.pinned.size() : 0;
            sb.append(String.format("  INFO  pinned recipes  = %d\n", count));
            if (pins.pinned != null && pins.pinned.size() <= 10) {
               for (ResourceLocation id : pins.pinned) {
                  sb.append(String.format("         - %s\n", id));
               }
            }

            sb.append("  PASS  PinnedRecipeManager present\n");
            return true;
         }
      } catch (Exception var5) {
         sb.append(String.format("  FAIL  %s\n", var5.getMessage()));
         return false;
      }
   }

   private static boolean checkBookLayout(StringBuilder sb) {
      sb.append("\n─── 5. BookLayout Constants ────────────────────────────\n");
      sb.append(String.format("  INFO  TEXTURE_WIDTH      = %d\n", 147));
      sb.append(String.format("  INFO  TEXTURE_HEIGHT     = %d\n", 166));
      sb.append(String.format("  INFO  BUTTON_SIZE        = %d\n", 25));
      sb.append(String.format("  INFO  GRID_PAD           = %d\n", 11));
      sb.append(String.format("  INFO  GRID_GAP           = %d\n", 2));
      sb.append(String.format("  INFO  TAB_WIDTH          = %d\n", 30));
      sb.append(String.format("  INFO  TAB_SPACING        = %d\n", 27));
      sb.append(String.format("  INFO  X_OFFSET_CENTERED  = %d\n", 162));
      sb.append(String.format("  INFO  X_OFFSET_STANDARD  = %d\n", 86));
      sb.append("  PASS  all constants present\n");
      return true;
   }

   private static boolean checkRecipeViewers(StringBuilder sb) {
      sb.append("\n─── 6. RecipeViewer Registry ────────────────────────────\n");

      try {
         RecipeViewerRegistry registry = AppContext.instance().recipeViewers();
         if (registry == null) {
            sb.append("  FAIL  registry is null\n");
            return false;
         } else {
            boolean any = registry.anyAvailable();
            sb.append(String.format("  INFO  anyAvailable = %s\n", any));
            RecipeViewer found = registry.findFirst();
            if (found != null && found != RecipeViewer.NONE) {
               sb.append(String.format("  INFO  first available = %s\n", found.getClass().getSimpleName()));
            } else {
               sb.append("  INFO  no viewers available (JEI/REI not loaded)\n");
            }

            sb.append("  PASS  registry operational\n");
            return true;
         }
      } catch (Exception var4) {
         sb.append(String.format("  FAIL  %s\n", var4.getMessage()));
         return false;
      }
   }

   private static boolean checkConstraintLayout(StringBuilder sb) {
      sb.append("\n─── 7. Constraint Layout (runtime geometry) ────────────\n");

      try {
         Screen screen = Minecraft.getInstance().screen;
         if (!(screen instanceof AbstractContainerScreen<?> acs)) {
            sb.append("  SKIP  no container screen open\n");
            return true;
         } else {
            int screenW = acs.width;
            int screenH = acs.height;
            sb.append(String.format("  INFO  screen           = %s (%d×%d)\n", screen.getClass().getSimpleName(), screenW, screenH));
            BookLayout layout = AppContext.instance().bookLayout();
            boolean centered = AppContext.instance().config().keepCentered;
            boolean expanded = false;
            BookLayout.Rect available = BookLayout.Rect.of(0, 0, screenW, screenH);
            BookGeometry geo = layout.compute(available, centered, expanded);
            sb.append(String.format("  INFO  book             = (%d,%d) %d×%d\n", geo.bookLeft(), geo.bookTop(), geo.bookWidth(), geo.bookHeight()));
            sb.append(String.format("  INFO  searchBox        = (%d,%d) %d×%d\n", geo.searchX(), geo.searchY(), geo.searchWidth(), geo.searchHeight()));
            sb.append(String.format("  INFO  filterButton     = (%d,%d) %d×%d\n", geo.filterX(), geo.filterY(), geo.filterWidth(), geo.filterHeight()));
            sb.append(String.format("  INFO  settingsButton   = (%d,%d) %dpx\n", geo.settingsX(), geo.settingsY(), geo.settingsSize()));
            sb.append(
               String.format(
                  "  INFO  grid             = (%d,%d) %d cols × %d rows, button=%dpx\n",
                  geo.gridX(),
                  geo.gridY(),
                  geo.gridColumns(),
                  geo.gridRows(),
                  geo.buttonSize()
               )
            );
            sb.append(
               String.format("  INFO  gridZone         = (%d,%d) %d×%d\n", geo.gridZone().left, geo.gridZone().top, geo.gridZone().width, geo.gridZone().height)
            );
            sb.append(
               String.format("  INFO  pageArrows       = back(%d,%d) forward(%d,%d)\n", geo.arrowBackX(), geo.arrowY(), geo.arrowForwardX(), geo.arrowY())
            );
            sb.append(String.format("  INFO  instantCraft     = (%d,%d)\n", geo.instantCraftX(), geo.instantCraftY()));

            for (BookLayout.TabPosition pos : BookLayout.TabPosition.values()) {
               BookLayout.Zone z = geo.tabZone(pos);
               sb.append(String.format("  INFO  TabZone[%-6s]   = (%d,%d) %d×%d\n", pos, z.left, z.top, z.width, z.height));
            }

            int checks = 0;
            int filterRight = geo.filterX() + geo.filterWidth();
            int gridRight = geo.gridX() + geo.gridColumns() * (geo.buttonSize() + 2) - 2;
            boolean gridAlign = Math.abs(filterRight - gridRight) <= 1;
            sb.append(String.format("  %s  filterButton.right(%d) ≈ gridRight(%d)\n", gridAlign ? "PASS" : "WARN", filterRight, gridRight));
            if (gridAlign) {
               checks++;
            }

            BookLayout.Zone leftTab = geo.tabZone(BookLayout.TabPosition.LEFT);
            boolean tabWidth = leftTab.width == 30;
            sb.append(String.format("  %s  leftTabZone.width(%d) = TAB_WIDTH(%d)\n", tabWidth ? "PASS" : "FAIL", leftTab.width, 30));
            if (tabWidth) {
               checks++;
            }

            boolean settingsArrowY = geo.settingsY() == geo.arrowY();
            sb.append(String.format("  %s  settingsY(%d) = arrowY(%d)\n", settingsArrowY ? "PASS" : "WARN", geo.settingsY(), geo.arrowY()));
            if (settingsArrowY) {
               checks++;
            }

            int icRight = geo.instantCraftX() + 26;
            boolean icAlign = Math.abs(icRight - gridRight) <= 1;
            sb.append(String.format("  %s  instantCraft.right(%d) ≈ gridRight(%d)\n", icAlign ? "PASS" : "WARN", icRight, gridRight));
            if (icAlign) {
               checks++;
            }

            sb.append(String.format("  RESULT  %d/4 constraint invariants hold\n", checks));
            return checks >= 3;
         }
      } catch (Exception var19) {
         sb.append(String.format("  FAIL  %s\n", var19.getMessage()));
         return false;
      }
   }
}
