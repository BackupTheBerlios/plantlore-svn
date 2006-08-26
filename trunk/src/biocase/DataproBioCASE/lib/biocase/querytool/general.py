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
import urllib, logging
import  re, random, time, md5
from stat import ST_MTIME

import biocase.configuration
from biocase.querytool import __version__
from biocase.querytool.preferences import getPreferences, QTPreferences
from biocase.tools.htmltools  import ankerClass, escapeHtml, escapeBackslash, getDropDownHtml, getDropDownOptionHtml
from biocase.tools.templating import PageMacro, genOptionList
from biocase.datasources import getDsaList
from biocase.tools.various_functions import unique
from biocase.querytool.filter import *
  

############################################################################################################
#
#   GENERAL FUNCTIONS
#
#===========================================================================================================
    
def createFilter(form, schemaObj, LOPClass=AndClass):
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
        oldFilter = createFilterObjFromString( form.getUnicodeValues('filter'), schemaObj)
        log.info("OLD FILTER FROM STRING '%s' BECOMES '%s'"%(form.getUnicodeValues('filter'), unicode(oldFilter)))
        log.debug("  AS XML: %s"%(oldFilter._reprXML()))
        filterObj.addOperand(oldFilter)
        
    else:
        log.info('NO OLD FILTER FOUND'        )
    # then check for additional operands
    args = {}
    for para in form.keys():
        # make underscores spaces again as in the prefs object
        if para[:3]=='con':
            label=para[3:]            
            if args.has_key(label):
                # the parameter tuple already exists
                args[label].conceptObj = schemaObj.getConcept(label)
                args[label].value = form.getUnicodeValues(para)
            else:
                # create new para object
                pObj = paraClass()
                pObj.conceptObj = schemaObj.getConcept(label)
                pObj.value = form.getUnicodeValues(para)
                args[label] = pObj
        if para[:3]=='cop':
            label=para[3:]
            if args.has_key(label):
                # the parameter tuple already exists
                args[label].copType = form.getUnicodeValues(para)
            else:
                pObj = paraClass()
                pObj.copType = form.getUnicodeValues(para)
                args[label] = pObj
    log.info("FILTER ARGS: %s"%unicode(args))
    # remove empty args (Value=None)
    for label, arg in args.items():
        if arg.value is None or len(arg.value)==0:
            del args[label]
    log.debug("FILTER ARGS CLEANED: %s"%unicode(args))
    # create new COP operands
    copclasses = {'~':LikeClass,'=':EqualsClass,'<':LesserClass,'>':GreaterClass}
    for label, pObj in args.items():
        copObj = binaryCopObjectFactory(CopClass=copclasses[ pObj.copType ], conceptObj=pObj.conceptObj, val=pObj.value)
        log.debug(copObj)
        #log.debug(" COP CREATED: %s"%copObj)
        filterObj.addOperand( copObj )
    return getOptimizedFilterObj(filterObj)

def logDiagnostics(diagnosticsList):
    log.debug("WRAPPER DIAGNOSTICS:")
    for d in diagnosticsList:
        log.debug(d[2])    
    
def printOverHTTP(templ):
    print cfg.http_header
    try:
        print str(templ)
    except:
        log.error("Couldnt print template.")



class FieldStorageForUnicode(cgi.FieldStorage):
    '''Transforms fieldstorage data into unicode with the given encoding when creating the object.'''
    ENCODING = 'utf-8'
    def __init__(self, encoding='utf-8'):
        self.__class__.ENCODING = encoding
        cgi.FieldStorage.__init__(self)
        self.QS = cgi.parse_qs( os.environ.get("QUERY_STRING","") )
        
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
            
    def getUnicodeValues(self, para):
        # return a list of form values in unicode
        #return unicode( self.getvalue(para, None) , self.__class__.ENCODING)
        if self.has_key(para):
            if type(self.getvalue(para)) in (ListType,TupleType):
                return [unicode( val, self.__class__.ENCODING) for val in self.getvalue(para)]
            else:
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
cfg._updateLogger('querytool')
# debugging to file
log = logging.getLogger("webapp.querytool")
log.info("--- NEW QUERYTOOL REQUEST ---")
# dirs used with the querytool
configDir        = os.path.join(cfg.configurationLocator, 'querytool') # querytool config dir
diagnostics = None


#
# ANALYZE CGI ENVIRONMENT
#
form = FieldStorageForUnicode()

# check datasource alias (DSA) to parse preferences etc.
if form.has_key('dsa'):
    dsa = form.getUnicodeValues('dsa')
    wrapper_url = '%s?dsa=%s' %(cfg.PyWrapperURL, dsa)
    prefs = getPreferences(dsa, cfg)
else:
    dsa = None
    wrapper_url = None
    prefs = QTPreferences()
    # check for skin to use:
    if form.has_key('skin'):
        prefs.skin = form.getUnicodeValues('skin')

# allow overriding of wrapper url
if form.has_key('wrapper_url'):
    wrapper_url = form.getUnicodeValues('wrapper_url')

# default directory for HTML templates
templateDir = os.path.join(configDir, 'skins', 'default') 
# use custom skin?
if prefs.skin is not None:
    fn = os.path.join(configDir, 'skins', prefs.skin)
    if os.path.isdir(fn):
        templateDir = fn
    else:
        log.warn("Cannot find skin %s" % prefs.skin)
        
