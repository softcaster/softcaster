/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.interpreter;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PySystemState;

/**
 *
 * @author Emy
 */
public class JythonObjectFactory {

    private final Class interfaceType;
    private final PyObject klass;

    // Constructor obtains a reference to the importer, module, and the class name
    public JythonObjectFactory(org.python.core.PySystemState state, Class interfaceType, String moduleName, String className) {
        this.interfaceType = interfaceType;

        // AGGANCIO STREAM: Fondamentale per vedere i print e usare pdb
        state.stdin = Py.java2py(System.in);
        state.stdout = Py.java2py(System.out);
        state.stderr = Py.java2py(System.err);
        
        PyObject importer = state.getBuiltins().__getitem__(Py.newString("__import__"));
        //PyObject importer = null;
        PyObject module = importer.__call__(Py.newString(moduleName));
        klass = module.__getattr__(className);
        //System.err.println("module=" + module + ",class=" + klass);
    }

    // This constructor passes through to the other constructor
    public JythonObjectFactory(Class interfaceType, String moduleName, String className) {
        // Uso Py.getSystemState() invece di crearne uno vuoto ogni volta
        this(Py.getSystemState(), interfaceType, moduleName, className);
    }

    // All of the followng methods return
    // a coerced Jython object based upon the pieces of information
    // that were passed into the factory. The differences are
    // between them are the number of arguments that can be passed
    // in as arguents to the object.
    public Object createObject() {
        return klass.__call__().__tojava__(interfaceType);
    }

    public Object createObject(Object arg1) {
        return klass.__call__(Py.java2py(arg1)).__tojava__(interfaceType);
    }

    public Object createObject(Object arg1, Object arg2) {
        return klass.__call__(Py.java2py(arg1), Py.java2py(arg2)).__tojava__(interfaceType);
    }

    public Object createObject(Object arg1, Object arg2, Object arg3) {
        return klass.__call__(Py.java2py(arg1), Py.java2py(arg2),
                Py.java2py(arg3)).__tojava__(interfaceType);
    }

    public Object createObject(Object args[], String keywords[]) {
        PyObject convertedArgs[] = new PyObject[args.length];
        for (int i = 0; i < args.length; i++) {
            convertedArgs[i] = Py.java2py(args[i]);
        }

        return klass.__call__(convertedArgs, keywords).__tojava__(interfaceType);
    }

    public Object createObject(Object... args) {
        return createObject(args, Py.NoKeywords);
    }

}
