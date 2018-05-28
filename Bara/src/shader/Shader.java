package shader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import shader.source.ShaderSource;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL43.*;

public class Shader {

    public static final int VERTEX_SHADER = GL_VERTEX_SHADER;
    public static final int FRAGMENT_SHADER = GL_FRAGMENT_SHADER;
    public static final int GEOMETRY_SHADER = GL_GEOMETRY_SHADER;
    public static final int TESSELLATION_CONTROL_SHADER = GL_TESS_CONTROL_SHADER;
    public static final int TESSELLATION_EVALUATION_SHADER = GL_TESS_EVALUATION_SHADER;
    public static final int COMPUTE_SHADER = GL_COMPUTE_SHADER;

    private int shaderID;
    
    public Shader(int type, ShaderSource source, String name){
    	this(
    			type, 
    			loadSourceFromStream(source.getShaderStream(name), name), 
    			name
    	);
    }
    
    public Shader(int type, ShaderSource source, String name, String defines){
    	this(
    			type, 
    			insertDefines(loadSourceFromStream(source.getShaderStream(name), name), defines), 
    			name + " with defines:\n" + defines
    	);
    }
    
    public Shader(int type, String sourceCode){
    	this(
    			type, 
    			sourceCode,
    			"custom shader:\n<code start>\n" + sourceCode + "\n<code end>"
    	);
    }

    /*public Shader(int type, File file) {
        initialize(type, loadFileSource(file), file.getAbsolutePath());
    }

    public Shader(int type, String sourceCode, File file) {
        initialize(type, sourceCode + '\n' + loadFileSource(file), file.getAbsolutePath() + " with defines:\n" + sourceCode);
    }

    public Shader(int type, String source) {
        initialize(type, source, "generated shader:\n<Shader start>\n" + source + "\n<ShaderEnd>");
    }*/
    
    private Shader(int type, String sourceCode, String shaderName){
        shaderID = glCreateShader(type);
        glShaderSource(shaderID, sourceCode);
        glCompileShader(shaderID);

        String errorLog = glGetShaderInfoLog(shaderID, 16384);
        if(errorLog.length() != 0){
            System.out.println("\nCompiling " + shaderName + "\nShader compile log: \n" + errorLog);
        }
    }
    
    private static String loadSourceFromStream(InputStream stream, String name){
   
        StringBuilder source = new StringBuilder();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            while ((line = reader.readLine()) != null) {
                source.append(line).append('\n');
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Failed to read shader source for shader '" + name + "'!");
            e.printStackTrace();
            return null;
        }
        return source.toString();
    }
    
    private static String insertDefines(String source, String defines){
    	int index = source.indexOf('\n');
    	return source.substring(0, index) + "\n" + defines + "\n" + source.substring(index+1); 
    }

    public void dispose(){
        glDeleteShader(shaderID);
    }

    public int getID(){
        return shaderID;
    }
}
