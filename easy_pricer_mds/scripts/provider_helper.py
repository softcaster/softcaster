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
    # Italy Yield Curve
    #
    def getItYieldNodes(self):
        nodes= list()
       
        offset = Offset(1,OffsetType.YEARS);
        node = Node("IT1Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(3,OffsetType.YEARS);
        node = Node("IT3Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(5,OffsetType.YEARS);
        node = Node("IT5Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(10,OffsetType.YEARS);
        node = Node("IT10Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(15,OffsetType.YEARS);
        node = Node("IT15Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(20,OffsetType.YEARS);
        node = Node("IT20Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        return nodes;

    #
    # United States Yield Curve
    #
    def getUsYieldNodes(self):
        nodes= list()
       
        offset = Offset(1,OffsetType.MONTHS);
        node = Node("U.S. 1M",offset, None, "ACT_365", "SIMPLE");
        nodes.append(node);

        offset = Offset(2,OffsetType.MONTHS);
        node = Node("U.S. 2M",offset, None, "ACT_365", "SIMPLE");
        nodes.append(node);

        offset = Offset(3,OffsetType.MONTHS);
        node = Node("U.S. 3M",offset, None, "ACT_365", "SIMPLE");
        nodes.append(node);

        offset = Offset(4,OffsetType.MONTHS);
        node = Node("U.S. 4M",offset, None, "ACT_365", "SIMPLE");
        nodes.append(node);

        offset = Offset(6,OffsetType.MONTHS);
        node = Node("U.S. 6M",offset, None, "ACT_365", "SIMPLE");
        nodes.append(node);

        offset = Offset(1,OffsetType.YEARS);
        node = Node("U.S. 1Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(2,OffsetType.YEARS);
        node = Node("U.S. 2Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(3,OffsetType.YEARS);
        node = Node("U.S. 3Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(5,OffsetType.YEARS);
        node = Node("U.S. 5Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(7,OffsetType.YEARS);
        node = Node("U.S. 7Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(10,OffsetType.YEARS);
        node = Node("U.S. 10Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(20,OffsetType.YEARS);
        node = Node("U.S. 20Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(30,OffsetType.YEARS);
        node = Node("U.S. 30Y",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        return nodes;

    #
    # Tassi Term Sofr/Estr
    #
    def getCmeTermNodes(self):

        nodes= list()
       
        offset = Offset(1,OffsetType.MONTHS);
        node = Node("1M",offset, None, "ACT_360", "SIMPLE");
        nodes.append(node);

        offset = Offset(3,OffsetType.MONTHS);
        node = Node("3M",offset, None, "ACT_360", "SIMPLE");
        nodes.append(node);

        offset = Offset(6,OffsetType.MONTHS);
        node = Node("6M",offset, None, "ACT_360", "SIMPLE");
        nodes.append(node);

        offset = Offset(1,OffsetType.YEARS);
        node = Node("1Y",offset, None, "ACT_360", "SIMPLE");
        nodes.append(node);

        return nodes;

    #
    # Tassi Eurirs
    #
    def getEurirsNodes(self):

        nodes = list()

        offset = Offset(1,OffsetType.YEARS);
        node = Node("EUR 01A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(2,OffsetType.YEARS);
        node = Node("EUR 02A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(3,OffsetType.YEARS);
        node = Node("EUR 03A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(4,OffsetType.YEARS);
        node = Node("EUR 04A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(5,OffsetType.YEARS);
        node = Node("EUR 05A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(6,OffsetType.YEARS);
        node = Node("EUR 06A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(7,OffsetType.YEARS);
        node = Node("EUR 07A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(8,OffsetType.YEARS);
        node = Node("EUR 08A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(9,OffsetType.YEARS);
        node = Node("EUR 09A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(10,OffsetType.YEARS);
        node = Node("EUR 10A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(11,OffsetType.YEARS);
        node = Node("EUR 11A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(12,OffsetType.YEARS);
        node = Node("EUR 12A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(15,OffsetType.YEARS);
        node = Node("EUR 15A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(20,OffsetType.YEARS);
        node = Node("EUR 20A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(25,OffsetType.YEARS);
        node = Node("EUR 25A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(30,OffsetType.YEARS);
        node = Node("EUR 30A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(40,OffsetType.YEARS);
        node = Node("EUR 40A Irs",offset, None, "ACT_365", "COMPOUNDED");
        nodes.append(node);

        offset = Offset(50,OffsetType.YEARS);
        node = Node("EUR 50A Irs",offset, None, "ACT_365", "COMPOUNDED");
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
        elif symbol=='ITYIELD':
            return self.getItYieldNodes()
        elif symbol=='USYIELD':
            return self.getUsYieldNodes()
        elif symbol=='FMIRS':
            return self.getEurirsNodes()
        else:
            return None

    def getDebugInfo(self):
        return "Debug is: " + sys.registry.getProperty("python.debug")
        
       

    
