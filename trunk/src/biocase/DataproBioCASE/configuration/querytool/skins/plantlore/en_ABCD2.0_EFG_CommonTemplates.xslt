<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" xmlns:n1="http://www.tdwg.org/schemas/abcd/2.06" 
    xmlns:efg="http://www.synthesys.info/ABCDEFG/1.0"
    exclude-result-prefixes="xsl xs fn xdt n1 efg">
    <xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html"/>

    
    <!--
        StratigraphicAttributions
    -->
    
    <!--MineralRockIdentifiedType-->
    <xsl:template name="MineralRockIdentifiedType">
        <xsl:if test="efg:MineralRockGroup/efg:MineralRockGroupName">
            <span class="label">Name: </span>
            <xsl:value-of select="string(efg:MineralRockGroup/efg:MineralRockGroupName)"/>
            <br/> 
        </xsl:if>       
        <xsl:if test="efg:MineralRockGroup/efg:MineralRockclassification">
            <span class="label">Classification: </span>
            <xsl:value-of select="efg:MineralRockGroup/efg:MineralRockclassification"/>
            <br/> 
        </xsl:if>     
        <xsl:if test="efg:ClassifiedName">
            <xsl:if test="efg:ClassifiedName/efg:FullScientificNameString">
                <span class="label">Full Scientific Name: </span>
                <xsl:value-of select="efg:ClassifiedName/efg:FullScientificNameString"/>
                <br/>
            </xsl:if>
            <xsl:if test="efg:ClassifiedName/efg:NameAtomised">
                
                <xsl:if test="efg:ClassifiedName/efg:NameAtomised/efg:ScientificNameString">
                    <span class="label">Scientific Name: </span>
                    <xsl:value-of select="efg:ClassifiedName/efg:NameAtomised/efg:ScientificNameString"/>
                    <br/>
                </xsl:if>
                <xsl:if test="efg:ClassifiedName/efg:NameAtomised/efg:AuthorTeamAndYear">
                    <span class="label">Author (Team): </span>
                    <xsl:value-of select="efg:ClassifiedName/efg:NameAtomised/efg:AuthorTeamAndYear"/>
                    <br/>
                </xsl:if>
                <xsl:if test="efg:ClassifiedName/efg:NameAtomised/efg:MineralRockClassification">
                    <span class="label">Mineral Rock Classification: </span>
                    <xsl:value-of select="efg:ClassifiedName/efg:NameAtomised/efg:MineralRockClassification"/>
                    <br/>
                </xsl:if>
            </xsl:if>
            <xsl:if test="efg:InformalNameString">
                <span class="label">Informal Name: </span>
                <xsl:value-of select="efg:InformalNameString"/>
                <br/>
            </xsl:if>
            <xsl:if test="efg:VarietalNameString">
                <span class="label">Varietal Name: </span>
                <xsl:value-of select="efg:VarietalNameString"/>
                <br/>
            </xsl:if>
            <xsl:if test="efg:NameAddendum">
                <span class="label">Name Addendum: </span>
                <xsl:value-of select="efg:NameAddendum"/>
                <br/>
            </xsl:if>
            <xsl:if test="efg:IdentificationQualifier">
                <span class="label">Identification Qualifier: </span>
                <xsl:value-of select="efg:IdentificationQualifier"/>
                <br/>
            </xsl:if>
            <xsl:if test="efg:Certainty">
                <span class="label">Certainty: </span>
                <xsl:value-of select="efg:Certainty"/>
                <br/>
            </xsl:if>
            <xsl:if test="efg:Identifiers">
                <span class="label">Identifiers: </span>
                <xsl:value-of select="efg:Identifiers"/>
                <br/>
            </xsl:if>
            <xsl:if test="efg:NameComments">
                <span class="label">Name Comments: </span>
                <xsl:value-of select="efg:NameComments"/>
                <br/>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    
    <!--StratigraphicAttributionsType -->
    <xsl:template name="StratigraphicAttributionsType">
        
        <efg:ChronostratigraphicAttributions> efg:ChronostratigraphicAttributionsType </efg:ChronostratigraphicAttributions> [0..1] ?
        
                <efg:LithostratigraphicAttributions> efg:LithostratigraphicAttributionsType </efg:LithostratigraphicAttributions> [0..1] ?
                <efg:BiostratigraphicAttributions> efg:BiostratigraphicAttributionsType </efg:BiostratigraphicAttributions> [0..1] ?
                <efg:MagnetostratigraphicDeterminations> efg:MagnetostratigraphicDeterminationsType </efg:MagnetostratigraphicDeterminations> [0..1] ?
                <efg:IsotopeStratigraphicDeterminations> efg:IsotopeStratigraphicDeterminationsType </efg:IsotopeStratigraphicDeterminations> [0..1] ?
                 <efg:RadiometricDates> efg:RadiometricDatesType </efg:RadiometricDates> [0..1] ?
                <efg:PositionInSequence> xs:string </efg:PositionInSequence> 
    </xsl:template>
    
    <xsl:template name="AnalysisAtomised">
        
    </xsl:template>
</xsl:stylesheet>