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
  
  public static void main(String[] args) {
      
  }

  public Quaternion transform(Vector3D vec){
    Quaternion q = new Quaternion(vec, 0);
    Quaternion t = this.multiply_q(q).inverse();
  }

  public static Quaternion quaternion_rotation(Vector3D axis, double theta){
    double angle = Math.toRadians(theta/2);

    double cos = Math.cos(angle);
    double sin = Math.sin(angle);

    double rotate = cos + ((sin * axis.x) + (sin * axis.y) + (sin * axis.z));

    return null;
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
 
  public double inverse(){
    double magnitude = Math.pow(this.w, 2) + Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2);
    double q_conjugate = this.w - this.x - this.y - this.z;

    double inverse = q_conjugate/magnitude;

    return inverse;
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