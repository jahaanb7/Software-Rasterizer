package src.main;

import java.awt.Color;
import java.awt.image.BufferedImage;

class LineDrawer{

  public void put_pixel(BufferedImage screen, int x1, int y1, Color color){
    screen.setRGB(x1,y1,color.getRGB());
  }

  public void draw_line_h(BufferedImage screen, int x1, int y1, int x2, int y2) {
    if (x1 > x2) {
        int tx = x1;
        int ty = y1;

        x1 = x2; 
        y1 = y2;

        x2 = tx; 
        y2 = ty;
    }

    int dx = x2 - x1;
    int dy = y2 - y1;

    int dir;
    if(dy < 0){
      dir = -1;
    }
    else dir = 1;

    dy *= dir;

    if(dx != 0){
      int y = y1;
      int p = 2*dy - dx;

      for(int x = x1; x < x2+1; x++){
        put_pixel(screen, x, y,  Color.WHITE);
        if(p >= 0){
          y += dir;
          p = p + 2*dy - 2*dx;
        }
        else{p = p + 2*dy;}
      }
    }
  }

  public void draw_line_v(BufferedImage screen, int x1, int y1, int x2, int y2) {
    if (y1 > y2) {
        int tx = x1; 
        int ty = y1;

        x1 = x2; 
        y1 = y2;
        
        x2 = tx;
        y2 = ty;
    }

    int dx = x2 - x1;
    int dy = y2 - y1;

    int dir = 0;
    if(dx < 0){
      dir = -1;
    }
    else dir = 1;

    dx *= dir;

    if(dy != 0){
      int x = x1;
      int p = 2*dx - dy;

      for(int y = y1; y < y2+1; y++){
        put_pixel(screen, x, y,  Color.WHITE);
        if(p >= 0){
          x += dir;
          p = p + 2*dx - 2*dy;
        }
        else{p = p + 2*dx;}
      }
    }
  }

  public void drawline(BufferedImage screen, int x0, int y0, int x1, int y1){
    
    if(Math.abs(x1 - x0) > Math.abs(y1 - y0)){
      draw_line_h(screen, x0, y0, x1, y1);
    }
    else{
      draw_line_v(screen, x0, y0, x1, y1);
    }
  }

  public void connect(BufferedImage screen, Matrix a){    
    for (int i = 0; i <  a.rows; i++) {
      int next = (i + 1)% a.rows;

      int x1 = (int) a.data[i][0];
      int y1 = (int) a.data[i][1];
      int x2 = (int) a.data[next][0];
      int y2 = (int) a.data[next][1];
      drawline(screen, x1, y1, x2, y2);
    }
  }

  public double edge_function(Vector3D a, Vector3D b, Vector3D c){
    return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
  }

  public void draw_triangle(Vector3D v1, Vector3D v2, Vector3D v3, BufferedImage screen, Color c1, Color c2, Color c3) {
  
  //bounding box
  int minX = (int)Math.floor(Math.min(v1.x, Math.min(v2.x, v3.x)));
  int maxX = (int)Math.ceil(Math.max(v1.x, Math.max(v2.x, v3.x)));
  int minY = (int)Math.floor(Math.min(v1.y, Math.min(v2.y, v3.y)));
  int maxY = (int)Math.ceil(Math.max(v1.y, Math.max(v2.y, v3.y)));

  double area = edge_function(v1, v2, v3);

  //go over every pixel in the bounding box
  for (int y = minY; y <= maxY; y++) {
    for (int x = minX; x <= maxX; x++) {

        //set an intital pixel to detect
        Vector3D p = new Vector3D(x + 0.5, y + 0.5, 0);

        //barycentric weights
        double w1 = (edge_function(v2, v3, p))/area;
        double w2 = (edge_function(v3, v1, p))/area;
        double w3 = (edge_function(v1, v2, p))/area;

        //point inside the triangle
        if (w1 >= 0 && w2 >= 0 && w3 >= 0) {
            Color col = interpolateColor(p, v1, v2, v3, c1, c2, c3);
            
          if (x >= 0 && x < screen.getWidth() && y >= 0 && y < screen.getHeight()) { //prevents the entire screen from becoming a 'triangle'
              screen.setRGB(x, y, col.getRGB());
          }
        }
      }
    }
  }

  private Color interpolateColor(Vector3D p, Vector3D v1, Vector3D v2, Vector3D v3, Color c1, Color c2, Color c3) {
    double area = edge_function(v1, v2, v3);

    double w1 = edge_function(v2, v3, p) / area;
    double w2 = edge_function(v3, v1, p) / area;
    double w3 = edge_function(v1, v2, p) / area;

    w1 = Math.max(0, Math.min(1, w1));
    w2 = Math.max(0, Math.min(1, w2));
    w3 = Math.max(0, Math.min(1, w3));

    // Normalize just in case
    double sum = w1 + w2 + w3;
    w1 /= sum;
    w2 /= sum;
    w3 /= sum;

    int r = (int)Math.round(c1.getRed() * w1 + c2.getRed() * w2 + c3.getRed() * w3);
    int g = (int)Math.round(c1.getGreen() * w1 + c2.getGreen() * w2 + c3.getGreen() * w3);
    int b = (int)Math.round(c1.getBlue() * w1 + c2.getBlue() * w2 + c3.getBlue() * w3);

    return new Color(Math.min(255,r), Math.min(255,g), Math.min(255,b));
  }
}