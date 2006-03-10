#!C:\PROGRA~1\Python23\python.exe
# -*- coding: UTF-8 -*-

'''
$RCSfile: main.py,v $
$Revision: 400 $
$Author: markus $
$Date: 2005-10-19 17:24:36 +0200 (Mi, 19 Okt 2005) $
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


# DSA BY NEMELO BYT NIKDY NONE - POKUD BUDE, MELA BY BYT VYPSANA CHYBOVA HLASKA S RADOU CO SKONTROLOVAT A EMAIL NA ADMINA
# display global DSA select box?
if dsa is None:
    # use the datasource.html template to select a datasource first
    tmpl = PageMacro('Content', PageMacro.DELMODE)
    tmpl.load('Content', os.path.join(templateDir, 'error.html'))    
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
    tmpl['schema'] = schema
    # get the relevant form fields from preferences
    concepts = []
    if prefs.schemas.has_key(schema):
        debug + "Use schema %s to build the form."%schema
        schemaObj = prefs.schemas[schema]
        for label in schemaObj.form:
            conObj = schemaObj.concepts[label]
            conDict = {'label_display':label.replace('_',' '), 'label':label}
            if not conObj.cops == u'~':
                optionHtml = getDropDownHtml('cop%s'%label, [c for c in conObj.cops] )
            else:
                optionHtml = ''
            conDict['copOptions'] = optionHtml
            concepts.append(conDict)
        # create grouping drop down
        valDict = {}
        for c in schemaObj.form:
            valDict[c.replace('_',' ')] = c
        tmpl['groupby_options'] = getDropDownOptionHtml(vals=valDict, default=schemaObj.grouping)
    else:
        debug + "Cant find preferences for the selected schema %s."%schema
    tmpl.expand('Content', 'conceptlist', concepts)
    # create alternative schema selection
    tmpl['schema_options'] = getDropDownOptionHtml(vals=prefs.schemas.keys(), default=schema)

#
# print HTML !
#
printOverHTTP( tmpl, debug )
        

