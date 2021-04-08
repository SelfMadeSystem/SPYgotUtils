package uwu.smsgamer.spygotutils.utils.python;

import org.python.core.*;
import org.python.util.InteractiveInterpreter;

public class PyInterpreter extends PyShell {
    public InteractiveInterpreter interpreter;
    public StringBuffer lines = new StringBuffer();

    public PyInterpreter() {
        interpreter = new MyInteractiveInterpreter();
        super.interpreter = interpreter;
    }

    @Override
    public PyObject eval(String str) {
        PyObject result = interpreter.eval(str);
        lines.append(str).append("\n");
        return result;
    }

    public boolean interpret(String str) {
        boolean result = interpreter.runsource(str);
        if (result) return true;
        lines.append(str).append("\n");
        return false;
    }

    public String getLines() {
        return lines.toString();
    }

    private static class MyInteractiveInterpreter extends InteractiveInterpreter {
        @Override
        public boolean runsource(String source, String filename, CompileMode kind) {
            PyObject code = Py.compile_command_flags(source, filename, kind, this.cflags, true);

            if (code == Py.None) {
                return true;
            } else {
                this.runcode(code);
                return false;
            }
        }
    }
}
