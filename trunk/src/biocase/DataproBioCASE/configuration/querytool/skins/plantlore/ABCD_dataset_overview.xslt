<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" xmlns:n1="http://www.tdwg.org/schemas/abcd/1.2" exclude-result-prefixes="xsl xs fn xdt n1">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html" />
	<xsl:param name="dsa"/>
	<xsl:template match="/">


      <table width="98%" border="1" align="center" cellpadding="0" cellspacing="0" bordercolor="#333333" bgcolor="#F2F2F2" style="border-collapse: collapse">
        <tr>
          <td><table width="100%" border="1" cellpadding="4" cellspacing="0" bordercolor="#FFFFFF"  bgcolor="white" class="normal" style="border-collapse: collapse">
              <tr bgcolor="#cccc97">
                <td width="13%"><strong>Details (ID)</strong></td>
                <td width="15%"><strong>Exsiccatum</strong></td>
                <td width="55%"><strong>Country, locality </strong></td>
                <td width="17%"><strong>Collecting date </strong></td>
              </tr>
              
					<xsl:for-each select="n1:DataSets">
						<xsl:for-each select="n1:DataSet">

								<xsl:for-each select="n1:Units">
									<xsl:for-each select="n1:Unit">
										<tr bgcolor="white">
											<td>
												<!-- to link to the unit details we have to use the SourceInstitutionCode + SourceName + UnitID as GUID -->
												<a>
												<xsl:attribute name="href">details.cgi?dsa=<xsl:value-of select="$dsa"/>&amp;detail=unit&amp;SourceInstitutionCode=<xsl:value-of select="normalize-space(../../n1:OriginalSource/n1:SourceInstitutionCode)"/>&amp;SourceName=<xsl:value-of select="normalize-space(../../n1:OriginalSource/n1:SourceName)"/>&amp;UnitID=<xsl:value-of select="normalize-space(n1:UnitID)"/></xsl:attribute>
													<xsl:for-each select="n1:UnitID">
														<xsl:apply-templates/>
													</xsl:for-each>
												</a>
											</td>

											<td>
											<xsl:if test="../../n1:Units/n1:Unit/n1:UnitCollectionDomain/n1:HerbariumUnit/n1:Exsiccatum">
													<xsl:if test="n1:UnitCollectionDomain/n1:HerbariumUnit/n1:Exsiccatum">
														<xsl:value-of select="n1:UnitCollectionDomain/n1:HerbariumUnit/n1:Exsiccatum"/>
													</xsl:if>
											</xsl:if>
											</td>
											
											<td>
											<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:GatheringSite/n1:Country">
													<xsl:if test="n1:Gathering/n1:GatheringSite/n1:Country">
														<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:CountryName"/> <xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:ISO2Letter"/> <xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:ISO3Letter"/>
													</xsl:if>
													, 
											</xsl:if>
											<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:GatheringSite/n1:LocalityText">
													<xsl:if test="n1:Gathering/n1:GatheringSite/n1:LocalityText">
														<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:LocalityText"/>
													</xsl:if>
											</xsl:if>
											</td>

											<td>
											<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:GatheringDateTime/n1:ISODateTimeBegin">
													<xsl:if test="n1:Gathering/n1:GatheringDateTime/n1:ISODateTimeBegin">
														<xsl:value-of select="n1:Gathering/n1:GatheringDateTime/n1:ISODateTimeBegin"/>
													</xsl:if>
											</xsl:if>
											</td>
											
										</tr>
									</xsl:for-each>
								</xsl:for-each>	
							
						</xsl:for-each>
					</xsl:for-each>
				
            </table></td>
        </tr>
      </table>

	</xsl:template>
</xsl:stylesheet>
