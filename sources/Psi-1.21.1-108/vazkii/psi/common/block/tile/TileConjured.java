package vazkii.psi.common.block.tile;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.BlockConjured;
import vazkii.psi.common.block.base.ModBlocks;

public class TileConjured extends BlockEntity {
   private static final String TAG_COLORIZER = "colorizer";
   public ItemStack colorizer = ItemStack.EMPTY;

   public TileConjured(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlocks.conjuredType.get(), pos, state);
   }

   public void doParticles() {
      int color = Psi.proxy.getColorForColorizer(this.colorizer);
      float r = PsiRenderHelper.r(color) / 255.0F;
      float g = PsiRenderHelper.g(color) / 255.0F;
      float b = PsiRenderHelper.b(color) / 255.0F;
      if (this.getLevel() != null) {
         BlockState state = this.getLevel().getBlockState(this.getBlockPos());
         if (state.getBlock() == ModBlocks.conjured.get() && (Boolean)state.getValue(BlockConjured.SOLID)) {
            boolean[] edges = new boolean[12];
            Arrays.fill(edges, true);
            if ((Boolean)state.getValue(BlockConjured.BLOCK_DOWN)) {
               this.removeEdges(edges, 0, 1, 2, 3);
            }

            if ((Boolean)state.getValue(BlockConjured.BLOCK_UP)) {
               this.removeEdges(edges, 4, 5, 6, 7);
            }

            if ((Boolean)state.getValue(BlockConjured.BLOCK_NORTH)) {
               this.removeEdges(edges, 3, 7, 8, 11);
            }

            if ((Boolean)state.getValue(BlockConjured.BLOCK_SOUTH)) {
               this.removeEdges(edges, 1, 5, 9, 10);
            }

            if ((Boolean)state.getValue(BlockConjured.BLOCK_EAST)) {
               this.removeEdges(edges, 2, 6, 10, 11);
            }

            if ((Boolean)state.getValue(BlockConjured.BLOCK_WEST)) {
               this.removeEdges(edges, 0, 4, 8, 9);
            }

            double x = this.getBlockPos().getX();
            double y = this.getBlockPos().getY();
            double z = this.getBlockPos().getZ();
            this.makeParticle(edges[0], r, g, b, x + 0.0, y + 0.0, z + 0.0, 0.0, 0.0, 1.0);
            this.makeParticle(edges[1], r, g, b, x + 0.0, y + 0.0, z + 1.0, 1.0, 0.0, 0.0);
            this.makeParticle(edges[2], r, g, b, x + 1.0, y + 0.0, z + 0.0, 0.0, 0.0, 1.0);
            this.makeParticle(edges[3], r, g, b, x + 0.0, y + 0.0, z + 0.0, 1.0, 0.0, 0.0);
            this.makeParticle(edges[4], r, g, b, x + 0.0, y + 1.0, z + 0.0, 0.0, 0.0, 1.0);
            this.makeParticle(edges[5], r, g, b, x + 0.0, y + 1.0, z + 1.0, 1.0, 0.0, 0.0);
            this.makeParticle(edges[6], r, g, b, x + 1.0, y + 1.0, z + 0.0, 0.0, 0.0, 1.0);
            this.makeParticle(edges[7], r, g, b, x + 0.0, y + 1.0, z + 0.0, 1.0, 0.0, 0.0);
            this.makeParticle(edges[8], r, g, b, x + 0.0, y + 0.0, z + 0.0, 0.0, 1.0, 0.0);
            this.makeParticle(edges[9], r, g, b, x + 0.0, y + 0.0, z + 1.0, 0.0, 1.0, 0.0);
            this.makeParticle(edges[10], r, g, b, x + 1.0, y + 0.0, z + 1.0, 0.0, 1.0, 0.0);
            this.makeParticle(edges[11], r, g, b, x + 1.0, y + 0.0, z + 0.0, 0.0, 1.0, 0.0);
         } else if (Math.random() < 0.5) {
            float w = 0.15F;
            float h = 0.05F;
            double x = this.getBlockPos().getX() + 0.5 + (Math.random() - 0.5) * w;
            double y = this.getBlockPos().getY() + 0.25 + (Math.random() - 0.5) * h;
            double z = this.getBlockPos().getZ() + 0.5 + (Math.random() - 0.5) * w;
            float s = 0.2F + (float)Math.random() * 0.1F;
            float m = 0.01F + (float)Math.random() * 0.015F;
            Psi.proxy.wispFX(x, y, z, r, g, b, s, -m);
         }
      }
   }

   public void makeParticle(boolean doit, float r, float g, float b, double xp, double yp, double zp, double xv, double yv, double zv) {
      if (doit) {
         float m = 0.1F;
         xv *= m;
         yv *= m;
         zv *= m;
         Psi.proxy.sparkleFX(xp, yp, zp, r, g, b, (float)xv, (float)yv, (float)zv, 2.75F, 15);
      }
   }

   public void removeEdges(boolean[] edges, int... posArray) {
      for (int i : posArray) {
         edges[i] = false;
      }
   }

   public void saveAdditional(@NotNull CompoundTag cmp, @NotNull Provider pRegistries) {
      super.saveAdditional(cmp, pRegistries);
      if (!this.colorizer.isEmpty()) {
         cmp.put("colorizer", this.colorizer.save(pRegistries, new CompoundTag()));
      }
   }

   public void loadAdditional(@NotNull CompoundTag cmp, @NotNull Provider pRegistries) {
      super.loadAdditional(cmp, pRegistries);
      this.readPacketNBT(cmp, pRegistries);
   }

   public void readPacketNBT(CompoundTag cmp, Provider pRegistries) {
      if (cmp.contains("colorizer")) {
         this.colorizer = ItemStack.parseOptional(pRegistries, cmp.getCompound("colorizer"));
      } else {
         this.colorizer = ItemStack.EMPTY;
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider pRegistries) {
      CompoundTag cmp = new CompoundTag();
      this.saveAdditional(cmp, pRegistries);
      return cmp;
   }

   public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, @NotNull Provider pRegistries) {
      this.readPacketNBT(pkt.getTag(), pRegistries);
   }
}
