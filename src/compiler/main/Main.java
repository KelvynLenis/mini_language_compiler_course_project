package compiler.main;

import compiler.lexico.Scanner;
import compiler.lexico.Token;
import utils.TokenType;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner("source_code.mc");
		Token token = null;
		do {
			token = scanner.nextToken();
			if(token != null && token.getType() != TokenType.COMMENTARY) {
				System.out.println(token);				
			}
		} while (token != null);
	}
}
