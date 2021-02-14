package uwu.smsgamer.spygotutils.utils.python;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import uwu.smsgamer.spygotutils.utils.FileUtils;

import java.io.File;
import java.util.*;

public class PyScript {
    public File scriptFile;
    public PythonInterpreter interpreter;
    // load is on script load or whatever so no need
    public List<PyFunction> enableFuns = new ArrayList<>();
    public List<PyFunction> reloadFuns = new ArrayList<>();
    public List<PyFunction> disableFuns = new ArrayList<>();

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

    // how would you name this???
    public void getGoodFuns() {
        getAndSet("onEnable", enableFuns);
        getAndSet("on_enable", enableFuns);
        getAndSet("enable", enableFuns);
        getAndSet("onReload", reloadFuns);
        getAndSet("on_reload", reloadFuns);
        getAndSet("reload", reloadFuns);
        getAndSet("onDisable", disableFuns);
        getAndSet("on_disable", disableFuns);
        getAndSet("disable", disableFuns);
    }

    private void getAndSet(String name, List<PyFunction> list) {
        PyObject obj = this.interpreter.get(name);
        if (obj instanceof PyFunction) list.add((PyFunction) obj);
    }

    public void execAll(List<PyFunction> funs) {
        for (PyFunction fun : funs) {
            fun.__call__();
        }
    }
}