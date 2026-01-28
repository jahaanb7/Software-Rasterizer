package src.main.engine;

public class camera{
  // position and speed
  public Vector3D cam_position;
  public double cam_speed = 0;
  public Quaternion orient;

  public camera() {
    this.cam_position = new Vector3D(0, 0, 0);
    this.cam_speed = 10.0;
    this.orient = new Quaternion(1,0,0,0);
  }

  public void set_cam(double x, double y, double z, double camera_speed){
    this.cam_position = new Vector3D(x, y, z);
    this.cam_speed = camera_speed;
    this.orient = new Quaternion(1,0,0,0);
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


  public static void main(String[] args) {
      
  }
}
