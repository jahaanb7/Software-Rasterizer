package src.main.engine;

public class DepthBuffer{
  public double[][] buffer;
  public int screen_width;
  public int screen_height;

  public DepthBuffer(int screen_width, int screen_height){
    this.screen_width = screen_width;
    this.screen_height = screen_height;

    buffer = new double[screen_height][screen_width];

    init();
  }

  public boolean depthTest(Vector3D v){
    int x = (int)v.x;
    int y = (int)v.y;
    double z = v.z;

    //outside x-bounds and y-bounds
    if(x < 0 || x >= screen_width || y < 0 || y >= screen_height){
      return false;
    }

    //closer to the camera than current depth, if so returns true
    if (z > buffer[y][x]) {
      buffer[y][x] = z;
      return true;
    }

    //returns false because its not outside the camera 
    //nor less than previous depth meaning its behind
    return false;
  }

  public double getDepth(int x, int y) {
    if (x < 0 || x >= screen_width || y < 0 || y >= screen_height) {
      return Double.NEGATIVE_INFINITY;
    }
    return buffer[y][x];
  }

  public void init(){
    for(int pixel_row = 0; pixel_row < screen_height; pixel_row++){
      for(int pixel_col = 0; pixel_col < screen_width; pixel_col++){
        buffer[pixel_row][pixel_col] = Double.NEGATIVE_INFINITY;      
      }
    }
  }
}