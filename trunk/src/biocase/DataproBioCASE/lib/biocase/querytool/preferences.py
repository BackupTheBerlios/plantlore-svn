# -*- coding: utf8 -*-
'''
querytool preferences parsing
###############################
$RCSfile: preferences.py,v $
$Revision: 614 $
$Author: markus $
$Date: 2006-04-05 18:47:18 +0200 (Wed, 05 Apr 2006) $
'''


import os.path
import xml.sax, xml.sax.handler
from biocase.wrapper.errorclasses         import *
from biocase.tools.various_functions     import isTrue
from biocase.querytool.filter     import Concept
import logging
# debugging to file
log = logging.getLogger("webapp.querytool")



# ------------------------------------------------------------------------------------
class QTPreferences:
    '''Querytool preferences class. Stores datasource specific preferences.'''
    def __init__(self):
        self.skin = 'default'
        self.defaultSchema = None # first schemaObj of the QT prefs file
        self.schemas  = [] # list of QTSchemaPreferences objects

    def __repr__(self):
        return '''QTPrefs: skin=%s, def.schema=%s, schemas=%s'''%(self.skin, self.defaultSchema, str(self.schemas))

    def getSchema(self, labelOrNS):
        '''Return a schema object identified by its namespace or unique label.
        Otherwise None.'''
        # try to find namespace or label in schema list
        for s in self.schemas:
            if s.label == labelOrNS or s.NS == labelOrNS: return s
        return None


# ------------------------------------------------------------------------------------
class QTSchemaPreferences:
    '''Querytool preferences for a single schema.'''
    def __init__(self):
        self.label = None # label of the schema
        self.NS    = None # the namespace
        self.concepts = [] # list of QTConceptPreferences object used
        self.grouping = None # the default conceptObj used for grouping
        self.reclistXSL = None # the stylesheet to be used for the record list page
        self.noRecordMessage = 'There is no valid record available for your query. &lt;br/&gt;All available records were invalid.'
        self.limit = 100 # the record limit used for the recordlist paging
        self.details  = {} # key=detail name, value=detail object
     
    def __repr__(self):
        grpPath = None
        if self.grouping is not None:
            grpPath = self.grouping.path
        return '''\n  QTSchema: label=%s, ns=%s, grouping=%s, reclist=%s, details=%s, concepts=%s\n'''%(self.label, self.NS, grpPath, self.reclistXSL, str(self.details.values()), str(self.concepts) )

    def knowsLabel(self, label):
        if self.getConcept(label) is not None:
            return True
        else:
            return False

    def hasConcepts(self):
        '''Return True if this schema has at least 1 concept'''
        if len(self.concepts) > 0: return True
        return False

    def hasConcept(self, labelOrPath):
        if self.getConcept(labelOrPath) is not None: return True
        return False
            
    def getConcept(self, labelOrPath):
        # try to find label in list of concepts first                       
        for c in self.concepts:
            if labelOrPath in (c.label,c.path): return c
        # try to find label in rec IDs of detail pages
        for detailObj in self.details.values():
            if detailObj.recID.has_key(labelOrPath):
                recID  = detailObj.recID[labelOrPath]
                return QTConceptPreferences(NS=self.NS, path=recID.path, label=labelOrPath, cops='=')
        return None
     
# ------------------------------------------------------------------------------------
class QTConceptPreferences(Concept):
    '''Querytool preferences for a single concept.'''
    def __init__(self, NS, path, label=None, cops='~'):
        Concept.__init__(self, NS=NS, path=path, label=label)
        self.cops  = cops  # the allowed comparison operators

    def __repr__(self):
        return '''\n    QTConcept: label=%s, NS=%s, path=%s, cops=%s'''%(self.label, self.NS, self.path, self.cops)

        
# ------------------------------------------------------------------------------------
class QTDetailPreferences:
    '''Querytool preferences for a single schema.'''
    def __init__(self):
        self.name = None # name of the detail
        self.stylesheet = None # the default stylesheet
        self.recID = {} # dict of QTRecID objects, key=parameter name, val=Obj
        self.notAvailableMessage = 'There was no result from the wrapper.&lt;br/&gt;The data provider seems to be out of service.'

    def __repr__(self):
        return '''QTDetail: name=%s, xsl=%s, recIDs=%s'''%(self.name, self.stylesheet, str(self.recID))


# ------------------------------------------------------------------------------------
class QTRecID:
    '''Record identifier object.'''
    def __init__(self, para=None, path=None):
        self.parameter = para # name of the GET parameter
        self.path = path # concept xpath

    def __repr__(self):
        return '''QTRecID: parameter=%s, path=%s'''%(self.parameter, self.path)


