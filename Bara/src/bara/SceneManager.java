package bara;

import static bara.Bara.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.reflections.Reflections;

import bara.actions.Action;
import bara.actions.ResetScene;
import bara.character.Person;
import bara.character.PersonImpl;
import bara.font.GLFont;
import bara.font.VerticalAlignment;
import bara.scenes.Scene;
import shader.Shader;
import shader.ShaderProgram;
import shader.source.ShaderSource;

public class SceneManager {

	private ArrayBlockingQueue<ArrayList<Action>> actionQueue;
	private SynchronousQueue<Object> resultQueue;
	private ArrayList<Action> actions;
	
	private ShaderProgram fontShader;
	private GLFont font;
	
	
	private Texture background;
	private float faderAlpha = 0;
	
	private String text;
	private int numChars;
	
	private ArrayList<PersonImpl> persons;
	
	
	
	private HashMap<String, Class<? extends Scene>> sceneMap;
	private Thread scriptThread;
	private volatile boolean finished = false;
	
	
	public SceneManager() {
		
		actionQueue = new ArrayBlockingQueue<>(1);
		resultQueue = new SynchronousQueue<>();
		actions = null;
		
		fontShader = new ShaderProgram();
		fontShader.attachShader(new Shader(Shader.FRAGMENT_SHADER, ShaderSource.DEFAULT_SHADER_SOURCE, "font.frag"));
		fontShader.link();
		
		
		try {
			font = new GLFont(new File("fonts/font.wdf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		persons = new ArrayList<>();
		
		ScriptFunctions.setSceneManager(this);
		
		
		
		
		
		sceneMap = new HashMap<>();
		Reflections reflections = new Reflections("bara.scenes");
		for(Class<? extends Scene> c : reflections.getSubTypesOf(Scene.class)) {
			sceneMap.put(c.getSimpleName(), c);
		}
		
		scriptThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String nextScene = "Start";
				
				while(nextScene != null) {

					submitActions(new ResetScene());

					Scene scene = findScene(nextScene);
					if(scene != null) {
						System.out.println(">>> Starting scene '" + nextScene + "' <<<");
						nextScene = scene.play();
					}else {
						System.err.println("Failed to find scene '" + nextScene + "'");
						nextScene = null;
					}
				}
				finished = true;
			}
		}, "Bara Script Thread");
		scriptThread.setDaemon(true);
		scriptThread.start();
	}
	
	public void mouseClicked() {
		if(actions != null) {
			for(int i = 0; i < actions.size(); i++) {
				actions.get(i).skip(this);
			}
			actions = null;
			try {
				resultQueue.put(new Object());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Object submitActions(Action action) {
		ArrayList<Action> actions = new ArrayList<>();
		actions.add(action);
		return submitActions(actions);
	}
	
	public Object submitActions(ArrayList<Action> actions) {
		try {
			actionQueue.put(actions);
			
			return resultQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/*
	 * Script callbacks.
	 */
	public void resetScene() {
		if(background != null) {
			background.dispose();
			background = null;
		}
		faderAlpha = 0;
		
		text = null;
		numChars = 0;
		
		for(int i = 0; i < persons.size(); i++) {
			persons.get(i).deleteSpriteTexture();;
		}
		persons.clear();
		
		System.out.println("Scene reset.");
		
		
	}
	
	public void setBackground(String name) {
		if(background != null) {
			background.dispose();
		}
		if(name != null) {
			background = new Texture("backgrounds/" + name + ".png");
		} else {
			background = null;
		}
	}
	
	public void setFaderAlpha(float faderAlpha) {
		this.faderAlpha = faderAlpha;
	}
	
	public float getFaderAlpha() {
		return faderAlpha;
	}

	public void setText(String text, int numChars) {
		this.text = text;
		this.numChars = numChars;
	}

	public void addPerson(PersonImpl person) {
		persons.add(person);
	}
	

	public void removePerson(PersonImpl person) {
		persons.remove(person);
	}
	
	
	
	
	
	

	public boolean update(float delta) {

		if(actions == null) {
			actions = actionQueue.poll();
			if(actions != null) {
				for(int i = 0; i < actions.size(); i++) {
					Action a = actions.get(i);
					if(!a.start(this)) {
						actions.remove(i--);
					}
				}
			}
		}
		
		if(actions != null) {
			for(int i = 0; i < actions.size(); i++) {
				Action a = actions.get(i);
				if(!a.update(this, delta)) {
					actions.remove(i--);
				}
			}
			if(actions.isEmpty()) {
				actions = null;
				try {
					resultQueue.put(new Object());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		if(background != null) {
			background.bind();
		}else {
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		glColor4f(1, 1, 1, 1);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2f(-INTERNAL_WIDTH*0.5f, -INTERNAL_HEIGHT*0.5f);
		glTexCoord2f(1, 0);
		glVertex2f(+INTERNAL_WIDTH*0.5f, -INTERNAL_HEIGHT*0.5f);
		glTexCoord2f(1, 1);
		glVertex2f(+INTERNAL_WIDTH*0.5f, +INTERNAL_HEIGHT*0.5f);
		glTexCoord2f(0, 1);
		glVertex2f(-INTERNAL_WIDTH*0.5f, +INTERNAL_HEIGHT*0.5f);
		glEnd();
		
		renderPersons();
		
		renderTextBox();
		
		
		glColor4f(0, 0, 0, 1 - faderAlpha*faderAlpha);
		glBegin(GL_QUADS);
		glVertex2f(-INTERNAL_WIDTH*0.5f, -INTERNAL_HEIGHT*0.5f);
		glVertex2f(+INTERNAL_WIDTH*0.5f, -INTERNAL_HEIGHT*0.5f);
		glVertex2f(+INTERNAL_WIDTH*0.5f, +INTERNAL_HEIGHT*0.5f);
		glVertex2f(-INTERNAL_WIDTH*0.5f, +INTERNAL_HEIGHT*0.5f);
		glEnd();
		
		return finished;
	}
	
	private void renderPersons() {
		for(int i = 0; i < persons.size(); i++) {
			PersonImpl p = persons.get(i);
			p.render();
		}
	}

	private void renderTextBox() {

		float boxX1 = -INTERNAL_WIDTH*0.5f + 40;
		float boxX2 = +INTERNAL_WIDTH*0.5f - 40;
		
		float boxY1 = INTERNAL_HEIGHT*0.5f - 300;
		float boxY2 = INTERNAL_HEIGHT*0.5f - 40;
		
		glBindTexture(GL_TEXTURE_2D, 0);
		glColor4f(0, 0, 0, 0.5f);
		glBegin(GL_QUADS);
		glVertex2f(boxX1, boxY1);
		glVertex2f(boxX2, boxY1);
		glVertex2f(boxX2, boxY2);
		glVertex2f(boxX1, boxY2);
		glEnd();
		
		if(text != null) {
			fontShader.bind();
			glColor3f(1f, 1f, 1f);
			font.drawString(text, numChars, VerticalAlignment.TOP, boxX1 + 20, boxY1 + 20, boxX2 - boxX1 - 40, 75, 0, 0);
			ShaderProgram.useFixed();
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}

	private Scene findScene(String sceneName){
		Class<? extends Scene> c = sceneMap.get(sceneName);
		if(c != null) {
			try {
				return (Scene)c.getConstructors()[0].newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException	| InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
