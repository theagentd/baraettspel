package bara.actions;

import bara.SceneManager;

public class Sleep extends TimedAction {
	
	public Sleep(float time) {
		super(time);
	}

	@Override
	public void init(SceneManager sceneManager) {}

	@Override
	public void updateProgress(SceneManager sceneManager, float progress) {}

	@Override
	public void finish(SceneManager sceneManager) {}
}