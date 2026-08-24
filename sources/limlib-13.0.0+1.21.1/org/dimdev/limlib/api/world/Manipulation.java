package org.dimdev.limlib.api.world;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public enum Manipulation implements StringRepresentable {
   NONE("none", Rotation.NONE, Mirror.NONE),
   CLOCKWISE_90("clockwise_90", Rotation.CLOCKWISE_90, Mirror.NONE),
   CLOCKWISE_180("180", Rotation.CLOCKWISE_180, Mirror.NONE),
   COUNTERCLOCKWISE_90("counterclockwise_90", Rotation.COUNTERCLOCKWISE_90, Mirror.NONE),
   FRONT_BACK("front_back", Rotation.NONE, Mirror.FRONT_BACK),
   LEFT_RIGHT("left_right", Rotation.NONE, Mirror.LEFT_RIGHT),
   TOP_LEFT_BOTTOM_RIGHT("top_left_bottom_right", Rotation.COUNTERCLOCKWISE_90, Mirror.LEFT_RIGHT),
   TOP_RIGHT_BOTTOM_LEFT("top_right_bottom_left", Rotation.CLOCKWISE_90, Mirror.LEFT_RIGHT);

   public static final Codec<Manipulation> CODEC = StringRepresentable.fromEnum(Manipulation::values);
   final String id;
   final Rotation rotation;
   final Mirror mirror;

   private Manipulation(String id, Rotation rotation, Mirror mirror) {
      this.id = id;
      this.rotation = rotation;
      this.mirror = mirror;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public String getSerializedName() {
      return this.id;
   }

   public static Manipulation random(RandomSource random) {
      return values()[random.nextInt(8)];
   }

   public static Manipulation of(Rotation rotation) {
      return of(rotation, Mirror.NONE);
   }

   public static Manipulation of(Mirror mirror) {
      return of(Rotation.NONE, mirror);
   }

   public static Manipulation of(Rotation rotation, Mirror mirror) {
      return switch (rotation) {
         case NONE -> {
            switch (mirror) {
               case NONE:
                  yield NONE;
               case FRONT_BACK:
                  yield FRONT_BACK;
               case LEFT_RIGHT:
                  yield LEFT_RIGHT;
               default:
                  throw new MatchException(null, null);
            }
         }
         case CLOCKWISE_180 -> {
            switch (mirror) {
               case NONE:
                  yield CLOCKWISE_180;
               case FRONT_BACK:
                  yield LEFT_RIGHT;
               case LEFT_RIGHT:
                  yield FRONT_BACK;
               default:
                  throw new MatchException(null, null);
            }
         }
         case CLOCKWISE_90 -> {
            switch (mirror) {
               case NONE:
                  yield CLOCKWISE_90;
               case FRONT_BACK:
                  yield TOP_LEFT_BOTTOM_RIGHT;
               case LEFT_RIGHT:
                  yield TOP_RIGHT_BOTTOM_LEFT;
               default:
                  throw new MatchException(null, null);
            }
         }
         case COUNTERCLOCKWISE_90 -> {
            switch (mirror) {
               case NONE:
                  yield COUNTERCLOCKWISE_90;
               case FRONT_BACK:
                  yield TOP_RIGHT_BOTTOM_LEFT;
               case LEFT_RIGHT:
                  yield TOP_LEFT_BOTTOM_RIGHT;
               default:
                  throw new MatchException(null, null);
            }
         }
         default -> throw new MatchException(null, null);
      };
   }

   public Manipulation rotate(Rotation rotation) {
      return switch (rotation) {
         case NONE -> this;
         case CLOCKWISE_180 -> {
            switch (this) {
               case NONE:
                  yield CLOCKWISE_180;
               case CLOCKWISE_90:
                  yield COUNTERCLOCKWISE_90;
               case CLOCKWISE_180:
                  yield NONE;
               case COUNTERCLOCKWISE_90:
                  yield CLOCKWISE_90;
               case FRONT_BACK:
                  yield LEFT_RIGHT;
               case LEFT_RIGHT:
                  yield FRONT_BACK;
               case TOP_LEFT_BOTTOM_RIGHT:
                  yield TOP_RIGHT_BOTTOM_LEFT;
               case TOP_RIGHT_BOTTOM_LEFT:
                  yield TOP_LEFT_BOTTOM_RIGHT;
               default:
                  throw new MatchException(null, null);
            }
         }
         case CLOCKWISE_90 -> {
            switch (this) {
               case NONE:
                  yield CLOCKWISE_90;
               case CLOCKWISE_90:
                  yield CLOCKWISE_180;
               case CLOCKWISE_180:
                  yield COUNTERCLOCKWISE_90;
               case COUNTERCLOCKWISE_90:
                  yield NONE;
               case FRONT_BACK:
                  yield TOP_RIGHT_BOTTOM_LEFT;
               case LEFT_RIGHT:
                  yield TOP_LEFT_BOTTOM_RIGHT;
               case TOP_LEFT_BOTTOM_RIGHT:
                  yield FRONT_BACK;
               case TOP_RIGHT_BOTTOM_LEFT:
                  yield LEFT_RIGHT;
               default:
                  throw new MatchException(null, null);
            }
         }
         case COUNTERCLOCKWISE_90 -> {
            switch (this) {
               case NONE:
                  yield COUNTERCLOCKWISE_90;
               case CLOCKWISE_90:
                  yield NONE;
               case CLOCKWISE_180:
                  yield CLOCKWISE_90;
               case COUNTERCLOCKWISE_90:
                  yield CLOCKWISE_180;
               case FRONT_BACK:
                  yield TOP_LEFT_BOTTOM_RIGHT;
               case LEFT_RIGHT:
                  yield TOP_RIGHT_BOTTOM_LEFT;
               case TOP_LEFT_BOTTOM_RIGHT:
                  yield LEFT_RIGHT;
               case TOP_RIGHT_BOTTOM_LEFT:
                  yield FRONT_BACK;
               default:
                  throw new MatchException(null, null);
            }
         }
         default -> throw new MatchException(null, null);
      };
   }

   public Manipulation mirror(Mirror mirror) {
      return switch (mirror) {
         case NONE -> this;
         case FRONT_BACK -> {
            switch (this) {
               case NONE:
                  yield FRONT_BACK;
               case CLOCKWISE_90:
                  yield TOP_LEFT_BOTTOM_RIGHT;
               case CLOCKWISE_180:
                  yield LEFT_RIGHT;
               case COUNTERCLOCKWISE_90:
                  yield TOP_RIGHT_BOTTOM_LEFT;
               case FRONT_BACK:
                  yield NONE;
               case LEFT_RIGHT:
                  yield CLOCKWISE_180;
               case TOP_LEFT_BOTTOM_RIGHT:
                  yield CLOCKWISE_90;
               case TOP_RIGHT_BOTTOM_LEFT:
                  yield COUNTERCLOCKWISE_90;
               default:
                  throw new MatchException(null, null);
            }
         }
         case LEFT_RIGHT -> {
            switch (this) {
               case NONE:
                  yield LEFT_RIGHT;
               case CLOCKWISE_90:
                  yield TOP_RIGHT_BOTTOM_LEFT;
               case CLOCKWISE_180:
                  yield FRONT_BACK;
               case COUNTERCLOCKWISE_90:
                  yield TOP_LEFT_BOTTOM_RIGHT;
               case FRONT_BACK:
                  yield CLOCKWISE_180;
               case LEFT_RIGHT:
                  yield NONE;
               case TOP_LEFT_BOTTOM_RIGHT:
                  yield COUNTERCLOCKWISE_90;
               case TOP_RIGHT_BOTTOM_LEFT:
                  yield CLOCKWISE_90;
               default:
                  throw new MatchException(null, null);
            }
         }
         default -> throw new MatchException(null, null);
      };
   }

   public Manipulation manipulate(Manipulation manipulation) {
      return this.rotate(manipulation.rotation).mirror(manipulation.mirror);
   }

   public static Manipulation[] rotations() {
      return new Manipulation[]{NONE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90};
   }

   public static Manipulation[] mirrors() {
      return new Manipulation[]{NONE, FRONT_BACK, LEFT_RIGHT, TOP_LEFT_BOTTOM_RIGHT, TOP_RIGHT_BOTTOM_LEFT};
   }
}
