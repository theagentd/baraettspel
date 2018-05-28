package bara.character;

public interface Person {
	
	public void setPosition(float x, float y);
	
	public void moveTo(float x, float y, float time);
	
	public void setSprite(String spriteName, boolean flipped);
	
	public void setAlpha(float alpha);
	
	public void fade(float alpha, float time);
	
	public void destroy();
}
