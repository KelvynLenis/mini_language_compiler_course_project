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
		this.COMMAND_LIST();
	}

	private void COMMAND_LIST(){
		this.COMMAND();
	}

	private void 	COMMAND(){
		this.ASSIGN_COMMAND();
		this.OUTPUT_COMMAND();
		this.CONDITION_COMMAND();
		this.LOOP_COMMAND();
	}

	private void ASSIGN_COMMAND() {
		// TODO
		// 'VARIAVEL' =
		this.ARITHMETIC_EXPRESSION();
	}

	private void OUTPUT_COMMAND() {
		// TODO
		// 'PRINT' (VARIAVEL | CADEIA)
	}

	private void CONDITION_COMMAND() {
		// TODO
		// IF
		this.RELATIONAL_EXPRESSION();
		// THEN
		this.COMMAND();
		// ELSE
		this.COMMAND();
	}

	private void LOOP_COMMAND() {
		// TODO
		// WHILE
		this.RELATIONAL_EXPRESSION();
		this.COMMAND();
	}

	private void ARITHMETIC_EXPRESSION3(){
		if(token.getContent() == "+" ||
		   token.getContent() == "-"){
			this.ARITHMETIC_TERM();
		}
	}

	private void ARITHMETIC_EXPRESSION2(){
		token = scanner.nextToken();
		if (token != null){
			this.ARITHMETIC_EXPRESSION3();
			this.ARITHMETIC_EXPRESSION2();
		}
	}

	private void ARITHMETIC_EXPRESSION(){
		this.ARITHMETIC_TERM();
		this.ARITHMETIC_EXPRESSION2();		
	}

	private void ARITHMETIC_FACTOR(){
		token = scanner.nextToken();
		if (token.getType() != TokenType.NUMBER &&
			token.getType() != TokenType.FLOAT &&
			token.getType() != TokenType.IDENTIFIER &&
			token.getType() != TokenType.OPEN_PARENTHESIS){
				throw new SyntaxException("Identifier or Integer or Float or Open Parenthesis expected, found "+token.getType()+ 
					" ("+token.getContent()+")  at LINE " +
					token.getLine()+ " and COLUMN "+ token.getColumn());
		}

		if (token.getType() == TokenType.OPEN_PARENTHESIS){
			this.ARITHMETIC_EXPRESSION();
			token = scanner.nextToken();
			if (token.getType() != TokenType.CLOSE_PARENTHESIS){
				throw new SyntaxException("Close parenthesis expected, found "+token.getType()+ 
					" ("+token.getContent()+")  at LINE " +
					token.getLine()+ " and COLUMN "+ token.getColumn());
			}
		}
	}

	private void ARITHMETIC_TERM2(){
		token = scanner.nextToken();
		if(token != null) {
			if(token.getContent() == "*" ||
			   token.getContent() == "/"){
				this.ARITHMETIC_FACTOR();
				this.ARITHMETIC_TERM2();
			}
			else{
				throw new SyntaxException("* or / expected, found"+token.getType()+ 
				" ("+token.getContent()+")  at LINE " +
				token.getLine()+ " and COLUMN "+ token.getColumn());
			}
		}
	}

	private void ARITHMETIC_TERM() {
		this.ARITHMETIC_FACTOR();
		this.ARITHMETIC_TERM2();
	}

	private void RELATIONAL_EXPRESSION() {
		// TODO
		this.RELATIONAL_TERM();
	}

	private void RELATIONAL_TERM(){
		
		this.ARITHMETIC_EXPRESSION();
	}

	//////////////////////////////////////
	
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






