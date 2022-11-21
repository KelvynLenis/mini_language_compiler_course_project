package compiler.lexico;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import utils.TokenType;
import utils.ReservedWordsType;

public class Scanner {
	
	private String state;
	private int row = 1, column = 0;
	private int pos;
	private char[] contentBuffer;
	private boolean debug_mode = false;
	
	public Scanner(String filename) {
		try {
			String contentTxt = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			this.contentBuffer = contentTxt.toCharArray();
			this.pos = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Token nextToken() {
		Token tk;
		String content = "";
		this.state = "base";
		char currentChar;

		if(isEOF()) {
			return null;
		}

		while(true) {
			currentChar = nextChar();

			boolean isCurrentCharInvalid = isInvalid(currentChar);

			if(isNewLine(currentChar)){
				row++;
				column=0;
			}

			switch (state) {
				case "base":
					column++;
					if(isLetter(currentChar) || isUnderscore(currentChar)) {
						content += currentChar;
						state = "identifier";
					}
					else if (isNumber(currentChar)) {
						content += currentChar;
						state = "number";
					}
					else if(isBreakLine(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.BREAK_LINE, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if(currentChar == '.'){
						content += "0.";
						state = "float";
					}
					else if(isCommentary(currentChar)){
						state = "commentary";
					}
					else if (isOpenParanthesis(currentChar)){
						content += currentChar;

						tk = new Token(TokenType.OPEN_PARENTHESIS, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if (isCloseParanthesis(currentChar)){
						content += currentChar;
						
						tk = new Token(TokenType.CLOSE_PARENTHESIS, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if (isSpace(currentChar)){
						if (isNewLine(currentChar))
							row--;
						break;
					}
					else if(isMathOperator(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.MATH, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if(isAssign(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.ASSIGN, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if(isOperator(currentChar)){
						content += currentChar;
						state = "relational";
					}
					else {
						throw new RuntimeException("Unrecognized Symbol at row " + row + " column " + column);
					}
					break;
				case "identifier":
					if(!isNewLine(currentChar)){
						column++;
					}

					if(isLetter(currentChar) || isNumber(currentChar) || isUnderscore(currentChar)) {
						content += currentChar;
					}
					else if(isBreakLine(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if (isMathOperator(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;	
					}
					else if(isCloseParanthesis(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;	
					}
					else if(isOpenParanthesis(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;	
					}
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						state = "commentary";
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;	
					}
					else if((isSpace(currentChar) || isAssign(currentChar) || isOperator(currentChar)) && currentChar != '\n'){
						if(isReservedWord(content)){
							tk = new Token(TokenType.RESERVED_WORD, content);
							if(debug_mode) {
								System.out.println(tk);
							}
							return tk;	
						}
						
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}	
					else if(isLastChar(currentChar)){
						if(isLastCharInvalid(currentChar)){
							throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " column " + column);
						}
						else if(isReservedWord(content)){
							tk = new Token(TokenType.RESERVED_WORD, content);
							if(debug_mode) {
								System.out.println(tk);
							}
							return tk;	
						}
						else if(isCloseParanthesis(currentChar)){
							tk = new Token(TokenType.IDENTIFIER, content);
							back();
							if(debug_mode) {
								System.out.println(tk);
							}
							return tk;	
						}
						else if(isOpenParanthesis(currentChar)){
							tk = new Token(TokenType.IDENTIFIER, content);
							back();
							if(debug_mode) {
								System.out.println(tk);
							}
							return tk;	
						}
						
						tk = new Token(TokenType.IDENTIFIER, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if(isCurrentCharInvalid) {
						throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " column " + column);
					}
					else {
						if(isReservedWord(content)){
							tk = new Token(TokenType.RESERVED_WORD, content);
							if(debug_mode) {
								System.out.println(tk);
							}
							return tk;	
						}

						tk = new Token(TokenType.IDENTIFIER, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					break;
				case "number":
					if(!isNewLine(currentChar)){
						column++;
					}

					if(isNumber(currentChar)) {
						content += currentChar;
					}
					else if(currentChar == '.'){
						content += currentChar;
						state = "float";
					}
					else if(isBreakLine(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.NUMBER, content);
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if (isMathOperator(currentChar)){
						tk = new Token(TokenType.NUMBER, content);
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;	
					}
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.NUMBER, content);
						state = "commentary";
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;	
					}
					else if(isOperator(currentChar)){
						tk = new Token(TokenType.NUMBER, content);
						back();
						state = "relational";
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if(isAssign(currentChar)){
						tk = new Token(TokenType.NUMBER, content);
						back();
						state = "base";
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if(isSpace(currentChar)) {
						tk = new Token(TokenType.NUMBER, content);
						back();
						state = "base";
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if(isEOF() && currentChar != '\0'){
						if(isLastCharInvalid(currentChar)){
							throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " column " + column);
						}
					}
					else if(isEOF()){
						tk = new Token(TokenType.NUMBER, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else {
						throw new RuntimeException("Malformed Number at row " + row + " column " + column);
					}
					break;
				case "commentary":
					if(isNewLine(currentChar) || currentChar == '\0'){
						state = "base";
						tk = new Token(TokenType.COMMENTARY, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					break;
				case "float":
					if(!isNewLine(currentChar)){
						column++;
					}

					if(isNumber(currentChar)) {
						content += currentChar;
					}
					else if(isLetter(currentChar)){
						throw new RuntimeException("Malformed Number at row " + row + " column " + column);
					}
					else if(isBreakLine(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.FLOAT, content);
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if (isMathOperator(currentChar)){
						content += "0";
						tk = new Token(TokenType.FLOAT, content);
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;	
					}
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.FLOAT, content);
						state = "commentary";
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;	
					}
					else if(isEOF() && currentChar != '\0'){
						if(isLastCharInvalid(currentChar)){
							throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " column " + column);
						}
					}
					else if(isOperator(currentChar) || isSpace(currentChar) || isAssign(currentChar) || isEOF()) {
						if(content.endsWith(".")){
							content += "0";
						}
						tk = new Token(TokenType.FLOAT, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else {
						throw new RuntimeException("Malformed Number at row " + row + " column " + column);
					}
					break;
				case "relational":
					if(!isNewLine(currentChar)){
						column++;
					}	

					if (isAssign(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.RELATIONAL, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.RELATIONAL, content);
						state = "commentary";
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;	
					}
					else if(isBreakLine(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.RELATIONAL, content);
						back();
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else if(isLetter(currentChar) || isNumber(currentChar) || isSpace(currentChar) || 
							isOperator(currentChar) || isOpenParanthesis(currentChar) || isCloseParanthesis(currentChar) || 
							isUnderscore(currentChar) || isMathOperator(currentChar) || isEOF() || currentChar == '.'){
						tk = new Token(TokenType.RELATIONAL, content);
						if(debug_mode) {
							System.out.println(tk);
						}
						return tk;
					}
					else{
						throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " column " + column);
					}
			}
		}
	}
	
	private void back() {
		this.column--;
		this.pos--;
		
	}
	
	private boolean isInvalid(char c) {
		return !isLetter(c) && !isNumber(c) && !isSpace(c) && !isAssign(c) && 
					!isOperator(c) && !isEOF() && !isOpenParanthesis(c) && 
					!isCloseParanthesis(c) && !isCommentary(c) && !isUnderscore(c) && !isMathOperator(c);
	}

	private boolean isLastCharInvalid(char c){
		return !isLetter(c) && !isNumber(c) && !isSpace(c) && !isAssign(c) && 
					!isOperator(c) && !isOpenParanthesis(c) && !isCloseParanthesis(c) && 
					!isUnderscore(c) && !isMathOperator(c);

	}

	private boolean isLastChar(char c){
		return isEOF() && c != '\0';
	}

	private boolean isEOF() {
		if(this.pos >= this.contentBuffer.length) {
			return true;
		}
		return false;
	}
	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	private boolean isUnderscore(char c){
		return c == '_';
	}
	private boolean isMathOperator(char c){
		return c == '+' || c == '-' || c == '*' || c == '/';
	}
	private boolean isNumber(char c) {
		return c >= '0' && c <= '9';
	}
	private boolean isAssign(char c) {
		return c == '=';
	}
	private boolean isOperator(char c) {
		return c == '>' || c == '<' || c == '!'; 
	}
	private boolean isSpace(char c) {
		return c == ' ' || c == '\n' || c == '\t' || c == '\r';
	}
	private boolean isOpenParanthesis(char c){
		return c == '(';
	}
	private boolean isCloseParanthesis(char c){
		return c == ')';
	}
	private boolean isReservedWord(String generatedToken){
		ReservedWordsType reservedWordsTypes = new ReservedWordsType();
		return reservedWordsTypes.hasToken(generatedToken);
		// return generatedToken.equals("int") || 
		// 			generatedToken.equals("float") || 
		// 			generatedToken.equals("print") || 
		// 			generatedToken.equals("if") || 
		// 			generatedToken.equals("else");
	}
	private boolean isCommentary(char c){
		return c == '#';
	}

	private boolean isNewLine(char c) {
		return c == '\n' || c == '\r';
	}

	private boolean isBreakLine(char c){
		return c == ';';
	}

	private char nextChar() {
		if(isEOF()) {
			return '\0';
		}
		return this.contentBuffer[pos++];
	}

}
