package uwu.smsgamer.spygotutils.utils.python;

import org.python.core.*;

public class PyShell extends PyThing {
    public StringBuffer lines = new StringBuffer();

    @Override
    public PyObject eval(String str) throws PyException {
        PyObject result = super.eval(str);
        lines.append(str).append("\n");
        return result;
    }
}
