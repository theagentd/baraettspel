package shader.source;

import java.io.InputStream;

public class ClasspathShaderSource implements ShaderSource{
	
	private String subDirectory;
	
	public ClasspathShaderSource() {
		this("");
	}
	
	public ClasspathShaderSource(String subDirectory) {
		this.subDirectory = subDirectory;
	}
	
	
	@Override
	public InputStream getShaderStream(String shader) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(subDirectory + shader);
	}
}