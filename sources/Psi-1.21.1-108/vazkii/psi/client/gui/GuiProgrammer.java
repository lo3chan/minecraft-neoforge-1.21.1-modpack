package vazkii.psi.client.gui;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderStateShard.LightmapStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TooltipFlag.Default;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellGrid;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.client.core.helper.SharingHelper;
import vazkii.psi.client.gui.button.GuiButtonHelp;
import vazkii.psi.client.gui.button.GuiButtonIO;
import vazkii.psi.client.gui.button.GuiButtonSideConfig;
import vazkii.psi.client.gui.widget.CallbackTextFieldWidget;
import vazkii.psi.client.gui.widget.PiecePanelWidget;
import vazkii.psi.client.gui.widget.SideConfigWidget;
import vazkii.psi.client.gui.widget.SpellCostsWidget;
import vazkii.psi.client.gui.widget.StatusWidget;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.tile.TileProgrammer;
import vazkii.psi.common.core.handler.ConfigHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageSpellModified;
import vazkii.psi.common.spell.SpellCompiler;
import vazkii.psi.common.spell.other.PieceConnector;

@OnlyIn(Dist.CLIENT)
public class GuiProgrammer extends Screen {
   public static final ResourceLocation texture = ResourceLocation.parse("psi:textures/gui/programmer.png");
   public static final RenderType LAYER;
   public static SpellPiece clipboard = null;
   public static int selectedX;
   public static int selectedY;
   public final TileProgrammer programmer;
   public final Stack<Spell> undoSteps = new Stack<>();
   public final Stack<Spell> redoSteps = new Stack<>();
   public Spell spell;
   public List<Component> tooltip = new ArrayList<>();
   public Either<CompiledSpell, SpellCompilationException> compileResult;
   public int xSize;
   public int ySize;
   public int padLeft;
   public int padTop;
   public int left;
   public int top;
   public int gridLeft;
   public int gridTop;
   public int cursorX;
   public int cursorY;
   public boolean commentEnabled;
   public GuiButtonHelp helpButton;
   public EditBox spellNameField;
   public EditBox commentField;
   public PiecePanelWidget panelWidget;
   public SideConfigWidget configWidget;
   public SpellCostsWidget spellCostsWidget;
   public StatusWidget statusWidget;
   public TooltipFlag tooltipFlag;
   public boolean mouseMoved = false;
   public boolean takingScreenshot = false;
   public boolean shareToReddit = false;
   boolean spectator;

   public GuiProgrammer(TileProgrammer programmer) {
      this(programmer, programmer.spell);
   }

   public GuiProgrammer(TileProgrammer tile, Spell spell) {
      super(Component.empty());
      this.programmer = tile;
      this.spell = spell;
      this.compileResult = new SpellCompiler().compile(spell);
   }

   public static String convertIntToLetter(int i) {
      return !ConfigHandler.CLIENT.changeGridCoordinatesToLetterNumber.get() ? String.valueOf(i) : String.valueOf((char)(i % 27 + 64));
   }

   public void mouseMoved(double xPos, double mouseY) {
      this.mouseMoved = true;
   }

