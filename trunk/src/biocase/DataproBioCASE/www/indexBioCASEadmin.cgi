#!C:\PROGRA~1\PYTHON\PYTHON23\python.exe
# -*- coding: UTF-8 -*-

'''
$RCSfile: index.cgi,v $
$Revision: 743 $
$Author: markus $
$Date: 2006-06-26 14:30:31 +0200 (Mon, 26 Jun 2006) $
The deafult BioCASe provider software page
'''


import os, sys
# ***** include the biocase.lib directory in the python sys path for module importing *****
execfile( os.path.abspath(os.path.join(os.path.dirname( __file__ ), os.path.pardir, 'lib', 'biocase', 'adjustpath.py' ) ) )

try:
    #Import some stuff-----------------------------------------
    import cgitb; cgitb.enable()
    import string
    #-----------------------------------------------------------------------------------------------------------
    import biocase.tools.templating
    import biocase.configuration
    import biocase.datasources
    from biocase import __version__
    
    
    # get config data
    cfg=biocase.configuration.Cfg()
    
    service_name = 'BioCASe Provider Software 2'
    metadata = ''
    
    #------------------------------------------------------------------------------------
    
    pm = biocase.tools.templating.PageMacro('Content', biocase.tools.templating.PageMacro.KEEPMODE)
    pm.load('Content', os.path.join(cfg.wwwLocator, '_index.html'))
    pm['ServiceTitle'] = service_name
    pm['Metadata'] = metadata
    pm['version']  = __version__
    
    #Now get the list of datasources available in the data provider
    dsaListOfHash = [{'dsa':dsaObj.name} for dsaObj in biocase.datasources.getDsaList()]
    pm.expand('Content', 'DataSourcesList', dsaListOfHash )
    
    
    #Print the headers  
    print 'Content-Type: text/html; charset=UTF-8'
    print # Blank line marking end of HTTP headers
    
    print pm

except ImportError:
    # redirect to lib test page
    print "Location: utilities/testlibs.cgi"
    print
    

    