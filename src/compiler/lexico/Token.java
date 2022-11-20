package compiler.lexico;

import utils.TokenType;

public class Token {
	private TokenType type;
	private String content;
	private int line;
	private int column;
	
	
	public Token(TokenType type, String content) {
		super();
		this.type = type;
		this.content = content;
	}

	private Token() {
		super();
	}

	public TokenType getType() {
		return type;
	}
	public void setType(TokenType type) {
		this.type = type;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Token [type=" + type + ", content=" + content + "]";
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	
}
