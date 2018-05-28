package bara.actions;

import bara.SceneManager;

public interface Action {
	
	public boolean start(SceneManager sceneManager); 
	public boolean update(SceneManager sceneManager, float delta);
	public void skip(SceneManager sceneManager);
}
