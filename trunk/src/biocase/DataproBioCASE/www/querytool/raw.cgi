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



############################################################################################################
#
#   MAIN
#
#===========================================================================================================

if dsa == "plantlorePSWD":
      # check authentication!
      MD5Passwd = authenticate(MD5Passwd, login, clearPasswd, dsa)
      if not MD5Passwd:
          authenticationForm(script='raw.cgi', dsa=dsa) 

log.info("ORIGINAL FILTER STRING: %s"%unicode(form['filter'].value, errors='replace'))
filterObj = createFilterObjFromString(form['filter'].value, schemaObj)
log.info("FILTER OBJ: %s"%unicode(str(filterObj), errors='replace'))

# generate the protocol
QG = QueryGenerator(protocol)
protocolXML = QG.getSearchProtocol(NS=schemaObj.NS, respNS=schemaObj.NS, count=False, filterObj=filterObj, destination=wrapper_url)
log.info("QUERY PROTOCOL CREATED:\n%s"%protocolXML)

# query the wrapper
QD = QueryDispatcher(protocolNS=protocol)
recStatus = QD.sendQuery(wrapper_url, protocolXML, security_role=security_role)

content = QD.getContent()
logDiagnostics(QD.getDiagnostics())


if content is None:
    # no wrapper results found
    log.info("ERROR: NO PROTOCOL CONTENT FOUND")
    print 'Content-Type: text/plain; charset=utf-8\n\n'
    print '-----------------------------'
    print 'There was no wrapper result !'
    print 'Please check debug log.'
    print '-----------------------------'
else:
    log.debug("CONTENT ROOT %s"%content.name)
    # apply stylesheet
    print 'Content-Type: text/xml; charset=utf-8\n'
    print content.serialize()


