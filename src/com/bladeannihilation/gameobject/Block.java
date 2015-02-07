public class Block {
   private int x = 0;
   private int y = 0;
   private Tile type = Tile.UNKNOWN;
   public Block(int x, int y, Tile type) {
      this.x = x;
      this.y = y;
      this.type = type;
   }
   public int getX() {
      return x;
   }
   public int getY() {
      return y;
   }
   public int getType() {
      return type;
   }
}