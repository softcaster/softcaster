#! /usr/bin/python

# To change this template, choose Tools | Templates
# and open the template in the editor.

from org.softcaster.marketdataprovider.interpreter import IYieldCurveHelper
from org.softcaster.marketdataprovider.yieldcurve import YieldNode
from org.softcaster.marketdataprovider import OFFSET_TYPE

class PyYcHelper(IYieldCurveHelper):
    
    #
    # Gov Bond ITA
    #
    def getNodeListITA(self):
        nodeList = list()

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(1);
        node.setRic("italy-1-month");
        nodeList.append(node);
        
        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(3);
        node.setRic("italy-3-month-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(6);
        node.setRic("italy-6-month-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(9);
        node.setRic("italy-9-month-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(1);
        node.setRic("italy-1-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(2);
        node.setRic("italy-2-year-bond-yield");
        nodeList.append(node);
        node = YieldNode()
        
        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(3);
        node.setRic("italy-3-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(4);
        node.setRic("italy-4-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(5);
        node.setRic("italy-5-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(6);
        node.setRic("italy-6-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(7);
        node.setRic("italy-7-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(8);
        node.setRic("italy-8-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(9);
        node.setRic("italy-9-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(10);
        node.setRic("italy-10-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(15);
        node.setRic("italy-15-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(20);
        node.setRic("italy-20-year");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(25);
        node.setRic("italy-25-year");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(30);
        node.setRic("italy-30-year");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(50);
        node.setRic("italy-50-year");
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
        node.setRic("u.s.-1-month-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(2);
        node.setRic("u.s.-2-month-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(3);
        node.setRic("u.s.-3-month-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(4);
        node.setRic("u.s.-4-month-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(6);
        node.setRic("u.s.-6-month-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(1);
        node.setRic("u.s.-1-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(2);
        node.setRic("u.s.-2-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(3);
        node.setRic("u.s.-3-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(5);
        node.setRic("u.s.-5-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(7);
        node.setRic("u.s.-7-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(10);
        node.setRic("u.s.-10-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(20);
        node.setRic("us-20-year-bond-yield");
        nodeList.append(node);

        node = YieldNode()
        node.setOffsetType(OFFSET_TYPE.YEARS);
        node.setOffset(30);
        node.setRic("u.s.-30-year-bond-yield");
        nodeList.append(node);
        return nodeList;
                
    #
    # Entry Point
    #    
    def getNodeList(self, curveId):
        
        if curveId=='ITA':
            return self.getNodeListITA()
        elif curveId=='USA':    
            return self.getNodeListUSD()
        else:
            return None
       

    
