#
# -*- coding: utf-8 -*-

'''General things all querytool scripts need.
It also adjusts the sys path via biocase.adjustpath.py .
This module should be executed via execfile('ABS PATH TO HERE'). 
'''

############################################################################################################
#
#   IMPORTS
#
#===========================================================================================================

import cgitb; cgitb.enable()
import cgi
import os, sys, string
from types import *
import urllib

import biocase.configuration
from biocase.querytool import __version__
from biocase.querytool.preferences import getPreferences, QTPreferences
from biocase.tools.htmltools  import ankerClass, escapeHtml, escapeBackslash, getDropDownHtml, getDropDownOptionHtml
from biocase.tools.templating import PageMacro, genOptionList
from biocase.datasources import getDsaList
from biocase.tools.debugging import DebugClass
from biocase.tools.various_functions import unique
from biocase.querytool.filter import *

import  md5, time, random, re
from stat import ST_MTIME


############################################################################################################
#
#   GENERAL FUNCTIONS
#
#===========================================================================================================
    
def createFilter(form, schemaObj, LOPClass=AndClass):
    global debug
    class paraClass:
        '''simple class to handle parameters'''
        def __init__(self):
            self.conceptObj = None
            self.value = None
            self.copType = '~'
    # build filter object
    filterObj = LOPClass()
    # first check if their is a filterstring
    if form.has_key('filter'):
        oldFilter = createFilterObjFromString( form.getUnicodeValue('filter'), schemaObj)
        debug + "OLD FILTER FROM STRING '%s' BECOMES '%s'"%(form.getUnicodeValue('filter'), unicode(oldFilter))
        debug + "  AS XML: %s"%(oldFilter._reprXML())
        filterObj.addOperand(oldFilter)
        
    else:
        debug + 'NO OLD FILTER FOUND'        
    # then check for additional operands
    args = {}
    for para in form.keys():
        # make underscores spaces again as in the prefs object
        if para[:3]=='con':
            label=para[3:]
            if args.has_key(label):
                # the parameter tuple already exists
                args[label].conceptObj = schemaObj.concepts[label]
                args[label].value = form.getUnicodeValue(para)
            else:
                # create new para object
                pObj = paraClass()
                pObj.conceptObj = schemaObj.concepts[label]
                pObj.value = form.getUnicodeValue(para)
                args[label] = pObj
        if para[:3]=='cop':
            label=para[3:]
            if args.has_key(label):
                # the parameter tuple already exists
                args[label].copType = form.getUnicodeValue(para)
            else:
                pObj = paraClass()
                pObj.copType = form.getUnicodeValue(para)
                args[label] = pObj
    debug + "FILTER ARGS: %s"%unicode(args)
    # remove empty args (Value=None)
    for label, arg in args.items():
        if arg.value is None or len(arg.value)==0:
            del args[label]
    debug + "FILTER ARGS CLEANED: %s"%unicode(args)
    # create new COP operands
    copclasses = {'~':LikeClass,'=':EqualsClass,'<':LesserClass,'>':GreaterClass}
    for label, pObj in args.items():
        copObj = copclasses[ pObj.copType ](pObj.conceptObj, pObj.value)
        debug + copObj
        #debug + " COP CREATED: %s"%copObj
        filterObj.addOperand( copObj )
    return getOptimizedFilterObj(filterObj, debug)


def printOverHTTP(templ, debug=None, diagnostics=None):
    print cfg.http_header
    if debug.display > 0:
        debug.escapeHtml = True
        if diagnostics is not None:
            templ['diagnostics'] = '''<p class="label">Diagnostics:</p>
            <pre>%s</pre>''' %( escapeBackslash(escapeHtml(string.join( diagnostics, '\n * ' ))) )
        templ['debug'] = '''<p class="label">Debugging:</p>
        <pre>%s</pre>''' %unicode(debug)
    try:
        print str(templ)
    except:
        templ['diagnostics'] = 'ERROR when printing diagnostics data into template!'
        templ['debug'] = 'ERROR when printing debug data into template!'
        print str(templ)



