package bara.scenes;

import bara.Bara;

public class Start implements Scene {

	@Override
	public String play(Bara bara) {
		
		bara.text("Hello!");
		
		return "Talia";
	}
}
