# -*- coding: utf-8 -*-  
'''
Created on 2015/04/23

@author: pake
'''

import matplotlib.pyplot as plt
from copy import deepcopy
from cProfile import label
import numpy as np
from lib2to3.pgen2.tokenize import Double


def ReadDataFile(filename):
    fp = open(filename, 'r')              
    DataList = []
    try:
        for line in fp.readlines():
            DataList.append(line.strip().split(" "))
    finally:
        fp.close()
    return DataList


def isdomination(x1,y1,x2,y2):
    if (float(x1)<float(x2) and float(y1)<float(y2)):
            return True;
    return False;

def isdominationbylist(xlist, ylist, x1, y1):
    for i in range(len(xlist)):
        if (isdomination(xlist[i], ylist[i], x1, y1)):
            print 'zipei' , xlist[i], ylist[i], x1, y1
            return True;
    return False
    
def ShowPic(Xlist, Ylist):
    #plt.xlim(0, 10)
    #plt.ylim(0, 10)
    print Xlist
    print Ylist
    
    newxlist = deepcopy(Xlist);
    newylist = deepcopy(Ylist);
    
    for i in range(len(Xlist)-1, 0, -1):
        if isdominationbylist(newxlist, newylist, Xlist[i], Ylist[i]):
            print i
            print "del", newxlist[i], newylist[i]
            del newxlist[i]
            del newylist[i]
    

    
    print len(newxlist), len(Xlist)
    #plt.show()
if __name__ == '__main__':
    Datalist1 = ReadDataFile('D:/experiment/workspace/BeeForGap/output/bee')
    Datalist2 = ReadDataFile('D:/experiment/workspace/BeeForGap/output/nsga')
    Datalist3 = ReadDataFile('D:/experiment/workspace/BeeForGap/output/moead')
    xlist = [[],[],[]]
    ylist = [[],[],[]]
    for line in Datalist1:
        xlist[0].append(line[0])
        ylist[0].append(line[1])
    for line1 in Datalist2:
        xlist[1].append(line1[0])
        ylist[1].append(line1[1])
    for line2 in Datalist3:
        xlist[2].append(line2[0])
        ylist[2].append(line2[1])
    plt.grid(True)    
    #plt.title('$5 \times 15$')
    plt.xlabel(r"$F_c(x) $", fontsize=20)
    plt.ylabel(r'$F_r(x)$' , fontsize=20)
    plot1 = plt.plot(xlist[0], ylist[0], 'o', label='Bee')
    plot2 = plt.plot(xlist[1], ylist[1], 'o', label='NSGAII')
    plot3 = plt.plot(xlist[2], ylist[2], 'o', label='MOEAD')
    plt.legend(loc = 'upper left')
    #plt.savefig('simple plot.png',dpi = 200)
    plt.show()
    