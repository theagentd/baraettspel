package shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.EXTGeometryShader4.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class ShaderProgram {
	
	private static final boolean WARN_IF_LINKED = true;

    private int program;
    private boolean linked;
    
    public ShaderProgram(){
        program = glCreateProgram();
        linked = false;
    }

    public void attachShader(Shader shader){
        glAttachShader(program, shader.getID());
        warnIfLinked("shader attached");
    }

	public void detachShader(Shader shader){
        glDetachShader(program, shader.getID());
        warnIfLinked("shader detached");
    }

    public void setAttribLocation(String name, int location){
    	glBindAttribLocation(program, location, name);
        warnIfLinked("attribute location updated");
    }
    
    public void bindFragDataLocation(int index, String name){
    	glBindFragDataLocation(program, index, name);
        warnIfLinked("frag data location updated");
    }

    public void link(){
        glLinkProgram(program);
        if(glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE){
            String log = glGetProgramInfoLog(program);
            if(log.length() != 0){
                System.out.println("Program " + this + " link log:\n" + log);
            }
        	System.exit(0);
        }
        linked = true;
    }

    public static int binds = 0;
    public void bind(){
        glUseProgram(program);
        binds++;
    }

    public void dispose(){
        glDeleteProgram(program);
    }

    public int getAttribLocation(String name){
        return getAttribLocation(name, true);
    }
    
    public int getAttribLocation(String name, boolean checkError){
    	int loc = glGetAttribLocation(program, name);
        if(checkError && loc == -1){
            System.err.println("SHADER ERROR: Attribute '" + name + "' does not exist in shader program " + this + "!");
        }
        return loc;
    }
    
    public int getUniformLocation(String name){
    	return getUniformLocation(name, true);
    }

    public int getUniformLocation(String name, boolean checkError){
        int loc = glGetUniformLocation(program, name);
        if(checkError && loc == -1){
            System.err.println("Warning: Uniform '" + name + "' does not exist in shader program " + this);
        }
        return loc;
    }

	public void setBinaryRetrievableHint(boolean b) {
		//glProgramParameteri(program, GL_PROGRAM_BINARY_RETRIEVABLE_HINT, value);
		programParameter(GL_PROGRAM_BINARY_RETRIEVABLE_HINT, b ? GL_TRUE : GL_FALSE);
	}

    public void programParameter(int parameter, int i){
        //Why is this method only core in GL 4.1 when geometry shaders are available in 3.2?!
        glProgramParameteriEXT(program, parameter, i);
    }

    public byte[] getProgramBinary(){
    	int length = glGetProgrami(program, GL_PROGRAM_BINARY_LENGTH);
    	
    	IntBuffer lengthBuffer = BufferUtils.createIntBuffer(4);
    	//lengthBuffer.put(length).flip();
    	IntBuffer formatBuffer = BufferUtils.createIntBuffer(4);
    	ByteBuffer buffer = BufferUtils.createByteBuffer(length);
    	
    	glGetProgramBinary(program, lengthBuffer, formatBuffer, buffer);
    	
    	byte[] binaryData = new byte[length];
    	buffer.get(binaryData);
    	return binaryData;
    }
    
    public void dumpProgramBinary(){
    	System.out.println("Dumping " + this + ":");
    	System.out.println(new String(getProgramBinary()));
    	System.out.println("End of dump.");
    }
    
    public int getID(){
    	return program;
    }

    public static void useFixed(){
        glUseProgram(0);
    }

    private void warnIfLinked(String string) {
		if(WARN_IF_LINKED && linked){
			System.err.println("WARNING: " + string);
		}
	}
}
