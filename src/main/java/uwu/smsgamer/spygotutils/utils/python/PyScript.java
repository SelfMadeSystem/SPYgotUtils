package uwu.smsgamer.spygotutils.utils.python;

import org.python.core.*;
import uwu.smsgamer.senapi.utils.Pair;

import java.io.*;
import java.util.*;

public class PyScript extends PyThing {
    public File scriptFile;
    // load is on script load or whatever so no need
    public List<PyFunction> enableFuns = new ArrayList<>();
    public List<PyFunction> reloadFuns = new ArrayList<>();
    public List<PyFunction> disableFuns = new ArrayList<>();

    public PyScript(File scriptFile) {
        this.scriptFile = scriptFile;
    }

    public void execFile() throws FileNotFoundException {
        this.interpreter.execfile(new FileInputStream(this.scriptFile), this.scriptFile.getName());
    }

    public PyScript set(String name, PyObject obj) {
        return (PyScript) super.set(name, obj);
    }

    public PyScript setFuns(PyFunction[] objs) {
        return (PyScript) super.setFuns(objs);
    }

    public PyScript setVars(Pair<String, PyObject>[] objs) {
        return (PyScript) super.setVars(objs);
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