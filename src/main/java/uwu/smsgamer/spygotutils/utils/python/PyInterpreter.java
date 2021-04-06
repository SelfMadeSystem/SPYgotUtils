package uwu.smsgamer.spygotutils.utils.python;

import org.python.core.*;
import org.python.util.*;

public class PyInterpreter extends PyShell {
    public InteractiveInterpreter interpreter;
    public StringBuffer lines = new StringBuffer();

    public PyInterpreter() {
        interpreter = new InteractiveInterpreter();
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

//    private static class MyInteractiveInterpreter extends InteractiveInterpreter {
//        @Override
//        public void showexception(PyException exc) {
//            Py.printException(exc, null, getSystemState().stderr);
//        }
//
//        public void write(String data) {
//            ((StdoutWrapper) getSystemState().stderr.__tojava__(StdoutWrapper.class)).write(data);
//        }
//    }
}
