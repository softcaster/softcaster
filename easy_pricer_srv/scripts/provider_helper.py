# -*- coding: utf-8 -*-
import sys

# Forza l'aggiunta della libreria interna se non vista
if "__pyclasspath__/Lib" not in sys.path:
    sys.path.append("__pyclasspath__/Lib")

from org.softcaster.marketdataprovider.interpreter import IProviderHelper
from org.softcaster.marketdataprovider import YieldNode
from org.softcaster.marketdataprovider import OFFSET_TYPE

class PyProviderHelper(IProviderHelper):
    
    #
    # Gov Bond ITA
    #
    def getNodeListITA(self):
        nodeList = list()

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(1);
        node.setRic("Italy 1M");
        nodeList.append(node);
        
        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(3);
        node.setRic("Italy 3M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(6);
        node.setRic("Italy 6M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(9);
        node.setRic("Italy 9M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(1);
        node.setRic("Italy 1Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(2);
        node.setRic("Italy 2Y");
        nodeList.append(node);
        node = YieldNode()
        
        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(3);
        node.setRic("Italy 3Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(4);
        node.setRic("Italy 4Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(5);
        node.setRic("Italy 5Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(6);
        node.setRic("Italy 6Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(7);
        node.setRic("Italy 7Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(8);
        node.setRic("Italy 8Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(9);
        node.setRic("Italy 9Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(10);
        node.setRic("Italy 10Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(15);
        node.setRic("Italy 15Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(20);
        node.setRic("Italy 20Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(25);
        node.setRic("Italy 25Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(30);
        node.setRic("Italy 30Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(50);
        node.setRic("Italy 50Y");
        nodeList.append(node);

        return nodeList;

    #
    # Gov Bond USD
    #
    def getNodeListUSD(self):

        nodeList = list()

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(1);
        node.setRic("U.S. 1M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(2);
        node.setRic("U.S. 2M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(3);
        node.setRic("U.S. 3M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(4);
        node.setRic("U.S. 4M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(6);
        node.setRic("U.S. 6M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(1);
        node.setRic("U.S. 1Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(2);
        node.setRic("U.S. 2Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(3);
        node.setRic("U.S. 3Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(5);
        node.setRic("U.S. 5Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(7);
        node.setRic("U.S. 7Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(10);
        node.setRic("U.S. 10Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(20);
        node.setRic("U.S. 20Y");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(30);
        node.setRic("U.S. 30Y");
        nodeList.append(node);
        return nodeList;

    #
    # Tassi Euribor
    #
    def getNodeListEuribor(self):

        nodeList = list()

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.DAYS);
        node.setOffset(7);
        node.setRic("EURIBOR 1W");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(1);
        node.setRic("EURIBOR 1M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(3);
        node.setRic("EURIBOR 3M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(6);
        node.setRic("EURIBOR 6M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(1);
        node.setRic("EURIBOR 1Y");
        nodeList.append(node);
        return nodeList;
                
    #
    # Tassi Eurirs
    #
    def getNodeListEurirs(self):

        nodeList = list()

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(1);
        node.setRic("EUR 01A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(2);
        node.setRic("EUR 02A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(3);
        node.setRic("EUR 03A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(4);
        node.setRic("EUR 04A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(5);
        node.setRic("EUR 05A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(6);
        node.setRic("EUR 06A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(7);
        node.setRic("EUR 07A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(8);
        node.setRic("EUR 08A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(9);
        node.setRic("EUR 09A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(10);
        node.setRic("EUR 10A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(11);
        node.setRic("EUR 11A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(12);
        node.setRic("EUR 12A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(15);
        node.setRic("EUR 15A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(20);
        node.setRic("EUR 20A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(25);
        node.setRic("EUR 25A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(30);
        node.setRic("EUR 30A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(40);
        node.setRic("EUR 40A Irs");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(50);
        node.setRic("EUR 50A Irs");
        nodeList.append(node);

        return nodeList;

    #
    # Tassi Sofr
    #
    def getNodeListSofr(self):

        nodeList = list()

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(1);
        node.setRic("SOFR 1M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(3);
        node.setRic("SOFR 3M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(6);
        node.setRic("SOFR 6M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(1);
        node.setRic("SOFR 1Y");
        nodeList.append(node);
        return nodeList;

    #
    # Tassi Ester
    #
    def getNodeListEster(self):

        nodeList = list()

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(1);
        node.setRic("ESTER 1M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(3);
        node.setRic("ESTER 3M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(6);
        node.setRic("ESTER 6M");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(1);
        node.setRic("ESTER 1Y");
        nodeList.append(node);
        return nodeList;

    #
    # Entry Point
    #    
    def getNodeList(self, curveId):

        if sys.registry.getProperty("python.debug") == "true":
            import pdb
            from org.python.core.util import FileUtil
            # Colleghiamo gli stream solo se siamo in debug
            py_in = FileUtil.wrap(sys.stdin, 'r', 0)
            pdb.Pdb(stdin=py_in, stdout=sys.stdout).set_trace()        
        
        if curveId=='ITA':
            return self.getNodeListITA()
        elif curveId=='USD':    
            return self.getNodeListUSD()
        elif curveId=='EURIBOR':    
            return self.getNodeListEuribor()
        elif curveId=='EURIRS':    
            return self.getNodeListEurirs()
        elif curveId=='SOFR':    
            return self.getNodeListSofr()
        elif curveId=='ESTER':    
            return self.getNodeListEster()
        else:
            return None
       

    
