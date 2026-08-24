package net.joefoxe.hexerei.data.books;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.PaintDataToServer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class PaintSystem {
   public static BufferedImage clipboardImage;
   public static Rectangle clipboardBounds;
   public static BufferedImage clipboardMask;
   private final List<PaintSystem.Layer> layers = new ArrayList<>();
   private PaintSystem.Layer activeLayer;
   public final ResourceLocation parentLocation;
   private UUID uuid;
   public int width;
   public int height;
   private static PaintSystem.Colors colors = new PaintSystem.Colors(List.of());
   private float colorsVisibility;
   private float colorsVisibilityOld;
   private PaintSystem.Brush brush;
   private PaintSystem.Selection selection;
   public List<PaintSystem.Button> buttons = new ArrayList<>();
   private PaintSystem.ValueSliders valueSliders;
   public BufferedImage compositeImage;
   public BufferedImage strokeMask;
   public PaintSystem.Brush.Type strokeType;
   private BufferedImage movingSelection;
   private PaintSystem.Pos2i movingSelectionOffset = new PaintSystem.Pos2i(0, 0);
   private PaintSystem.Pos2i movingSelectionClickedPos = new PaintSystem.Pos2i(0, 0);
   private boolean skipNextRelease;
   private boolean dirty;
   private boolean updateToServer = false;
   private static PaintSystem.Tool currentTool = PaintSystem.Tool.BRUSH;
   private final List<PaintSystem.Tool> tools = Arrays.asList(PaintSystem.Tool.values());
   public float cursorX = -10.0F;
   public float cursorY = -10.0F;
   public float cursorXOld = -10.0F;
   public float cursorYOld = -10.0F;
   public boolean shouldTick = false;
   public boolean toolsVisible = false;
   public float toolVisibility = 0.0F;
   public float toolVisibilityOld = 0.0F;
   public boolean locked = false;
   public UUID lockedByUUID = new UUID(0L, 0L);
   public Component lockedByName = Component.empty();
   private PaintSystem.DrawAction currentDrawAction = null;
   private Rectangle initialSelectionBounds = null;
   private BufferedImage initialSelectionMask = null;
   public PaintSystem.ActionManager actionManager = new PaintSystem.ActionManager();

   public PaintSystem(int width, int height, ResourceLocation parentLocation, UUID uuid) {
      this.parentLocation = parentLocation;
      this.uuid = uuid;
      this.width = width;
      this.height = height;
      this.brush = new PaintSystem.Brush(this);
      this.selection = new PaintSystem.Selection(this);
      this.addLayer(width, height);
      this.valueSliders = new PaintSystem.ValueSliders(this);
      this.valueSliders.updateColorSliders(colors.getColor());
      this.valueSliders.updateHardnessSlider(PaintSystem.Brush.hardness);
      this.valueSliders.updateBrushSizeSlider(PaintSystem.Brush.size);
      this.valueSliders.updateToleranceSlider(PaintSystem.Brush.tolerance);
      this.movingSelection = null;
      this.buttons
         .add(
            new PaintSystem.Button(
               -0.5F,
               0.0F,
               5.5F,
               0.0F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/selection_box.png",
               "hexerei:textures/book/paint_tools/selection_box_hover.png",
               "hexerei:textures/book/paint_tools/selection_box.png",
               paintSystem -> paintSystem.setCurrentTool(PaintSystem.Tool.SELECTION),
               Component.translatable("Select").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.getCurrentTool() == PaintSystem.Tool.SELECTION,
               ps -> false,
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               -0.5F,
               0.8F,
               5.5F,
               0.8F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/magic_wand.png",
               "hexerei:textures/book/paint_tools/magic_wand_hover.png",
               "hexerei:textures/book/paint_tools/magic_wand.png",
               paintSystem -> paintSystem.setCurrentTool(PaintSystem.Tool.MAGIC_WAND),
               Component.translatable("Magic Wand").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.getCurrentTool() == PaintSystem.Tool.MAGIC_WAND,
               ps -> false,
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               -0.5F,
               1.6F,
               5.5F,
               1.6F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/move.png",
               "hexerei:textures/book/paint_tools/move_hover.png",
               "hexerei:textures/book/paint_tools/move.png",
               paintSystem -> paintSystem.setCurrentTool(PaintSystem.Tool.MOVE),
               Component.translatable("Move").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.getCurrentTool() == PaintSystem.Tool.MOVE,
               ps -> false,
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               -0.5F,
               2.4F,
               5.5F,
               2.4F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/brush.png",
               "hexerei:textures/book/paint_tools/brush_hover.png",
               "hexerei:textures/book/paint_tools/brush.png",
               paintSystem -> paintSystem.setCurrentTool(PaintSystem.Tool.BRUSH),
               Component.translatable("Brush").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.getCurrentTool() == PaintSystem.Tool.BRUSH,
               ps -> false,
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               -0.5F,
               3.2F,
               5.5F,
               3.2F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/eraser.png",
               "hexerei:textures/book/paint_tools/eraser_hover.png",
               "hexerei:textures/book/paint_tools/eraser.png",
               paintSystem -> paintSystem.setCurrentTool(PaintSystem.Tool.ERASER),
               Component.translatable("Eraser").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.getCurrentTool() == PaintSystem.Tool.ERASER,
               ps -> false,
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               -0.5F,
               4.0F,
               5.5F,
               4.0F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/fill.png",
               "hexerei:textures/book/paint_tools/fill_hover.png",
               "hexerei:textures/book/paint_tools/fill.png",
               paintSystem -> paintSystem.setCurrentTool(PaintSystem.Tool.FILL),
               Component.translatable("Fill").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.getCurrentTool() == PaintSystem.Tool.FILL,
               ps -> false,
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               -0.5F,
               4.8F,
               5.5F,
               4.8F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/color_picker.png",
               "hexerei:textures/book/paint_tools/color_picker_hover.png",
               "hexerei:textures/book/paint_tools/color_picker.png",
               paintSystem -> paintSystem.setCurrentTool(PaintSystem.Tool.EYEDROPPER),
               Component.translatable("Color Picker").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.getCurrentTool() == PaintSystem.Tool.EYEDROPPER,
               ps -> false,
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.ToggleButton(
               -0.5F,
               6.2F,
               5.5F,
               6.2F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/visible.png",
               "hexerei:textures/book/paint_tools/visible_hover.png",
               "hexerei:textures/book/paint_tools/visible.png",
               "hexerei:textures/book/paint_tools/visible_toggled.png",
               "hexerei:textures/book/paint_tools/visible_toggled_hover.png",
               "hexerei:textures/book/paint_tools/visible_toggled.png",
               paintSystem -> this.toolsVisible = !this.toolsVisible,
               paintSystem -> this.toolsVisible = !this.toolsVisible,
               Component.translatable("Tool Visibility").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> false,
               ps -> this.locked,
               ps -> !this.locked,
               ps -> !this.toolsVisible
            )
         );
      AtomicReference<Float> lockY = new AtomicReference<>(0.0F);
      AtomicReference<Float> lockYO = new AtomicReference<>(0.0F);
      AtomicReference<Float> lockYTarget = new AtomicReference<>(0.0F);
      this.buttons
         .add(
            new PaintSystem.ToggleButton(
               (ps, partial) -> -0.5F,
               (ps, partial) -> 5.4F + HexereiUtil.easeInOutCubic(Mth.lerp(partial, lockYO.get(), lockY.get())) * 0.8F,
               (ps, partial) -> 5.5F,
               (ps, partial) -> 5.4F + HexereiUtil.easeInOutCubic(Mth.lerp(partial, lockYO.get(), lockY.get())) * 0.8F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/locked.png",
               "hexerei:textures/book/paint_tools/locked_hover.png",
               "hexerei:textures/book/paint_tools/locked.png",
               "hexerei:textures/book/paint_tools/locked_toggled.png",
               "hexerei:textures/book/paint_tools/locked_toggled_hover.png",
               "hexerei:textures/book/paint_tools/locked_toggled.png",
               paintSystem -> {
                  if (Minecraft.getInstance().player != null) {
                     this.locked = true;
                     this.lockedByName = Minecraft.getInstance().player.getName();
                     this.lockedByUUID = Minecraft.getInstance().player.getUUID();
                     HexereiPacketHandler.sendToServer(new PaintDataToServer(paintSystem.toPaintData()));
                  }
               },
               paintSystem -> {
                  if (Minecraft.getInstance().player != null && this.lockedByUUID != null && this.lockedByUUID.equals(Minecraft.getInstance().player.getUUID())
                     )
                   {
                     this.locked = false;
                     this.lockedByName = Component.empty();
                     this.lockedByUUID = new UUID(0L, 0L);
                     HexereiPacketHandler.sendToServer(new PaintDataToServer(paintSystem.toPaintData()));
                  }
               },
               Component.translatable("Lock").withStyle(ChatFormatting.GRAY),
               ps -> {
                  lockYO.set(lockY.get());
                  lockYTarget.set(ps.locked ? 1.0F : 0.0F);
                  lockY.set(HexereiUtil.moveTo(lockY.get(), lockYTarget.get(), Math.abs(lockYTarget.get() - lockY.get()) / 5.0F + 0.01F));
               },
               ps -> false,
               ps -> false,
               ps -> !this.toolsVisible,
               ps -> this.locked
            ) {
               @Override
               public List<Component> getTooltipList() {
                  List<Component> components = new ArrayList<>();
                  if (PaintSystem.this.locked) {
                     components.add(Component.translatable("Unlock").withStyle(ChatFormatting.GRAY));
                     components.add(Component.translatable("Locked by: ").withStyle(ChatFormatting.DARK_GRAY).append(PaintSystem.this.lockedByName));
                  } else {
                     components.add(Component.translatable("Lock").withStyle(ChatFormatting.GRAY));
                  }

                  return components;
               }
            }
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               0.6F,
               -1.0F,
               0.6F,
               -1.0F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/undo.png",
               "hexerei:textures/book/paint_tools/undo_hover.png",
               "hexerei:textures/book/paint_tools/undo_disabled.png",
               paintSystem -> paintSystem.actionManager.undo(),
               Component.translatable("Undo").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> false,
               ps -> ps.actionManager.undoStack.isEmpty(),
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               1.5F,
               -1.0F,
               1.5F,
               -1.0F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/redo.png",
               "hexerei:textures/book/paint_tools/redo_hover.png",
               "hexerei:textures/book/paint_tools/redo_disabled.png",
               paintSystem -> paintSystem.actionManager.redo(),
               Component.translatable("Redo").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> false,
               ps -> ps.actionManager.redoStack.isEmpty(),
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               2.4F,
               -1.0F,
               2.4F,
               -1.0F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/cut.png",
               "hexerei:textures/book/paint_tools/cut_hover.png",
               "hexerei:textures/book/paint_tools/cut_disabled.png",
               PaintSystem::cutSelectionToClipboard,
               Component.translatable("Cut").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> false,
               ps -> ps.selection.isEmpty(),
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               3.3F,
               -1.0F,
               3.3F,
               -1.0F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/copy.png",
               "hexerei:textures/book/paint_tools/copy_hover.png",
               "hexerei:textures/book/paint_tools/copy_disabled.png",
               PaintSystem::copySelectionToClipboard,
               Component.translatable("Copy").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> false,
               ps -> ps.selection.isEmpty(),
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               4.2F,
               -1.0F,
               4.2F,
               -1.0F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/paste.png",
               "hexerei:textures/book/paint_tools/paste_hover.png",
               "hexerei:textures/book/paint_tools/paste_disabled.png",
               PaintSystem::pasteClipboard,
               Component.translatable("Paste").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> false,
               ps -> clipboardImage == null,
               ps -> this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               0.6F,
               7.1F,
               1.4F,
               7.1F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/magic_wand_replace.png",
               "hexerei:textures/book/paint_tools/magic_wand_replace_hover.png",
               "hexerei:textures/book/paint_tools/magic_wand_replace.png",
               ps -> ps.selection.setType(PaintSystem.Selection.Type.REPLACE),
               Component.translatable("Replace").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.selection.getType() == PaintSystem.Selection.Type.REPLACE,
               ps -> ps.getCurrentTool() != PaintSystem.Tool.MAGIC_WAND,
               ps -> ps.getCurrentTool() == PaintSystem.Tool.MAGIC_WAND && this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               1.5F,
               7.1F,
               2.3F,
               7.1F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/magic_wand_add.png",
               "hexerei:textures/book/paint_tools/magic_wand_add_hover.png",
               "hexerei:textures/book/paint_tools/magic_wand_add.png",
               ps -> ps.selection.setType(PaintSystem.Selection.Type.ADD),
               Component.translatable("Add").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.selection.getType() == PaintSystem.Selection.Type.ADD,
               ps -> ps.getCurrentTool() != PaintSystem.Tool.MAGIC_WAND,
               ps -> ps.getCurrentTool() == PaintSystem.Tool.MAGIC_WAND && this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               2.4F,
               7.1F,
               3.2F,
               7.1F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/magic_wand_remove.png",
               "hexerei:textures/book/paint_tools/magic_wand_remove_hover.png",
               "hexerei:textures/book/paint_tools/magic_wand_remove.png",
               ps -> ps.selection.setType(PaintSystem.Selection.Type.REMOVE),
               Component.translatable("Remove").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.selection.getType() == PaintSystem.Selection.Type.REMOVE,
               ps -> ps.getCurrentTool() != PaintSystem.Tool.MAGIC_WAND,
               ps -> ps.getCurrentTool() == PaintSystem.Tool.MAGIC_WAND && this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.ToggleButton(
               3.55F,
               6.95F,
               4.35F,
               6.95F,
               18.0F,
               18.0F,
               value -> value / 2.0F * 0.9F,
               "hexerei:textures/book/paint_tools/global_toggled.png",
               "hexerei:textures/book/paint_tools/global_toggled_hover.png",
               "hexerei:textures/book/paint_tools/global_toggled.png",
               "hexerei:textures/book/paint_tools/global_detoggled.png",
               "hexerei:textures/book/paint_tools/global_detoggled_hover.png",
               "hexerei:textures/book/paint_tools/global_detoggled.png",
               ps -> ps.selection.setGlobal(!ps.selection.getGlobal()),
               ps -> ps.selection.setGlobal(!ps.selection.getGlobal()),
               Component.translatable("Toggle Global").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> false,
               ps -> ps.getCurrentTool() != PaintSystem.Tool.MAGIC_WAND,
               ps -> ps.getCurrentTool() == PaintSystem.Tool.MAGIC_WAND && this.toolsVisible,
               ps -> !ps.selection.getGlobal()
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               0.6F,
               7.1F,
               1.4F,
               7.1F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/selection_box.png",
               "hexerei:textures/book/paint_tools/selection_box_hover.png",
               "hexerei:textures/book/paint_tools/selection_box.png",
               ps -> ps.selection.setType(PaintSystem.Selection.Type.REPLACE),
               Component.translatable("Replace").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.selection.getType() == PaintSystem.Selection.Type.REPLACE,
               ps -> false,
               ps -> ps.getCurrentTool() == PaintSystem.Tool.SELECTION && this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               1.5F,
               7.1F,
               2.3F,
               7.1F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/selection_box_add.png",
               "hexerei:textures/book/paint_tools/selection_box_add_hover.png",
               "hexerei:textures/book/paint_tools/selection_box_add.png",
               ps -> ps.selection.setType(PaintSystem.Selection.Type.ADD),
               Component.translatable("Add").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.selection.getType() == PaintSystem.Selection.Type.ADD,
               ps -> ps.getCurrentTool() != PaintSystem.Tool.SELECTION,
               ps -> ps.getCurrentTool() == PaintSystem.Tool.SELECTION && this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               2.4F,
               7.1F,
               3.2F,
               7.1F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/selection_box_remove.png",
               "hexerei:textures/book/paint_tools/selection_box_remove_hover.png",
               "hexerei:textures/book/paint_tools/selection_box_remove.png",
               ps -> ps.selection.setType(PaintSystem.Selection.Type.REMOVE),
               Component.translatable("Remove").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> ps.selection.getType() == PaintSystem.Selection.Type.REMOVE,
               ps -> ps.getCurrentTool() != PaintSystem.Tool.SELECTION,
               ps -> ps.getCurrentTool() == PaintSystem.Tool.SELECTION && this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.Button(
               3.3F,
               7.1F,
               4.1F,
               7.1F,
               32.0F,
               32.0F,
               value -> value / 2.0F * 1.01F,
               "hexerei:textures/book/paint_tools/selection_box_deselect.png",
               "hexerei:textures/book/paint_tools/selection_box_deselect_hover.png",
               "hexerei:textures/book/paint_tools/selection_box_deselect_disabled.png",
               ps -> ps.selection.deselect(),
               Component.translatable("Clear").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> false,
               ps -> this.selection.isEmpty(),
               ps -> ps.getCurrentTool() == PaintSystem.Tool.SELECTION && this.toolsVisible
            )
         );
      this.buttons
         .add(
            new PaintSystem.ToggleButton(
               3.55F,
               6.95F,
               4.35F,
               6.95F,
               18.0F,
               18.0F,
               value -> value / 2.0F,
               "hexerei:textures/book/paint_tools/global_toggled.png",
               "hexerei:textures/book/paint_tools/global_toggled_hover.png",
               "hexerei:textures/book/paint_tools/global_toggled.png",
               "hexerei:textures/book/paint_tools/global_detoggled.png",
               "hexerei:textures/book/paint_tools/global_detoggled_hover.png",
               "hexerei:textures/book/paint_tools/global_detoggled.png",
               ps -> ps.brush.setGlobal(!ps.brush.getGlobal()),
               ps -> ps.brush.setGlobal(!ps.brush.getGlobal()),
               Component.translatable("Toggle Global").withStyle(ChatFormatting.GRAY),
               ps -> {},
               ps -> false,
               ps -> ps.getCurrentTool() != PaintSystem.Tool.FILL,
               ps -> ps.getCurrentTool() == PaintSystem.Tool.FILL && this.toolsVisible,
               ps -> !ps.brush.getGlobal()
            )
         );
   }

   public PaintData toPaintData() {
      List<PaintData.LayerData> layerDataList = new ArrayList<>();

      for (PaintSystem.Layer layer : this.layers) {
         int[] pixels = new int[layer.pixels.getWidth() * layer.pixels.getHeight()];
         layer.pixels.getRGB(0, 0, layer.pixels.getWidth(), layer.pixels.getHeight(), pixels, 0, layer.pixels.getWidth());
         layerDataList.add(
            new PaintData.LayerData(
               layer.pixels.getWidth(), layer.pixels.getHeight(), Arrays.stream(pixels).boxed().toList(), layer.opacity, layer.blendMode.name(), layer.name
            )
         );
      }

      return new PaintData(this.width, this.height, layerDataList, this.parentLocation, this.uuid, this.locked, this.lockedByUUID, this.lockedByName);
   }

   public void fromPaintData(PaintData data) {
      this.layers.clear();
      this.setActiveLayer(null);
      this.locked = data.locked;
      this.lockedByUUID = data.lockedByUUID;
      this.lockedByName = data.lockedByName;

      for (PaintData.LayerData layerData : data.getLayers()) {
         BufferedImage image = new BufferedImage(layerData.width(), layerData.height(), 2);
         image.setRGB(0, 0, layerData.width(), layerData.height(), layerData.pixels().stream().mapToInt(Integer::intValue).toArray(), 0, layerData.width());
         PaintSystem.Layer layer = new PaintSystem.Layer();
         layer.pixels = image;
         layer.opacity = layerData.opacity();
         layer.blendMode = PaintSystem.BlendMode.valueOf(layerData.blendMode());
         layer.name = layerData.name();
         this.addLayer(layer);
      }

      this.dirty = true;
      this.rebuildComposite();
      if (this.compositeImage != null
         && Minecraft.getInstance().getTextureManager().getTexture(this.getImageLocation()) instanceof DynamicTexture dynamicTexture) {
         dynamicTexture.setPixels(convertToNativeImage(this.compositeImage));
         dynamicTexture.upload();
      }
   }

   public static BufferedImage deepCopy(BufferedImage bi) {
      if (bi == null) {
         return null;
      } else {
         ColorModel cm = bi.getColorModel();
         boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
         WritableRaster raster = bi.copyData(null);
         return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
      }
   }

   public float getColorsVisibility(float partial) {
      return Math.max(0.0F, HexereiUtil.easeInOutCubic(Mth.lerp(partial, this.colorsVisibilityOld, this.colorsVisibility)));
   }

   public PaintSystem.ValueSliders getValueSliders() {
      return this.valueSliders;
   }

   public PaintSystem.Tool getCurrentTool() {
      return currentTool;
   }

   public void setCurrentTool(PaintSystem.Tool tool) {
      currentTool = tool;
   }

   public void setCurrentToolById(int id) {
      if (id >= 0 && id < this.tools.size()) {
         currentTool = this.tools.get(id);
      }
   }

   public void nextTool() {
      int currentIndex = this.tools.indexOf(currentTool);
      int nextIndex = (currentIndex + 1) % this.tools.size();
      currentTool = this.tools.get(nextIndex);
   }

   public void previousTool() {
      int currentIndex = this.tools.indexOf(currentTool);
      int prevIndex = (currentIndex - 1 + this.tools.size()) % this.tools.size();
      currentTool = this.tools.get(prevIndex);
   }

   public List<PaintSystem.Tool> getTools() {
      return this.tools;
   }

   public int getColor() {
      return colors.getColor();
   }

   public void setColor(int col) {
      colors.setColor(col);
   }

   public void click(float xPixel, float yPixel) {
      switch (currentTool) {
         case BRUSH:
         case ERASER:
            this.brush.cursorX = xPixel;
            this.brush.cursorY = yPixel;
            this.brush.cursorXOld = xPixel;
            this.brush.cursorYOld = yPixel;
            this.brush.type = this.getCurrentTool() == PaintSystem.Tool.ERASER ? PaintSystem.Brush.Type.ERASE : PaintSystem.Brush.Type.DRAW;
            this.brush.drawing = true;
            if (this.activeLayer != null) {
               this.currentDrawAction = new PaintSystem.DrawAction(this.activeLayer, deepCopy(this.activeLayer.pixels));
               this.actionManager.beginAction(this.currentDrawAction);
            }

            this.draw(xPixel, yPixel, xPixel, yPixel);
            break;
         case SELECTION:
            if (!this.selection.adjustingSelection) {
               this.initialSelectionBounds = new Rectangle(this.selection.bounds);
               this.initialSelectionMask = this.selection.mask != null ? deepCopy(this.selection.mask) : null;
               this.startSelection((int)xPixel, (int)yPixel);
            }
            break;
         case MOVE:
            if (!this.selection.isEmpty() && this.selection.bounds.contains(xPixel, yPixel)) {
               if (this.movingSelection == null) {
                  this.startMoveSelection((int)xPixel, (int)yPixel);
               } else {
                  this.endMoveSelection();
               }
            }
            break;
         case FILL:
            if (!(xPixel < 0.0F) && !(xPixel >= this.width) && !(yPixel < 0.0F) && !(yPixel >= this.height) && this.activeLayer != null) {
               PaintSystem.DrawAction fillAction = new PaintSystem.DrawAction(this.activeLayer, deepCopy(this.activeLayer.pixels));
               this.actionManager.beginAction(fillAction);
               this.floodFill(this.activeLayer, (int)xPixel, (int)yPixel, this.getColor(), PaintSystem.Brush.tolerance);
               fillAction.captureAfter();
               this.actionManager.commitAction();
            }
            break;
         case EYEDROPPER:
            if (!(xPixel < 0.0F) && !(xPixel >= this.width) && !(yPixel < 0.0F) && !(yPixel >= this.height)) {
               this.setColor(this.pickColor(this.activeLayer, (int)xPixel, (int)yPixel));
               this.valueSliders.updateColorSliders(this.getColor());
            }
            break;
         case MAGIC_WAND:
            if (!(xPixel < 0.0F) && !(xPixel >= this.width) && !(yPixel < 0.0F) && !(yPixel >= this.height) && this.activeLayer != null) {
               this.initialSelectionBounds = new Rectangle(this.selection.bounds);
               this.initialSelectionMask = this.selection.mask != null ? deepCopy(this.selection.mask) : null;
               BufferedImage newMask = this.selection
                  .generateMagicWandMask(this.activeLayer, (int)xPixel, (int)yPixel, PaintSystem.Brush.tolerance, this.selection.getGlobal());
               this.selection.bounds.x = 0;
               this.selection.bounds.y = 0;
               this.selection.setSelectionMask(newMask);
               if (this.selection.getType() == PaintSystem.Selection.Type.ADD) {
                  if (this.initialSelectionMask != null) {
                     Rectangle combinedBounds = this.initialSelectionBounds;
                     if (!this.selection.bounds.isEmpty()) {
                        combinedBounds = (Rectangle)this.selection.bounds.createUnion(this.initialSelectionBounds);
                     }

                     BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, 2);
                     Graphics2D g = combinedMask.createGraphics();
                     if (this.selection.mask != null) {
                        g.drawImage(
                           this.selection.mask,
                           this.selection.bounds.x - combinedBounds.x,
                           this.selection.bounds.y - combinedBounds.y,
                           this.selection.mask.getWidth(),
                           this.selection.mask.getHeight(),
                           null
                        );
                     }

                     if (this.initialSelectionMask != null) {
                        g.drawImage(
                           this.initialSelectionMask,
                           this.initialSelectionBounds.x - combinedBounds.x,
                           this.initialSelectionBounds.y - combinedBounds.y,
                           this.initialSelectionMask.getWidth(),
                           this.initialSelectionMask.getHeight(),
                           null
                        );
                     }

                     g.dispose();
                     this.selection.bounds = combinedBounds;
                     this.selection.setSelectionMask(combinedMask);
                     this.selection.cropMaskToSelection();
                  }
               } else if (this.selection.getType() == PaintSystem.Selection.Type.REMOVE) {
                  if (this.initialSelectionMask == null) {
                     this.selection.clear();
                     return;
                  }

                  Rectangle combinedBoundsx = this.selection.isEmpty()
                     ? this.initialSelectionBounds
                     : (Rectangle)this.selection.bounds.createUnion(this.initialSelectionBounds);
                  BufferedImage combinedMaskx = new BufferedImage(combinedBoundsx.width, combinedBoundsx.height, 2);
                  Graphics2D gx = combinedMaskx.createGraphics();
                  if (this.initialSelectionMask != null) {
                     gx.drawImage(
                        this.initialSelectionMask,
                        this.initialSelectionBounds.x - combinedBoundsx.x,
                        this.initialSelectionBounds.y - combinedBoundsx.y,
                        this.initialSelectionMask.getWidth(),
                        this.initialSelectionMask.getHeight(),
                        null
                     );
                  }

                  for (int y1 = 0; y1 < this.selection.bounds.height; y1++) {
                     for (int x1 = 0; x1 < this.selection.bounds.width; x1++) {
                        int globalX = this.selection.bounds.x + x1;
                        int globalY = this.selection.bounds.y + y1;
                        if (globalX >= 0
                           && globalX < this.width
                           && globalY >= 0
                           && globalY < this.height
                           && x1 >= 0
                           && x1 < this.selection.mask.getWidth()
                           && y1 >= 0
                           && y1 < this.selection.mask.getHeight()
                           && globalX - combinedBoundsx.x >= 0
                           && globalX - combinedBoundsx.x < combinedBoundsx.width
                           && globalY - combinedBoundsx.y >= 0
                           && globalY - combinedBoundsx.y < combinedBoundsx.height) {
                           int maskAlpha = this.selection.mask.getRGB(x1, y1) >> 24 & 0xFF;
                           if (maskAlpha > 0) {
                              combinedMaskx.setRGB(globalX - combinedBoundsx.x, globalY - combinedBoundsx.y, 0);
                           }
                        }
                     }
                  }

                  gx.dispose();
                  this.selection.bounds = combinedBoundsx;
                  this.selection.setSelectionMask(combinedMaskx);
                  this.selection.cropMaskToSelection();
               }

               PaintSystem.SelectionAction action = new PaintSystem.SelectionAction(
                  this.initialSelectionBounds, this.initialSelectionMask, this.selection.bounds, this.selection.mask
               );
               this.actionManager.beginAction(action);
               this.actionManager.commitAction();
            }
      }
   }

   public void hover(float xPixel, float yPixel) {
      this.cursorX = xPixel;
      this.cursorY = yPixel;
      switch (currentTool) {
         case BRUSH:
         case ERASER:
            if (this.brush.drawing) {
               this.brush.cursorX = xPixel;
               this.brush.cursorY = yPixel;
            }
            break;
         case SELECTION:
            if (this.selection.adjustingSelection) {
               this.selection.cursorX = (int)xPixel;
               this.selection.cursorY = (int)yPixel;
            }
            break;
         case MOVE:
            if (this.movingSelection != null) {
               this.updateMoveSelection((int)xPixel, (int)yPixel);
            }
      }
   }

   public void released(int xPixel, int yPixel) {
      switch (currentTool) {
         case BRUSH:
         case ERASER:
            this.brush.drawing = false;
            this.endDrawing();
            break;
         case SELECTION:
            if (this.selection.adjustingSelection) {
               if (this.initialSelectionBounds != null && !this.initialSelectionBounds.isEmpty()
                  || this.selection.bounds != null && !this.selection.bounds.isEmpty()) {
                  this.endSelection(xPixel, yPixel);
               } else {
                  this.selection.adjustingSelection = false;
               }
            }
            break;
         case MOVE:
            if (this.skipNextRelease) {
               this.skipNextRelease = false;
            } else if (this.movingSelection != null) {
               this.endMoveSelection();
            }
      }
   }

   public ResourceLocation getImageLocation() {
      return ResourceLocation.parse(this.parentLocation.toString() + "/" + this.uuid.toString());
   }

   public void tick() {
      this.toolVisibilityOld = this.toolVisibility;
      if (this.toolsVisible) {
         this.toolVisibility = HexereiUtil.moveTo(this.toolVisibility, 1.0F, 0.01F + Math.clamp(Math.abs(this.toolVisibility - 1.0F), 0.0F, 1.0F) * 0.15F);
      } else {
         this.toolVisibility = HexereiUtil.moveTo(this.toolVisibility, -1.0F, 0.01F + Math.clamp(Math.abs(this.toolVisibility - 1.0F), 0.0F, 1.0F) * 0.25F);
      }

      this.shouldTick = false;
      if (this.cursorXOld != this.cursorX || this.cursorYOld != this.cursorY) {
         this.cursorXOld = this.cursorX;
         this.cursorYOld = this.cursorY;
         if (this.getCurrentTool().shouldShowBrushSliders() || this.selection.adjustingSelection) {
            this.dirty = true;
         }
      }

      this.getColors().tick();
      this.getValueSliders().updateColorSliders(this.getColor());
      this.getValueSliders().updateBrushSizeSlider(PaintSystem.Brush.size);
      this.getValueSliders().updateToleranceSlider(PaintSystem.Brush.tolerance);
      this.getValueSliders().updateHardnessSlider(PaintSystem.Brush.hardness);
      this.colorsVisibilityOld = this.colorsVisibility;
      if (this.getCurrentTool().shouldShowColorSliders() && this.toolsVisible) {
         this.colorsVisibility = HexereiUtil.moveTo(this.colorsVisibility, 1.0F, 0.01F + Math.clamp(Math.abs(this.colorsVisibility - 1.0F), 0.0F, 1.0F) * 0.15F);
      } else {
         this.colorsVisibility = HexereiUtil.moveTo(
            this.colorsVisibility, -1.0F, 0.01F + Math.clamp(Math.abs(this.colorsVisibility - 1.0F), 0.0F, 1.0F) * 0.25F
         );
      }

      for (PaintSystem.Button button : this.buttons) {
         button.tick(this);
      }

      for (PaintSystem.ValueSlider slider : this.valueSliders.sliders) {
         slider.tick(this);
      }

      boolean anyDirty = false;
      if (this.dirty) {
         anyDirty = true;
         this.dirty = false;
      }

      if (this.brush.drawing) {
         if (this.brush.cursorXOld != this.cursorX || this.brush.cursorYOld != this.cursorY) {
            this.draw(this.brush.cursorXOld, this.brush.cursorYOld, this.cursorX, this.cursorY);
            this.brush.cursorXOld = this.brush.cursorX;
            this.brush.cursorYOld = this.brush.cursorY;
         }
      } else {
         this.endDrawing();
      }

      if (this.selection.adjustingSelection) {
         this.updateSelection(this.selection.cursorX, this.selection.cursorY);
         anyDirty = true;
      }

      for (PaintSystem.Layer layer : this.layers) {
         if (layer.dirty) {
            anyDirty = true;
            break;
         }
      }

      if (this.selection.bounds.width * this.selection.bounds.height > 0) {
         anyDirty = true;
      }

      if (anyDirty) {
         this.rebuildComposite();
         if (this.compositeImage != null
            && Minecraft.getInstance().getTextureManager().getTexture(this.getImageLocation()) instanceof DynamicTexture dynamicTexture) {
            dynamicTexture.setPixels(convertToNativeImage(this.compositeImage));
            dynamicTexture.upload();
         }

         if (this.updateToServer) {
            this.updateToServer = false;
            PaintData paintData = this.toPaintData();
            HexereiPacketHandler.sendToServer(new PaintDataToServer(paintData));
         }
      }
   }

   public int pickColor(PaintSystem.Layer layer, int x, int y) {
      if (layer != null && layer.pixels != null) {
         int width = layer.pixels.getWidth();
         int height = layer.pixels.getHeight();
         return x >= 0 && x < width && y >= 0 && y < height ? layer.pixels.getRGB(x, y) : 0;
      } else {
         return 0;
      }
   }

   public void floodFill(PaintSystem.Layer layer, int x, int y, int newColor, float tolerance) {
      this.startStroke(this.activeLayer.pixels.getWidth(), this.activeLayer.pixels.getHeight(), PaintSystem.Brush.Type.DRAW);
      if (layer != null && layer.pixels != null) {
         int width = layer.pixels.getWidth();
         int height = layer.pixels.getHeight();
         if (x >= 0 && x < width && y >= 0 && y < height) {
            int targetColor = layer.pixels.getRGB(x, y);
            float[] targetHSV = this.rgbToHsv(targetColor);
            if (!this.brush.getGlobal()) {
               Stack<Point> stack = new Stack<>();
               stack.push(new Point(x, y));
               boolean[][] visited = new boolean[width][height];

               while (!stack.isEmpty()) {
                  Point p = stack.pop();
                  int px = p.x;
                  int py = p.y;
                  if ((
                        this.selection.isEmpty()
                           || this.selection.bounds.contains(px, py)
                              && (this.selection.mask.getRGB(px - this.selection.bounds.x, py - this.selection.bounds.y) >> 24 & 0xFF) != 0
                     )
                     && px >= 0
                     && px < width
                     && py >= 0
                     && py < height
                     && !visited[px][py]) {
                     visited[px][py] = true;
                     int currentColor = layer.pixels.getRGB(px, py);
                     float[] currentHSV = this.rgbToHsv(currentColor);
                     int alpha = currentColor >> 24 & 0xFF;
                     int targetAlpha = targetColor >> 24 & 0xFF;
                     float difference = this.calculateColorDifference(targetHSV, currentHSV);
                     if (alpha == 0 && targetAlpha == 0 || difference <= tolerance) {
                        this.strokeMask.setRGB(px, py, newColor);
                        if (px > 0) {
                           stack.push(new Point(px - 1, py));
                        }

                        if (px < width - 1) {
                           stack.push(new Point(px + 1, py));
                        }

                        if (py > 0) {
                           stack.push(new Point(px, py - 1));
                        }

                        if (py < height - 1) {
                           stack.push(new Point(px, py + 1));
                        }
                     }
                  }
               }
            } else {
               for (int px = 0; px < layer.pixels.getWidth(); px++) {
                  for (int py = 0; py < layer.pixels.getHeight(); py++) {
                     if (this.selection.isEmpty()
                        || this.selection.bounds.contains(px, py)
                           && (this.selection.mask.getRGB(px - this.selection.bounds.x, py - this.selection.bounds.y) >> 24 & 0xFF) != 0) {
                        int currentColor = layer.pixels.getRGB(px, py);
                        float[] currentHSV = this.rgbToHsv(currentColor);
                        int alpha = currentColor >> 24 & 0xFF;
                        int targetAlpha = targetColor >> 24 & 0xFF;
                        float difference = this.calculateColorDifference(targetHSV, currentHSV);
                        if (alpha == 0 && targetAlpha == 0 || difference <= tolerance) {
                           this.strokeMask.setRGB(px, py, newColor);
                        }
                     }
                  }
               }
            }

            this.endStroke();
            layer.dirty = true;
         }
      }
   }

   private float[] rgbToHsv(int rgb) {
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb & 0xFF;
      int a = rgb >> 24 & 0xFF;
      float rNorm = r / 255.0F;
      float gNorm = g / 255.0F;
      float bNorm = b / 255.0F;
      float aNorm = a / 255.0F;
      float max = Math.max(rNorm, Math.max(gNorm, bNorm));
      float min = Math.min(rNorm, Math.min(gNorm, bNorm));
      float delta = max - min;
      float h = 0.0F;
      float s;
      if (delta != 0.0F) {
         s = delta / max;
         if (rNorm == max) {
            h = (gNorm - bNorm) / delta;
         } else if (gNorm == max) {
            h = 2.0F + (bNorm - rNorm) / delta;
         } else {
            h = 4.0F + (rNorm - gNorm) / delta;
         }

         h *= 60.0F;
         if (h < 0.0F) {
            h += 360.0F;
         }
      } else {
         s = 0.0F;
         h = -1.0F;
      }

      return new float[]{h, s, max, aNorm};
   }

   private float calculateColorDifference(float[] hsv1, float[] hsv2) {
      float dh = Math.abs(hsv1[0] - hsv2[0]);
      if (dh > 180.0F) {
         dh = 360.0F - dh;
      }

      dh /= 360.0F;
      float alphaAvg = (hsv1[3] + hsv2[3]) / 2.0F;
      dh *= alphaAvg;
      float ds = Math.abs(hsv1[1] - hsv2[1]);
      float dv = Math.abs(hsv1[2] - hsv2[2]);
      float da = Math.abs(hsv1[3] - hsv2[3]);
      float weightH = 0.55F * alphaAvg;
      float weightS = 0.25F * alphaAvg;
      float weightV = 0.25F * alphaAvg;
      float weightA = 0.3F;
      return (float)Math.sqrt(weightH * dh * dh + weightS * ds * ds + weightV * dv * dv + weightA * da * da);
   }

   private void startMoveSelection(int x, int y) {
      PaintSystem.SelectionAndDrawAction selAction = new PaintSystem.SelectionAndDrawAction(this.activeLayer, this.selection);
      this.actionManager.beginAction(selAction);
      this.movingSelection = this.copySelection();
      this.movingSelectionClickedPos = new PaintSystem.Pos2i(x - this.selection.bounds.x, y - this.selection.bounds.y);
      this.movingSelectionOffset = new PaintSystem.Pos2i(x - this.movingSelectionClickedPos.x, y - this.movingSelectionClickedPos.y);
      this.selection.deleteFromLayer(this.activeLayer);
      this.activeLayer.dirty = true;
   }

   private void updateMoveSelection(int x, int y) {
      this.movingSelectionOffset = new PaintSystem.Pos2i(x - this.movingSelectionClickedPos.x, y - this.movingSelectionClickedPos.y);
      this.selection.bounds.x = this.movingSelectionOffset.x;
      this.selection.bounds.y = this.movingSelectionOffset.y;
   }

   private void endMoveSelection() {
      if (this.movingSelection != null) {
         Graphics2D g2d = this.activeLayer.pixels.createGraphics();
         this.deleteSelection();
         g2d.drawImage(this.movingSelection, this.movingSelectionOffset.x, this.movingSelectionOffset.y, null);
         g2d.dispose();
         this.movingSelection = null;
         this.activeLayer.dirty = true;
         int oldX = this.movingSelectionOffset.x;
         int oldY = this.movingSelectionOffset.y;
         int layerWidth = this.activeLayer.pixels.getWidth();
         int layerHeight = this.activeLayer.pixels.getHeight();
         int newX = Math.max(oldX, 0);
         int newY = Math.max(oldY, 0);
         int newWidth = Math.max(0, Math.min(this.selection.bounds.width, layerWidth - newX));
         int newHeight = Math.max(0, Math.min(this.selection.bounds.height, layerHeight - newY));
         if (newWidth != 0 && newHeight != 0) {
            this.selection.bounds = new Rectangle(newX, newY, newWidth, newHeight);
            if (this.selection.mask != null) {
               int offsetX = newX - oldX;
               int offsetY = newY - oldY;
               BufferedImage newMask = new BufferedImage(newWidth, newHeight, 2);
               Graphics2D g = newMask.createGraphics();
               g.drawImage(this.selection.mask, 0, 0, newWidth, newHeight, offsetX, offsetY, offsetX + newWidth, offsetY + newHeight, null);
               g.dispose();
               this.selection.mask = newMask;
               this.selection.cropMaskToSelection();
               this.selection.edgePoints = this.selection.orderEdgePoints(this.selection.findEdgePoints(this.selection.mask).stream().toList());
            }
         } else {
            this.selection.clear();
         }

         if (this.actionManager.currentAction instanceof PaintSystem.SelectionAndDrawAction selectionAndDrawAction) {
            selectionAndDrawAction.captureAfter(this.selection);
            this.actionManager.commitAction();
         }

         this.updateToServer = true;
      }
   }

   public void startSelection(int x, int y) {
      this.selection.cursorX = x;
      this.selection.cursorY = y;
      this.selection.cursorXOld = x;
      this.selection.cursorYOld = y;
      this.selection.adjustingSelection = true;
      this.selection.initializeBounds(x, y);
      this.selection.clearMask();
      this.selection.anchor = new PaintSystem.Pos2i(Math.clamp(x, 0, this.activeLayer.pixels.getWidth()), Math.clamp(y, 0, this.activeLayer.pixels.getHeight()));
      this.selection.updateEdgePoints();
      this.dirty = true;
   }

   public void updateSelection(int x, int y) {
      Rectangle newBounds = this.selection
         .getUpdateRectangleBounds(
            this.selection.anchor,
            new PaintSystem.Pos2i(Math.clamp(x, 0, this.activeLayer.pixels.getWidth() - 1), Math.clamp(y, 0, this.activeLayer.pixels.getHeight() - 1))
         );
      this.selection.bounds = newBounds;
      if (!(this.selection.bounds.getWidth() > 1.0) && !(this.selection.bounds.getHeight() > 1.0)) {
         this.selection.setSelectionMask(null);
      } else {
         this.selection.createRectangleMask();
      }
   }

   public void endSelection(int x, int y) {
      if (this.selection.adjustingSelection) {
         this.selection.adjustingSelection = false;
         this.updateSelection(x, y);
         if (this.activeLayer != null && this.activeLayer.pixels != null) {
            if (!(this.selection.bounds.getWidth() > 1.0) && !(this.selection.bounds.getHeight() > 1.0)) {
               this.selection.setSelectionMask(null);
            } else {
               this.selection.createRectangleMask();
            }
         }

         this.dirty = true;
      }

      if (this.selection.getType() == PaintSystem.Selection.Type.ADD) {
         if (this.initialSelectionMask != null) {
            Rectangle combinedBounds = this.selection.isEmpty()
               ? this.initialSelectionBounds
               : (Rectangle)this.selection.bounds.createUnion(this.initialSelectionBounds);
            BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, 2);
            Graphics2D g = combinedMask.createGraphics();
            if (this.selection.mask != null) {
               g.drawImage(
                  this.selection.mask,
                  this.selection.bounds.x - combinedBounds.x,
                  this.selection.bounds.y - combinedBounds.y,
                  this.selection.mask.getWidth(),
                  this.selection.mask.getHeight(),
                  null
               );
            }

            if (this.initialSelectionMask != null) {
               g.drawImage(
                  this.initialSelectionMask,
                  this.initialSelectionBounds.x - combinedBounds.x,
                  this.initialSelectionBounds.y - combinedBounds.y,
                  this.initialSelectionMask.getWidth(),
                  this.initialSelectionMask.getHeight(),
                  null
               );
            }

            g.dispose();
            this.selection.bounds = combinedBounds;
            this.selection.setSelectionMask(combinedMask);
            this.selection.cropMaskToSelection();
         }
      } else if (this.selection.getType() == PaintSystem.Selection.Type.REMOVE) {
         if (this.initialSelectionMask == null) {
            this.selection.clear();
            return;
         }

         Rectangle combinedBoundsx = this.selection.isEmpty()
            ? this.initialSelectionBounds
            : (Rectangle)this.selection.bounds.createUnion(this.initialSelectionBounds);
         BufferedImage combinedMaskx = new BufferedImage(combinedBoundsx.width, combinedBoundsx.height, 2);
         Graphics2D gx = combinedMaskx.createGraphics();
         if (this.initialSelectionMask != null) {
            gx.drawImage(
               this.initialSelectionMask,
               this.initialSelectionBounds.x - combinedBoundsx.x,
               this.initialSelectionBounds.y - combinedBoundsx.y,
               this.initialSelectionMask.getWidth(),
               this.initialSelectionMask.getHeight(),
               null
            );
         }

         for (int y1 = 0; y1 < this.selection.bounds.height; y1++) {
            for (int x1 = 0; x1 < this.selection.bounds.width; x1++) {
               int globalX = this.selection.bounds.x + x1;
               int globalY = this.selection.bounds.y + y1;
               if (globalX >= 0
                  && globalX < this.width
                  && globalY >= 0
                  && globalY < this.height
                  && x1 >= 0
                  && x1 < this.selection.mask.getWidth()
                  && y1 >= 0
                  && y1 < this.selection.mask.getHeight()
                  && globalX - combinedBoundsx.x >= 0
                  && globalX - combinedBoundsx.x < combinedBoundsx.width
                  && globalY - combinedBoundsx.y >= 0
                  && globalY - combinedBoundsx.y < combinedBoundsx.height) {
                  int maskAlpha = this.selection.mask.getRGB(x1, y1) >> 24 & 0xFF;
                  if (maskAlpha > 0) {
                     combinedMaskx.setRGB(globalX - combinedBoundsx.x, globalY - combinedBoundsx.y, 0);
                  }
               }
            }
         }

         gx.dispose();
         this.selection.bounds = combinedBoundsx;
         this.selection.setSelectionMask(combinedMaskx);
      }

      if (this.initialSelectionMask != null || this.selection.mask != null) {
         PaintSystem.SelectionAction selAction = new PaintSystem.SelectionAction(
            this.initialSelectionBounds, this.initialSelectionMask, this.selection.bounds, this.selection.mask
         );
         this.actionManager.beginAction(selAction);
         this.actionManager.commitAction();
      }
   }

   public BufferedImage copySelection() {
      return this.activeLayer != null && !this.selection.isEmpty() ? this.selection.extractSelectedArea(this.activeLayer) : null;
   }

   public boolean canCopy() {
      return this.selection.bounds != null && this.selection.mask != null;
   }

   public void copySelectionToClipboard() {
      if (this.canCopy()) {
         clipboardImage = this.copySelection();
         clipboardBounds = new Rectangle(this.selection.bounds);
         clipboardMask = deepCopy(this.selection.mask);
      }
   }

   public void cutSelectionToClipboard() {
      if (this.canCopy()) {
         PaintSystem.SelectionAndDrawAction action = new PaintSystem.SelectionAndDrawAction(this.activeLayer, this.selection);
         this.actionManager.beginAction(action);
         this.copySelectionToClipboard();
         this.deleteSelection();
         this.selection.clear();
         this.activeLayer.dirty = true;
         action.captureAfter(this.selection);
         this.actionManager.commitAction();
         this.updateToServer = true;
      }
   }

   public void pasteClipboard() {
      if (clipboardImage != null && clipboardBounds != null && clipboardMask != null) {
         if (this.movingSelection != null) {
            this.endMoveSelection();
         }

         PaintSystem.SelectionAndDrawAction action = new PaintSystem.SelectionAndDrawAction(this.activeLayer, this.selection);
         this.actionManager.beginAction(action);
         this.movingSelection = deepCopy(clipboardImage);
         this.selection.mask = deepCopy(clipboardMask);
         this.selection.bounds = clipboardBounds == null ? null : new Rectangle(clipboardBounds);
         this.selection.updateEdgePoints();
         this.movingSelectionClickedPos = new PaintSystem.Pos2i(this.movingSelection.getWidth() / 2, this.movingSelection.getHeight() / 2);
         this.movingSelectionOffset = new PaintSystem.Pos2i(
            (int)this.cursorX - this.movingSelectionClickedPos.x, (int)this.cursorY - this.movingSelectionClickedPos.y
         );
         this.setCurrentTool(PaintSystem.Tool.MOVE);
         this.updateMoveSelection((int)this.cursorX, (int)this.cursorY);
         this.skipNextRelease = true;
      }
   }

   public void deleteSelection() {
      this.selection.deleteFromLayer(this.activeLayer);
   }

   public void startStroke(int width, int height, PaintSystem.Brush.Type strokeType) {
      this.strokeType = strokeType;
      this.strokeMask = new BufferedImage(width, height, 2);
   }

   public void endStroke() {
      if (this.strokeMask != null && this.activeLayer != null) {
         boolean changed = false;

         for (int y = 0; y < this.strokeMask.getHeight(); y++) {
            for (int x = 0; x < this.strokeMask.getWidth(); x++) {
               switch (this.strokeType) {
                  case DRAW:
                     int maskPixelx = this.strokeMask.getRGB(x, y);
                     int maskAx = maskPixelx >> 24 & 0xFF;
                     if (maskAx > 0) {
                        int layerPixel = this.activeLayer.pixels.getRGB(x, y);
                        if (this.activeLayer.blendMode != null) {
                           maskPixelx = this.activeLayer
                              .blendMode
                              .apply(layerPixel, maskAx, maskPixelx >> 16 & 0xFF, maskPixelx >> 8 & 0xFF, maskPixelx & 0xFF);
                        }

                        this.activeLayer.pixels.setRGB(x, y, maskPixelx);
                        changed = true;
                     }
                     break;
                  case ERASE:
                     int maskPixel = this.strokeMask.getRGB(x, y);
                     int maskA = maskPixel >> 24 & 0xFF;
                     int pixel = this.activeLayer.pixels.getRGB(x, y);
                     int destA = pixel >> 24 & 0xFF;
                     if (destA != 0 && maskA != 0) {
                        int newA = Math.max(0, destA - maskA);
                        int newPixel = newA << 24 | pixel & 16777215;
                        this.activeLayer.pixels.setRGB(x, y, newPixel);
                        changed = true;
                     }
               }
            }
         }

         this.strokeMask = null;
         this.activeLayer.dirty = true;
         if (changed) {
            this.updateToServer = true;
         }

         if (this.currentDrawAction != null) {
            this.currentDrawAction.captureAfter();
            if (changed) {
               this.actionManager.commitAction();
            }

            this.currentDrawAction = null;
         }
      }
   }

   public static NativeImage convertToNativeImage(BufferedImage bufferedImage) {
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      NativeImage nativeImage = new NativeImage(Format.RGBA, width, height, false);

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int argb = bufferedImage.getRGB(x, y);
            int a = argb >> 24 & 0xFF;
            int r = argb >> 16 & 0xFF;
            int g = argb >> 8 & 0xFF;
            int b = argb & 0xFF;
            int abgrColor = a << 24 | b << 16 | g << 8 | r;
            nativeImage.setPixelRGBA(x, y, abgrColor);
         }
      }

      return nativeImage;
   }

   public void addAndUpdateTexture() {
      this.rebuildComposite();
      DynamicTexture dynamicTexture = new DynamicTexture(convertToNativeImage(this.compositeImage));
      Minecraft.getInstance().getTextureManager().register(this.getImageLocation(), dynamicTexture);
   }

   public void rebuildComposite() {
      if (!this.layers.isEmpty()) {
         int width = this.width;
         int height = this.height;
         this.compositeImage = new BufferedImage(width, height, 2);
         Graphics2D g2d = this.compositeImage.createGraphics();

         for (PaintSystem.Layer layer : this.layers) {
            BufferedImage temp = new BufferedImage(width, height, 2);

            for (int x = 0; x < width; x++) {
               for (int y = 0; y < height; y++) {
                  int pixel = layer.pixels.getRGB(x, y);
                  int a = pixel >> 24 & 0xFF;
                  if (layer.opacity != 1.0F) {
                     a = (int)(a * layer.opacity);
                  }

                  if (layer == this.activeLayer && this.strokeMask != null && this.strokeType == PaintSystem.Brush.Type.ERASE) {
                     int maskPixel = this.strokeMask.getRGB(x, y);
                     int maskA = maskPixel >> 24 & 0xFF;
                     int destA = pixel >> 24 & 0xFF;
                     if (a != 0 && maskA != 0) {
                        int newA = Math.max(0, destA - maskA);
                        int newPixel = newA << 24 | pixel & 16777215;
                        pixel = newPixel;
                        a = newA;
                     }
                  }

                  int existing = this.compositeImage.getRGB(x, y);
                  if (layer.blendMode != null) {
                     pixel = layer.blendMode.apply(existing, a, pixel >> 16 & 0xFF, pixel >> 8 & 0xFF, pixel & 0xFF);
                  }

                  temp.setRGB(x, y, pixel);
               }
            }

            if (layer == this.activeLayer && this.movingSelection != null) {
               for (int y = 0; y < this.selection.bounds.height; y++) {
                  for (int x = 0; x < this.selection.bounds.width; x++) {
                     int globalX = this.selection.bounds.x + x;
                     int globalY = this.selection.bounds.y + y;
                     if (globalX >= 0
                        && globalX < temp.getWidth()
                        && globalY >= 0
                        && globalY < temp.getHeight()
                        && x < this.selection.mask.getWidth()
                        && y < this.selection.mask.getHeight()) {
                        int maskAlpha = this.selection.mask.getRGB(x, y) >> 24 & 0xFF;
                        if (maskAlpha > 0) {
                           temp.setRGB(globalX, globalY, 0);
                        }
                     }
                  }
               }

               Graphics2D g = temp.createGraphics();
               g.drawImage(this.movingSelection, this.movingSelectionOffset.x, this.movingSelectionOffset.y, null);
            }

            g2d.drawImage(temp, 0, 0, null);
            if (layer == this.activeLayer && this.strokeMask != null && this.brush.type == PaintSystem.Brush.Type.DRAW) {
               for (int xx = 0; xx < width; xx++) {
                  for (int y = 0; y < height; y++) {
                     int maskPixel = this.strokeMask.getRGB(xx, y);
                     int maskA = maskPixel >> 24 & 0xFF;
                     if (maskA > 0) {
                        int existingPixel = this.compositeImage.getRGB(xx, y);
                        if (layer.blendMode != null) {
                           maskPixel = layer.blendMode.apply(existingPixel, maskA, maskPixel >> 16 & 0xFF, maskPixel >> 8 & 0xFF, maskPixel & 0xFF);
                        }

                        this.compositeImage.setRGB(xx, y, maskPixel);
                     }
                  }
               }
            }

            layer.dirty = false;
         }

         if (this.selection != null && this.toolsVisible) {
            this.selection.render(g2d);
         }

         if (currentTool.shouldShowBrushSliders() && this.toolsVisible) {
            int diameter = PaintSystem.Brush.size + 1;
            int drawX = (int)(this.cursorX - diameter / 2.0F + 0.5F);
            int drawY = (int)(this.cursorY - diameter / 2.0F + 0.5F);
            BufferedImage brushImage = this.brush.generateBrushMask(true);
            brushImage = this.adjustImage(brushImage, 0.35F, Color.GRAY);
            if (this.selection.mask != null) {
               for (int yx = 0; yx < brushImage.getWidth(); yx++) {
                  for (int xx = 0; xx < brushImage.getHeight(); xx++) {
                     int globalX = drawX + xx;
                     int globalY = drawY + yx;
                     if (globalX - this.selection.bounds.x >= 0
                        && globalX - this.selection.bounds.x < this.selection.mask.getWidth()
                        && globalY - this.selection.bounds.y >= 0
                        && globalY - this.selection.bounds.y < this.selection.mask.getHeight()
                        && globalX >= this.selection.bounds.x
                        && globalX < this.selection.mask.getWidth() + this.selection.bounds.x
                        && globalY >= this.selection.bounds.y
                        && globalY < this.selection.mask.getHeight() + this.selection.bounds.y) {
                        int maskAlpha = this.selection.mask.getRGB(globalX - this.selection.bounds.x, globalY - this.selection.bounds.y) >> 24 & 0xFF;
                        if (maskAlpha == 0) {
                           brushImage.setRGB(xx, yx, 0);
                        }
                     } else {
                        brushImage.setRGB(xx, yx, 0);
                     }
                  }
               }
            }

            g2d.drawImage(brushImage, drawX, drawY, null);
         }

         g2d.dispose();
      }
   }

   public BufferedImage adjustImage(BufferedImage image, float alphaFactor, Color newColor) {
      int width = image.getWidth();
      int height = image.getHeight();
      BufferedImage result = new BufferedImage(width, height, 2);

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int pixel = image.getRGB(x, y);
            int origAlpha = pixel >> 24 & 0xFF;
            int newAlpha = (int)(origAlpha * alphaFactor);
            int r = newColor.getRed();
            int g = newColor.getGreen();
            int b = newColor.getBlue();
            int outPixel = newAlpha << 24 | r << 16 | g << 8 | b;
            result.setRGB(x, y, outPixel);
         }
      }

      return result;
   }

   public void addLayer(int width, int height) {
      PaintSystem.Layer layer = new PaintSystem.Layer();
      layer.pixels = new BufferedImage(width, height, 2);
      this.layers.add(layer);
      if (this.activeLayer == null) {
         this.activeLayer = layer;
      }
   }

   public void addLayer(PaintSystem.Layer layer) {
      this.layers.add(layer);
      if (this.activeLayer == null) {
         this.activeLayer = layer;
      }
   }

   public void setActiveLayer(int index) {
      if (index >= 0 && index < this.layers.size()) {
         this.activeLayer = this.layers.get(index);
      }
   }

   public void endDrawing() {
      if (this.strokeMask != null) {
         this.endStroke();
      }
   }

   public void draw(float lastX, float lastY, float x, float y) {
      if (this.activeLayer != null && this.brush != null) {
         if (this.strokeMask == null) {
            this.startStroke(this.activeLayer.pixels.getWidth(), this.activeLayer.pixels.getHeight(), this.brush.type);
         }

         this.drawLine(lastX, lastY, x, y);
         this.activeLayer.dirty = true;
      }
   }

   private void drawLine(float x0, float y0, float x1, float y1) {
      float currentX = x0;
      float currentY = y0;
      int targetX = (int)Math.ceil(x1);
      int targetY = (int)Math.ceil(y1);
      int lastX = (int)Math.ceil(x0);
      int lastY = (int)Math.ceil(y0);
      this.brush.apply(x0, y0);
      this.brush.apply(x1, y1);
      double dx = targetX - Math.ceil(x0);
      double dy = targetY - Math.ceil(y0);
      double distance = Math.sqrt(dx * dx + dy * dy);
      if (distance != 0.0) {
         float stepX = (float)dx / (float)distance;
         float stepY = (float)dy / (float)distance;
         int i = 0;

         while (Math.ceil(currentX) != targetX || Math.ceil(currentY) != targetY) {
            if (++i + 0.5F > distance) {
               break;
            }

            currentX += stepX;
            currentY += stepY;
            int newX = (int)currentX;
            int newY = (int)currentY;
            if (newX != lastX || newY != lastY) {
               this.brush.apply(newX, newY);
               lastX = newX;
               lastY = newY;
            }
         }
      }
   }

   public void setToolsVisible(boolean toolsVisible) {
      if (this.toolsVisible != toolsVisible) {
         this.toolsVisible = toolsVisible;
         this.dirty = true;
      }
   }

   public BufferedImage getMovingSelection() {
      return this.movingSelection;
   }

   public List<PaintSystem.Layer> getLayers() {
      return this.layers;
   }

   public PaintSystem.Layer getActiveLayer() {
      return this.activeLayer;
   }

   public void setActiveLayer(PaintSystem.Layer activeLayer) {
      this.activeLayer = activeLayer;
   }

   public PaintSystem.Brush getBrush() {
      return this.brush;
   }

   public PaintSystem.Colors getColors() {
      return colors;
   }

   public interface Action {
      void undo();

      void redo();
   }

   public class ActionManager {
      private final Stack<PaintSystem.Action> undoStack = new Stack<>();
      private final Stack<PaintSystem.Action> redoStack = new Stack<>();
      private static final int MAX_SIZE = 30;
      private PaintSystem.Action currentAction = null;

      public void beginAction(PaintSystem.Action action) {
         this.currentAction = action;
      }

      public void commitAction() {
         if (this.currentAction != null) {
            this.undoStack.push(this.currentAction);
            if (this.undoStack.size() > 30) {
               this.undoStack.removeFirst();
            }

            this.redoStack.clear();
            this.currentAction = null;
         }
      }

      public void undo() {
         if (!this.undoStack.isEmpty()) {
            PaintSystem.Action action = this.undoStack.pop();
            action.undo();
            this.redoStack.push(action);
         }

         PaintSystem.this.activeLayer.dirty = true;
      }

      public void redo() {
         if (!this.redoStack.isEmpty()) {
            PaintSystem.Action action = this.redoStack.pop();
            action.redo();
            this.undoStack.push(action);
         }

         PaintSystem.this.activeLayer.dirty = true;
      }
   }

   public static enum BlendMode {
      NORMAL {
         @Override
         public int apply(int srcColor, int a, int r, int g, int b) {
            int destA = srcColor >> 24 & 0xFF;
            int destR = srcColor >> 16 & 0xFF;
            int destG = srcColor >> 8 & 0xFF;
            int destB = srcColor & 0xFF;
            if (a == 255 || destA == 0) {
               return a << 24 | r << 16 | g << 8 | b;
            } else if (a == 0) {
               return srcColor;
            } else {
               float alpha = a / 255.0F;
               float invAlpha = 1.0F - alpha;
               int blendedA = (int)(a + destA * invAlpha);
               int blendedR = (int)(r * alpha + destR * invAlpha);
               int blendedG = (int)(g * alpha + destG * invAlpha);
               int blendedB = (int)(b * alpha + destB * invAlpha);
               return blendedA << 24 | blendedR << 16 | blendedG << 8 | blendedB;
            }
         }
      },
      OVERLAY {
         @Override
         public int apply(int srcColor, int a, int r, int g, int b) {
            int sr = srcColor >> 16 & 0xFF;
            int sg = srcColor >> 8 & 0xFF;
            int sb = srcColor & 0xFF;
            int or = sr * r / 255;
            int og = sg * g / 255;
            int ob = sb * b / 255;
            return a << 24 | or << 16 | og << 8 | ob;
         }
      };

      public abstract int apply(int var1, int var2, int var3, int var4, int var5);
   }

   public static class Brush {
      public PaintSystem parent;
      public PaintSystem.Brush.Type type = PaintSystem.Brush.Type.DRAW;
      public static int size = 5;
      public static float hardness = 1.0F;
      public static float tolerance = 0.0F;
      public float cursorX = 0.0F;
      public float cursorY = 0.0F;
      public float cursorXOld = 0.0F;
      public float cursorYOld = 0.0F;
      public boolean drawing = false;
      public static boolean global = false;

      public Brush(PaintSystem parent) {
         this.parent = parent;
      }

      public void apply(float x, float y) {
         this.applyBrushMask(this.parent.strokeMask, x, y);
      }

      private BufferedImage generateBrushMask() {
         return this.generateBrushMask(false);
      }

      private BufferedImage generateBrushMask(boolean ignoreColorAlpha) {
         int diameter = size + 1;
         BufferedImage brushImage = new BufferedImage(diameter, diameter, 2);
         int color = this.parent.getColor();
         float radius = diameter / 2.0F;
         float centerX = (diameter - 1) / 2.0F;
         float centerY = centerX;
         float innerRadius = hardness * radius;

         for (int x = 0; x < diameter; x++) {
            for (int y = 0; y < diameter; y++) {
               float dx = x - centerX;
               float dy = y - centerY;
               float distance = (float)Math.sqrt(dx * dx + dy * dy);
               int alpha = (int)(this.calculateAlpha(distance, radius, innerRadius) / 255.0F * (ignoreColorAlpha ? 255 : color >> 24 & 0xFF));
               brushImage.setRGB(x, y, alpha << 24 | color & 16777215);
            }
         }

         return brushImage;
      }

      private int calculateAlpha(float distance, float radius, float innerRadius) {
         if (distance <= innerRadius) {
            return 255;
         } else if (distance <= radius) {
            float t = (distance - innerRadius) / (radius - innerRadius);
            return (int)(255.0F * (1.0F - t));
         } else {
            return 0;
         }
      }

      private void applyBrushMask(BufferedImage strokeMask, float x, float y) {
         int diameter = size + 1;
         BufferedImage brushImage = this.generateBrushMask();
         int drawX = (int)(x - diameter / 2.0F + 0.5F);
         int drawY = (int)(y - diameter / 2.0F + 0.5F);

         for (int bx = 0; bx < brushImage.getWidth(); bx++) {
            for (int by = 0; by < brushImage.getHeight(); by++) {
               int sx = drawX + bx;
               int sy = drawY + by;
               if (sx >= 0 && sx < strokeMask.getWidth() && sy >= 0 && sy < strokeMask.getHeight()) {
                  if (this.parent.selection.mask != null) {
                     int posX = sx - this.parent.selection.bounds.x;
                     int posY = sy - this.parent.selection.bounds.y;
                     if (posX >= this.parent.selection.mask.getWidth()
                        || posY >= this.parent.selection.mask.getHeight()
                        || posX < 0
                        || posY < 0
                        || (this.parent.selection.mask.getRGB(posX, posY) >> 24 & 0xFF) == 0) {
                        continue;
                     }
                  }

                  int brushPixel = brushImage.getRGB(bx, by);
                  int brushAlpha = brushPixel >>> 24;
                  int strokePixel = strokeMask.getRGB(sx, sy);
                  int strokeAlpha = strokePixel >>> 24;
                  if (brushAlpha > strokeAlpha) {
                     strokeMask.setRGB(sx, sy, brushPixel);
                  }
               }
            }
         }
      }

      public void setGlobal(boolean global) {
         PaintSystem.Brush.global = global;
      }

      public boolean getGlobal() {
         return global;
      }

      public static enum Type {
         DRAW,
         ERASE;
      }
   }

   public class Button {
      public BiFunction<PaintSystem, Float, Float> lx;
      public BiFunction<PaintSystem, Float, Float> ly;
      public BiFunction<PaintSystem, Float, Float> rx;
      public BiFunction<PaintSystem, Float, Float> ry;
      public float width;
      public float height;
      public Function<Float, Float> scale;
      public String texture;
      public String hoverTexture;
      public String disabledTexture;
      public Consumer<PaintSystem> onClick;
      public Component tooltip;
      public Consumer<PaintSystem> onTick;
      public Function<PaintSystem, Boolean> selected;
      public Function<PaintSystem, Boolean> disabled;
      public Function<PaintSystem, Boolean> visible;
      public float visibility = 0.0F;
      public float visibilityOld = 0.0F;
      public boolean clicked = false;
      public float clickedScale = 1.0F;

      Button(
         float lx,
         float ly,
         float rx,
         float ry,
         float width,
         float height,
         Function<Float, Float> scale,
         String texture,
         String hoverTexture,
         String disabledTexture,
         Consumer<PaintSystem> onClick,
         Component tooltip,
         Consumer<PaintSystem> onTick,
         Function<PaintSystem, Boolean> selected,
         Function<PaintSystem, Boolean> disabled,
         Function<PaintSystem, Boolean> visible
      ) {
         this.lx = (ps, partial) -> lx;
         this.ly = (ps, partial) -> ly;
         this.rx = (ps, partial) -> rx;
         this.ry = (ps, partial) -> ry;
         this.width = width;
         this.height = height;
         this.scale = scale;
         this.texture = texture;
         this.hoverTexture = hoverTexture;
         this.disabledTexture = disabledTexture;
         this.onClick = onClick;
         this.tooltip = tooltip;
         this.selected = selected;
         this.disabled = disabled;
         this.visible = visible;
         this.onTick = onTick;
      }

      Button(
         BiFunction<PaintSystem, Float, Float> lx,
         BiFunction<PaintSystem, Float, Float> ly,
         BiFunction<PaintSystem, Float, Float> rx,
         BiFunction<PaintSystem, Float, Float> ry,
         float width,
         float height,
         Function<Float, Float> scale,
         String texture,
         String hoverTexture,
         String disabledTexture,
         Consumer<PaintSystem> onClick,
         Component tooltip,
         Consumer<PaintSystem> onTick,
         Function<PaintSystem, Boolean> selected,
         Function<PaintSystem, Boolean> disabled,
         Function<PaintSystem, Boolean> visible
      ) {
         this.lx = lx;
         this.ly = ly;
         this.rx = rx;
         this.ry = ry;
         this.width = width;
         this.height = height;
         this.scale = scale;
         this.texture = texture;
         this.hoverTexture = hoverTexture;
         this.disabledTexture = disabledTexture;
         this.onClick = onClick;
         this.tooltip = tooltip;
         this.selected = selected;
         this.disabled = disabled;
         this.visible = visible;
         this.onTick = onTick;
      }

      public float getX(PaintSystem paintSystem, PageDrawing.PageOn pageOn, float partial) {
         return pageOn.isOnLeftSide() ? this.lx.apply(paintSystem, partial) : this.rx.apply(paintSystem, partial);
      }

      public float getY(PaintSystem paintSystem, PageDrawing.PageOn pageOn, float partial) {
         return pageOn.isOnLeftSide() ? this.ly.apply(paintSystem, partial) : this.ry.apply(paintSystem, partial);
      }

      public Component getTooltip() {
         return this.tooltip;
      }

      public List<Component> getTooltipList() {
         return List.of(this.getTooltip());
      }

      public String getDisabledTexture(PaintSystem paintSystem) {
         return this.disabledTexture;
      }

      public String getTexture(PaintSystem paintSystem) {
         return this.texture;
      }

      public String getHoverTexture(PaintSystem paintSystem) {
         return this.hoverTexture;
      }

      public boolean shouldRender(PaintSystem paintSystem) {
         return this.visibility > 0.0F;
      }

      public float getVisibility(float partial) {
         return Math.max(0.0F, HexereiUtil.easeInOutCubic(Mth.lerp(partial, this.visibilityOld, this.visibility)));
      }

      public boolean isVisible(PaintSystem paintSystem) {
         return this.visible.apply(paintSystem);
      }

      public boolean getDisabled(PaintSystem paintSystem) {
         return this.disabled.apply(paintSystem);
      }

      public void onClick(PaintSystem paintSystem) {
         this.getOnClick(paintSystem).accept(paintSystem);
      }

      public Consumer<PaintSystem> getOnClick(PaintSystem paintSystem) {
         return this.onClick;
      }

      public float getScale(float val) {
         return this.scale.apply(val) * this.clickedScale;
      }

      public void tick(PaintSystem ps) {
         this.onTick.accept(ps);
         this.visibilityOld = this.visibility;
         if (this.isVisible(ps)) {
            this.visibility = HexereiUtil.moveTo(this.visibility, 1.0F, 0.01F + Math.clamp(Math.abs(this.visibility - 1.0F), 0.0F, 1.0F) * 0.15F);
         } else {
            this.visibility = HexereiUtil.moveTo(this.visibility, -1.0F, 0.01F + Math.clamp(Math.abs(this.visibility - 1.0F), 0.0F, 1.0F) * 0.25F);
         }

         if (this.clicked) {
            this.clickedScale = HexereiUtil.moveTo(this.clickedScale, 0.75F, 0.01F + Math.abs(this.clickedScale - 0.75F) * 0.5F);
            if (this.clickedScale == 0.75F) {
               this.clicked = false;
            }
         } else {
            this.clickedScale = HexereiUtil.moveTo(this.clickedScale, 1.0F, 0.01F + Math.abs(this.clickedScale - 0.75F) * 2.0F);
         }
      }
   }

   public static class Colors {
      public List<PaintSystem.Colors.ColorSelection> colors = new ArrayList<>();
      private final PaintSystem.Colors.ColorSelectionPosData colorSelectionPosData1 = new PaintSystem.Colors.ColorSelectionPosData(
         new Vec3(0.8999999761581421, 7.96999979019165, 0.0), 4.0F, 4.0F
      );
      private final PaintSystem.Colors.ColorSelectionPosData colorSelectionPosData2 = new PaintSystem.Colors.ColorSelectionPosData(
         this.colorSelectionPosData1.pos.add(0.25, 0.15, -7.999999797903001E-5),
         this.colorSelectionPosData1.width - 0.75F,
         this.colorSelectionPosData1.height - 0.75F
      );
      private final PaintSystem.Colors.ColorSelectionPosData colorSelectionPosData3 = new PaintSystem.Colors.ColorSelectionPosData(
         this.colorSelectionPosData2.pos.add(0.25, 0.15, -7.999999797903001E-5),
         this.colorSelectionPosData2.width - 0.75F,
         this.colorSelectionPosData2.height - 0.75F
      );
      float offset = 0.8F;

      public Colors(List<PaintSystem.Colors.ColorSelection> colors) {
         this.colors.add(new PaintSystem.Colors.ColorSelection(-16777216, this.colorSelectionPosData1.copy()));
         this.colors.add(new PaintSystem.Colors.ColorSelection(-1, this.colorSelectionPosData2.copy()));
         this.colors.add(new PaintSystem.Colors.ColorSelection(-8355712, this.colorSelectionPosData3.copy()));
         this.colors.addAll(colors);
      }

      public void tick() {
         for (PaintSystem.Colors.ColorSelection colorSelection : this.colors) {
            colorSelection.tick();
         }
      }

      public int getColor() {
         return ((PaintSystem.Colors.ColorSelection)this.colors.getFirst()).getColor();
      }

      public void setColor(int col) {
         ((PaintSystem.Colors.ColorSelection)this.colors.getFirst()).setColor(col);
      }

      public void cycleColor(PaintSystem paintSystem) {
         PaintSystem.Colors.ColorSelection first = (PaintSystem.Colors.ColorSelection)this.colors.removeFirst();
         this.colors.addLast(first);
         paintSystem.getValueSliders().updateColorSliders(this.getColor());
         this.updateColorSelectionTargetPos();
      }

      public void cycleColorBack(PaintSystem paintSystem) {
         PaintSystem.Colors.ColorSelection last = (PaintSystem.Colors.ColorSelection)this.colors.removeLast();
         this.colors.addFirst(last);
         paintSystem.getValueSliders().updateColorSliders(this.getColor());
         this.updateColorSelectionTargetPos();
      }

      public void updateColorSelectionTargetPos() {
         this.colors.get(0).target = this.colorSelectionPosData1;
         this.colors.get(1).target = this.colorSelectionPosData2;
         this.colors.get(2).target = this.colorSelectionPosData3;
      }

      public static class ColorSelection {
         public PaintSystem.Colors.ColorSelectionPosData target;
         public PaintSystem.Colors.ColorSelectionPosData colorPosData;
         public PaintSystem.Colors.ColorSelectionPosData colorPosDataOld;
         public int color;

         public ColorSelection(int color, PaintSystem.Colors.ColorSelectionPosData colorPosData) {
            this.color = color;
            this.colorPosData = colorPosData;
            this.colorPosDataOld = colorPosData;
            this.target = colorPosData;
         }

         public void tick() {
            this.colorPosDataOld = this.colorPosData.copy();
            float x = HexereiUtil.moveTo(
               (float)this.colorPosData.pos.x, (float)this.target.pos.x, 0.001F + 0.175F * Mth.abs((float)(this.target.pos.x - this.colorPosData.pos.x))
            );
            float y = HexereiUtil.moveTo(
               (float)this.colorPosData.pos.y, (float)this.target.pos.y, 0.001F + 0.175F * Mth.abs((float)(this.target.pos.y - this.colorPosData.pos.y))
            );
            float z = HexereiUtil.moveTo((float)this.colorPosData.pos.z, (float)this.target.pos.z, 0.001F);
            float w = HexereiUtil.moveTo(this.colorPosData.width, this.target.width, 0.25F);
            float h = HexereiUtil.moveTo(this.colorPosData.height, this.target.height, 0.25F);
            this.colorPosData = new PaintSystem.Colors.ColorSelectionPosData(new Vec3(x, y, z), w, h);
         }

         public int getColor() {
            return this.color;
         }

         public void setColor(int color) {
            this.color = color;
         }
      }

      public static class ColorSelectionPosData {
         public float width;
         public float height;
         public Vec3 pos;

         public ColorSelectionPosData(Vec3 pos, float width, float height) {
            this.pos = pos;
            this.width = width;
            this.height = height;
         }

         public PaintSystem.Colors.ColorSelectionPosData copy() {
            return new PaintSystem.Colors.ColorSelectionPosData(this.pos, this.width, this.height);
         }
      }
   }

   public class DrawAction implements PaintSystem.Action {
      private PaintSystem.Layer layer;
      private int index;
      private BufferedImage beforeImage;
      private BufferedImage afterImage;

      public DrawAction(PaintSystem.Layer layer, BufferedImage beforeImage) {
         this.layer = layer;
         this.index = PaintSystem.this.getLayers().indexOf(layer);
         this.beforeImage = PaintSystem.deepCopy(beforeImage);
      }

      public void captureAfter() {
         this.afterImage = PaintSystem.deepCopy(this.layer.pixels.getSubimage(0, 0, this.layer.pixels.getWidth(), this.layer.pixels.getHeight()));
      }

      @Override
      public void undo() {
         PaintSystem.Layer layer = PaintSystem.this.getLayers().get(this.index);
         layer.pixels = PaintSystem.deepCopy(this.beforeImage);
         layer.dirty = true;
         PaintSystem.this.updateToServer = true;
      }

      @Override
      public void redo() {
         PaintSystem.Layer layer = PaintSystem.this.getLayers().get(this.index);
         layer.pixels = PaintSystem.deepCopy(this.afterImage);
         layer.dirty = true;
         PaintSystem.this.updateToServer = true;
      }
   }

   public static class Layer {
      public BufferedImage pixels;
      public float opacity = 1.0F;
      public PaintSystem.BlendMode blendMode = PaintSystem.BlendMode.NORMAL;
      public boolean dirty = true;
      public String name = "";
   }

   public static class Pos2i {
      public final int x;
      public final int y;

      Pos2i(int x, int y) {
         this.x = x;
         this.y = y;
      }
   }

   class Selection {
      private Rectangle bounds;
      private BufferedImage mask;
      public PaintSystem.Pos2i anchor;
      public boolean adjustingSelection;
      private PaintSystem parent;
      List<Point> edgePoints = new ArrayList<>();
      public int cursorX = 0;
      public int cursorY = 0;
      public int cursorXOld = 0;
      public int cursorYOld = 0;
      public static PaintSystem.Selection.Type type = PaintSystem.Selection.Type.REPLACE;
      public static boolean global = false;

      public Selection(PaintSystem parent) {
         this.parent = parent;
         this.bounds = new Rectangle(0, 0, 0, 0);
         this.mask = null;
         this.anchor = new PaintSystem.Pos2i(0, 0);
         this.adjustingSelection = false;
      }

      public void setType(PaintSystem.Selection.Type type) {
         PaintSystem.Selection.type = type;
      }

      public PaintSystem.Selection.Type getType() {
         return type;
      }

      public void setGlobal(boolean global) {
         PaintSystem.Selection.global = global;
      }

      public boolean getGlobal() {
         return global;
      }

      public BufferedImage getMask() {
         return this.mask;
      }

      public Rectangle getBounds() {
         return this.bounds;
      }

      public boolean isEmpty() {
         if (this.adjustingSelection) {
            switch (PaintSystem.this.selection.getType()) {
               case REPLACE:
                  return this.mask == null || this.bounds.width == 1 && this.bounds.height == 1;
               case ADD:
               case REMOVE:
                  return (this.mask == null || this.bounds.width == 1 && this.bounds.height == 1)
                     && (
                        PaintSystem.this.initialSelectionMask == null
                           || PaintSystem.this.initialSelectionBounds.width == 1 && PaintSystem.this.initialSelectionBounds.height == 1
                     );
            }
         }

         return this.mask == null || this.bounds.width == 1 && this.bounds.height == 1;
      }

      public void setBoundsByOffset(int x, int y) {
         this.bounds.setBounds(x, y, this.bounds.width, this.bounds.height);
      }

      public void updateBoundsAfterMove(PaintSystem.Pos2i offset) {
         this.bounds.setLocation(offset.x, offset.y);
      }

      public void initializeBounds(int x, int y) {
         this.bounds = new Rectangle(x, y, 0, 0);
      }

      public Rectangle getUpdateRectangleBounds(PaintSystem.Pos2i start, PaintSystem.Pos2i end) {
         int x1 = Math.min(start.x, end.x);
         int y1 = Math.min(start.y, end.y);
         int width = Math.abs(end.x - start.x) + 1;
         int height = Math.abs(end.y - start.y) + 1;
         return new Rectangle(x1, y1, width, height);
      }

      public void clearMask() {
         this.mask = null;
         this.edgePoints.clear();
      }

      public void createRectangleMask() {
         BufferedImage mask = new BufferedImage(this.bounds.width, this.bounds.height, 2);
         Graphics2D g2d = mask.createGraphics();
         g2d.setColor(new Color(0, 0, 0, 255));
         g2d.fillRect(0, 0, this.bounds.width, this.bounds.height);
         g2d.dispose();
         this.setSelectionMask(mask);
      }

      public void clear() {
         this.clearMask();
         this.bounds.setBounds(0, 0, 0, 0);
      }

      public void deselect() {
         PaintSystem.this.initialSelectionBounds = new Rectangle(PaintSystem.this.selection.bounds);
         PaintSystem.this.initialSelectionMask = PaintSystem.this.selection.mask != null ? PaintSystem.deepCopy(PaintSystem.this.selection.mask) : null;
         this.clearMask();
         this.bounds.setBounds(0, 0, 0, 0);
         PaintSystem.SelectionAction action = PaintSystem.this.new SelectionAction(
            PaintSystem.this.initialSelectionBounds, PaintSystem.this.initialSelectionMask, PaintSystem.this.selection.bounds, PaintSystem.this.selection.mask
         );
         PaintSystem.this.actionManager.beginAction(action);
         PaintSystem.this.actionManager.commitAction();
         PaintSystem.this.dirty = true;
      }

      public void updateEdgePoints() {
         Rectangle bounds = this.bounds;
         BufferedImage mask = this.mask;
         if (PaintSystem.this.selection.getType() == PaintSystem.Selection.Type.ADD && PaintSystem.this.selection.adjustingSelection) {
            if (PaintSystem.this.initialSelectionMask == null && mask == null) {
               return;
            }

            Rectangle combinedBounds = PaintSystem.this.selection.isEmpty()
               ? PaintSystem.this.initialSelectionBounds
               : (Rectangle)PaintSystem.this.selection.bounds.createUnion(PaintSystem.this.initialSelectionBounds);
            BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, 2);
            Graphics2D g = combinedMask.createGraphics();
            if (mask != null) {
               g.drawImage(
                  PaintSystem.this.selection.mask,
                  PaintSystem.this.selection.bounds.x - combinedBounds.x,
                  PaintSystem.this.selection.bounds.y - combinedBounds.y,
                  PaintSystem.this.selection.mask.getWidth(),
                  PaintSystem.this.selection.mask.getHeight(),
                  null
               );
            }

            if (PaintSystem.this.initialSelectionMask != null) {
               g.drawImage(
                  PaintSystem.this.initialSelectionMask,
                  PaintSystem.this.initialSelectionBounds.x - combinedBounds.x,
                  PaintSystem.this.initialSelectionBounds.y - combinedBounds.y,
                  PaintSystem.this.initialSelectionMask.getWidth(),
                  PaintSystem.this.initialSelectionMask.getHeight(),
                  null
               );
            }

            g.dispose();
            mask = combinedMask;
         } else if (PaintSystem.this.selection.getType() == PaintSystem.Selection.Type.REMOVE && PaintSystem.this.selection.adjustingSelection) {
            if (PaintSystem.this.initialSelectionMask == null && mask == null) {
               return;
            }

            Rectangle combinedBoundsx = PaintSystem.this.selection.isEmpty()
               ? PaintSystem.this.initialSelectionBounds
               : (Rectangle)PaintSystem.this.selection.bounds.createUnion(PaintSystem.this.initialSelectionBounds);
            BufferedImage combinedMaskx = new BufferedImage(combinedBoundsx.width, combinedBoundsx.height, 2);
            Graphics2D gx = combinedMaskx.createGraphics();
            if (PaintSystem.this.initialSelectionMask != null) {
               gx.drawImage(
                  PaintSystem.this.initialSelectionMask,
                  PaintSystem.this.initialSelectionBounds.x - combinedBoundsx.x,
                  PaintSystem.this.initialSelectionBounds.y - combinedBoundsx.y,
                  PaintSystem.this.initialSelectionMask.getWidth(),
                  PaintSystem.this.initialSelectionMask.getHeight(),
                  null
               );
            }

            for (int y = 0; y < bounds.height; y++) {
               for (int x = 0; x < bounds.width; x++) {
                  int globalX = bounds.x + x;
                  int globalY = bounds.y + y;
                  if (globalX >= 0
                     && globalX < PaintSystem.this.width
                     && globalY >= 0
                     && globalY < PaintSystem.this.height
                     && x >= 0
                     && x < mask.getWidth()
                     && y >= 0
                     && y < mask.getHeight()) {
                     int maskAlpha = mask.getRGB(x, y) >> 24 & 0xFF;
                     if (maskAlpha > 0) {
                        combinedMaskx.setRGB(globalX - combinedBoundsx.x, globalY - combinedBoundsx.y, 0);
                     }
                  }
               }
            }

            gx.dispose();
            mask = combinedMaskx;
         }

         if (mask != null) {
            this.edgePoints = this.orderEdgePoints(this.findEdgePoints(mask).stream().toList());
         }
      }

      public void setSelectionMask(BufferedImage mask) {
         this.mask = mask;
         if (mask != null) {
            this.cropMaskToSelection();
         } else {
            this.clear();
            this.updateEdgePoints();
         }
      }

      public BufferedImage generateMagicWandMask(PaintSystem.Layer layer, int x, int y, float tolerance, boolean global) {
         if (layer != null && layer.pixels != null) {
            int width = layer.pixels.getWidth();
            int height = layer.pixels.getHeight();
            if (x >= 0 && x < width && y >= 0 && y < height) {
               BufferedImage mask = new BufferedImage(width, height, 2);
               int targetColor = layer.pixels.getRGB(x, y);
               float[] targetHSV = PaintSystem.this.rgbToHsv(targetColor);
               if (global) {
                  for (int py = 0; py < height; py++) {
                     for (int px = 0; px < width; px++) {
                        int currentColor = layer.pixels.getRGB(px, py);
                        int alpha = currentColor >> 24 & 0xFF;
                        int targetAlpha = targetColor >> 24 & 0xFF;
                        float difference = PaintSystem.this.calculateColorDifference(targetHSV, PaintSystem.this.rgbToHsv(currentColor));
                        if (alpha == 0 && targetAlpha == 0 || difference <= tolerance) {
                           mask.setRGB(px, py, -16777216);
                        }
                     }
                  }
               } else {
                  Stack<Point> stack = new Stack<>();
                  boolean[][] visited = new boolean[width][height];
                  stack.push(new Point(x, y));

                  while (!stack.isEmpty()) {
                     Point p = stack.pop();
                     int pxx = p.x;
                     int py = p.y;
                     if (pxx >= 0 && pxx < width && py >= 0 && py < height && !visited[pxx][py]) {
                        visited[pxx][py] = true;
                        int currentColor = layer.pixels.getRGB(pxx, py);
                        int alpha = currentColor >> 24 & 0xFF;
                        int targetAlpha = targetColor >> 24 & 0xFF;
                        float difference = PaintSystem.this.calculateColorDifference(targetHSV, PaintSystem.this.rgbToHsv(currentColor));
                        if (alpha == 0 && targetAlpha == 0 || difference <= tolerance) {
                           mask.setRGB(pxx, py, -16777216);
                           if (pxx > 0) {
                              stack.push(new Point(pxx - 1, py));
                           }

                           if (pxx < width - 1) {
                              stack.push(new Point(pxx + 1, py));
                           }

                           if (py > 0) {
                              stack.push(new Point(pxx, py - 1));
                           }

                           if (py < height - 1) {
                              stack.push(new Point(pxx, py + 1));
                           }
                        }
                     }
                  }
               }

               return mask;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      public BufferedImage extractSelectedArea(PaintSystem.Layer layer) {
         if (!this.isEmpty() && layer != null && layer.pixels != null && this.mask != null) {
            BufferedImage selectedArea = new BufferedImage(this.bounds.width, this.bounds.height, 2);

            for (int y = 0; y < this.bounds.height; y++) {
               for (int x = 0; x < this.bounds.width; x++) {
                  int maskAlpha = this.mask.getRGB(x, y) >> 24 & 0xFF;
                  if (maskAlpha > 0) {
                     int globalX = this.bounds.x + x;
                     int globalY = this.bounds.y + y;
                     if (globalX >= 0 && globalX < layer.pixels.getWidth() && globalY >= 0 && globalY < layer.pixels.getHeight()) {
                        int pixel = layer.pixels.getRGB(globalX, globalY);
                        selectedArea.setRGB(x, y, pixel);
                     }
                  }
               }
            }

            return selectedArea;
         } else {
            return null;
         }
      }

      public void pasteToLayer(PaintSystem.Layer layer, BufferedImage image) {
         if (layer != null && image != null) {
            Graphics2D g2d = layer.pixels.createGraphics();
            g2d.drawImage(image, this.bounds.x, this.bounds.y, null);
            g2d.dispose();
         }
      }

      public void deleteFromLayer(PaintSystem.Layer layer) {
         if (!this.isEmpty() && layer != null && layer.pixels != null) {
            for (int y = 0; y < this.bounds.height; y++) {
               for (int x = 0; x < this.bounds.width; x++) {
                  int globalX = this.bounds.x + x;
                  int globalY = this.bounds.y + y;
                  if (globalX >= 0
                     && globalX < layer.pixels.getWidth()
                     && globalY >= 0
                     && globalY < layer.pixels.getHeight()
                     && x >= 0
                     && x < this.mask.getWidth()
                     && y >= 0
                     && y < this.mask.getHeight()) {
                     int maskAlpha = this.mask.getRGB(x, y) >> 24 & 0xFF;
                     if (maskAlpha > 0) {
                        layer.pixels.setRGB(globalX, globalY, 0);
                     }
                  }
               }
            }
         }
      }

      private void cropMaskToSelection() {
         if (this.mask == null) {
            this.clear();
            PaintSystem.this.dirty = true;
         } else {
            int minX = this.mask.getWidth();
            int minY = this.mask.getHeight();
            int maxX = 0;
            int maxY = 0;
            boolean empty = true;

            for (int y = 0; y < this.mask.getHeight(); y++) {
               for (int x = 0; x < this.mask.getWidth(); x++) {
                  if (this.bounds.x + x <= PaintSystem.this.width && this.bounds.y + y <= PaintSystem.this.height) {
                     int alpha = this.mask.getRGB(x, y) >> 24 & 0xFF;
                     if (alpha > 0) {
                        empty = false;
                        if (x < minX) {
                           minX = x;
                        }

                        if (y < minY) {
                           minY = y;
                        }

                        if (x > maxX) {
                           maxX = x;
                        }

                        if (y > maxY) {
                           maxY = y;
                        }
                     }
                  }
               }
            }

            if (minX <= maxX && minY <= maxY && !empty) {
               int width = maxX - minX + 1;
               int height = maxY - minY + 1;
               BufferedImage croppedMask = new BufferedImage(width, height, 2);

               for (int y = 0; y < height; y++) {
                  for (int xx = 0; xx < width; xx++) {
                     int pixel = this.mask.getRGB(minX + xx, minY + y);
                     croppedMask.setRGB(xx, y, pixel);
                  }
               }

               this.mask = croppedMask;
               this.bounds = new Rectangle(this.bounds.x + minX, this.bounds.y + minY, width, height);
            } else {
               this.clear();
               PaintSystem.this.dirty = true;
            }

            this.updateEdgePoints();
         }
      }

      public void render(Graphics2D g2d) {
         Rectangle bounds = this.bounds;
         BufferedImage mask = this.mask;
         if (PaintSystem.this.selection.getType() != PaintSystem.Selection.Type.REPLACE && PaintSystem.this.selection.adjustingSelection) {
            if (PaintSystem.this.selection.getType() == PaintSystem.Selection.Type.ADD) {
               if (PaintSystem.this.initialSelectionMask == null && mask == null) {
                  return;
               }

               Rectangle combinedBounds = PaintSystem.this.selection.isEmpty()
                  ? PaintSystem.this.initialSelectionBounds
                  : (Rectangle)PaintSystem.this.selection.bounds.createUnion(PaintSystem.this.initialSelectionBounds);
               BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, 2);
               Graphics2D g = combinedMask.createGraphics();
               if (mask != null) {
                  g.drawImage(
                     PaintSystem.this.selection.mask,
                     PaintSystem.this.selection.bounds.x - combinedBounds.x,
                     PaintSystem.this.selection.bounds.y - combinedBounds.y,
                     PaintSystem.this.selection.mask.getWidth(),
                     PaintSystem.this.selection.mask.getHeight(),
                     null
                  );
               }

               if (PaintSystem.this.initialSelectionMask != null) {
                  g.drawImage(
                     PaintSystem.this.initialSelectionMask,
                     PaintSystem.this.initialSelectionBounds.x - combinedBounds.x,
                     PaintSystem.this.initialSelectionBounds.y - combinedBounds.y,
                     PaintSystem.this.initialSelectionMask.getWidth(),
                     PaintSystem.this.initialSelectionMask.getHeight(),
                     null
                  );
               }

               g.dispose();
               bounds = combinedBounds;
               mask = combinedMask;
            } else if (PaintSystem.this.selection.getType() == PaintSystem.Selection.Type.REMOVE) {
               if (PaintSystem.this.initialSelectionMask == null && mask == null) {
                  return;
               }

               Rectangle combinedBoundsx = PaintSystem.this.selection.isEmpty()
                  ? PaintSystem.this.initialSelectionBounds
                  : (Rectangle)PaintSystem.this.selection.bounds.createUnion(PaintSystem.this.initialSelectionBounds);
               BufferedImage combinedMaskx = new BufferedImage(combinedBoundsx.width, combinedBoundsx.height, 2);
               Graphics2D gx = combinedMaskx.createGraphics();
               if (PaintSystem.this.initialSelectionMask != null) {
                  gx.drawImage(
                     PaintSystem.this.initialSelectionMask,
                     PaintSystem.this.initialSelectionBounds.x - combinedBoundsx.x,
                     PaintSystem.this.initialSelectionBounds.y - combinedBoundsx.y,
                     PaintSystem.this.initialSelectionMask.getWidth(),
                     PaintSystem.this.initialSelectionMask.getHeight(),
                     null
                  );
               }

               for (int y = 0; y < bounds.height; y++) {
                  for (int x = 0; x < bounds.width; x++) {
                     int globalX = bounds.x + x;
                     int globalY = bounds.y + y;
                     if (globalX >= 0
                        && globalX < PaintSystem.this.width
                        && globalY >= 0
                        && globalY < PaintSystem.this.height
                        && x >= 0
                        && x < mask.getWidth()
                        && y >= 0
                        && y < mask.getHeight()) {
                        int maskAlpha = mask.getRGB(x, y) >> 24 & 0xFF;
                        if (maskAlpha > 0) {
                           Color tealTint = new Color(418317056, true);
                           g2d.setColor(tealTint);
                           g2d.fillRect(globalX, globalY, 1, 1);
                        }
                     }
                  }
               }

               gx.dispose();
               bounds = combinedBoundsx;
               mask = combinedMaskx;
            }
         } else if (mask == null) {
            return;
         }

         BufferedImage img = PaintSystem.deepCopy(mask);
         if (PaintSystem.this.getCurrentTool().shouldSelectionShowMask() && PaintSystem.this.movingSelection == null) {
            Color tealTint = new Color(811204590, true);

            for (int y = 0; y < img.getHeight(); y++) {
               for (int xx = 0; xx < img.getWidth(); xx++) {
                  int alpha = img.getRGB(xx, y) >> 24 & 0xFF;
                  if (alpha > 0) {
                     img.setRGB(xx, y, tealTint.getRGB());
                  }
               }
            }

            g2d.drawImage(img, bounds.x, bounds.y, null);
         }

         this.renderDashedOutline(g2d, bounds);
      }

      private void renderDashedOutline(Graphics2D g2d, Rectangle bounds) {
         int dotLengthMAX = 5;
         int skipLengthMAX = 1;
         float clientTicks = ClientEvents.getClientTicks();

         for (int i = 0; i < this.edgePoints.size(); i++) {
            if (!((i + clientTicks * 0.5F) % (dotLengthMAX + skipLengthMAX) > dotLengthMAX - 1)) {
               Point p = this.edgePoints.get(i);
               float hue = ((float)i / this.edgePoints.size() + clientTicks * 0.01F) % 1.0F;
               int col = (PaintSystem.this.movingSelection == null && !PaintSystem.this.getCurrentTool().shouldShowColorSliders() ? 128 : 48) << 24
                  | Color.getHSBColor(hue, 1.0F, 1.0F).getRGB() & 16777215;
               Color color = new Color(col, true);
               g2d.setColor(color);
               int renderX = bounds.x + p.x;
               int renderY = bounds.y + p.y;
               g2d.fillRect(renderX, renderY, 1, 1);
            }
         }
      }

      private List<Point> orderEdgePoints(List<Point> edgePoints) {
         if (edgePoints.isEmpty()) {
            return Collections.emptyList();
         } else {
            List<Point> orderedPoints = new ArrayList<>();
            Set<Point> visited = new HashSet<>();
            Point currentPoint = (Point)edgePoints.getFirst();
            orderedPoints.add(currentPoint);
            visited.add(currentPoint);

            while (orderedPoints.size() < edgePoints.size()) {
               List<Point> unvisited = edgePoints.stream().filter(p -> !visited.contains(p)).toList();
               if (unvisited.isEmpty()) {
                  break;
               }

               double minDist = 1.7976931348623157E308;

               for (Point p : unvisited) {
                  double d = currentPoint.distance(p);
                  if (d < minDist) {
                     minDist = d;
                  }
               }

               Point finalCurrentPoint1 = currentPoint;
               double finalMinDist = minDist;
               List<Point> minDistPoints = unvisited.stream().filter(px -> finalCurrentPoint1.distance(px) == finalMinDist).toList();
               String prevDirection;
               if (orderedPoints.size() >= 2) {
                  Point prevPrev = orderedPoints.get(orderedPoints.size() - 2);
                  Point prev = (Point)orderedPoints.getLast();
                  int dx = prev.x - prevPrev.x;
                  int dy = prev.y - prevPrev.y;
                  prevDirection = this.computeDirection(dx, dy);
               } else {
                  prevDirection = null;
               }

               List<Point> sameDirCandidates = new ArrayList<>();
               if (prevDirection != null) {
                  Point finalCurrentPoint = currentPoint;
                  sameDirCandidates = minDistPoints.stream().filter(px -> {
                     int pDx = px.x - finalCurrentPoint.x;
                     int pDy = px.y - finalCurrentPoint.y;
                     String dir = this.computeDirection(pDx, pDy);
                     return dir != null && dir.equals(prevDirection);
                  }).toList();
               }

               List<Point> candidatesToConsider = new ArrayList<>(sameDirCandidates.isEmpty() ? minDistPoints : sameDirCandidates);
               candidatesToConsider.sort(this.getDirectionComparator(currentPoint));
               if (candidatesToConsider.isEmpty()) {
                  break;
               }

               Point nearestPoint = (Point)candidatesToConsider.getFirst();
               orderedPoints.add(nearestPoint);
               visited.add(nearestPoint);
               currentPoint = nearestPoint;
            }

            return orderedPoints;
         }
      }

      private String computeDirection(int dx, int dy) {
         if (dx > 0 && dy == 0) {
            return "E";
         } else if (dx < 0 && dy == 0) {
            return "W";
         } else if (dy > 0 && dx == 0) {
            return "S";
         } else {
            return dy < 0 && dx == 0 ? "N" : null;
         }
      }

      private Comparator<Point> getDirectionComparator(Point currentPoint) {
         return (a, b) -> {
            int aDir = this.getDirectionPriority(currentPoint, a);
            int bDir = this.getDirectionPriority(currentPoint, b);
            if (aDir != bDir) {
               return Integer.compare(aDir, bDir);
            } else if (aDir != 1 && aDir != 2) {
               int cmpY = Integer.compare(a.y, b.y);
               if (cmpY != 0) {
                  return cmpY;
               } else {
                  return aDir == 3 ? Integer.compare(a.x, b.x) : Integer.compare(b.x, a.x);
               }
            } else {
               int cmpX = Integer.compare(a.x, b.x);
               if (cmpX != 0) {
                  return cmpX;
               } else {
                  return aDir == 1 ? Integer.compare(a.y, b.y) : Integer.compare(b.y, a.y);
               }
            }
         };
      }

      private int getDirectionPriority(Point current, Point p) {
         int dx = p.x - current.x;
         int dy = p.y - current.y;
         if (dy < 0) {
            return 1;
         } else if (dy > 0) {
            return 2;
         } else if (dx > 0) {
            return 3;
         } else {
            return dx < 0 ? 4 : 5;
         }
      }

      private Set<Point> findEdgePoints(BufferedImage mask) {
         if (mask == null) {
            return Set.of();
         } else {
            Set<Point> edgePoints = new HashSet<>();
            int width = mask.getWidth();
            int height = mask.getHeight();

            for (int y = 0; y < height; y++) {
               for (int x = 0; x < width; x++) {
                  int alpha = mask.getRGB(x, y) >> 24 & 0xFF;
                  if (alpha > 0 && this.isEdgePixel(mask, x, y, width, height)) {
                     edgePoints.add(new Point(x, y));
                  }
               }
            }

            return edgePoints;
         }
      }

      private boolean isEdgePixel(BufferedImage mask, int x, int y, int width, int height) {
         if ((mask.getRGB(x, y) >> 24 & 0xFF) == 0) {
            return false;
         } else if (x != 0 && y != 0 && x != width - 1 && y != height - 1) {
            int[][] directions = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] d : directions) {
               int nx = x + d[0];
               int ny = y + d[1];
               if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                  int neighborAlpha = mask.getRGB(nx, ny) >> 24 & 0xFF;
                  if (neighborAlpha == 0) {
                     return true;
                  }
               }
            }

            return false;
         } else {
            return true;
         }
      }

      public static enum Type {
         REPLACE,
         ADD,
         REMOVE;
      }
   }

   public class SelectionAction implements PaintSystem.Action {
      private Rectangle oldBounds;
      private BufferedImage oldMask;
      private Rectangle newBounds;
      private BufferedImage newMask;

      public SelectionAction(Rectangle oldBounds, BufferedImage oldMask, Rectangle newBounds, BufferedImage newMask) {
         this.oldBounds = oldBounds == null ? null : new Rectangle(oldBounds);
         this.oldMask = oldMask != null ? PaintSystem.deepCopy(oldMask) : null;
         this.newBounds = newBounds == null ? null : new Rectangle(newBounds);
         this.newMask = newMask != null ? PaintSystem.deepCopy(newMask) : null;
      }

      @Override
      public void undo() {
         PaintSystem.this.selection.bounds.setLocation(0, 0);
         PaintSystem.this.selection.setSelectionMask(this.oldMask != null ? PaintSystem.deepCopy(this.oldMask) : null);
         PaintSystem.this.selection.bounds = this.oldBounds == null ? new Rectangle(0, 0, 0, 0) : new Rectangle(this.oldBounds);
      }

      @Override
      public void redo() {
         PaintSystem.this.selection.bounds.setLocation(0, 0);
         PaintSystem.this.selection.setSelectionMask(this.newMask != null ? PaintSystem.deepCopy(this.newMask) : null);
         PaintSystem.this.selection.bounds = this.newBounds == null ? new Rectangle(0, 0, 0, 0) : new Rectangle(this.newBounds);
      }
   }

   public class SelectionAndDrawAction implements PaintSystem.Action {
      private PaintSystem.Layer layer;
      private int index;
      private BufferedImage beforeImage;
      private BufferedImage afterImage;
      private Rectangle oldBounds;
      private BufferedImage oldMask;
      private Rectangle newBounds;
      private BufferedImage newMask;

      public SelectionAndDrawAction(PaintSystem.Layer layer, PaintSystem.Selection selection) {
         this.layer = layer;
         this.index = PaintSystem.this.getLayers().indexOf(layer);
         this.beforeImage = PaintSystem.deepCopy(layer.pixels);
         this.oldBounds = selection.bounds == null ? null : new Rectangle(selection.bounds);
         this.oldMask = selection.mask != null ? PaintSystem.deepCopy(selection.mask) : null;
      }

      public void captureAfter(PaintSystem.Selection selection) {
         this.afterImage = PaintSystem.deepCopy(this.layer.pixels);
         this.newBounds = selection.bounds == null ? null : new Rectangle(selection.bounds);
         this.newMask = selection.mask != null ? PaintSystem.deepCopy(selection.mask) : null;
      }

      @Override
      public void undo() {
         PaintSystem.Layer layer = PaintSystem.this.getLayers().get(this.index);
         Graphics2D g = layer.pixels.createGraphics();
         g.setBackground(new Color(Color.BLACK.getRGB() & 16777215, true));
         g.clearRect(0, 0, layer.pixels.getWidth(), layer.pixels.getHeight());
         g.drawImage(this.beforeImage, 0, 0, null);
         g.dispose();
         layer.dirty = true;
         PaintSystem.this.selection.bounds.setLocation(0, 0);
         PaintSystem.this.selection.setSelectionMask(this.oldMask != null ? PaintSystem.deepCopy(this.oldMask) : null);
         PaintSystem.this.selection.bounds = this.oldBounds == null ? new Rectangle(0, 0, 0, 0) : new Rectangle(this.oldBounds);
         PaintSystem.this.updateToServer = true;
      }

      @Override
      public void redo() {
         PaintSystem.Layer layer = PaintSystem.this.getLayers().get(this.index);
         Graphics2D g = layer.pixels.createGraphics();
         g.setBackground(new Color(Color.BLACK.getRGB() & 16777215, true));
         g.clearRect(0, 0, layer.pixels.getWidth(), layer.pixels.getHeight());
         g.drawImage(this.afterImage, 0, 0, null);
         g.dispose();
         layer.dirty = true;
         PaintSystem.this.selection.bounds.setLocation(0, 0);
         PaintSystem.this.selection.setSelectionMask(this.newMask != null ? PaintSystem.deepCopy(this.newMask) : null);
         PaintSystem.this.selection.bounds = this.newBounds == null ? new Rectangle(0, 0, 0, 0) : new Rectangle(this.newBounds);
         PaintSystem.this.updateToServer = true;
      }
   }

   public class ToggleButton extends PaintSystem.Button {
      public Function<PaintSystem, Boolean> toggled;
      public Consumer<PaintSystem> toggledOnClick;
      public String toggledTexture;
      public String toggledHoverTexture;
      public String toggledDisabledTexture;

      ToggleButton(
         float lx,
         float ly,
         float rx,
         float ry,
         float width,
         float height,
         Function<Float, Float> scale,
         String texture,
         String hoverTexture,
         String disabledTexture,
         String toggledTexture,
         String toggledHoverTexture,
         String toggledDisabledTexture,
         Consumer<PaintSystem> onClick,
         Consumer<PaintSystem> toggledOnClick,
         Component tooltip,
         Consumer<PaintSystem> onTick,
         Function<PaintSystem, Boolean> selected,
         Function<PaintSystem, Boolean> disabled,
         Function<PaintSystem, Boolean> visible,
         Function<PaintSystem, Boolean> toggled
      ) {
         super(lx, ly, rx, ry, width, height, scale, texture, hoverTexture, disabledTexture, onClick, tooltip, onTick, selected, disabled, visible);
         this.toggledTexture = toggledTexture;
         this.toggledHoverTexture = toggledHoverTexture;
         this.toggledDisabledTexture = toggledDisabledTexture;
         this.toggledOnClick = toggledOnClick;
         this.toggled = toggled;
      }

      ToggleButton(
         BiFunction<PaintSystem, Float, Float> lx,
         BiFunction<PaintSystem, Float, Float> ly,
         BiFunction<PaintSystem, Float, Float> rx,
         BiFunction<PaintSystem, Float, Float> ry,
         float width,
         float height,
         Function<Float, Float> scale,
         String texture,
         String hoverTexture,
         String disabledTexture,
         String toggledTexture,
         String toggledHoverTexture,
         String toggledDisabledTexture,
         Consumer<PaintSystem> onClick,
         Consumer<PaintSystem> toggledOnClick,
         Component tooltip,
         Consumer<PaintSystem> onTick,
         Function<PaintSystem, Boolean> selected,
         Function<PaintSystem, Boolean> disabled,
         Function<PaintSystem, Boolean> visible,
         Function<PaintSystem, Boolean> toggled
      ) {
         super(lx, ly, rx, ry, width, height, scale, texture, hoverTexture, disabledTexture, onClick, tooltip, onTick, selected, disabled, visible);
         this.toggledTexture = toggledTexture;
         this.toggledHoverTexture = toggledHoverTexture;
         this.toggledDisabledTexture = toggledDisabledTexture;
         this.toggledOnClick = toggledOnClick;
         this.toggled = toggled;
      }

      public boolean getToggled(PaintSystem paintSystem) {
         return this.toggled.apply(paintSystem);
      }

      @Override
      public String getTexture(PaintSystem paintSystem) {
         return this.getToggled(paintSystem) ? this.toggledTexture : super.getTexture(paintSystem);
      }

      @Override
      public String getDisabledTexture(PaintSystem paintSystem) {
         return this.getToggled(paintSystem) ? this.toggledDisabledTexture : super.getDisabledTexture(paintSystem);
      }

      @Override
      public String getHoverTexture(PaintSystem paintSystem) {
         return this.getToggled(paintSystem) ? this.toggledHoverTexture : super.getHoverTexture(paintSystem);
      }

      @Override
      public Consumer<PaintSystem> getOnClick(PaintSystem paintSystem) {
         return this.getToggled(paintSystem) ? this.toggledOnClick : super.getOnClick(paintSystem);
      }
   }

   public static enum Tool {
      BRUSH("Brush"),
      ERASER("Eraser"),
      SELECTION("Selection"),
      MOVE("Move"),
      FILL("Fill"),
      EYEDROPPER("Eyedropper"),
      MAGIC_WAND("Magic Wand");

      private final String name;

      private Tool(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public boolean shouldShowColorSliders() {
         return this == BRUSH || this == ERASER || this == EYEDROPPER || this == FILL;
      }

      public boolean shouldShowBrushSliders() {
         return this == BRUSH || this == ERASER;
      }

      public boolean shouldShowToleranceSliders() {
         return this == FILL || this == MAGIC_WAND;
      }

      public boolean shouldSelectionShowMask() {
         return this != BRUSH && this != ERASER && this != EYEDROPPER && this != FILL;
      }
   }

   public class ValueSlider {
      public float lx;
      public float ly;
      public float rx;
      public float ry;
      public float width;
      public float height;
      private boolean horizontal;
      private float value = 0.5F;
      private boolean dragging = false;
      private boolean hovering = false;
      private float hoveringScale = 1.0F;
      private float hoveringScaleOld = 1.0F;
      private Function<PaintSystem, Integer> color1;
      private Function<PaintSystem, Integer> color2;
      private Function<PaintSystem, Integer> sliderColor;
      private Function<PaintSystem, Boolean> visible;
      public float visibility = 0.0F;
      public float visibilityOld = 0.0F;
      private Function<PaintSystem, Component> tooltip;
      private boolean isSpecialHueSlider = false;
      private PaintSystem.ValueSlider.SliderListener listener;

      ValueSlider(
         float lx,
         float ly,
         float rx,
         float ry,
         float width,
         float height,
         Function<PaintSystem, Integer> color1,
         Function<PaintSystem, Integer> color2,
         Function<PaintSystem, Integer> sliderColor,
         Function<PaintSystem, Boolean> visible,
         Function<PaintSystem, Component> tooltip,
         boolean horizontal
      ) {
         this.visible = visible;
         this.color1 = color1;
         this.color2 = color2;
         this.tooltip = tooltip;
         this.sliderColor = sliderColor;
         this.lx = lx;
         this.ly = ly;
         this.rx = rx;
         this.ry = ry;
         this.width = width;
         this.height = height;
         this.horizontal = horizontal;
      }

      ValueSlider(
         float lx,
         float ly,
         float rx,
         float ry,
         float width,
         float height,
         Function<PaintSystem, Integer> color1,
         Function<PaintSystem, Integer> color2,
         Function<PaintSystem, Integer> sliderColor,
         Function<PaintSystem, Boolean> visible,
         Function<PaintSystem, Component> tooltip,
         boolean horizontal,
         boolean isSpecialHueSlider
      ) {
         this(lx, ly, rx, ry, width, height, color1, color2, sliderColor, visible, tooltip, horizontal);
         this.isSpecialHueSlider = isSpecialHueSlider;
      }

      public float getX(PageDrawing.PageOn pageOn) {
         return pageOn.isOnLeftSide() ? this.lx : this.rx;
      }

      public float getY(PageDrawing.PageOn pageOn) {
         return pageOn.isOnLeftSide() ? this.ly : this.ry;
      }

      public float getVisibility(float partial) {
         return Math.max(0.0F, HexereiUtil.easeInOutCubic(Mth.lerp(partial, this.visibilityOld, this.visibility)));
      }

      public void tick(PaintSystem ps) {
         this.visibilityOld = this.visibility;
         if (this.isVisible(ps)) {
            this.visibility = HexereiUtil.moveTo(this.visibility, 1.0F, 0.01F + Math.clamp(Math.abs(this.visibility - 1.0F), 0.0F, 1.0F) * 0.15F);
         } else {
            this.visibility = HexereiUtil.moveTo(this.visibility, -1.0F, 0.01F + Math.clamp(Math.abs(this.visibility - 1.0F), 0.0F, 1.0F) * 0.25F);
         }

         this.hoveringScaleOld = this.hoveringScale;
         if (this.hovering) {
            this.hoveringScale = HexereiUtil.moveTo(this.hoveringScale, 1.0F, 0.01F + Math.abs(this.hoveringScale - 1.0F) * 0.1F);
         } else {
            this.hoveringScale = HexereiUtil.moveTo(this.hoveringScale, 0.0F, 0.01F + Math.abs(this.hoveringScale - 0.0F) * 0.025F);
         }

         this.hovering = false;
      }

      public boolean isSpecialHueSlider() {
         return this.isSpecialHueSlider;
      }

      public Component getTooltip(PaintSystem ps) {
         return this.tooltip.apply(ps);
      }

      public boolean shouldRender(PaintSystem ps) {
         return this.visibility > 0.0F;
      }

      public boolean isVisible(PaintSystem ps) {
         return this.visible.apply(ps);
      }

      public int getColor1(PaintSystem ps) {
         return this.color1.apply(ps);
      }

      public int getColor2(PaintSystem ps) {
         return this.color2.apply(ps);
      }

      public int getSliderColor(PaintSystem ps) {
         return this.sliderColor.apply(ps);
      }

      public void setHovering() {
         this.hovering = true;
      }

      public float getHoveringScale(float partial) {
         float val = HexereiUtil.easeInOutCubic(Mth.lerp(partial, this.hoveringScaleOld, this.hoveringScale));
         return val * 1.25F + 1.0F;
      }

      public boolean isHorizontal() {
         return this.horizontal;
      }

      public boolean isDragging() {
         return this.dragging;
      }

      private boolean click(float cursorX, float cursorY, PageDrawing.PageOn pageOn) {
         float scale = 1.0F;
         float w1 = this.width / 326.0F * 2.55F * scale / 0.062F;
         float h1 = this.height / 326.0F * 2.55F * scale / 0.062F;
         float x1 = this.getX(pageOn) + 0.025F - (pageOn.isOnLeftSide() ? 0.0F : 0.09F);
         float y1 = this.getY(pageOn) - 0.5F - h1 / 2.0F;
         float u = (cursorX - x1) / w1;
         float v = (cursorY - y1) / h1;
         boolean clicked = u >= 0.0F && u <= 1.0F && v >= 0.0F && v <= 1.0F;
         this.dragging = clicked;
         return clicked;
      }

      public void updateValue(float cursorX, float cursorY, PageDrawing.PageOn pageOn) {
         float scale = 1.0F;
         float w1 = this.width / 326.0F * 2.55F * scale / 0.062F;
         float h1 = this.height / 326.0F * 2.55F * scale / 0.062F;
         float x1 = this.getX(pageOn) + 0.025F - (pageOn.isOnLeftSide() ? 0.0F : 0.09F);
         float y1 = this.getY(pageOn) - 0.5F;
         float u = (cursorX - x1) / w1;
         float v = (cursorY - y1 + h1 / 2.0F) / h1;
         if (this.horizontal) {
            this.value = Math.max(0.0F, Math.min(1.0F, u));
         } else {
            this.value = Math.max(0.0F, Math.min(1.0F, 1.0F - v));
         }

         if (this.listener != null) {
            this.listener.onValueChanged(this.value);
         }
      }

      public void setSliderListener(PaintSystem.ValueSlider.SliderListener listener) {
         this.listener = listener;
      }

      public float getValue() {
         return this.value;
      }

      public void setValue(float value) {
         this.value = value;
      }

      public interface SliderListener {
         void onValueChanged(float var1);
      }
   }

   public class ValueSliders {
      private final PaintSystem.ValueSlider hueSlider;
      private final PaintSystem.ValueSlider saturationSlider;
      private final PaintSystem.ValueSlider brightnessSlider;
      private final PaintSystem.ValueSlider alphaSlider;
      private final PaintSystem.ValueSlider hardnessSlider;
      private final PaintSystem.ValueSlider brushSizeSlider;
      private final PaintSystem.ValueSlider toleranceSlider;
      private final List<PaintSystem.ValueSlider> sliders = new ArrayList<>();
      private final PaintSystem parent;

      public ValueSliders(PaintSystem parent) {
         this.parent = parent;
         float x = 1.75F;
         float y = 7.75F;
         float height = 0.33F;
         float rightOffset = 0.8F;
         this.hueSlider = PaintSystem.this.new ValueSlider(
            x,
            y,
            x + rightOffset,
            y,
            8.0F,
            2.5F,
            ps -> -1,
            ps -> -1,
            ps -> Color.HSBtoRGB(this.getHueSlider().getValue(), 1.0F, 1.0F),
            ps -> ps.getCurrentTool().shouldShowColorSliders() && PaintSystem.this.toolsVisible,
            ps -> Component.translatable("Hue - %s", new Object[]{(int)(this.getHueSlider().getValue() * 360.0F)}).withStyle(ChatFormatting.GRAY),
            true,
            true
         );
         this.saturationSlider = PaintSystem.this.new ValueSlider(
            x,
            y + height,
            x + rightOffset,
            y + height,
            8.0F,
            2.5F,
            ps -> Color.HSBtoRGB(ps.getValueSliders().getHueSlider().getValue(), 0.0F, ps.getValueSliders().getBrightnessSlider().getValue()),
            ps -> Color.HSBtoRGB(ps.getValueSliders().getHueSlider().getValue(), 1.0F, ps.getValueSliders().getBrightnessSlider().getValue()),
            ps -> 0xFF000000 | ps.getColor() & 16777215,
            ps -> ps.getCurrentTool().shouldShowColorSliders() && PaintSystem.this.toolsVisible,
            ps -> Component.translatable("Saturation - %s%%", new Object[]{(int)(this.getSaturationSlider().getValue() * 100.0F)})
               .withStyle(ChatFormatting.GRAY),
            true
         );
         this.brightnessSlider = PaintSystem.this.new ValueSlider(
            x,
            y + height * 2.0F,
            x + rightOffset,
            y + height * 2.0F,
            8.0F,
            2.5F,
            ps -> Color.HSBtoRGB(ps.getValueSliders().getHueSlider().getValue(), ps.getValueSliders().getSaturationSlider().getValue(), 0.0F),
            ps -> Color.HSBtoRGB(ps.getValueSliders().getHueSlider().getValue(), ps.getValueSliders().getSaturationSlider().getValue(), 1.0F),
            ps -> 0xFF000000 | ps.getColor() & 16777215,
            ps -> ps.getCurrentTool().shouldShowColorSliders() && PaintSystem.this.toolsVisible,
            ps -> Component.translatable("Brightness - %s%%", new Object[]{(int)(this.getBrightnessSlider().getValue() * 100.0F)})
               .withStyle(ChatFormatting.GRAY),
            true
         );
         this.alphaSlider = PaintSystem.this.new ValueSlider(
            x + 1.2F,
            y + height,
            x + 1.2F + rightOffset,
            y + height,
            2.5F,
            6.5F,
            ps -> ps.getColor() & 16777215,
            ps -> 0xFF000000 | ps.getColor() & 16777215,
            ps -> ps.getColor(),
            ps -> ps.getCurrentTool().shouldShowColorSliders() && PaintSystem.this.toolsVisible,
            ps -> Component.translatable("Alpha - %s%%", new Object[]{(int)(this.getAlphaSlider().getValue() * 100.0F)}).withStyle(ChatFormatting.GRAY),
            false
         );
         this.hardnessSlider = PaintSystem.this.new ValueSlider(
            x + 1.75F,
            y + height * 1.5F,
            x + 1.75F + rightOffset,
            y + height * 1.5F,
            8.0F,
            2.5F,
            ps -> -14855091,
            ps -> -11806520,
            ps -> -13066092,
            ps -> ps.getCurrentTool().shouldShowBrushSliders() && PaintSystem.this.toolsVisible,
            ps -> Component.translatable("Hardness - %s%%", new Object[]{(int)(this.getHardnessSlider().getValue() * 100.0F)}).withStyle(ChatFormatting.GRAY),
            true
         );
         this.brushSizeSlider = PaintSystem.this.new ValueSlider(
            x + 1.75F,
            y + height * 0.5F,
            x + 1.75F + rightOffset,
            y + height * 0.5F,
            8.0F,
            2.5F,
            ps -> -14855091,
            ps -> -11806520,
            ps -> -13066092,
            ps -> ps.getCurrentTool().shouldShowBrushSliders() && PaintSystem.this.toolsVisible,
            ps -> Component.translatable(
                  "Brush Size - %s", new Object[]{(int)(this.getBrushSizeSlider().getValue() * ps.getActiveLayer().pixels.getWidth() / 4.0F + 1.0F)}
               )
               .withStyle(ChatFormatting.GRAY),
            true
         );
         this.toleranceSlider = PaintSystem.this.new ValueSlider(
            x + 1.75F,
            y + height * 1.9F,
            x + 1.75F + rightOffset,
            y + height * 1.9F,
            8.0F,
            2.5F,
            ps -> -14855091,
            ps -> -11806520,
            ps -> -13066092,
            ps -> ps.getCurrentTool().shouldShowToleranceSliders() && PaintSystem.this.toolsVisible,
            ps -> Component.translatable("Tolerance - %s%%", new Object[]{(int)(this.getToleranceSlider().getValue() * 100.0F)}).withStyle(ChatFormatting.GRAY),
            true
         );
         this.hueSlider.setSliderListener(value -> this.updateColor());
         this.saturationSlider.setSliderListener(value -> this.updateColor());
         this.brightnessSlider.setSliderListener(value -> this.updateColor());
         this.alphaSlider.setSliderListener(value -> this.updateColor());
         this.hardnessSlider.setSliderListener(value -> this.updateHardness());
         this.brushSizeSlider.setSliderListener(value -> this.updateBrushSize());
         this.toleranceSlider.setSliderListener(value -> this.updateTolerance());
         this.sliders.add(this.hueSlider);
         this.sliders.add(this.saturationSlider);
         this.sliders.add(this.brightnessSlider);
         this.sliders.add(this.alphaSlider);
         this.sliders.add(this.hardnessSlider);
         this.sliders.add(this.brushSizeSlider);
         this.sliders.add(this.toleranceSlider);
      }

      public List<PaintSystem.ValueSlider> getSliders() {
         return this.sliders;
      }

      private void updateColor() {
         float h = this.hueSlider.getValue();
         float s = this.saturationSlider.getValue();
         float v = this.brightnessSlider.getValue();
         float a = this.alphaSlider.getValue();
         this.parent.setColor((int)(a * 255.0F) << 24 | Color.HSBtoRGB(h, s, v) & 16777215);
         this.updateColorSliders(this.parent.getColor());
      }

      private void updateHardness() {
         this.parent.getBrush();
         PaintSystem.Brush.hardness = this.hardnessSlider.getValue();
      }

      private void updateHardnessSlider(float hardness) {
         this.hardnessSlider.setValue(hardness);
      }

      private void updateBrushSize() {
         this.parent.getBrush();
         PaintSystem.Brush.size = (int)(this.brushSizeSlider.getValue() * this.parent.width / 4.0F);
      }

      private void updateBrushSizeSlider(int size) {
         this.brushSizeSlider.setValue(size / (this.parent.width / 4.0F));
      }

      private void updateTolerance() {
         this.parent.getBrush();
         PaintSystem.Brush.tolerance = this.toleranceSlider.getValue();
      }

      private void updateToleranceSlider(float tolerance) {
         this.toleranceSlider.setValue(tolerance);
      }

      public void updateColorSliders(int col) {
         if (!this.hueSlider.dragging && !this.saturationSlider.dragging && !this.brightnessSlider.dragging && !this.alphaSlider.dragging) {
            float[] colors = HexereiUtil.rgbaIntToFloatArray(col);
            float[] hsv = new float[3];
            Color.RGBtoHSB((int)(colors[0] * 255.0F), (int)(colors[1] * 255.0F), (int)(colors[2] * 255.0F), hsv);
            if (hsv[1] != 0.0F) {
               this.hueSlider.setValue(hsv[0]);
            }

            if (hsv[2] != 0.0F) {
               this.saturationSlider.setValue(hsv[1]);
            }

            this.brightnessSlider.setValue(hsv[2]);
            this.alphaSlider.setValue(colors[3]);
         }
      }

      public PaintSystem.ValueSlider getHueSlider() {
         return this.hueSlider;
      }

      public PaintSystem.ValueSlider getSaturationSlider() {
         return this.saturationSlider;
      }

      public PaintSystem.ValueSlider getBrightnessSlider() {
         return this.brightnessSlider;
      }

      public PaintSystem.ValueSlider getAlphaSlider() {
         return this.alphaSlider;
      }

      public PaintSystem.ValueSlider getHardnessSlider() {
         return this.hardnessSlider;
      }

      public PaintSystem.ValueSlider getBrushSizeSlider() {
         return this.brushSizeSlider;
      }

      public PaintSystem.ValueSlider getToleranceSlider() {
         return this.toleranceSlider;
      }

      public void release() {
         this.hueSlider.dragging = false;
         this.saturationSlider.dragging = false;
         this.brightnessSlider.dragging = false;
         this.alphaSlider.dragging = false;
         this.hardnessSlider.dragging = false;
         this.brushSizeSlider.dragging = false;
         this.toleranceSlider.dragging = false;
      }

      public boolean click(float cursorX, float cursorY, PageDrawing.PageOn pageOn) {
         if (this.parent.getCurrentTool().shouldShowColorSliders()) {
            if (this.hueSlider.click(cursorX, cursorY, pageOn)) {
               return true;
            }

            if (this.saturationSlider.click(cursorX, cursorY, pageOn)) {
               return true;
            }

            if (this.brightnessSlider.click(cursorX, cursorY, pageOn)) {
               return true;
            }

            if (this.alphaSlider.click(cursorX, cursorY, pageOn)) {
               return true;
            }
         }

         if (this.parent.getCurrentTool().shouldShowBrushSliders()) {
            if (this.hardnessSlider.click(cursorX, cursorY, pageOn)) {
               return true;
            }

            if (this.brushSizeSlider.click(cursorX, cursorY, pageOn)) {
               return true;
            }
         }

         return this.parent.getCurrentTool().shouldShowToleranceSliders() && this.toleranceSlider.click(cursorX, cursorY, pageOn);
      }
   }
}
