package compiler.syntax;

import compiler.exceptions.SyntaxException;
import compiler.lexical.Scanner;
import compiler.lexical.Token;
import utils.TokenType;

public class Parser {
	Scanner scanner;
	Token token;
	
	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public void E() {
		this.T();
		this.El();
	}
	
	private void T() {
		token = scanner.nextToken();
		if(token.getType() != TokenType.IDENTIFIER && 
				token.getType() != TokenType.NUMBER) {
			throw new SyntaxException("Identifier or Number expected, found "+token.getType()+ 
					" ("+token.getContent()+")  at LINE " +
					token.getLine()+ " and COLUMN "+ token.getColumn());
		}
	}
	
	private void El() {
		token = scanner.nextToken();
		if(token != null) {
			this.OP();
			this.T();
			this.El();
		}
	}
	
	private void OP() {
		if(token.getType() != TokenType.OPERATOR) {
			throw new SyntaxException("Operator expected, found "+token.getType()+" ("+token.getContent()+") at LINE "+
					token.getLine()+" and COLUMN "+ token.getColumn());
		}
	}
}






