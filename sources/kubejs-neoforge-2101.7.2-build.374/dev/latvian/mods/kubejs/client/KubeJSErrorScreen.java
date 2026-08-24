package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.betteradvancedtooltips.BATIcons;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextIcons;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.TimeJS;
import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

public class KubeJSErrorScreen extends Screen {
   public final Screen lastScreen;
   public final ScriptType scriptType;
   public final Path logFile;
   public final List<ConsoleLine> errors;
   public final List<ConsoleLine> warnings;
   public final boolean canClose;
   public List<ConsoleLine> viewing;
   private KubeJSErrorScreen.ErrorList list;

   public KubeJSErrorScreen(
      Screen lastScreen, ScriptType scriptType, @Nullable Path logFile, List<ConsoleLine> errors, List<ConsoleLine> warnings, boolean canClose
   ) {
      super(Component.empty());
      this.lastScreen = lastScreen;
      this.scriptType = scriptType;
      this.logFile = logFile;
      this.errors = errors;
      this.warnings = warnings;
      this.canClose = canClose;
      this.viewing = errors.isEmpty() && !warnings.isEmpty() ? warnings : errors;
   }

   public KubeJSErrorScreen(Screen lastScreen, ConsoleJS console, boolean canClose) {
      this(lastScreen, console.scriptType, console.scriptType.getLogFile(), new ArrayList<>(console.errors), new ArrayList<>(console.warnings), canClose);
   }

   public Component getNarrationMessage() {
      return Component.literal("There were KubeJS " + this.scriptType.name + " errors!");
   }

   protected void init() {
      super.init();
      this.list = new KubeJSErrorScreen.ErrorList(this, this.minecraft, this.width, this.height, 32, this.height - 32, this.viewing);
      this.addWidget(this.list);
      int i = this.height - 26;
      Button openLog;
      if (CommonProperties.get().startupErrorReportUrl.isBlank()) {
         openLog = (Button)this.addRenderableWidget(
            Button.builder(Component.literal("Open Log File"), this::openLog).bounds(this.width / 2 - 155, i, 150, 20).build()
         );
         this.addRenderableWidget(
            Button.builder(Component.literal(this.canClose ? "Close" : "Quit"), this::quit).bounds(this.width / 2 - 155 + 160, i, 150, 20).build()
         );
      } else {
         openLog = (Button)this.addRenderableWidget(
            Button.builder(Component.literal("Open Log File"), this::openLog).bounds(this.width / 4 - 55, i, 100, 20).build()
         );
         this.addRenderableWidget(Button.builder(Component.literal("Report"), this::report).bounds(this.width / 2 - 50, i, 100, 20).build());
         this.addRenderableWidget(
            Button.builder(Component.literal(this.canClose ? "Close" : "Quit"), this::quit).bounds(this.width * 3 / 4 - 45, i, 100, 20).build()
         );
      }

      openLog.active = this.logFile != null;
      Button viewOther = (Button)this.addRenderableWidget(
         Button.builder(
               Component.literal(this.viewing == this.errors ? "View Warnings [" + this.warnings.size() + "]" : "View Errors [" + this.errors.size() + "]"),
               this::viewOther
            )
            .bounds(this.width - 107, 7, 100, 20)
            .build()
      );
      if (this.errors.isEmpty() || this.warnings.isEmpty()) {
         viewOther.active = false;
      }
   }

   private void quit(Button button) {
      if (this.canClose) {
         this.onClose();
      } else {
         this.minecraft.stop();
      }
   }

