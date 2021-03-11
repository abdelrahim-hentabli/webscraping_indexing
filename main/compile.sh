[ -d ../bin ] || mkdir ../bin
HADOOP_CLASSPATH=`hadoop classpath`
javac -cp $PATH_TO_LUCENE/core/lucene-core-8.7.0.jar:$PATH_TO_LUCENE/queryparser/lucene-queryparser-8.7.0.jar:$PATH_TO_LUCENE/analysis/common/lucene-analyzers-common-8.7.0.jar:$PATH_TO_LUCENE/demo/lucene-demo-8.7.0.jar:$HADOOP_CLASSPATH:../lib/* -d ../bin ../src/*.java
