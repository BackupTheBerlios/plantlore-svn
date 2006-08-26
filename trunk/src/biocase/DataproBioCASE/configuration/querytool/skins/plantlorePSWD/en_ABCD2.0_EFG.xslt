<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:fn="http://www.w3.org/2004/07/xpath-functions" 
    xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" 
    xmlns:n1="http://www.tdwg.org/schemas/abcd/2.06" 
    xmlns:efg="http://www.synthesys.info/ABCDEFG/1.0"
    exclude-result-prefixes="xsl xs fn xdt n1 efg">
    <!--
    EGF Extension for ABCD 2.0
    
    Author: Cristian Oancea
    Created: 22 Feb 2006
    Updated:
    -->
    <xsl:include href="en_ABCD2.0_EFG_CommonTemplates.xslt" />
    
    <xsl:template name="efgExtension">
        <div>
            <!--<h3 class="background">Extension </h3>-->
            <table width="96%">
                <tr>
                    <xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:MineralRockGroup">
                        <th>Mineral Rock Group</th> 
                    </xsl:if>
                    <!--
                    <xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName">
                        <th>Classified Name</th> 
                    </xsl:if>
                    --> 
                </tr>        
                <tr valign="top" bgcolor="#f4f4f4">  
                    <td>
                        <xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified">
                        <xsl:for-each select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified">
                            <xsl:call-template name="MineralRockIdentifiedType">
                            </xsl:call-template>
                        </xsl:for-each>
                        </xsl:if>
                        
                        <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:MineralRockGroup/efg:MineralRockGroupName">
                            <span class="label">Name: </span>
                            <xsl:value-of select="string(n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:MineralRockGroup/efg:MineralRockGroupName)"/>
                            <br/> 
                        </xsl:if>-->       
                        <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:MineralRockGroup/efg:MineralRockclassification">
                            <span class="label">Classification: </span>
                             <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:MineralRockGroup/efg:MineralRockclassification"/>
                            <br/> 
                        </xsl:if>-->     
                     <xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName">
                        <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName/efg:FullScientificNameString">
                            <span class="label">Full Scientific Name: </span>
                            <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName/efg:FullScientificNameString"/>
                            <br/>
                        </xsl:if>-->
                         <xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName/efg:NameAtomised">
                         
                         <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName/efg:NameAtomised/efg:ScientificNameString">
                             <span class="label">Scientific Name: </span>
                             <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName/efg:NameAtomised/efg:ScientificNameString"/>
                             <br/>
                         </xsl:if>-->
                         <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName/efg:NameAtomised/efg:AuthorTeamAndYear">
                             <span class="label">Author (Team): </span>
                                 <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName/efg:NameAtomised/efg:AuthorTeamAndYear"/>
                              <br/>
                         </xsl:if>-->
                             <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName/efg:NameAtomised/efg:MineralRockClassification">
                                 <span class="label">Mineral Rock Classification: </span>
                                 <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:ClassifiedName/efg:NameAtomised/efg:MineralRockClassification"/>
                                 <br/>
                             </xsl:if>-->
                         </xsl:if>
                         <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:InformalNameString">
                             <span class="label">Informal Name: </span>
                             <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:InformalNameString"/>
                             <br/>
                         </xsl:if>-->
                         <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:VarietalNameString">
                             <span class="label">Varietal Name: </span>
                             <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:VarietalNameString"/>
                             <br/>
                         </xsl:if>-->
                         <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:NameAddendum">
                             <span class="label">Name Addendum: </span>
                             <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:NameAddendum"/>
                             <br/>
                         </xsl:if>-->
                         <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:IdentificationQualifier">
                             <span class="label">Identification Qualifier: </span>
                             <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:IdentificationQualifier"/>
                             <br/>
                         </xsl:if>-->
                         <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:Certainty">
                             <span class="label">Certainty: </span>
                             <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:Certainty"/>
                             <br/>
                         </xsl:if>-->
                         <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:Identifiers">
                             <span class="label">Identifiers: </span>
                             <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:Identifiers"/>
                             <br/>
                         </xsl:if>-->
                         <!--<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:NameComments">
                             <span class="label">Name Comments: </span>
                             <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:Extension/efg:MineralRockIdentified/efg:NameComments"/>
                             <br/>
                         </xsl:if>-->
                    </xsl:if>
                   </td>
                </tr>
            </table>
        </div>
    </xsl:template>
    
    <xsl:template name="efgExtension_Unit">
        <div>
            <xsl:if test="efg:EFGUnit">
            <xsl:choose>
                <xsl:when test="efg:EFGUnit/efg:RockUnit">
                    <h3 class="background"> Rock Unit Details: </h3>
                </xsl:when>
                <xsl:when test="efg:EFGUnit/efg:MineralogicalUnit">
                    <h3 class="background"> Mineral Unit Details: </h3>
                </xsl:when>
                <xsl:when test="efg:EFGUnit/efg:PalaeontologicalUnit">
                    <h3 class="background"> Palaeontological Unit Details: </h3>
                </xsl:when>
                <xsl:otherwise>
                    <h3 class="background"> Earth Science Unit Details: </h3>
                </xsl:otherwise>
            </xsl:choose>
            </xsl:if>
            <xsl:for-each select="efg:EFGUnit">
            
                <table width="96%">
                    <xsl:if test="efg:UnitType">
                        <tr>
                            <td>
                                <span class="label">Unit Type:</span>
                                <xsl:value-of select="efg:UnitType"/>
                            </td>
                        </tr>
                    </xsl:if>
                    <xsl:if test="efg:UnitClassOfMaterial">
                        <tr>
                            <td>
                                <span class="label">Unit Class Of Material:</span>
                                <xsl:value-of select="efg:UnitClassOfMaterial"/>
                            </td>
                        </tr>
                    </xsl:if>
                    <xsl:if test="efg:UnitSize">
                        <tr>
                            <td>
                                <span class="label">Unit Size:</span>
                                <xsl:value-of select="efg:UnitSize"/>
                            </td>
                        </tr>
                    </xsl:if>
                    <xsl:if test="efg:UnitWeight">
                        <tr>
                            <td>
                                <span class="label">Unit Weight:</span>
                                <xsl:value-of select="efg:UnitWeight"/>
                            </td>
                        </tr>
                    </xsl:if>
                    <xsl:if test="efg:Hazard">
                        <tr>
                            <td>
                                <span class="label">Hazard :</span>
                                <xsl:value-of select="efg:Hazard"/>
                            </td>
                        </tr>
                    </xsl:if>
                    <xsl:if test="efg:RockMineralUsage">
                        <tr>
                            <td>
                                <span class="label">Rock Mineral Usage:</span>
                                <xsl:value-of select="efg:RockMineralUsage"/>
                            </td>
                        </tr>
                    </xsl:if>
                    <xsl:if test="efg:UnitHostRock">
                        <xsl:if test="efg:UnitHostRock/efg:LithificationHostRock">
                            <!-- xs:string (value comes from list: {'unlithified'|'poorly lithified'|'lithified'|'metamorphosed'})-->
                            <tr>
                                <td>
                                    <span class="label">Unit Lithification Host Rock:</span>
                                    <xsl:value-of
                                        select="efg:UnitHostRock/efg:LithificationHostRock"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <xsl:if test="efg:UnitHostRock/efg:LithologyHostRock">
                            <tr>
                                <td>
                                    <span class="label">Unit Lithology Host Rock:</span>
                                    <!--<xsl:value-of select="efg:UnitHostRock/efg:LithologyHostRock"/>-->
                                    <xsl:for-each select="efg:UnitHostRock/efg:LithologyHostRock">
                                        <xsl:call-template name="MineralRockIdentifiedType"
                                        > </xsl:call-template>
                                    </xsl:for-each>
                                </td>
                            </tr>
                        </xsl:if>
                        <xsl:if test="efg:UnitHostRock/efg:LithologyAttributesHostRock">
                            <tr>
                                <td>
                                    <span class="label">Unit Lithology Attributes Host Rock:</span>
                                    <xsl:value-of
                                        select="efg:UnitHostRock/efg:LithologyAttributesHostRock"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <xsl:if test="efg:UnitHostRock/efg:HostRockComment">
                            <tr>
                                <td>
                                    <span class="label">Unit Host Rock Comment:</span>
                                    <xsl:value-of select="efg:UnitHostRock/efg:HostRockComment"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <xsl:if test="efg:UnitHostRock/efg:HostRockStratigraphy">
                            <tr>
                                <td>
                                    <span class="label">Unit Host Rock Stratigraphy:</span>
                                    <xsl:for-each select="efg:UnitHostRock/efg:HostRockStratigraphy">
                                        <xsl:call-template name="StratigraphicAttributionsType"
                                        > </xsl:call-template>
                                    </xsl:for-each>
                                </td>
                            </tr>
                        </xsl:if>
                        <xsl:if test="efg:AllocthonousMaterial">
                            <xsl:if test="efg:AllocthonousMaterial/efg:PlaceOfOrigin">
                                <tr>
                                    <td>
                                        <span class="label">Unit Place Of Origin:</span>
                                        <xsl:value-of
                                            select="efg:AllocthonousMaterial/efg:PlaceOfOrigin"/>
                                    </td>
                                </tr>
                            </xsl:if>
                            <xsl:if test="efg:AllocthonousMaterial/efg:ModeOfTransport">
                                <tr>
                                    <td>
                                        <span class="label">Unit Mode Of Transport:</span>
                                        <xsl:value-of
                                            select="efg:AllocthonousMaterial/efg:ModeOfTransport"/>
                                    </td>
                                </tr>
                            </xsl:if>
                            <xsl:if test="efg:AllocthonousMaterial/efg:OriginalStratigraphy">
                                <xsl:if
                                    test="efg:AllocthonousMaterial/efg:OriginalStratigraphy/efg:StratigraphicComment">
                                    <tr>
                                        <td>
                                            <span class="label">Unit Stratigraphic Comment: </span>
                                            <xsl:value-of
                                                select="efg:AllocthonousMaterial/efg:OriginalStratigraphy/efg:StratigraphicComment"
                                            />
                                        </td>
                                    </tr>
                                </xsl:if>
                                <!-- 
                                    <xsl:if test="efg:AllocthonousMaterial/efg:OriginalStratigraphy/efg:StratigraphicAttributions">
                                        <tr><td>
                                            <span class="label">Unit Stratigraphic Comment:</span>
                                        <xsl:value-of select="efg:AllocthonousMaterial/efg:OriginalStratigraphy/efg:StratigraphicComment"/>
                                        </td></tr>
                                    </xsl:if>
                                    -->
                                <xsl:if
                                    test="efg:AllocthonousMaterial/efg:OriginalStratigraphy/efg:StratigraphicReferences">
                                    <tr>
                                        <td>
                                            <span class="label">Unit Stratigraphic Refrence(s): </span>
                                            <xsl:for-each
                                                select="efg:AllocthonousMaterial/efg:OriginalStratigraphy/efg:StratigraphicReferences/efg:StratigraphicReference">
                                                <!--<xsl:value-of select="efg:AllocthonousMaterial/efg:OriginalStratigraphy/efg:StratigraphicComment"/>-->
                                                <xsl:call-template name="reference"
                                                > </xsl:call-template>
                                            </xsl:for-each>
                                        </td>
                                    </tr>
                                </xsl:if>
                            </xsl:if>
                            <!---   EFG Gathering -->
                            <xsl:if test="n1:UnitExtension/efg:Gathering">
                                <xsl:if
                                    test="n1:UnitExtension/efg:Gathering/efg:NamedGeologicalFeature">
                                    <xsl:if
                                        test="n1:UnitExtension/efg:Gathering/efg:NamedGeologicalFeature">
                                        <tr>
                                            <td>
                                                <span class="label">Named Geological Feature: </span>
                                                <xsl:value-of
                                                  select="n1:UnitExtension/efg:Gathering/efg:NamedGeologicalFeature"
                                                />
                                            </td>
                                        </tr>
                                    </xsl:if>
                                    <xsl:if
                                        test="n1:UnitExtension/efg:Gathering/efg:SiteStratigraphy">
                                        <xsl:if
                                            test="n1:UnitExtension/efg:Gathering/efg:NamedGeologicalFeature">
                                            <tr>
                                                <td>
                                                  <span class="label">Named Geological Feature: </span>
                                                  <xsl:value-of
                                                  select="n1:UnitExtension/efg:Gathering/efg:NamedGeologicalFeature"
                                                  />
                                                </td>
                                            </tr>
                                        </xsl:if>
                                        <xsl:if
                                            test="n1:UnitExtension/efg:Gathering/efg:SiteStratigraphy/efg:SiteStratigraphyReferences">
                                            <tr>
                                                <td>
                                                  <span class="label">Unit Stratigraphic
                                                  Refrence(s): </span>
                                                  <xsl:for-each
                                                  select="n1:UnitExtension/efg:Gathering/efg:SiteStratigraphy/efg:SiteStratigraphyReferences">
                                                  <xsl:call-template name="reference"
                                                  > </xsl:call-template>
                                                  </xsl:for-each>
                                                </td>
                                            </tr>
                                        </xsl:if>
                                    </xsl:if>
                                </xsl:if>
                                <!---   EFG Gathering -->
                            </xsl:if>
                        </xsl:if>
                    </xsl:if>
                    <!--<xsl:if test="efg:AllocthonousMaterial">
                            
                        </xsl:if>
                        <xsl:if test="efg:UnitStratigraphy">
                            
                        </xsl:if>-->
                    <xsl:if test="efg:RockUnit">
                        <xsl:if test="efg:RockUnit/efg:RockType">
                            <tr><td><span class="label"> Rock Type: </span>
                            <xsl:value-of select="efg:RockUnit/efg:RockType"/>
                            <br/></td></tr>
                        </xsl:if>
                        <!--<efg:RockType> xs:string (value comes from list: {'carbonaceous'|'carbonate'|'clastic'|'evaporite'|'fault rock'|'gneiss'|'high-grade metamorphic'|'hornfels'|'igneous'|'igneous extrusive'|'igneous intrusive'|'low-grade metamorphic'|'metamorphic'|'metasomatic'|'meteorite'|'migmatite'|'mylomite'|'plutonic intrusive'|'sedimentary'|'schist'|'slate'|'vein / pegmatite'|'volcaniclastic'}) </efg:RockType> [1..*] ?-->
                        <!--<efg:RockPhysicalCharacteristics> abcd:MeasurementOrFact </efg:RockPhysicalCharacteristics> [0..*] ?-->
                        <xsl:if test="efg:RockUnit/efg:RockPhysicalCharacteristics">
                            <xsl:for-each
                                select="efg:RockUnit/efg:RockPhysicalCharacteristics/efg:RockPhysicalCharacteristic">
                                <xsl:if test="efg:MeasurementOrFactText">
                                    <tr><td><span class="label"> Fact Text: </span>
                                    <xsl:value-of select="efg:MeasurementOrFactText"/>
                                    <br/></td></tr>
                                </xsl:if>
                                <xsl:if test="efg:RockPhysicalCharacteristicAtomised/efg:MeasurementOrFactAtomised">
                                <xsl:for-each select="efg:RockPhysicalCharacteristicAtomised/efg:MeasurementOrFactAtomised">
                                    <tr><td><xsl:call-template name="measurementatomised">
                                        <xsl:with-param name="label"> Rock </xsl:with-param>
                                    </xsl:call-template></td></tr>
                                </xsl:for-each>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:if>
                        <!--<efg:Petrology> [0..1] ?-->
                        <xsl:if test="efg:RockUnit/efg:Petrology">
                            <!--<efg:PetrologyDescriptiveText> xs:string </efg:PetrologyDescriptiveText> [0..1] ?-->
                            <xsl:if test="efg:RockUnit/efg:Petrology/efg:PetrologyDescriptiveText">
                                <tr><td><span class="label"> Petrology Description : </span>
                                <xsl:value-of
                                    select="efg:RockUnit/efg:Petrology/efg:PetrologyDescriptiveText"/>
                                <br/></td></tr>
                            </xsl:if>
                            <!--<efg:Petrologicalanalyses> [0..1] ?
                                    <efg:PetrologicalAnalysis> [1..*] ?
                                           <efg:PerologicalAnalysisComment> xs:string </efg:PerologicalAnalysisComment> [0..1] ?
                                            <efg:PetrologicalAnalysisAtomised> efg:AnalysisAtomisedType </efg:PetrologicalAnalysisAtomised> [0..1]
                                       </efg:PetrologicalAnalysis>
                                </efg:Petrologicalanalyses>-->
                            <xsl:if test="efg:RockUnit/efg:Petrology/efg:Petrologicalanalyses">
                                <xsl:for-each
                                    select="efg:RockUnit/efg:Petrology/efg:Petrologicalanalyses/efg:PetrologicalAnalysis">
                                   <tr><td> <span class="label">Analysis Comment: </span>
                                    <xsl:value-of select="efg:PerologicalAnalysisComment"/>
                                    <br/></td></tr>
                                </xsl:for-each>
                            </xsl:if>
                        </xsl:if>
                        <!--<efg:AssociatedMineralAssemblage> efg:AssociatedMineralAssemblageType </efg:AssociatedMineralAssemblage> [0..1]-->
                        <xsl:if test="efg:RockUnit/efg:AssociatedMineralAssemblage"> </xsl:if>
                        <!--<efg:AssociatedFossilAssemblage> efg:AssociatedFossilAssemblageType </efg:AssociatedFossilAssemblage> [0..1] ?-->
                        <xsl:if test="efg:RockUnit/efg:AssociatedFossilAssemblage"> </xsl:if>
                        <!--<efg:DepositionalEnvironment> [0..1] ?-->
                        <xsl:if test="efg:RockUnit/efg:DepositionalEnvironment">
                            <!--<efg:DepositionalEnvironmentText> xs:string </efg:DepositionalEnvironmentText> [0..1] ?-->
                            <xsl:if
                                test="efg:RockUnit/efg:DepositionalEnvironment/efg:DepositionalEnvironmentText">
                                <tr><td><span class="label">Depositional Environment : </span>
                                <xsl:value-of
                                    select="efg:RockUnit/efg:DepositionalEnvironment/efg:DepositionalEnvironmentText"/>
                                <br/></td></tr>
                            </xsl:if>
                            <!--<efg:DepositionalEnvironmentType> xs:string (value comes from list: {'alluvial fan'|'basinal carbonate'|'basinal siliceous'|'carbonate'|'cave'|'channel'|'channel lag'|'coarse channel fill'|'coastal'|'crater lake'|'crevasse splay'|'deep subtidal'|'deep subtidal ramp'|'deep subtidal shelf'|'deltaic'|'delta front'|'delta plain'|'dry floodplain'|'dune'|'estuarine'|'eolian'|'fissure fill'|'floodplain'|'fluvial'|'fluvial-deltaic'|'fluvial-lacustrine'|'foreshore'|'glacial'|'interdistributary bay'|'interdune'|'karst'|'lacustrine'|'lagoonal'|'levee'|'loess'|'marginal marine'|'marine'|'offshore'|'offshore ramp'|'offshore shelf'|'open shallow subtidal'|'paralic'|'peritidal'|'pond'|'prodelta'|'reef or bioherm'|'shallow subtidal'|'shoreface'|'shore transition zone'|'sinkhole'|'slope'|'swamp'|'restricted shallow subtidal'|'sand shoal'|'tar'|'terrestrial'|'wet floodplain'}) </efg:DepositionalEnvironmentType> [1..*] ?-->
                            <xsl:if
                                test="efg:RockUnit/efg:DepositionalEnvironment/efg:DepositionalEnvironmentType">
                                <tr><td><span class="label">Depositional Environment Type: </span>
                                <xsl:value-of
                                    select="efg:RockUnit/efg:DepositionalEnvironment/efg:DepositionalEnvironmentType"/>
                                <br/></td></tr>
                            </xsl:if>
                        </xsl:if>
                    </xsl:if>
                    <xsl:if test="efg:MineralogicalUnit">
                        <!--<efg:MineralHabit> xs:string </efg:MineralHabit> [0..1] ?-->
                        <xsl:if test="efg:MineralogicalUnit/efg:MineralHabit">
                            <tr><td><span class="label"> Mineral Habit: </span>
                            <xsl:value-of select="efg:MineralogicalUnit/efg:MineralHabit"/>
                            <br/></td></tr>
                        </xsl:if>
                        <!--<efg:MineralColour> xs:string </efg:MineralColour> [0..1] ?-->
                        <xsl:if test="efg:MineralogicalUnit/efg:MineralColour">
                            <tr><td><span class="label"> Mineral Colour: </span>
                            <xsl:value-of select="efg:MineralogicalUnit/efg:MineralColour"/>
                            <br/></td></tr>
                        </xsl:if>
                        <!--<efg:CrystalForm> [0..1] ?-->
                        <xsl:if test="efg:MineralogicalUnit/efg:CrystalForm">
                            <!--<efg:CrystalFormText> xs:string </efg:CrystalFormText> [0..1] ?-->
                            <xsl:if test="efg:MineralogicalUnit/efg:CrystalForm/efg:CrystalFormText">
                                <tr><td><span class="label"> Cristal Details: </span>
                                <xsl:value-of
                                    select="efg:MineralogicalUnit/efg:CrystalForm/efg:Twinning"/>
                                <br/></td></tr>
                            </xsl:if>
                            <!--<efg:Twinning> xs:string </efg:Twinning> [0..1] ?-->
                            <xsl:if test="efg:MineralogicalUnit/efg:CrystalForm/efg:Twinning">
                                <tr><td><span class="label"> Cristal Twinning: </span>
                                <xsl:value-of
                                    select="efg:MineralogicalUnit/efg:CrystalForm/efg:Twinning"/>
                                <br/></td></tr>
                            </xsl:if>
                            <!--<efg:Pseudomorph> xs:string </efg:Pseudomorph> [0..1] ?-->
                            <xsl:if test="efg:MineralogicalUnit/efg:CrystalForm/efg:Pseudomorph">
                                <tr><td><span class="label"> Cristal Pseudomorph: </span>
                                <xsl:value-of
                                    select="efg:MineralogicalUnit/efg:CrystalForm/efg:Pseudomorph"/>
                                <br/></td></tr>
                            </xsl:if>
                            <!--<efg:CrystalMeasurements> [0..1] ?-->
                            <xsl:if
                                test="efg:MineralogicalUnit/efg:CrystalForm/efg:CrystalMeasurements">
                                <!--<efg:CrystalMeasurement> abcd:MeasurementOrFact </efg:CrystalMeasurement> [1..*] ?-->
                                <xsl:for-each
                                    select="efg:MineralogicalUnit/efg:CrystalForm/efg:CrystalMeasurements/efg:CrystalMeasurement">
                                    
                                    <tr><td><span class="label"> Fact Details: </span>
                                    <xsl:value-of select="efg:MeasurementOrFactText"/>
                                    <br/>
                                    <xsl:for-each select="efg:MeasurementOrFactAtomised">
                                    <xsl:call-template name="measurementatomised">
                                        <xsl:with-param name="label"> Cristal </xsl:with-param>
                                    </xsl:call-template>
                                    </xsl:for-each></td></tr>
                                </xsl:for-each>
                            </xsl:if>
                            <!--</efg:CrystalForm>-->
                        </xsl:if>
                        <!--<efg:MineralDescriptionText> xs:string </efg:MineralDescriptionText> [0..1] ?-->
                        <xsl:if test="efg:MineralogicalUnit/efg:MineralDescriptionText">
                            <tr><td><span class="label"> Mineral Description: </span>
                            <xsl:value-of select="efg:MineralogicalUnit/efg:MineralDescriptionText"/>
                            <br/></td></tr>
                        </xsl:if>
                    </xsl:if>
                    <xsl:if test="efg:PalaeontologicalUnit">
                        <!--<efg:PartOfOrganism> xs:string </efg:PartOfOrganism> [0..1]-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:PartOfOrganism">
                            <tr>
                                <td>
                                    <span class="label"> Part Of Organism: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:PartOfOrganism"/>
                                </td>
                            </tr>
                        </xsl:if>
                        
                        <!--<efg:Completeness> xs:string </efg:Completeness> [0..1]-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:Completeness">
                            <tr>
                                <td>
                                    <span class="label"> Completeness: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:Completeness"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <!--<efg:Articulation> xs:string </efg:Articulation> [0..1]-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:Articulation">
                            <tr>
                                <td>
                                    <span class="label"> Articulation: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:Articulation"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <!--<efg:FeedingPredationTraces> xs:string </efg:FeedingPredationTraces>-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:FeedingPredationTraces">
                            <tr>
                                <td>
                                    <span class="label"> Feeding Predation Traces: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:FeedingPredationTraces"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <!--<efg:Bioerosion> xs:string </efg:Bioerosion> [0..1]-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:Bioerosion">
                            <tr>
                                <td>
                                    <span class="label"> Bioerosion: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:Bioerosion"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <!--<efg:Encrustation> xs:string </efg:Encrustation> [0..1]-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:Encrustation">
                            <tr>
                                <td>
                                    <span class="label"> Encrustation: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:Encrustation"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <!--<efg:Orientation> xs:string </efg:Orientation> [0..1]-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:Orientation">
                            <tr>
                                <td>
                                    <span class="label"> Orientation: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:Orientation"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <!--<efg:AssemblageOrigin> xs:string </efg:AssemblageOrigin> [0..1]-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:AssemblageOrigin">
                            <tr>
                                <td>
                                    <span class="label"> Assemblage Origin: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:AssemblageOrigin"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <!--<efg:PostBurialTransportation> xs:string-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:PostBurialTransportation">
                            <tr>
                                <td>
                                    <span class="label"> Post Burial Transportation: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:PostBurialTransportation"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <!--<efg:PreservationQuality> xs:string </efg:PreservationQuality> [0..1] ?-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:PreservationQuality">
                            <tr>
                                <td>
                                    <span class="label"> Preservation Quality: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:PreservationQuality"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <!--<efg:PreservationMode> [0..1]-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:PreservationMode">
                            <!--<efg:PreservationModeText> xs:string </efg:PreservationModeText>-->
                            <xsl:if test="efg:PalaeontologicalUnit/efg:PreservationMode/efg:PreservationModeText">
                                <tr>
                                <td>
                                    <span class="label"> Preservation Mode: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:PreservationMode/efg:PreservationModeText"/>
                                </td>
                            </tr>
                            </xsl:if>
                            <!--<efg:PreservationModeKeywords> xs:string (value comes from list:-->
                            <xsl:if test="efg:PalaeontologicalUnit/efg:PreservationMode/efg:PreservationModeKeywords">
                                <tr>
                                <td>
                                    <span class="label"> Preservation Mode Keywords: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:PreservationMode/efg:PreservationModeKeywords"/>
                                </td>
                            </tr>
                            </xsl:if>
                            <!--<efg:PreservationSpecialMode> xs:string (value comes from list:-->
                            <xsl:if test="efg:PalaeontologicalUnit/efg:PreservationMode/efg:PreservationSpecialMode">
                                <tr>
                                <td>
                                    <span class="label"> Preservation Special Mode: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:PreservationMode/efg:PreservationSpecialMode"/>
                                </td>
                            </tr>
                            </xsl:if>
                        </xsl:if>
                        <!--<efg:PreservationAlteration> [0..1] ?
                        <efg:PreservationAlterationText>   xs:string </efg:PreservationAlterationText> [0..1]-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:PreservationAlteration">
                            <xsl:if test="efg:PalaeontologicalUnit/efg:PreservationAlteration/efg:PreservationAlterationText">
                                <tr>
                                <td>
                                    <span class="label"> Preservation Alteration Details: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:PreservationAlteration/efg:PreservationAlterationText"/>
                                </td>
                            </tr>
                            </xsl:if>
                            <!--<efg:OriginalBiominerals> xs:string (value comes from list:-->
                        <xsl:if test="efg:PalaeontologicalUnit/efg:PreservationAlteration/efg:OriginalBiominerals">
                            <tr>
                                <td>
                                    <span class="label"> Original Biominerals: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:PreservationAlteration/efg:OriginalBiominerals"/>
                                </td>
                            </tr>
                        </xsl:if>        
                        <!--<efg:ReplacementMinerals> xs:string-->
                         <xsl:if test="efg:PalaeontologicalUnit/efg:PreservationAlteration/efg:ReplacementMinerals">
                            <tr>
                                <td>
                                    <span class="label"> Replacement Minerals: </span>
                                    <xsl:value-of select="efg:PalaeontologicalUnit/efg:PreservationAlteration/efg:ReplacementMinerals"/>
                                </td>
                            </tr>
                        </xsl:if>
                        </xsl:if>

                    </xsl:if>
                    <xsl:if test="efg:Alteration"> </xsl:if>
                </table>
             </xsl:for-each>
             
            <xsl:for-each select="efg:Gathering">
                <table width="96%">
                    <tr>
                        <!--<xsl:if test="n1:UnitExtension/efg:Gathering">-->
                        <xsl:if test="efg:NamedGeologicalFeature">
                            <xsl:if test="efg:NamedGeologicalFeature">
                                <tr>
                                    <td>
                                        <span class="label">Named Geological Feature: </span>
                                        <xsl:value-of select="efg:NamedGeologicalFeature"/>
                                    </td>
                                </tr>
                            </xsl:if>
                            <xsl:if test="efg:SiteStratigraphy">
                                <xsl:if test="efg:NamedGeologicalFeature">
                                    <tr>
                                        <td>
                                            <span class="label">Named Geological Feature: </span>
                                            <xsl:value-of select="efg:NamedGeologicalFeature"/>
                                        </td>
                                    </tr>
                                </xsl:if>
                                <xsl:if test="efg:SiteStratigraphy/efg:SiteStratigraphyReferences">
                                    <tr>
                                        <td>
                                            <span class="label">Unit Stratigraphic Refrence(s): </span>
                                            <xsl:for-each
                                                select="efg:SiteStratigraphy/efg:SiteStratigraphyReferences">
                                                <xsl:call-template name="reference"
                                                > </xsl:call-template>
                                            </xsl:for-each>
                                        </td>
                                    </tr>
                                </xsl:if>
                            </xsl:if>
                        </xsl:if>
                        <!---   EFG Gathering -->
                        <!--</xsl:if>-->
                    </tr>
                </table>
            </xsl:for-each>
        </div>
    </xsl:template>
    
    
</xsl:stylesheet>