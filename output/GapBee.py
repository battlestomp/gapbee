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
from matplotlib.lines import Line2D

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
    Datalist1 = ReadDataFile('bee(5.15)')
    Datalist2 = ReadDataFile('nsga5.15')
    xlist = [[],[],[]]
    ylist = [[],[],[]]
    for line in Datalist1:
        xlist[0].append(line[0])
        ylist[0].append(line[1])
    for line1 in Datalist2:
        xlist[1].append(line1[0])
        ylist[1].append(line1[1])
    plt.grid(True)    
    
    #plt.title('$5 ** 15$')
    plt.xlabel(r"$f_r(x) $", fontsize=20)
    plt.ylabel(r'$f_o(x)$' , fontsize=20)
    markers = []
    for m in Line2D.markers:
        try:
            if len(m) == 1 and m != ' ':
                markers.append(m)
        except TypeError:
            pass
    styles = markers + [r'$\lambda$',r'$\bowtie$',r'$\circlearrowleft$', r'$\clubsuit$', r'$\checkmark$']
    style = styles[11]
    plot1 = plt.plot(xlist[0], ylist[0], 'o', label='multi-BEE', marker=styles[11], markersize=15, color='black',)
    plot2 = plt.plot(xlist[1], ylist[1], 'o', label='NSGA-II', marker=styles[9], markersize=15, color='black',)
    plt.legend(loc = 'upper right')
    #plt.xlim(270, 295)
    #plt.ylim(118, 130)
    #plt.savefig('simple plot.png',dpi = 200)
    plt.show()
    