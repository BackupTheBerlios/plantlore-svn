#!C:\PROGRA~1\Python23\python.exe
# -*- coding: UTF-8 -*-

'''
$RCSfile: grouping.py,v $
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


############################################################################################################
#
#   MAIN
#
#===========================================================================================================

if dsa == "plantlorePSWD":
        # check authentication!
        MD5Passwd = authenticate(MD5Passwd, login, clearPasswd, dsa)
        if not MD5Passwd:
            authenticationForm(script='grouping.cgi', dsa=dsa)  

tmpl = PageMacro('Content', PageMacro.DELMODE)
tmpl.load('Content', os.path.join(templateDir, 'grouping.html'))

try:
   # get the schema object being used
   schemaObj = prefs.schemas[schema]
except:
   tmpl.load('Content', os.path.join(templateDir, 'error.html')) 
   printOverHTTP( tmpl, debug, diagnostics )
   sys.exit()

# build a new filter object from form values
filterObj = createFilter(form, schemaObj)
debug + "FILTER OBJ: %s"%str(filterObj)

# generate the protocol
QG = QueryGenerator()
groupConcept = form['groupby'].value
protocolXML = QG.getScanProtocol(concept=schemaObj.concepts[groupConcept].path, NS=schemaObj.NS, filterObj=filterObj)
debug + "QUERY PROTOCOL CREATED:\n%s"%escapeHtml(protocolXML)

# update template
tmpl['dsa'] = dsa
tmpl['id'] = MD5Passwd
tmpl['schema'] = schema
tmpl['limit'] = str(schemaObj.limit)
if filterObj is not None:
    tmpl['filter'] = str(filterObj)
tmpl['filter_display'] = escapeHtml( str(filterObj).replace('_', ' ') )
tmpl['groupingCon'] = groupConcept
tmpl['groupby_options'] = getDropDownOptionHtml(vals=['---None---']+schemaObj.form, default='---None---')
if wrapper_url is not None:
    tmpl['wrapper_url'] = wrapper_url

# query the wrapper
QD = QueryDispatcher(debug)
recStatus = QD.sendQuery(wrapper_url, protocolXML, security_role=security_role)
if recStatus is None:
	# wrapper results:
	tmpl['hits'] = "0"
	debug.display = True
else:
	valuelist = QD.getScanValues()
	diagnostics = QD.getDiagnostics()

	# wrapper results:
	tmpl['hits'] = str(recStatus.count)
	valueTemplateList = [{'val':v} for v in valuelist]
	tmpl.expand('Content', 'grouplist', valueTemplateList)

#
# print HTML !
#
printOverHTTP( tmpl, debug, diagnostics )
