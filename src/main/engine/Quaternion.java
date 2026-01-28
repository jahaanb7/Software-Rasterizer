package src.main.engine;

public class Quaternion{
  public double x;
  public double y;
  public double z;
  public double w;

  public Vector3D u = new Vector3D(x, y, z);

  public Quaternion(double w,double x,double y,double z){
    this.w = w;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Quaternion(Vector3D u, double w){
    this.w = w;
    this.u = u;
  }

  public Vector3D rotate_axis(Vector3D vec){
    Quaternion q = new Quaternion(vec, 0);
    Quaternion rotate = this.multiply_q(q).multiply_q(this.inverse());
    return new Vector3D(rotate.x, rotate.y, rotate.z);
  }

  public static Quaternion quaternion_rotation(Vector3D axis, double theta){
    double half_angle = Math.toRadians(theta/2);

    double cos = Math.cos(half_angle);
    double sin = Math.sin(half_angle);

    axis = axis.normalize();

    return new Quaternion(
      cos,
      sin * axis.x,
      sin * axis.y,
      sin * axis.z
    );
  }

  public Quaternion normalize(Quaternion q) {
    double norm = q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w;
    if (norm > 0.0001) { // Avoid division by zero
      return new Quaternion(
        q.w / norm,
        q.x / norm,
        q.y / norm,
        q.z / norm
      );
    }

    return new Quaternion(1.0f, 0.0f, 0.0f, 0.0f);
  }

  public Matrix to_Matrix(Quaternion q){

    q = normalize(q);
    
    return new Matrix(new double[][]{
      {1.0f - 2.0f*q.y*q.y - 2.0f*q.z*q.z, 2.0f*q.x*q.y - 2.0f*q.z*q.w, 2.0f*q.x*q.z + 2.0f*q.y*q.w, 0.0f},
      {2.0f*q.x*q.y + 2.0f*q.z*q.w, 1.0f - 2.0f*q.x*q.x - 2.0f*q.z*q.z, 2.0f*q.y*q.z - 2.0f*q.x*q.w, 0.0f},
      {2.0f*q.x*q.z - 2.0f*q.y*q.w, 2.0f*q.y*q.z + 2.0f*q.x*q.w, 1.0f - 2.0f*q.x*q.x - 2.0f*q.y*q.y, 0.0f},
      {0.0f, 0.0f, 0.0f, 1.0f}
    });
  }

  public Quaternion multiply_q(Quaternion q){
    double w1 = this.w;     double w2 = q.w;
    double x1 = this.x;     double x2 = q.x;
    double y1 = this.y;     double y2 = q.y;
    double z1 = this.z;     double z2 = q.z;

    return new Quaternion(
      (w1*w2 - x1*x2 - y1*y2 - z1*z2),
      (w1*x2 + x1*w2 + y1*z2 - z1*y2),
      (w1*y2 - x1*z2 + y1*w2 + z1*x2),
      (w1*z2 + x1*y2 - y1*x2 + z1*w2)
    );
  }

  public Quaternion mul_by_vec(Quaternion q){
    double w1 = this.w;
    double w2 = q.w;

    Vector3D v1 = this.u;
    Vector3D v2 = q.u;

    double amount_rotation = (w1*w2 - (Vector3D.dot(v1, v2)));
    Vector3D axis = Vector3D.multiply(w1, v2).add(Vector3D.multiply(w2, v1).add(Vector3D.cross(v1, v2)));

    return new Quaternion(axis, amount_rotation);
  }
 
  public Quaternion inverse(){
    double magnitude = (this.w*this.w) + (this.x*this.x) + (this.y*this.y) + (this.z*this.z);
    return new Quaternion( this.w, -this.x/magnitude, -this.y/magnitude, -this.z/magnitude);
  }

  public Quaternion get_pure(Vector3D vec){
    this.w = 0;
    this.x = vec.x;
    this.y = vec.y;
    this.z = vec.z;

    return this;
  }
  
  public Vector3D toEulerAngles() {
    Vector3D angles = new Vector3D(0, 0, 0);
    
    // Pitch (X-axis rotation)
    double sinp = 2.0 * (w * y - z * x);
    if (Math.abs(sinp) >= 1) {
      angles.x = Math.toDegrees(Math.copySign(Math.PI / 2, sinp));
    } else {
      angles.x = Math.toDegrees(Math.asin(sinp));
    }
    
    // Yaw (Y-axis rotation)
    double siny_cosp = 2.0 * (w * z + x * y);
    double cosy_cosp = 1.0 - 2.0 * (y * y + z * z);
    angles.y = Math.toDegrees(Math.atan2(siny_cosp, cosy_cosp));
    
    // Roll (Z-axis rotation)
    double sinr_cosp = 2.0 * (w * x + y * z);
    double cosr_cosp = 1.0 - 2.0 * (x * x + y * y);
    angles.z = Math.toDegrees(Math.atan2(sinr_cosp, cosr_cosp));
    
    return angles;
  }


  public static void main(String[] args) {
      
  }
}