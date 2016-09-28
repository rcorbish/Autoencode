FROM rcorbish/openblas-jre9

WORKDIR /home/autoencode

ADD run.sh  run.sh
ADD src/main/resources  /home/autoencode/resources
ADD target/classes  /home/autoencode/classes
ADD target/dependency /home/autoencode/libs

RUN chmod 0500 run.sh ; 
ENV CP classes:resources

VOLUME [ "/home/autoencode/data" ]

ENTRYPOINT [ "sh", "/home/autoencode/run.sh" ]  
CMD [ "data" ]
