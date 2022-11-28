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
		if(!token.getContent().equals("begin")){
			throw new SyntaxException("Expected keyword begin!");
		}

		this.DECLARACOES();
		this.ALGORITMO();
	}

	private void DECLARACOES(){
		token = scanner.nextToken();

		if(token.getContent().equals("do")){
			throw new SyntaxException("You must define at least one variable!");
		}

		this.DECLARATION();

		if(token.getContent().equals("do")){
			return;
		}
		this.DECLARATION_lIST();
	}

	private void DECLARATION_lIST(){
		token = scanner.nextToken();

		if(token.getContent().equals("do")){
			return;
		}

		this.DECLARATION();

		this.DECLARATION_lIST();
	}

	private void DECLARATION(){

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
		if(token.getType() != TokenType.BREAK_LINE){
			throw new SyntaxException("expected ; at the end of the line, found "+
			token.getType() + " ("+token.getContent()+")  at LINE " +
			token.getLine() + " and COLUMN "+ token.getColumn());
		}
	}

	private void ALGORITMO(){
		token = scanner.nextToken();

		if(token.getContent().equals("end")){
			throw new SyntaxException("You must define at least one command!");
		}

		this.COMMAND();

		if(token.getContent().equals("end")){
			return;
		}

		this.COMMAND_LIST();
	}

	private void COMMAND_LIST(){
		token = scanner.nextToken();

		if(token.getContent().equals("end")){
			return;
		}

		this.COMMAND();
		this.COMMAND_LIST();
	}

	private void 	COMMAND(){
		
		if(token.getType() == TokenType.IDENTIFIER){
			this.ASSIGN_COMMAND();
		}
		else if(token.getContent().equals("print")){
			this.OUTPUT_COMMAND();
		}
		else if(token.getContent().equals("if")){
			this.CONDITION_COMMAND();
		}
		else if(token.getContent().equals("while")){
			this.LOOP_COMMAND();
		}
	}

	private void ASSIGN_COMMAND() {
		token = scanner.nextToken();
		if(!token.getContent().equals("=")){
			throw new SyntaxException("expected '=' but instead found "+
			token.getType() + " ("+token.getContent()+")  at LINE " +
			token.getLine() + " and COLUMN "+ token.getColumn());
		}
		
		token = scanner.nextToken();
		this.ARITHMETIC_EXPRESSION();
	}

	private void OUTPUT_COMMAND() {
		token = scanner.nextToken();
		
		if(token.getType() == TokenType.OPEN_PARENTHESIS){
			this.STRING();
			return;
		}
		else if(token.getType() != TokenType.IDENTIFIER){
			throw new SyntaxException("expected a variable but found "+
			token.getType() + " ("+token.getContent()+")  at LINE " +
			token.getLine() + " and COLUMN "+ token.getColumn());
		}

		token = scanner.nextToken();
		if(token.getType() != TokenType.BREAK_LINE){
			throw new SyntaxException("expected ; at the end of the line, found "+
			token.getType() + " ("+token.getContent()+")  at LINE " +
			token.getLine() + " and COLUMN "+ token.getColumn());
		}
	}

	private void STRING(){
		token = scanner.nextToken();
		while(token.getType() != TokenType.CLOSE_PARENTHESIS){
			token = scanner.nextToken();
		}

		if(token.getType() == TokenType.BREAK_LINE){
			return;
		}
	}

	private void CONDITION_COMMAND() {
		
		this.RELATIONAL_EXPRESSION();

		if(!token.getContent().equals("then")){
			throw new SyntaxException("expected keyword 'then' but found "+
			token.getType() + " ("+token.getContent()+")  at LINE " +
			token.getLine() + " and COLUMN "+ token.getColumn());
		}
		
		
		token = scanner.nextToken();
		this.COMMAND();
		this.CONDITION_COMMAND2();
	}

	private void CONDITION_COMMAND2(){

		if(token.getType() == TokenType.BREAK_LINE){
			return;
		}

		if(!token.getContent().equals("else")){
			throw new SyntaxException("expected ; at the end of the line, found "+
			token.getType() + " ("+token.getContent()+")  at LINE " +
			token.getLine() + " and COLUMN "+ token.getColumn());
		}
		
		this.COMMAND();
	}

	private void LOOP_COMMAND() {
		this.RELATIONAL_EXPRESSION();
		this.COMMAND();
	}

	private void ARITHMETIC_EXPRESSION(){
		this.ARITHMETIC_TERM();
		this.ARITHMETIC_EXPRESSION2();		
	}

	private void ARITHMETIC_EXPRESSION2(){
		token = scanner.nextToken();
		if (token != null){
			this.ARITHMETIC_EXPRESSION3();
			this.ARITHMETIC_EXPRESSION2();
		}
	}

	private void ARITHMETIC_EXPRESSION3(){
		if(token.getContent() == "+" ||
		   token.getContent() == "-"){
			this.ARITHMETIC_TERM();
		}
	}

	private void ARITHMETIC_TERM() {
		this.ARITHMETIC_FACTOR();
		this.ARITHMETIC_TERM2();
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

	// private void ARITHMETIC_EXPRESSION(){
	// 	this.ARITHMETIC_FACTOR();
	// 	this.ARITHMETIC_EXPRESSION2();		
	// }

	// private void ARITHMETIC_EXPRESSION2(){
	// 	token = scanner.nextToken();

	// 	if (token.getContent().equals(";") ||
	// 			token.getContent().equals(")") ||
	// 			token.getType() == TokenType.RELATIONAL ||
	// 			token.getContent().equals("then") ||
	// 			token.getContent().equals("print") ||
	// 			token.getType() == TokenType.IDENTIFIER
	// 		){
	// 			return;
	// 	}
	// 	else if (token.getType() == TokenType.MATH){
			
	// 		token = scanner.nextToken();
	// 		this.ARITHMETIC_FACTOR();
	// 		this.ARITHMETIC_EXPRESSION2();
	// 	}	
	// 	else {
	// 		throw new SyntaxException("expected ; at the end of the line, found "+
	// 		token.getType() + " ("+token.getContent()+")  at LINE " +
	// 		token.getLine() + " and COLUMN "+ token.getColumn());

	// 	}
	// }

	private void ARITHMETIC_FACTOR(){

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

	private void RELATIONAL_EXPRESSION() {
		this.RELATIONAL_TERM();
		this.RELATIONAL_EXPRESSION2();
	}

	private void RELATIONAL_EXPRESSION2() {

		if(!isBOOLEAN_OPERATOR()){
			return;
		}

		this.RELATIONAL_TERM();
		this.RELATIONAL_EXPRESSION2();
	}

	private void RELATIONAL_TERM(){
		token = scanner.nextToken();

		if(token.getType() == TokenType.OPEN_PARENTHESIS){
			this.RELATIONAL_EXPRESSION();
			if(token.getType() == TokenType.CLOSE_PARENTHESIS){
				return;
			} else {
				throw new SyntaxException("expected ')' but found "+
				token.getType() + " ("+token.getContent()+")  at LINE " +
				token.getLine() + " and COLUMN "+ token.getColumn());
			}
		}

		this.ARITHMETIC_EXPRESSION();
		this.RELATIONAL_OPERATOR();
		token = scanner.nextToken();
		this.ARITHMETIC_EXPRESSION();
	}

	private void RELATIONAL_OPERATOR(){

		if(token.getType() != TokenType.RELATIONAL) {
			throw new SyntaxException("expected a relational operator but found "+
			token.getType() + " ("+token.getContent()+")  at LINE " +
			token.getLine() + " and COLUMN "+ token.getColumn());
		}
	}

	private boolean isBOOLEAN_OPERATOR(){

		if(!token.getContent().equals("and") || 
			 !token.getContent().equals("or")) {
			return false;
		}
		return true;
	}
}






