package shader.source;

import java.io.InputStream;

public class SubFolderShaderSource implements ShaderSource {

	private ShaderSource source;
	private String path;
	
	public SubFolderShaderSource(ShaderSource source, String path) {
		this.source = source;
		this.path = path;
	}

	@Override
	public InputStream getShaderStream(String shader) {
		return source.getShaderStream(path + shader);
	}
	
}
