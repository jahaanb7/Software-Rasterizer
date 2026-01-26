# 3D Graphics Engine

A 3D software rasterizer built from scratch in Java. This project implements the core concepts of computer graphics, including 3D transformations, perspective projection, depth buffering, backface culling, and lighting - all without using any external graphics libraries.

## Features

- **Custom 3D Pipeline**: Complete graphics pipeline implemented from scratch
- **OBJ Model Loading**: Parse and render standard .obj 3D model files with texture coordinate support
- **Real-time Rendering**: Smooth 60 FPS rendering with interactive camera controls
- **Depth Buffer (Z-Buffer)**: Proper occlusion implement (model visibility)
- **Dual Rendering Modes**:
  - Wireframe mode for visualization and debugging
  - Rasterized mode with lighting and shading
- **Interactive Camera**:
  - WASD keyboard controls for translation
  - Mouse drag for model rotation
  - Mouse wheel for zoom
- **Lighting System**: Directional lighting with diffuse shading and ambient light
- **Backface Culling**: Automatic removal of back-facing triangles for performance
- **Perspective Projection**: Proper 3D to 2D transformation with field of view

## Getting Started

### Prerequisites

- Java 21 or higher
- No external libraries required (uses only Java standard library)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/jahaanb7/Software-Rasterizer.git
cd Software-Rasterizer
```

2. Compile the Java files:
```bash
mkdir bin
javac -d bin src/main/engine/*.java
cp -r resources bin/
```

3. Run the application:
```bash
java -cp bin src.main.engine.rasterizer
```

### Download Pre-built Releases

Pre-built executables are available for Windows and macOS:

- **Windows**: Download the `.zip` file, extract, and run `GraphicsEngine.exe`
- **macOS**: Download the `.dmg` file, mount it, and drag to Applications

[Download Latest Release](https://github.com/jahaanb7/Software-Rasterizer/releases)

## Project Structure

```
Software-Rasterizer/
├── src/main/engine/
│   ├── rasterizer.java      # Main application and rendering loop
│   ├── LineDrawer.java       # Line and triangle rasterization algorithms
│   ├── DepthBuffer.java      # Z-buffer for depth testing
│   ├── Matrix.java           # Matrix operations and transformations
│   ├── Vector3D.java         # 3D vector mathematics
│   ├── Vector4D.java         # 4D homogeneous coordinates
│   ├── Triangle.java         # Triangle data structure with UV support
│   ├── Mesh.java             # 3D mesh container
│   ├── OBJLoader.java        # OBJ file parser with texture coordinate support
│   └── MyMeshes.java         # Predefined mesh library
├── resources/
│   ├── monkey.obj            # Blender's Suzanne model
│   ├── sphere.obj            # Icosphere model
│   ├── rabbit.obj            # Stanford bunny model
│   ├── MaxPlanck.obj         # Max Planck bust
│   └── homer.obj             # Additional model
└── README.md
```

## Graphics Pipeline

The engine implements a complete 3D graphics pipeline:

1. **Model Loading**: Parse OBJ files to extract vertices, texture coordinates, and face definitions
2. **Model Transformation**: Apply rotation matrices for model orientation
3. **World Space**: Scale and translate model into world coordinates
4. **Backface Culling**: Calculate surface normals using cross product and cull back-facing triangles
5. **Lighting Calculation**: Compute diffuse lighting using dot product with light direction
6. **Camera Transform**: Translate vertices relative to camera position
7. **Clipping**: Cull triangles outside the view frustum (near/far plane)
8. **Projection**: Transform 3D coordinates to clip space using perspective projection matrix
9. **Perspective Divide**: Normalize coordinates by dividing by the w component
10. **Screen Mapping**: Convert normalized device coordinates (NDC) to screen pixels
11. **Rasterization**: Scan-convert triangles using barycentric coordinates
12. **Depth Testing**: Z-buffer comparison to determine pixel visibility
13. **Shading**: Apply interpolated colors or texture samples with lighting

## Key Algorithms & Techniques

### Depth Buffer (Z-Buffer)

### Bresenham's Line Algorithm

### Barycentric Coordinates

### Perspective Projection Matrix

FOV-based transformation from 3D camera space to 2D clip space:

```
[f/aspect   0      0              0           ]
[   0       f      0              0           ]
[   0       0   -(f+n)/(f-n)   -2fn/(f-n)    ]
[   0       0      -1             0           ]
```

where:
- `f` = far plane distance
- `n` = near plane distance
- `aspect` = width/height ratio
- `f` = 1/tan(fov/2)

### Rotation Matrices

### Backface Culling

### Lighting Model

Simple directional lighting:

## Loading Your Own Models

1. Export your 3D model as a `.obj` file (from modeling software such as Blender, etc.)
2. Place the `.obj` file in the `resources/` folder
3. In `rasterizer.java`, add to the constructor:

```java
Mesh yourModel = new Mesh();
yourModel.tris.addAll(OBJLoader.loadOBJ(
    getClass().getResourceAsStream("/resources/your_model.obj")
));
```

4. Update the render method in `paintComponent()`:

```java
render(myModel, rotation, buffer, screen);
```

## Configuration

Customize rendering parameters in `rasterizer.java`:

```java
// Screen resolution
public static final int SCREEN_WIDTH = 800;
public static final int SCREEN_HEIGHT = 800;

// Camera settings
private final int fov = 90;              // Field of view (degrees)
private final double near = 1;            // Near clipping plane
private final int far = 550;              // Far clipping plane

// Model settings
private double zOffset = 5;               // Distance from camera
private double scale = 1.0;               // Model scale factor

// Performance
private final int fps = 60;               // Target framerate
private double cam_speed = 0.10;          // Camera movement speed
```