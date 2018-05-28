package bara;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.stb.STBImage.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;


public class Texture {
	
	private int id;
	
	private int width, height;
	
	public Texture(String path) {
		File file = new File(path);
		
		int[] w = new int[1];
		int[] h = new int[1];
		int[] c = new int[1];
		
		

		ByteBuffer imageData = stbi_load(file.getAbsolutePath(), w, h, c, 4);

		if(imageData == null){
			throw new RuntimeException(new FileNotFoundException("Could not load texture " + file.getAbsolutePath()));
		}
		
		width = w[0];
		height = h[0];

		int length = width*height * 4;
		for(int i = 0; i < length; i += 4){
			int r = imageData.get(i + 0) & 0xFF;
			int g = imageData.get(i + 1) & 0xFF;
			int b = imageData.get(i + 2) & 0xFF;
			int a = imageData.get(i + 3) & 0xFF;
			
			r = r * a >> 8;
			g = g * a >> 8;
			b = b * a >> 8;
			
			imageData.put(i + 0, (byte)r);
			imageData.put(i + 1, (byte)g);
			imageData.put(i + 2, (byte)b);
		}
		
		
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_SRGB8_ALPHA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
		glGenerateMipmap(GL_TEXTURE_2D);
		
		stbi_image_free(imageData);
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void dispose() {
		glDeleteTextures(id);
	}
}