'''
Created on 12.11.2014

@author: Leonid Glanz
'''
import fnmatch
import os


resultsPath = "/Users/leonidglanz/FindBugs/res"
outputFile = "/Users/leonidglanz/FindBugs/tim/results.csv"

def extract(extractPath, out):
    outFile = open(out, "wb")
    for root, _, files in os.walk(extractPath):
        for items in fnmatch.filter(files, "*.html"):
            f = open(root + "/" + items, "rb")
            str1 = f.read()
            f.close()
            idx = str1.find("<td class=\"summary-priority-all\">")
            print root + "/" + items
            if idx != -1:
                str1 = str1[idx + 33:]
                idx = str1.find("<")
                outFile.write(root + "/" + items + ";" + str1[:idx] + "\n")
                outFile.flush()
            else:
                outFile.write(root + "/" + items + ";0\n")
                outFile.flush()
    outFile.close()
        
extract(resultsPath, outputFile)
