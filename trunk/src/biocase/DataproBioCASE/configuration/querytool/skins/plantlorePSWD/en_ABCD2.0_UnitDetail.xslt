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
						<td align="center">             
							<h1><xsl:value-of select="n1:Identifications/n1:Identification/n1:Result/n1:TaxonIdentified/n1:ScientificName/n1:FullScientificNameString"/>
               <span style="font-size:11px;">(<xsl:value-of select="n1:UnitID"/>)</span></h1>					
						</td>
				  </tr>
				  <tr>
				     <td>
				        Podrobnější data o nálezu najdete v poskytnutém XML.<br/>
							  Další možností je vyhledávat podle standartu ABCD 1.20.
             </td>
				  </tr>
				</table>  
			</div>			
		</xsl:for-each>
		</xsl:for-each>	
		<!--      **************************************UNIT*********************************************  -->
	</xsl:template>
</xsl:stylesheet>
