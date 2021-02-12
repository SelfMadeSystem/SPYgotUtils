package uwu.smsgamer.spygotutils.evaluator;

import lombok.Getter;
import org.jetbrains.annotations.*;
import uwu.smsgamer.senapi.utils.StringUtils;

import java.util.*;

@Getter
public abstract class EvalVar<T> extends EvalToken {
    public String name;
    public T value;
    public final VarType type;

    protected EvalVar(String name, T value, VarType type, int nest) {
        super(nest);
        this.name = name;
        this.value = value;
        this.type = type;
    }

    protected EvalVar(T value, VarType type, int nest) {
        super(nest);
        this.name = "__unnamed__";
        this.value = value;
        this.type = type;
    }

    protected EvalVar(String name, @Nullable T value, VarType type) {
        super(-1);
        this.name = name;
        this.value = value;
        this.type = type;
    }

    protected EvalVar(@Nullable T value, VarType type) {
        super(-1);
        this.name = "__unnamed__";
        this.value = value;
        this.type = type;
    }

    public abstract Number number();

    public int i() {
        return number().intValue();
    }

    public double d() {
        return number().doubleValue();
    }

    public String string() {
        return Objects.toString(value);
    }

    public String s() {
        return string();
    }

    public abstract boolean bool();

    @Override
    public EvalVar<?> toVar(Evaluator ev) {
        return this;
    }

    public enum VarType {
        STRING,
        NUMBER,
        BOOLEAN,
        ANY
    }

    public static class Str extends EvalVar<String> {
        public Str(String name, @Nullable String value) {
            super(name, value, VarType.STRING);
        }
        public Str(@Nullable String value) {
            super(value, VarType.STRING);
        }
        public Str(String name, @Nullable String value, int nest) {
            super(name, value, VarType.STRING, nest);
        }
        public Str(@Nullable String value, int nest) {
            super(value, VarType.STRING, nest);
        }

        @Override
        public String toString() {
            return "Str{" +
              "name='" + name + '\'' +
              ", value=" + value +
              ", nestingLevel=" + nestingLevel +
              "}\n";
        }

        @Override
        public Number number() {
            if (value == null) return 0;
            return Double.parseDouble(value);
        }

        @Override
        public boolean bool() {
            if (value == null) return false;
            return !value.isEmpty();
        }
    }

    public static class Num extends EvalVar<Number> {
        public Num(String name, @Nullable Number value) {
            super(name, value, VarType.NUMBER);
        }
        public Num(@Nullable Number value) {
            super(value, VarType.NUMBER);
        }
        public Num(String name, @Nullable Number value, int nest) {
            super(name, value, VarType.NUMBER, nest);
        }
        public Num(@Nullable Number value, int nest) {
            super(value, VarType.NUMBER, nest);
        }

        @Override
        public String toString() {
            return "Num{" +
              "name='" + name + '\'' +
              ", value=" + value +
              ", nestingLevel=" + nestingLevel +
              "}\n";
        }

        @Override
        public Number number() {
            return value == null ? 0 : value;
        }

        @Override
        public boolean bool() {
            return i() != 0;
        }
    }

    public static class Bool extends EvalVar<Boolean> {
        public Bool(String name, @Nullable Boolean value) {
            super(name, value, VarType.BOOLEAN);
        }
        public Bool(@Nullable Boolean value) {
            super(value, VarType.BOOLEAN);
        }
        public Bool(String name, @Nullable Boolean value, int nest) {
            super(name, value, VarType.BOOLEAN, nest);
        }
        public Bool(@Nullable Boolean value, int nest) {
            super(value, VarType.BOOLEAN, nest);
        }

        @Override
        public String toString() {
            return "Bool{" +
              "name='" + name + '\'' +
              ", value=" + value +
              ", nestingLevel=" + nestingLevel +
              "}\n";
        }

        @Override
        public Number number() {
            return Boolean.TRUE.equals(value) ? 1 : 0;
        }

        @Override
        public boolean bool() {
            return Boolean.TRUE.equals(value);
        }
    }

    public static class Unknown extends EvalToken {
        public String name;

        public Unknown(String name, int nest) {
            super(nest);
            this.name = name;
        }

        @Override
        public String toString() {
            return "Unknown{" +
              "name='" + name + '\'' +
              ", nestingLevel=" + nestingLevel +
              "}\n";
        }

        @Override
        public EvalVar<?> toVar(Evaluator ev) {
            return ev == null ? null : ev.varMap.get(name);
        }
    }

    public static class PAPI extends EvalToken {
        private final String value;

        public PAPI(@NotNull String value, int nest) {
            super(nest);
            this.value = value;
        }

        public PAPI(@Nullable String value) {
            super(0);
            this.value = value;
        }

        @Override
        public String toString() {
            return "PAPI{" +
              ", value=" + value +
              ", nestingLevel=" + nestingLevel +
              "}\n";
        }

        @Override
        public EvalVar<?> toVar(Evaluator ev) {
            return new Str(StringUtils.replacePlaceholders(ev.player, "%" + value + "%"));
        }
    }
}
