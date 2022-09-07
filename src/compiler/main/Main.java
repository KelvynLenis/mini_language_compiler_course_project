package compiler.main;

import compiler.lexico.Scanner;
import compiler.lexico.Token;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner("source_code.mc");
		Token token = null;
		do {
			token = scanner.nextToken();
			if(token != null) {
				System.out.println(token);				
			}
		} while (token != null);
	}
}
