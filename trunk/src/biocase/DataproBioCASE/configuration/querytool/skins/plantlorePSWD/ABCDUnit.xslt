<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" xmlns:n1="http://www.tdwg.org/schemas/abcd/1.2" exclude-result-prefixes="xsl xs fn xdt n1">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html" />
	<xsl:template match="/">

					<!--      **************************************UNIT*********************************************  -->
					<xsl:for-each select="n1:DataSets/n1:DataSet/n1:Units/n1:Unit">
						<div id="Unit">
							<!-- *********************************/HEADER ************************************** 	-->
							<table width="96%" border="3">
								<tr>
									<td>
										<span class="style1">
										
<!-- ***********************************************************************+NAME****************************************************** -->												
												<xsl:for-each select="n1:Identifications/n1:Identification">
											<!--General rule for names -->
											<!-- Avoid execution if there are no Identifications-->
											
												<!--Differenciate if there is only one identification o more -->
												<xsl:choose>
													<xsl:when test="count(../n1:Identification) = 1">
														<!--Only one identification, is shown as non prefered -->
														<span class="preferedbig"><xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/></span>
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
																		<span class="preferedbig"><xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/> </span>
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
										</span>
										<br/>
										<!-- ** GUID ** -->
										<a name="{normalize-space(../../n1:OriginalSource/n1:SourceInstitutionCode)}-{normalize-space(../../n1:OriginalSource/n1:SourceName)}-{normalize-space(n1:UnitID)}" class="linktarget">
											<span class="label">Unit ID: </span>
											<span class="style1">
												<xsl:value-of select="n1:UnitID"/>
											</span>
										</a>
										<xsl:if test="n1:RecordBasis">
											<br/>
											<span class="label">Record Basis: </span>
											<xsl:value-of select="n1:RecordBasis"/>
										</xsl:if>
									</td>
									<td align="right">
										<span class="label">Source Institution: </span>
										<xsl:value-of select="../../n1:OriginalSource/n1:SourceName"/> (<xsl:value-of select="../../n1:OriginalSource/n1:SourceInstitutionCode"/>)<br/>
										<span class="label">Last update: </span>
										<xsl:value-of select="../../n1:OriginalSource/n1:SourceLastUpdatedDate"/>
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
							<h3 class="background">Identification(s): </h3>
							<table width="96%" border="3">
								<tr>
									<th>Name</th>
									<xsl:if test="n1:Identifications/n1:Identification/n1:TaxonIdentified/n1:HigherTaxa/n1:HigherTaxon != ''">
										<th>Taxonomy</th>
									</xsl:if>
									<xsl:if test="string(n1:Identifications/n1:Identification/n1:IdentificationReference)">
										<th>Reference</th>
									</xsl:if>
									<xsl:if test="string(n1:IdentificationNotes)">
										<th>Notes</th>
									</xsl:if>
									<xsl:if test="string(n1:Identifications/n1:Identification/n1:TaxonIdentified/n1:InformalNameString)">
										<th>Vernacular name(s)</th>
									</xsl:if>
								</tr>
								<xsl:for-each select="n1:Identifications/n1:Identification">
								<xsl:sort select="@PreferredIdentificationFlag" data-type="text" order="descending"/>
									<!-- Change the color of the background depending if it is a PREFERED identification or not -->
									<tr>

										<td>
<!-- ***********************************************************************+NAME****************************************************** -->
											<!--General rule for names -->
											<!-- Avoid execution if there are no Identifications-->
											
												<!--Differenciate if there is only one identification o more -->
												<xsl:choose>
													<xsl:when test="count(../n1:Identification) = 1">
														<!--Only one identification, is shown as non prefered -->
														<span class="prefered"><xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/></span>
													</xsl:when>
													<!-- more than 1 identifications -->
													<xsl:otherwise>
														<!--Check if there is a preferedflag -->
														<xsl:choose>
															<xsl:when test="not(@PreferredIdentificationFlag)">
																<!-- Identification without prefered flag -->
																<xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/>
															</xsl:when>
															<xsl:otherwise>
																<xsl:choose>
																	<xsl:when test="@PreferredIdentificationFlag= '1' or @PreferredIdentificationFlag='true'">
																		<!--Multiple identifications, prefered one -->
																		<span class="prefered"><xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/></span>
																	</xsl:when>
																	<xsl:otherwise>
																		<!--Multiple identifications, non prefered one -->
																		<xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/>
																	</xsl:otherwise>
																</xsl:choose>
															</xsl:otherwise>
														</xsl:choose>	
													</xsl:otherwise>
												</xsl:choose>		
