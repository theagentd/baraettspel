package bara.actions;

import bara.SceneManager;

public class ResetScene extends InstantAction{

	@Override
	public void perform(SceneManager sceneManager) {
		sceneManager.resetScene();
	}
}