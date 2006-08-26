#!C:\PROGRA~1\PYTHON\PYTHON23\python.exe
# -*- coding: UTF-8 -*-

'''
$RCSfile: main.py,v $
$Revision: 735 $
$Author: markus $
$Date: 2006-06-26 13:09:24 +0200 (Mon, 26 Jun 2006) $
The BioCASE querytool
'''

import os, sys
# ***** include the biocase.lib directory in the python sys path for importing *****
execfile( os.path.abspath( os.path.join( os.path.dirname( __file__ ), os.pardir, os.pardir, 'lib', 'biocase', 'adjustpath.py' ) ))

from biocase.querytool.general import *


############################################################################################################
#
#   MAIN
#
#===========================================================================================================


# display global DSA select box?
if dsa is None:
    # use the datasource.html template to select a datasource first
    tmpl = PageMacro('Content', PageMacro.DELMODE)
    tmpl.load('Content', os.path.join(plantloreDir, 'error1.html')) 
    # get list of available dsa's 
else:
    #PRIDANO
    if dsa == "plantlorePSWD":
        # check authentication!
        MD5Passwd = authenticate(MD5Passwd, login, clearPasswd, dsa)
        if not MD5Passwd:
            authenticationForm(script='main.cgi', dsa=dsa)    

    # generate the search form for this dsa. use the form.html template
    tmpl = PageMacro('Content', PageMacro.DELMODE)
    tmpl.load('Content', os.path.join(templateDir, 'form.html'))
    tmpl['wrapper_url'] = wrapper_url
    tmpl['dsa'] = dsa
    tmpl['id'] = MD5Passwd
    tmpl['login'] = login
    # get the relevant form fields from preferences
    concepts = []
    if schemaObj is not None:                
        log.info("Use schema %s to build the form."%schema)       
        schema = schemaObj.NS
        for conObj in schemaObj.concepts:
            conDict = {'label_display':conObj.label.replace('_',' '), 'label':conObj.label}
            if not conObj.cops == u'~':
                optionHtml = getDropDownHtml('cop%s'%conObj.label, [c for c in conObj.cops] )
            else:
                optionHtml = ''
            conDict['copOptions'] = optionHtml
            concepts.append(conDict)
        # create grouping drop down
        valDict = {}
        for c in schemaObj.concepts:
            valDict[c.label.replace('_',' ')] = c.label
        tmpl['groupby_options'] = getDropDownOptionHtml(vals=valDict, default=schemaObj.grouping.label)
    else:
        log.info("Cant find preferences for the selected schema %s."%schema)
    tmpl.expand('Content', 'conceptlist', concepts)
    # create alternative schema selection
    tmpl['schema'] = schema
    tmpl['schema_options'] = getDropDownOptionHtml(vals=dict([('','')] + [(s.label,s.NS) for s in prefs.schemas if s.NS != schema and s.hasConcepts()]))

#
# print HTML !
#
printOverHTTP( tmpl )
        

