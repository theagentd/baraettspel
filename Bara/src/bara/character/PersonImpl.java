package bara.character;

import static bara.ScriptFunctions.*;

import static org.lwjgl.opengl.GL11.*;

import bara.SceneManager;
import bara.Texture;
import bara.Util;
import bara.actions.InstantAction;
import bara.actions.TimedAction;

public class PersonImpl implements Person {
	
	private String name;
	private float scale;
	
	private String spriteName;
	private Texture sprite;
	private boolean flipped;
	
	private float x, y;
	
	private float alpha;
	
	public PersonImpl(String name, float scale) {
		this.name = name;
		this.scale = scale;
		
		sprite = null;
		flipped = false;
		
		alpha = 0;
	}
	
	public void render() {
		if(sprite == null) {
			return;
		}
		
		sprite.bind();
	
		glColor4f(alpha, alpha, alpha, alpha);
		glBegin(GL_QUADS);
		
		float left = flipped ? 1 : 0;
		float right = flipped ? 0 : 1;
		
		glTexCoord2f(left, 0);
		glVertex2f(x - sprite.getWidth()*scale*0.5f, y - sprite.getHeight()*scale*0.5f);
		
		glTexCoord2f(right, 0);
		glVertex2f(x + sprite.getWidth()*scale*0.5f, y - sprite.getHeight()*scale*0.5f);
		
		glTexCoord2f(right, 1);
		glVertex2f(x + sprite.getWidth()*scale*0.5f, y + sprite.getHeight()*scale*0.5f);
		
		glTexCoord2f(left, 1);
		glVertex2f(x - sprite.getWidth()*scale*0.5f, y + sprite.getHeight()*scale*0.5f);
		
		glEnd();
		
	}

	@Override
	public void setSprite(String spriteName, boolean flipped) {
		addCustomAction(new InstantAction() {
			@Override
			public void perform(SceneManager sceneManager) {
				
				if(spriteName != null && spriteName.equals(PersonImpl.this.spriteName)) {
					//same texture, skip reload
					PersonImpl.this.flipped = flipped;
				}else {
					deleteSpriteTexture();
					
					if(spriteName != null) {
						sprite = new Texture("characters/" + name + "/" + spriteName + ".png");
					} else {
						sprite = null;
					}
					PersonImpl.this.flipped = flipped;
				}
				
				PersonImpl.this.spriteName = spriteName;
				
			}
		}, false);
	}

	@Override
	public void setPosition(float x, float y) {
		addCustomAction(new InstantAction() {
			@Override
			public void perform(SceneManager sceneManager) {
				PersonImpl.this.x = x;
				PersonImpl.this.y = y;
			}
		}, false);
	}

	@Override
	public void moveTo(float targetX, float targetY, float time) {
		
		float startX = x;
		float startY = y;
		
		addCustomAction(new TimedAction(time) {
			
			@Override
			public void init(SceneManager sceneManager) {}
			
			@Override
			public void updateProgress(SceneManager sceneManager, float progress) {
				x = Util.smoothmix(startX, targetX, progress);
				y = Util.smoothmix(startY, targetY, progress);
			}
			
			@Override
			public void finish(SceneManager sceneManager) {
				x = targetX;
				y = targetY;
			}
		}, true);
	}

	@Override
	public void setAlpha(float alpha) {
		addCustomAction(new InstantAction() {
			@Override
			public void perform(SceneManager sceneManager) {
				PersonImpl.this.alpha = alpha;
			}
		}, false);
	}

	@Override
	public void fade(float targetAlpha, float time) {
		float startAlpha = alpha;
		addCustomAction(new TimedAction(time) {
			
			@Override
			public void init(SceneManager sceneManager) {}
			
			@Override
			public void updateProgress(SceneManager sceneManager, float progress) {
				alpha = Util.smoothmix(startAlpha, targetAlpha, progress);
			}
			
			@Override
			public void finish(SceneManager sceneManager) {
				alpha = targetAlpha;
			}
		}, true);
	}

	@Override
	public void destroy() {
		addCustomAction(new InstantAction() {
			@Override
			public void perform(SceneManager sceneManager) {
				deleteSpriteTexture();
			}
		}, false);
	}
	
	public void deleteSpriteTexture() {
		if(sprite != null) {
			sprite.dispose();
			sprite = null;
		}
	}
	
	
}