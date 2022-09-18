package compiler.lexico;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import utils.TokenType;

public class Scanner {
	
	private int state;
	private int row = 1, column = 0;
	private int pos;
	private char[] contentBuffer;
	
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
		this.state = 0;
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
				case 0:
					column++;
					if(isLetter(currentChar) || isUnderscore(currentChar)) {
						content += currentChar;
						state = 1;
					}
					else if (isNumber(currentChar)) {
						content += currentChar;
						state = 2;
					}
					else if(currentChar == '.'){
						content += "0.";
						state = 4;
					}
					else if(isCommentary(currentChar)){
						state = 3;
					}
					else if (isOpenParanthesis(currentChar)){
						content += currentChar;

						tk = new Token(TokenType.OPEN_PARENTHESIS, content);
						return tk;
					}
					else if (isCloseParanthesis(currentChar)){
						content += currentChar;
						
						tk = new Token(TokenType.CLOSE_PARENTHESIS, content);
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
						return tk;
					}
					else if(isAssign(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.ASSIGN, content);
						return tk;
					}
					else if(isOperator(currentChar)){
						content += currentChar;
						state = 5;
					}
					else {
						throw new RuntimeException("Unrecognized Symbol at row " + row + " column " + column);
					}
					break;
				case 1:
					if(!isNewLine(currentChar)){
						column++;
					}

					if(isLetter(currentChar) || isNumber(currentChar) || isUnderscore(currentChar)) {
						content += currentChar;
					}
					else if (isMathOperator(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						return tk;	
					}
					else if(isCloseParanthesis(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						return tk;	
					}
					else if(isOpenParanthesis(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						return tk;	
					}
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						state = 3;
						back();
						return tk;	
					}
					else if((isSpace(currentChar) || isAssign(currentChar) || isOperator(currentChar)) && currentChar != '\n'){
						if(isReservedWord(content)){
							tk = new Token(TokenType.RESERVED_WORD, content);
							return tk;	
						}
						
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						return tk;
					}	
					else if(isLastChar(currentChar)){
						if(isLastCharInvalid(currentChar)){
							throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " column " + column);
						}
						else if(isReservedWord(content)){
							tk = new Token(TokenType.RESERVED_WORD, content);
							return tk;	
						}
						else if(isCloseParanthesis(currentChar)){
							tk = new Token(TokenType.IDENTIFIER, content);
							back();
							return tk;	
						}
						else if(isOpenParanthesis(currentChar)){
							tk = new Token(TokenType.IDENTIFIER, content);
							back();
							return tk;	
						}
						
						tk = new Token(TokenType.IDENTIFIER, content);
						return tk;
					}
					else if(isCurrentCharInvalid) {
						throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " column " + column);
					}
					else {
						if(isReservedWord(content)){
							tk = new Token(TokenType.RESERVED_WORD, content);
							return tk;	
						}

						tk = new Token(TokenType.IDENTIFIER, content);
						return tk;
					}
					break;
				case 2:
					if(!isNewLine(currentChar)){
						column++;
					}

					if(isNumber(currentChar)) {
						content += currentChar;
					}
					else if(currentChar == '.'){
						content += currentChar;
						state = 4;
					}
					else if (isMathOperator(currentChar)){
						tk = new Token(TokenType.NUMBER, content);
						back();
						return tk;	
					}
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.NUMBER, content);
						state = 3;
						back();
						return tk;	
					}
					else if(isOperator(currentChar)){
						tk = new Token(TokenType.NUMBER, content);
						back();
						state = 5;
						return tk;
					}
					else if(isAssign(currentChar)){
						tk = new Token(TokenType.NUMBER, content);
						back();
						state = 0;
						return tk;
					}
					else if(isSpace(currentChar)) {
						tk = new Token(TokenType.NUMBER, content);
						back();
						state = 0;
						return tk;
					}
					else if(isEOF() && currentChar != '\0'){
						if(isLastCharInvalid(currentChar)){
							throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " column " + column);
						}
					}
					else if(isEOF()){
						tk = new Token(TokenType.NUMBER, content);
						return tk;
					}
					else {
						throw new RuntimeException("Malformed Number at row " + row + " column " + column);
					}
					break;
				case 3:
					if(isNewLine(currentChar) || currentChar == '\0'){
						state = 0;
						tk = new Token(TokenType.COMMENTARY, content);
						return tk;
					}
					break;
				case 4:
					if(!isNewLine(currentChar)){
						column++;
					}

					if(isNumber(currentChar)) {
						content += currentChar;
					}
					else if(isLetter(currentChar)){
						throw new RuntimeException("Malformed Number at row " + row + " column " + column);
					}
					else if (isMathOperator(currentChar)){
						content += "0";
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						return tk;	
					}
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.FLOAT, content);
						state = 3;
						back();
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
						return tk;
					}
					else {
						throw new RuntimeException("Malformed Number at row " + row + " column " + column);
					}
					break;
				case 5:
					if(!isNewLine(currentChar)){
						column++;
					}	

					if (isAssign(currentChar)){
						content += currentChar;
						tk = new Token(TokenType.RELATIONAL, content);
						return tk;
					}
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.RELATIONAL, content);
						state = 3;
						back();
						return tk;	
					}
					else if(isLetter(currentChar) || isNumber(currentChar) || isSpace(currentChar) || 
							isOperator(currentChar) || isOpenParanthesis(currentChar) || isCloseParanthesis(currentChar) || 
							isUnderscore(currentChar) || isMathOperator(currentChar) || isEOF() || currentChar == '.'){
						tk = new Token(TokenType.RELATIONAL, content);
						back();
						return tk;
					}
					else{
						throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " column " + column);
					}
					// break;
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
		return generatedToken.equals("int") || generatedToken.equals("float") || generatedToken.equals("print") || generatedToken.equals("if") || generatedToken.equals("else");
	}
	private boolean isCommentary(char c){
		return c == '#';
	}

	private boolean isNewLine(char c) {
		return c == '\n' || c == '\r';
	}

	private char nextChar() {
		if(isEOF()) {
			return '\0';
		}
		return this.contentBuffer[pos++];
	}

}
