<?xml version="1.0" encoding="UTF-8" standalone="no"?>
  <!DOCTYPE helpset PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN" "http://java.sun.com/products/javahelp/helpset_1_0.dtd">
  <helpset version="1.0">
    <title>Plantlore Help</title>
    <maps>
      <homeID>top</homeID>
      <mapref location="en/jhelpmap.jhm"/>
    </maps>
    <view>
      <name>TOC</name>
      <label>Table Of Contents</label>
      <type>javax.help.TOCView</type>
      <data>en/jhelptoc.xml</data>
    </view>
    <view>
      <name>Index</name>
      <label>Index</label>
      <type>javax.help.IndexView</type>
      <data>en/jhelpidx.xml</data>
    </view>
    <view>
      <name>Search</name>
      <label>Search</label>
      <type>javax.help.SearchView</type>
      <data engine="com.sun.java.help.search.DefaultSearchEngine">JavaHelpSearch</data>
    </view>
    <presentation default=true>
       <name>main window</name>
       <title>Plantlore Help</title>
       <location x="200" y="200" />
       <toolbar>
           <helpaction>javax.help.BackAction</helpaction>
           <helpaction>javax.help.ForwardAction</helpaction>
           <helpaction image="homeicon">javax.help.HomeAction</helpaction>
       </toolbar>
   </presentation>
  </helpset>
