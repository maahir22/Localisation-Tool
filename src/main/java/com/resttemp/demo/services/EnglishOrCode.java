package com.resttemp.demo.services;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


@Service
public class EnglishOrCode {
 
	private static Tokenizer tokenizer = null;
 
	public static void initializeTokenizer() {
		tokenizer = new Tokenizer();
 
		//key words
		String keyString = "abstract assert boolean break byte case catch "
				+ "char class const continue default do double else enum"
				+ " extends false final finally float for goto if implements "
				+ "import instanceof int interface long native new null "
				+ "package private protected public return short static "
				+ "strictfp super switch synchronized this throw throws true "
				+ "transient try void volatile while todo";
		String[] keys = keyString.split(" ");
		String keyStr = StringUtils.join(keys, "|");
 
		tokenizer.add(keyStr, 1);
		tokenizer.add("\\(|\\)|\\{|\\}|\\[|\\]|;|,|\\.|=|>|<|!|~|"
						+ "\\?|:|==|<=|>=|!=|&&|\\|\\||\\+\\+|--|"
						+ "\\+|-|\\*|/|&|\\||\\^|%|\'|\"|\n|\r|\\$|\\#",
						2);//separators, operators, etc
 
		tokenizer.add("[0-9]+", 3); //number
		tokenizer.add("[a-zA-Z][a-zA-Z0-9_]*", 4);//identifier
		tokenizer.add("@", 4);
	}
 
	public static Boolean tokenCoder(String input) throws SQLException, ClassNotFoundException, IOException {
		initializeTokenizer();
		if(isEnglish(input)){
			return false;
		}else{
			return true;
		} 
	}
	
	public Boolean RegEXEvaluation(String string) {
		String[] words = string.split("\\b");
		int nonText = 0;
		for (String word : words) {
			if (!word.matches("^[A-Za-z][a-z]*|[0-9]+(.[0-9]+)?|[ .,]|. $")) {
				nonText++;
			}
		}
		System.out.print("\n");
		double percentage = ((double) nonText) / words.length;
		System.out.println(percentage);
		if (percentage > .2) {
			return true;
		}
		if (percentage < .1) {
			return false;
		}
		return false;
	}
	
	private static boolean isEnglish(String replaced) {
		tokenizer.tokenize(replaced);
		String patternString = tokenizer.getTokensString();
 
		if(patternString.matches(".*444.*") || patternString.matches("4+")){
			return true;
		}else{
			return false;
		}
	}
}
