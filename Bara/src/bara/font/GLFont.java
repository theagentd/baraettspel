package bara.font;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import org.lwjgl.BufferUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;

public class GLFont {
	
	private float originalSize;
	private float lineHeight;
	private float topOffset;
	
	private Glyph[] glyphs;
	
	private float inverseWidth, inverseHeight;
	private int texture;
	
	private float maxAdvance = 0f;
	
	public GLFont(File file) throws IOException{
		BufferedInputStream fos = new BufferedInputStream(new FileInputStream(file));
		try{
			_GLDistanceFieldFont(fos);
		}finally{
			if(fos != null){
				fos.close();
			}
		}
	}
	
	public GLFont(InputStream stream) throws IOException{
		_GLDistanceFieldFont(stream);
	}
	
	private void _GLDistanceFieldFont(InputStream stream) throws IOException{
		
		//System.out.println("Starting loading...");
		DataInputStream in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(stream)));
		
		originalSize = in.readFloat();
		lineHeight = in.readFloat();
		topOffset = in.readFloat();
		
		int maxGlyphIndex = in.readInt();
		glyphs = new Glyph[maxGlyphIndex+1];
		
		int numGlyphs = in.readInt();
		for(int i = 0; i < numGlyphs; i++){
			
			int character = in.readInt();
			
			float x1 = in.readFloat();
			float y1 = in.readFloat();
			float x2 = in.readFloat();
			float y2 = in.readFloat();
			
			float offsetX = in.readFloat();
			float offsetY = in.readFloat();
			
			float advance = in.readFloat();
			
			if (advance > maxAdvance){
				maxAdvance = advance;
			}
			
			glyphs[character] = new Glyph(x1, y1, x2, y2, offsetX, offsetY, advance);
		}

		//System.out.println("Loaded glyphs, loading texture...");
		
		int width = in.readInt();
		int height = in.readInt();
		
		//System.out.println("Font texture size: " + width + ", " + height);
		
		inverseWidth = 1f / width;
		inverseHeight = 1f / height;
		
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[]{-1, -1, -1, -1});
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		//long startTime = System.nanoTime();
		
		ByteBuffer data = BufferUtils.createByteBuffer(width*height*2);
		for(int i = 0; i < width*height; i++){
			/*short s = in.readShort();
			data.putShort(s);*/
			
			data.putShort(in.readShort()); //Makes sure endianness is handled correctly by the buffer.
		}
		data.flip();
		
		//System.out.println("Took: " + (System.nanoTime() - startTime) / 1000 / 1000f + " ms");
		
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_R16_SNORM, width, height, 0, GL_RED, GL_SHORT, data);
		glGenerateMipmap(GL_TEXTURE_2D);
		
		//System.out.println("Font loaded.");
	}
	
	public float[] getStringDimensions(String string, float maxLineWidth, float size, float extraSpacing, float extraLineSpacing) {
		int len = string.length();
		
		

		float scale = size / originalSize;
		float lineHeight = this.lineHeight * scale + extraLineSpacing;
		
		

		float advanceX = 0, advanceY = 0;
		float maxAdvanceX = 0;
		
		boolean isCommand = false;
		
		for(int i = 0; i < len; i++){
			char c = string.charAt(i);
			char next = (i+1) < len ? string.charAt(i+1) : 0;
			
			if(!isCommand){
				if(c == '\\' && next == '{'){
					c = next; // escaped curly brace {
					i++;
				}else{
					if(c == '{'){
						isCommand = true;
						continue;
					}
				}
				
				if(c == '\n'){
					maxAdvanceX = Math.max(maxAdvanceX, advanceX);
					advanceX = 0;
					advanceY += lineHeight;
					continue;
				}
				
				GLFont.Glyph g = getGlyph(c);
				if(g != null){
					float a = g.advance * scale + extraSpacing;
					if(advanceX + a > maxLineWidth){
						maxAdvanceX = Math.max(maxAdvanceX, advanceX);
						advanceX = 0;
						advanceY += lineHeight;
					}
					advanceX += a;
				}
				
				if(isBreakable(c)){
					float wordLength = getWordLength(string, len, i+1, scale, extraSpacing);
					if(advanceX + wordLength > maxLineWidth){
						maxAdvanceX = Math.max(maxAdvanceX, advanceX);
						advanceX = 0;
						advanceY += lineHeight;
					}
				}
			}else{
				if(c == '}'){
					isCommand = false;
				}
			}
		}
		
		if(isCommand){
			System.err.println("error in string \"" + string + "\": end of string in command");
		}
		
		return new float[]{Math.max(maxAdvanceX, advanceX), advanceY + lineHeight}; //Make sure to capture the last line.
	}

	public void drawString(String string, int numChars, VerticalAlignment verticalAlignment, float x, float y, float maxLineWidth, float size, float extraSpacing, float extraLineSpacing) {
		
		int len = string.length();
		
		if(len == 0){
			return;
		}
		
		float scale = size / originalSize;
		float lineHeight = this.lineHeight * scale + extraLineSpacing;
		
		
		
		
		float advanceX = 0, advanceY = 0;
		
		if(verticalAlignment == VerticalAlignment.TOP){
			advanceY = topOffset * scale;
		}else if(verticalAlignment == VerticalAlignment.CENTER){
			advanceY = topOffset * scale * 0.5f;
		}

		
		
		boolean isCommand = false;
		int commandStart = 0;
		
		glBindTexture(GL_TEXTURE_2D, texture);
		glBegin(GL_QUADS);
		
		for(int i = 0; i < numChars; i++){
			char c = string.charAt(i);
			char next = (i+1) < len ? string.charAt(i+1) : 0;
			
			if(!isCommand){
				if(c == '\\' && next == '{'){
					c = next; // escaped curly brace {
					i++;
				}else{
					if(c == '{'){
						isCommand = true;
						commandStart = i+1;
						continue;
					}
				}
				
				if(c == '\n'){
					advanceX = 0;
					advanceY += lineHeight;
					continue;
				}
				
				GLFont.Glyph g = getGlyph(c);
				if(g != null){
					float a = g.advance * scale + extraSpacing;
					if(advanceX + a > maxLineWidth){
						advanceX = 0;
						advanceY += lineHeight;
					}
					
					drawGlyph(x + advanceX, y + advanceY,
							g, inverseWidth, inverseHeight, scale);
					
					advanceX += a;
				}
				
				if(isBreakable(c)){
					float wordLength = getWordLength(string, len, i+1, scale, extraSpacing);
					if(advanceX + wordLength > maxLineWidth){
						advanceX = 0;
						advanceY += lineHeight;
					}
				}
			}else{
				if(c == '}'){
					isCommand = false;
					//int commandLength = i - commandStart;
					
					if(equalsCommand(string, commandStart, "color")){
						//TODO: implement >___<
					}else{
						System.err.println("error in string \"" + string + "\": unrecognized command '" + string.substring(commandStart, i) + "'");
					}
				}
			}
		}
		glEnd();
		
	}
	
	private void drawGlyph(float x, float y, GLFont.Glyph g, float inverseWidth, float inverseHeight, float sizeRatio){

		
		float x1 = x + g.offsetX * sizeRatio;
		float y1 = y + g.offsetY * sizeRatio;
		float x2 = x + (g.x2 - g.x1 + g.offsetX) * sizeRatio;
		float y2 = y + (g.y2 - g.y1 + g.offsetY) * sizeRatio;
		
		float tx1 = g.x1 * inverseWidth;
		float ty1 = g.y1 * inverseHeight;
		float tx2 = g.x2 * inverseWidth;
		float ty2 = g.y2 * inverseHeight;

		glTexCoord2f(tx1, ty1);
		glVertex2f(x1, y1);

		glTexCoord2f(tx2, ty1);
		glVertex2f(x2, y1);

		glTexCoord2f(tx2, ty2);
		glVertex2f(x2, y2);

		glTexCoord2f(tx1, ty2);
		glVertex2f(x1, y2);
	}

	private boolean isBreakable(char c) {
		return c == ' ' || c == '\n';
	}
	
	private float getWordLength(String string, int len, int i, float scale, float extraSpacing) {

		boolean isCommand = false;
		
		float advance = 0;
		for(; i < string.length(); i++){
			
			char c = string.charAt(i);
			char next = (i+1) < len ? string.charAt(i+1) : 0;
			
			if(!isCommand){
				if(c == '\\' && next == '{'){
					c = next; // escaped curly brace {
					i++;
				}else{
					if(c == '{'){
						isCommand = true;
						continue;
					}
				}

				GLFont.Glyph g = getGlyph(c);
				if(g != null){
					advance += g.advance * scale + extraSpacing;
				}
				if(isBreakable(c)){
					return advance;
				}
			}else{
				if(c == '}'){
					isCommand = false;
				}
			}
		}
		return advance;
	}
	
	private boolean equalsCommand(String string, int offset, String command){
		if(offset + command.length() > string.length()){
			return false;
		}
		for(int i = 0; i < command.length(); i++){
			if(string.charAt(offset + i) != command.charAt(i)){
				return false;
			}
		}
		return true;
	}
	
	public float getOriginalSize() {
		return originalSize;
	}
	
	public float getLineHeight() {
		return lineHeight;
	}
	
	public float getTopOffset() {
		return topOffset;
	}
	
	public Glyph getGlyph(int index){
		if(index >= glyphs.length){
			return null;
		}else{
			return glyphs[index];
		}
	}
	
	public float getInverseWidth() {
		return inverseWidth;
	}
	
	public float getInverseHeight() {
		return inverseHeight;
	}
	
	public float getMaxAdvance(){
		return maxAdvance;
	}
	
	public int getTexture() {
		return texture;
	}
	
	public static class Glyph{

		public final float x1, y1, x2, y2;
		
		public final float offsetX, offsetY;
		
		public final float advance;
		
		public Glyph(float x1, float y1, float x2, float y2, float offsetX, float offsetY, float advance) {
			
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			
			this.advance = advance;
		}
		
	}
	
}