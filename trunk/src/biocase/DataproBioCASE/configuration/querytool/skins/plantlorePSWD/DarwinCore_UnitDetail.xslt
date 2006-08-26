<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
xmlns:fn="http://www.w3.org/2004/07/xpath-functions" 
xmlns:xdt="http://www.w3.org/2004/07/xpath-datatypes" 
xmlns:dwc="http://digir.net/schema/conceptual/darwin/2003/1.0" 
xmlns:digir="http://digir.net/schema/protocol/2003/1.0" 
exclude-result-prefixes="xsl xs fn xdt dwc">
	<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/html" />
	<xsl:template match="/">

					<!--      **************************************UNIT*********************************************  -->
					<xsl:for-each select="//digir:record">
						<div id="Unit">
            <h1>						
								<span class="preferedbig">
									<xsl:value-of select="dwc:ScientificName"/>
								</span>
							</h1>
							Podrobnější data o nálezu najdete v poskytnutém XML.<br/>
							Další možností je vyhledávat podle standartu ABCD 1.20.
							<!-- *********************************NOTES ************************************** 	-->
							<p/>
						</div>
						<p/>
					</xsl:for-each>
					<!--      **************************************UNIT*********************************************  -->
		
	</xsl:template>
</xsl:stylesheet>
