#!/bin/bash

JAVA_HOME=/home/ilikeblue/NetBeansJDKs/jdk-17.0.12_linux-x64_bin.tar/jdk-17.0.12 /opt/netbeans-27/java/maven/bin/mvn -Dexec.vmArgs= "-Dexec.args=${exec.vmArgs} -classpath %classpath ${exec.mainClass} ${exec.appArgs}" -Dexec.executable=/home/ilikeblue/NetBeansJDKs/jdk-17.0.12_linux-x64_bin.tar/jdk-17.0.12/bin/java -Dexec.mainClass=com.qdmh.patientsmartcardqdmh.client.app.view.MenuAdmin -Dexec.classpathScope=runtime -Dexec.appArgs= --no-transfer-progress process-classes org.codehaus.mojo:exec-maven-plugin:3.1.0:exec
