# -*- coding: utf-8 -*-
import sys

# Forza l'aggiunta della libreria interna se non vista
if "__pyclasspath__/Lib" not in sys.path:
    sys.path.append("__pyclasspath__/Lib")

from org.softcaster.market_data.core.interpreter import IProviderHelper
from org.softcaster.market_data.core import Data
from org.softcaster.engine.curve import Offset
from org.softcaster.market_data.core import Node
from org.softcaster.engine.enums import OffsetType
from org.softcaster.engine.enums import Compounding
from org.softcaster.engine.enums import CurveNodeType
from org.softcaster.engine.enums import DaycountBasis

class PyProviderHelper(IProviderHelper):
    
    #
    # Tassi Term Sofr/Estr
    #
    def getCmeTermNodes(self):

        nodes= list()
       
        offset = Offset(1,OffsetType.MONTHS);
        node = Node("1M",offset, DaycountBasis.ACT_360, Compounding.SIMPLE, CurveNodeType.MONEY_MARKET, None);
        nodes.append(node);

        offset = Offset(3,OffsetType.MONTHS);
        node = Node("3M",offset, DaycountBasis.ACT_360, Compounding.SIMPLE, CurveNodeType.MONEY_MARKET, None);
        nodes.append(node);

        offset = Offset(6,OffsetType.MONTHS);
        node = Node("6M",offset, DaycountBasis.ACT_360, Compounding.SIMPLE, CurveNodeType.MONEY_MARKET, None);
        nodes.append(node);

        offset = Offset(1,OffsetType.YEARS);
        node = Node("1Y",offset, DaycountBasis.ACT_360, Compounding.SIMPLE, CurveNodeType.MONEY_MARKET, None);
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

    def getUrl(self, curveId):
        if curveId=='USD_OIS':
            return 'https://www.bluegamma.io/usd-swap-rates'
        elif curveId=='EURIBOR_OIS':
            return 'https://www.bluegamma.io/euribor-swap-rates'
        elif curveId=='ESTR_OIS':
            return 'https://www.bluegamma.io/swap-rates/estr-swap-rates'
        elif curveId=='CHF_OIS':
            return 'https://www.bluegamma.io/swap-rates/chf-swap-rates'
        elif curveId=='CAD_OIS':
            return 'https://www.bluegamma.io/swap-rates/cad-swap-rates'
        else:
            return None
        
       

    
