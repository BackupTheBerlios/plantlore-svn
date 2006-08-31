<?xml version="1.0" encoding="UTF-8"?>

<!-- 	**************************************************************************
		**	Dataset overview 	 for ABCD 2.0									**
		**																		**
		**	This template provides a simple overview of an ABCD document.		**
		**	It needs to															**
		**	include the ABCD1.2_CommonTemplates.xslt file, but if it is being 	**
		**	called by the All_parts XSLT then it is already included before		**
		**	and should be mantained commented									**
		**																		**
		**	Author: Cristian Oancea   Updated: 											**
		**************************************************************************
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" xmlns:n1="http://www.tdwg.org/schemas/abcd/2.06" exclude-result-prefixes="xsl xs fn xdt n1">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html"/>
	<xsl:include href="en_ABCD2.0_CommonTemplates.xslt" />
	<!-- if no dsa is passed use relative anchor links instead of querytool unit links  -->
	<xsl:param name="dsa"/>
	<xsl:param name="unitlink"/>
	<xsl:param name="id"/>
	<xsl:param name="login"/>
		
	<xsl:template name="DatasetOverview" match="/">
	
		 <xsl:for-each select="n1:DataSets">
			 <xsl:for-each select="n1:DataSet/n1:Metadata">
				 <xsl:variable name="DataSetId" select="position()"/>
                				
					<h3>
						<xsl:if test="../n1:Metadata/n1:Owners/n1:Owner/n1:LogoURL">
							<img border="0" src="{n1.Owner/n1:LogoURL}" align="right" height="45"/>
						</xsl:if>						
						<xsl:if test="../n1:Units/n1:Unit/n1:SourceID">
						   <br/>
						  <span class="subText">
							     <xsl:value-of select="../n1:Units/n1:Unit/n1:SourceID"/> 
							     (<xsl:value-of select="../n1:Units/n1:Unit/n1:SourceInstitutionID"/>)
							 </span>    
						</xsl:if>						
					</h3>
					
				<p>	
			 	  <span class="label">Datum poslední aktualizace: </span>
						<xsl:value-of select="../n1:Metadata/n1:RevisionData/n1:DateModified"/>|
					<xsl:if test="../n1:Metadata/n1:Owners/n1:Owner/n1:URIs"> 
						<a href="{../n1:Metadata/n1:Owners/n1:Owner/n1:URIs/n1:URL}" target ="_blank">URL</a>|
						</xsl:if>
					<xsl:if test="../n1:Metadata/n1:Owners/n1:Owner/n1:EmailAddresses/n1:EmailAddress"> <a href="mailto:{../n1:Metadata/n1:Owners/n1:Owner/n1:EmailAddresses/n1:EmailAddress}">EMAIL</a></xsl:if>
					<br/>Na detail nálezu se dostanete kliknutím na id.č.
				</p>	
					
				<table width="100%" border="3" border-styl="outset" cellspacing="0" cellpadding="0" bgcolor="white">
					<tr>
						<th width="50">Id.č.</th>
						<th>Taxon</th>											
						<th width="100">Nejbližší obec</th>											
						<th width="150">Popis lokality</th>			
            <th width="70">Datum nálezu</th>			
					</tr>					
					
					<xsl:for-each select="../n1:Units">
						<xsl:for-each select="n1:Unit">							
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
												<xsl:attribute name="href"><xsl:value-of select="$unitlink"/>&amp;inst=<xsl:value-of select="normalize-space(n1:SourceInstitutionID)"/>&amp;id=<xsl:value-of select="$id"/>&amp;login=<xsl:value-of select="$login"/>&amp;col=<xsl:value-of select="normalize-space(n1:SourceID)"/>&amp;cat=<xsl:value-of select="normalize-space(n1:UnitID)"/></xsl:attribute>
											</xsl:otherwise>
										</xsl:choose>
										
										<xsl:for-each select="n1:UnitID">
											<xsl:apply-templates/>
										</xsl:for-each>
									</a>
								</td>
								<td>
									 <xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:FullScientificNameString"/>
								</td>																
									<td>
										<xsl:if test="n1:Gathering/n1:NearNamedPlaces/n1:NamedPlaceRelation/n1:NearNamedPlace">
											<xsl:value-of select="n1:Gathering/n1:NearNamedPlaces/n1:NamedPlaceRelation/n1:NearNamedPlace"/>
										</xsl:if>
									</td>								
								<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:LocalityText">
									<td>
										<xsl:if test="n1:Gathering/n1:LocalityText">
											<xsl:value-of select="n1:Gathering/n1:LocalityText"/>                       
										</xsl:if>
									</td>
								</xsl:if>
								<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:DateTime/n1:ISODateTimeBegin">
									<td>
										<xsl:if test="n1:Gathering/n1:DateTime/n1:ISODateTimeBegin">
											<xsl:value-of select="n1:Gathering/n1:DateTime/n1:ISODateTimeBegin"/>
										</xsl:if>
									</td>
								</xsl:if>
							</tr>
						</xsl:for-each>
					</xsl:for-each>
				</table>								
											
			</xsl:for-each>
			<br></br>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
