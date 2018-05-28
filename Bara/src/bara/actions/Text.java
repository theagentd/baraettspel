package bara.actions;

import bara.SceneManager;

public class Text implements Action{

	private String text;
	private float lettersPerSecond;
	
	private float progress;
	
	
	public Text(String text, float lettersPerSecond) {
		this.text = text;
		this.lettersPerSecond = lettersPerSecond;
		progress = 0;
	}

	@Override
	public boolean start(SceneManager sceneManager) {
		return true;
	}

	@Override
	public boolean update(SceneManager sceneManager, float delta) {
		
		progress = Math.min(text.length(), progress + lettersPerSecond * delta);
		
		sceneManager.setText(text, (int)progress);
		if(progress == text.length()) {
			return false;
		}
		return true;
	}

	@Override
	public void skip(SceneManager sceneManager) {
		sceneManager.setText(text, text.length());
	}
}