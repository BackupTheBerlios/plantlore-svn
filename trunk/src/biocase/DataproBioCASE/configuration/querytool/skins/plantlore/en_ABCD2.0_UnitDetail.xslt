<?xml version="1.0" encoding="UTF-8"?>
<!-- 	**************************************************************************
		**	Unit details library for ABCD 2.0									**
		**																		**
		**	This template process the information of ABCD units. It needs to	**
		**	include the ABCD1.2_CommonTemplates.xslt file, but if it is being 	**
		**	called by the All_parts XSLT then it is already included before		**
		**	and should be mantained commented									**
		**																		**
		**	Author: Javier de la Torre 											**
		**************************************************************************
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" xmlns:n1="http://www.tdwg.org/schemas/abcd/2.06" exclude-result-prefixes="xsl xs fn xdt n1">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html"/> 
	<xsl:include href="en_ABCD2.0_CommonTemplates.xslt" />
	<xsl:include href="en_ABCD2.0_EFG.xslt" />
	
	<xsl:template name="UnitDetails" match="/">
		<hr size="1"/>
		<h3 class="background">Units details: </h3>
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
						<td><a><xsl:attribute name="name"><xsl:value-of select="$UnitID"/></xsl:attribute></a>

							<xsl:call-template name="preferedTaxonName"/>
							<br/>
							<!-- ** GUID ** -->
								<span class="label">Unit ID: </span>
								<span class="style1">
									<xsl:value-of select="n1:UnitID"/>
								</span>
							<xsl:if test="n1:UnitGUID">
								<br/>
								<span class="label">Unit GUID: </span>
								<xsl:value-of select="n1:UnitGUID"/>
							</xsl:if>
							
							<xsl:if test="n1:UnitIDNumeric">
								<br/>
								<span class="label">Unit ID Numeric: </span>
								<xsl:value-of select="n1:UnitIDNumeric"/>
							</xsl:if>
							<xsl:if test="n1:RecordBasis">
								<br/>
								<span class="label">Record Basis: </span>
								<xsl:value-of select="n1:RecordBasis"/>
							</xsl:if>
							<xsl:if test="n1:KindOfUnit">
								<br/>
								<span class="label">Kind of Unit: </span>
								<xsl:value-of select="n1:KindOfUnit"/>
							</xsl:if>
						</td>
						<td align="right">
							<span class="label">Source Institution: </span>
								<xsl:value-of select="n1:SourceID"/> (<xsl:value-of select="n1:SourceInstitutionID"/>)<br/>
							<xsl:if test="string(n1:LastEditor)">
							<span class="label"> Last Editor:</span>
								<xsl:value-of select="n1:LastEditor"/><br/>
							</xsl:if>
							<xsl:if test="n1:DateLastEdited">
							<span class="label">Last update: </span>
							<xsl:value-of select="n1:DateLastEdited"/>
							<br/>
							</xsl:if>
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
				<table width="96%" border="0">
					<tr>
						<th>Name</th>
						<xsl:if test="string(n1:Identifications/n1:Identification/n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:NameAddendum)">
							<th>Name Addendum</th>
						</xsl:if>
						<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:TaxonIdentified/n1:HigherTaxa/n1:HigherTaxon/n1:HigherTaxonName != ''">
							<th>Taxonomy</th>
						</xsl:if>
						<xsl:if test="string(n1:Identifications/n1:Identification/n1:References/n1:Reference/n1:TitleCitation)">
							<th>Reference</th>
						</xsl:if>
						<xsl:if test="string(n1:Identifications/n1:Identification/n1:Notes)">
							<th>Notes</th>
						</xsl:if>
						<xsl:if test="string(n1:Identifications/n1:Identification/n1:Result/n1:TaxonIdentified/n1:InformalNameString)">
							<th>Vernacular name(s)</th>
						</xsl:if>
					</tr>
					<xsl:for-each select="n1:Identifications/n1:Identification">
						<xsl:sort select="@PreferredFlag" data-type="text" order="descending"/>
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
										<span class="prefered"><xsl:value-of select="n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:FullScientificNameString"/></span>
									</xsl:when>
									<!-- more than 1 identifications -->
									<xsl:otherwise>
										<!--Check if there is a preferedflag -->
										<xsl:choose>
											<xsl:when test="not(@PreferredFlag)">
												<!-- Identification without prefered flag -->
												<xsl:value-of select="n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:FullScientificNameString"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:choose>
													<xsl:when test="@PreferredFlag= '1' or @PreferredFlag='true'">
														<!--Multiple identifications, prefered one -->
														<span class="prefered">
															<xsl:value-of select="n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:FullScientificNameString"/>
														</span>
													</xsl:when>
													<xsl:otherwise>
														<!--Multiple identifications, non prefered one -->
														<xsl:value-of select="n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:FullScientificNameString"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
								<!-- ***********************************************************************+NAME****************************************************** -->
								<xsl:if test="string(n1:IdentificationDate)">
									<br/>
									<span class="label">Date: </span>
									<xsl:value-of select="n1:IdentificationDate"/>
								</xsl:if>
								<xsl:if test="string(n1:Identifier)">
									<br/>
									<span class="label">Identifier: </span>
									<xsl:value-of select="n1:Identifier"/>
								</xsl:if>
							</td>
							
								<xsl:if test="string(n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:NameAddendum)">
									<td>
									<xsl:value-of select="n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:NameAddendum"/>
									</td>
								</xsl:if>
							
								<xsl:if test="n1:Result/n1:TaxonIdentified/n1:HigherTaxa/n1:HigherTaxon">
								<td>
								<xsl:for-each select="n1:Result/n1:TaxonIdentified/n1:HigherTaxa/n1:HigherTaxon">
										<xsl:if test="n1:HigherTaxonRank">
											<span class="label">
												<xsl:value-of select="n1:HigherTaxonRank"/>: 
											</span>
										</xsl:if>
										<xsl:value-of select="n1:HigherTaxonName"/>
										<br></br>
									</xsl:for-each>
								</td>
							</xsl:if>
							<xsl:if test="string(n1:References/n1:Reference/n1:TitleCitation)">
								<td>
									<xsl:for-each select="n1:References/n1:Reference">
											<xsl:value-of select="n1:TitleCitation"/>
										<br/>
										<xsl:if test="n1:CitationDetail">
											<span class="label">Details: </span>
											<xsl:value-of select="n1:CitationDetail"/>
										</xsl:if>
										<xsl:if test="n1:URI">
											| <a href="{n1:URI}" target ="_blank">URL</a>
										</xsl:if>
									</xsl:for-each>
								</td>
							</xsl:if>
							<xsl:if test="string(n1:Notes)">
								<td>
									<xsl:value-of select="n1:Notes"/>
								</td>
							</xsl:if>
							<xsl:if test="string(n1:Result/n1:TaxonIdentified/n1:InformalNameString)">
								<td>
									<xsl:value-of select="n1:Result/n1:TaxonIdentified/n1:InformalNameString"/> (<xsl:value-of select="n1:TaxonIdentified/n1:InformalNameString/@Language"/>)
								</td>
							</xsl:if>
						</tr>
					</xsl:for-each>
				</table>
				<!-- *********************************IDENTIFICATIONS ************************************** 	-->
				<!-- ********************************* Extension ************************************** 	-->
				<xsl:if test="n1:Identifications/n1:Identification/n1:Result/n1:Extension">
					<xsl:call-template name="efgExtension" />	
				</xsl:if>
				<!-- *********************************Extension ************************************** 	-->
				<!-- ********************************** Identifiers Details ******************************** -->
				<xsl:if test="n1:Identifications/n1:Identification/n1:Identifiers">
					<h3 class="background">Unit Identifier Details: </h3>
					<xsl:for-each select="n1:Identifications/n1:Identification/n1:Identifiers/n1:Identifier">
						<xsl:if test="n1:Person">
							<xsl:if test="n1:Person/n1:FullName">
								<span class="label">Identifier Name: </span>
								<xsl:value-of select="n1:Person/n1:FullName"/>
							</xsl:if>
							<br></br>
						</xsl:if>
						<xsl:if test="n1:Organisation">
							<xsl:if test="n1:Organisation/n1:Name">
								<span class="label">Identifier Organisation: </span>
								<xsl:value-of select="n1:Organisation/n1:Name"/>
							</xsl:if>
							<br></br>
						</xsl:if>
						<xsl:if test="n1:IdentifiersText">
							<span class="label">Text: </span>
							<xsl:value-of select="n1:IdentifiersText"/>
							<br></br>
						</xsl:if>
						<xsl:if test="n1:IdentificationSource">
							<span class="label">Identification Reference: </span>
							<xsl:for-each select="n1:IdentificationSource">
								<xsl:call-template name="reference">
								</xsl:call-template>
							</xsl:for-each>
							<br></br>
						</xsl:if>
						<xsl:if test="n1:IdentifierRole">
							<span class="label">Role: </span>
							<xsl:value-of select="n1:IdentifierRole"/>
							<br></br>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
				<!-- ********************************** Identifiers Details ******************************** -->
				<!-- ****************************** Unit Refrences ************** -->
				<xsl:if test="n1:SourceReference or n1:UnitReferences">
					<h3 class="background">Unit Refrence(s): </h3>
					<xsl:if test="n1:SourceReference">
						<span style="label">Source Reference:</span>
						<xsl:for-each select="n1:SourceReference">
							<xsl:call-template name="reference">
							</xsl:call-template>
						</xsl:for-each>
						<br></br>
					</xsl:if>
					<xsl:if test="n1:UnitReferences">
						<span style="label">Unit reference(s):</span>
						<xsl:for-each select="n1:UnitReferences/n1:UnitReference">
							<xsl:call-template name="reference">
							</xsl:call-template>
						</xsl:for-each>
						<br></br>
					</xsl:if>
				</xsl:if>
				<!-- ****************************** Unit Refrences ************** -->
				<!-- ************************ Record Owner **************** -->
				<xsl:if test="n1:Owner">
					<h3 class="background">Unit Owner: </h3>
					<xsl:for-each select="n1:Owner">
						<xsl:call-template name="owner">
						</xsl:call-template>
					</xsl:for-each>
				</xsl:if>
				<!-- ************************ Record Owner **************** -->
				<!-- ************************ IPR for Unit ****************-->
				<xsl:if test="n1:IPRStatements">
					<h3 class="background">Unit Rights: </h3>
					<xsl:for-each select="n1:IPRStatements">
						<xsl:call-template name="RightsTable"/>
					</xsl:for-each>
				</xsl:if>
				<!-- ************************ IPR for Unit ****************-->
				<!-- ************************ Unit Content Contacts ****************-->
				<xsl:if test="n1:UnitContentContacts">
					<h3 class="background">Unit Content Contact(s): </h3>
					<xsl:for-each select="n1:UnitContentContacts/n1:UnitContentContact">
						<xsl:call-template name="owner">
						</xsl:call-template>
					</xsl:for-each>
				</xsl:if>
				<!-- ************************ Unit Content Contacts ****************-->
				
				<!-- *********************************GATHERING ************************************** 	-->
				<xsl:if test="string(n1:Gathering)">
					<h3 class="background">Gathering:</h3>
					<table width="96%">
						<tr>
							<xsl:if test="string(n1:Gathering/n1:Code)">
								<th>Code:
								<xsl:value-of select="n1:Gathering/n1:Code"/>
								</th>
							</xsl:if>
							<xsl:if test="string(n1:Gathering/n1:Method)">
								<th>Method:
									<xsl:value-of select="n1:Gathering/n1:Method"/>
								</th>
							</xsl:if>
						</tr>
						<tr>
							<xsl:if test="string(n1:Gathering/n1:LocalityText)">
								<th>Locality:</th>
							</xsl:if>
							<xsl:if test="string(n1:Gathering/n1:Agents)">
								<th>Collector(s):</th>
							</xsl:if>
							<th>Other info:</th>
						</tr>
						<tr valign="top">
							<xsl:if test="string(n1:Gathering)">
							<td>
								<xsl:if test="string(n1:Gathering/n1:Country)">
									<span class="label">Country: </span>
										<xsl:value-of select="n1:Gathering/n1:Country/n1:Name"/> 
										<xsl:if test="string(n1:Gathering/n1:Country/n1:ISO3166Code)"> 
										(<xsl:value-of select="n1:Gathering/n1:Country/n1:ISO3166Code"/>)
									</xsl:if> 
									<br/>
								</xsl:if>
								<xsl:if test="string(n1:Gathering/n1:NamedAreas)">
									<xsl:for-each select="n1:Gathering/n1:NamedAreas/n1:NamedArea">
										<xsl:choose>
											<xsl:when test="n1:NamedAreaClass">
												<span class="label"><xsl:value-of select="n1:NamedAreaClass"/>: </span>
											</xsl:when>
											<xsl:otherwise><span class="label">Region: </span></xsl:otherwise>
										</xsl:choose>
										<xsl:value-of select="n1:AreaName"/>
										<xsl:if test="n1:AreaCodeStandard">
											<xsl:value-of select="n1:AreaCodeStandard"/>
										</xsl:if>
										<xsl:if test="n1:AreaCode">
											<xsl:value-of select="n1:AreaCode"/>
										</xsl:if>
										<xsl:if test="n1:DataSource">
											<xsl:value-of select="n1:DataSource"/>
										</xsl:if>
										<br/>
									</xsl:for-each>
								</xsl:if>
								<xsl:if test="string(n1:Gathering/n1:LocalityText)">
									<span class="label">Locality: </span>
									<xsl:value-of select="n1:Gathering/n1:LocalityText"/>
								</xsl:if>
								<xsl:if test="n1:Gathering/n1:SiteCoordinateSets">
									<br/>
									<form action="http://linuxgurrl.agr.gc.ca/mapdata/itis/itisrosa.php" method="POST" target="_blank" name ="form{$UnitID}">
												<input>
													<xsl:attribute name="value">%3C%3Fxml+version%3D%271.0%27+encoding%3D%27iso-8859-1%27%3F%3E%3Cresponse%3E%3Cheader%3E%3Cauthor%3E%3C%2Fauthor%3E%3Cboundingbox%3E-180%2C-90%2C180%2C90%2CWorld%3C%2Fboundingbox%3E%3Cdescription%3EThis+Generic+Point+Mapper+is+a+service+provided+by+the+Canadian+Biological+Information+Facility%3C%2Fdescription%3E%3Cifx%3E%3C%2Fifx%3E%3Clanguage%3Een%3C%2Flanguage%3E%3Cprojection%3Elatlong%3C%2Fprojection%3E%3Crecordcount%3E%3C%2Frecordcount%3E%3Ctimestamp%3E2004-11-30+11%3A10%3A58.043%3C%2Ftimestamp%3E%3Ctitle%3ESimple+access+to+ABCD+providers+-+Point+Location+Data%3C%2Ftitle%3E%3Curl%3Ehttp%3A%2F%2Fwww.cbif.gc.ca%2Fmc%2Findex_e.php%3C%2Furl%3E%3C%2Fheader%3E%3Crecords<xsl:for-each select="n1:Gathering/n1:SiteCoordinateSets/n1:SiteCoordinates">%3E%3Crecord<xsl:for-each select="n1:CoordinatesLatLong"><xsl:for-each select="n1:LatitudeDecimal">%3E%3Clatitude%3E<xsl:apply-templates/>%3C%2Flatitude</xsl:for-each><xsl:for-each select="n1:LongitudeDecimal">%3E%3Clongitude%3E<xsl:apply-templates/>%3C%2Flongitude</xsl:for-each></xsl:for-each>%3E%3Crecordurl%3E%3C%2Frecordurl%3E%3C%2Frecord</xsl:for-each>%3E%3C%2Frecords%3E%3C%2Fresponse%3E</xsl:attribute>
													<xsl:attribute name="type">hidden</xsl:attribute>
													<xsl:attribute name="name">xml</xsl:attribute>
												</input>
												Coordinates: <xsl:value-of select="n1:Gathering/n1:SiteCoordinateSets/n1:SiteCoordinates/n1:CoordinatesLatLong/n1:LatitudeDecimal"/> | <xsl:value-of select="n1:Gathering/n1:SiteCoordinateSets/n1:SiteCoordinates/n1:CoordinatesLatLong/n1:LongitudeDecimal"/> <a href="Javascript:document.form{$UnitID}.submit();"> Map!</a>
									</form>
								</xsl:if>
										<!-- /For to create a map-->

							</td>
							</xsl:if>
							<xsl:if test="n1:Gathering/n1:Agents">
								<td>
									<xsl:for-each select="n1:Gathering/n1:Agents/n1:GatheringAgent">
										<xsl:apply-templates/>
										<br/>
									</xsl:for-each>
								</td>
							</xsl:if>
							<td>
								<xsl:if test="string(n1:Gathering/n1:Altitude)">
									<xsl:if test="string(n1:Gathering/n1:Altitude/n1:MeasurementOrFactAtomised)">
										<xsl:for-each select="n1:Gathering/n1:Altitude/n1:MeasurementOrFactAtomised">
											<xsl:call-template name="measurementatomised">
												<xsl:with-param name="label">
													Altitude
												</xsl:with-param>
											</xsl:call-template>
										</xsl:for-each>
										</xsl:if>
										
									</xsl:if>	
								  <xsl:if test="string(n1:Gathering/n1:Depth)">
										<xsl:if test="string(n1:Gathering/n1:Depth/n1:MeasurementOrFactAtomised)">
											<xsl:for-each select="n1:Gathering/n1:Depth/n1:MeasurementOrFactAtomised">
												<xsl:call-template name="measurementatomised">
													<xsl:with-param name="label">
														Depth
													</xsl:with-param>
												</xsl:call-template>
											</xsl:for-each>
										</xsl:if>
									</xsl:if>
								<xsl:if test="string(n1:Gathering/n1:Height)">
									<xsl:if test="string(n1:Gathering/n1:Height/n1:MeasurementOrFactAtomised)">
										<xsl:for-each select="n1:Gathering/n1:Height/n1:MeasurementOrFactAtomised">
											<xsl:call-template name="measurementatomised">
												<xsl:with-param name="label">
													Height
												</xsl:with-param>
											</xsl:call-template>
										</xsl:for-each>
									</xsl:if>
								</xsl:if>
								<xsl:if test="string(n1:Gathering/n1:SiteMeasurementsOrFacts)">
									<xsl:for-each select="n1:Gathering/n1:SiteMeasurementsOrFacts/n1:SiteMeasurementOrFact">
									<xsl:if test="string(n1:MeasurementOrFactAtomised)">
										<xsl:for-each select="n1:MeasurementOrFactAtomised">
											<xsl:call-template name="measurementatomised">
												<xsl:with-param name="label">
													Site Measurements
												</xsl:with-param>
											</xsl:call-template>
										</xsl:for-each>
									</xsl:if>
									</xsl:for-each>
								</xsl:if>	
								
								<xsl:if test="string(n1:Gathering/n1:Biotope/n1:Text)">
									<span class="label">Biotope:</span>
									<xsl:value-of select="n1:Gathering/n1:Biotope/n1:Text"/><br/>
								</xsl:if>
								<xsl:if test="string(n1:Gathering/n1:DateTime)">
									<span class="label">Date: </span>
									<xsl:value-of select="n1:Gathering/n1:DateTime"/><br/>
								</xsl:if>
								<xsl:if test="string(n1:Gathering/n1:Project/n1:ProjectTitle)">
									<span class="label">Project: </span>
									<xsl:value-of select="n1:Gathering/n1:Project/n1:ProjectTitle"/>
								</xsl:if>

							</td>
						</tr>
					</table>
				</xsl:if>
				<!-- *********************************GATHERING ************************************** 	-->
				
				<xsl:if test="string(n1:Gathering/n1:SiteImages)">
					<h3 class="background">  Gathering Digital images:</h3>
					<table border="1" cellpadding="5" cellspacing="5" width="96%">
						<tbody>
							<xsl:for-each select="n1:Gathering/n1:SiteImages/n1:SiteImage">
								<tr valign="top" >
									<td>
										<xsl:if test="n1:ID">
											<span class="label">ID: </span>
											<xsl:value-of select="n1:ID"/>
											<xsl:text>   </xsl:text>
										</xsl:if>
											
										<xsl:if test="n1:FileURI">
											<xsl:choose>
												<xsl:when test="substring(n1:FileURI,1,7)='http://' or substring(.,1,8)='https://'">
													<a href="{n1:FileURI}" target ="_blank"><xsl:value-of select="n1:FileURI"/></a>
															<!--
															<a target="_blank">
																<xsl:attribute name="href">
																	<xsl:value-of select="n1:FileURI"/>
																</xsl:attribute>
															</a>
															-->
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="n1:FileURI" disable-output-escaping="yes"/>
													</xsl:otherwise>
												</xsl:choose>
										</xsl:if>
										
										<xsl:if test="string(n1:ProductURI)">
											<br></br>
											<span class="label">ProductURI: </span>
											<xsl:value-of select="n1:ProductURI"/>
										</xsl:if>
										<xsl:if test="string(n1:Context)">
											<br></br>
											<span class="label">Context: </span>
											<xsl:value-of select="n1:Context"/>
										</xsl:if>
										<xsl:if test="string(n1:Format)">
											<br></br>
											<span class="label">Format: </span>
											<xsl:value-of select="n1:Format"/>
										</xsl:if>
										<xsl:if test="string(n1:ImageSize/n1:Width)">
											<xsl:text>   </xsl:text>
											<span class="label">Size: </span>
											<xsl:value-of select="n1:ImageSize/n1:Width"/>
											<xsl:text>x</xsl:text>
											<xsl:value-of select="n1:ImageSize/n1:Height"/>
										</xsl:if>
										<xsl:if test="string(n1:ImageResolution)">
											<xsl:text>   </xsl:text>
											<span class="label">Resolution: </span>
											<xsl:value-of select="n1:ImageResolution"/>
										</xsl:if>
										<xsl:if test="string(n1:FileSize)">
											<xsl:text>   </xsl:text>
											<span class="label">File Size: </span>
											<xsl:value-of select="n1:FileSize"/>
										</xsl:if>
										<!--</ul>-->
								</td>
								</tr>
								</xsl:for-each>
								
							
								<xsl:for-each select="n1:Gathering/n1:SiteImages/n1:SiteImage/n1:IPR">
									<xsl:call-template name="RightsTable"/>
								</xsl:for-each>
								
							</tbody>
						</table>	
						
				</xsl:if>
				
				
				<!-- *********************************DIGITAL IMAGES ************************************** 	-->
				<!-- 
				<xsl:if test="n1:UnitDigitalImages/n1:UnitDigitalImage/n1:ImageURI">
					<xsl:for-each select="n1:UnitDigitalImages">
						<h3 class="background">  Digital images:</h3>
						<table border="1" cellpadding="5" cellspacing="5" width="96%">
							<tbody>
								<tr valign="top" >
									<td>
										<ul>
											<xsl:for-each select="n1:UnitDigitalImage/n1:ImageURI">
												<li>
					<xsl:choose>
					<xsl:when test="substring(.,1,7)='http://' or substring(.,1,8)='https://'">
					<a target="_blank">
					<xsl:attribute name="href">
					<xsl:value-of select="."/>
					</xsl:attribute>
					<xsl:apply-templates/>
					</a>
					</xsl:when>
					<xsl:otherwise>
					<xsl:value-of select="." disable-output-escaping="yes"/>
					</xsl:otherwise>
					</xsl:choose>
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
				-->
				<!-- *********************************DIGITAL IMAGES ************************************** 	-->
				<!-- *********************************FACTS ************************************** 	-->
				<!-- 		
					<xsl:if test="n1:UnitFacts/n1:FactText">						
					<xsl:for-each select="n1:UnitFacts">
					<h3 class="background">Facts:</h3>
					<table>
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
					<xsl:if test="string(n1:MeasurementsOrFacts)">
					<xsl:for-each select="n1:MeasurementsOrFacts/n1:MeasurementOrFact">
					<xsl:if test="string(n1:MeasurementOrFactAtomised)">
					<xsl:for-each select="n1:MeasurementOrFactAtomised">
					<xsl:call-template name="measurementatomised">
					<xsl:with-param name="label">
					Unit Fact
					</xsl:with-param>
					</xsl:call-template>
					</xsl:for-each>
					</xsl:if>
					</xsl:for-each>
					</xsl:if>
				-->
				<!-- *********************************FACTS ************************************** 	-->
				
				<!-- Aspect -->						
				<xsl:if test="string(n1:Gathering/n1:Aspect)">
					<h3 class="background">Specimen Aspect Unit Details:</h3>
					<strong>Aspect </strong>
					<xsl:for-each select="n1:Gathering/n1:Aspect">
						<xsl:if test="string(n1:Ordination)">
							<strong>Ordination: </strong>
							<xsl:value-of select="n1:Ordination"/>
							<xsl:text> </xsl:text>
						</xsl:if>
						<xsl:if test="string(n1:CompassBearing)">
							<strong>Compass bearing: </strong>
							<xsl:value-of select="n1:CompassBearing"/>
							<xsl:text> </xsl:text>
						</xsl:if>
						<xsl:if test="string(n1:Accuracy)">
							<strong>Accuracy: </strong>
							<xsl:value-of select="n1:Accuracy"/>
							<xsl:text> </xsl:text>										
						</xsl:if>
						<!-- freetext description (AspectText) should probably rather be 
							included as an alternative to the above!
						-->		
						<xsl:if test="string(n1:AspectText)">
							<strong>Description: </strong>
							<xsl:value-of select="n1:AspectText"/>
						</xsl:if>
					</xsl:for-each>	
					<br/>
				</xsl:if>
				<!-- /Aspect -->
				
				<!-- *********************************Specimen Details ************************************** 	-->
				<xsl:if test="string(n1:SpecimenUnit)">
					<xsl:for-each select="n1:SpecimenUnit">
					<div>
						<h3 class="background">Specimen Unit Details:</h3>
						<table width="96%" border="0">
							<tr>
								<xsl:if test="n1:Acquisition">
									<th>Acquisition date</th>
								</xsl:if>
								
								<xsl:if test="n1:Acquisition/n1:AcquisitionType">
									<th>Acquisition Type</th>
								</xsl:if>
								<xsl:if test="n1:Accessions/n1:AccessionNumber">
									<th>Accession Number</th>
								</xsl:if>
								<xsl:if test="n1:Accessions/n1:AccessionCatalogue">
									<th>Accession Catalogue</th>
								</xsl:if>
								
							</tr>
							<tr>
								<td>
									<xsl:value-of select="n1:Acquisition/n1:AcquisitionDate"/>
								</td>
								<xsl:if test="n1:Acquisition/n1:AcquisitionType">
									<td>
										<xsl:value-of select="n1:Acquisition/n1:AcquisitionType"/>
									</td>
								</xsl:if>
								<td>
									<xsl:for-each select="n1:Accessions/n1:AccessionNumber">
										<xsl:value-of select="."/><br></br>
									</xsl:for-each>
								</td>
								<xsl:if test="n1:Accessions/n1:AccessionCatalogue">
									<td>
										<xsl:for-each select="n1:Accessions/n1:AccessionCatalogue">
											<xsl:value-of select="."/><br></br>
										</xsl:for-each>
									</td>
								</xsl:if>
							</tr>
						</table>
						<xsl:if test="n1:Owner">
							<span class="label">Owner:</span>
							<xsl:for-each select="n1:Owner">
								<xsl:call-template name="owner"></xsl:call-template>
							</xsl:for-each>
						</xsl:if>
					</div>
				</xsl:for-each>
				</xsl:if>
				<!-- *********************************Specimen Details ************************************** 	-->
				<xsl:if test="n1:UnitExtension">
					<xsl:for-each select="n1:UnitExtension">
					<xsl:call-template name="efgExtension_Unit" />
					</xsl:for-each>	
				</xsl:if>
				<!-- ********************************* PlantGeneticResourcesUnit************************************** 	-->
				<p>
					
					<xsl:for-each select="n1:PlantGeneticResourcesUnit">
					<h3 class="background">Plant Genetic Resources Unit Details:</h3>
					<!--
					AncestralData 0/1 cmpl-type [StringL]
					language [optional]
					-->
					<span class="label">Plant Genetic Resources Unit Details</span>
					<table width="96%" border="0">
						<xsl:if test="n1:NationalInventoryCode">
							<tr>
								<td>
								<span class="label">NationalInventoryCode: </span>
								<xsl:value-of select="n1:NationalInventoryCode"/><br></br>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:BreedingInstitutionCode">
							<tr>
								<td>
									<span class="label">Breeding Institution Code: </span>
									<xsl:value-of select="n1:BreedingInstitutionCode"/><br></br>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:BreedingCountryCode">
							<tr>
								<td>
									<span class="label">Breeding Country Code: </span>
									<xsl:value-of select="n1:BreedingCountryCode"/><br></br>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:BiologicalStatus">
							<tr>
								<td>
									<span class="label">BiologicalStatus: </span>
									<xsl:value-of select="n1:BiologicalStatus"/><br></br>
								</td>
								
							</tr>
						</xsl:if>
						<xsl:if test="n1:CollectingAcquisitionSource">
							<tr>
								<td>
									<span class="label">CollectingAcquisitionSource: </span>
									<xsl:value-of select="n1:CollectingAcquisitionSource"/><br></br>
								</td>
							</tr>
						</xsl:if>
						
						<xsl:if test="n1:OtherIdentification">
							<tr>
								<td>
									<span class="label">Other Identification: </span>
									<xsl:value-of select="n1:OtherIdentification"/><br></br>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:LocationSafetyDuplicates">
							<tr>
								<td>
									<span class="label">Location Safety Duplicates: </span>
									<xsl:value-of select="n1:LocationSafetyDuplicates"/><br></br>
								</td>
							</tr>
						</xsl:if>
						
						<xsl:if test="n1:TypeGermplasmStorage">
							<tr>
								<td>
									<span class="label">Type Germplasm Storage: </span>
									<xsl:value-of select="n1:TypeGermplasmStorage"/><br></br>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:DecodedBreedingInstitute">
							<tr>
								<td>
									<span class="label">Decoded Breeding Institute: </span>
									<xsl:value-of select="n1:DecodedBreedingInstitute"/><br></br>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:DecodedDonorInstitute">
							<tr>
								<td>
									<span class="label">Decoded Donor Institute: </span>
									<xsl:value-of select="n1:DecodedBreedingInstitute"/><br></br>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:DecodedLocationSafetyDuplicates">
							<tr>
								<td>
									<span class="label">Decoded Location Safety Duplicates: </span>
									<xsl:value-of select="n1:DecodedLocationSafetyDuplicates"/><br></br>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:GatheringInstitutionCode">
							<tr>
								<td>
									<span class="label">Gathering Institution Code: </span>
									<xsl:value-of select="n1:GatheringInstitutionCode"/><br></br>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:AccessionNames">
							<tr>
								<td>
									<span class="label">AccessionNames:</span>
									<xsl:for-each select="n1:AccessionNames/n1:AccessionNameText">
										<xsl:value-of select="."/>
									</xsl:for-each>
								</td>
							</tr>
						</xsl:if>	
						
						
					</table>
				</xsl:for-each>
				</p>
				<!-- *********************************PlantGeneticResourcesUnit ************************************** 	-->
				<!-- ******************************** UNIT MEASUREMENTS ********************  -->
				<xsl:for-each select="n1:UnitMeasurements">
					<xsl:for-each select="n1:UnitMeasurement">
						<table>
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
					<table>
						<tr>
							<xsl:if test="n1:BotanicalGardenUnit/n1:AccessionSpecimenNumbers">
								<th>AccessionSpecimenNumbers</th>
							</xsl:if>
							<xsl:if test="n1:BotanicalGardenUnit/n1:AccessionStatus">
								<th>AccessionStatus</th>
							</xsl:if>
							<xsl:if test="n1:BotanicalGardenUnit/n1:LocationInGarden">
								<th>LocationInGarden</th>
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
							<!--
								Cultivation 0/1 smpl-type [String]
								PlantingDate 0/1
								Propagation 0/1 smpl-type [String]
								Perennation 0/1
								BreedingSystem 0/1
								IPEN 0/1 smpl-type [String]-->
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
								<xsl:for-each select="n1:LocationInGarden">
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
				<!-- ***********************************************************************-->
				<!--
				ZoologicalUnit 0/1 cmpl-type [ZoologicalUnit]
				PhasesOrStages 0/1
				PhaseOrStage 1/? cmpl-type [StringL]
				language [optional]
				--> 
				<!-- ***********************************************************************-->
				
				<!-- *********************************OTHER DATA ************************************** 	-->
				<xsl:if test="n1:LastEditor or n1:DateLastEdited or n1:CollectorsFieldNumber or n1:SpecimenUnit/n1:NomenclaturalTypeDesignations/n1:NomenclaturalTypeDesignation/n1:TypeStatus">
					<h3 class="background">Other data:</h3>
					<table>
						<tbody>
							<tr valign="top">
								<td>
									<!-- Last editor -->
									<xsl:for-each select="n1:LastEditor">
										<span class="label">Last editor:</span>
										<xsl:value-of select="."/>
										<br></br>
										<!-- <xsl:apply-templates/>-->
									</xsl:for-each>
									<!-- Last editor -->
									<xsl:for-each select="n1:DateLastEdited">
										<span class="label">Date Last Edited: </span>
										<xsl:value-of select="."/>
										<br></br>
										<!-- <xsl:apply-templates/>-->
									</xsl:for-each>
									<xsl:for-each select="n1:CollectorsFieldNumber">
										<span class="label">Collectors field number:</span>
										<xsl:value-of select="."/>
										<br></br>
										<!--<xsl:apply-templates/>-->
									</xsl:for-each>
									<xsl:for-each select="n1:SpecimenUnit/n1:NomenclaturalTypeDesignations/n1:NomenclaturalTypeDesignation/n1:TypeStatus">
										<span class="label">Type Status: </span>
										<xsl:value-of select="."/>
										<br></br>
										<!--<xsl:apply-templates/>-->
									</xsl:for-each>
									<xsl:if test="string(n1:RecordURI)">
										<span class="label">Record URI: </span>
										 <!--<xsl:value-of select="n1:RecordURI"/>-->
										 <a href="{n1:RecordURI}" target ="_blank">URL</a>
										<br>
										</br>
									</xsl:if>
									<xsl:if test="string(n1:Age)">
										<span class="label">Age:</span>
										<xsl:value-of select="n1:Age"/>
										<br></br>
									</xsl:if>
									<xsl:if test="string(n1:Sex)">
										<span class="label">Sex:</span>
										<xsl:value-of select="n1:Sex"/>
										<br></br>
									</xsl:if>
									
									<xsl:if test="string(n1:Notes)">
										<span class="label">Notes:</span>
										<xsl:value-of select="n1:Notes"/>
										<br></br>
									</xsl:if>
								</td>
							</tr>
						</tbody>
					</table>
				</xsl:if>
				<!-- *********************************OTHER DATA ************************************** 	-->
				<!-- ********************************* IPR Specific for the Unit ************************************** 	-->
				<xsl:for-each select="n1:RecordRights">
					<h3 class="background">Specific Unit rights:</h3>
					<xsl:call-template name="RightsTable"/>
				</xsl:for-each>
				<!-- *********************************IPR Specific for the Unit************************************** 	-->
				<!-- ********************************* UnitStatements ************************************** 	-->
				<xsl:for-each select="n1:UnitStatements">
					<h3 class="background">Unit statements:</h3>
					<table>
						<tbody>
							<tr valign="top">
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
							<tr valign="top">
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
		</xsl:for-each>	
		<!--      **************************************UNIT*********************************************  -->
	</xsl:template>
</xsl:stylesheet>