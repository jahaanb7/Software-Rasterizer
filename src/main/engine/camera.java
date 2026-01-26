package src.main.engine;

public class camera{
  // position and speed
  public Vector3D cam_position;
  public double cam_speed = 0;

  //camera rotation
  public double pitch;
  public double yaw;
  public double roll;
  
  public camera() {
    this.cam_position = new Vector3D(0, 0, 0);
    this.cam_speed = 10.0;
    this.pitch = 0;
    this.yaw = 0;
    this.roll = 0;
  }

  public void set_cam(double x, double y, double z, double camera_speed){
    this.cam_position = new Vector3D(x, y, z);
    this.cam_speed = camera_speed;

    this.pitch = 0;
    this.yaw = 0;
    this.roll = 0;
  }

  public void update_movement(boolean move_left, boolean move_right, boolean move_up, boolean move_down, boolean move_backward, boolean move_forward){
    
    // Convert yaw to radians for trig functions
    double yaw_rad = Math.toRadians(yaw);
    
    // Calculate forward and right vectors based on yaw
    double forwardX = Math.sin(yaw_rad);
    double forwardZ = Math.cos(yaw_rad);
    double rightX = Math.cos(yaw_rad);
    double rightZ = -Math.sin(yaw_rad);
    
    // Move relative to camera orientation
    if(move_left)     {cam_position.x -= rightX * cam_speed; 
                       cam_position.z -= rightZ * cam_speed;}
    if(move_right)    {cam_position.x += rightX * cam_speed; 
                       cam_position.z += rightZ * cam_speed;}
    if(move_forward)  {cam_position.x += forwardX * cam_speed; 
                       cam_position.z += forwardZ * cam_speed;}
    if(move_backward) {cam_position.x -= forwardX * cam_speed; 
                       cam_position.z -= forwardZ * cam_speed;}
    
    // Up/down is still world-space
    if(move_up)       {cam_position.y -= cam_speed;}
    if(move_down)     {cam_position.y += cam_speed;}
  }

  public double get_x(){ 
    return cam_position.x; 
  }
  public double get_y(){ 
    return cam_position.y; 
  }
  public double get_z(){ 
    return cam_position.z; 
  }

  public Vector3D get_position() {
    return new Vector3D(cam_position.x, cam_position.y, cam_position.z);
  }

  public void set_position(double x, double y, double z) {
    cam_position.x = x;
    cam_position.y = y;
    cam_position.z = z;
  }

  public void rotate_cam(double r_pitch, double r_yaw, double r_roll){
    this.pitch += r_pitch;
    this.yaw += r_yaw;
    this.roll += r_roll;
  }

  public static void main(String[] args) {
      
  }
}