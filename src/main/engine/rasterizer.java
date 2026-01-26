package src.main.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
  3D Graphics Engine - Software Rasterizer
  Includes: Camera Movement, Model Rotations, Perspective Projection, Backface-culling, and shading
*/

public class rasterizer extends  JPanel implements Runnable{

  LineDrawer drawer = new LineDrawer();

  //screen dimensions
  public static final int SCREEN_HEIGHT = 800;
  public static final int SCREEN_WIDTH = 800;

  //Camera movement
  private boolean move_left = false;
  private boolean move_right = false;
  private boolean move_up = false;
  private boolean move_down = false;
  private boolean move_forward = false;
  private boolean move_backward = false;

  //camera movement
  public double cameraX = 0; 
  public double cameraY = 0;
  public double cameraZ = 0;

  //Camera Rotation
  private int last_mouse_x = 0;
  private int last_mouse_y = 0;
  private boolean is_dragging = false;

  //Rotation Model
  private double rotationX = 0;
  private double rotationY = 0;
  private double rotationZ = 0;

    //model adjustment
  private double zOffset = 5;
  private double scale = 1.0;

  //wireframe for model debugging and testing
  private boolean wireframe_mode = false;

  //projection matrix
  private final int fov = 90;
  private final double aspect = SCREEN_WIDTH/(double)SCREEN_HEIGHT;
  private final double near = 1;
  private final int far = 550;

  Matrix project = Matrix.project(fov, aspect, near, far);
    
  //rendering variables
  private boolean is_running = false;
  private final double rotation_speed = 1;
  private final int fps = 60;
  private final long frame_time = 1_000_000_000L/fps;
  Thread gameThread;

  //adjusting for movement and rotation speed
  private double cam_speed = 0.10;

  // 3D models
  Mesh monkey = new Mesh(); //blender monkey  model
  Mesh homer = new Mesh(); // Homer Simpson model
  Mesh rabbit = new Mesh(); // Rabbit model
  Mesh sphere = new Mesh(); // Ico-Sphere model
  Mesh maxPlanck = new Mesh(); // Max Planck Head model

  public static void main(String[] args) {
    JFrame frame = new JFrame();

    frame.add(new rasterizer());
    frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setTitle("3D Model Graphics");
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  public rasterizer(){

    monkey.tris.addAll(OBJLoader.loadOBJ(getClass().getResourceAsStream("/resources/monkey.obj")));
    homer.tris.addAll(OBJLoader.loadOBJ(getClass().getResourceAsStream("/resources/homer.obj")));
    rabbit.tris.addAll(OBJLoader.loadOBJ(getClass().getResourceAsStream("/resources/rabbit.obj")));
    sphere.tris.addAll(OBJLoader.loadOBJ(getClass().getResourceAsStream("/resources/sphere.obj")));
    maxPlanck.tris.addAll(OBJLoader.loadOBJ(getClass().getResourceAsStream("/resources/MaxPlanck.obj")));

    setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    setBackground(Color.WHITE);
    setFocusable(true);
    requestFocusInWindow();
    setOpaque(true);
    setDoubleBuffered(true);
    start();

    /*
    Keyboard Inputs for Camera Movement: 

    (WASD for y-axis and x-axis movement)
    (QE for zooming in and out)
    */
   
    addKeyListener(new KeyAdapter(){
      @Override
      public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()){
          case KeyEvent.VK_W -> {move_up = true;}
          case KeyEvent.VK_S -> {move_down = true;}
          case KeyEvent.VK_D -> {move_right = true;}
          case KeyEvent.VK_A -> {move_left = true;}
          case KeyEvent.VK_Q -> {move_forward = true;}
          case KeyEvent.VK_E -> {move_backward = true;}
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()){
          case KeyEvent.VK_W -> {move_up = false;}
          case KeyEvent.VK_S -> {move_down = false;}
          case KeyEvent.VK_D -> {move_right = false;}
          case KeyEvent.VK_A -> {move_left = false;}
          case KeyEvent.VK_Q -> {move_forward = false;}
          case KeyEvent.VK_E -> {move_backward = false;}
        }
      }
    });

    /*
    Mouse Inputs for Rotation and Zoom in and out
    
    Scroll Up/Down - Zoom in & Zoom out
    Drag and Move --> rotation
    */

    addMouseListener(new MouseAdapter(){
      @Override
      public void mousePressed(MouseEvent mouse){
        last_mouse_x = mouse.getX();
        last_mouse_y = mouse.getY();
        is_dragging = true;
      }

      @Override
      public void mouseReleased(MouseEvent mouse){
        is_dragging = false;
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent mouse){
        int delta_mouse_x = mouse.getX() - last_mouse_x;
        int delta_mouse_y = mouse.getY() - last_mouse_y;

        rotationX += delta_mouse_y * 0.4;
        rotationY += delta_mouse_x * 0.4;

        last_mouse_x = mouse.getX();
        last_mouse_y = mouse.getY();

        repaint();
      }
    });

