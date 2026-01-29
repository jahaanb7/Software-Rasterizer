package src.main.engine;

public class Vector2D{
  public double u;
  public double v;

  public Vector2D(double u, double v){
    this.u = u;
    this.v = v;
  }

  public static Vector2D interpolate(Vector2D uv1, Vector2D uv2, Vector2D uv3, 
                                     double w1, double w2, double w3) {
    double u = w1 * uv1.u + w2 * uv2.u + w3 * uv3.u;
    double v = w1 * uv1.v + w2 * uv2.v + w3 * uv3.v;
    return new Vector2D(u, v);
  }
}