<!-- ***********************************************************************+NAME****************************************************** -->
											<xsl:if test="n1:IdentificationDate">
												<br/>
												<span class="label">Date: </span>
												<xsl:value-of select="n1:IdentificationDate"/>
											</xsl:if>
											<xsl:if test="n1:Identifier">
												<br/>
												<span class="label">Identifier: </span>
												<xsl:value-of select="n1:Identifier"/>
											</xsl:if>
										</td>
										<xsl:if test="n1:TaxonIdentified/n1:HigherTaxa/n1:HigherTaxon != ''">
											<td>
												<xsl:for-each select="n1:TaxonIdentified/n1:HigherTaxa/n1:HigherTaxon">
													<xsl:if test="@TaxonRank">
														<span class="label">
															<xsl:value-of select="@TaxonRank"/>: </span>
													</xsl:if>
													<xsl:value-of select="."/>
												</xsl:for-each>
											</td>
										</xsl:if>
										<xsl:if test="string(n1:IdentificationReference)">
											<td>
												<xsl:value-of select="n1:IdentificationReference/n1:ReferenceCitation"/>
												<br/>
												<span class="label">Details: </span>
												<xsl:value-of select="n1:IdentificationReference/n1:ReferenceDetail"/>
											</td>
										</xsl:if>
										<xsl:if test="string(n1:IdentificationNotes)">
											<td>
												<xsl:value-of select="n1:IdentificationNotes"/>
											</td>
										</xsl:if>
										<xsl:if test="string(n1:TaxonIdentified/n1:InformalNameString)">
											<td>
												<xsl:value-of select="n1:TaxonIdentified/n1:InformalNameString"/> (<xsl:value-of select="n1:TaxonIdentified/n1:InformalNameString/@Language"/>)
											</td>
										</xsl:if>
									</tr>
								</xsl:for-each>
							</table>
							<!-- *********************************IDENTIFICATIONS ************************************** 	-->
							<!-- *********************************GATHERING ************************************** 	-->
							<xsl:if test="n1:Gathering">
								<h3 class="background">Gathering:</h3>
								<table width="96%" border="3">
									<tr>
										<xsl:if test="n1:Gathering/n1:GatheringSite">
											<th>Locality:</th>
										</xsl:if>
										<xsl:if test="n1:Gathering/n1:GatheringAgents">
											<th>Collector(s):</th>
										</xsl:if>
										<th>Other info:</th>
									</tr>
									<tr valign="top">
										<td>
											<xsl:if test="n1:Gathering/n1:GatheringSite/n1:Country">
												<span class="label">Country: </span>
												<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:CountryName"/> <xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:ISO2Letter"/> <xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:ISO3Letter"/>
												<br/>
											</xsl:if>
											<xsl:if test="n1:Gathering/n1:GatheringSite/n1:NamedAreas">
												<xsl:for-each select="n1:Gathering/n1:GatheringSite/n1:NamedAreas/n1:NamedArea">
													<span class="label">
														<xsl:value-of select="n1:NamedAreaClass"/>: </span>
													<xsl:value-of select="n1:NamedAreaName"/>
													<br/>
												</xsl:for-each>
											</xsl:if>
											<xsl:if test="n1:Gathering/n1:GatheringSite/n1:LocalityText">
												<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:LocalityText"/>
											</xsl:if>
											
											<xsl:if test="n1:Gathering/n1:GatheringSite/n1:SiteCoordinateSets">
											<br/>
											<form action="http://linuxgurrl.agr.gc.ca/mapdata/itis/itisrosa.php" method="POST" target="_blank">
												<xsl:attribute name="name">GeneralMap</xsl:attribute>
												<input>
													<xsl:attribute name="value">%3C%3Fxml+version%3D%271.0%27+encoding%3D%27iso-8859-1%27%3F%3E%3Cresponse%3E%3Cheader%3E%3Cauthor%3E%3C%2Fauthor%3E%3Cboundingbox%3E-180%2C-90%2C180%2C90%2CWorld%3C%2Fboundingbox%3E%3Cdescription%3EThis+Generic+Point+Mapper+is+a+service+provided+by+the+Canadian+Biological+Information+Facility%3C%2Fdescription%3E%3Cifx%3E%3C%2Fifx%3E%3Clanguage%3Een%3C%2Flanguage%3E%3Cprojection%3Elatlong%3C%2Fprojection%3E%3Crecordcount%3E%3C%2Frecordcount%3E%3Ctimestamp%3E2004-11-30+11%3A10%3A58.043%3C%2Ftimestamp%3E%3Ctitle%3ESimple+access+to+ABCD+providers+-+Point+Location+Data%3C%2Ftitle%3E%3Curl%3Ehttp%3A%2F%2Fwww.cbif.gc.ca%2Fmc%2Findex_e.php%3C%2Furl%3E%3C%2Fheader%3E%3Crecords
													<xsl:for-each select="n1:Gathering/n1:GatheringSite/n1:SiteCoordinateSets/n1:SiteCoordinates">
														%3E%3Crecord
															<xsl:for-each select="n1:CoordinatesLatLong"><xsl:for-each select="n1:LatitudeDecimal">
																	%3E%3Clatitude%3E<xsl:apply-templates/>%3C%2Flatitude
																</xsl:for-each><xsl:for-each select="n1:LongitudeDecimal">
																	%3E%3Clongitude%3E<xsl:apply-templates/>%3C%2Flongitude
																</xsl:for-each></xsl:for-each>
															%3E%3Crecordurl%3E%3C%2Frecordurl
															%3E%3C%2Frecord
													</xsl:for-each>
												%3E%3C%2Frecords%3E%3C%2Fresponse%3E
											</xsl:attribute>
													<xsl:attribute name="type">hidden</xsl:attribute>
													<xsl:attribute name="name">xml</xsl:attribute>
												</input>
												Coordinates: <xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:SiteCoordinateSets/n1:SiteCoordinates/n1:CoordinatesLatLong/n1:LatitudeDecimal"/> | <xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:SiteCoordinateSets/n1:SiteCoordinates/n1:CoordinatesLatLong/n1:LongitudeDecimal"/> <a href="Javascript:document.GeneralMap.submit()"> Map!</a>
											</form>
										</xsl:if>
										<!-- /For to create a map-->

										</td>
										<xsl:if test="n1:Gathering/n1:GatheringAgents">
											<td>
												<xsl:for-each select="n1:Gathering/n1:GatheringAgents/n1:GatheringAgent">
													<xsl:apply-templates/>
													<br/>
												</xsl:for-each>
											</td>
										</xsl:if>
										<td>
											<xsl:if test="string(n1:Gathering/n1:GatheringSite/n1:Altitude/n1:MeasurementAtomized)">
												<xsl:for-each select="n1:Gathering/n1:GatheringSite/n1:Altitude/n1:MeasurementAtomized">
													<span class="label">Height : </span>
													<xsl:value-of select="n1:MeasurementLowerValue"/>
													<xsl:value-of select="n1:MeasurementScale"/> <xsl:if test="string(n1:MeasurementUpperValue)">- <xsl:value-of select="n1:MeasurementUpperValue"/></xsl:if>
													<xsl:value-of select="n1:MeasurementScale"/>
													<br/>
												</xsl:for-each>
											</xsl:if>
											<xsl:if test="string(n1:Gathering/n1:GatheringSite/n1:BiotopeData)">
												<span class="label">Biotope:</span>
												<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:BiotopeData"/><br/>
											</xsl:if>
											<xsl:if test="string(n1:Gathering/n1:GatheringDateTime)">
												<span class="label">Date: </span>
												<xsl:value-of select="n1:Gathering/n1:GatheringDateTime"/><br/>
											</xsl:if>
											<xsl:if test="string(n1:Gathering/n1:Project/n1:ProjectTitle)">
												<span class="label">Project: </span>
												<xsl:value-of select="n1:Gathering/n1:ProjectTitle"/>
											</xsl:if>

										</td>
									</tr>
								</table>
							</xsl:if>
							<!-- *********************************GATHERING ************************************** 	-->
							<!-- *********************************DIGITAL IMAGES ************************************** 	-->
							<xsl:if test="n1:UnitDigitalImages/n1:UnitDigitalImage/n1:ImageURI">
							<xsl:for-each select="n1:UnitDigitalImages">
								<h3 class="background">  Digital images:</h3>
								<table border="3" bordercolor="#e4e4e4" cellpadding="5" cellspacing="5" width="96%">
									<tbody>
										<tr valign="top" bgcolor="#f4f4f4">
											<td>
												<ul>
													<xsl:for-each select="n1:UnitDigitalImage/n1:ImageURI">
														<li>
															<a target="_blank">
																<xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
																<xsl:apply-templates/>
																
															</a>
														</li>
													</xsl:for-each>
												</ul>
											</td>
											<xsl:for-each select="n1:UnitDigitalImage/n1:ImageIPR">
												<td>
													<span class="label">IPR::</span>
													<xsl:apply-templates/>
												</td>
											</xsl:for-each>
										</tr>
									</tbody>
								</table>
							</xsl:for-each>
							</xsl:if>
							<!-- *********************************DIGITAL IMAGES ************************************** 	-->
							<!-- *********************************FACTS ************************************** 	-->
							<xsl:if test="n1:UnitFacts/n1:FactText">						
							<xsl:for-each select="n1:UnitFacts">
								<h3 class="background">Facts:</h3>
								<table border="3">
									<tr>
										<xsl:if test="n1:UnitFact/n1:FactType">
											<th>FactType</th>
										</xsl:if>
										<xsl:if test="n1:UnitFact/n1:FactText">
											<th>FactText</th>
										</xsl:if>
										<xsl:if test="n1:UnitFact/n1:DateEntered">
											<th>DateEntered</th>
										</xsl:if>
										<xsl:if test="n1:UnitFact/n1:AddedBy">
											<th>AddedBy</th>
										</xsl:if>
										<xsl:if test="n1:UnitFact/n1:Reference">
											<th>Reference</th>
										</xsl:if>
									</tr>
									<xsl:for-each select="n1:UnitFact">
										<tr>
											<xsl:if test="n1:FactType">
												<td>
													<xsl:for-each select="n1:FactType">
														<xsl:apply-templates/>
													</xsl:for-each>
												</td>
											</xsl:if>
											<xsl:if test="n1:FactText">
												<td>
													<xsl:for-each select="n1:FactText">
														<xsl:apply-templates/>
													</xsl:for-each>
												</td>
											</xsl:if>
											<xsl:if test="n1:DateEntered">
												<td>
													<xsl:for-each select="n1:DateEntered">
														<xsl:apply-templates/>
													</xsl:for-each>
												</td>
											</xsl:if>
											<xsl:if test="n1:AddedBy">
												<td>
													<xsl:for-each select="n1:AddedBy">
														<xsl:apply-templates/>
													</xsl:for-each>
												</td>
											</xsl:if>
											<xsl:if test="n1:Reference">
												<td>
													<xsl:for-each select="n1:Reference">
														<xsl:apply-templates/>
													</xsl:for-each>
												</td>
											</xsl:if>
										</tr>
									</xsl:for-each>
								</table>
							</xsl:for-each>
							</xsl:if>
							<!-- *********************************FACTS ************************************** 	-->
							<!-- ******************************** UNIR MEASUREMENTS ********************  -->
							<xsl:for-each select="n1:UnitMeasurements">
								<xsl:for-each select="n1:UnitMeasurement">
									<table border="3">
										<tr>
											<xsl:if test="n1:UnitMeasurements/n1:UnitMeasurement/n1:MeasurementAppliesTo">
												<th>MeasurementAppliesTo</th>
											</xsl:if>
											<xsl:if test="n1:UnitMeasurements/n1:UnitMeasurement/n1:MeasurementMethod">
												<th>MeasurementMethod</th>
											</xsl:if>
											<xsl:if test="n1:UnitMeasurements/n1:UnitMeasurement/n1:MeasurementDuration">
												<th>MeasurementDuration</th>
											</xsl:if>
											<xsl:if test="n1:UnitMeasurements/n1:UnitMeasurement/n1:MeasurementAccuracy">
												<th>MeasurementAccuracy</th>
											</xsl:if>
											<xsl:if test="n1:UnitMeasurements/n1:UnitMeasurement/n1:ParameterMeasured">
												<th>ParameterMeasured</th>
											</xsl:if>
											<xsl:if test="n1:UnitMeasurements/n1:UnitMeasurement/n1:MeasurementScale">
												<th>MeasurementScale</th>
											</xsl:if>
											<xsl:if test="n1:UnitMeasurements/n1:UnitMeasurement/n1:MeasurementLowerValue">
												<th>MeasurementLowerValue</th>
											</xsl:if>
											<xsl:if test="n1:UnitMeasurements/n1:UnitMeasurement/n1:MeasurementUpperValue">
												<th>MeasurementUpperValue</th>
											</xsl:if>
										</tr>
										<xsl:for-each select="n1:MeasurementAtomized">
											<tr>
												<xsl:for-each select="n1:MeasurementAppliesTo">
													<td>
														<xsl:apply-templates/>
													</td>
												</xsl:for-each>
												<xsl:for-each select="n1:MeasurementMethod">
													<td>
														<xsl:apply-templates/>
													</td>
												</xsl:for-each>
												<xsl:for-each select="n1:MeasurementDuration">
													<td>
														<xsl:apply-templates/>
													</td>
												</xsl:for-each>
												<xsl:for-each select="n1:MeasurementAccuracy">
													<td>
														<xsl:apply-templates/>
													</td>
												</xsl:for-each>
												<xsl:for-each select="n1:ParameterMeasured">
													<td>
														<xsl:apply-templates/>
													</td>
												</xsl:for-each>
												<xsl:for-each select="n1:MeasurementScale">
													<td>
														<xsl:apply-templates/>
													</td>
												</xsl:for-each>
												<xsl:for-each select="n1:MeasurementLowerValue">
													<td>
														<xsl:apply-templates/>
													</td>
												</xsl:for-each>
												<xsl:for-each select="n1:MeasurementUpperValue">
													<td>
														<xsl:apply-templates/>
													</td>
												</xsl:for-each>
											</tr>
										</xsl:for-each>
									</table>
								</xsl:for-each>
							</xsl:for-each>
							<!-- ******************************** UNIR MEASUREMENTS ********************  -->
							<!--********************************** UNIT COLLECTION DOMAIN/ BOTANICAL GARDEN ***************************** -->
							<xsl:for-each select="n1:UnitCollectionDomain">
								<h3 class="background">Botanical garden specific data:</h3>
								<table border="3">
									<tr>
										<xsl:if test="n1:BotanicalGardenUnit/n1:AccessionSpecimenNumbers">
											<th>AccessionSpecimenNumbers</th>
										</xsl:if>
										<xsl:if test="n1:BotanicalGardenUnit/n1:AccessionStatus">
											<th>AccessionStatus</th>
										</xsl:if>
										<xsl:if test="n1:BotanicalGardenUnit/n1:AccessionMaterialType">
											<th>AccessionMaterialType</th>
										</xsl:if>
										<xsl:if test="n1:BotanicalGardenUnit/n1:Hardiness">
											<th>Hardiness</th>
										</xsl:if>
										<xsl:if test="n1:BotanicalGardenUnit/n1:ProvenanceCategory">
											<th>ProvenanceCategory</th>
										</xsl:if>
										<xsl:if test="n1:BotanicalGardenUnit/n1:PropagationHistoryCode">
											<th>PropagationHistoryCode</th>
										</xsl:if>
										<xsl:if test="n1:BotanicalGardenUnit/n1:AccessionLineage">
											<th>AccessionLineage</th>
										</xsl:if>
										<xsl:if test="n1:BotanicalGardenUnit/n1:DonorCategory">
											<th>DonorCategory</th>
										</xsl:if>
									</tr>
									<xsl:for-each select="n1:BotanicalGardenUnit">
										<tr>
											<xsl:for-each select="n1:AccessionSpecimenNumbers">
												<td>
													<xsl:apply-templates/>
												</td>
											</xsl:for-each>
											<xsl:for-each select="n1:AccessionStatus">
												<td>
													<xsl:apply-templates/>
												</td>
											</xsl:for-each>
											<xsl:for-each select="n1:AccessionMaterialType">
												<td>
													<xsl:apply-templates/>
												</td>
											</xsl:for-each>
											<xsl:for-each select="n1:Hardiness">
												<td>
													<xsl:apply-templates/>
												</td>
											</xsl:for-each>
											<xsl:for-each select="n1:ProvenanceCategory">
												<td>
													<xsl:apply-templates/>
												</td>
											</xsl:for-each>
											<xsl:for-each select="n1:PropagationHistoryCode">
												<td>
													<xsl:apply-templates/>
												</td>
											</xsl:for-each>
											<xsl:for-each select="n1:AccessionLineage">
												<td>
													<xsl:apply-templates/>
												</td>
											</xsl:for-each>
											<xsl:for-each select="n1:DonorCategory">
												<td>
													<xsl:apply-templates/>
												</td>
											</xsl:for-each>
										</tr>
									</xsl:for-each>
								</table>
							</xsl:for-each>
							<!-- *********************************OTHER DATA ************************************** 	-->
							<xsl:if test="n1:LastEditor or n1:DateLastEdited or n1:CollectorsFieldNumber or n1:UnitStateDomain/n1:SpecimenUnit/n1:NomenclaturalTypeDesignations/n1:NomenclaturalTypeDesignation/n1:TypeStatus">
								<h3 class="background">Other data:</h3>
								<table border="3">
									<tbody>
										<tr valign="top">
											<td>
												<!-- Last editor -->
												<xsl:for-each select="n1:LastEditor">
													<span class="label">Last editor:</span>
													<xsl:apply-templates/>
												</xsl:for-each>
												<!-- Last editor -->
												<xsl:for-each select="n1:DateLastEdited">
													<span class="label">Date Last Edited:</span>
													<xsl:apply-templates/>
												</xsl:for-each>
												<xsl:for-each select="n1:CollectorsFieldNumber">
													<span class="label">Collectors field number:</span>
													<xsl:apply-templates/>
												</xsl:for-each>
												<xsl:for-each select="n1:UnitStateDomain/n1:SpecimenUnit/n1:NomenclaturalTypeDesignations/n1:NomenclaturalTypeDesignation/n1:TypeStatus">
													<br/>
													<span class="label">Type Status: </span>
													<xsl:apply-templates/>
												</xsl:for-each>
											</td>
										</tr>
									</tbody>
								</table>
							</xsl:if>
							<!-- *********************************OTHER DATA ************************************** 	-->
							<!-- ********************************* IPR Specific for the Unit ************************************** 	-->
							<xsl:for-each select="n1:RecordRights">
								<h3 class="background">Specific Unit right rights:</h3>
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
							<!-- *********************************IPR Specific for the Unit************************************** 	-->
							<!-- ********************************* UnitStatements ************************************** 	-->
							<xsl:for-each select="n1:UnitStatements">
								<h3 class="background">Unit statements:</h3>
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
							<!-- ********************************* UnitStatements************************************** 	-->
							<!-- *********************************NOTES ************************************** 	-->
							<xsl:if test="string(n1:UnitDescription)">
								<xsl:for-each select="n1:UnitDescription">
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
