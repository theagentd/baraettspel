package shader.source;

import java.io.InputStream;

public interface ShaderSource {
	
	public static ShaderSource DEFAULT_SHADER_SOURCE = new ClasspathShaderSource("shaders/");
	
	public InputStream getShaderStream(String shader);
}