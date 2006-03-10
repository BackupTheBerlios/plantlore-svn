#!C:\PROGRA~1\Python23\python.exe
# -*- coding: UTF-8 -*-

'''
$RCSfile: details.py,v $
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
          authenticationForm(script='raw.cgi', dsa=dsa) 

# get the schema object being used
schemaObj = prefs.schemas[schema]
debug + unicode(schemaObj)

debug + "ORIGINAL FILTER STRING: %s"%unicode(form['filter'].value, errors='replace')
filterObj = createFilterObjFromString(form['filter'].value, schemaObj)
debug + "FILTER OBJ: %s"%unicode(str(filterObj), errors='replace')

# generate the protocol
QG = QueryGenerator()
protocolXML = QG.getSearchProtocol(reqNS=schemaObj.NS, respNS=schemaObj.NS, count=False, filterObj=filterObj)
debug + "QUERY PROTOCOL CREATED:\n%s"%protocolXML

# query the wrapper
QD = QueryDispatcher(debug)
recStatus = QD.sendQuery(wrapper_url, protocolXML, security_role=security_role)
content = QD.getContent()
diagnostics = QD.getDiagnostics()

if content is None:
    # no wrapper results found
    debug + "ERROR: NO PROTOCOL CONTENT FOUND"
    print 'Content-Type: text/plain; charset=utf-8\n\n'
    print '-----------------------------'
    print 'There was no wrapper result !'
    print '-----------------------------'
    print '\n\nDebug data:'
    print '-----------'
    debug.escapeHtml = False
    print str(debug)
else:
    debug + "CONTENT ROOT %s"%content.name
    # apply stylesheet
    print 'Content-Type: text/xml; charset=utf-8\n'
    #print "<debug>%s</debug>"%str(debug)
    print content.serialize()


