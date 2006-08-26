<?xml version="1.0" encoding="UTF-8"?>
<!-- 	**************************************************************************
		**	Unit details library for ABCD 1.2									**
		**																		**
		**	This template process the information of ABCD units. It needs to	**
		**	include the en_ABCD1.2_CommonTemplates.xslt file, but if it is being 	**
		**	called by the All_parts XSLT then it is already included before		**
		**	and should be mantained commented									**
		**																		**
		**	Author: Javier de la Torre 											**
		**************************************************************************
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" xmlns:n1="http://www.tdwg.org/schemas/abcd/1.2" exclude-result-prefixes="xsl xs fn xdt n1">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html"/> 
	<xsl:include href="en_ABCD1.2_CommonTemplates.xslt" />
	
	<xsl:template name="UnitDetails" match="/">				
		<!-- We first create our own UnitD to refeer to it inside the document -->
		<xsl:for-each select="n1:DataSets/n1:DataSet">
		<xsl:variable name="DataSetId" select="position()"/>
		<xsl:for-each select="n1:Units/n1:Unit">
		
			<!-- We first calculate the ID for this Unit to use it after -->			
			<xsl:variable name="UnitID" select="concat($DataSetId,'_',position())"/>
			<!-- Now the varibale ID contains the Unique identifier -->
			<div id="Unit">

		
				<!-- *********************************/HEADER ************************************** 	-->
							<table width="96%" border="0">
					<tr>
						<td align="center">             
							<h1><xsl:value-of select="n1:Identifications/n1:Identification/n1:TaxonIdentified/n1:NameAuthorYearString"/>
               <span style="font-size:11px;">(<xsl:value-of select="n1:UnitID"/>)</span></h1>					
						</td>
				  </tr>
          <tr>                      		
						<td align="right">
							<span class="label">Společnost: </span>
										<xsl:value-of select="../../n1:OriginalSource/n1:SourceName"/> (<xsl:value-of select="../../n1:OriginalSource/n1:SourceInstitutionCode"/>)              
							<br/>							
							<span class="label">Datum poslední aktualizace: </span>
							<xsl:value-of select="../../n1:OriginalSource/n1:SourceLastUpdatedDate"/>
							<br/>
							<span class="label">Projekt: </span>
							<xsl:value-of select="n1:Gathering/n1:Project/n1:ProjectTitle"/>
							<br/>
							<xsl:if test="../../n1:OriginalSource/n1:SourceWebAddress">
								<a>
									<xsl:attribute name="href"><xsl:value-of select="../../n1:OriginalSource/n1:SourceWebAddress"/></xsl:attribute>URL</a> |
							</xsl:if>
							<xsl:if test="../../n1:DatasetDerivations/n1:DatasetDerivation/n1:Supplier/n1:EmailAddresses/n1:EmailAddress">
								<a>
									<xsl:attribute name="href"><xsl:text disable-output-escaping="yes">mailto:</xsl:text><xsl:value-of select="../../n1:DatasetDerivations/n1:DatasetDerivation/n1:Supplier/n1:EmailAddresses/n1:EmailAddress"/></xsl:attribute>EMAIL</a>
							</xsl:if>
						</td>
					</tr>
				</table>
				<!-- *********************************/HEADER ************************************** 	-->
				<!-- *********************************IDENTIFICATIONS ************************************** 	-->		
        
        <h3>Informace o nálezu</h3>
        <table width="100%" border="3" border-styl="outset" cellspacing="0" cellpadding="0" bgcolor="white">              
          <tr>
            <th>Zdroj</th>            
            <th>Datum nálezu</th>
            <th>Poznámka k nálezu</th>                                
          </tr> 
          <tr>
             <td><xsl:value-of select="n1:UnitReferences/n1:UnitReference/n1:ReferenceCitation"/>, <xsl:value-of select="n1:UnitReferences/n1:UnitReference/n1:ReferenceDetail"/></td>             
             <td><xsl:value-of select="n1:Gathering/n1:GatheringDateTime/n1:ISODateTimeBegin"/></td>
             <td><xsl:value-of select="n1:Gathering/n1:GatheringAgents/n1:GhateringAgentsText"/></td>            
          </tr>
        </table> 
        
        <h3>Informace o lokalitě</h3>
        <table width="100%" border="3" border-styl="outset" cellspacing="0" cellpadding="0" bgcolor="white">              
          <tr>
            <th>Popis lokality</th>            
            <th>Nejbližší větší sídlo</th>   
            <th>Fytochorion</th>
            <th>Oblast</th>                 
            <th>Stát</th>   
            <th>Zeměpisná délka</th>
            <th>Zeměpisná šířka</th>
            <th>Nadmořská výška</th> 
            <th>Poznámka</th>        
          </tr> 
          <tr>
             <td><xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:LocalityText"/></td>
             <td><xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:NearNamedPlaces/n1:NamedPlaceRelation/n1:NearNamedPlace"/></td>
             <td><xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:NamedAreas/n1:NamedAra/n1:AreaCode"/></td>
             <td><xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:AreaDetail"/></td>
             <td><xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:CountryName"/></td>
             <td><xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:SiteCoordinateSets/n1:SiteCoordinates/n1:CoordinatesLatLong/n1:LongitudeDecimal "/></td>
             <td><xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:SiteCoordinateSets/n1:SiteCoordinates/n1:CoordinatesLatLong/n1:LatitudeDecimal"/></td>
             <td><xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Altitude/n1:MeasurementAtomized/n1:MeasurementLowerValue"/></td>
             <td><xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Notes"/></td>             
          </tr>
        </table>           
        
        <h3>Lidé spojeni s nálezem</h3>	 
       <table width="100%" border="3" border-styl="outset" cellspacing="0" cellpadding="0" bgcolor="white">              
        <tr>
          <th>Role</th>        
          <th>Jméno</th>
          <th>Email</th>
          <th width="200">Adresa</th>
        </tr>        
           <xsl:if test="n1:Gathering/n1:GatheringAgents">								
						<xsl:for-each select="n1:Gathering/n1:GatheringAgents/n1:GatheringAgent">
						 <tr>
						    <td>Nálezce</td> 
						    <td>
							     <xsl:value-of select="n1:Person/n1:PersonName"/>
							  </td>                 
                <td>---</td>
                <td>---</td>
							</tr>
						</xsl:for-each>								
					</xsl:if>  			
          <xsl:if test="n1:RecordRights">								
						<xsl:for-each select="n1:RecordRights/n1:LegalOwner">
						 <tr>
						    <td>Přispěvatel</td> 
						    <td><xsl:value-of select="n1:Person/n1:PersonName"/></td>
						    <td><xsl:value-of select="n1:EmailAddresses/n1:EmailAddress"/></td>
                <td><xsl:value-of select="n1:Addresses/n1:Address"/></td>                							  
							</tr>
						</xsl:for-each>								
					</xsl:if> 
          <xsl:if test="n1:Identifications/n1:Identifications/n1:Identification/n1:Identifier/n1:IdentifierPersonName/n1:PersonName">								
						<xsl:for-each select="n1:Identifications/n1:Identification">
						 <tr>
						    <td>Identifikoval</td> 
						    <td><xsl:value-of select="n1:Identifier/n1:IdentifierPersonName/n1:PersonName"/></td>
						    <td></td>
                <td></td>                							  
							</tr>
						</xsl:for-each>								
					</xsl:if>  	   
      </table>            				
			 	<!-- *********************************NOTES ************************************** 	-->
				<p/>
			</div>
			<p/>
		</xsl:for-each>
		</xsl:for-each>	
		<!--      **************************************UNIT*********************************************  -->
	</xsl:template>
</xsl:stylesheet>
