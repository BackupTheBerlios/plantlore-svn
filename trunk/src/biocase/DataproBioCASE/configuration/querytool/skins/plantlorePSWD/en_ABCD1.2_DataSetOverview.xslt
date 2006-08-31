<?xml version="1.0" encoding="UTF-8"?>

<!-- 	**************************************************************************
		**	Dataset overview 	 for ABCD 1.2									**
		**																		**
		**	This template provides a simple overview of an ABCD document.		**
		**	It needs to															**
		**	include the en_ABCD1.2_CommonTemplates.xslt file, but if it is being 	**
		**	called by the All_parts XSLT then it is already included before		**
		**	and should be mantained commented									**
		**																		**
		**	Author: Javier de la Torre, Markus Döring							**
		**************************************************************************
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" xmlns:n1="http://www.tdwg.org/schemas/abcd/1.2" exclude-result-prefixes="xsl xs fn xdt n1">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html"/>
	<xsl:include href="en_ABCD1.2_CommonTemplates.xslt" />
	<!-- if no dsa is passed use relative anchor links instead of querytool unit links  -->
	<xsl:param name="dsa"/>
	<xsl:param name="unitlink"/>
	<xsl:param name="id"/>
	<xsl:param name="login"/>
	
	<xsl:template name="DatasetOverview" match="/">
    
		<xsl:for-each select="n1:DataSets">
			<xsl:for-each select="n1:DataSet">
			<xsl:variable name="DataSetId" select="position()"/>
				<xsl:for-each select="n1:OriginalSource">
					<h3>
						<xsl:if test="../n1:DatasetDerivations/n1:DatasetDerivation/n1:Statements/n1:LogoURL">
							<img border="0" src="{../n1:DatasetDerivations/n1:DatasetDerivation/n1:Statements/n1:LogoURL}" align="right" height="45"/>
						</xsl:if>
						<!-- resource Identifier -->
						<br/>
						<span class="subText"><xsl:for-each select="n1:SourceName"><xsl:apply-templates/></xsl:for-each> (<xsl:for-each select="n1:SourceInstitutionCode"><xsl:apply-templates/></xsl:for-each>)</span>
					</h3>
					<p>
						<span class="label">Datum poslední aktualizace: </span>
						<xsl:value-of select="n1:SourceLastUpdatedDate"/> | <xsl:if test="n1:SourceWebAddress"> <a href="{n1:SourceWebAddress}" target ="_blank">URL</a>|</xsl:if>
						<xsl:if test="../n1:DatasetDerivations/n1:DatasetDerivation/n1:Supplier/n1:EmailAddresses/n1:EmailAddress"> <a href="mailto:{../n1:DatasetDerivations/n1:DatasetDerivation/n1:Supplier/n1:EmailAddresses/n1:EmailAddress}">EMAIL</a></xsl:if>
					  <br/>Na detail nálezu se dostanete kliknutím na id.č.
					</p>
				</xsl:for-each>
				<table width="100%" border="3" border-styl="outset" cellspacing="0" cellpadding="0" bgcolor="white">
					<tr>
						<th width="50">Id.č.</th>
						<th>Taxon</th>											
						<th width="100">Nejbližší obec</th>											
						<th width="150">Popis lokality</th>			
            <th width="70">Datum nálezu</th>			
					</tr>
					<xsl:for-each select="n1:Units">
						<xsl:for-each select="n1:Unit">
							<xsl:variable name="UnitID" select="concat($DataSetId,'_',position())"/>
							<tr>
								<td>
									<!-- to link to the unit details we have to use the SourceInstitutionCode + SourceName + UnitID as GUID -->
									<a>
										<xsl:choose>
											<xsl:when test="string($dsa)='' ">
												<!-- relative link -->
												<xsl:attribute name="href">#<xsl:value-of select="$UnitID"/></xsl:attribute>
											</xsl:when>
											<xsl:otherwise>
												<!-- its a querytool link -->
												<xsl:attribute name="href"><xsl:value-of select="$unitlink"/>&amp;id=<xsl:value-of select="$id"/>&amp;login=<xsl:value-of select="$login"/>&amp;inst=<xsl:value-of select="normalize-space(../../n1:OriginalSource/n1:SourceInstitutionCode)"/>&amp;col=<xsl:value-of select="normalize-space(../../n1:OriginalSource/n1:SourceName)"/>&amp;cat=<xsl:value-of select="normalize-space(n1:UnitID)"/></xsl:attribute>
											</xsl:otherwise>
										</xsl:choose>
										<xsl:for-each select="n1:UnitID">
											<xsl:apply-templates/>
										</xsl:for-each>
									</a>
								</td>
								<td>
									 <xsl:value-of select="n1:Identifications/n1:Identification/n1:TaxonIdentified/n1:NameAuthorYearString"/>
								</td>																
									<td>
										<xsl:if test="n1:Gathering/n1:GatheringSite/n1:NearNamedPlaces/n1:NamedPlaceRelation/n1:NearNamedPlace">
											<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:NearNamedPlaces/n1:NamedPlaceRelation/n1:NearNamedPlace"/>
										</xsl:if>
									</td>								
								<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:GatheringSite/n1:LocalityText">
									<td>
										<xsl:if test="n1:Gathering/n1:GatheringSite/n1:LocalityText">
											<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:LocalityText"/>                       
										</xsl:if>
									</td>
								</xsl:if>
								<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:GatheringDateTime/n1:ISODateTimeBegin">
									<td>
										<xsl:if test="n1:Gathering/n1:GatheringDateTime/n1:ISODateTimeBegin">
											<xsl:value-of select="n1:Gathering/n1:GatheringDateTime/n1:ISODateTimeBegin"/>
										</xsl:if>
									</td>
								</xsl:if>
							</tr>
						</xsl:for-each>
					</xsl:for-each>
				</table>
				<xsl:for-each select="n1:DatasetDerivations/n1:DatasetDerivation/n1:Rights">
					<xsl:call-template name="RightsTable"/>					
				</xsl:for-each>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
