package uwu.smsgamer.spygotutils.evaluator;

import org.bukkit.ChatColor;
import uwu.smsgamer.senapi.utils.StringUtils;
import uwu.smsgamer.spygotutils.evaluator.EvalVar.VarType;

import java.util.Arrays;
import java.util.regex.Pattern;

import static uwu.smsgamer.spygotutils.evaluator.EvalVar.VarType.*;

public class EvalOperator extends EvalToken {
    public final FunType type;
    public EvalToken[] args;

    public EvalOperator(int nestingLevel, FunType type) {
        super(nestingLevel);
        this.type = type;
    }

    @Override
    public String toString() {
        return "EvalOperator{" +
          "type=" + type +
          ", nestingLevel=" + nestingLevel +
          "}\n";
    }

    @Override
    public EvalVar<?> toVar(Evaluator ev) {
        EvalVar<?>[] vars = new EvalVar[args.length];
        for (int i = 0; i < args.length; i++) vars[i] = args[i].toVar(ev);
        return type.fun.run(ev, vars);
    }

    public float priority() {
        return nestingLevel + (args == null ? type.priority : 0);
    }

    public enum FunType {
        // Boolean operators
        NOT((e, v) -> new EvalVar.Bool(!v[0].bool()), "! $", 0.9, BOOLEAN, BOOLEAN),
        AND((e, v) -> new EvalVar.Bool(v[0].bool() && v[1].bool()), "$ && $", 0.5, BOOLEAN, BOOLEAN, BOOLEAN),
        OR((e, v) -> new EvalVar.Bool(v[0].bool() || v[1].bool()), "$ || $", 0.7, BOOLEAN, BOOLEAN, BOOLEAN),
        XOR((e, v) -> new EvalVar.Bool(v[0].bool() == v[1].bool()), "$ ^^ $", 0.7, BOOLEAN, BOOLEAN, BOOLEAN),
        NOR((e, v) -> NOT.run(e, OR.run(e, v)), "$ !| $", 0.7, BOOLEAN, BOOLEAN, BOOLEAN),
        XNOR((e, v) -> NOT.run(e, XOR.run(e, v)), "$ !^ $", 0.7, BOOLEAN, BOOLEAN, BOOLEAN),
        NAND((e, v) -> NOT.run(e, AND.run(e, v)), "$ !& $", 0.5, BOOLEAN, BOOLEAN, BOOLEAN),
        // Number operators
        ADD((e, v) -> new EvalVar.Num(v[0].d() + v[1].d()), "$ + $", 0.1, NUMBER, NUMBER, NUMBER),
        SUB((e, v) -> new EvalVar.Num(v[0].d() - v[1].d()), "$ - $", 0.1, NUMBER, NUMBER, NUMBER),
        MULT((e, v) -> new EvalVar.Num(v[0].d() * v[1].d()), "$ * $", 0.2, NUMBER, NUMBER, NUMBER),
        DIV((e, v) -> new EvalVar.Num(v[0].d() / v[1].d()), "$ / $", 0.2, NUMBER, NUMBER, NUMBER),
        MOD((e, v) -> new EvalVar.Num(v[0].d() % v[1].d()), "$ % $", 0.2, NUMBER, NUMBER, NUMBER),
        POW((e, v) -> new EvalVar.Num(Math.pow(v[0].d(), v[1].d())), "$ ^ $", 0.3, NUMBER, NUMBER, NUMBER),
        SQRT((e, v) -> new EvalVar.Num(Math.sqrt(v[0].d())), "sqrt $", NUMBER, NUMBER),
        ISQRT((e, v) -> new EvalVar.Num(1 / Math.sqrt(v[0].d())), "isqrt $", NUMBER, NUMBER),
        ROOT((e, v) -> new EvalVar.Num(Math.pow(v[0].d(), 1 / v[1].d())), "root $ $", NUMBER, NUMBER, NUMBER),
        MAX((e, v) -> new EvalVar.Num(Math.max(v[0].d(), v[1].d())), "max $ $", NUMBER, NUMBER, NUMBER),
        MIN((e, v) -> new EvalVar.Num(Math.min(v[0].d(), v[1].d())), "min $ $", NUMBER, NUMBER, NUMBER),
        FLOOR((e, v) -> new EvalVar.Num(Math.floor(v[0].d())), "floor $", NUMBER, NUMBER),
        CEIL((e, v) -> new EvalVar.Num(Math.ceil(v[0].d())), "ceil $", NUMBER, NUMBER),
        ROUND((e, v) -> new EvalVar.Num(Math.round(v[0].d())), "round $", NUMBER, NUMBER),
        RANDOM((e, v) -> new EvalVar.Num(Math.random()), "random", NUMBER),
        GREATER((e, v) -> new EvalVar.Bool(v[0].d() > v[1].d()), "$ > $", NUMBER, NUMBER, BOOLEAN),
        GREATER_E((e, v) -> new EvalVar.Bool(v[0].d() >= v[1].d()), "$ >= $", NUMBER, NUMBER, BOOLEAN),
        LESSER((e, v) -> new EvalVar.Bool(v[0].d() < v[1].d()), "$ < $", NUMBER, NUMBER, BOOLEAN),
        LESSER_E((e, v) -> new EvalVar.Bool(v[0].d() <= v[1].d()), "$ <= $", NUMBER, NUMBER, BOOLEAN),
        STRIP_ZERO((e, v) -> {
            if (v[0].i() == v[0].d()) return new EvalVar.Str(Integer.toString(v[0].i()));
            else return new EvalVar.Str(Double.toString(v[0].d()));
        }, "stp-0 $", 0.05, NUMBER, STRING),
        // String operators
        CONCAT((e, v) -> new EvalVar.Str(v[0].s().concat(v[1].s())), "$ s+ $", STRING, STRING, STRING),
        SUB_STRR((e, v) -> new EvalVar.Str(v[0].s().substring(v[1].i())), "$ substrr $ ", STRING, NUMBER, BOOLEAN),
        SUB_STR((e, v) -> new EvalVar.Str(v[0].s().substring(v[1].i(), v[2].i())), "$ substr $ $ ", STRING, NUMBER, NUMBER, BOOLEAN),
        REPLACE((e, v) -> new EvalVar.Str(v[0].s().replace(v[1].s(), v[2].s())), "$ replace $ $ ", STRING, STRING, STRING, BOOLEAN),
        REPLACE_FIRST((e, v) -> new EvalVar.Str(v[0].s().replace(v[1].s(), v[2].s())), "$ replaceFirst $ $ ", STRING, STRING, STRING, BOOLEAN),
        REPLACE_REG((e, v) -> new EvalVar.Str(v[0].s().replaceAll(v[1].s(), v[2].s())), "$ replaceReg $ $ ", STRING, STRING, STRING, BOOLEAN),
        STARTS_WITH((e, v) -> new EvalVar.Bool(v[0].s().startsWith(v[1].s())), "$ startsWith $ ", STRING, STRING, BOOLEAN),
        ENDS_WITH((e, v) -> new EvalVar.Bool(v[0].s().endsWith(v[1].s())), "$ endsWith $ ", STRING, STRING, BOOLEAN),
        CONTAINS((e, v) -> new EvalVar.Bool(v[0].s().contains(v[1].s())), "$ contains $ ", STRING, STRING, BOOLEAN),
        CONTAINS_IC((e, v) -> new EvalVar.Bool(v[0].s().toLowerCase().contains(v[1].s().toLowerCase())), "$ containsIc $ ", STRING, STRING, BOOLEAN),
        MATCHES((e, v) -> new EvalVar.Bool(v[0].s().matches(v[1].s())), "$ matches $ ", STRING, STRING, BOOLEAN),
        INDEX_OF((e, v) -> new EvalVar.Num(v[0].s().indexOf(v[1].s())), "$ indexOf $ ", STRING, STRING, NUMBER),
        LENGTH((e, v) -> new EvalVar.Num(v[0].s().length()), "$ length", STRING, NUMBER),
        EQUALS_IC((e, v) -> new EvalVar.Bool(v[0].s().equalsIgnoreCase(v[1].s())), "$ equalsIc $ ", STRING, STRING, NUMBER),
        PAPI((e, v) -> new EvalVar.Str(StringUtils.replacePlaceholders(e.player, v[0].s())), "papi $ ", 0.99, STRING, STRING, NUMBER),
        COLOR((e, v) -> new EvalVar.Str(StringUtils.colorize(v[0].s())), "color $ ", 0.99, STRING, NUMBER),
        COLOR_LTD((e, v) -> new EvalVar.Str(StringUtils.colorizeLimited(v[0].s(), v[1].s())), "colorLtd $ $ ", 0.99, STRING, STRING, NUMBER),
        GET_LAST_COLORS((e, v) -> new EvalVar.Str(ChatColor.getLastColors(v[0].s())), "getLastColors $ ", 0.99, STRING, NUMBER),
        // Any operators
        EQUALS((e, v) -> new EvalVar.Bool(v[0].s().equals(v[1].s())), "$ == $", ANY, ANY, BOOLEAN),
        NOT_EQUALS((e, v) -> new EvalVar.Bool(!v[0].s().equals(v[1].s())), "$ != $", ANY, ANY, BOOLEAN),
        // Player
        HAS_PERM((e, v) -> new EvalVar.Bool(e.player.getPlayer().hasPermission(v[0].s())), "hasPerm $", BOOLEAN, STRING),
        IS_OP((e, v) -> new EvalVar.Bool(e.player.isOp()), "isOp", BOOLEAN),
        ;

