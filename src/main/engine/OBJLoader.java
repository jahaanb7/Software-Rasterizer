package src.main.engine;

import java.io.*;
import java.util.*;

public class OBJLoader {

  /**
   * Loads an OBJ file and returns a list of Triangles
   * @param filename path to the OBJ file
   * @return ArrayList of Triangle objects
   */
  
  public static ArrayList<Triangle> loadOBJ(InputStream inputStream) {

    ArrayList<Vector3D> vertices = new ArrayList<>();
    ArrayList<Vector2D> vertex_texture = new ArrayList<>();
    ArrayList<Triangle> triangles = new ArrayList<>();

    try (BufferedReader b_read = new BufferedReader(new InputStreamReader(inputStream))){
        String line;

        while ((line = b_read.readLine()) != null){ //until there is something to read
            line = line.trim(); //remove any whitespaces

            if (line.startsWith("v ")) { //check for vertices

                // Vertex line: v --> {x y z}
                String[] tokens = line.split("\\s+"); //tokeniz

                double x = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                double z = Double.parseDouble(tokens[3]);

                vertices.add(new Vector3D(x, y, z)); //add to vertices array as a new Vector3D
            }

            else if (line.startsWith("vt")){

                String[] tokens = line.split("\\s+");

                double u = Double.parseDouble(tokens[1]);
                double v = Double.parseDouble(tokens[2]);

                vertex_texture.add(new Vector2D(u, v));
            }

            else if (line.startsWith("f ")) { //check for faces to join the vertices in the given order

                int[] vertexIndices = new int[3];
                int[] uvIndices = new int[3];
                boolean hasUVs = false;

                // Face line: f --> {i j k} 
                String[] tokens = line.split("\\s+");

                for (int i = 0; i < 3; i++) {
                    String[] indices = tokens[i + 1].split("/");

                    // Vertex index (always present)
                    vertexIndices[i] = Integer.parseInt(indices[0]) - 1; // OBJ is 1-indexed

                    // UV index (optional)
                    if (indices.length > 1 && !indices[1].isEmpty()) {
                      uvIndices[i] = Integer.parseInt(indices[1]) - 1;
                      hasUVs = true;
                    }
                }

                // btw: indexing obj files means that vertices/faces are indexed from 1 to n
                Vector3D v1 = vertices.get(vertexIndices[0]);
                Vector3D v2 = vertices.get(vertexIndices[1]);
                Vector3D v3 = vertices.get(vertexIndices[2]);

                if (hasUVs && !vertex_texture.isEmpty()) {

                    Vector2D uv1 = vertex_texture.get(uvIndices[0]);
                    Vector2D uv2 = vertex_texture.get(uvIndices[1]);
                    Vector2D uv3 = vertex_texture.get(uvIndices[2]);

                    triangles.add(new Triangle(v1, v2, v3, uv1, uv2, uv3));
                }
                else {
                    triangles.add(new Triangle(v1, v2, v3));
                }
            }
        }
        System.out.println("OBJ Loaded: " + vertices.size() + " vertices, " + vertex_texture.size() + " UVs, " + triangles.size() + " triangles");
    } 
    catch (IOException e){
        e.printStackTrace();
        System.out.println("Loading Object/Model Error");
    }
    return triangles;
  }
}