package src.main.engine;

public class camera{
  // position and speed
  public Vector3D cam_position;
  public double cam_speed = 0;
  public Quaternion orient;

  //basis vectors for orient
  private Vector3D i_hat;
  private Vector3D j_hat;
  private Vector3D k_hat;


  public camera() {
    this.cam_position = new Vector3D(0, 0, 0);
    this.cam_speed = 10.0;
    this.orient = new Quaternion(1,0,0,0);

    update_cam();
  }

  public void set_cam(double x, double y, double z, double camera_speed){
    this.cam_position = new Vector3D(x, y, z);
    this.cam_speed = camera_speed;
    this.orient = new Quaternion(1,0,0,0);

    update_cam();
  }

  public void update_cam(){
    Vector3D defaultForward = new Vector3D(0, 0, -1);
    Vector3D defaultRight = new Vector3D(1, 0, 0);
    Vector3D defaultUp = new Vector3D(0, 1, 0);
    
    // Rotate default vectors by camera orientation
    k_hat= orient.rotate_axis(defaultForward);
    i_hat= orient.rotate_axis(defaultRight);
    j_hat= orient.rotate_axis(defaultUp);
  }

  public void rotate(double pitch, double yaw){
    //create the rotations as quaternions
    Quaternion pitchQuat = Quaternion.quaternion_rotation(new Vector3D(1, 0, 0), pitch);
    Quaternion yawQuat = Quaternion.quaternion_rotation(new Vector3D(0, 1, 0), yaw);
    
    //apply yaw then pitch
    Quaternion rotation = yawQuat.multiply_q(pitchQuat);
    
    //apply that to the camera
    orient = rotation.multiply_q(orient);
    
    orient = orient.normalize(orient);
    
    update_cam();
  }

  public void rotateAroundAxis(Vector3D axis, double angle){

    Quaternion rotation = Quaternion.quaternion_rotation(axis, angle);
    orient = rotation.multiply_q(orient);

    orient = orient.normalize(orient);
    update_cam();
  }

  public void resetOrientation() {
    orient = new Quaternion(1, 0, 0, 0);
    update_cam();
  }

  public Vector3D worldToCamera3(Vector3D worldPoint) {
    //move point relative to camera position
    Vector3D translated = new Vector3D(
      worldPoint.x - cam_position.x,
      worldPoint.y - cam_position.y,
      worldPoint.z - cam_position.z
    );
    
    //apply inverse camera rotation
    Quaternion inverseOrientation = orient.conjugate();
    Vector3D rotated = inverseOrientation.rotate_axis(translated);
    
    return rotated;
  }

  public Vector4D worldToCamera4(Vector4D worldPoint) {

    // Convert to vector3D, transform it and then convert back
    Vector3D point3D = new Vector3D(worldPoint.x, worldPoint.y, worldPoint.z);
    Vector3D transformed = worldToCamera3(point3D);

    return new Vector4D(transformed.x, transformed.y, transformed.z, worldPoint.w);
  }

  public void move_k_hat(double amount){
    cam_position.x += k_hat.x * amount;
    cam_position.y += k_hat.y * amount;
    cam_position.z += k_hat.z * amount;
  }

  public void move_i_hat(double amount) {
    cam_position.x += i_hat.x * amount;
    cam_position.y += i_hat.y * amount;
    cam_position.z += i_hat.z * amount;
  }

  public void move_j_hat(double amount) {
    cam_position.x += j_hat.x * amount;
    cam_position.y += j_hat.y * amount;
    cam_position.z += j_hat.z * amount;
  }

  public Vector3D get_khat(){
    return k_hat;
  }
  public Vector3D get_ihat(){
    return i_hat;
  }
  public Vector3D get_jhat(){
    return j_hat;
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
