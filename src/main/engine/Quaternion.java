package src.main.engine;

public class Quaternion{
  public double x;
  public double y;
  public double z;
  public double w;

  public Quaternion(double w,double x,double y,double z){
    this.w = w;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Quaternion(Vector3D u, double w){
    this.w = w;
    this.x = u.x;
    this.y = u.y;
    this.z = u.z;
  }
  
  public static void main(String[] args) {
      
  }

  public Vector3D transform(Vector3D vec){
    Quaternion q = new Quaternion(vec, 0);
    Quaternion rotate = this.multiply_q(q).multiply_q(this.inverse());
    return new Vector3D(rotate.x, rotate.y, rotate.z);
  }

  public static Quaternion quaternion_rotation(Vector3D axis, double theta){
    double angle = Math.toRadians(theta/2);

    double cos = Math.cos(angle);
    double sin = Math.sin(angle);

    double mag = Math.sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z);
    
    return new Quaternion(
      cos,
      sin * axis.x/mag,
      sin * axis.y/mag,
      sin * axis.z/mag
    );
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

  public Quaternion multiply_v(Vector3D v){
    return null;
  }
 
  public Quaternion inverse(){
    double magnitude = (this.w*this.w) + (this.x*this.x) + (this.y*this.y) + (this.z*this.z);
    return new Quaternion( this.w, -this.x/magnitude, -this.y/magnitude, -this.z/magnitude);
  }

  public Vector3D get_pure(Quaternion q){
    return new Vector3D(q.x, q.y, q.z);
  }

  public Quaternion convert_to_quaternion(Vector3D v, double s){
    return new Quaternion(s, v.x, v.y, v.z);
  }

  public void test_rotation(){
    Quaternion q = new Quaternion(1, 0, 0, 0);

    Vector3D i_hat = new Vector3D(1, 0, 0);
    Vector3D j_hat = new Vector3D(0, 1, 0);
    Vector3D k_hat = new Vector3D(0, 0, 1);
  }

  
}