class FieldStorageForUnicode(cgi.FieldStorage):
    '''Transforms fieldstorage data into unicode with the given encoding when creating the object.'''
    ENCODING = 'utf-8'
    def __init__(self, encoding='utf-8'):
        self.__class__.ENCODING = encoding
        cgi.FieldStorage.__init__(self)
        self.QS = cgi.parse_qs( os.environ["QUERY_STRING"] )
        
    def has_key(self, key):
        '''Subclass method to also find raw querystring parameters.'''
        if cgi.FieldStorage.has_key(self, key):
            return True
        elif self.QS.has_key(key):
            return True
        else:
            return False
    
    def getvalue(self, key):
        '''Subclass method to also find raw querystring parameters.'''
        if cgi.FieldStorage.has_key(self, key):
            return cgi.FieldStorage.getvalue(self, key)
        else:
            return self.QS[key][0]
        
    def getUnicodeValue(self, para):
        # return a form value in unicode
        #return unicode( self.getvalue(para, None) , self.__class__.ENCODING)
        if self.has_key(para):
            return unicode( self.getvalue(para), self.__class__.ENCODING)
        else:
            return None
            

############################################################################################################
#
#   MAIN
#
#===========================================================================================================


# get global project configurations
cfg = biocase.configuration.Cfg('querytool')
# dirs used with the querytool
configDir        = os.path.join(cfg.configurationLocator, 'querytool') # querytool config dir
# debugging
debug = DebugClass()
diagnostics = None


#
# ANALYZE CGI ENVIRONMENT
#
form = FieldStorageForUnicode()

# debugging
if form.has_key('debug'):
    debug.display = True
else:
    debug.display = cfg.querytool.debug

# check datasource alias (DSA) to parse preferences etc.
if form.has_key('dsa'):
    dsa = form.getUnicodeValue('dsa')
    wrapper_url = '%s?dsa=%s' %(cfg.PyWrapperURL, dsa)
    prefs = getPreferences(dsa, cfg)
else:
    dsa = None
    wrapper_url = None
    prefs = QTPreferences()
    # check for skin to use:
    if form.has_key('skin'):
        prefs.skin = form.getUnicodeValue('skin')

# allow overriding of wrapper url
if form.has_key('wrapper_url'):
    wrapper_url = form.getUnicodeValue('wrapper_url')

# default directory for HTML templates
templateDir = os.path.join(configDir, 'skins', 'default') 
# use custom skin?
if prefs.skin is not None:
    fn = os.path.join(configDir, 'skins', prefs.skin)
    if os.path.isdir(fn):
        templateDir = fn
    else:
        debug + "Cannot find skin %s" % prefs.skin
        
# which protocol should be used?
if form.has_key('protocol'):
    protocol = form.getUnicodeValue('protocol')
else:
    protocol = 'BioCASe'

# which schema to use?
if form.has_key('schema'):
    schema = form.getUnicodeValue('schema')
else:
    schema = prefs.defaultSchema
    
# was there a security role passed?
if form.has_key('role'):
    security_role = form.getUnicodeValue('role')
else:
    security_role = None

# for debugging:
debug + "WRAPPER URL: %s" %(unicode(wrapper_url))
debug + "DSA: %s" %(unicode(dsa))
debug + "SECURITY ROLE: %s" % (security_role)
debug + "SCHEMA: %s" %(unicode(schema))
debug + "skin: %s" %(unicode(prefs.skin))
debug + "templateDir: %s" %(unicode(templateDir))
debug + unicode(prefs)



############################################################################################################
#
#   GENERAL FUNCTIONS - AUTENTIFICATION - ADD!!!
#
#===========================================================================================================

