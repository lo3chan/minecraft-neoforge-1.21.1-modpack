package vazkii.psi.api.spell;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public final class SpellGrid {
   public static final int GRID_SIZE = 9;
   public static final int GRID_CENTER = 4;
   public static final StreamCodec<RegistryFriendlyByteBuf, SpellGrid> STREAM_CODEC = StreamCodec.composite(
      NeoForgeStreamCodecs.lazy(() -> SpellGrid.PieceWithPosition.STREAM_CODEC.apply(ByteBufCodecs.list())),
      SpellGrid::getPiecesAsFlattenedList,
      SpellGrid::fromCodecData
   );
   private static final String TAG_SPELL_LIST = "spellList";
   public static final MapCodec<SpellGrid> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.lazyInitialized(() -> Codec.list(SpellGrid.PieceWithPosition.CODEC.codec()))
               .fieldOf("spellList")
               .forGetter(SpellGrid::getPiecesAsFlattenedList)
         )
         .apply(instance, SpellGrid::fromCodecData)
   );
   private static final String TAG_SPELL_POS_X_LEGACY = "spellPosX";
   private static final String TAG_SPELL_POS_Y_LEGACY = "spellPosY";
   private static final String TAG_SPELL_DATA_LEGACY = "spellData";
   private static final String TAG_SPELL_POS_X = "x";
   private static final String TAG_SPELL_POS_Y = "y";
   private static final String TAG_SPELL_DATA = "data";
   public final Spell spell;
   public SpellPiece[][] gridData;
   private boolean empty;
   private int leftmost;
   private int rightmost;
   private int topmost;
   private int bottommost;

   public SpellGrid(Spell spell) {
      this.spell = spell;
      this.gridData = new SpellPiece[9][9];
   }

   public static boolean exists(int x, int y) {
      return x >= 0 && y >= 0 && x < 9 && y < 9;
   }

   private static SpellGrid fromCodecData(List<SpellGrid.PieceWithPosition> spellList) {
      SpellGrid grid = new SpellGrid(new Spell());

      for (SpellGrid.PieceWithPosition piece : spellList) {
         piece.piece.x = piece.x;
         piece.piece.y = piece.y;
         grid.gridData[piece.x][piece.y] = piece.piece;
      }

      grid.empty = spellList.isEmpty();
      return grid;
   }

   @OnlyIn(Dist.CLIENT)
   public void draw(PoseStack pPoseStack, MultiBufferSource buffers, int light) {
      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            SpellPiece p = this.gridData[i][j];
            if (p != null) {
               pPoseStack.pushPose();
               pPoseStack.translate(i * 18, j * 18, 0.0F);
               p.draw(pPoseStack, buffers, light);
               pPoseStack.popPose();
            }
         }
      }
   }

   private void recalculateBoundaries() {
      this.empty = true;
      this.leftmost = 9;
      this.rightmost = -1;
      this.topmost = 9;
      this.bottommost = -1;

      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            SpellPiece p = this.gridData[i][j];
            if (p != null) {
               this.empty = false;
               if (i < this.leftmost) {
                  this.leftmost = i;
               }

               if (i > this.rightmost) {
                  this.rightmost = i;
               }

               if (j < this.topmost) {
                  this.topmost = j;
               }

               if (j > this.bottommost) {
                  this.bottommost = j;
               }
            }
         }
      }
   }

   public int getSize() {
      this.recalculateBoundaries();
      return this.empty ? 0 : Math.max(this.rightmost - this.leftmost + 1, this.bottommost - this.topmost + 1);
   }

   public void mirrorVertical() {
      this.recalculateBoundaries();
      if (!this.empty) {
         SpellPiece[][] newGrid = new SpellPiece[9][9];

         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
               SpellPiece p = this.gridData[i][j];
               if (p != null) {
                  int newY = 9 - j - 1;
                  newGrid[i][newY] = p;
                  p.y = newY;
                  p.paramSides.replaceAll((k, v) -> p.paramSides.get(k).mirrorVertical());
               }
            }
         }

         this.gridData = newGrid;
      }
   }

   public void rotate(boolean ccw) {
      this.recalculateBoundaries();
      if (!this.empty) {
         int xMod = ccw ? -1 : 1;
         int yMod = ccw ? 1 : -1;
         SpellPiece[][] newGrid = new SpellPiece[9][9];

         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
               SpellPiece p = this.gridData[i][j];
               if (p != null) {
                  int newX = xMod * (j - 4) + 4;
                  int newY = yMod * (i - 4) + 4;
                  newGrid[newX][newY] = p;
                  p.x = newX;
                  p.y = newY;

                  for (SpellParam<?> param : p.paramSides.keySet()) {
                     p.paramSides.compute(param, (k, side) -> ccw ? side.rotateCCW() : side.rotateCW());
                  }
               }
            }
         }

         this.gridData = newGrid;
      }
   }

   public boolean shift(SpellParam.Side side, boolean doit) {
      this.recalculateBoundaries();
      if (this.empty) {
         return false;
      } else if (exists(this.leftmost + side.offx, this.topmost + side.offy) && exists(this.rightmost + side.offx, this.bottommost + side.offy)) {
         if (!doit) {
            return true;
         } else {
            SpellPiece[][] newGrid = new SpellPiece[9][9];

            for (int i = 0; i < 9; i++) {
               for (int j = 0; j < 9; j++) {
                  SpellPiece p = this.gridData[i][j];
                  if (p != null) {
                     int newX = i + side.offx;
                     int newY = j + side.offy;
                     newGrid[newX][newY] = p;
                     p.x = newX;
                     p.y = newY;
                  }
               }
            }

            this.gridData = newGrid;
            return true;
         }
      } else {
         return false;
      }
   }

   private SpellPiece getPieceAtSide(Multimap<SpellPiece, SpellParam.Side> traversed, int x, int y, SpellParam.Side side) throws SpellCompilationException {
      SpellPiece atSide = this.getPieceAtSideSafely(x, y, side);
      if (!traversed.put(atSide, side)) {
         throw new SpellCompilationException("psi.spellerror.loop");
      } else {
         return atSide;
      }
   }

   @Deprecated
   public SpellPiece getPieceAtSideWithRedirections(List<SpellPiece> unused, int x, int y, SpellParam.Side side) throws SpellCompilationException {
      return this.getPieceAtSideWithRedirections(x, y, side);
   }

   public SpellPiece getPieceAtSideWithRedirections(int x, int y, SpellParam.Side side) throws SpellCompilationException {
      return this.getPieceAtSideWithRedirections(x, y, side, piece -> {});
   }

   public SpellPiece getPieceAtSideWithRedirections(int x, int y, SpellParam.Side side, SpellGrid.SpellPieceConsumer walker) throws SpellCompilationException {
      Multimap<SpellPiece, SpellParam.Side> traversed = HashMultimap.create();

      SpellPiece atSide;
      while ((atSide = this.getPieceAtSide(traversed, x, y, side)) instanceof IGenericRedirector) {
         IGenericRedirector redirector = (IGenericRedirector)atSide;
         walker.accept(atSide);
         SpellParam.Side rside = redirector.remapSide(side);
         if (!rside.isEnabled()) {
            return null;
         }

         side = rside;
         x = atSide.x;
         y = atSide.y;
      }

      return atSide;
   }

   public SpellPiece getPieceAtSideSafely(int x, int y, SpellParam.Side side) {
      int xp = x + side.offx;
      int yp = y + side.offy;
      return !exists(xp, yp) ? null : this.gridData[xp][yp];
   }

   public boolean isEmpty() {
      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            SpellPiece piece = this.gridData[i][j];
            if (piece != null) {
               return false;
            }
         }
      }

      return true;
   }

   public void readFromNBT(CompoundTag cmp) {
      this.gridData = new SpellPiece[9][9];
      ListTag list = cmp.getList("spellList", 10);
      int len = list.size();

      for (int i = 0; i < len; i++) {
         CompoundTag lcmp = list.getCompound(i);
         int posX;
         int posY;
         if (lcmp.contains("spellPosX")) {
            posX = lcmp.getInt("spellPosX");
            posY = lcmp.getInt("spellPosY");
         } else {
            posX = lcmp.getInt("x");
            posY = lcmp.getInt("y");
         }

         CompoundTag data;
         if (lcmp.contains("spellData")) {
            data = lcmp.getCompound("spellData");
         } else {
            data = lcmp.getCompound("data");
         }

         SpellPiece piece = SpellPiece.createFromNBT(this.spell, data);
         if (piece != null) {
            this.gridData[posX][posY] = piece;
            piece.isInGrid = true;
            piece.x = posX;
            piece.y = posY;
         }
      }
   }

   private List<SpellGrid.PieceWithPosition> getPiecesAsFlattenedList() {
      List<SpellGrid.PieceWithPosition> pieces = new ArrayList<>();

      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            SpellPiece piece = this.gridData[i][j];
            if (piece != null) {
               pieces.add(new SpellGrid.PieceWithPosition(piece, i, j));
            }
         }
      }

      return pieces;
   }

   public void writeToNBT(CompoundTag cmp) {
      ListTag list = new ListTag();

      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            SpellPiece piece = this.gridData[i][j];
            if (piece != null) {
               CompoundTag lcmp = new CompoundTag();
               lcmp.putInt("x", i);
               lcmp.putInt("y", j);
               CompoundTag data = new CompoundTag();
               piece.writeToNBT(data);
               lcmp.put("data", data);
               list.add(lcmp);
            }
         }
      }

      cmp.put("spellList", list);
   }

   record PieceWithPosition(SpellPiece piece, int x, int y) {
      public static final MapCodec<SpellGrid.PieceWithPosition> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.lazyInitialized(() -> SpellPiece.CODEC).fieldOf("data").forGetter(SpellGrid.PieceWithPosition::piece),
               Codec.INT.fieldOf("x").forGetter(SpellGrid.PieceWithPosition::x),
               Codec.INT.fieldOf("y").forGetter(SpellGrid.PieceWithPosition::y)
            )
            .apply(instance, SpellGrid.PieceWithPosition::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, SpellGrid.PieceWithPosition> STREAM_CODEC = StreamCodec.composite(
         NeoForgeStreamCodecs.lazy(() -> SpellPiece.STREAM_CODEC),
         SpellGrid.PieceWithPosition::piece,
         ByteBufCodecs.VAR_INT,
         SpellGrid.PieceWithPosition::x,
         ByteBufCodecs.VAR_INT,
         SpellGrid.PieceWithPosition::y,
         SpellGrid.PieceWithPosition::new
      );
   }

   @FunctionalInterface
   public interface SpellPieceConsumer {
      void accept(SpellPiece var1) throws SpellCompilationException;
   }
}
