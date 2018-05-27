package bara.scenes;

import bara.Bara;

public class Talia implements Scene {

	@Override
	public String play(Bara bara) {
		
		bara.text("Hello boii!");
		
		return "Start";
	}
}
