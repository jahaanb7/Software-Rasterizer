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
  camera cam = new camera();
  Quaternion q;

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

  //Camera Rotation
  private int last_mouse_x = 0;
  private int last_mouse_y = 0;
  private boolean is_dragging = false;
  private double rot_v_x = 0;
  private double rot_v_y = 0;
  private double rot_v_z = 0;
  

  //Rotation Model
  private double rotationX = 0;
  private double rotationY = 0;
  private double rotationZ = 0;

  //model adjustment
  private double zOffset = 2;
  private double scale = 1;
  private double cam_speed = 0.10;
  private double mouse_sensitivity = 0.2;

  //wireframe for model debugging and testing
  private boolean wireframe_mode = false;
    private boolean texture_mode = true;

  //projection matrix
  private final int fov = 90;
  private final double aspect = SCREEN_WIDTH/(double)SCREEN_HEIGHT;
  private final double near = 1;
  private final int far = 1000;

  Matrix project = Matrix.project(fov, aspect, near, far);
    
  //rendering variables
  private boolean is_running = false;
  private final double rotation_speed = 1;
  private final int fps = 60;
  private final long frame_time = 1_000_000_000L/fps;
  Thread gameThread;

  // 3D models
  Mesh monkey = new Mesh(); //blender monkey  model
  Mesh cube = new Mesh(); // Homer Simpson model
  Mesh rabbit = new Mesh(); // Rabbit model
  Mesh sphere = new Mesh(); // Ico-Sphere model
  Mesh maxPlanck = new Mesh(); // Max Planck Head model
  Mesh face = new Mesh();

  private DepthBuffer buffer = new DepthBuffer(SCREEN_WIDTH, SCREEN_HEIGHT);

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
    cube.tris.addAll(OBJLoader.loadOBJ(getClass().getResourceAsStream("/resources/homer.obj")));
    rabbit.tris.addAll(OBJLoader.loadOBJ(getClass().getResourceAsStream("/resources/rabbit.obj")));
    sphere.tris.addAll(OBJLoader.loadOBJ(getClass().getResourceAsStream("/resources/sphere.obj")));
    maxPlanck.tris.addAll(OBJLoader.loadOBJ(getClass().getResourceAsStream("/resources/MaxPlanck.obj")));
    face.tris.addAll(OBJLoader.loadOBJ(getClass().getResourceAsStream("/resources/face.obj")));


    try {
      BufferedImage texture = javax.imageio.ImageIO.read(
            getClass().getResourceAsStream("/resources/face_tex.png")
        );
        face.set_texture(texture);
        System.out.println("________________Texture loaded!__________________");
    } 
    catch (Exception e) {
        System.out.println("___________________Failed: _________________________" + e.getMessage());
    }

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
          case KeyEvent.VK_SPACE -> cam.resetOrientation();
          case KeyEvent.VK_R -> {
            if(!wireframe_mode){
              wireframe_mode = true;
            }
            else if(wireframe_mode){
              wireframe_mode = false;
            }
          }
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
        public void mouseDragged(MouseEvent mouse) {
        if (!is_dragging) return;

        int delta_x = mouse.getX() - last_mouse_x;
        int delta_y = mouse.getY() - last_mouse_y;

        if ((mouse.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {

          double yaw = delta_x * mouse_sensitivity;   
          double pitch = delta_y * mouse_sensitivity; 
          
          cam.rotate(pitch, yaw);
        }

        if ((mouse.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
          rotationX += delta_y * 0.4;
          rotationY += delta_x * 0.4;
        }

        last_mouse_x = mouse.getX();
        last_mouse_y = mouse.getY();
        repaint();
      }
    });

    addMouseWheelListener(new MouseWheelListener(){
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        double scroll = e.getPreciseWheelRotation();

        if (scroll > 0) {cam.move_k_hat(-cam_speed);}
        if(scroll < 0) {cam.move_k_hat(cam_speed);}

        // prevent model to go behind camera (clipping is not added yet)
        if(cam.cam_position.z > 3) {cam.cam_position.z = 3;}
      }      
    });
  }

  public void update_movement(){

    if(move_left)  {cam.move_i_hat(cam_speed);}
    if(move_right) {cam.move_i_hat(-cam_speed);}
    if(move_up)    {cam.move_j_hat(cam_speed);}
    if(move_down)  {cam.move_j_hat(-cam_speed);}

    if (move_forward)  {cam.move_k_hat(cam_speed);}
    if (move_backward) {cam.move_k_hat(-cam_speed);}
  }


  private void start() {
    is_running = true;
    gameThread = new Thread(this, "GameThread");
    gameThread.start();
  }

  public void render(Mesh mesh, Matrix matrix, DepthBuffer buffer, BufferedImage screen){

    for(Triangle tri : mesh.tris) {

      double cameraX = cam.cam_position.x;
      double cameraY = cam.cam_position.y;
      double cameraZ = cam.cam_position.z;

      Vector4D r1 = tri.v1.mul(matrix);
      Vector4D r2 = tri.v2.mul(matrix);
      Vector4D r3 = tri.v3.mul(matrix);
    
      //Translation and offset into the screen, to avoid drawing behind the camera
      r1.scalar_mul(scale);
      r2.scalar_mul(scale);
      r3.scalar_mul(scale);
    
      r1.z += zOffset;
      r2.z += zOffset;
      r3.z += zOffset;

      //moves the camera in each axis for each vertice of triangle
      r1 = cam.worldToCamera4(r1);
      r2 = cam.worldToCamera4(r2);
      r3 = cam.worldToCamera4(r3);
    
      Vector3D a = new Vector3D((r2.x - r1.x), (r2.y - r1.y), (r2.z - r1.z)); // Edge A for this triangle
      Vector3D b = new Vector3D((r3.x - r1.x), (r3.y - r1.y), (r3.z - r1.z)); // Edge B for this triangle

      //creates a cross product (surface normal) from the two edges that is perpendicular
      Vector3D normal = (Vector3D.cross(a, b)).normalize(); 
    
      Vector3D center = Vector3D.center(r1, r2, r3);
     
      //represents the postion of camera
      Vector3D view = new Vector3D(cameraX - center.x, cameraY - center.y, cameraZ - center.z);
      double facing_cam = Vector3D.dot(normal, view);

      if(Math.abs(facing_cam) > 0.0001){

        if(facing_cam < 0){
          normal = new Vector3D(-normal.x, -normal.y, -normal.z);
        }
        
        Vector3D light_dir = new Vector3D(0,0 ,-1.0).normalize(); // vector that points from camera
      
        // calculates the dot product between the each surface normal and light direction from camera
        double shading = Vector3D.dot(normal, light_dir) * 0.9;

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

        // NDC to Screen Space
        Vector3D[] v = Vector3D.normal_to_screen(p1, p2, p3);

        Vector3D A = v[0];
        Vector3D B = v[1];
        Vector3D C = v[2];

        //Color and lighting
        Color baseColor = new Color(171,171,171);
      
        int r_color = (int)(baseColor.getRed() * shading);
        int g_color = (int)(baseColor.getGreen() * shading);
        int b_color = (int)(baseColor.getBlue() * shading);
      
        r_color = Math.min(255, Math.max(0, r_color));
        g_color = Math.min(255, Math.max(0, g_color));
        b_color = Math.min(255, Math.max(0, b_color));
      
        Color shadedColor = new Color(r_color, g_color, b_color);
      
        //Wireframe or Solid Mode
        if(wireframe_mode){
          drawer.drawline(screen, (int)A.x, (int)A.y, (int)B.x, (int)B.y);
          drawer.drawline(screen, (int)B.x, (int)B.y, (int)C.x, (int)C.y);     // This is for wireframe and for debugging
          drawer.drawline(screen, (int)C.x, (int)C.y, (int)A.x, (int)A.y);
        }
        else if(texture_mode && mesh.is_texture){
            drawer.draw_triangle_textured(A, B, C,
            tri.uv1, tri.uv2, tri.uv3,
            mesh.texture, buffer, screen, shading
          );
        }
        else{
          drawer.draw_triangle(A, B, C, buffer, screen, shadedColor, shadedColor, shadedColor);
        }
      }
    }
  }

  @Override
    public void run() {
    long lastTime;
    while (is_running) {
      lastTime = System.nanoTime();

      update_movement();

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

    buffer.init();

    render(face, rotation, buffer, screen);

    g.drawImage(screen, 0, 0, null);
  }
}