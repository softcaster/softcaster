# -*- coding: utf-8 -*-
import sys

# Forza l'aggiunta della libreria interna se non vista
if "__pyclasspath__/Lib" not in sys.path:
    sys.path.append("__pyclasspath__/Lib")

from org.softcaster.provider.interpreter import IProviderHelper
from org.softcaster.provider.bricks import Data
from org.softcaster.provider.bricks import Offset
from org.softcaster.provider.bricks import Node
from org.softcaster.provider.enums import OffsetType

class PyProviderHelper(IProviderHelper):
    
    #
    # Tassi Term Sofr/Estr
    #
    def getCmeTermNodes(self):

        nodes= list()
       
        offset = Offset(1,OffsetType.MONTHS);
        node = Node("1M",offset, None);
        nodes.append(node);

        offset = Offset(3,OffsetType.MONTHS);
        node = Node("3M",offset, None);
        nodes.append(node);

        offset = Offset(6,OffsetType.MONTHS);
        node = Node("6M",offset, None);
        nodes.append(node);

        offset = Offset(1,OffsetType.YEARS);
        node = Node("1Y",offset, None);
        nodes.append(node);

        return nodes;

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
        
        if symbol=='TERMSOFR':
            return self.getCmeTermNodes()
        elif symbol=='TERMESTR':
            return self.getCmeTermNodes()
        else:
            return None

    def getDebugInfo(self):
        return "Debug is: " + sys.registry.getProperty("python.debug")
        
       

    
