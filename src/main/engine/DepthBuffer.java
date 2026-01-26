package src.main.engine;

public class DepthBuffer{
  public Vector3D max = new Vector3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
  public Vector3D min = new Vector3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

  public DepthBuffer(Vector3D max, Vector3D min){
    this.max = max;
    this.min = min;
  }
  
}