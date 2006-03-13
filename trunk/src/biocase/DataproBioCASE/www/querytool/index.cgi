#!C:\PROGRA~1\Python23\python.exe
# -*- coding: UTF-8 -*-

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


tmpl = PageMacro('Content', PageMacro.DELMODE)
tmpl.load('Content', os.path.join(plantloreDir, 'index.html')) 
tmpl['dsa'] = 'plantlore'
printOverHTTP( tmpl, debug, diagnostics )
sys.exit()
