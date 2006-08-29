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
		
	<xsl:template name="DatasetOverview" match="/">
	
		 <xsl:for-each select="n1:DataSets">
			 <xsl:for-each select="n1:DataSet/n1:Metadata">
				 <xsl:variable name="DataSetId" select="position()"/>
					 <xsl:if test="n1:Description">
						<xsl:for-each select="n1:Description/n1:Representation">
							<span class="label"><xsl:value-of select="n1:Title"/><br></br></span>
								<!-- language -->
							<xsl:if test="n1:Details">
								<span class="label"><xsl:value-of select="n1:Details"/><br></br></span>
					 </xsl:if>
					<xsl:if test="n1:Coverage">
						<xsl:value-of select="n1:Coverage"/><br></br>
					</xsl:if>
					</xsl:for-each>
				</xsl:if>
				<xsl:if test="n1:IconURI">
					<img border="0" src="{n1:IconURI}" align="right" height="45"/>
				</xsl:if>
				<xsl:if test="n1:Scope">
					<xsl:if test="n1:Scope/n1:GeoecologicalTerms">
						<xsl:for-each select="n1:Scope/n1:GeoecologicalTerms/n1:GeoEcologicalTerm">
							<xsl:value-of select="n1:Scope/n1:GeoecologicalTerms/n1:GeoEcologicalTerm"/><br></br>
							<!-- language [optional] -->
						</xsl:for-each>
					</xsl:if> 
					<xsl:if test="n1:Scope/n1:TaxonomicTerms">
						<xsl:for-each select="n1:Scope/n1:TaxonomicTerms/n1:TaxonomicTerm">
							<xsl:value-of select="n1:Scope/n1:TaxonomicTerms/n1:TaxonomicTerm"/><br></br>
							<!-- language [optional] -->
						</xsl:for-each>
					</xsl:if>
				</xsl:if>
				<xsl:if test="n1:Version">
					<span class="label">Version:</span>  
					<xsl:if test="n1:Version/n1:Major">
						<xsl:value-of select="n1:Version/n1:Major"/>
					</xsl:if>
					<xsl:if test="n1:Version/n1:Minor">
						.<xsl:value-of select="n1:Version/n1:Minor"/>
					</xsl:if>
					<xsl:if test="n1:Version/n1:Modifier">
						-<xsl:value-of select="n1:Version/n1:Modifier"/><br></br>
					</xsl:if>
					<xsl:if test="n1:Version/n1:DateIssued">
						<span class="label">DateIssued : </span><xsl:value-of select="n1:Version/n1:DateIssued"/><br></br>
					</xsl:if>
				</xsl:if>
				<xsl:if test="n1:RevisionData">
					<span class="label">RevisionData:</span><br></br> 
					<xsl:if test="n1:RevisionData/n1:Creators">
						<span class="label">Creators:</span>
						<xsl:value-of select="n1:RevisionData/n1:Creators"/><br></br>
					</xsl:if>
					<xsl:if test="n1:RevisionData/n1:Contributors">
						<span class="label">Contributors:</span>
						<xsl:value-of select="n1:RevisionData/n1:Contributors"/><br></br>
					</xsl:if>
					<xsl:if test="n1:RevisionData/n1:DateCreated">
						<span class="label">DateCreated:</span><xsl:value-of select="n1:RevisionData/n1:DateCreated"/><br></br>
					</xsl:if>
					<xsl:if test="n1:RevisionData/n1:DateModified">
						<span class="label">DateModified : </span><xsl:value-of select="n1:RevisionData/n1:DateModified"/><br></br>
					</xsl:if>
				</xsl:if>
				
				<xsl:if test="n1:Owners">
					<h3 class="background">Owner(s): </h3>
					<!--<span class="label"></span>-->
				<xsl:for-each select="n1:Owners/n1:Owner">
					<xsl:call-template name="owner">
					</xsl:call-template>
				</xsl:for-each>
				</xsl:if>
		
				<br></br>
			 	<xsl:if test="../n1:TechnicalContacts or ../n1:ContentContacts">
			 		<h3 class="background">Contact(s): </h3>
					<xsl:if test="../n1:TechnicalContacts">
						
						<span class="label">Technical Contacts:</span><br/>
						<!-- <span class="style1">-->
							<xsl:for-each select="../n1:TechnicalContacts/n1:TechnicalContact">
								<br></br>
								<xsl:call-template name="contact"></xsl:call-template>
							</xsl:for-each>
						<!-- </span> -->
					</xsl:if>
				<br></br>	
				<xsl:if test="../n1:ContentContacts">
					<br></br>
					<span class="label">Content Contacts:</span><br/>
						<!-- <span class="style1"> -->
							<xsl:for-each select="../n1:ContentContacts/n1:ContentContact">
								<br></br>
								<xsl:call-template name="contact"></xsl:call-template>
							</xsl:for-each>
						<!-- </span> -->
					</xsl:if>
				<br></br>
				</xsl:if>
				<p>
					<br></br>
				Click on UnitId to get details.
				</p> 
				<!--<xsl:for-each select="../../n1:DataSet">-->
					<h3>
						<xsl:if test="../n1:Metadata/n1:Owners/n1:Owner/n1:LogoURL">
							<img border="0" src="{n1.Owner/n1:LogoURL}" align="right" height="45"/>
						</xsl:if>
						<!-- resource Identifier -->
						<a name="{$DataSetId}" class="linktarget">
							<!--<xsl:for-each select="n1:Metadata/n1:Owners/n1:Owner/n1:Organisation/n1:Name/n1:Representation/n1:Text">-->
						<!--<xsl:if test="n1:Metadata/n1:Description/n1:Representation/n1:Title">
							<xsl:value-of select="n1:Metadata/n1:Description/n1:Representation/n1:Title"/>
						</xsl:if>-->
						
						<!--<xsl:if test="n1:Metadata/n1:Owners/n1:Owner/n1:Organisation/n1:Name/n1:Representation/n1:Abbreviation"> 
							(<xsl:for-each select="n1:Metadata/n1:Owners/n1:Owner/n1:Organisation/n1:Name/n1:Representation/n1:Abbreviation">
							<xsl:apply-templates/></xsl:for-each>)
						</xsl:if>-->
						<xsl:if test="../n1:Units/n1:Unit/n1:SourceID">
							<xsl:value-of select="../n1:Units/n1:Unit/n1:SourceID"/> 
							(<xsl:value-of select="../n1:Units/n1:Unit/n1:SourceInstitutionID"/>)
						</xsl:if>
						</a>
					</h3>
					
					
						<span class="label">Last update: </span>
						<xsl:value-of select="../n1:Metadata/n1:RevisionData/n1:DateModified"/>|
					<xsl:if test="../n1:Metadata/n1:Owners/n1:Owner/n1:URIs"> 
						<a href="{../n1:Metadata/n1:Owners/n1:Owner/n1:URIs/n1:URL}" target ="_blank">URL</a>|
						</xsl:if>
					<xsl:if test="../n1:Metadata/n1:Owners/n1:Owner/n1:EmailAddresses/n1:EmailAddress"> <a href="mailto:{../n1:Metadata/n1:Owners/n1:Owner/n1:EmailAddresses/n1:EmailAddress}">EMAIL</a></xsl:if>
					<xsl:if test="../n1:Units/n1:Unit/n1:Gathering/n1:SiteCoordinateSets">
						| <a><xsl:attribute name="href">Javascript:document.form<xsl:value-of select="$DataSetId"/>.submit()</xsl:attribute>Create Map!</a>
					</xsl:if>
				<!-- Form to create a map, only if there is data on it-->
				<xsl:if test="../n1:Units/n1:Unit/n1:Gathering/n1:SiteCoordinateSets">
					<form action="http://linuxgurrl.agr.gc.ca/mapdata/itis/itisrosa.php" method="POST" target="_blank">
						<xsl:attribute name="name">form<xsl:value-of select="$DataSetId"/></xsl:attribute>
						<input><xsl:attribute name="value">%3C%3Fxml+version%3D%271.0%27+encoding%3D%27iso-8859-1%27%3F%3E%3Cresponse%3E%3Cheader%3E%3Cauthor%3E%3C%2Fauthor%3E%3Cboundingbox%3E-180%2C-90%2C180%2C90%2CWorld%3C%2Fboundingbox%3E%3Cdescription%3EThis+Generic+Point+Mapper+is+a+service+provided+by+the+Canadian+Biological+Information+Facility%3C%2Fdescription%3E%3Cifx%3E%3C%2Fifx%3E%3Clanguage%3Een%3C%2Flanguage%3E%3Cprojection%3Elatlong%3C%2Fprojection%3E%3Crecordcount%3E%3C%2Frecordcount%3E%3Ctimestamp%3E2004-11-30+11%3A10%3A58.043%3C%2Ftimestamp%3E%3Ctitle%3ESimple+access+to+ABCD+providers+-+Point+Location+Data%3C%2Ftitle%3E%3Curl%3Ehttp%3A%2F%2Fwww.cbif.gc.ca%2Fmc%2Findex_e.php%3C%2Furl%3E%3C%2Fheader%3E%3Crecords<xsl:for-each select="../n1:Units/n1:Unit/n1:Gathering/n1:SiteCoordinateSets/n1:SiteCoordinates">%3E%3Crecord<xsl:for-each select="n1:CoordinatesLatLong"><xsl:for-each select="n1:LatitudeDecimal">%3E%3Clatitude%3E<xsl:apply-templates/>%3C%2Flatitude</xsl:for-each><xsl:for-each select="n1:LongitudeDecimal">%3E%3Clongitude%3E<xsl:apply-templates/>%3C%2Flongitude</xsl:for-each></xsl:for-each>%3E%3Crecordurl%3E%3C%2Frecordurl%3E%3C%2Frecord</xsl:for-each>%3E%3C%2Frecords%3E%3C%2Fresponse%3E</xsl:attribute>
						<xsl:attribute name="type">hidden</xsl:attribute>
						<xsl:attribute name="name">xml</xsl:attribute>
						</input>
					</form>
				</xsl:if>
				<!-- /For to create a map-->
					
				<!--</xsl:for-each>-->
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<th width="125">UnitId</th>
						<th>Prefered Name(s) </th>
						<xsl:if test="../n1:Units/n1:Unit/n1:RecordBasis">
							<th>Record Basis </th>
						</xsl:if>
						<xsl:if test="../n1:Units/n1:Unit/n1:Gathering/n1:LocalityText">
							<th>Locality</th>
						</xsl:if>
						<xsl:if test="../n1:Units/n1:Unit/n1:Gathering/n1:Country">
							<th width="120">Country</th>
						</xsl:if>
					</tr>
					<xsl:for-each select="../n1:Units">
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
												<xsl:attribute name="href"><xsl:value-of select="$unitlink"/>&amp;inst=<xsl:value-of select="normalize-space(n1:SourceInstitutionID)"/>&amp;col=<xsl:value-of select="normalize-space(n1:SourceID)"/>&amp;cat=<xsl:value-of select="normalize-space(n1:UnitID)"/></xsl:attribute>
											</xsl:otherwise>
										</xsl:choose>
										
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
												<span class="style2">
													<xsl:value-of select="n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:FullScientificNameString"/>
												</span>
											</xsl:when>
											<!-- more than 1 identifications -->
											<xsl:otherwise>
												<!--Check if there is a preferedflag -->
												<xsl:choose>
													<xsl:when test="not(@PreferredFlag)">
														<!-- Identification without prefered flag -->
														<xsl:value-of select="n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:FullScientificNameString"/> |
													</xsl:when>
													<xsl:otherwise>
														<xsl:choose>
															<xsl:when test="@PreferredFlag= '1' or @PreferredFlag='true'">
																<!--Multiple identifications, prefered one -->
																<span class="style2">
																	<xsl:value-of select="n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:FullScientificNameString"/>
																</span>
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
								<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:LocalityText">
									<td>
										<xsl:if test="n1:Gathering/n1:LocalityText">
											<xsl:value-of select="n1:Gathering/n1:LocalityText"/>
										</xsl:if>
									</td>
								</xsl:if>
								<xsl:if test="../../n1:Units/n1:Unit/n1:Gathering/n1:Country">
									<td>
										<xsl:if test="n1:Gathering/n1:Country">
											<xsl:value-of select="n1:Gathering/n1:Country/n1:Name"/> <xsl:if test="string(n1:Gathering/n1:GatheringSite/n1:Country/n1:ISO2Letter)"> (<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:ISO2Letter"/>)</xsl:if> <xsl:if test="string(n1:Gathering/n1:GatheringSite/n1:Country/n1:ISO3Letter)"> 
												(<xsl:value-of select="n1:Gathering/n1:GatheringSite/n1:Country/n1:ISO3166Code"/>)</xsl:if>
										</xsl:if>
									</td>
								</xsl:if>
							</tr>
						</xsl:for-each>
					</xsl:for-each>
				</table>
				<xsl:for-each select="../n1:Metadata/n1:IPRStatements">
					<xsl:call-template name="RightsTable"/>
				</xsl:for-each>
			</xsl:for-each>
			<br></br>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
