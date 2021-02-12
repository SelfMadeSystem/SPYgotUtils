package uwu.smsgamer.senapi.utils;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * Evaluates shit in python.
 */
public class Evaluator {
    PythonInterpreter interpreter = new PythonInterpreter();

    public PyObject eval(String s) {
        return interpreter.eval(s);
    }

    public void exec(String s) {
        interpreter.exec(s);
    }

    public void set(String name, Object obj) {
        interpreter.set(name, obj);
    }
}
