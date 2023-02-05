#!/usr/bin/env python3

import sys
import numpy as np
import matplotlib
matplotlib.use('Agg')
from matplotlib import pyplot as plt, ticker

x_Axis = ["1", "2", "4", "8"]

for outFile in ["query1", "query2", "query3_df", "query3_rdd", "query4", "query5"]:
    y_Axis = []
    fp = open("../"+outFile + ".txt")
    
    for numExec in [1, 2, 4, 8]:
        sum = 0
        for i in range(20):
            sum += float(fp.readline())
        y_Axis.append(sum/20.0)
    print(y_Axis)
    fig, ax = plt.subplots()
    ax.bar(x_Axis, y_Axis, width=0.9)
    plt.title("Execution Time per Number of Executors")
    ax.set_xlabel("Number of Executors")
    ax.set_ylabel("Execution Time (sec)")
    plt.savefig("./" + outFile + "_time.png", bbox_inches="tight")