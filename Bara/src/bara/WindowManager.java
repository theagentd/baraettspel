package bara;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

public class WindowManager {

	private GLFWErrorCallback errorCallback;
	
	private long window;
	
	private int[] width, height;

	private GLFWMouseButtonCallback mouseCallback;
	
	public WindowManager() {
		
		glfwSetErrorCallback(errorCallback = new GLFWErrorCallback(){
			@Override
			public void invoke(int error, long description) {
				
				System.err.println("GLFW error occured");
				System.err.println("\t" + MemoryUtil.memUTF8(description));
				System.err.println("Stack trace:");
				StackTraceElement[] stack = Thread.currentThread().getStackTrace();
				for(int i = 4; i < stack.length; i++){
					System.err.println(stack[i]);
				}
				System.err.println();
			}
		});
		
		
		if(!glfwInit()) {
			throw new UnsupportedOperationException("failed to initialize GLFW");
		}
		
		
		glfwDefaultWindowHints();

		glfwWindowHint(GLFW_RED_BITS, 8);
		glfwWindowHint(GLFW_GREEN_BITS, 8);
		glfwWindowHint(GLFW_BLUE_BITS, 8);
		
		glfwWindowHint(GLFW_ALPHA_BITS, 0);
		
		glfwWindowHint(GLFW_SRGB_CAPABLE, GLFW_TRUE);
		
		glfwWindowHint(GLFW_DEPTH_BITS, 0);
		glfwWindowHint(GLFW_STENCIL_BITS, 0);
		glfwWindowHint(GLFW_SAMPLES, 0);
		
		window = glfwCreateWindow(1280, 720, "Bara", 0, 0);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		
		width = new int[1];
		height = new int[1];
		glfwGetFramebufferSize(window, width, height);
	}
	
	public void setMouseCallback(GLFWMouseButtonCallback mouseCallback) {
		glfwSetMouseButtonCallback(window, this.mouseCallback = mouseCallback);
	}
	
	public void update() {
		
		glfwSwapBuffers(window);
		
		glfwPollEvents();
		
		glfwGetFramebufferSize(window, width, height);
	}
	
	public int getWidth() {
		return width[0];
	}
	
	public int getHeight() {
		return height[0];
	}

	public boolean isCloseRequested() {
		return glfwWindowShouldClose(window);
	}
}
