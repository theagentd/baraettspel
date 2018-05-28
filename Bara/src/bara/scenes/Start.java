package bara.scenes;

import static bara.ScriptFunctions.*;

import bara.character.Person;

public class Start implements Scene {

	@Override
	public String play() {
		
		setBackground("test");
		
		Person kawaiichan = createPerson("kawaii-chan", 0.75f);
		kawaiichan.setPosition(400, 400);
		kawaiichan.setSprite("happy", false);
		
		fade(1, 2);
		
		text("Hello.");
		
		kawaiichan.fade(1.0f, 1.5f);
		
		text("My name is Kawaii-chan and I'm a fujoshi.");
		
		text("F U J O S H I", 4f); //slow-mo text

		text("That means I'm 12 and write edgy shit on my blog with crazy-ass smileys everywhere <3 >__< >///< .__. i can't even");
		
		kawaiichan.moveTo(-400, 400, 0.75f);
		kawaiichan.setSprite("surprised", true);
		text("Holy balls, I can move.");
		text("And I also turned around! Magic!");
		text("What if I say something really annoyingly long? Will it start line wrapping correctly?");
		

		kawaiichan.setSprite("happy", true);
		text("Yep! It seems to be working!");

		text("Sadly I have to go now.");

		kawaiichan.setSprite("sad", true);
		text("#sadface");
		
		text("Bye bye!");
		fade(0.2f, 1.5f);
		fade(1f, 0.2f);
		text("Wait a second! I have one last question:");
		text("do yu kno da waeeee, boiiiii");
		
		text("ANGRY DANCE PARTY");
		for(int i = 0; i < 20; i++) {
			kawaiichan.setSprite("upset", true);
			fade(0.5f, 0.15f);
			
			kawaiichan.setSprite("upset", false);
			fade(1f, 0.15f);
		}
		
		
		kawaiichan.setSprite("sad", false);
		text("wow, that was annoying");
		text("Bye bye!");
		
		fade(0, 2);
		
		
		return null;
	}
}
