package bara;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Bara {

	public static final int INTERNAL_WIDTH = 1920;
	public static final int INTERNAL_HEIGHT = 1080;
	public static final float ASPECT_RATIO = (float)INTERNAL_WIDTH/INTERNAL_HEIGHT;
	
	private WindowManager windowManager;
	
	
	private SceneManager sceneManager;
	
	private float mouseX, mouseY;
	
	public Bara() {
		windowManager = new WindowManager();
		
		sceneManager = new SceneManager();
		
		windowManager.setMouseCallback(new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if(action == GLFW_PRESS) {
					sceneManager.mouseClicked(mouseX, mouseY);
				}
			}
		});
		
		windowManager.setMouseMotionCallback(new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double mx, double my) {
				
				float w = windowManager.getWidth();
				float h = windowManager.getHeight();
				
				
				mouseX = (float)((mx / w - 0.5) * INTERNAL_WIDTH);
				mouseY = (float)((my / h - 0.5) * INTERNAL_HEIGHT);
				
				
				float aspectError = (w / h) / ASPECT_RATIO;
				
				if(aspectError > 1.0) {
					mouseX *= aspectError;
				} else {
					mouseY /= aspectError;
				}
				
			}
		});

		glEnable(GL_TEXTURE_2D);
		glEnable(GL_FRAMEBUFFER_SRGB);
	}
	
	private void gameloop() {
		
		long prevTime = System.nanoTime();
		
		while(!windowManager.isCloseRequested()) {
			
			long time = System.nanoTime();
			float delta = Math.min(1f / 15f, (time - prevTime) / 1_000_000_000f);
			prevTime = time;
			
			glClearColor(0, 0, 0, 1);
			glClear(GL_COLOR_BUFFER_BIT);
			
			setupViewport();
			
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(-INTERNAL_WIDTH/2.0f, INTERNAL_WIDTH/2.0f, INTERNAL_HEIGHT/2.0f, -INTERNAL_HEIGHT/2.0f, -1, +1);
			
			
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
			
			boolean done = sceneManager.update(delta, mouseX, mouseY);
			
			glDisable(GL_BLEND);
			
			windowManager.update();
			
			if(done) {
				break;
			}
		}
		System.out.println("Game ended.");
	}

	private void setupViewport() {
		
		int width = windowManager.getWidth();
		int height = windowManager.getHeight();
		
		float aspect = (float)width / height;
		
		int renderWidth = width, renderHeight = height;
		if(aspect > ASPECT_RATIO) {
			renderWidth = Math.round(height * ASPECT_RATIO);
		} else {
			renderHeight = Math.round(width / ASPECT_RATIO);
		}
		
		glViewport(width/2 - renderWidth/2, height/2 - renderHeight/2, renderWidth, renderHeight);
		
	}

	
	public static void main(String[] args) {
		new Bara().gameloop();
	}
}