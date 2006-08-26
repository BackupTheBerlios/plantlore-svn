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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/07/xpath-functions" xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" xmlns:n1="http://www.tdwg.org/schemas/abcd/1.2" exclude-result-prefixes="xsl xs fn xdt n1">
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
		* This template selects from an ABCD 1.2 unit the prefered
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
							<xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/>
						</span>
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
										<span class="preferedbig">
											<xsl:value-of select="n1:TaxonIdentified/n1:NameAuthorYearString"/>
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
				<xsl:if test="string(n1:TermsOfUse)">
				<tr>
					<td>
						<strong>Terms of Use: </strong><xsl:value-of select="n1:TermsOfUse"/>
					</td>
				</tr>
				</xsl:if>
				<xsl:if test="string(n1:SpecificRestrictions)">
				<tr>
					<td>
						<strong>Specific Restrictions: </strong><xsl:value-of select="n1:SpecificRestrictions"/>
					</td>
				</tr>
				</xsl:if>
				<xsl:if test="string(n1:LegalOwner/n1:Organisation/n1:OrganisationName)">
				<tr>
					<td>
						<strong>Legal Owner: </strong><xsl:value-of select="n1:LegalOwner/n1:Organisation/n1:OrganisationName"/>
					</td>
				</tr>
				</xsl:if>
				<xsl:if test="string(n1:CopyrightDeclaration)">
				<tr>
					<td>
						<strong>Copyright Declaration: </strong><xsl:value-of select="n1:CopyrightDeclaration"/>
					</td>
				</tr>
				</xsl:if>
				<xsl:if test="string(n1:IPRDeclaration)">
				<tr>
					<td>
						<strong>IPR Declaration: </strong><xsl:value-of select="n1:IPRDeclaration"/>
					</td>
				</tr>
				</xsl:if>
				<xsl:if test="string(n1:RightsURL)">
				<tr>
					<td>
						<strong>Rights URL: </strong><xsl:value-of select="n1:RightsURL"/>
					</td>
				</tr>
				</xsl:if>
			</table>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>