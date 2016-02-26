#!/bin/bash

#cd ~/workspace_tS/TrpCore/src/main/java
#/usr/local/jdk7/bin/xjc ../resources/xsd/pagecontent.xsd -p org.dea.transcript.trp.core.model.beans.pagecontent

# TODO: cd to src/main/jaxb folder!
# assuming you are in src/main/jaxb folder:

# IMPORTANT: add @XmlRootElement(name="PcGts") in PcGtsType.java. Otherwise this won't go via Jersey/JaxB
xjc -b ../resources/xsd/pagecontent_jaxb_binding.xml ../resources/xsd/pagecontent.xsd -p eu.transkribus.core.model.beans.pagecontent


