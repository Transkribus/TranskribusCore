#!/bin/bash

#cd ~/workspace_tS/TrpCore/src/main/java
#/usr/local/jdk7/bin/xjc ../resources/xsd/pagecontent.xsd -p org.dea.transcript.trp.core.model.beans.pagecontent

# TODO: cd to src/main/jaxb folder!
# assuming you are in src/main/jaxb folder:
xjc -b ../resources/xsd/pagecontent_jaxb_binding.xml ../resources/xsd/pagecontent.xsd -p eu.transkribus.core.model.beans.pagecontent


