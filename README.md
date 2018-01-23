# ranger-gaian-plugin


1. clone the repo

2. cd to git dir

3. mvn clean install

4. copy all policy folder to Gaian Node an under Gaian dir

On Gaian Node:

1. add jar files and conf in classpath:
add these two lines in launchGaianServer.sh

export CLASSPATH="$CLASSPATH:/root/gaiandb/gaiandb/policy/*"
export CLASSPATH="$CLASSPATH:/root/gaiandb/gaiandb/policy/conf/"

2. configure Gaian to use rangerGaianresultFilter
add this line in the end of gaiandb_config.properties

SQL_RESULT_FILTER=org.apache.ranger.services.gaian.RangerPolicyResultFilter

3. start Gaian Server
