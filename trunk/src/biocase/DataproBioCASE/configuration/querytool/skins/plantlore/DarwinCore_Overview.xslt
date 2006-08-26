<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:fn="http://www.w3.org/2004/07/xpath-functions" 
	xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes"
	xmlns:digir="http://digir.net/schema/protocol/2003/1.0" 
	xmlns:dwc="http://digir.net/schema/conceptual/darwin/2003/1.0" 
	xmlns:dwcExt="http://www.bgbm.org/schemas/darwin2ext/querytool" 
	exclude-result-prefixes="xsl xs fn xdt">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html" />
	<xsl:param name="wrapper_url"/>
	<xsl:param name="dsa"/>
	<xsl:template match="/">

							<table id="recordlist" width="100%" border="0" cellspacing="0" cellpadding="2">
								<tr>
									<th nowrap="true">ID</th>
									<th nowrap="true">Identification</th>
									<th>Institution</th>
									<th>Collection</th>
									<th>RecordBasis</th>
									<th>Locality</th>
									<th>Country</th>
								</tr>
								<xsl:for-each select="//digir:record">
										<tr>
											<td nowrap="true">
												<!-- to link to the unit details we have to use the SourceInstitutionCode + SourceName + UnitID as GUID. Also include the wrapper_url cause might be different from the dsa  -->
												<a>
													<xsl:variable name="link">details.cgi?dsa=<xsl:value-of select="$dsa"/>&amp;wrapper_url=<xsl:value-of select="normalize-space(dwcExt:AccessPoint)"/>&amp;protocol=<xsl:value-of select="normalize-space(dwcExt:Protocol)"/>&amp;detail=unit&amp;inst=<xsl:value-of select="normalize-space(dwc:InstitutionCode)"/>&amp;col=<xsl:value-of select="normalize-space(dwc:CollectionCode)"/>&amp;cat=<xsl:value-of select="normalize-space(dwc:CatalogNumber)"/></xsl:variable>
													<xsl:choose>													
														<xsl:when test="dwcExt:ConceptualSchema">
															<xsl:attribute name="href"><xsl:value-of select="$link"/>&amp;schema=<xsl:value-of select="normalize-space(dwcExt:ConceptualSchema)"/></xsl:attribute>
														</xsl:when>
														<xsl:otherwise>
															<xsl:attribute name="href"><xsl:value-of select="$link"/>&amp;schema=http://digir.net/schema/conceptual/darwin/2003/1.0</xsl:attribute>
														</xsl:otherwise>
														</xsl:choose>
													<xsl:for-each select="dwc:CatalogNumber">
														<xsl:apply-templates/>
													</xsl:for-each>
												</a>
											</td>
											<td nowrap="true">
											<xsl:if test="dwc:ScientificName">
													<xsl:value-of select="dwc:ScientificName"/>
											</xsl:if>
											</td>
											<td>
											<xsl:if test="dwc:InstitutionCode">
													<xsl:value-of select="dwc:InstitutionCode"/>
											</xsl:if>
											</td>
											<td>
											<xsl:if test="dwc:CollectionCode">
													<xsl:value-of select="dwc:CollectionCode"/>
											</xsl:if>
											</td>
											<td>
											<xsl:if test="dwc:RecordBasis">
													<xsl:value-of select="dwc:RecordBasis"/>
											</xsl:if>
											</td>
											<td>
											<xsl:if test="dwc:Locality">
													<xsl:value-of select="dwc:Locality"/>
											</xsl:if>
											</td>
											<td>
											<xsl:if test="dwc:Country">
													<xsl:value-of select="dwc:Country"/>
											</xsl:if>
											</td>
										</tr>
									</xsl:for-each>
							</table>				

	</xsl:template>
</xsl:stylesheet>