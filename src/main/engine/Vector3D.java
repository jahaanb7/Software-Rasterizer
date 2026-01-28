package src.main.engine;

class Vector3D{
  public double x;
  public double y;
  public double z;

  public Vector3D(double x, double y, double z){
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vector3D sub(Vector3D v) {
    double x = this.x - v.x;
    double y = this.y - v.y;
    double z = this.z - v.z;

    Vector3D result = new Vector3D(x, y, z);
    return result;
  }

  public Vector3D add(Vector3D v) {
    double x = this.x + v.x;
    double y = this.y + v.y;
    double z = this.z + v.z;

    Vector3D result = new Vector3D(x, y, z);
    return result;
  }

  public static Vector3D multiply(double s, Vector3D v){
    return new Vector3D(s*v.x, s*v.y, s*v.z);
  }

  public static double dot(Vector3D a, Vector3D b){
    double result = ((a.x * b.x)+(a.y * b.y)+(a.z * b.z));
    return result;
  }

  public static Vector3D cross(Vector3D a, Vector3D b){
    double x = a.y * b.z - a.z * b.y;
    double y = a.z * b.x - a.x * b.z;
    double z = a.x * b.y - a.y * b.x;

    return new Vector3D(x, y, z);
  }

  public Vector3D normalize(){
    double length = Math.sqrt(x*x + y*y + z*z);
    return new Vector3D(x/length, y/length, z/length);
  }

  public Vector4D mul(Matrix m) {
    double nx = this.x * m.data[0][0] + this.y * m.data[1][0] + this.z * m.data[2][0] + 1.0 * m.data[3][0];
    double ny = this.x * m.data[0][1] + this.y * m.data[1][1] + this.z * m.data[2][1] + 1.0 * m.data[3][1];
    double nz = this.x * m.data[0][2] + this.y * m.data[1][2] + this.z * m.data[2][2] + 1.0 * m.data[3][2];
    double nw = this.x * m.data[0][3] + this.y * m.data[1][3] + this.z * m.data[2][3] + 1.0 * m.data[3][3];

    return new Vector4D(nx, ny, nz, nw);
  }

  public static Vector3D center(Vector4D v1, Vector4D v2, Vector4D v3){
    return new Vector3D(
    (v1.x + v2.x + v3.x)/3.0,
    (v1.y + v2.y + v3.y)/3.0,
    (v1.z + v2.z + v3.z)/3.0);
  }


  public static double getDeterminant(Vector3D a, Vector3D b, Vector3D p){
    Vector3D ab = new Vector3D(b.x, b.y, b.z);
    ab.sub(a);

    Vector3D ac = new Vector3D(p.x,p.y,p.z);
    ac.sub(a);

    double result = ac.y * ac.x - ab.x * ac.y;

    return result;
  }

  public static Vector3D[] normal_to_screen(Vector4D p1, Vector4D p2, Vector4D p3){
    /* 
    Convert from NDC (Normalized Device Coordinates) to screen space
    0.5 to get it to the center of the screen, and + 1 to get it in infront of camera.
    */
    int sx1 = (int)((p1.x + 1) * 0.5 * rasterizer.SCREEN_WIDTH); 
    int sy1 = (int)((1 - (p1.y + 1) * 0.5) * rasterizer.SCREEN_HEIGHT);

    int sx2 = (int)((p2.x + 1) * 0.5 * rasterizer.SCREEN_WIDTH);
    int sy2 = (int)((1 - (p2.y + 1) * 0.5) * rasterizer.SCREEN_HEIGHT);

    int sx3 = (int)((p3.x + 1) * 0.5 * rasterizer.SCREEN_WIDTH);
    int sy3 = (int)((1 - (p3.y + 1) * 0.5) * rasterizer.SCREEN_HEIGHT);

    Vector3D[] output = {
      new Vector3D(sx1, sy1, p1.z), 
      new Vector3D(sx2, sy2, p2.z), 
      new Vector3D(sx3, sy3, p3.z), 
    };
    return output;
  }

  public Vector4D toVector4D(){
    return new Vector4D(x, y, z, 1);
  }
}