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
					if(isLetter(currentChar)) {
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
					else {
						throw new RuntimeException("Unrecognized Symbol at row " + row + " colum " + column);
					}
					break;
				case 1:
					if(!isNewLine(currentChar)){
						column++;
					}

					if(isLetter(currentChar) || isNumber(currentChar)) {
						content += currentChar;
					}
					else if(isCloseParanthesis(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						back();
						column--;
						return tk;	
					}
					else if(isOpenParanthesis(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						column--;
						back();
						return tk;	
					}
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.IDENTIFIER, content);
						state = 3;
						back();
						return tk;	
					}
					else if(isLastChar(currentChar)){
						if(isLastCharInvalid(currentChar)){
							throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " colum " + column);
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
						throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " colum " + column);
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
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.NUMBER, content);
						state = 3;
						back();
						return tk;	
					}
					else if(isEOF() && currentChar != '\0'){
						if(isLastCharInvalid(currentChar)){
							throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " colum " + column);
						}
					}
					else if(isOperator(currentChar) || isSpace(currentChar) || isAssign(currentChar) || isEOF()) {
						tk = new Token(TokenType.NUMBER, content);
						return tk;
					}
					else {
						throw new RuntimeException("Malformed Number at row " + row + " colum " + column);
					}
					break;
				case 3:
					if(isNewLine(currentChar)){
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
					else if(isCommentary(currentChar)){
						tk = new Token(TokenType.FLOAT, content);
						state = 3;
						back();
						return tk;	
					}
					else if(isEOF() && currentChar != '\0'){
						if(isLastCharInvalid(currentChar)){
							throw new RuntimeException("Lexical Error: Unrecognized symbol at at row " + row + " colum " + column);
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
						throw new RuntimeException("Malformed Number at row " + row + " colum " + column);
					}
					break;
			}
		}
	}
	
	private void back() {
		this.pos--;
		
	}
	
	private boolean isInvalid(char c) {
		return !isLetter(c) && !isNumber(c) && !isSpace(c) && !isAssign(c) && 
					!isOperator(c) && !isEOF() && !isOpenParanthesis(c) && 
					!isCloseParanthesis(c) && !isCommentary(c);
	}

	private boolean isLastCharInvalid(char c){
		return !isLetter(c) && !isNumber(c) && !isSpace(c) && !isAssign(c) && !isOperator(c) && !isOpenParanthesis(c) && !isCloseParanthesis(c);

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
	// 5) a.
	private boolean isOpenParanthesis(char c){
		return c == '(';
	}
	// 5) b.
	private boolean isCloseParanthesis(char c){
		return c == ')';
	}
	// 7) a)-e)
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
