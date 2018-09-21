#!/bin/bash
TMP=/tmp
TMP_FILE=$TMP/vpctemp.asm
TMP_MIF_FILE=$TMP/vpctemp.mif
TMP_DEBUGMIF_FILE=$TMP_MIF_FILE.debug
VPC_JAR_DIR=`pwd`/target
VPC_ASMLIB_DIR=`pwd`/asmlibrary/
VPC_JAR=$VPC_JAR_DIR/vpc-assembler-1.1-SNAPSHOT.jar

echo "INFO: Vulture Platform Compute (VPC) - Wrapper script"
echo "INFO: Builds the assembler file with library directory"
echo "INFO: VPC_JAR_DIR" $VPC_JAR_DIR
echo "INFO: VPC_ASMLIB_DIR" $VPC_ASMLIB_DIR
echo "INFO: VPC_JAR" $VPC_JAR

if [ -e $VPC_JAR ] 
then
	if [ -d $VPC_ASMLIB_DIR ] 
	then
		if [ -f "$1" ]
		then
			echo "INFO: Assembling file passed as argument with library files"
			cat $1 > $TMP_FILE
			for i in `ls $VPC_ASMLIB_DIR`; do
				cat $VPC_ASMLIB_DIR/$i >> $TMP_FILE
			done
			echo "INFO: Compiling program"
			java -jar $VPC_JAR $TMP_FILE > $TMP_MIF_FILE
			java -jar $VPC_JAR $TMP_FILE 
			java -jar $VPC_JAR $TMP_FILE -v > $TMP_DEBUGMIF_FILE
			echo "INFO: Completed. Please check mif file $TMP_MIF_FILE and debug file $TMP_DEBUGMIF_FILE"
		else
			echo "INFO: This script requires a file as an argument."
			echo "INFO: -v as an optional argument for the assembler"
		fi	
	else
		echo "ERROR: $VPC_ASMLIB_DIR not found"
	fi
	
else
	echo "ERROR: $VPC_JAR not found"
fi




