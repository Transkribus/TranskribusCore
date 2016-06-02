#!/bin/bash

# change to directory of script_
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
#echo $DIR
cd "$DIR"

# IMPORTANT: add @XmlRootElement(name="PcGts") in PcGtsType.java. Otherwise this won't go via Jersey/JaxB
# assuming you are in scripts folder and xjc is available in path:

# xjc -b ../xsd/pagecontent_jaxb_binding.xml ../xsd/pagecontent.xsd -p eu.transkribus.core.model.beans.pagecontent -d ../../java
xjc -b ../xsd/pagecontent_extension_jaxb_binding.xml ../xsd/pagecontent_extension.xsd -p eu.transkribus.core.model.beans.pagecontent -d ../../java