# which protocol should be used?
if form.has_key('protocol'):
    protocol = form.getUnicodeValues('protocol').lower()
else:
    protocol = 'biocase'

log.debug(str(prefs))
# which schema to use?
if form.has_key('schema'):
    schemaObj = prefs.getSchema( form.getUnicodeValues('schema') )
    if schemaObj is None:
        raise "The requested schema %s is not supported by this datasource." % form.getvalue('schema')
else:
    schemaObj = prefs.defaultSchema
schema = None
if schemaObj is not None:
    schema = schemaObj.NS


# was there a security role passed?
if form.has_key('role'):
    security_role = form.getUnicodeValues('role')
else:
    security_role = None

# for debugging:
log.info( "WRAPPER URL: %s" %(unicode(wrapper_url)))
log.info("DSA: %s" %(unicode(dsa)))
log.info("PROTOCOL: %s" % (protocol))
log.info("SECURITY ROLE: %s" % (security_role))
log.info("SCHEMA: %s" %(unicode(schema)))
log.info("SKIN: %s" %(unicode(prefs.skin)))
log.debug("TemplateDir: %s" %(unicode(templateDir)))
log.debug(unicode(prefs))


############################################################################################################
#
#   GENERAL FUNCTIONS
#
#===========================================================================================================
def printOverHTTP(templ):
    print cfg.http_header
    print str(templ)

# ------------------------------------------------------------------------------------
def authenticate(MD5passwd, login, clearPasswd, dsa=None):
    '''Check MD5 encrypted password with temporary DSA .password file.
    Removes file if older than 30 minutes.'''
    global cfg    
    passwdFilename = '.passwordDATA'+ str(login)
    userFilename = '.userData'
    
    # check file
    if dsa is not None:        
        passwdFile = os.path.join(cfg.datasourcesLocator, dsa, passwdFilename)
        userFile = os.path.join(cfg.datasourcesLocator, dsa, userFilename)
    else:
        # system admin part
        passwdFile = ''
    # check if file is not older than 30 minutes
    if MD5passwd == '' or MD5passwd is None or not os.path.isfile(passwdFile) or os.stat(passwdFile)[ST_MTIME] + 1800 < int(time.time()):
        return authenticateInitially(clearPasswd, login, passwdFile, userFile)
    log.debug("MD5 password file exists at %s" % passwdFile)        
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
    global cfg
    if clearPasswd == '' or clearPasswd is None:
        return False
    elif passwdFile == '' or passwdFile is None:
        return False           
    elif authenticateUser(clearPasswd, login, userFile):        
        return createAuthenPasswordFile(clearPasswd, passwdFile) 
    return False    

#-------------------------------------------------------------------------------------
def authenticateUser(clearPasswd, login, userFile):
    try:
      fUser = open(userFile, "r")
    except:  
      log.debug(" ERROR when reading .userData file")
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
    log.debug(" MD5 password file created: %s"%MD5Passwd)
    file(passwdFile, 'w').write(MD5Passwd)
    return MD5Passwd

# ------------------------------------------------------------------------------------
def authenticationForm(script='main.py', dsa=None):
    global templateDir
    tmpl = PageMacro('Content', PageMacro.DELMODE)
    tmpl.load('Content', os.path.join(templateDir, 'password.html'))
    if dsa is not None:
        tmpl['dsa'] = dsa
    tmpl['script'] = script
    # print HTML !
    printOverHTTP( tmpl )    
    # stop script
    sys.exit()
    
# ------------------------------------------------------------------------------------
def readTmpCmFile(cmfObj, cmFile, psfObj):
    global cfg
    cmFilePickle = cmFile + cfg.configtool.tmpCmfPickleSuffix
    if os.path.isfile( cmFilePickle ):
        log.debug("Read existing pickled tmp file %s"%(cmFilePickle))
        isGood = cmfObj.__unpickle__(filename=cmFilePickle)
        if not isGood:
            log.debug("Failed to read pickled file. Read original CMF xml-file.")
            readOriginalCmFile(cmfObj, cmFile=cmFile, psfObj=psfObj)
    else:
        readOriginalCmFile(cmfObj, cmFile=cmFile, psfObj=psfObj)
    log.debug("altRootTableAliasSPICE = %s" % unicode(cmfObj.altRootTableAliasSPICE))
       

# ------------------------------------------------------------------------------------
def readOriginalCmFile(cmfObj, psfObj, cmFile=None):
    global cfg
    log.debug("Read original CMF xml-file %s"%(cmFile))
    try:
        cmfObj.loadCMFdata( filename=cmFile, psfObj=psfObj, pickle=1 )
    except TableConfigError:
        log.debug("CMF file %s and the PSF dont match. Table config errors."%(cmFile))

#projectDir
plantloreDir = os.path.join(cfg.wwwLocator, 'querytool', 'plantlore') 
#login
login = form.getfirst('login', None)
log.debug("Login clear=%s"%str(login))

# passwords
clearPasswd = form.getfirst('passwd', None)
log.debug("Passwd clear=%s"%str(clearPasswd))
MD5Passwd = form.getfirst('id', None)
log.debug("Passwd MD5=%s"%str(MD5Passwd))   

        