   private void report(Button button) {
      this.handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, CommonProperties.get().startupErrorReportUrl)));
   }

   private void openLog(Button button) {
      if (this.logFile != null) {
         this.handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_FILE, this.logFile.toAbsolutePath().toString())));
      }
   }

   private void viewOther(Button button) {
      this.viewing = this.viewing == this.errors ? this.warnings : this.errors;
      this.repositionElements();
   }

   public void render(GuiGraphics guiGraphics, int mx, int my, float delta) {
      super.render(guiGraphics, mx, my, delta);
      this.list.render(guiGraphics, mx, my, delta);
      guiGraphics.drawCenteredString(
         this.font, "KubeJS " + this.scriptType.name + " script " + (this.viewing == this.errors ? "errors" : "warnings"), this.width / 2, 12, 16777215
      );
      if (this.errors.isEmpty() && this.warnings.isEmpty()) {
         guiGraphics.drawCenteredString(this.font, "No errors or warnings found!", this.width / 2, this.height / 2 - 6, 6750054);
      }
   }

   public boolean shouldCloseOnEsc() {
      return this.canClose;
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public static class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<KubeJSErrorScreen.Entry> {
      private final KubeJSErrorScreen.ErrorList errorList;
      private final Minecraft minecraft;
      private final ConsoleLine line;
      private long lastClickTime;
      private final FormattedCharSequence indexText;
      private final FormattedCharSequence scriptLineText;
      private final FormattedCharSequence timestampText;
      private final List<FormattedCharSequence> errorText;
      private final List<FormattedCharSequence> firstStackTraceLine;
      private final List<FormattedCharSequence> stackTraceText;
      private final List<FormattedCharSequence> fullStackTraceText;
      private final int totalStackTraceSize;

      public Entry(KubeJSErrorScreen.ErrorList errorList, Minecraft minecraft, int index, ConsoleLine line, Calendar calendar) {
         this.errorList = errorList;
         this.minecraft = minecraft;
         this.line = line;
         this.indexText = Component.literal("#" + (index + 1)).getVisualOrderText();
         ArrayList<SourceLine> sourceLines = new ArrayList<>(line.sourceLines);
         ArrayList<String> scriptLineTextList = new ArrayList<>();

         for (int i = 0; i < sourceLines.size(); i++) {
            if (!sourceLines.get(i).source().endsWith(".java")) {
               if (i >= 3) {
                  scriptLineTextList.add("...");
                  break;
               }

               scriptLineTextList.add(sourceLines.get(i).toString());
            }
         }

         if (scriptLineTextList.isEmpty()) {
            scriptLineTextList.add(this.line.type == LogType.WARN ? "Internal Warning" : "Internal Error");
         }

         this.scriptLineText = Component.literal(String.join(" < ", scriptLineTextList)).getVisualOrderText();
         StringBuilder sb = new StringBuilder();
         calendar.setTimeInMillis(line.timestamp);
         TimeJS.appendTimestamp(sb, calendar);
         this.timestampText = Component.literal(sb.toString()).getVisualOrderText();
         int maxWidth = minecraft.getWindow().getGuiScaledWidth() - 24;
         this.errorText = new ArrayList<>(minecraft.font.split(Component.literal(line.message), errorList.getRowWidth()).stream().limit(3L).toList());
         if (line.stackTrace.isEmpty()) {
            this.firstStackTraceLine = List.of();
            this.stackTraceText = List.of();
            this.fullStackTraceText = List.of();
            this.totalStackTraceSize = 0;
         } else {
            this.firstStackTraceLine = new ArrayList<>();
            this.stackTraceText = new ArrayList<>();
            this.fullStackTraceText = new ArrayList<>();

            for (String l1 : ((String)line.stackTrace.getFirst()).split("\n")) {
               this.firstStackTraceLine.addAll(minecraft.font.split(Component.literal(l1).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)), maxWidth));
            }

            label72:
            for (int ix = 1; ix < line.stackTrace.size(); ix++) {
               for (String l1 : line.stackTrace.get(ix).split("\n")) {
                  for (FormattedCharSequence l2 : minecraft.font
                     .split(Component.literal(l1).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)), 2147483647)) {
                     this.stackTraceText.add(l2);
                     if (this.stackTraceText.size() >= 4) {
                        break label72;
                     }
                  }
               }
            }

            for (int ix = 1; ix < line.stackTrace.size(); ix++) {
               for (String l1 : line.stackTrace.get(ix).split("\n")) {
                  this.fullStackTraceText
                     .addAll(minecraft.font.split(Component.literal(l1).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)), maxWidth));
               }
            }

            this.totalStackTraceSize = this.firstStackTraceLine.size() + this.fullStackTraceText.size();
         }
      }

      public Component getNarration() {
         return Component.empty();
      }

      public void render(GuiGraphics g, int idx, int y, int x, int w, int h, int mx, int my, boolean hovered, float delta) {
         int col = this.line.type == LogType.ERROR ? 16735075 : 16759643;
         g.drawString(this.minecraft.font, this.indexText, x + 1, y + 1, col);
         g.drawCenteredString(this.minecraft.font, this.scriptLineText, x + w / 2, y + 1, 16777215);
         g.drawString(this.minecraft.font, this.timestampText, x + w - this.minecraft.font.width(this.timestampText) - 4, y + 1, 6710886);

         for (int i = 0; i < this.errorText.size(); i++) {
            g.drawString(this.minecraft.font, this.errorText.get(i), x + 1, y + 13 + i * 10, col);
         }

         if (hovered && this.totalStackTraceSize > 0) {
            if (my < y + 10 && this.line.sourceLines.size() >= 3) {
               ArrayList<FormattedCharSequence> lines = new ArrayList<>();
               int ln = 0;

               for (SourceLine line : this.line.sourceLines) {
                  if (line.line() > 0 && line.source().endsWith(".js")) {
                     ln = line.line();
                     break;
                  }
               }

               if (ln > 0) {
                  MutableComponent comp = Component.empty();
                  comp.append("Double-click to open file");
                  if (EditorExt.isKnownVSCode()) {
                     comp.append(" in ");
                     comp.append(TextIcons.VSCODE);
                     comp.append(BATIcons.SMALL_SPACE);
                     comp.append(Component.literal("VSCode").withColor(2271218));
                  }

                  lines.addAll(this.minecraft.font.split(comp, 1000));
               }

               for (SourceLine linex : this.line.sourceLines) {
                  lines.add(Component.empty().append(Component.literal(linex.source()).kjs$gray()).append("#" + linex.line()).getVisualOrderText());
               }

               this.errorList.screen.setTooltipForNextRenderPass(lines);
            } else {
               ArrayList<FormattedCharSequence> list = new ArrayList<>(this.firstStackTraceLine);
               if (Screen.hasShiftDown()) {
                  list.addAll(this.fullStackTraceText);
               } else {
                  list.addAll(this.stackTraceText);
               }

               this.errorList.screen.setTooltipForNextRenderPass(list);
            }
         }
      }

      public boolean mouseClicked(double d, double e, int i) {
         this.errorList.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            if (i == 1) {
               this.minecraft.keyboardHandler.setClipboard(String.join("\n", this.line.stackTrace));
            } else {
               this.open();
            }

            return true;
         } else {
            this.lastClickTime = Util.getMillis();
            return true;
         }
      }

      private String fixSource(@Nullable String source) {
         if (source != null && !source.isEmpty()) {
            int c = source.indexOf(58);
            if (c >= 0) {
               return source.substring(c + 1);
            }
         }

         return source;
      }

      public void open() {
         Path path = this.line.externalFile == null
            ? (
               !this.line.sourceLines.isEmpty() && !this.line.sourceLines.iterator().next().source().isEmpty()
                  ? this.line.console.scriptType.path.resolve(this.fixSource(this.line.sourceLines.iterator().next().source()))
                  : null
            )
            : this.line.externalFile;
         if (path != null && Files.exists(path)) {
            try {
               if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE_FILE_DIR)) {
                  throw new IllegalStateException("Error");
               }

               Desktop.getDesktop().browseFileDirectory(path.toFile());
            } catch (Exception var6) {
               if (Files.isRegularFile(path) && !path.getFileName().toString().endsWith(".js")) {
                  path = path.getParent();
               }

               int ln = 1;

               for (SourceLine line : this.line.sourceLines) {
                  if (line.line() > 0 && line.source().endsWith(".js")) {
                     ln = line.line();
                     break;
                  }
               }

               EditorExt.openFile(path, ln, 0);
            }
         }
      }
   }

   public static class ErrorList extends ObjectSelectionList<KubeJSErrorScreen.Entry> {
      public final KubeJSErrorScreen screen;
      public final List<ConsoleLine> lines;

      public ErrorList(KubeJSErrorScreen screen, Minecraft minecraft, int width, int height, int top, int bottom, List<ConsoleLine> lines) {
         super(minecraft, width, bottom - top, top, 48);
         this.screen = screen;
         this.lines = lines;
         Calendar calendar = Calendar.getInstance();

         for (int i = 0; i < lines.size(); i++) {
            this.addEntry(new KubeJSErrorScreen.Entry(this, minecraft, i, lines.get(i), calendar));
         }
      }

      public boolean keyPressed(int i, int j, int k) {
         if (CommonInputs.selected(i)) {
            KubeJSErrorScreen.Entry sel = (KubeJSErrorScreen.Entry)this.getSelected();
            if (sel != null) {
               sel.open();
               return true;
            }
         }

         return super.keyPressed(i, j, k);
      }

      public int getRowWidth() {
         return (int)(this.width * 0.93);
      }
   }
}
