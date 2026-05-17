# -*- coding: utf-8 -*-
import sys

# Forza l'aggiunta della libreria interna se non vista
if "__pyclasspath__/Lib" not in sys.path:
    sys.path.append("__pyclasspath__/Lib")

from org.softcaster.provider.interpreter import IYieldCurveBuilder
from org.softcaster.provider.cme import CmeGroupProvider
from org.softcaster.provider.ecb import ECBProvider
from org.softcaster.provider.bricks import Node

class PyYCB(IYieldCurveBuilder):

    #
    # Entry Point
    #    
    def getNodeList(self, symbol):

        if sys.registry.getProperty("python.debug") == "true":
            import pdb
            from org.python.core.util import FileUtil
            # Colleghiamo gli stream solo se siamo in debug
            py_in = FileUtil.wrap(sys.stdin, 'r', 0)
            pdb.Pdb(stdin=py_in, stdout=sys.stdout).set_trace()        
        
        if symbol=='EUR01':
            return self.getEUR01()
        else:
            return None

    def getEUR01(self):
        cme = CmeGroupProvider.getInstance()
        if cme is not None:
            # prima chiamata per ottenere tassi term-estr
            nodes = cme.getTermEsterRates()
            # converto in una lista python, se nodes e`null
            # creo la lista vuota
            if nodes is not None:
                _nodes = list(nodes)
            else:
                _nodes = []
            # aggiungo ovn-estr
            ecb = ECBProvider.getInstance()
            if ecb is not None:
                ovnNode = ecb.getOvnEstr()
                _nodes.append(ovnNode)

            # Jython converte automaticamente la lista Python in una java.util.List
            return _nodes        
        else:
            return None
    

