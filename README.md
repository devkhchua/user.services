# Java Application - Spring Boot with JPA to maintain user into MySQL Database running on Embedded Tomcat

# Part of Microservices

1) User CRUD
- to create, maintain and to handle user in a standalone database (MySQL)
- encrypt stored password

2) Custom Login Authentication (Spring Security)
- custom class to retrieve user from database
- override authentication for custom authentication
- override success authentication for jwt token generation

3) Custom Authorization (Spring Security)
- custom class to perform authorization for each and every request passing through api
- each request will be required to have access token
- token will be verified to ensure validity and identity

4) Security Config (Spring Security)
- configuring for all http requests
- allowing/disallowing certain requests
- overriding default spring security login api url
- adding filter that leads to custom authentication/custom authorization
- requests are only allowed for authenticated users

5) Json Web Token Util
- to create util class to be used across other classes
- generate access token
- generate refresh token
- verify token

