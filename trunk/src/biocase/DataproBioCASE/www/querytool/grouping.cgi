#!C:\PROGRA~1\PYTHON\PYTHON23\python.exe
# -*- coding: UTF-8 -*-

'''
$RCSfile: grouping.py,v $
$Revision: 735 $
$Author: markus $
$Date: 2006-06-26 13:09:24 +0200 (Mon, 26 Jun 2006) $
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
    
    # build a new filter object from form values
    filterObj = createFilter(form, schemaObj)
    log.info("FILTER OBJ: %s"%str(filterObj))
    
    # generate the protocol
    QG = QueryGenerator(protocol)
    groupConcept = form['groupby'].value
    protocolXML = QG.getScanProtocol(concept=schemaObj.getConcept(groupConcept).path, NS=schemaObj.NS, filterObj=filterObj)
    log.info("QUERY PROTOCOL CREATED:\n%s"%escapeHtml(protocolXML))
    
    # update template
    tmpl['dsa'] = dsa
    tmpl['id'] = MD5Passwd
    tmpl['login'] = login
    tmpl['schema'] = schema
    tmpl['limit'] = str(schemaObj.limit)
    if filterObj is not None:
        tmpl['filter'] = str(filterObj)
    tmpl['filter_display'] = escapeHtml( str(filterObj).replace('_', ' ') )
    tmpl['groupingCon'] = groupConcept
    tmpl['groupby_options'] = getDropDownOptionHtml(vals=['---None---']+[c.label for c in schemaObj.concepts], default='---None---')
    if wrapper_url is not None:
        tmpl['wrapper_url'] = wrapper_url
    
    # query the wrapper
    QD = QueryDispatcher(protocolNS=protocol)
    recStatus = QD.sendQuery(wrapper_url, protocolXML, security_role=security_role)
    if recStatus is None:
        # wrapper results:
        tmpl['hits'] = "0"
    else:
        valuelist = QD.getScanValues()
        logDiagnostics(QD.getDiagnostics())
    
    
        # wrapper results:
        tmpl['hits'] = str(recStatus.count)
        valueTemplateList = [{'val':v} for v in valuelist]
        tmpl.expand('Content', 'grouplist', valueTemplateList)
    
    #
    # print HTML !
    #
    printOverHTTP( tmpl )
except:
    tmpl.load('Content', os.path.join(plantloreDir, 'error1.html')) 
    printOverHTTP( tmpl)
    sys.exit()
