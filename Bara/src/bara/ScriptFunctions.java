package bara;

import java.util.ArrayList;

import bara.actions.*;
import bara.character.Person;
import bara.character.PersonImpl;

public class ScriptFunctions {
	
	public static final int WIDTH = Bara.INTERNAL_WIDTH;
	public static final int HEIGHT = Bara.INTERNAL_HEIGHT;
	
	public static final int HALF_WIDTH = Bara.INTERNAL_WIDTH / 2;
	public static final int HALF_HEIGHT = Bara.INTERNAL_HEIGHT / 2;
	
	private static SceneManager sceneManager;
	private static ArrayList<Action> actions = new ArrayList<>();
	
	static void setSceneManager(SceneManager sceneManager) {
		ScriptFunctions.sceneManager = sceneManager;
	}
	
	public static void text(String text) {
		text(text, 20f);
	}
	
	public static void setBackground(String name) {
		actions.add(new InstantAction() {
			@Override
			public void perform(SceneManager sceneManager) {
				sceneManager.setBackground(name);
			}
		});
	}
	
	public static void text(String text, float lettersPerSecond) {
		actions.add(new Text(text, lettersPerSecond));
		submit();
		actions.add(new WaitForInput(true));
		submit();
	}
	
	public static void fade(float targetAlpha, float time) {
		actions.add(new Fade(targetAlpha, time));
		submit();
	}
	
	public static Person createPerson(String name, float scale) {
		PersonImpl person = new PersonImpl(name, scale);
		actions.add(new AddPerson(person));
		return person;
	}
	
	public static void addCustomAction(Action action, boolean wait) {
		actions.add(action);
		if(wait) {
			submit();
		}
	}
	
	public static int showChoice(String... choices) {
		actions.add(new ShowChoice(choices));
		return (Integer)submit();
	}

	private static Object submit() {
		Object result = sceneManager.submitActions(actions);
		actions = new ArrayList<>();
		return result;
	}
}
