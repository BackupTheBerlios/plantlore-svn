<?xml version="1.0" encoding="UTF-8"?>
<!-- 	**************************************************************************
		**	Templates library													**
		**																		**
		**	Common templates used in several stylesheets for processing ABCD	**
		**	documents.															**
		**																		**
		**	Author: Javier de la Torre 											**
		**************************************************************************
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" xmlns:n1="http://www.tdwg.org/schemas/abcd/2.06" exclude-result-prefixes="xsl xs fn xdt n1">
	<!--
	** replaceCharsInString template
	** 
	** Description: function that takes three parameters:
	** StringIn: String where the replacement should take effect
	** charsIn: The characters that will replace charsOut
	** charsOut: The characters that should be replaced by charsIn
	************************************************************
-->
	<xsl:template name="replaceCharsInString">
		<xsl:param name="stringIn"/>
		<xsl:param name="charsIn"/>
		<xsl:param name="charsOut"/>
		<xsl:choose>
			<xsl:when test="contains($stringIn,$charsIn)">
				<xsl:value-of select="concat(substring-before($stringIn,$charsIn),$charsOut)"/>
				<xsl:call-template name="replaceCharsInString">
					<xsl:with-param name="stringIn" select="substring-after($stringIn,$charsIn)"/>
					<xsl:with-param name="charsIn" select="$charsIn"/>
					<xsl:with-param name="charsOut" select="$charsOut"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$stringIn"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- _________________________________________________________________________ -->
	<!--
		* preferedTaxonName template
		* 
		* This template selects from an ABCD 2.0 unit the prefered
		* identification name.
		* It takes care if there are multiple identifications, if the prefered flag is set,
		* if it is being used 1 or True to indeicate the flag, etc.
		****************************************
	  template for selecting the accepted name -->
	<xsl:template name="preferedTaxonName">
		<span class="style1">
			<xsl:for-each select="n1:Identifications/n1:Identification">
				<!--General rule for names -->
				<!-- Avoid execution if there are no Identifications-->
				<!--Differenciate if there is only one identification o more -->
				<xsl:choose>
					<xsl:when test="count(../n1:Identification) = 1">
						<!--Only one identification, is shown as non prefered -->
						<span class="preferedbig">
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
										<span class="preferedbig">
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
		</span>
	</xsl:template>
	
	<!-- _____________________________________________________________________________________  -->
	<xsl:template name="RightsTable">
		<xsl:if test="string(.)">
			<table width="100%"  border="0" cellspacing="0" cellpadding="0">
				<xsl:if test="n1:IPRDeclarations">
					<tr>
						<td>
							<span class="label">IPR Declaration: </span>
							<xsl:for-each select="n1:IPRDeclarations/n1:IPRDeclaration">
								<xsl:value-of select="n1:Text"/>
								<xsl:if test="n1:Details">
									<xsl:value-of select="n1:Details"/>
								</xsl:if>
								<xsl:if test="URI">
									<xsl:value-of select="n1:URI"/>
								</xsl:if>	
							</xsl:for-each>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="n1:Copyrights">
					<tr>
						<td>
							<span class="label">Copyright Declaration: </span>
							<xsl:for-each select="n1:Copyrights/n1:Copyright">
								<xsl:value-of select="n1:Text"/>
								<xsl:if test="n1:Details">
									<xsl:value-of select="n1:Details"/>
								</xsl:if>
								<xsl:if test="URI">
									<xsl:value-of select="n1:URI"/>
								</xsl:if>
							</xsl:for-each>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="n1:Licenses">
					<tr>
						<td>
							<span class="label">License(s): </span>
							<xsl:for-each select="n1:Licenses/n1:License">
								<xsl:value-of select="n1:Text"/>
								<xsl:if test="n1:Details">
									<xsl:value-of select="n1:Details"/>
								</xsl:if>
								<xsl:if test="URI">
									<xsl:value-of select="n1:URI"/>
								</xsl:if>
							</xsl:for-each>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="n1:TermsOfUseStatements">
					<tr>
						<td>
							<span class="label">Terms of Use: </span>
							<xsl:for-each select="n1:TermsOfUseStatements/n1:TermsOfUse">
								<xsl:value-of select="n1:Text"/>
								<xsl:if test="n1:Details">
									<xsl:value-of select="n1:Details"/>
								</xsl:if>
								<xsl:if test="URI">
									<xsl:value-of select="n1:URI"/>
								</xsl:if>
							</xsl:for-each>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="n1:Disclaimers">
					<tr>
						<td>
							<span class="label">Disclaimers: </span>
							<xsl:for-each select="n1:Disclaimers/n1:Disclaimer">
								<xsl:value-of select="n1:Text"/>
								<xsl:if test="n1:Details">
									<xsl:value-of select="n1:Details"/>
								</xsl:if>
								<xsl:if test="URI">
									<xsl:value-of select="n1:URI"/>
								</xsl:if>
							</xsl:for-each>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="n1:Acknowledgements">
					<tr>
						<td>
							<span class="label">Acknowledgement(s): </span>
							<xsl:for-each select="n1:Acknowledgements/n1:Acknowledgement">
								<xsl:value-of select="n1:Text"/>
								<xsl:if test="n1:Details">
									<xsl:value-of select="n1:Details"/>
								</xsl:if>
								<xsl:if test="URI">
									<xsl:value-of select="n1:URI"/>
								</xsl:if>
							</xsl:for-each>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="n1:Citations">
					<tr>
						<td>
							<span class="label">Citation(s): </span>
							<xsl:for-each select="n1:Citations/n1:Citation">
								<xsl:value-of select="n1:Text"/>
								<xsl:if test="n1:Details">
									<xsl:value-of select="n1:Details"/>
								</xsl:if>
								<xsl:if test="URI">
									<xsl:value-of select="n1:URI"/>
								</xsl:if>
							</xsl:for-each>
						</td>
					</tr>
				</xsl:if>
			</table>
		</xsl:if>
	</xsl:template>
	
	<!-- measurement fact atomised -->
	<xsl:template name="measurementatomised">
		<xsl:param name="label"></xsl:param>
		<xsl:if test="string(n1:MeasuredBy)">
			<span class="label"><xsl:value-of select="$label" /> MeasuredBy: </span>
			<xsl:value-of select="string(n1:MeasuredBy)"/>
			<br/>
		</xsl:if>
		<xsl:if test="string(n1:MeasurementDateTime)">
			<span class="label"><xsl:value-of select="$label" /> MeasurementDateTime: </span>
			<xsl:value-of select="string(n1:MeasurementDateTime)"/>
			<br/>
		</xsl:if>
		<xsl:if test="string(n1:Duration)">
			<span class="label"><xsl:value-of select="$label" /> Measurement Duration: </span>
			<xsl:value-of select="string(n1:Duration)"/>
			<br/>
		</xsl:if>
		<xsl:if test="string(n1:Method)">
			<span class="label"><xsl:value-of select="$label" /> Measurement Method: </span>
			<xsl:value-of select="string(n1:Method)"/>
			<br/>
		</xsl:if>
		<xsl:if test="string(n1:Parameter)">
			<span class="label"><xsl:value-of select="$label" /> Measurement Parameter: </span>
			<xsl:value-of select="string(n1:Parameter)"/>
			<br/>
		</xsl:if>
		<xsl:if test="string(n1:AppliesTo)">
			<span class="label"><xsl:value-of select="$label" /> Measurement AppliesTo: </span>
			<xsl:value-of select="string(n1:AppliesTo)"/>
			<br/>
		</xsl:if>
		<xsl:if test="string(n1:LowerValue)">
			<span class="label"><xsl:value-of select="$label" /> lower Value: </span>
			<xsl:value-of select="string(n1:LowerValue)"/>
			<br/>
		</xsl:if>
		<xsl:if test="string(n1:UpperValue)">
			<span class="label"><xsl:value-of select="$label" /> Upper Value: </span>
			<xsl:value-of select="string(n1:UpperValue)"/>
			<br/>
		</xsl:if>
		<xsl:if test="string(n1:UnitOfMeasurement)">
			<span class="label"><xsl:value-of select="$label" /> UnitOfMeasurement: </span>
			<xsl:value-of select="string(n1:UnitOfMeasurement)"/>
			<br/>
		</xsl:if>
		<xsl:if test="string(n1:Accuracy)">
			<span class="label"><xsl:value-of select="$label" /> Measurement Accuracy: </span>
			<xsl:value-of select="string(n1:Accuracy)"/>
			<br/>
		</xsl:if>
		<xsl:if test="string(n1:MeasurementOrFactReference)">
			<xsl:if test="string(n1:MeasurementOrFactReference/n1:TitleCitation)">
				<span class="label"><xsl:value-of select="$label" /> Measurement Referece TitleCitation: </span>
				<xsl:value-of select="string(n1:MeasurementOrFactReference/n1:TitleCitation)"/>
				<br/>
			</xsl:if>
			<xsl:if test="string(n1:MeasurementOrFactReference/n1:CitationDetail)">
				<span class="label"><xsl:value-of select="$label" /> Measurement Referece CitationDetail: </span>
				<xsl:value-of select="string(n1:MeasurementOrFactReference/n1:CitationDetail)"/>
				<br/>
			</xsl:if>
			<xsl:if test="string(n1:MeasurementOrFactReference/n1:URI)">
				<span class="label"><xsl:value-of select="$label" /> Measurement Referece URI: </span>
				<xsl:value-of select="string(n1:MeasurementOrFactReference/n1:URI)"/>
				<br/>
			</xsl:if>
			<xsl:if test="string(n1:IsQuantitative)">
				<span class="label"><xsl:value-of select="$label" /> IsQuantitative: </span>
				<xsl:value-of select="string(n1:IsQuantitative)"/>
				<br/>
			</xsl:if>
		</xsl:if>
		<xsl:if test="string(n1:MeasurementOrFactText)">
			<span class="label"><xsl:value-of select="$label" /> : </span>
			<xsl:value-of select="string(n1:MeasurementOrFactText)"/>
			<br/>
		</xsl:if>
		
	</xsl:template>
	
	<xsl:template name="owner">
			<xsl:for-each select=".">
				<xsl:if test="n1:Organisation">
					<span class="label">Organisation: </span>
					<xsl:value-of select="n1:Organisation/n1:Name/n1:Representation/n1:Text"/>
					<!-- language -->
					<xsl:if test="n1:Organisation/n1:Name/n1:Representation/n1:Abbreviation">
						(<xsl:value-of select="n1:Organisation/n1:Name/n1:Representation/n1:Abbreviation"/>)
					</xsl:if>
					<br></br>
					<xsl:if test="n1:Organisation/n1:OrgUnits">
						<span class="label">Organisation Unit (s): </span>
						<xsl:for-each select="n1:Organisation/n1:OrgUnits/n1:OrgUnit">
							<xsl:value-of select="."/>
							<!-- language -->
						</xsl:for-each>
						<br></br>
					</xsl:if>
				</xsl:if>
				<xsl:if test="n1:Person">
					<span class="label">Person(s): </span>
					<xsl:value-of select="n1:Person/n1:FullName"/><br></br>
					<xsl:if test="n1:Person/n1:SortinName">
						<xsl:value-of select="n1:Person/SortingName"/>
					</xsl:if>
					<xsl:if test="n1:Person/n1:AtomisedName">
						<xsl:value-of select="n1:Person/n1:AtomisedName/n1:InheritedName"/>
						<xsl:if test="n1:Person/n1:AtomisedName/n1:Prefix">
							<xsl:value-of select="n1:Person/n1:AtomisedName/n1:Prefix"/>
						</xsl:if>
						<xsl:if test="n1:Person/n1:AtomisedName/n1:Suffix">
							<xsl:value-of select="n1:Person/n1:AtomisedName/n1:Suffix"/>
						</xsl:if>
						<xsl:if test="n1:Person/n1:AtomisedName/n1:GivenNames">
							<xsl:value-of select="n1:Person/n1:AtomisedName/n1:GivenNames"/>
						</xsl:if>
						<xsl:if test="n1:Person/n1:AtomisedName/n1:PreferredName">
							<xsl:value-of select="n1:Person/n1:AtomisedName/n1:PreferredName"/>
						</xsl:if>
						<br></br>
					</xsl:if>
					<xsl:if test="n1:Roles">
						<span class="label">Role(s): </span>
						<xsl:for-each select="n1:Roles/n1:Role">
							<xsl:value-of select="."/>
							<!--language [optional]-->
						</xsl:for-each>
						<br></br>
					</xsl:if>
					<xsl:if test="n1:Addresses">
						<span  class="label">Address(es): </span>
						<!--language [optional]-->
						<xsl:for-each select="n1:Addresses/n1:Address">
							<xsl:choose>
								<xsl:when test="count(n1:Addresses/n1:Address) = 1">
									<!--Only one identification, is shown as non prefered -->
									<span class="preferedbig">
										<xsl:value-of select="."/>
									</span>
								</xsl:when>
								<!-- more than 1 identifications -->
								<xsl:otherwise>
									<!--Check if there is a preferedflag -->
									<xsl:choose>
										<xsl:when test="@preferred= '0' or not(@preferred)">
											<!-- Identification without prefered flag -->
											<xsl:value-of select="."/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="@preferred= '1' or @preferred='true'">
													<!--Multiple identifications, prefered one -->
													<span class="preferedbig">
														<xsl:value-of select="."/>
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
							<br></br>
						</xsl:for-each>
					</xsl:if>
					<xsl:if test="n1:TelephoneNumbers">
						<xsl:for-each select="n1:TelephoneNumbers/n1:TelephoneNumber">
							<!-- preferred -->
							<xsl:choose>
								<xsl:when test="count(../n1:TelephoneNumber) = 1">
									<!--Only one identification, is shown as non prefered -->
									<span class="preferedbig">
										<xsl:choose>
										<xsl:when test="n1:Device">
											<xsl:value-of select="n1:Device"/>:
										</xsl:when>
										<xsl:otherwise><span class="label">Phone:</span></xsl:otherwise>
										</xsl:choose>
										<xsl:value-of select="n1:Number"/>
										<xsl:if test="n1:UsageNotes">
											<!-- language -->
											| <xsl:value-of select="n1:UsageNotes"/>
										</xsl:if>
									</span>
								</xsl:when>
								<!-- more than 1 identifications -->
								<xsl:otherwise>
									<!--Check if there is a preferedflag -->
									<xsl:choose>
										<xsl:when test="@preferred= '0' or not(@preferred)">
											<!-- Identification without prefered flag -->
											<xsl:choose>
												<xsl:when test="n1:Device">
													<xsl:value-of select="n1:Device"/>:
												</xsl:when>
												<xsl:otherwise><span class="label">Phone:</span></xsl:otherwise>
											</xsl:choose>
											<xsl:value-of select="n1:Number"/>
											<xsl:if test="n1:UsageNotes">
												<!-- language -->
												<xsl:value-of select="n1:UsageNotes"/>
											</xsl:if>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="@preferred= '1' or @preferred='true'">
													<!--Multiple identifications, prefered one -->
													<span class="preferedbig">
													<xsl:choose>
														<xsl:when test="n1:Device">
															<xsl:value-of select="n1:Device"/>:
														</xsl:when>
														<xsl:otherwise><span class="label">Phone:</span></xsl:otherwise>
													</xsl:choose>
													<xsl:value-of select="n1:Number"/>
												<xsl:if test="n1:UsageNotes">
													<!-- language -->
													| <xsl:value-of select="n1:UsageNotes"/>
												</xsl:if>
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
							<br></br>
						</xsl:for-each>
					</xsl:if>
					<xsl:if test="n1:EmailAddresses">
						<xsl:for-each select="n1:EmailAddresses/n1:EmailAddress">
							<xsl:choose>
								<xsl:when test="count(../n1:EmailAddress) = 1">
									<!--Only one identification, is shown as non prefered -->
									<span class="preferedbig">
										<a href="mailto:{../n1:EmailAddress}">EMAIL</a>
									</span>
								</xsl:when>
								<!-- more than 1 identifications -->
								<xsl:otherwise>
									<!--Check if there is a preferedflag -->
									<xsl:choose>
										<xsl:when test="@preferred= '0' or not(@preferred)">
											<!-- Identification without prefered flag -->
											<a href="mailto:{../n1:EmailAddress}">EMAIL</a>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="@preferred= '1' or @preferred='true'">
													<!--Multiple identifications, prefered one -->
													<span class="preferedbig">
														<a href="mailto:{../n1:EmailAddress}">EMAIL</a>
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
					</xsl:if>
					<xsl:if test="n1:URIs">
						<xsl:for-each select="n1:URIs/n1:URL">
							<xsl:choose>
								<xsl:when test="count(../n1:URL) = 1">
									<!--Only one identification, is shown as non prefered -->
									| <span class="preferedbig">
										<a href="{../n1:URL}" target ="_blank">URL</a>
									</span>
								</xsl:when>
								<!-- more than 1 identifications -->
								<xsl:otherwise>
									<!--Check if there is a preferedflag -->
									<xsl:choose>
										<xsl:when test="@preferred= '0' or not(@preferred)">
											<!-- Identification without prefered flag -->
											| <a href="{../n1:URL}" target ="_blank">URL</a>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="@preferred= '1' or @preferred='true'">
													<!--Multiple identifications, prefered one -->
													| <span class="preferedbig">
														<a href="{../n1:URL}" target ="_blank">URL</a>
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
					</xsl:if>
				</xsl:if>
			</xsl:for-each>
	</xsl:template>

	<xsl:template name="contact">
		<xsl:choose>
			<xsl:when test="count(ancestor::node()) = 1">
				<!--Only one identification, is shown as non prefered -->
				<span class="preferedbig">
					<xsl:value-of select="n1:Name"/>
					<xsl:if test="n1:Email"> | <a href="mailto:{n1:Email}">EMAIL</a></xsl:if>
					<xsl:if test="n1:Phone"> | Phone: <xsl:value-of select="n1:Phone"/> </xsl:if>
					<xsl:if test="n1:Address"> | <xsl:value-of select="n1:Address"/> </xsl:if>
				</span>
			</xsl:when>
			<!-- more than 1 identifications -->
			<xsl:otherwise>
				<!--Check if there is a preferedflag -->
				<xsl:choose>
					<xsl:when test="not(@preferred) or @preferred= '0'">
						<!-- Identification without prefered flag -->
						<xsl:value-of select="n1:Name"/>
						<xsl:if test="n1:Email"> | <a href="mailto:{n1:Email}">EMAIL</a></xsl:if>
						<xsl:if test="n1:Phone"> | Phone: <xsl:value-of select="n1:Phone"/> </xsl:if>
						<xsl:if test="n1:Address"> | <xsl:value-of select="n1:Address"/> </xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="@preferred= '1' or @preferred='true'">
								<!--Multiple identifications, prefered one -->
								<span class="preferedbig">
									<xsl:value-of select="n1:Name"/>
									<xsl:if test="n1:Email"> | <a href="mailto:{n1:Email}">EMAIL</a> </xsl:if>
									<xsl:if test="n1:Phone"> | Phone: <xsl:value-of select="n1:Phone"/> </xsl:if>
									<xsl:if test="n1:Address"> | <xsl:value-of select="n1:Address"/> </xsl:if>
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
	</xsl:template>
	
	<xsl:template name="reference">
		<xsl:for-each select=".">
		<xsl:value-of select="n1:TitleCitation"/> 
		<xsl:if test="n1:CitationDetail">
			| <span class="label">Details: </span>
			<xsl:value-of select="n1:CitationDetail"/>
		</xsl:if>
		<xsl:if test="n1:URI">
			| <a href="{n1:URI}" target ="_blank">URL</a>
		</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="scnameatomised1">
		
	</xsl:template>
	
</xsl:stylesheet>