    addMouseWheelListener(new MouseWheelListener(){
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        double scroll = e.getPreciseWheelRotation();

        if (scroll > 0) {cameraZ += cam_speed;}
        if(scroll < 0) {cameraZ -= cam_speed;}

        // prevent model to go behind camera (clipping is not added yet)
        if(cameraZ > 3) {cameraZ = 3;}
      }      
    });
  }


  private void start() {
    is_running = true;
    gameThread = new Thread(this, "GameThread");
    gameThread.start();
  }

  public void updateCam(){
    if(move_left)  {cameraX -= cam_speed;}
    if(move_right) {cameraX += cam_speed;}
    if(move_up)    {cameraY += cam_speed;}
    if(move_down)  {cameraY -= cam_speed;}

    if(move_backward){cameraZ -= cam_speed;}
    if(move_forward){cameraZ += cam_speed;}
  }

  public void render(Mesh mesh, Matrix rotation, BufferedImage screen){
    for(Triangle tri : mesh.tris) {

      Vector4D r1 = tri.v1.mul(rotation);
      Vector4D r2 = tri.v2.mul(rotation);
      Vector4D r3 = tri.v3.mul(rotation);
    
      //Translation and offset into the screen, to avoid drawing behind the camera
      r1.scalar_mul(scale);
      r2.scalar_mul(scale);
      r3.scalar_mul(scale);
    
      r1.z += zOffset;
      r2.z += zOffset;
      r3.z += zOffset;
    
      Vector3D a = new Vector3D((r2.x - r1.x), (r2.y - r1.y), (r2.z - r1.z)); // Edge A for this triangle
      Vector3D b = new Vector3D((r3.x - r1.x), (r3.y - r1.y), (r3.z - r1.z)); // Edge B for this triangle

      //creates a cross product (surface normal) from the two edges that is perpendicular
      Vector3D normal = (Vector3D.cross(a, b)).normalize(); 
    
      Vector3D center = new Vector3D(
        (r1.x + r2.x + r3.x)/3.0,
        (r1.y + r2.y + r3.y)/3.0,
        (r1.z + r2.z + r3.z)/3.0
      );
     
      //represents the postion of camera
      Vector3D view = new Vector3D(cameraX - center.x, cameraY - center.y, cameraZ - center.z);
      double facing_cam = Vector3D.dot(normal, view);

      if(facing_cam > 0){
      
        Vector3D light_dir = new Vector3D(0,0 ,-1.0).normalize(); // vector that points from camera
      
        // calculates the dot product between the each surface normal and light direction from camera
        double shading = Vector3D.dot(normal, light_dir) * 0.9;

        //moves the camera in each axis for each vertice of triangle
        r1.x -= cameraX;      r1.y -= cameraY;      r1.z -= cameraZ;
        r2.x -= cameraX;      r2.y -= cameraY;      r2.z -= cameraZ; 
        r3.x -= cameraX;      r3.y -= cameraY;      r3.z -= cameraZ; 

        if (r1.z <= near || r2.z <= near || r3.z <= near) {
          continue;
        }

        if (r1.z >= (far) || r2.z >= (far) || r3.z >= (far)){
          continue;
        }

        //multiply by projection matrix to project onto screen (3D --> 2D)
        Vector4D p1 = r1.mul(project);
        Vector4D p2 = r2.mul(project);
        Vector4D p3 = r3.mul(project);
      
        //perspective divide
        if (p1.w != 0) {p1.x /= p1.w; p1.y /= p1.w; p1.z /= p1.w;}
        if (p2.w != 0) {p2.x /= p2.w; p2.y /= p2.w; p2.z /= p2.w;}
        if (p3.w != 0) {p3.x /= p3.w; p3.y /= p3.w; p3.z /= p3.w;}
      
        /* 
        Convert from NDC (Normalized Device Coordinates) to screen space
        0.5 to get it to the center of the screen, and + 1 to get it in infront of camera.
        */

        int sx1 = (int)((p1.x + 1) * 0.5 * SCREEN_WIDTH); 
        int sy1 = (int)((1 - (p1.y + 1) * 0.5) * SCREEN_HEIGHT);

        int sx2 = (int)((p2.x + 1) * 0.5 * SCREEN_WIDTH);
        int sy2 = (int)((1 - (p2.y + 1) * 0.5) * SCREEN_HEIGHT);

        int sx3 = (int)((p3.x + 1) * 0.5 * SCREEN_WIDTH);
        int sy3 = (int)((1 - (p3.y + 1) * 0.5) * SCREEN_HEIGHT);
      
        Vector3D A = new Vector3D(sx1, sy1, p1.z); 
        Vector3D B = new Vector3D(sx2, sy2, p2.z); 
        Vector3D C = new Vector3D(sx3, sy3, p3.z); 
      
        Color baseColor = new Color(171,171,171);
      
        int r_color = (int)(baseColor.getRed() * shading);
        int g_color = (int)(baseColor.getGreen() * shading);
        int b_color = (int)(baseColor.getBlue() * shading);
      
        r_color = Math.min(255, Math.max(0, r_color));
        g_color = Math.min(255, Math.max(0, g_color));
        b_color = Math.min(255, Math.max(0, b_color));
      
        Color shadedColor = new Color(r_color, g_color, b_color);
      
        if(wireframe_mode){
          drawer.drawline(screen, sx1, sy1, sx2, sy2);
          drawer.drawline(screen, sx2, sy2, sx3, sy3);     // This is for wireframe and for debugging
          drawer.drawline(screen, sx3, sy3, sx1, sy1);
        }
        else{
          drawer.draw_triangle(A, B, C, screen, shadedColor, shadedColor, shadedColor);
        }
      }
    }
  }

  @Override
    public void run() {
    long lastTime;
    while (is_running) {
      lastTime = System.nanoTime();

      updateCam();
      repaint();

      //Animation and Frame timing
      long elapsed = System.nanoTime() - lastTime;
      long sleepTime = frame_time - elapsed;

      if (sleepTime > 0) {
        try {
            Thread.sleep(sleepTime / 1_000_000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      }
    }
  }

  @Override
  protected void paintComponent(Graphics g){
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setColor(Color.WHITE);

    BufferedImage screen = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
    Matrix rotation = Matrix.combined_rotation(rotationX, rotationY, rotationZ);

    render(sphere, rotation, screen);

    g.drawImage(screen, 0, 0, null);
  }
}