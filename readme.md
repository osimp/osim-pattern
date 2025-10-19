# Osim - Open Session In Method pattern

Open Session in Method - a good twin of OSIV with no drawbacks.

## Osim annotation

Contains Osim annotation, which you put on method, so that the same instance of
EntityManager would be used inside the method.

That helps if you want separate transactions within one session.

## ReleaseConnection annotation

Put ReleaseConnection annotation on method if you want to release DB connection
before the method starts. 

Really useful, if you want to make sure that your code uses connections properly
and not held idle, while application waits for an answer from external HTTP
service.