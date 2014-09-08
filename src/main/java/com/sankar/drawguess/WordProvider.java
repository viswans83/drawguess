package com.sankar.drawguess;

import java.util.ArrayList;
import java.util.List;

import com.sankar.drawguess.api.IWordProvider;

public class WordProvider implements IWordProvider {
	
	private List<String> dictionary = new ArrayList<String>();
	
	private int nextWordIndex;
	
	public WordProvider() {
		nextWordIndex = 0;
		
		dictionary.add("Chicken");
		dictionary.add("Rock");
		dictionary.add("Camera");
		dictionary.add("Book");
		dictionary.add("Rabbit");
		dictionary.add("Arm");
		dictionary.add("Arm");
		dictionary.add("Crayon");
		dictionary.add("Jump");
		dictionary.add("Pig");
		dictionary.add("Monkey");
		dictionary.add("Baby");
		dictionary.add("Happy");
		dictionary.add("Hopscotch");
		dictionary.add("Spider");
		dictionary.add("Bird");
		dictionary.add("Doll");
		dictionary.add("Wings");
		dictionary.add("Turtle");
		dictionary.add("Room");
		dictionary.add("Drum");
		dictionary.add("Ear");
		dictionary.add("Cheek");
		dictionary.add("Smile");
		dictionary.add("Jar");
		dictionary.add("Chin");
		dictionary.add("Telephone");
		dictionary.add("Mouth");
		dictionary.add("Basketball");
		dictionary.add("Tail");
		dictionary.add("Airplane");
		dictionary.add("Tree");
		dictionary.add("Star");
		dictionary.add("Point");
		dictionary.add("Scissors");
		dictionary.add("Elephant");
		dictionary.add("Jump");
		dictionary.add("Chair");
		dictionary.add("Pinch");
		dictionary.add("Mosquito");
		dictionary.add("Sunglasses");
		dictionary.add("Head");
		dictionary.add("Kick");
		dictionary.add("Football");
		dictionary.add("Skip");
		dictionary.add("Dance");
		dictionary.add("Alligator");
		dictionary.add("Stop");
		dictionary.add("Door");
		dictionary.add("Blinking");
	}
	
	@Override
	public String nextWord() {
		if (nextWordIndex == dictionary.size())
			nextWordIndex = 0;
		
		return dictionary.get(nextWordIndex++);
	}

}
