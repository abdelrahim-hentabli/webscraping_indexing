HADOOP_CLASSPATH=`hadoop classpath`
java -cp ../bin:$HADOOP_CLASSPATH:../lib/* HadoopIndex
