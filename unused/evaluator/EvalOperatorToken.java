package uwu.smsgamer.spygotutils.evaluator;

import java.util.*;

public class EvalOperatorToken {
    public final String stringMatch;
    public final int argNum;

    public EvalOperatorToken(String stringMatch) {
        this.stringMatch = stringMatch;
        this.argNum = -1;
    }

    public EvalOperatorToken(int argNum) {
        this.stringMatch = "";
        this.argNum = argNum;
    }
    
    public static EvalOperatorToken[] getTokensForOperator(String input) {
        List<EvalOperatorToken> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        char[] charArray = input.toCharArray();
        int argNum = -1;
        for (char c : charArray) {
            if (Character.isWhitespace(c)) {
                if (sb.length() > 0) tokens.add(new EvalOperatorToken(sb.toString()));
                sb = new StringBuilder();
                continue;
            }
            if (c == '%') {
                if (sb.length() > 0) tokens.add(new EvalOperatorToken(sb.toString()));
                tokens.add(new EvalOperatorToken(++argNum));
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        return tokens.toArray(new EvalOperatorToken[0]);
    }

    @Override
    public String toString() {
        return "EvalOperatorToken{" +
          "stringMatch='" + stringMatch + '\'' +
          ", argNum=" + argNum +
          "}\n";
    }
}