def authenticate(MD5passwd, login, clearPasswd, dsa=None):
    '''Check MD5 encrypted password with temporary DSA .password file.
    Removes file if older than 30 minutes.'''
    global cfg
    passwdFilename = '.passwordDATA'+ str(login)
    userFilename = '.userData'
    
    # check file
    # ***zj??cestu k souboru, kter mi zabezpe?e p?up - pejmenovala jsem ho na .passwordDATA (.password se jmenuje
    # ***soubor zabezpecujici pristup ke konfiguraci daneho dsa v biocase)
    if dsa is not None:
        passwdFile = os.path.join(cfg.datasourcesLocator, dsa, passwdFilename)
        userFile = os.path.join(cfg.datasourcesLocator, dsa, userFilename)
    else:
        # system admin part
        # *** nebude se tu pracovat s heslem v jine urovni --> passwdFile = ''
        #*** PUVODNE: passwdFile = os.path.join(cfg.datasourcesLocator, passwdFilename)
        passwdFile = ''
    # check if file is not older than 30 minutes
    if MD5passwd == '' or MD5passwd is None or not os.path.isfile(passwdFile) or os.stat(passwdFile)[ST_MTIME] + 1800 < int(time.time()):
        return authenticateInitially(clearPasswd, login, passwdFile, userFile)
    # compare md5 passwords from file and parameter
    content = file(passwdFile).read()
    if content == MD5passwd:
        # update filedate to now
        file(passwdFile, 'w').write(content)
        return MD5passwd
    return authenticateInitially(clearPasswd, login, passwdFile, userFile)
    
# ------------------------------------------------------------------------------------
def authenticateInitially(clearPasswd, login, passwdFile, userFile):
    '''Compare clear password with DSA or system config files.
    systemAdmin password allows to edit anything.
    datasource specific passwords are stored in the configtool prefs of the datasource
    and allow to edit that dsa only.
    If system wide authentication is required, leave the dsa parameter None.'''
    if clearPasswd == '' or clearPasswd is None:
        return False
    elif passwdFile == '' or passwdFile is None:
        return False        
    elif authenticateUser(clearPasswd, login, userFile):        
        return createAuthenPasswordFile(clearPasswd, passwdFile) 
    return False

#-------------------------------------------------------------------------------------
# *** projde soubor .userData a pokud, zde narazi na stejne logina  jmeno, co bylo zadano, tak bude uzivatel prihlasen
# *** bylo by dobre mit heslo ulozeno zakodovane - ??? bude admin psat primo do souboru .userData nebo bude k tomu existovat nejaka f-ce v plantlore???
def authenticateUser(clearPasswd, login, userFile):
    try:
      fUser = open(userFile, "r")
    except:  
      debug + "ERROR when reading .userData file"
      return False
      
    #Reads first line from file ".userData"
    line = fUser.readline()
    while line != "":          
      line = line.replace('\r\n','')
      line = line.replace('\n','')
      parseLine = re.findall(r'\b[^@]+', line)
      # test login and password
      if login == parseLine[0] and clearPasswd == parseLine[1]:
          return True
      #reads other line
      line = fUser.readline()

    return False
# ------------------------------------------------------------------------------------
def createAuthenPasswordFile(clearPasswd, passwdFile):
    #MD5Passwd = md5.new("%s-%f" %(clearPasswd, time.time()) ).digest()
    MD5Passwd = str( random.randint(100000000000000,999999999999999) )
    debug + "MD5 password file created: %s"%MD5Passwd
    file(passwdFile, 'w').write(MD5Passwd)
    return MD5Passwd

# ------------------------------------------------------------------------------------
def authenticationForm(script='main.py', dsa=None):
    global templateDir, debug, diagnostics
    tmpl = PageMacro('Content', PageMacro.DELMODE)
    tmpl.load('Content', os.path.join(plantloreDir, 'password.html'))
    if dsa is not None:
        tmpl['dsa'] = dsa
    tmpl['script'] = script
    # print HTML !
    printOverHTTP( tmpl, debug, diagnostics )    
    # stop script
    sys.exit()    
    
# dirs used with the querytool for 
# **** zmenit na, aby password.html se volalo z adresare skins/dsa
#skinsDir = os.path.join(cfg.configurationLocator, 'querytool', 'skins', 'default') 
plantloreDir = os.path.join(cfg.wwwLocator, 'querytool', 'plantlore') 

#login
login = form.getfirst('login', None)
debug + "Login clear=%s"%str(login)
# passwords
clearPasswd = form.getfirst('passwd', None)
debug + "Passwd clear=%s"%str(clearPasswd)
MD5Passwd = form.getfirst('id', None)
debug + "Passwd MD5=%s"%str(MD5Passwd)     