   protected void init() {
      this.xSize = 174;
      this.ySize = 184;
      this.padLeft = 7;
      this.padTop = 7;
      this.left = (this.width - this.xSize) / 2;
      this.top = (this.height - this.ySize) / 2;
      this.gridLeft = this.left + this.padLeft;
      this.gridTop = this.top + this.padTop;
      this.cursorX = this.cursorY = -1;
      this.tooltipFlag = this.getMinecraft().options.advancedItemTooltips ? Default.ADVANCED : Default.NORMAL;
      if (this.programmer == null) {
         this.spectator = false;
      } else {
         this.spectator = !this.programmer.playerLock.isEmpty()
            && this.getMinecraft().player != null
            && !this.programmer.playerLock.equals(this.getMinecraft().player.getName().getString());
      }

      this.statusWidget = (StatusWidget)this.addRenderableWidget(new StatusWidget(this.left - 48, this.top + 5, 48, 30, "", this));
      this.spellCostsWidget = (SpellCostsWidget)this.addRenderableWidget(
         new SpellCostsWidget(this.left + this.xSize + 3, this.top + (this.takingScreenshot ? 40 : 20), 100, 126, "", this)
      );
      this.panelWidget = (PiecePanelWidget)this.addRenderableWidget(new PiecePanelWidget(0, 0, 100, 125, "", this));
      this.helpButton = (GuiButtonHelp)this.addRenderableWidget(
         new GuiButtonHelp(this.left + this.xSize + 2, this.top + this.ySize - (this.spectator ? 32 : 48), this)
      );
      this.configWidget = (SideConfigWidget)this.addRenderableWidget(new SideConfigWidget(this.left - 81, this.top + 55, 81, 115, this));
      int var10005 = this.left + this.xSize - 130;
      this.spellNameField = (EditBox)this.addRenderableWidget(
         new CallbackTextFieldWidget(this.getMinecraft().font, var10005, this.top + this.ySize - 14, 120, 10, button -> {
            this.spell.name = this.spellNameField.getValue();
            this.onSpellChanged(true);
         })
      );
      this.spellNameField.setBordered(false);
      this.spellNameField.setMaxLength(20);
      this.spellNameField.setEditable(!this.spectator);
      int var10006 = this.top + this.ySize / 2 - 10;
      this.commentField = (EditBox)this.addRenderableWidget(
         new CallbackTextFieldWidget(this.getMinecraft().font, this.left, var10006, this.xSize, 20, button -> {})
      );
      this.commentField.setEditable(false);
      this.commentField.setVisible(false);
      this.commentField.setMaxLength(500);
      this.panelWidget.searchField = (EditBox)this.addRenderableWidget(new CallbackTextFieldWidget(this.getMinecraft().font, 0, 0, 70, 10, button -> {
         this.panelWidget.page = 0;
         this.panelWidget.updatePanelButtons();
      }));
      this.panelWidget.searchField.setEditable(false);
      this.panelWidget.searchField.setVisible(false);
      this.panelWidget.searchField.setBordered(false);
      if (this.spell == null) {
         this.spell = new Spell();
      }

      if (this.programmer != null && this.programmer.spell == null) {
         this.programmer.spell = this.spell;
      }

      this.spellNameField.setValue(this.spell.name);
      this.panelWidget.populatePanelButtons();
      this.onSelectedChanged();
      this.addRenderableWidget(new GuiButtonIO(this.left + this.xSize + 2, this.top + this.ySize - (this.spectator ? 16 : 32), true, this, button -> {
         if (hasShiftDown()) {
            CompoundTag cmp = new CompoundTag();
            if (this.spell != null) {
               this.spell.writeToNBT(cmp);
            }

            this.getMinecraft().keyboardHandler.setClipboard(cmp.toString());
         }
      }));
      if (!this.spectator) {
         this.addRenderableWidget(
            new GuiButtonIO(
               this.left + this.xSize + 2,
               this.top + this.ySize - 16,
               false,
               this,
               button -> {
                  if (hasShiftDown()) {
                     String cb = this.getMinecraft().keyboardHandler.getClipboard();
                     LocalPlayer player = Minecraft.getInstance().player;
                     if (player == null) {
                        return;
                     }

                     try {
                        cb = cb.replaceAll("([^a-z0-9])\\d+:", "$1");
                        CompoundTag cmp = TagParser.parseTag(cb);
                        if (!cmp.contains("modsRequired")) {
                           player.sendSystemMessage(
                              Component.translatable("psimisc.spellmaynotfunctionasintended").setStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                           );
                        } else {
                           ListTag mods = (ListTag)cmp.get("modsRequired");
                           if (mods == null) {
                              return;
                           }

                           for (Tag mod : mods) {
                              String modName = ((CompoundTag)mod).getString("modName");
                              if (!PsiAPI.SPELL_PIECE_REGISTRY
                                 .keySet()
                                 .stream()
                                 .map(ResourceLocation::getNamespace)
                                 .collect(Collectors.toSet())
                                 .contains(modName)) {
                                 player.sendSystemMessage(
                                    Component.translatable("psimisc.modnotfound", new Object[]{modName}).setStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                                 );
                              }

                              if (modName.equals("psi")) {
                                 boolean sendMessage = false;
                                 String modVersion = ((CompoundTag)mod).getString("modVersion");
                                 int[] versionEntry = Arrays.stream(modVersion.replaceFirst("^\\D+", "").split("\\D+")).mapToInt(Integer::parseInt).toArray();
                                 int[] currentVersion = Arrays.stream(
                                       ((ModContainer)ModList.get().getModContainerById("psi").get())
                                          .getModInfo()
                                          .getVersion()
                                          .toString()
                                          .replaceFirst("^\\D+", "")
                                          .split("\\D+")
                                    )
                                    .mapToInt(Integer::parseInt)
                                    .toArray();

                                 for (int i = 0; i < versionEntry.length && versionEntry.length == currentVersion.length; i++) {
                                    if (i + 1 > currentVersion.length) {
                                       sendMessage = true;
                                       break;
                                    }

                                    if (currentVersion[i] > versionEntry[i]) {
                                       break;
                                    }

                                    if (currentVersion[i] < versionEntry[i]) {
                                       sendMessage = true;
                                       break;
                                    }
                                 }

                                 if (sendMessage) {
                                    player.sendSystemMessage(
                                       Component.translatable("psimisc.spellonnewerversion").setStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                                    );
                                 }
                              }
                           }
                        }

                        this.spell = Spell.createFromNBT(cmp);
                        if (this.spell == null) {
                           return;
                        }

                        PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);

                        for (int i = 0; i < 9; i++) {
                           for (int j = 0; j < 9; j++) {
                              SpellPiece piece = this.spell.grid.gridData[i][j];
                              if (piece != null) {
                                 Optional<Entry<ResourceKey<Collection<Class<? extends SpellPiece>>>, Collection<Class<? extends SpellPiece>>>> advancementEntry = PsiAPI.ADVANCEMENT_GROUP_REGISTRY
                                    .entrySet()
                                    .stream()
                                    .filter(entry -> entry.getValue().contains(piece.getClass()))
                                    .findFirst();
                                 if (!advancementEntry.isEmpty()
                                    && !player.isCreative()
                                    && !data.isPieceGroupUnlocked(advancementEntry.get().getKey().location(), piece.registryKey)) {
                                    player.sendSystemMessage(
                                       Component.translatable("psimisc.missing_pieces").setStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                                    );
                                    return;
                                 }
                              }
                           }
                        }

                        this.pushState(true);
                        this.spellNameField.setValue(this.spell.name);
                        this.onSpellChanged(false);
                     } catch (Exception var14) {
                        player.sendSystemMessage(
                           Component.translatable("psimisc.malformed_json", new Object[]{var14.getMessage()})
                              .setStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                        );
                        Psi.logger.error("Error importing spell from clipboard", var14);
                     }
                  }
               }
            )
         );
      }
   }

   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (this.programmer == null
         || this.programmer.getLevel() == null
         || this.getMinecraft().player == null
         || this.programmer.getLevel().getBlockEntity(this.programmer.getBlockPos()) == this.programmer
            && this.programmer.canPlayerInteract(this.getMinecraft().player)) {
         String comment = "";
         int color = Psi.magical ? 0 : 16777215;
         graphics.pose().pushPose();
         this.renderBackground(graphics, mouseX, mouseY, partialTicks);
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         graphics.blit(texture, this.left, this.top, 0, 0, this.xSize, this.ySize);
         SpellPiece piece = null;
         if (SpellGrid.exists(selectedX, selectedY)) {
            piece = this.spell.grid.gridData[selectedX][selectedY];
         }

         this.cursorX = (mouseX - this.gridLeft) / 18;
         this.cursorY = (mouseY - this.gridTop) / 18;
         if (this.panelWidget.panelEnabled
            || this.cursorX > 8
            || this.cursorY > 8
            || this.cursorX < 0
            || this.cursorY < 0
            || mouseX < this.gridLeft
            || mouseY < this.gridTop) {
            this.cursorX = -1;
            this.cursorY = -1;
         }

         graphics.pose().pushPose();
         this.tooltip.clear();
         graphics.pose().translate(this.gridLeft, this.gridTop, 0.0F);
         BufferSource buffers = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
         this.spell.draw(graphics.pose(), buffers, 15728880);
         buffers.endBatch();
         this.compileResult
            .right()
            .ifPresent(
               ex -> {
                  Pair<Integer, Integer> errorPos = ex.location;
                  if (errorPos != null && (Integer)errorPos.getRight() != -1 && (Integer)errorPos.getLeft() != -1) {
                     graphics.drawString(
                        this.getMinecraft().font, "!!", (Integer)errorPos.getLeft() * 18 + 12, (Integer)errorPos.getRight() * 18 + 8, 16711680, true
                     );
                  }
               }
            );
         graphics.pose().popPose();
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         graphics.pose().translate(0.0F, 0.0F, 1.0F);
         if (selectedX != -1 && selectedY != -1 && !this.takingScreenshot) {
            graphics.blit(texture, this.gridLeft + selectedX * 18, this.gridTop + selectedY * 18, 32, this.ySize, 16, 16);
         }

         if (hasAltDown()) {
            this.tooltip.clear();
            this.cursorX = selectedX;
            this.cursorY = selectedY;
            mouseX = this.gridLeft + this.cursorX * 18 + 10;
            mouseY = this.gridTop + this.cursorY * 18 + 8;
         }

         if (this.takingScreenshot) {
            Set<String> addons = this.spell.getPieceNamespaces().stream().filter(namespace -> !namespace.equals("psi")).collect(Collectors.toSet());
            if (!addons.isEmpty()) {
               String requiredAddons = ChatFormatting.GREEN + "Required Addons:";
               graphics.drawString(this.getMinecraft().font, requiredAddons, this.left - this.font.width(requiredAddons) - 5, this.top + 40, 16777215, true);
               int i = 1;

               for (String addon : addons) {
                  if (ModList.get().getModContainerById(addon).isPresent()) {
                     String modName = ((ModContainer)ModList.get().getModContainerById(addon).get()).getModInfo().getDisplayName();
                     graphics.drawString(
                        this.getMinecraft().font, "* " + modName, this.left - this.font.width(requiredAddons) - 5, this.top + 40 + 10 * i, 16777215, true
                     );
                     i++;
                  }
               }
            }

            String version = "Psi " + ((ModContainer)ModList.get().getModContainerById("psi").get()).getModInfo().getVersion().toString();
            graphics.drawString(
               this.getMinecraft().font, version, this.left + this.xSize / 2.0F - this.font.width(version) / 2.0F, this.top - 22.0F, 16777215, true
            );
         }

         SpellPiece pieceAtCursor = null;
         if (this.cursorX != -1 && this.cursorY != -1) {
            pieceAtCursor = this.spell.grid.gridData[this.cursorX][this.cursorY];
            if (pieceAtCursor != null) {
               pieceAtCursor.getTooltip(this.tooltip);
               comment = pieceAtCursor.comment;
            }

            if (!this.takingScreenshot) {
               if (this.cursorX == selectedX && this.cursorY == selectedY) {
                  graphics.blit(texture, this.gridLeft + this.cursorX * 18, this.gridTop + this.cursorY * 18, 16, this.ySize, 8, 16);
               } else {
                  graphics.blit(texture, this.gridLeft + this.cursorX * 18, this.gridTop + this.cursorY * 18, 16, this.ySize, 16, 16);
               }
            }
         }

         int topY = this.top - 22;
         if (!this.takingScreenshot) {
            int topYText = topY;
            if (this.spectator) {
               String spectator = ChatFormatting.RED + I18n.get("psimisc.spectator", new Object[0]);
               graphics.drawString(this.getMinecraft().font, spectator, this.left + this.xSize / 2.0F - this.font.width(spectator) / 2.0F, topY, 16777215, true);
               topYText = topY - 10;
            }

            if (piece != null) {
               String pieceName = I18n.get(piece.getUnlocalizedName(), new Object[0]);
               graphics.drawString(
                  this.getMinecraft().font, pieceName, this.left + this.xSize / 2.0F - this.font.width(pieceName) / 2.0F, topYText, 16777215, true
               );
            }

            String coords;
            if (SpellGrid.exists(this.cursorX, this.cursorY)) {
               coords = I18n.get(
                  "psimisc.programmer_coords",
                  new Object[]{convertIntToLetter(selectedX + 1), selectedY + 1, convertIntToLetter(this.cursorX + 1), this.cursorY + 1}
               );
            } else {
               coords = I18n.get("psimisc.programmer_coords_no_cursor", new Object[]{convertIntToLetter(selectedX + 1), selectedY + 1});
            }

            int var10003 = this.left + 4;
            graphics.drawString(this.getMinecraft().font, coords, var10003, topY + this.ySize + 24, 1157627903);
            String version = "Psi " + ((ModContainer)ModList.get().getModContainerById("psi").get()).getModInfo().getVersion().toString();
            graphics.drawString(
               this.getMinecraft().font,
               version,
               this.left + this.xSize / 2.0F - this.font.width(version) / 2.0F,
               topY + this.ySize + 24 + this.font.wordWrapHeight(coords, this.font.width(coords)) + 5,
               1157627903,
               true
            );
         }

         if (Psi.magical) {
            graphics.drawString(
               this.getMinecraft().font, I18n.get("psimisc.name", new Object[0]), this.left + this.padLeft, this.spellNameField.getY() + 1, color
            );
         } else {
            graphics.drawString(
               this.getMinecraft().font, I18n.get("psimisc.name", new Object[0]), this.left + this.padLeft, this.spellNameField.getY() + 1, color, true
            );
         }

         if (this.commentEnabled) {
            String enterCommit = I18n.get("psimisc.enter_commit", new Object[0]);
            graphics.drawString(
               this.getMinecraft().font,
               enterCommit,
               this.left + this.xSize / 2.0F - this.font.width(enterCommit) / 2.0F,
               this.commentField.getY() + 24,
               16777215,
               true
            );
            String semicolonLine = I18n.get("psimisc.semicolon_line", new Object[0]);
            graphics.drawString(
               this.getMinecraft().font,
               semicolonLine,
               this.left + this.xSize / 2.0F - this.font.width(semicolonLine) / 2.0F,
               this.commentField.getY() + 34,
               16777215,
               true
            );
         }

         List<Component> legitTooltip = null;
         if (hasAltDown()) {
            legitTooltip = new ArrayList<>(this.tooltip);
         }

         if (hasAltDown()) {
            this.tooltip = legitTooltip;
         }

         for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTicks);
         }

         if (!this.takingScreenshot && this.tooltip != null && !this.tooltip.isEmpty() && pieceAtCursor == null && this.mouseMoved) {
            graphics.renderTooltip(this.getMinecraft().font, this.tooltip, Optional.empty(), mouseX, mouseY);
         }

         if (!this.takingScreenshot && pieceAtCursor != null && this.mouseMoved) {
            if (this.tooltip != null && !this.tooltip.isEmpty()) {
               pieceAtCursor.drawTooltip(graphics, mouseX, mouseY, this.tooltip, this);
            }

            if (comment != null && !comment.isEmpty()) {
               List<Component> commentList = Arrays.stream(comment.split(";")).<Component>map(Component::literal).collect(Collectors.toList());
               pieceAtCursor.drawCommentText(graphics, mouseX, mouseY, commentList, this);
            }
         }

         graphics.pose().popPose();
         if (this.takingScreenshot) {
            String name = this.spell.name;
            String export = Spell.CODEC.encode(this.spell, JsonOps.INSTANCE, JsonOps.INSTANCE.mapBuilder()).toString();
            if (this.shareToReddit) {
               SharingHelper.uploadAndShare(name, export);
            } else {
               SharingHelper.uploadAndOpen(name, export);
            }

            this.takingScreenshot = false;
            this.shareToReddit = false;
         }
      } else {
         this.getMinecraft().setScreen(null);
      }
   }

   public void addButtons(List<Button> list) {
      list.forEach(x$0 -> {
         Button var10000 = (Button)this.addRenderableWidget(x$0);
      });
   }

   public void pushState(boolean wipeRedo) {
      if (wipeRedo) {
         this.redoSteps.clear();
      }

      this.undoSteps.push(this.spell.copy());
      if (this.undoSteps.size() > 25) {
         this.undoSteps.removeFirst();
      }
   }

   public void onSpellChanged(boolean nameOnly) {
      if (this.programmer != null) {
         if (!this.spectator) {
            MessageSpellModified message = new MessageSpellModified(this.programmer.getBlockPos(), this.spell);
            MessageRegister.sendToServer(message);
         }

         this.programmer.spell = this.spell;
         this.programmer.onSpellChanged();
      }

      this.onSelectedChanged();
      if (!nameOnly || this.compileResult.right().filter(ex -> ex.getMessage().equals("psi.spellerror.noname")).isPresent() || this.spell.name.isEmpty()) {
         this.compileResult = new SpellCompiler().compile(this.spell);
      }
   }

   public void onSelectedChanged() {
      this.renderables.removeAll(this.configWidget.configButtons);
      this.children().removeAll(this.configWidget.configButtons);
      this.configWidget.configButtons.clear();
      this.spellNameField.setEditable(!this.spectator);
      if (selectedX != -1 && selectedY != -1) {
         SpellPiece piece = this.spell.grid.gridData[selectedX][selectedY];
         if (piece != null) {
            boolean intercept = piece.interceptKeystrokes();
            this.spellNameField.setEditable(!this.spectator && !intercept);
            if (piece.hasConfig()) {
               int i = 0;

               for (String paramName : piece.params.keySet()) {
                  SpellParam<?> param = piece.params.get(paramName);
                  int x = this.left - 17;
                  int y = this.top + 70 + i * 26;
                  UnmodifiableIterator var9 = ImmutableSet.of(
                        SpellParam.Side.TOP, SpellParam.Side.BOTTOM, SpellParam.Side.LEFT, SpellParam.Side.RIGHT, SpellParam.Side.OFF
                     )
                     .iterator();

                  while (var9.hasNext()) {
                     SpellParam.Side side = (SpellParam.Side)var9.next();
                     if (side.isEnabled() || param.canDisable) {
                        int xp = x + side.offx * 8;
                        int yp = y + side.offy * 8;
                        this.configWidget.configButtons.add(new GuiButtonSideConfig(this, selectedX, selectedY, i, paramName, side, xp, yp, button -> {
                           if (!this.spectator) {
                              this.pushState(true);
                              GuiButtonSideConfig.performAction(this, selectedX, selectedY, paramName, side);
                              this.onSpellChanged(false);
                           }
                        }));
                     }
                  }

                  i++;
               }

               this.configWidget.configButtons.forEach(x$0 -> {
                  Button var10000 = (Button)this.addRenderableWidget(x$0);
               });
               this.configWidget.configEnabled = true;
               return;
            }
         }
      }

      this.configWidget.configEnabled = false;
   }

   public boolean charTyped(char character, int keyCode) {
      if (this.programmer != null) {
         this.spell = this.programmer.spell;
      }

      if (this.spectator) {
         return false;
      } else {
         super.charTyped(character, keyCode);
         if (!this.commentEnabled && !this.spellNameField.isFocused() && selectedX != -1 && selectedY != -1) {
            SpellPiece piece = this.spell.grid.gridData[selectedX][selectedY];
            if (piece != null && piece.interceptKeystrokes() && piece.onCharTyped(character, keyCode, false)) {
               this.pushState(true);
               piece.onCharTyped(character, keyCode, true);
               this.onSpellChanged(false);
               return true;
            }
         }

         return false;
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.programmer != null) {
         this.spell = this.programmer.spell;
      }

      if (keyCode == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (this.spectator) {
         return true;
      } else {
         if (this.commentEnabled) {
            switch (keyCode) {
               case 256:
                  this.closeComment(false);
                  return true;
               case 257:
                  this.closeComment(true);
                  return true;
            }
         }

         SpellPiece piece = null;
         if (selectedX != -1 && selectedY != -1) {
            piece = this.spell.grid.gridData[selectedX][selectedY];
            if (piece != null && piece.interceptKeystrokes() && piece.onKeyPressed(keyCode, scanCode, false)) {
               this.pushState(true);
               piece.onKeyPressed(keyCode, scanCode, true);
               this.onSpellChanged(false);
               return true;
            }
         }

         if (this.spellNameField.isFocused() && keyCode == 258) {
            this.spellNameField.setFocused(false);
            return true;
         } else {
            if (!this.spellNameField.isFocused() && !this.panelWidget.panelEnabled && !this.commentEnabled) {
               int param = -1;

               for (int i = 0; i < 4; i++) {
                  if (InputConstants.isKeyDown(this.getMinecraft().getWindow().getWindow(), 49 + i)) {
                     param = i;
                  }
               }

               switch (keyCode) {
                  case 67:
                     if (piece != null && hasControlDown()) {
                        clipboard = piece.copy();
                        return true;
                     }
                     break;
                  case 68:
                     if (piece != null && hasControlDown()) {
                        this.commentField.setVisible(true);
                        this.commentField.setFocused(true);
                        this.commentField.setEditable(true);
                        this.spellNameField.setEditable(false);
                        this.commentField.setValue(piece.comment);
                        this.setFocused(this.commentField);
                        this.commentEnabled = true;
                        return true;
                     }
                     break;
                  case 71:
                     if (hasControlDown()) {
                        this.shareToReddit = false;
                        if (hasShiftDown() && hasAltDown()) {
                           this.takingScreenshot = true;
                        }

                        return true;
                     }
                     break;
                  case 82:
                     if (hasControlDown()) {
                        this.shareToReddit = true;
                        if (hasShiftDown() && hasAltDown()) {
                           this.takingScreenshot = true;
                        }

                        return true;
                     }
                     break;
                  case 86:
                     if (SpellGrid.exists(selectedX, selectedY) && clipboard != null && hasControlDown()) {
                        SpellPiece copy = clipboard.copy();
                        copy.x = selectedX;
                        copy.y = selectedY;
                        this.pushState(true);
                        this.spell.grid.gridData[selectedX][selectedY] = copy;
                        this.spell.grid.gridData[selectedX][selectedY].isInGrid = true;
                        this.onSpellChanged(false);
                        return true;
                     }
                     break;
                  case 88:
                     if (piece != null && hasControlDown()) {
                        clipboard = piece.copy();
                        this.pushState(true);
                        this.spell.grid.gridData[selectedX][selectedY] = null;
                        this.onSpellChanged(false);
                        return true;
                     }
                     break;
                  case 89:
                     if (hasControlDown() && !this.redoSteps.isEmpty()) {
                        this.pushState(false);
                        this.spell = this.redoSteps.pop();
                        this.onSpellChanged(false);
                        return true;
                     }
                     break;
                  case 90:
                     if (hasControlDown() && !this.undoSteps.isEmpty()) {
                        this.redoSteps.add(this.spell.copy());
                        this.spell = this.undoSteps.pop();
                        this.onSpellChanged(false);
                        return true;
                     }
                     break;
                  case 257:
                     this.panelWidget.openPanel();
                     return true;
                  case 258:
                     this.spellNameField.setFocused(!this.spellNameField.isFocused());
                     this.setFocused(this.spellNameField);
                     return true;
                  case 259:
                  case 261:
                     if (hasControlDown() && hasShiftDown() && !this.spell.grid.isEmpty()) {
                        this.pushState(true);
                        this.spell = new Spell();
                        this.spellNameField.setValue("");
                        this.onSpellChanged(false);
                        return true;
                     }

                     if (piece != null) {
                        this.pushState(true);
                        this.spell.grid.gridData[selectedX][selectedY] = null;
                        this.onSpellChanged(false);
                        return true;
                     }
                     break;
                  case 262:
                     if (hasControlDown()) {
                        if (hasShiftDown()) {
                           this.pushState(true);
                           this.spell.grid.rotate(true);
                           this.onSpellChanged(false);
                           return true;
                        }

                        if (this.spell.grid.shift(SpellParam.Side.RIGHT, false)) {
                           this.pushState(true);
                           this.spell.grid.shift(SpellParam.Side.RIGHT, true);
                           this.onSpellChanged(false);
                           return true;
                        }
                     } else if (!this.onSideButtonKeybind(piece, param, SpellParam.Side.RIGHT) && selectedX < 8) {
                        selectedX++;
                        this.onSelectedChanged();
                        if (hasShiftDown() && this.spell.grid.gridData[selectedX][selectedY] == null) {
                           PieceConnector connector = new PieceConnector(this.spell);
                           connector.x = selectedX;
                           connector.y = selectedY;
                           connector.paramSides.put(connector.target, SpellParam.Side.LEFT);
                           this.spell.grid.gridData[selectedX][selectedY] = connector;
                           this.onSpellChanged(false);
                        }

                        return true;
                     }
                     break;
                  case 263:
                     if (hasControlDown()) {
                        if (hasShiftDown()) {
                           this.pushState(true);
                           this.spell.grid.rotate(false);
                           this.onSpellChanged(false);
                           return true;
                        }

                        if (this.spell.grid.shift(SpellParam.Side.LEFT, false)) {
                           this.pushState(true);
                           this.spell.grid.shift(SpellParam.Side.LEFT, true);
                           this.onSpellChanged(false);
                           return true;
                        }
                     } else if (!this.onSideButtonKeybind(piece, param, SpellParam.Side.LEFT) && selectedX > 0) {
                        selectedX--;
                        this.onSelectedChanged();
                        if (hasShiftDown() && this.spell.grid.gridData[selectedX][selectedY] == null) {
                           PieceConnector connector = new PieceConnector(this.spell);
                           connector.x = selectedX;
                           connector.y = selectedY;
                           connector.paramSides.put(connector.target, SpellParam.Side.RIGHT);
                           this.spell.grid.gridData[selectedX][selectedY] = connector;
                           this.onSpellChanged(false);
                        }

                        return true;
                     }
                     break;
                  case 264:
                     if (hasControlDown()) {
                        if (hasShiftDown()) {
                           this.pushState(true);
                           this.spell.grid.mirrorVertical();
                           this.onSpellChanged(false);
                           return true;
                        }

                        if (this.spell.grid.shift(SpellParam.Side.BOTTOM, false)) {
                           this.pushState(true);
                           this.spell.grid.shift(SpellParam.Side.BOTTOM, true);
                           this.onSpellChanged(false);
                           return true;
                        }
                     } else if (!this.onSideButtonKeybind(piece, param, SpellParam.Side.BOTTOM) && selectedY < 8) {
                        selectedY++;
                        this.onSelectedChanged();
                        if (hasShiftDown() && this.spell.grid.gridData[selectedX][selectedY] == null) {
                           PieceConnector connector = new PieceConnector(this.spell);
                           connector.x = selectedX;
                           connector.y = selectedY;
                           connector.paramSides.put(connector.target, SpellParam.Side.TOP);
                           this.spell.grid.gridData[selectedX][selectedY] = connector;
                           this.onSpellChanged(false);
                        }

                        return true;
                     }
                     break;
                  case 265:
                     if (hasControlDown()) {
                        if (hasShiftDown()) {
                           this.pushState(true);
                           this.spell.grid.mirrorVertical();
                           this.onSpellChanged(false);
                           return true;
                        }

                        if (this.spell.grid.shift(SpellParam.Side.TOP, false)) {
                           this.pushState(true);
                           this.spell.grid.shift(SpellParam.Side.TOP, true);
                           this.onSpellChanged(false);
                           return true;
                        }
                     } else if (!this.onSideButtonKeybind(piece, param, SpellParam.Side.TOP) && selectedY > 0) {
                        selectedY--;
                        this.onSelectedChanged();
                        if (hasShiftDown() && this.spell.grid.gridData[selectedX][selectedY] == null) {
                           PieceConnector connector = new PieceConnector(this.spell);
                           connector.x = selectedX;
                           connector.y = selectedY;
                           connector.paramSides.put(connector.target, SpellParam.Side.BOTTOM);
                           this.spell.grid.gridData[selectedX][selectedY] = connector;
                           this.onSpellChanged(false);
                        }

                        return true;
                     }
               }
            }

            if (this.panelWidget.panelEnabled) {
               this.panelWidget.keyPressed(keyCode, scanCode, modifiers);
            }

            if (this.commentField.isFocused()) {
               this.commentField.keyPressed(keyCode, scanCode, modifiers);
            }

            if (this.spellNameField.isFocused()) {
               this.spellNameField.keyPressed(keyCode, scanCode, modifiers);
            }

            return false;
         }
      }
   }

   public boolean onSideButtonKeybind(SpellPiece piece, int param, SpellParam.Side side) {
      if (param > -1 && piece != null && piece.params.size() >= param) {
         for (Button button : this.configWidget.configButtons) {
            GuiButtonSideConfig config = (GuiButtonSideConfig)button;
            if (config.matches(param, side)) {
               if (side == SpellParam.Side.OFF || piece.paramSides.get(piece.params.get(config.paramName)) != side) {
                  config.onPress();
                  return true;
               }

               side = SpellParam.Side.OFF;
            }
         }
      }

      return side == SpellParam.Side.OFF;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      if (this.programmer != null) {
         this.spell = this.programmer.spell;
      }

      if (!this.commentEnabled) {
         boolean did = this.spellNameField.mouseClicked(mouseX, mouseY, mouseButton);
         if (did) {
            this.spellNameField.setFocused(true);
            this.setFocused(this.spellNameField);
         } else {
            this.spellNameField.setFocused(false);
         }

         if (this.commentField.isVisible()) {
            this.commentField.mouseClicked(mouseX, mouseY, mouseButton);
         }

         if (this.cursorX != -1 && this.cursorY != -1) {
            selectedX = this.cursorX;
            selectedY = this.cursorY;
            if (mouseButton == 1 && !this.spectator && hasShiftDown()) {
               this.pushState(true);
               this.spell.grid.gridData[selectedX][selectedY] = null;
               this.onSpellChanged(false);
               return true;
            }

            this.onSelectedChanged();
         }
      }

      for (GuiEventListener guieventlistener : this.children()) {
         if (guieventlistener.mouseClicked(mouseX, mouseY, mouseButton)) {
            if (mouseButton == 0) {
               this.setDragging(true);
            }

            return true;
         }
      }

      return false;
   }

   public boolean isSpectator() {
      return this.spectator;
   }

   private void closeComment(boolean save) {
      SpellPiece piece = null;
      if (selectedX != -1 && selectedY != -1) {
         piece = this.spell.grid.gridData[selectedX][selectedY];
      }

      if (save && piece != null) {
         String text = this.commentField.getValue();
         this.pushState(true);
         piece.comment = text;
         this.onSpellChanged(false);
      }

      this.spellNameField.setEditable(!this.spectator && (piece == null || !piece.interceptKeystrokes()));
      this.commentField.setFocused(false);
      this.commentField.setVisible(false);
      this.commentField.setEditable(false);
      this.commentField.setValue("");
      this.commentEnabled = false;
   }

   public boolean shouldCloseOnEsc() {
      return !this.panelWidget.panelEnabled && !this.commentEnabled;
   }

   public List<Renderable> getButtons() {
      return this.renderables;
   }

   public boolean isPauseScreen() {
      return (Boolean)ConfigHandler.CLIENT.pauseGameInProgrammer.get();
   }

   static {
      CompositeState glState = CompositeState.builder()
         .setShaderState(new ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader))
         .setTextureState(new TextureStateShard(texture, false, false))
         .setLightmapState(new LightmapStateShard(true))
         .setCullState(new CullStateShard(false))
         .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
         .createCompositeState(false);
      LAYER = RenderType.create("psi:programmer", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS, 128, false, false, glState);
   }
}
