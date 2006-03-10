<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" 
  xmlns:dwc="http://www.namespaceTBD.org/darwin2" exclude-result-prefixes="xsl xs fn xdt">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html" />
	<xsl:param name="wrapper_url"/>
	<xsl:param name="dsa"/>
	<xsl:template match="/">
					<p>Click on UnitId to get details. 
					<!-- General map -->
						<xsl:if test="//dwc:Latitude">
							<a href="Javascript:document.GeneralMap.submit()">Create Map!</a> |
						<form action="http://linuxgurrl.agr.gc.ca/mapdata/itis/itisrosa.php" method="POST" target="_blank" name="GeneralMap">
								<input>
									<xsl:attribute name="value">%3C%3Fxml+version%3D%271.0%27+encoding%3D%27iso-8859-1%27%3F%3E%3Cresponse%3E%3Cheader%3E%3Cauthor%3E%3C%2Fauthor%3E%3Cboundingbox%3E-180%2C-90%2C180%2C90%2CWorld%3C%2Fboundingbox%3E%3Cdescription%3EThis+Generic+Point+Mapper+is+a+service+provided+by+the+Canadian+Biological+Information+Facility%3C%2Fdescription%3E%3Cifx%3E%3C%2Fifx%3E%3Clanguage%3Een%3C%2Flanguage%3E%3Cprojection%3Elatlong%3C%2Fprojection%3E%3Crecordcount%3E%3C%2Frecordcount%3E%3Ctimestamp%3E2004-11-30+11%3A10%3A58.043%3C%2Ftimestamp%3E%3Ctitle%3ESimple+access+to+ABCD+providers+-+Point+Location+Data%3C%2Ftitle%3E%3Curl%3Ehttp%3A%2F%2Fwww.cbif.gc.ca%2Fmc%2Findex_e.php%3C%2Furl%3E%3C%2Fheader%3E%3Crecords	
										<xsl:for-each select="dwc:RecordSet/dwc:Record">
															%3E%3Crecord
																<xsl:for-each select="dwc:Latitude">
																		%3E%3Clatitude%3E<xsl:apply-templates/>%3C%2Flatitude
																	</xsl:for-each><xsl:for-each select="dwc:Longitude">
																		%3E%3Clongitude%3E<xsl:apply-templates/>%3C%2Flongitude
																	</xsl:for-each>
																%3E%3Crecordurl%3E%3C%2Frecordurl
																%3E%3C%2Frecord
										</xsl:for-each>
									%3E%3C%2Frecords%3E%3C%2Fresponse%3E
								</xsl:attribute>
									<xsl:attribute name="type">hidden</xsl:attribute>
									<xsl:attribute name="name">xml</xsl:attribute>
								</input>
							</form>
						</xsl:if>
						<!-- Finish General Map -->
					</p>
							<xsl:for-each select="dwc:OriginalSource">
								<h3>
									<xsl:if test="../dwc:DatasetDerivations/dwc:DatasetDerivation/dwc:Statements/dwc:LogoURL">
										<img border="0" src="{../dwc:DatasetDerivations/dwc:DatasetDerivation/dwc:Statements/dwc:LogoURL}" align="right" height="45"/>
									</xsl:if>
									<!-- resource Identifier -->
									<a name="{normalize-space(dwc:SourceInstitutionCode)}-{normalize-space(dwc:SourceName)}" class="linktarget">
										<xsl:for-each select="dwc:SourceName">
											<xsl:apply-templates/>
										</xsl:for-each> (<xsl:for-each select="dwc:SourceInstitutionCode">
											<xsl:apply-templates/>
										</xsl:for-each>) </a>
								</h3>
								<p>
									<span class="label">Last update: </span>
									<xsl:for-each select="dwc:SourceLastUpdatedDate">
										<xsl:apply-templates/>
									</xsl:for-each>|
									
