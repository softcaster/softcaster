# -*- coding: utf-8 -*-
import sys

# Forza l'aggiunta della libreria interna se non vista
if "__pyclasspath__/Lib" not in sys.path:
    sys.path.append("__pyclasspath__/Lib")

from java.util import List
from ph.alephzero.finance.interpreter import IEngine


class PyEngine(IEngine):
    def fairValue(self, inputs):
        ##import sys
        ##import pdb
        ##from org.python.core.util import FileUtil

        # Convertiamo System.in (Java) in un oggetto file (Python)
        # 'r' indica lettura, 0 indica nessun buffering (immediato)
        ##py_stdin = FileUtil.wrap(sys.stdin, 'r', 0)
        
        # Creiamo il debugger forzando l'uso del nuovo stream compatibile
        ##debugger = pdb.Pdb(stdin=py_stdin, stdout=sys.stdout)
        ##debugger.set_trace()
        # sys.registry contiene le proprietà passate nell'initialize di Java
        if sys.registry.getProperty("python.debug") == "true":
            import pdb
            from org.python.core.util import FileUtil
            # Colleghiamo gli stream solo se siamo in debug
            py_in = FileUtil.wrap(sys.stdin, 'r', 0)
            pdb.Pdb(stdin=py_in, stdout=sys.stdout).set_trace()        
            
        return 100.0