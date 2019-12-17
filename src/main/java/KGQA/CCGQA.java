package KGQA;

import CCG.Syntax;

public class CCGQA {
    public static void main(String[] args) {
        Syntax syntax = Syntax.parse("(S\\NP)/NP");
        System.out.println(syntax);
    }
}
