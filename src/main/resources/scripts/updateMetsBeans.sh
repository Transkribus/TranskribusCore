#!/bin/bash

cd ~/workspace_tS/TrpCore/src/main/java
/usr/local/jdk7/bin/xjc -extension ../resources/xsd/mets.xsd -p org.dea.transcript.trp.core.model.beans.mets -b ../resources/xsd/mets_binding.xml


