package bara.actions;

import bara.SceneManager;
import bara.Util;

public class Fade extends TimedAction {
	
	private float startAlpha, targetAlpha;
	
	public Fade(float targetAlpha, float time) {
		super(time);
		this.targetAlpha = targetAlpha;
	}

	@Override
	public void init(SceneManager sceneManager) {
		startAlpha = sceneManager.getFaderAlpha();
	}

	@Override
	public void updateProgress(SceneManager sceneManager, float progress) {
		sceneManager.setFaderAlpha(Util.smoothmix(startAlpha, targetAlpha, progress));
	}

	@Override
	public void finish(SceneManager sceneManager) {
		sceneManager.setFaderAlpha(targetAlpha);
	}
}