package compiler.main;

import compiler.exceptions.LexicalException;
import compiler.exceptions.SyntaxException;
import compiler.lexico.Scanner;
import compiler.syntax.Parser;
// import compiler.lexico.Token;
// import utils.TokenType;

public class Main {
	public static void main(String[] args) {
		try {
			Scanner scanner = new Scanner("source_code.mc");
			Parser parser = new Parser(scanner);
	
			parser.PROGRAMA();
			System.out.println("Compilation Successful!");

		}catch (LexicalException err) {
			System.out.println("Lexical Error: "+err.getMessage());
		} catch (SyntaxException err) {
			System.out.println("Syntax Error: " + err.getMessage());
		}
		catch (Exception err) {
			System.out.println("Generic Error!!");
			System.out.println(err.getClass().getName());
		}
	}
}
