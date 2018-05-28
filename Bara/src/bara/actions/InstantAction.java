package bara.actions;

import bara.SceneManager;

public abstract class InstantAction implements Action{

	@Override
	public boolean start(SceneManager sceneManager) {
		perform(sceneManager);
		return false;
	}
	
	public abstract void perform(SceneManager sceneManager);

	@Override
	public boolean update(SceneManager sceneManager, float delta) {
		return false;
	}

	@Override
	public void skip(SceneManager sceneManager) {}

}
