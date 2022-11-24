package utils;

import java.util.HashMap;

public class ReservedWordsType {
  private HashMap<String, String> types = new HashMap<String, String>();

  public ReservedWordsType() {
    this.types.put("int", "varType");
    this.types.put("float", "varType");
    this.types.put("print", "command");
    this.types.put("if", "command");
    this.types.put("else", "command");
    this.types.put("begin", "command");
    this.types.put("do", "command");
    this.types.put("end", "command");
    this.types.put("while", "command");
    this.types.put("and", "command");
    this.types.put("or", "command");
  }

  public String getType(String reservedWord) {
    return this.types.get(reservedWord);
  }

  public void addType(String type, String reservedWord){
    this.types.put(reservedWord, type);
  }

  public Boolean hasToken(String token){
    return this.types.containsKey(token);
  }
}
