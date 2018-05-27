package bara.scenes;

import bara.Bara;

public class Start implements Scene {

	@Override
	public String play(Bara bara) {
		
		bara.text("Hello!");
		bara.text("This is a Bara game!");
		bara.text("Ewwwwww");
		bara.text("Talia, du är ful");
		bara.text("Robert är ful också!");
		bara.text("AWAWAWAWAWAWA");
		bara.text("och nu börjar det om från början igen!!!");
		
		return "Start";
	}
}
