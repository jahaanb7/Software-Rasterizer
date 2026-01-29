package src.main.engine;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Mesh {
    public ArrayList<Triangle> tris = new ArrayList<>();
    public BufferedImage texture;
    public boolean is_texture;

    public Mesh() {
        tris = new ArrayList<>();
        texture = null;
        is_texture = false;
    }

    public void set_texture(BufferedImage texture){
        this.texture = texture;
        this.is_texture = true;
    }

    public void remove_texture(){
        this.texture = null;
        this.is_texture = false;
    }
}