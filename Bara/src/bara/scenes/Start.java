package bara.scenes;

import bara.Bara;

public class Start implements Scene {

	@Override
	public String play(Bara bara) {
		
		bara.text("Hello!");
		bara.text("This is a Bara game!");
		bara.text("Ewwwwww");
		bara.text("Talia, du �r ful");
		bara.text("Robert �r ful ocks�!");
		bara.text("AWAWAWAWAWAWA");
		bara.text("och nu b�rjar det om fr�n b�rjan igen!!!");
		
		return "Start";
	}
}
