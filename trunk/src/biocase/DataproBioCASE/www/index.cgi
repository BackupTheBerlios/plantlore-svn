#!C:\PROGRA~1\Python23\python.exe
# -*- coding: UTF-8 -*-

import os, sys
# ***** include the biocase.lib directory in the python sys path for module importing *****
execfile( os.path.abspath(os.path.join(os.path.dirname( __file__ ), os.path.pardir, 'lib', 'biocase', 'adjustpath.py' ) ) )


#Import some stuff-----------------------------------------
import cgitb; cgitb.enable()
import string
#-----------------------------------------------------------------------------------------------------------
import biocase.tools.templating
import biocase.configuration



# get config data
cfg=biocase.configuration.Cfg()

#------------------------------------------------------------------------------------

pm = biocase.tools.templating.PageMacro('Content', biocase.tools.templating.PageMacro.KEEPMODE)
pm.load('Content', os.path.join(cfg.wwwLocator, 'index.html'))

#Print the headers	
print 'Content-Type: text/html; charset=UTF-8'
print # Blank line marking end of HTTP headers

print pm

    

    
