package compiler.lexico;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import utils.TokenType;

public class Scanner {
	
	private int state;
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

			switch (state) {
				case 0:
					if(isLetter(currentChar)) {
						content += currentChar;
						state = 1;
					}
					else if (isNumber(currentChar)) {
						content += currentChar;
						state = 2;
					}
					else {
						throw new RuntimeException("Unrecognized Symbol");
					}
					break;
				case 1:
					if(isLetter(currentChar) || isNumber(currentChar)) {
						content += currentChar;
					}
					else if(isInvalid(currentChar)) {
						throw new RuntimeException("Lexical Error: Unrecognized symbol");
					}
					else {
						tk = new Token(TokenType.IDENTIFIER, content);
						return tk;
					}
					break;
				case 2:
					if(isNumber(currentChar)) {
						content += currentChar;
					}
					else if(isOperator(currentChar) || isSpace(currentChar) || isAssign(currentChar) || isEOF()) {
						tk = new Token(TokenType.NUMBER, content);
						return tk;
					}
					else {
						throw new RuntimeException("Malformed Number");
					}
					break;
			}
		}
	}
	
	private void back() {
		this.pos--;
		
	}

	private boolean isEOF() {
		if(this.pos >= this.contentBuffer.length) {
			return true;
		}
		return false;
	}
	
	private boolean isInvalid(char c) {
		return !isLetter(c) && !isNumber(c) && !isSpace(c) && !isAssign(c) && !isOperator(c) && !isEOF();
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
	private char nextChar() {
		if(isEOF()) {
			return '\0';
		}
		return this.contentBuffer[pos++];
	}

}
