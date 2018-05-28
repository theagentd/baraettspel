package shader.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileShaderSource implements ShaderSource{
	
	private String path;
	
	public FileShaderSource(String path) {
		this.path = path;
	}

	@Override
	public InputStream getShaderStream(String shader) {
		try{
			return new FileInputStream(new File(path + shader));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

}
