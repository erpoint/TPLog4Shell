#! /bin/bash 

md5=$(md5sum java/* | md5sum)

while :
do
    newmd5=$(md5sum java/* | md5sum)
    if [[ "$md5" != "$newmd5" ]]
    then
        echo "Je build les classes"

        rm class/*
        for file in `ls java | grep .java`; do javac java/$file -d class; done

        md5="$newmd5"
    else
        sleep 1
    fi


done

