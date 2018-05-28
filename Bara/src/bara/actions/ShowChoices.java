package bara.actions;

import bara.SceneManager;

public class ShowChoices implements Action{
	
	private String[] choices;
	
	public ShowChoices(String[] choices) {
		this.choices = choices;
	}

	@Override
	public boolean start(SceneManager sceneManager) {
		sceneManager.showChoices(choices);
		return true;
	}

	@Override
	public boolean update(SceneManager sceneManager, float delta) {
		return true;
	}

	@Override
	public void skip(SceneManager sceneManager) {
	}
}