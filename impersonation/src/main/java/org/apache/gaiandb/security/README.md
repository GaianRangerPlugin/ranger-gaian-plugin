Gaiandb proxy authenticator

Ensure class is in classpath

Set the following in derby.properties:
derby.authentication.provider=org.apache.gaiandb.security.ProxyUserAuthenticator

To use, add the JDBC connection properties
 proxy-user (ie 'root')
 proxy-pwd (ie 'admin' )
 
 And set 'user' to the actual user you want to be auth;d
 as ie 'nigel'
 
 For now password is required by the driver, but is ignored, only
 the proxy-pwd is used for authentication
 
 There are no privilige checks on
  - whether the proxy-user can be permitted to act on behalf of another user
  - whether the user allows other users to impersonate it
  
  Further review needed. this is a first pass
  proof of concept