**Atlas type/entity creation for testing with tagsync**

Note that an atlas client v2 build containing the patch for ATLAS-2488 is required

An example build from 8 March 2018 has been posted currently into
the local repo in this git tree. 

This can be updated with something like:
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=../atlas/client/client-v2/target/atlas-client-v2-1.0.0-SNAPSHOT.jar -DgroupId=org.apache.atlas -DartifactId=atlas-client-v2 -Dversion=1.0.0-ATLAS-2488 -Dpackaging=jar -DlocalRepositoryPath=./repo