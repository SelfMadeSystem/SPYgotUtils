package uwu.smsgamer.spygotutils.utils.python;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import uwu.smsgamer.spygotutils.utils.FileUtils;

import java.io.File;

public class PyScript {
    public File scriptFile;
    public PythonInterpreter interpreter;

    public PyScript(File scriptFile) {
        this.scriptFile = scriptFile;
        this.interpreter = new PythonInterpreter();
    }

    public void execFile() {
        this.interpreter.exec(FileUtils.readLineByLine(scriptFile));
    }

    public PyScript set(String name, PyObject obj) {
        this.interpreter.set(name, obj);
        return this;
    }

    public PyScript setFuns(PyFunction[] objs) {
        for (PyFunction obj : objs)
            set(obj.__name__, obj);
        return this;
    }
}