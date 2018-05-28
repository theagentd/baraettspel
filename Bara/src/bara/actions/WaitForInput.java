package bara.actions;

import bara.SceneManager;

public class WaitForInput implements Action{
	
	private boolean clearText;
	
	public WaitForInput(boolean clearText) {
		this.clearText = clearText;
	}

	@Override
	public boolean start(SceneManager sceneManager) {
		return true;
	}

	@Override
	public boolean update(SceneManager sceneManager, float delta) {
		return true;
	}

	@Override
	public void skip(SceneManager sceneManager) {
		if(clearText) {
			sceneManager.setText(null, 0);
		}
	}
}