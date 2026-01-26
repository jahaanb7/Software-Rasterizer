package src.main.engine;

class Vector4D {
  public double x, y, z, w;

  public Vector4D(double x, double y, double z, double w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
    }

  public Vector4D scalar_mul(double n){
    this.x *= n;
    this.y *= n;
    this.z *=  n;

    return new Vector4D(x,y,z,w);
  }

  public Vector4D mul(Matrix m) {
    double nx = this.x * m.data[0][0] + this.y * m.data[1][0] + this.z * m.data[2][0] + this.w * m.data[3][0];
    double ny = this.x * m.data[0][1] + this.y * m.data[1][1] + this.z * m.data[2][1] + this.w * m.data[3][1];
    double nz = this.x * m.data[0][2] + this.y * m.data[1][2] + this.z * m.data[2][2] + this.w * m.data[3][2];
    double nw = this.x * m.data[0][3] + this.y * m.data[1][3] + this.z * m.data[2][3] + this.w * m.data[3][3];

    return new Vector4D(nx, ny, nz, nw);
  }

  public Vector4D apply(Vector3D v, double zOffset, double scale, Vector3D center) {
    return new Vector4D(
        (v.x - center.x) * scale,
        (v.y - center.y) * scale,
        (v.z - center.z) * scale + zOffset,
        1
    );
  }

  public Vector3D toVector3D(){
    return new Vector3D(this.x, this.y, this.z);
  }
}