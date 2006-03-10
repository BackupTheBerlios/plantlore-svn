<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" 
xmlns:dwc="http://www.namespaceTBD.org/darwin2" exclude-result-prefixes="xsl xs fn xdt dwc">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html" />
	<xsl:template match="/">

					<!--      **************************************UNIT*********************************************  -->
					<xsl:for-each select="dwc:RecordSet/dwc:Record">
						<div id="Unit">
							<!-- *********************************/HEADER ************************************** 	-->
							<table width="96%" border="0">
								<tr>
									<td>
										<span class="style1">
											<xsl:for-each select="dwc:ScientificName">
												<!--Only one identification, is shown as non prefered -->
												<span class="preferedbig"><xsl:value-of select="dwc:ScientificName"/></span>
											</xsl:for-each>
										</span>
										<br/>
										<!-- ** GUID ** -->
										<a name="{normalize-space(dwc:InstitutionCode)}-{normalize-space(dwc:CollectionCode)}-{normalize-space(dwc:CatalogNumber)}" class="linktarget">
											<span class="label">Catalog Number: </span>
											<span class="style1">
												<xsl:value-of select="dwc:CatalogNumber"/>
											</span>
										</a>
										<xsl:if test="dwc:BasisOfRecord">
											<br/>
											<span class="label">Record Basis: </span>
											<xsl:value-of select="dwc:BasisOfRecord"/>
										</xsl:if>
									</td>
									<td align="right">
										<span class="label">Institution Code: </span>
										<xsl:value-of select="dwc:InstitutionCode"/> (<xsl:value-of select="InstitutionCode"/>)<br/>
										<span class="label">Last update: </span>
										<xsl:value-of select="dwc:DateLastModified"/>
										<br/>
									</td>
								</tr>
							</table>
							<!-- *********************************/HEADER ************************************** 	-->
							<!-- *********************************IDENTIFICATIONS ************************************** 	-->
							<h3 class="background">Identification(s): </h3>
							<table width="96%" border="0">
								<tr>
									<th>Name</th>
									<th>Taxonomy</th>
								</tr>
									<!-- Change the color of the background depending if it is a PREFERED identification or not -->
									<tr>

										<td>
											<xsl:value-of select="dwc:ScientificName"/>
											<xsl:if test="dwc:YearIdentified">
												<br/>
												<span class="label">Date: </span>
												<xsl:value-of select="dwc:YearIdentified"/>/
												<xsl:value-of select="dwc:MonthIdentified"/>/
												<xsl:value-of select="dwc:DayIdentified"/>
											</xsl:if>
											<xsl:if test="dwc:IdentifiedBy">
												<br/>
												<span class="label">Identifier: </span>
												<xsl:value-of select="dwc:IdentifiedBy"/>
											</xsl:if>
										</td>
										<td>
											<xsl:if test="dwc:Kingdom">
												<span class="label">Kingdom: </span>
												<xsl:value-of select="dwc:Kingdom"/>
												<br/>
											</xsl:if>
											<xsl:if test="dwc:Phylum">
												<span class="label">Phylum: </span>
												<xsl:value-of select="dwc:Phylum"/>
												<br/>
											</xsl:if>
											<xsl:if test="dwc:Class">
												<span class="label">Class: </span>
												<xsl:value-of select="dwc:Class"/>
												<br/>
											</xsl:if>
											<xsl:if test="dwc:Order">
												<span class="label">Order: </span>
												<xsl:value-of select="dwc:Order"/>
												<br/>
											</xsl:if>
											<xsl:if test="dwc:Family">
												<span class="label">Family: </span>
												<xsl:value-of select="dwc:Family"/>
												<br/>
											</xsl:if>
											<xsl:if test="dwc:Genus">
												<span class="label">Genus: </span>
												<xsl:value-of select="dwc:Genus"/>
											</xsl:if>
										</td>
									</tr>
							</table>
							<!-- *********************************IDENTIFICATIONS ************************************** 	-->
							<!-- *********************************GATHERING ************************************** 	-->
								<h3 class="background">Gathering:</h3>
								<table width="96%">
									<tr>
										<th>Locality:</th>
										<xsl:if test="dwc:Collector">
											<th>Collector(s):</th>
										</xsl:if>
										<th>Other info:</th>
									</tr>
									<tr valign="top" bgcolor="#f4f4f4">
										<td>
											<xsl:if test="dwc:Country">
												<span class="label">Country: </span>
												<xsl:value-of select="dwc:Country"/> 
												<br/>
											</xsl:if>
											<xsl:if test="dwc:StateProvince">
													<span class="label">
														State or Province: </span>
													<xsl:value-of select="dwc:StateProvince"/>
													<br/>
											</xsl:if>
											<xsl:if test="dwc:County">
													<span class="label">
														County: </span>
													<xsl:value-of select="dwc:County"/>
													<br/>
											</xsl:if>
											<xsl:if test="dwc:Locality">
												<xsl:value-of select="dwc:Locality"/>
											</xsl:if>
											
											<xsl:if test="dwc:Latitude">
											<br/>
											<form action="http://linuxgurrl.agr.gc.ca/mapdata/itis/itisrosa.php" method="POST" target="_blank">
												<xsl:attribute name="name">GeneralMap</xsl:attribute>
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
												Coordinates: <xsl:value-of select="dwc:Latitude"/> | <xsl:value-of select="dwc:Longitude"/> <a href="Javascript:document.GeneralMap.submit()"> Map!</a>
											</form>
										</xsl:if>
										<!-- /For to create a map-->
										</td>


										<xsl:if test="dwc:Collector">
											<td>
												<xsl:for-each select="dwc:Collector">
													<xsl:apply-templates/>
													<br/>
												</xsl:for-each>
											</td>
										</xsl:if>
										<td>
										
											<xsl:if test="string(dwc:FieldNumber)">
												<span class="label">Field Number: </span>
												<xsl:value-of select="dwc:FieldNumber"/>
											</xsl:if>
											<xsl:if test="string(dwc:MinimumElevation)">
												<xsl:for-each select="dwc:MinimumElevation">
													<span class="label">Altitude : </span>
													<xsl:apply-templates/>
													<xsl:if test="string(dwc:MaximumElevation)">- <xsl:value-of select="dwc:MaximumElevation"/></xsl:if>
													<br/>
												</xsl:for-each>
											</xsl:if>
											<xsl:if test="string(dwc:YearCollected)">
												<span class="label">Date: </span>
												<xsl:value-of select="dwc:YearCollected"/>/
												<xsl:value-of select="dwc:MonthCollected"/>/
												<xsl:value-of select="dwc:DayCollected"/>
											</xsl:if>

										</td>
									</tr>
								</table>
							<!-- *********************************GATHERING ************************************** 	-->
							<!-- *********************************OTHER DATA ************************************** 	-->
							<xsl:if test="dwc:LastEditor or dwc:DateLastEdited or dwc:CollectorsFieldNumber or dwc:TypeStatus">
								<h3 class="background">Other data:</h3>
								<table>
									<tbody>
										<tr bgcolor="#f4f4f4" valign="top">
											<td>
												<xsl:for-each select="dwc:Sex">
													<span class="label">Sex:</span>
													<xsl:apply-templates/>
												</xsl:for-each>
												<xsl:for-each select="dwc:TypeStatus">
													<br/>
													<span class="label">Type Status: </span>
													<xsl:apply-templates/>
												</xsl:for-each>
												<xsl:for-each select="dwc:PreparationType">
													<br/>
													<span class="label">Preparation Type: </span>
													<xsl:apply-templates/>
												</xsl:for-each>
												<xsl:for-each select="dwc:IndividualCount">
													<br/>
													<span class="label">Individual Count: </span>
													<xsl:apply-templates/>
												</xsl:for-each>
											</td>
										</tr>
									</tbody>
								</table>
							</xsl:if>
							<!-- *********************************OTHER DATA ************************************** 	-->
							<!-- *********************************NOTES ************************************** 	-->
							<xsl:if test="string(dwc:Notes)">
								<xsl:for-each select="dwc:Notes">
									<h3 class="background">Notes:</h3>
									<table>
										<tbody>
											<tr bgcolor="#f4f4f4" valign="top">
												<td>
													<xsl:apply-templates/>
												</td>
											</tr>
										</tbody>
									</table>
								</xsl:for-each>
							</xsl:if>
							<!-- *********************************NOTES ************************************** 	-->
							<p/>
						</div>
						<p/>
					</xsl:for-each>
					<!--      **************************************UNIT*********************************************  -->
		
	</xsl:template>
</xsl:stylesheet>