#!C:\PROGRA~1\PYTHON\PYTHON23\python.exe
# -*- coding: UTF-8 -*-

'''
$RCSfile: details.py,v $
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
          authenticationForm(script='details.cgi', dsa=dsa)  

tmpl = PageMacro('Content', PageMacro.DELMODE)
tmpl.load('Content', os.path.join(templateDir, 'details.html'))

try:
    
    # which detail do we need to create?
    detail = form['detail'].value
    detailObj = schemaObj.details[detail]
    
    log.info("PREFS DETAILS OBJ: %s"%unicode(str(detailObj), errors='replace'))
    
    # build a new filter object from form values of the record identifiers.
    # First create list of needed COP objects
    if protocol == 'digir':
        argList = []
        for para, recID in detailObj.recID.items():
            conObj = Concept(NS=schema, path=recID.path, label=para)
            if form.getfirst(para, None) is not None:
                argList.append( EqualsClass(conObj, form.getfirst(para, None)) )
        if len(argList) > 1:
            currLOP = AndClass()
            filterObj = currLOP
            for cop in argList[:-2]:
                currLOP.addOperand(cop)
                newLOP = AndClass()
                currLOP.addOperand(newLOP)
                currLOP = newLOP
            currLOP.addOperand( argList[-2] )
            currLOP.addOperand( argList[-1] )
        else:
            filterObj = argList[0]
    else:
        filterObj = AndClass()
        for para, recID in detailObj.recID.items():
            # create an equals cop for every recID
            if form.getfirst(para, None) is not None:
                conObj = Concept(NS=schema, path=recID.path, label=para)
                cop = EqualsClass(conObj, form.getfirst(para, None))
                filterObj.addOperand(cop)
    log.info("FILTER OBJ: %s"%unicode(str(filterObj), errors='replace'))
    
    # generate the protocol
    QG = QueryGenerator(protocol)
        
    protocolXML = QG.getSearchProtocol(NS=schemaObj.NS, respNS=schemaObj.NS, count=False, filterObj=filterObj, destination=wrapper_url)
    log.info("QUERY PROTOCOL CREATED:\n%s"%(protocolXML))
    
    # query the wrapper
    QD = QueryDispatcher(protocolNS=protocol)
    recStatus = QD.sendQuery(wrapper_url, protocolXML, security_role=security_role)
    
    content = QD.getContent()
    logDiagnostics(QD.getDiagnostics())
            
    if content is None:
        # no wrapper results found
        log.info("NO CONTENT ROOT FOUND.")
        stylesheetResult = "<strong>%s</strong>" % detailObj.notAvailableMessage
    else:
        log.info("CONTENT ROOT %s"%content.name)
        # apply stylesheet
        stylesheetResult = transformXML(docDOM=content, xslLoc=os.path.join(templateDir, detailObj.stylesheet))
    
    # update template
    tmpl['dsa'] = dsa
    tmpl['id'] = MD5Passwd
    tmpl['login'] = login
    tmpl['schema'] = schema
    tmpl['protocol'] = protocol
    tmpl['filter'] = str(filterObj)
    tmpl['filter_display'] = str(filterObj).replace('_', ' ')
    tmpl['XSL'] = stylesheetResult
    if wrapper_url is not None:
        tmpl['wrapper_url'] = wrapper_url
    
    #
    # print HTML !
    #
    printOverHTTP( tmpl )
except:
    tmpl.load('Content', os.path.join(plantloreDir, 'error1.html')) 
    printOverHTTP( tmpl)
    sys.exit()
