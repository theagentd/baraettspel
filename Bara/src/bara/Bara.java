package bara;

import java.util.Scanner;

public class Bara {
	
	private Scanner scanner;
	
	public Bara() {
		scanner = new Scanner(System.in);
	}
	
	public void text(String text) {
		
		for(int i = 0; i < text.length(); i++) {
			System.out.print(text.charAt(i));
			wait(50);
		}
		System.out.println();
		
		waitForConfirmation();
		
	}
	
	public boolean didUserWrite(String text) {
		return scanner.nextLine().equals(text);
	}

	private void wait(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void waitForConfirmation() {
		scanner.nextLine();
	}
}
