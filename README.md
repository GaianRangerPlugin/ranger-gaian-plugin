# ranger-gaian-plugin

**Introduction**

This plugin provides support for Ranger policies to be implemented when access data through Gaian.
For example:

* Permit/Deny access to Gaian Resources
* Supports Data Masking [tbd]
* Ranger Tag support (including being sourced from Apache Atlas) [tbd]

**Building the plugin**

Ensure the required build requirements are installed
* Java 8 (151 or above)
* Maven 3.50 or later

Next extract the source & build:

    `git clone https://github.com/GaianRangerPlugin/ranger-gaian-plugin.git`
    `cd ranger-gaian-plugin`
    `mvn clean install`

This should produce a plugin built in the 'target' directory called ranger-gaian-plugin-1.0.0-SNAPSHOT.jar . 
This contains the plugin AND the dependent libraries.

NOTE: If you had an earlier version of this plugin you may have
dependent libraries in the policy directory. These are no longer required,
you ONLY need the jar file and the config files

**Deploying the plugin to Gaian**

First modify the 'launchGaianServer.sh' script provided by Gaian to add additional directories to the classpath, which is where
we will install the plugin. The best place to do this is just before the section of code labelled 'automatic jar discovery' :

    `export CLASSPATH="$CLASSPATH:/root/gaiandb/gaiandb/policy/*"`
    `export CLASSPATH="$CLASSPATH:/root/gaiandb/gaiandb/policy/conf/"`

* copy plugin/target/ranger-gaian-plugin-1.0.0-SNAPSHOT.jar from your build tree, to this policy folder on Gaian.

* Copy the configuration files found in this project under plugin/src/main/resources/conf to policy/conf on gaian. These are the configuration files
for the plugin which we will edit below

* configure Gaian to use RangerPolicyResultFilter, by adding this line at the end of gaiandb_config.properties:

    `SQL_RESULT_FILTER=org.apache.ranger.services.gaian.RangerPolicyResultFilter`


**Deploying the Service Definition to Ranger**

This needs to be done once only (unless the service definition changes)

    `cd resources/service-defs`
    `curl -u admin:admin -X POST -H "Accept: application/json" -H "Content-Type: application/json" --data @ranger-servicedef-gaian.json http://localhost:6080/service/plugins/definitions`

Replace localhost with the IP /port of your Ranger Server

Once posted, the ranger access manager page should now show 'Gaian' as a selectable component

**Updating an existing service definition**

First delete any existing gaian services through the ranger UI

Then find out the id of the gaian servicedef. This would either have been returned when deployed, or
you can pull all definitions to find it ie

    `curl -u admin:admin localhost:6080/service/public/v2/api/servicedef/ | jsonpp | less`

'jsonpp' is one way to format a json file on macOS (installed using the 'brew' environment ie brew install jsonpp)

Look for gaian & then find the id....

To delete:

    `curl  -u admin:admin -X DELETE -H "Accept: application/json" -H "Content-Type: application/json"  http://9.20.65.115:6080/service/public/v2/api/servicedef/102`

**Configuring the Gaian plugin**

Modify the following files on gaian under policy/conf:

* ranger-gaian-audit.xml

    To use solr modify the property 'xasecure.audit.solr.is.enabled' to true, and set the correct hostname/solr endpoint in xasecure.audit.solr.solr_url

    Alternatively to log directly to a RDBMS such as mysql modify the property 'xasecure.audit.db.is.enabled' to true, and set the correct hostname/db uri in xasecure.audit.jpa.javax.persistence.jdbc.url

* ranger-gaian-security.xml

    In order for the gaian plugin to be able to retrieve policies, we must specify the REST endpoint. If this is
    incorrect, or there is no access (firewall etc) ranger policies will not be updated/work
    
    Specify this in the ranger.plugin.gaian.policy.rest.url property
    
    Do not change the property ranger.plugin.gaian.service.name and leave it set to 'gaian'
    
**Creating Ranger Policies**

* Create a ranger service def by logging onto the Ranger UI and selecting resource policies. You should see a section labelled 'Gaian'. Create a new instance of a Gaian service. You MUST use the name 'gaian'. Tag service can be left blank for now, and whilst user/password have to be filled in, they
will be ignored.

**Testing Policies**

* A quick test:
0. Run testGaianDB.sh, it should show an empty table.
1. Create a policy of schema *, table LT0, column * on Ranger UI.
2. run testGaianDB.sh, it should show table LT0 correctly.

* Test with specific column query:
For this to be able to work correctly, must use derby vti syntax. For example, if only query column LOCATION in table LT0, the syntax is:

select LOCATION from new com.ibm.db2j.GaianTable('LT0') LT0

and then create column access/deny policies for testing.


**Verifying the environment**

(tbd)

**Todos**

* Add Masking support to plugin
* Add tag support to plugin
* Add proxy auth module
* Add info on verifying environment
* Add info on debugging/logging
* Create a 'dist' directory or similar with the two relevant jars + instructions + servicedef + policy configuration files
* check dependency versions. some are back level, though it may depend what ranger built with
 
