package vazkii.psi.client.gui.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.programmer.ProgrammerPopulateEvent;
import vazkii.psi.client.gui.GuiProgrammer;
import vazkii.psi.client.gui.button.GuiButtonPage;
import vazkii.psi.client.gui.button.GuiButtonSpellPiece;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.spell.constant.PieceConstantNumber;

public class PiecePanelWidget extends AbstractWidget implements GuiEventListener {
   private static final int PIECES_PER_PAGE = 25;
   public final GuiProgrammer parent;
   public final List<Button> panelButtons = new ArrayList<>();
   public final List<GuiButtonSpellPiece> visibleButtons = new ArrayList<>();
   public boolean panelEnabled = false;
   public int panelCursor;
   public EditBox searchField;
   public int page = 0;

   public PiecePanelWidget(int x, int y, int width, int height, String message, GuiProgrammer programmer) {
      super(x, y, width, height, Component.nullToEmpty(message));
      this.parent = programmer;
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float pTicks) {
      if (this.panelEnabled) {
         graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -2013265920);
         if (!this.visibleButtons.isEmpty()) {
            Button button = this.visibleButtons.get(Math.max(0, Math.min(this.panelCursor + this.page * 25, this.visibleButtons.size() - 1)));
            int panelPieceX = button.getX();
            int panelPieceY = button.getY();
            graphics.fill(panelPieceX - 1, panelPieceY - 1, panelPieceX + 17, panelPieceY + 17, 1436129791);
         }

         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         graphics.blit(GuiProgrammer.texture, this.searchField.getX() - 14, this.searchField.getY() - 2, 0, this.parent.ySize + 16, 12, 12);
         String s = Math.min(Math.max(this.getPageCount(), 1), this.page + 1) + "/" + Math.max(this.getPageCount(), 1);
         graphics.drawString(
            this.parent.getMinecraft().font,
            s,
            this.getX() + this.width / 2.0F - this.parent.getMinecraft().font.width(s) / 2.0F,
            this.getY() + this.height - 12,
            16777215,
            true
         );
      }
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
      if (this.panelEnabled && pScrollY != 0.0) {
         int next = (int)(this.page - pScrollY / Math.abs(pScrollY));
         if (next >= 0 && next < this.getPageCount()) {
            this.page = next;
            this.updatePanelButtons();
         }
      }

