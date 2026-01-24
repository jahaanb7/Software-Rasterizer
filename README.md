# 3D Graphics Engine

A simple 3D software-rasterizer built from scratch in Java. This project implements the core concepts of computer graphics, including 3D transformations and rotations, perspective projection, backface culling, and basic lighting - all without using any external graphics libraries.

## Features
- **Custom 3D Pipeline**: Complete graphics pipeline implemented from scratch
- **OBJ Model Loading**: Parse and render standard .obj 3D model files
- **Real-time Rendering**: Smooth 60 FPS rendering with camera movement
- **Dual Rendering Modes**:
  - Wireframe mode for visualization and debugging
  - Raster Mode for real lighting and filled model
- **Interactive Camera**: WASD + QE controls for camera movement
- **Lighting System**: Directional lighting with diffuse shading
- **Backface Culling**: Automatic removal of invisible triangles
- **Perspective Projection**: Proper 3D to 2D transformation

## Screenshots
<img width="700" height="700" alt="Screenshot 2026-01-24 at 10 21 39 AM" src="https://github.com/user-attachments/assets/08c6c228-cdbf-4c60-8f74-561066714e61" />

## Getting Started

### Prerequisites

- Java 11 or higher
- No external libraries required (uses only Java standard library)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/jahaanb7/Graphics-Engine.git
cd Graphics-Engine
```

2. Compile the Java files:
```bash
javac src/main/*.java
```

3. Run the application:
```bash
java src.main.rasterizer
```

## Camera Movement Controls

| Key | Action |
|-----|--------|
| **W** | Move camera up |
| **S** | Move camera down |
| **A** | Move camera left |
| **D** | Move camera right |
| **Q** | Zoom out (move camera backward) |
| **E** | Zoom in (move camera forward) |

## Project Structure

```
Graphics-Engine/
├── src/
  ├── main/
  │   ├── rasterizer.java      # Main application and rendering pipeline
  │   ├── LineDrawer.java       # Line and triangle rasterization
  │   ├── Matrix.java           # Matrix operations and transformations
  │   ├── Vector3D.java         # 3D vector mathematics
  │   ├── Vector4D.java         # 4D homogeneous coordinates
  │   ├── Triangle.java         # Triangle data structure
  │   ├── Mesh.java            # 3D mesh container
  │   ├── OBJLoader.java       # OBJ file parser
  │   └── MyMeshes.java        # Mesh management
  ├── resources/
  │   ├── monkey.obj           # Blender's Suzanne model
  │   ├── sphere.obj           # Sphere model
  │   ├── rabbit.obj           # Rabbit model
  │   ├── MaxPlanck.obj        # Max Planck bust
  │   └── 100.obj              # Additional model
  └── README.md
```

## Math and Logic behind a Graphics Engine

### Graphics Pipeline

Any engine implements a standard 3D graphics pipeline which follows as such:

1. **Model Loading**: Parse OBJ files to extract vertices and face definitions
2. **Transformation**: Apply rotation matrices to animate the model
3. **Camera Transform**: Translate vertices based on camera position
4. **Backface Culling**: Calculate surface normals and cull back-facing triangles
5. **Lighting**: Compute diffuse lighting using dot product with light direction
6. **Projection**: Transform 3D coordinates to 2D using perspective projection matrix
7. **Perspective Divide**: Normalize coordinates by the w component
8. **Screen Mapping**: Convert normalized device coordinates to screen pixels
9. **Rasterization**: Draw triangles using either wireframe or filled mode

### Key Ideas behind Graphic Engines

- **Bresenham's Line Algorithm**: Efficient pixel-perfect line drawing (better than others due to integer values)
- **Barycentric Coordinates**: Triangle rasterization and interpolation (for coloring and shading)
- **Perspective Projection Matrix**: FOV-based 3D to 2D transformation with perspective
- **Rotation Matrices**: Euler angle rotations (rotation for all axes --> X, Y, Z axes)

## 🎨 Loading Your Own Models

1. Export your 3D model as a `.obj` file (supported by Blender, etc.)
2. Place the `.obj` file in the `resources/` folder
3. In `rasterizer.java`, modify the mesh initialization:

```java
Mesh myModel = new Mesh();
myModel.tris.addAll(OBJLoader.loadOBJ("resources/your_model.obj"));
```

4. Update the rendering loop to use your model:

```java
for(Triangle tri : myModel.tris) {
    // rendering code...
}
```

## ⚙️ Configuration

Adjust these constants in `rasterizer.java` to customize the rendering:

```java
private static final int SCREEN_WIDTH = 800;      // Window width
private static final int SCREEN_HEIGHT = 800;     // Window height
private final int fov = 90;                       // Field of view
private final int fps = 60;                       // Target framerate
private double zOffset = 5.0;                     // Model depth offset
private double scale = 1.0;                       // Model scale
private final double rotation_speed = 1;          // Rotation speed
```

## Mathematical Foundations

The engine implements a major part of Mathematics - Linear Algebra:

### Homogeneous Coordinates
Uses 4D vectors (x, y, z, w) for affine transformations and perspective projection.

### Projection Matrix
```
[f/aspect   0      0           0    ]
[   0       f      0           0    ]
[   0       0   (f+n)/(f-n)    1    ]
[   0       0  -(f*n)/(f-n)    0    ]
```
where f = far plane, n = near plane

### Rotation Matrices
- **X-axis rotation**: Roll
- **Y-axis rotation**: Yaw (currently animated)
- **Z-axis rotation**: Pitch

**Note**: I am the only contributor, Juhi Bhavsar is another account on may laptop, I accidently used that account to push changes. Please be assured that I worked on this by myself