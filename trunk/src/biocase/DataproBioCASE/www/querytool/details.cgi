#!C:\PROGRA~1\Python23\python.exe
# -*- coding: UTF-8 -*-

'''
$RCSfile: details.py,v $
$Revision: 456 $
$Author: markus $
$Date: 2005-11-03 15:23:30 +0100 (Do, 03 Nov 2005) $
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
    # get the schema object being used
    schemaObj = prefs.schemas[schema]

    # which detail do we need to create?
    detail = form['detail'].value
    detailObj = schemaObj.details[detail]

    debug + "PREFS DETAILS OBJ: %s"%unicode(str(detailObj), errors='replace')
    
    # build a new filter object from form values of the record identifiers
    filterObj = AndClass()
    for para, recID in detailObj.recID.items():
        # create an equals cop for every recID
        conObj = Concept(label=para, path=recID.path)
        cop = EqualsClass(conObj, form.getfirst(para, None))
        filterObj.addOperand(cop)
    debug + "FILTER OBJ: %s"%unicode(str(filterObj), errors='replace')
    
    # generate the protocol
    QG = QueryGenerator()
    protocolXML = QG.getSearchProtocol(reqNS=schemaObj.NS, respNS=schemaObj.NS, count=False, filterObj=filterObj, destination=wrapper_url)
    debug + "QUERY PROTOCOL CREATED:\n%s"%(protocolXML)
    
    # query the wrapper
    QD = QueryDispatcher(debug)
    recStatus = QD.sendQuery(wrapper_url, protocolXML, security_role=security_role)
    content = QD.getContent()
    diagnostics = QD.getDiagnostics()
    
    if content is None:
        # no wrapper results found
        debug + "NO CONTENT ROOT FOUND."
        stylesheetResult = detailObj.notAvailableMessage
        debug.display = True
    else:
        debug + "CONTENT ROOT %s"%content.name
        # apply stylesheet
        stylesheetResult = transformXML(docDOM=content, xslLoc=os.path.join(templateDir, detailObj.stylesheet), debug=debug)
    
    # update template
    tmpl['dsa'] = dsa
    tmpl['id'] = MD5Passwd
    tmpl['login'] = login
    tmpl['schema'] = schema
    tmpl['filter'] = str(filterObj)
    tmpl['filter_display'] = str(filterObj).replace('_', ' ')
    tmpl['XSL'] = stylesheetResult
    if wrapper_url is not None:
        tmpl['wrapper_url'] = wrapper_url
    
    #
    # print HTML !
    #
    printOverHTTP( tmpl, debug, diagnostics )
    
except:
    tmpl.load('Content', os.path.join(templateDir, 'error.html')) 
    printOverHTTP( tmpl, debug, diagnostics )
    sys.exit()
    