      return false;
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.panelEnabled ? this.searchField.charTyped(p_charTyped_1_, p_charTyped_2_) : false;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.panelEnabled) {
         switch (keyCode) {
            case 256:
               this.closePanel();
               return true;
            case 257:
               if (!this.visibleButtons.isEmpty()) {
                  this.visibleButtons.get(this.panelCursor).onPress();
                  return true;
               }

               return false;
            case 258:
               if (!this.visibleButtons.isEmpty()) {
                  int newCursor = this.panelCursor + (Screen.hasAltDown() ? -1 : 1);
                  if (newCursor >= Math.min(this.visibleButtons.size(), 25)) {
                     this.panelCursor = 0;
                     return true;
                  }

                  this.panelCursor = Math.max(0, Math.min(newCursor, Math.min(this.visibleButtons.size(), 25) - 1));
                  return true;
               }
            default:
               this.searchField.keyPressed(keyCode, scanCode, modifiers);
         }
      }

      return false;
   }

   public int getPageCount() {
      return (int)Math.ceil(this.visibleButtons.size() / 25.0F);
   }

   public void populatePanelButtons() {
      PlayerDataHandler.PlayerData playerData = PlayerDataHandler.get(this.parent.getMinecraft().player);
      ProgrammerPopulateEvent event = new ProgrammerPopulateEvent(this.parent.getMinecraft().player, PsiAPI.SPELL_PIECE_REGISTRY);
      List<SpellPiece> shownPieces = new ArrayList<>();
      NeoForge.EVENT_BUS.post(event);

      for (ResourceLocation key : event.getSpellPieceRegistry().keySet()) {
         Class<? extends SpellPiece> clazz = (Class<? extends SpellPiece>)event.getSpellPieceRegistry().get(key);
         Optional<Entry<ResourceKey<Collection<Class<? extends SpellPiece>>>, Collection<Class<? extends SpellPiece>>>> advancementEntry = PsiAPI.ADVANCEMENT_GROUP_REGISTRY
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().contains(clazz))
            .findFirst();
         if (!advancementEntry.isEmpty()
            && (this.parent.getMinecraft().player.isCreative() || playerData.isPieceGroupUnlocked(advancementEntry.get().getKey().location(), key))) {
            SpellPiece piece = SpellPiece.create(clazz, this.parent.spell);
            shownPieces.clear();
            piece.getShownPieces(shownPieces);

            for (SpellPiece shownPiece : shownPieces) {
               GuiButtonSpellPiece spellPieceButton = new GuiButtonSpellPiece(this.parent, shownPiece, 0, 0, button -> {
                  if (!this.parent.isSpectator()) {
                     this.parent.pushState(true);
                     SpellPiece piece1 = ((GuiButtonSpellPiece)button).piece.copyFromSpell(this.parent.spell);
                     if (piece1.getPieceType() == EnumPieceType.TRICK && this.parent.spellNameField.getValue().isEmpty()) {
                        String pieceName = I18n.get(piece1.getUnlocalizedName(), new Object[0]);
                        String patternStr = I18n.get("psimisc.trick_pattern", new Object[0]);
                        Pattern pattern = Pattern.compile(patternStr);
                        Matcher matcher = pattern.matcher(pieceName);
                        if (matcher.matches()) {
                           String spellName = matcher.group(1);
                           this.parent.spellNameField.setValue(spellName);
                           this.parent.spell.name = spellName;
                           this.parent.onSpellChanged(true);
                        }
                     }

                     this.parent.spell.grid.gridData[GuiProgrammer.selectedX][GuiProgrammer.selectedY] = piece1;
                     this.parent.spell.grid.gridData[GuiProgrammer.selectedX][GuiProgrammer.selectedY].isInGrid = true;
                     this.parent.spell.grid.gridData[GuiProgrammer.selectedX][GuiProgrammer.selectedY].x = GuiProgrammer.selectedX;
                     this.parent.spell.grid.gridData[GuiProgrammer.selectedX][GuiProgrammer.selectedY].y = GuiProgrammer.selectedY;
                     this.parent.onSpellChanged(false);
                     this.closePanel();
                  }
               });
               spellPieceButton.visible = false;
               spellPieceButton.active = false;
               this.panelButtons.add(spellPieceButton);
               this.visibleButtons.add(spellPieceButton);
            }
         }
      }

      GuiButtonPage right = new GuiButtonPage(0, 0, true, this.parent, button -> {
         int max = this.getPageCount();
         int next = this.page + (((GuiButtonPage)button).right ? 1 : -1);
         if (next >= 0 && next < max) {
            this.page = next;
            this.updatePanelButtons();
         }
      });
      GuiButtonPage left = new GuiButtonPage(0, 0, false, this.parent, button -> {
         int max = this.getPageCount();
         int next = this.page + (((GuiButtonPage)button).right ? 1 : -1);
         if (next >= 0 && next < max) {
            this.page = next;
            this.updatePanelButtons();
         }
      });
      left.visible = false;
      left.active = false;
      right.visible = false;
      right.active = false;
      this.panelButtons.add(left);
      this.panelButtons.add(right);
      this.parent.addButtons(this.panelButtons);
   }

   public void updatePanelButtons() {
      this.panelCursor = 0;
      this.visibleButtons.clear();
      this.parent.getButtons().forEach(button -> {
         if (button instanceof GuiButtonPage || button instanceof GuiButtonSpellPiece) {
            ((Button)button).active = false;
            ((Button)button).visible = false;
         }
      });
      HashMap<Class<? extends SpellPiece>, Integer> pieceRankings = new HashMap<>();
      String text = this.searchField.getValue().toLowerCase(Locale.ROOT).trim();
      boolean noSearchTerms = text.isEmpty();
      this.parent.getButtons().forEach(button -> {
         if (button instanceof GuiButtonSpellPiece guiButtonSpellPiece) {
            SpellPiece piecex = guiButtonSpellPiece.getPiece();
            if (noSearchTerms) {
               this.visibleButtons.add(guiButtonSpellPiece);
            } else {
               int rank = this.ranking(text, piecex);
               if (rank > 0) {
                  pieceRankings.put((Class<? extends SpellPiece>)piecex.getClass(), rank);
                  this.visibleButtons.add(guiButtonSpellPiece);
               }
            }
         } else if (button instanceof GuiButtonPage otherPage) {
            if (otherPage.isRight() && this.page < this.getPageCount() - 1) {
               otherPage.setX(this.getX() + this.width - 22);
               otherPage.setY(this.getY() + this.height - 15);
               otherPage.visible = true;
               otherPage.active = true;
            } else if (!otherPage.isRight() && this.page > 0) {
               otherPage.setX(this.getX() + 4);
               otherPage.setY(this.getY() + this.height - 15);
               otherPage.visible = true;
               otherPage.active = true;
            }
         }
      });
      Comparator<GuiButtonSpellPiece> comparator;
      if (noSearchTerms) {
         comparator = Comparator.comparing(GuiButtonSpellPiece::getPieceSortingName);
      } else {
         comparator = Comparator.comparingInt(p -> -pieceRankings.get(p.getPiece().getClass()));
         comparator = comparator.thenComparing(GuiButtonSpellPiece::getPieceSortingName);
      }

      this.visibleButtons.sort(comparator);
      if (!text.isEmpty() && text.length() <= 5 && (text.matches("^-?\\d+(?:\\.\\d*)?") || text.matches("^-?\\d*(?:\\.\\d+)?"))) {
         this.parent
            .getButtons()
            .stream()
            .filter(el -> el instanceof GuiButtonSpellPiece ? ((GuiButtonSpellPiece)el).getPiece() instanceof PieceConstantNumber : false)
            .findFirst()
            .ifPresent(renderable -> {
               GuiButtonSpellPiece constantPiece = (GuiButtonSpellPiece)renderable;
               this.visibleButtons.remove(constantPiece);
               ((PieceConstantNumber)constantPiece.getPiece()).valueStr = text;
               this.visibleButtons.addFirst(constantPiece);
            });
      }

      int start = this.page * 25;

      for (int i = start; i < this.visibleButtons.size(); i++) {
         int c = i - start;
         if (c >= 25) {
            break;
         }

         GuiButtonSpellPiece piece = this.visibleButtons.get(i);
         GuiButtonSpellPiece buttonSpellPiece = (GuiButtonSpellPiece)this.parent.getButtons().stream().filter(el -> el.equals(piece)).findFirst().orElse(null);
         if (buttonSpellPiece != null) {
            buttonSpellPiece.setX(this.getX() + 5 + c % 5 * 18);
            buttonSpellPiece.setY(this.getY() + 20 + c / 5 * 18);
            buttonSpellPiece.visible = true;
            buttonSpellPiece.active = true;
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      if (this.parent.cursorX != -1
         && this.parent.cursorY != -1
         && !this.parent.commentEnabled
         && !this.parent.isSpectator()
         && mouseButton == 1
         && !this.panelEnabled) {
         this.openPanel();
         return true;
      } else if (this.panelEnabled
         && (mouseX < this.getX() || mouseY < this.getY() || mouseX > this.getX() + this.width || mouseY > this.getY() + this.height)
         && !this.parent.isSpectator()) {
         this.closePanel();
         return true;
      } else {
         return false;
      }
   }

   private int ranking(String token, SpellPiece p) {
      int rank = 0;
      String name = I18n.get(p.getUnlocalizedName(), new Object[0]).toLowerCase(Locale.ROOT);
      String desc = I18n.get(p.getUnlocalizedDesc(), new Object[0]).toLowerCase(Locale.ROOT);

      for (String nameToken : token.split("\\s+")) {
         if (!nameToken.isEmpty()) {
            if (!nameToken.startsWith("in:")) {
               if (nameToken.startsWith("out:")) {
                  String clippedToken = nameToken.substring(4);
                  if (!clippedToken.isEmpty()) {
                     String type = p.getEvaluationTypeString().getString().toLowerCase(Locale.ROOT);
                     if (this.rankTextToken(type, clippedToken) <= 0) {
                        return 0;
                     }

                     rank += this.rankTextToken(type, clippedToken);
                  }
               } else if (nameToken.startsWith("@")) {
                  String clippedToken = nameToken.substring(1);
                  if (!clippedToken.isEmpty()) {
                     ResourceLocation mod = PsiAPI.SPELL_PIECE_REGISTRY.getKey(p.getClass());
                     if (mod == null) {
                        return 0;
                     }

                     int modRank = this.rankTextToken(mod.getNamespace(), clippedToken);
                     if (modRank <= 0) {
                        return 0;
                     }

                     rank += modRank;
                  }
               } else {
                  int nameRank = this.rankTextToken(name, nameToken);
                  rank += nameRank;
                  if (nameRank <= 0 && this.rankTextToken(desc, nameToken) <= 0) {
                     return 0;
                  }

                  rank += this.rankTextToken(desc, nameToken) / 2;
               }
            } else {
               String clippedToken = nameToken.substring(3);
               if (!clippedToken.isEmpty()) {
                  int maxRank = 0;

                  for (SpellParam<?> param : p.params.values()) {
                     String type = param.getRequiredTypeString().getString().toLowerCase(Locale.ROOT);
                     maxRank = Math.max(maxRank, this.rankTextToken(type, clippedToken));
                  }

                  if (maxRank == 0) {
                     return 0;
                  }

                  rank += maxRank;
               }
            }
         }
      }

      return rank;
   }

   private int rankTextToken(String haystack, String token) {
      if (token.isEmpty()) {
         return 0;
      } else {
         if (token.startsWith("_")) {
            String clippedToken = token.substring(1);
            if (clippedToken.isEmpty()) {
               return 0;
            }

            if (haystack.endsWith(clippedToken)) {
               if (!Character.isLetterOrDigit(haystack.charAt(haystack.length() - clippedToken.length() - 1))) {
                  return clippedToken.length() * 3 / 2;
               }

               return clippedToken.length();
            }
         } else if (token.endsWith("_")) {
            String clippedTokenx = token.substring(0, token.length() - 1);
            if (clippedTokenx.isEmpty()) {
               return 0;
            }

            if (haystack.startsWith(clippedTokenx)) {
               if (haystack.length() >= clippedTokenx.length() + 1 && !Character.isLetterOrDigit(haystack.charAt(clippedTokenx.length() + 1))) {
                  return clippedTokenx.length() * 2;
               }

               return clippedTokenx.length();
            }
         } else {
            if (token.startsWith("has:")) {
               token = token.substring(4);
            }

            int idx = haystack.indexOf(token);
            if (idx >= 0) {
               int multiplier = 2;
               if (idx == 0 || !Character.isLetterOrDigit(haystack.charAt(idx - 1))) {
                  multiplier += 2;
               }

               if (idx + token.length() + 1 >= haystack.length() || !Character.isLetterOrDigit(haystack.charAt(idx + token.length() + 1))) {
                  multiplier++;
               }

               return token.length() * multiplier / 2;
            }
         }

         return 0;
      }
   }

   public void closePanel() {
      this.panelEnabled = false;
      this.parent.getButtons().forEach(button -> {
         if (button instanceof GuiButtonSpellPiece || button instanceof GuiButtonPage) {
            ((Button)button).visible = false;
            ((Button)button).active = false;
         }
      });
      this.searchField.visible = false;
      this.searchField.setEditable(false);
      this.searchField.setFocused(false);
      this.parent.setFocused(this.parent.statusWidget);
   }

   public void openPanel() {
      this.closePanel();
      this.panelEnabled = true;
      this.page = Math.min(this.page, Math.max(0, this.getPageCount() - 1));
      this.setX(this.parent.gridLeft + (GuiProgrammer.selectedX + 1) * 18);
      this.setY(this.parent.gridTop);
      this.searchField.setX(this.getX() + 18);
      this.searchField.setY(this.getY() + 4);
      this.searchField.setValue("");
      this.searchField.setVisible(true);
      this.searchField.active = true;
      this.updatePanelButtons();
      this.searchField.setEditable(true);
      this.searchField.setFocused(true);
      this.parent.setFocused(this.searchField);
   }

   protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
      this.defaultButtonNarrationText(pNarrationElementOutput);
   }
}
