#!/bin/bash
TMP=/tmp
VPC_JAR_DIR=`pwd`/target
VPC_ASMLIB_DIR=`pwd`/asmlibrary/
VPC_JAR=$VPC_JAR_DIR/vpc-emulator-1.1-SNAPSHOT.jar

echo "INFO: Vulture Platform Compute (VPC) - Emulator Wrapper script"
echo "INFO: Executes Emulator"
echo "INFO: VPC_JAR_DIR" $VPC_JAR_DIR
echo "INFO: VPC_JAR" $VPC_JAR

if [ -e $VPC_JAR ] 
then
		if [ -f "$1" ]
		then
			java -jar $VPC_JAR $1 
		else
			echo "INFO: This script requires a MIF file as an argument."
			echo "INFO: -v as an optional argument for the emulator"
		fi	
else
	echo "ERROR: $VPC_JAR not found"
fi