# ------------------------------------------------------------------------------------
class QTPreferencesHandler(xml.sax.handler.ContentHandler):
    '''parses the querytool preference file 
    and fills a QTPrefs object with its data. 
    You will need to call the skin handler later on, see getPreferences method.'''
    # _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
    def __init__(self, QTPrefObj):
        self.QTPrefObj = QTPrefObj
        # list of tablealias information used to init the graph
        self._isSchema = 0
        self._value = ''
        xml.sax.handler.ContentHandler.__init__(self)
    
    # _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
    def startElement(self, name, attrs):
        self._value = ''
        if name == 'Skin':            
            self.QTPrefObj.skin = attrs.get('name', 'default')
        elif name == 'Schema':
            self._isSchema   = 1
            self._schema = QTSchemaPreferences()
            self.QTPrefObj.schemas.append(self._schema)
            self._schema.NS    = attrs.get('ns', None)
            if self.QTPrefObj.defaultSchema is None:
                # the first schema is the default one:
                self.QTPrefObj.defaultSchema = self._schema
                log.debug("Found default schema %s" % self._schema.NS)
        if self._isSchema:
            if name == 'Concept':
                if attrs.get('path', None) is not None and len(attrs.get('path', '')) > 0:
                    conObj = QTConceptPreferences(NS=self._schema.NS, path=attrs.get('path', None))
                    self._schema.concepts.append(conObj)
                    
    # _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
    def endElement(self, name):
        self._value = self._value.strip()
        if name == 'Skin':
            self.QTPrefObj.skin = self._value            
        if name == 'Schema':
            self._isSchema   = 0
        self._value = ''

    # _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
    def characters(self, ch):
        self._value += ch


# ------------------------------------------------------------------------------------
class SkinPreferencesHandler(xml.sax.handler.ContentHandler):
    '''parses the skin configuration file 
    and fills a QTPrefs object with its data.'''
    # _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
    def __init__(self, QTPrefObj):
        self.QTPrefObj = QTPrefObj
        # list of tablealias information used to init the graph
        self._isSchema = 0
        self._value = ''
        xml.sax.handler.ContentHandler.__init__(self)
    
    # _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
    def startElement(self, name, attrs):
        self._value = ''
        if name == 'Schema':
            self._isSchema   = 1
            # does schema exist in pref object already? If not skip it
            ns = attrs.get('ns', None)
            self._schema = self.QTPrefObj.getSchema(ns)
            if self._schema is None:
                # schema didnt exist before (QTPrefs)
                self._schema = QTSchemaPreferences()
                self.QTPrefObj.schemas.append(self._schema)
                self._schema.NS = attrs.get('ns', None)
            else:
                self._schema.label = attrs.get('label', None)
        if self._isSchema:
            if name == 'Concept':
                cpath = attrs.get('path', None)
                label = attrs.get('label', None)
                # only use concepts that were created already (by the datasource specific querytool pref) 
                if label is not None and cpath is not None and self._schema.hasConcept(cpath):
                    conObj = self._schema.getConcept(cpath)
                    conObj.label = label
                    conObj.cops  = attrs.get('cops', '~')
                else:
                    log.debug("Found unused concept %s [%s] in skin schema %s" % (label,cpath,self._schema.NS))
                
            elif name == 'Grouping':
                grpPath = attrs.get('concept', None)
                if self._schema.hasConcept(grpPath):
                    self._schema.grouping = self._schema.getConcept(grpPath)
                else:
                    log.warn("The default grouping concept <%s> does not exist!" % grpPath) 
            elif name == 'Recordlist':
                self._schema.reclistXSL = attrs.get('stylesheet', None)
                try:
                    self._schema.limit = int( attrs.get('limit', 100) )
                except:
                    self._schema.limit = 1
            elif name == 'Detail':
                detObj = QTDetailPreferences()
                detObj.name = attrs.get('name', None)
                detObj.stylesheet = attrs.get('stylesheet', None)
                self._schema.details[detObj.name] = detObj
                # save current detail object for other use (recID)
                self._details = detObj
            elif name == 'RecordID':
                recIDObj = QTRecID(attrs.get('parametername', None), attrs.get('path', None))
                self._details.recID[recIDObj.parameter] = recIDObj
                    
                
    # _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
    def endElement(self, name):
        self._value = self._value.strip()
        if name == 'Schema':
            self._isSchema   = 0
        if self._isSchema:
            if name == 'NotAvailableMessage':
                self._details.notAvailableMessage = self._value
            if name == 'NoRecordMessage':
                self._schema.noRecordMessage = self._value
        self._value = ''

    # _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
    def characters(self, ch):
        self._value += ch

        
def getPreferences(dsa, cfg):
    prefs = QTPreferences()
    # read querytool prefs
    prefHandler = QTPreferencesHandler(prefs)
    absFilename = os.path.join(cfg.datasourcesLocator, dsa, 'querytool_prefs.xml')
    xml.sax.parse(absFilename, prefHandler)
    # read skin prefs
    skinHandler = SkinPreferencesHandler(prefs)          
    absFilename = os.path.join(cfg.skinLocator, prefs.skin, 'skin.xml')
    xml.sax.parse(absFilename, skinHandler)
    return prefs
    
