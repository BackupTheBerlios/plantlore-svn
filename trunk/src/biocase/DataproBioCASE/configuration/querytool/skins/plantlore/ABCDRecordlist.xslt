<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" xmlns:n1="http://www.tdwg.org/schemas/abcd/1.2" exclude-result-prefixes="xsl xs fn xdt n1">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html" />
	<xsl:param name="wrapper_url"/>
	<xsl:param name="dsa"/>
	<xsl:param name="id"/>
	<xsl:param name="login"/>
	<xsl:template match="/">
					
				
					<xsl:for-each select="n1:DataSets">
						<xsl:for-each select="n1:DataSet">
							<xsl:for-each select="n1:OriginalSource">
								<h3>
									<xsl:if test="../n1:DatasetDerivations/n1:DatasetDerivation/n1:Statements/n1:LogoURL">
										<img border="0" src="{../n1:DatasetDerivations/n1:DatasetDerivation/n1:Statements/n1:LogoURL}" align="right" height="45"/>
									</xsl:if>
									<!-- resource Identifier -->
									<a name="{normalize-space(n1:SourceInstitutionCode)}-{normalize-space(n1:SourceName)}" class="linktarget">
										<xsl:for-each select="n1:SourceName">
											<xsl:apply-templates/>
										</xsl:for-each> (<xsl:for-each select="n1:SourceInstitutionCode">
											<xsl:apply-templates/>
										</xsl:for-each>) </a>
								</h3>
								<p>
									<span class="label">Poslední aktualizace: </span>
									<xsl:for-each select="n1:SourceLastUpdatedDate">
										<xsl:apply-templates/>
									</xsl:for-each>|
									
<xsl:if test="n1:SourceWebAddress">
										<a>
											<xsl:attribute name="href"><xsl:value-of select="n1:SourceWebAddress"/></xsl:attribute>URL</a> |</xsl:if>
									<xsl:if test="../n1:DatasetDerivations/n1:DatasetDerivation/n1:Supplier/n1:EmailAddresses/n1:EmailAddress">
										<a>
											<xsl:attribute name="href"><xsl:text disable-output-escaping="yes">mailto:</xsl:text><xsl:value-of select="../n1:DatasetDerivations/n1:DatasetDerivation/n1:Supplier/n1:EmailAddresses/n1:EmailAddress"/></xsl:attribute>EMAIL</a>
									</xsl:if>
								</p>
							</xsl:for-each>
							<table width="96%" border="2" cellspacing="0" cellpadding="0">
								<tr>
									<th>							
									UnitId</th>
									<th>Prefered Name(s) </th>
									<xsl:if test="n1:Units/n1:Unit/n1:RecordBasis">
										<th>Record Basis </th>
									</xsl:if>
									<xsl:if test="n1:Units/n1:Unit/n1:Gathering/n1:GatheringSite/n1:LocalityText">
										<th>Locality</th>
									</xsl:if>
									<xsl:if test="n1:Units/n1:Unit/n1:Gathering/n1:GatheringSite/n1:Country">
										<th>Country</th>
									</xsl:if>
								</tr>
								<xsl:for-each select="n1:Units">
									<xsl:for-each select="n1:Unit">
										<tr>
											<td>
												<!-- to link to the unit details we have to use the SourceInstitutionCode + SourceName + UnitID as GUID. Also include the wrapper_url cause might be different from the dsa  -->
												<a>
												<xsl:attribute name="href">details.cgi?dsa=<xsl:value-of select="$dsa"/>&amp;id=<xsl:value-of select="$id"/>&amp;login=<xsl:value-of select="$login"/>&amp;wrapper_url=<xsl:value-of select="$wrapper_url"/>&amp;detail=unit&amp;SourceInstitutionCode=<xsl:value-of select="normalize-space(../../n1:OriginalSource/n1:SourceInstitutionCode)"/>&amp;SourceName=<xsl:value-of select="normalize-space(../../n1:OriginalSource/n1:SourceName)"/>&amp;UnitID=<xsl:value-of select="normalize-space(n1:UnitID)"/></xsl:attribute>
													<xsl:for-each select="n1:UnitID">
														<xsl:apply-templates/>
													</xsl:for-each>
												</a>
											</td>
											<td>
												
<!-- ***********************************************************************+NAME****************************************************** -->												
												<xsl:for-each select="n1:Identifications/n1:Identification">
											<!--General rule for names -->
											<!-- Avoid execution if there are no Identifications-->
											
												<!--Differenciate if there is only one identification o more -->
												<xsl:choose>
													<xsl:when test="count(../n1:Identification) = 1">
														<!--Only one identification, is shown as non prefered -->
														<span class="style2"><xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/></span>
													</xsl:when>
													<!-- more than 1 identifications -->
													<xsl:otherwise>
														<!--Check if there is a preferedflag -->
														<xsl:choose>
															<xsl:when test="not(@PreferredIdentificationFlag)">
																<!-- Identification without prefered flag -->
																<xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/> |
															</xsl:when>
															<xsl:otherwise>
																<xsl:choose>
																	<xsl:when test="@PreferredIdentificationFlag= '1' or @PreferredIdentificationFlag='true'">
																		<!--Multiple identifications, prefered one -->
																		<xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<!--Multiple identifications, non prefered one -->
																	</xsl:otherwise>
																</xsl:choose>
															</xsl:otherwise>
														</xsl:choose>	
													</xsl:otherwise>
												</xsl:choose>		
											</xsl:for-each>
<!-- ***********************************************************************+NAME****************************************************** -->
												
											</td>
											<xsl:if test="../../n1:Units/n1:Unit/n1:RecordBasis">
												<td>
													<xsl:if test="n1:RecordBasis">
														<xsl:value-of select="n1:RecordBasis"/>
													</xsl:if>
												</td>
											</xsl:if>
											<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:GatheringSite/n1:LocalityText">
												<td>
													<xsl:if test="n1:Gathering/n1:GatheringSite/n1:LocalityText">
														<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:LocalityText"/>
													</xsl:if>
												</td>
											</xsl:if>
											<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:GatheringSite/n1:Country">
												<td>
													<xsl:if test="n1:Gathering/n1:GatheringSite/n1:Country">
														<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:CountryName"/> <xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:ISO2Letter"/> <xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:ISO3Letter"/>
													</xsl:if>
												</td>
											</xsl:if>
										</tr>
									</xsl:for-each>
								</xsl:for-each>
							</table>												
	
						</xsl:for-each>
					</xsl:for-each>
		<p style="font-size:10"><br/>Pro zpobrazení detailu klikněte na UnitId. </p>
	</xsl:template>
</xsl:stylesheet>

