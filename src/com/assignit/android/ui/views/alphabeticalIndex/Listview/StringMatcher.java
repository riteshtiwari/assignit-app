package com.assignit.android.ui.views.alphabeticalIndex.Listview;
/**
 * String Matching for Indexable ListView
 * 
 * @author Innoppl
 * 
 */
public class StringMatcher {

	private final static char KOREAN_UNICODE_START = '가';
	private final static char KOREAN_UNICODE_END = '힣';
	private final static char KOREAN_UNIT = '까' - '가';
	private final static char[] KOREAN_INITIAL = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ',
			'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ',
			'ㅎ' };

	/**
	 * Method to match the list with selected string
	 * 
	 * @param value
	 * @param keyword
	 * @return
	 */
	public static boolean match(String value, String keyword) {
		if (value == null || keyword == null)
			return false;
		if (keyword.length() > value.length())
			return false;

		int i = 0, j = 0;
		do {
			if (isKorean(value.charAt(i)) && isInitialSound(keyword.charAt(j))) {
				if (keyword.charAt(j) == getInitialSound(value.charAt(i))) {
					i++;
					j++;
				} else if (j > 0)
					break;
				else
					i++;
			} else {
				if (keyword.charAt(j) == value.charAt(i)) {
					i++;
					j++;
				} else if (j > 0)
					break;
				else
					i++;
			}
		} while (i < value.length() && j < keyword.length());

		return (j == keyword.length()) ? true : false;
	}

	/**
	 * Method to find is korean or not
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isKorean(char c) {
		if (c >= KOREAN_UNICODE_START && c <= KOREAN_UNICODE_END)
			return true;
		return false;
	}

	/**
	 * Method to initialise the sound
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isInitialSound(char c) {
		for (char i : KOREAN_INITIAL) {
			if (c == i)
				return true;
		}
		return false;
	}

	/**
	 * Method to initialise the sound
	 * 
	 * @param c
	 * @return
	 */
	private static char getInitialSound(char c) {
		return KOREAN_INITIAL[(c - KOREAN_UNICODE_START) / KOREAN_UNIT];
	}
}
