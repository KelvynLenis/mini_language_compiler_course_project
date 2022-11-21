package compiler.syntax;

import compiler.exceptions.SyntaxException;
import compiler.lexico.Scanner;
import compiler.lexico.Token;
import utils.ReservedWordsType;
import utils.TokenType;

public class Parser {
	Scanner scanner;
	Token token;
	ReservedWordsType reservedWordsTypes = new ReservedWordsType();
	
	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}

	public void PROGRAMA(){
		token = scanner.nextToken();
		if(token.getContent().equals("begin")){
			this.DECLARACOES();
			
			this.ALGORITMO();
			
			token = scanner.nextToken();
			if(!token.getContent().equals("end")){
				throw new SyntaxException("ALGORITMO was never closed! Expected keyword end");
			}
		}
	}

	private void DECLARACOES(){
		this.DECLARATION_lIST();
	}

	private void DECLARATION_lIST(){
		this.DECLARATION();
		token = scanner.nextToken();

		if(token.getContent().equals("do")){
			return;
		}
		this.DECLARATION_lIST();
	}

	private void DECLARATION(){
		token = scanner.nextToken();

		if(token.getType() != TokenType.RESERVED_WORD && !reservedWordsTypes.getType(token.getContent()).equals("varType")){
			throw new SyntaxException("Variable Type expected, found "+
			token.getType() + " ("+token.getContent()+")  at LINE " +
			token.getLine() + " and COLUMN "+ token.getColumn());

		}
		
		token = scanner.nextToken();
		if(token.getType() != TokenType.IDENTIFIER){
			throw new SyntaxException("Identifier expected, found "+
			token.getType() + " ("+token.getContent()+")  at LINE " +
			token.getLine() + " and COLUMN "+ token.getColumn());
		}

		token = scanner.nextToken();
		if(!token.getContent().equals(";")){
			throw new SyntaxException("expected ; at the end of the line, found "+
			token.getType() + " ("+token.getContent()+")  at LINE " +
			token.getLine() + " and COLUMN "+ token.getColumn());
		}
	}

	private void ALGORITMO(){
		
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
		if(token.getType() != TokenType.MATH) {
			throw new SyntaxException("Operator expected, found "+token.getType()+" ("+token.getContent()+") at LINE "+
					token.getLine()+" and COLUMN "+ token.getColumn());
		}
	}
}






