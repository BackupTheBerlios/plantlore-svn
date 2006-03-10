#!C:\PROGRA~1\Python23\python.exe
# -*- coding: UTF-8 -*-

'''
$RCSfile: recordlist.py,v $
$Revision: 400 $
$Author: markus $
$Date: 2005-10-19 17:24:36 +0200 (Mi, 19 Okt 2005) $
The BioCASE querytool
'''

import os, sys
# ***** include the biocase.lib directory in the python sys path for importing *****
execfile( os.path.abspath( os.path.join( os.path.dirname( __file__ ), os.pardir, os.pardir, 'lib', 'biocase', 'adjustpath.py' ) ))

from biocase.querytool.general import *
from biocase.querytool.querydispatcher import QueryDispatcher
from biocase.querytool.querygenerator import QueryGenerator
from biocase.tools.xmllibtools import transformXML



############################################################################################################
#
#   MAIN
#
#===========================================================================================================

if dsa == "plantlorePSWD":
      # check authentication!
      MD5Passwd = authenticate(MD5Passwd, login, clearPasswd, dsa)
      if not MD5Passwd:
          authenticationForm(script='recordlist.cgi', dsa=dsa)  

#
# REDIRECT TO GROUPING.PY ?
#
if form.has_key('groupby') and form['groupby'].value is not None and form['groupby'].value != '---None---':
    execfile( os.path.abspath( os.path.join( os.path.dirname( __file__ ), 'grouping.cgi' ) ))
    sys.exit()
 
#
# PRESENT THE RECORDLIST PAGE
#    
tmpl = PageMacro('Content', PageMacro.DELMODE)
tmpl.load('Content', os.path.join(templateDir, 'recordlist.html'))

try:
   # get the schema object being used
   schemaObj = prefs.schemas[schema]
except:
   tmpl.load('Content', os.path.join(templateDir, 'error.html')) 
   printOverHTTP( tmpl, debug, diagnostics )
   sys.exit()

# build a new filter object from form values
filterObj = createFilter(form, schemaObj)
debug + "FILTER OBJ: %s"%unicode(filterObj)

# process paging history and set new start/limit values
limit = schemaObj.limit
start = int( form['start'].value )
if form.has_key('history'):
    history = string.split(form['history'].value, ',')
    thisPagingIndex = len(history)+1
else:
    history = []
    thisPagingIndex = 1

# update template
tmpl['dsa'] = dsa
tmpl['id'] = MD5Passwd
tmpl['schema'] = schema
tmpl['filter'] = str(filterObj)
tmpl['filter_display'] = escapeHtml( str(filterObj).replace('_', ' ') )
if wrapper_url is not None:
    tmpl['wrapper_url'] = wrapper_url

# generate the protocol
QG = QueryGenerator()
protocolXML = QG.getSearchProtocol(reqNS=schemaObj.NS, respNS=schemaObj.NS, start=start, limit=limit, count=False, filterObj=filterObj)
debug + "QUERY PROTOCOL CREATED:\n%s"%(protocolXML)

# query the wrapper
QD = QueryDispatcher(debug)
recStatus = QD.sendQuery(wrapper_url, protocolXML, security_role=security_role)
if recStatus is None:
	# wrapper error occured !!!
	debug.display = True
	printOverHTTP( tmpl, debug, diagnostics )
	sys.exit()
content = QD.getContent()
diagnostics = QD.getDiagnostics()


#
# calculate new paging indices, history etc
#
# new/next start index
nextIndex = recStatus.start + recStatus.count + recStatus.drop
nextNum = ''
pagingList1 = []
pagingList2 = []
i = 1
for h in history:
    if int(h) < recStatus.start:
        pagingDict={}
        pagingDict['label_num1']=str(i)
        pagingDict['start_index1']=str(h)
        pagingList1.append(pagingDict)
    elif int(h) == recStatus.start:
        thisPagingIndex = i
    else:
        pagingDict={}
        pagingDict['label_num2']=str(i)
        pagingDict['start_index2']=str(h)
        pagingList2.append(pagingDict)
    i += 1
# next button
if recStatus.total > nextIndex:
    # there are more records in the DB. Provide paging to next
    nextNum = 'next'

# append history list if needed
if len(history)==0 or int(max(history)) < int(start):
    history.append(start)

# apply stylesheet
paras = {'dsa':dsa, 'wrapper_url':wrapper_url, 'id':MD5Passwd}
stylesheetResult = transformXML(docDOM=content, xslLoc=os.path.join(templateDir, schemaObj.reclistXSL), paras=paras, debug=debug)

# update template with wrapper results
tmpl['result_start'] = str(recStatus.start+1)
tmpl['result_ende'] = str(nextIndex)
tmpl['result_count'] = str(recStatus.count)
tmpl['history'] = string.join([str(h) for h in history], ',')
tmpl['XSL'] = stylesheetResult
# paging
tmpl.expand('Content', 'paging_list1', pagingList1)
tmpl['num_this'] = str( thisPagingIndex )
tmpl.expand('Content', 'paging_list2', pagingList2)
tmpl['next_start'] = str(nextIndex)
tmpl['next_num'] = nextNum

#
# print HTML !
#
printOverHTTP( tmpl, debug, diagnostics )
