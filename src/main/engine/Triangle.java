package src.main.engine;

public class Triangle{
  public Vector3D v1;
  public Vector3D v2;
  public Vector3D v3;

  public Vector2D uv1;
  public Vector2D uv2;
  public Vector2D uv3;

  public Triangle(Vector3D v1, Vector3D v2, Vector3D v3, Vector2D uv1, Vector2D uv2, Vector2D uv3){
    this.v1 = v1;
    this.v2 = v2;
    this.v3 = v3;

    this.uv1 = uv1;
    this.uv2 = uv2;
    this.uv3 = uv3;
  }

  public Triangle(Vector3D v1, Vector3D v2, Vector3D v3){
    this.v1 = v1;
    this.v2 = v2;
    this.v3 = v3;
  }
}