<xsl:if test="dwc:SourceWebAddress">
										<a>
											<xsl:attribute name="href"><xsl:value-of select="dwc:SourceWebAddress"/></xsl:attribute>URL</a> |</xsl:if>
									<xsl:if test="../dwc:DatasetDerivations/dwc:DatasetDerivation/dwc:Supplier/dwc:EmailAddresses/dwc:EmailAddress">
										<a>
											<xsl:attribute name="href"><xsl:text disable-output-escaping="yes">mailto:</xsl:text><xsl:value-of select="../dwc:DatasetDerivations/dwc:DatasetDerivation/dwc:Supplier/dwc:EmailAddresses/dwc:EmailAddress"/></xsl:attribute>EMAIL</a>
									</xsl:if>
								</p>
							</xsl:for-each>
							
							
							<table width="96%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<th>							
									UnitId</th>
									<xsl:if test="dwc:RecordSet/dwc:Record/dwc:InstitutionCode">
										<th>Institution</th>
									</xsl:if>
									<xsl:if test="dwc:RecordSet/dwc:Record/dwc:CollectionCode">
										<th>Collection</th>
									</xsl:if>
									<th>Prefered Name </th>
									<xsl:if test="dwc:RecordSet/dwc:Record/dwc:RecordBasis">
										<th>Record Basis </th>
									</xsl:if>
									<xsl:if test="dwc:RecordSet/dwc:Record/dwc:Locality">
										<th>Locality</th>
									</xsl:if>
									<xsl:if test="dwc:RecordSet/dwc:Record/dwc:Country">
										<th>Country</th>
									</xsl:if>
								</tr>
								<xsl:for-each select="dwc:RecordSet/dwc:Record">
										<tr>
											<td>
												<!-- to link to the unit details we have to use the SourceInstitutionCode + SourceName + UnitID as GUID. Also include the wrapper_url cause might be different from the dsa  -->
												<a>
												<xsl:attribute name="href">details.cgi?dsa=<xsl:value-of select="$dsa"/>&amp;wrapper_url=<xsl:value-of select="$wrapper_url"/>&amp;detail=unit&amp;Inst=<xsl:value-of select="normalize-space(dwc:InstitutionCode)"/>&amp;Col=<xsl:value-of select="normalize-space(dwc:CollectionCode)"/>&amp;Cat=<xsl:value-of select="normalize-space(dwc:CatalogNumber)"/></xsl:attribute>
													<xsl:for-each select="dwc:CatalogNumber">
														<xsl:apply-templates/>
													</xsl:for-each>
												</a>
											</td>
											<xsl:if test="dwc:InstitutionCode">
												<td>
													<xsl:value-of select="dwc:InstitutionCode"/>
												</td>
											</xsl:if>
											<xsl:if test="dwc:CollectionCode">
												<td>
													<xsl:value-of select="dwc:CollectionCode"/>
												</td>
											</xsl:if>
											<xsl:if test="dwc:ScientificName">
												<td>
													<xsl:value-of select="dwc:ScientificName"/>
												</td>
											</xsl:if>
											<xsl:if test="dwc:RecordBasis">
												<td>
													<xsl:value-of select="dwc:RecordBasis"/>
												</td>
											</xsl:if>
											<xsl:if test="dwc:Locality">
												<td>
													<xsl:value-of select="dwc:Locality"/>
												</td>
											</xsl:if>
											<xsl:if test="dwc:Country">
												<td>
													<xsl:value-of select="dwc:Country"/>
												</td>
											</xsl:if>
										</tr>
									</xsl:for-each>
							</table>
							<p>
								<xsl:if test="dwc:Latitude">
									<a>
										<xsl:attribute name="href">
										<xsl:text disable-output-escaping="yes">Javascript:document.form</xsl:text>
										<xsl:value-of select="dwc:InstitutionCode"/>
										<xsl:text disable-output-escaping="yes">.submit()</xsl:text>
										</xsl:attribute>
									Create Map!</a>
								</xsl:if>
							</p>
							<!-- Form to create a map, only if there is data on it-->
							<xsl:if test="dwc:Latitude">
								<form action="http://linuxgurrl.agr.gc.ca/mapdata/itis/itisrosa.php" method="POST" target="_blank">
									<xsl:attribute name="name">form<xsl:value-of select="dwc:InstitutionCode"/></xsl:attribute>
									<input>
										<xsl:attribute name="value">%3C%3Fxml+version%3D%271.0%27+encoding%3D%27iso-8859-1%27%3F%3E%3Cresponse%3E%3Cheader%3E%3Cauthor%3E%3C%2Fauthor%3E%3Cboundingbox%3E-180%2C-90%2C180%2C90%2CWorld%3C%2Fboundingbox%3E%3Cdescription%3EThis+Generic+Point+Mapper+is+a+service+provided+by+the+Canadian+Biological+Information+Facility%3C%2Fdescription%3E%3Cifx%3E%3C%2Fifx%3E%3Clanguage%3Een%3C%2Flanguage%3E%3Cprojection%3Elatlong%3C%2Fprojection%3E%3Crecordcount%3E%3C%2Frecordcount%3E%3Ctimestamp%3E2004-11-30+11%3A10%3A58.043%3C%2Ftimestamp%3E%3Ctitle%3ESimple+access+to+ABCD+providers+-+Point+Location+Data%3C%2Ftitle%3E%3Curl%3Ehttp%3A%2F%2Fwww.cbif.gc.ca%2Fmc%2Findex_e.php%3C%2Furl%3E%3C%2Fheader%3E%3Crecords	
											%3E%3Crecord
												<xsl:for-each select="dwc:Latitude">
														%3E%3Clatitude%3E<xsl:apply-templates/>%3C%2Flatitude
													</xsl:for-each><xsl:for-each select="dwc:Longitude">
														%3E%3Clongitude%3E<xsl:apply-templates/>%3C%2Flongitude
													</xsl:for-each>
												%3E%3Crecordurl%3E%3C%2Frecordurl
												%3E%3C%2Frecord
									%3E%3C%2Frecords%3E%3C%2Fresponse%3E
								</xsl:attribute>
										<xsl:attribute name="type">hidden</xsl:attribute>
										<xsl:attribute name="name">xml</xsl:attribute>
									</input>
								</form>
							</xsl:if>
							<!-- /For to create a map-->				

	</xsl:template>
</xsl:stylesheet>