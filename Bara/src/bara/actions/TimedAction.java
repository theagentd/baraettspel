package bara.actions;

import bara.SceneManager;

public abstract class TimedAction implements Action{
	
	private float progress, invTime;
	
	public TimedAction(float time) {
		progress = 0;
		invTime = 1f / time;
	}

	@Override
	public final boolean start(SceneManager sceneManager) {
		init(sceneManager);
		return true;
	}
	
	public abstract void init(SceneManager sceneManager);

	@Override
	public final boolean update(SceneManager sceneManager, float delta) {
		
		progress += invTime * delta;
		if(progress >= 1) {
			finish(sceneManager);
			return false;
		}
		
		updateProgress(sceneManager, progress);
		
		return true;
	}
	
	public abstract void updateProgress(SceneManager sceneManager, float progress);

	@Override
	public final void skip(SceneManager sceneManager) {
		finish(sceneManager);
	}
	
	public abstract void finish(SceneManager sceneManager);
}