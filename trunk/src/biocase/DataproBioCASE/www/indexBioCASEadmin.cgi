#!C:\PROGRA~1\Python23\python.exe
# -*- coding: UTF-8 -*-

'''
$RCSfile: index.cgi,v $
$Revision: 400 $
$Author: markus $
$Date: 2005-10-19 17:24:36 +0200 (Mi, 19 Okt 2005) $
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
    
    
    #Transformaion of the metadata -----------------------------------------------------
    xsl_path = os.path.join( cfg.xslLocator, 'service_metadata.xsl')
    xml_path = os.path.join( cfg.configurationLocator, 'metadata.xml')
    
    
    #Do the transformation of the biocase\configuration\pywrapper\metadata.xml file to HTML to present it
    #If the necesary libraries are not installed then do not process the metadata and present a message inicating it.
    
    #try:
    #	import libxml2
    #	import libxslt
    #
    #	styledoc = libxml2.parseFile(xsl_path)
    #	style = libxslt.parseStylesheetDoc(styledoc)
    #	doc = libxml2.parseFile(xml_path)
    #	result = style.applyStylesheet(doc, None)
    #	metadata = style.saveResultToString(result)	# get the metadata transformed
    #	service_name = doc.children.next.children.next.content
    #	style.freeStylesheet()
    #	doc.freeDoc()
    #	result.freeDoc()
    #	
    #	
    #except:
    #	metadata = 'The metadata is not available, please check that you have the <a href="utilities/testlibs.cgi">libxml2 library</a> installed in your system and that you have set up your metadata files.'
    #	service_name = 'not available'	
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
    

    
