package uwu.smsgamer.spygotutils.utils.python;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import uwu.smsgamer.senapi.utils.Pair;

public class PyShell {
    public PythonInterpreter interpreter;

    public PyShell() {
        this.interpreter = new PythonInterpreter();
    }

    public PyShell set(String name, PyObject obj) {
        this.interpreter.set(name, obj);
        return this;
    }

    public PyShell setFuns(PyFunction[] objs) {
        if (objs != null) for (PyFunction obj : objs) {
            if (obj != null) set(obj.__name__, obj);
        }
        return this;
    }

    public PyShell setVars(Pair<String, PyObject>[] objs) {
        if (objs != null) for (Pair<String, PyObject> pair : objs) if (pair != null) set(pair.a, pair.b);
        return this;
    }

    public PyObject eval(String str) {
        return interpreter.eval(str);
    }
}
