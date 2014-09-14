package com.sankar.drawguess.mocks;

import com.sankar.drawguess.api.IWordProvider;

public class MockWordProvider implements IWordProvider {
	
	private int indx = 0;

	@Override
	public String nextWord() {
		return (indx++ % 2) == 0 ? "word1" : "word2";
	}
	
	public int countOfWordsWorvided() {
		return indx;
	}

}
