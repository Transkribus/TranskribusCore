#!/bin/bash

# package-info.java is NOT generated when running this command!!
cd ~/workspace_tS/TrpCore/src/main/java
/usr/local/jdk7/bin/xjc http://www.literature.at/schemas/FAT/FAT_1.1.xsd -p org.dea.transcript.trp.core.model.beans.fat