        public EvalVar<?> run(Evaluator ev, EvalVar<?>... vars) {
            return fun.run(ev, vars);
        }

        public final Fun fun;
        public final String format;
        public final String keyword;
        public final int argsBefore;
        public final int argsAfter;
        public final EvalOperatorToken[] tokens;
        public final float priority;
        public final VarType returnType;
        public final VarType[] inputTypes;

        FunType(Fun fun, String format, VarType... types) {
            this.fun = fun;
            this.format = format;
            this.keyword = format.replace(" ", "").replace("$", "");
            String[] split = this.format.split(Pattern.quote(this.keyword));
            if (split.length == 0) {
                this.argsBefore = 0;
                this.argsAfter = 0;
            } else if (split.length == 1) {
                this.argsBefore = (int) split[0].chars().filter(c -> c == '$').count();
                this.argsAfter = 0;
            } else {
                this.argsBefore = (int) split[0].chars().filter(c -> c == '$').count();
                this.argsAfter = (int) split[1].chars().filter(c -> c == '$').count();
            }
            this.priority = 0;
            this.returnType = types[types.length - 1];
            if (types.length > 1) this.inputTypes = Arrays.copyOfRange(types, 1, types.length - 1);
            else this.inputTypes = new VarType[0];
            this.tokens = EvalOperatorToken.getTokensForOperator(format);
        }

        FunType(Fun fun, String format, double priority, VarType... types) {
            this.fun = fun;
            this.format = format;
            this.keyword = format.replace(" ", "").replace("$", "");
            String[] split = this.format.split(Pattern.quote(this.keyword));
            if (split.length == 0) {
                this.argsBefore = 0;
                this.argsAfter = 0;
            } else if (split.length == 1) {
                this.argsBefore = (int) split[0].chars().filter(c -> c == '$').count();
                this.argsAfter = 0;
            } else {
                this.argsBefore = (int) split[0].chars().filter(c -> c == '$').count();
                this.argsAfter = (int) split[1].chars().filter(c -> c == '$').count();
            }
            this.priority = (float) priority;
            this.returnType = types[types.length - 1];
            if (types.length > 1) this.inputTypes = Arrays.copyOfRange(types, 1, types.length - 1);
            else this.inputTypes = new VarType[0];
            this.tokens = EvalOperatorToken.getTokensForOperator(format);
        }

        public static FunType getFunType(String keyword) {
            for (FunType value : values()) {
                if (value.keyword.equals(keyword)) return value;
            }
            return null;
        }
    }

    public interface Fun {
        EvalVar<?> run(Evaluator evaluator, EvalVar<?>... args);
    }
}
