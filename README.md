# ranger-gaian-plugin


1. clone the repo

2. cd to git dir

3. mvn clean install

4. copy policy/ranger-gaian-plugin-1.0.0-SNAPSHOT.jar in target to policy folder.

4. copy all policy folder to Gaian Node under Gaian dir.

On Gaian Node:

1. add jar files and conf in classpath, add these two lines in launchGaianServer.sh:

  export CLASSPATH="$CLASSPATH:/root/gaiandb/gaiandb/policy/*"
  
  export CLASSPATH="$CLASSPATH:/root/gaiandb/gaiandb/policy/conf/"

2. configure Gaian to use rangerGaianresultFilter, add this line in the end of gaiandb_config.properties:

  SQL_RESULT_FILTER=org.apache.ranger.services.gaian.RangerPolicyResultFilter

3. start Gaian Server

**Deploying the Service Definition to Ranger**

This needs to be done once only (unless the service definition changes)

cd resources/service-defs

curl -u admin:admin -X POST -H "Accept: application/json" -H "Content-Type: application/json" --data @ranger-servicedef-gaian.json http://localhost:6080/service/plugins/definitions

Replace localhost with the IP /port of your Ranger Server

Once posted, the ranger access manager page should now show 'Gaian' as a selectable component

**Updating an existing service definition**

First delete any existing gaian services through the ranger UI

Then find out the id of the gaian servicedef. This would either have been returned when deployed, or
you can pull all definitions to find it ie

curl -u admin:admin localhost:6080/service/public/v2/api/servicedef/ | jsonpp | less

'jsonpp' is one way to format a json file on macOS (installed using the 'brew' environment ie brew install jsonpp)

Look for gaian & then find the id....

To delete:

curl  -u admin:admin -X DELETE -H "Accept: application/json" -H "Content-Type: application/json"  http://9.20.65.115:6080/service/public/v2/api/servicedef/102