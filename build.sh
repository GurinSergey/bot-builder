#!/usr/bin/env bash

pn=$(basename $(pwd));
mvnlog='compile.log';

mvn clean verify > ${mvnlog};
if grep -q "ERROR" "$mvnlog"; then
    echo -e "\e[31m [VERIFY ERROR]\n  Look at "$mvnlog" for more \e[0m";
    exit 1;
fi

if [ -f "$pn.zip" ]; then

rm -f "$pn.zip";
fi

mkdir ${pn};

mvn clean package -Djavacpp.platform=linux-x86_64> ${mvnlog};

if grep -q "ERROR" "$mvnlog"; then
    echo -e "\e[31m  [BUILD ERROR]\n  Look at "$mvnlog" for more \e[0m";
    exit 1;
else
#    echo -e "\e[32m  [BUILD SUCCESS]\n \e[0m";
    cowsay BUILD SUCCESS
    rm -f ${mvnlog};
fi


chmod +x 'target/'${pn}'.jar';

cp 'target/'${pn}'.jar' $pn/$pn'.jar';

zip -r "$pn.zip" "$pn" >> /dev/null

if [ -d ${pn} ]; then # if exist dir
    rm -fr current-build;
    mv ${pn} current-build;
fi