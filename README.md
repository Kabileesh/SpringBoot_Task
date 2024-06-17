Clone the application using the following command

```
git clone https://github.com/Kabileesh/SpringBoot_Task.git
cd SpringBoot_Task
```

Create `application.properties` files under **src/resources** and include the following and replace with appropriate values
```
spring.application.name=APP_NAME
spring.data.mongodb.uri=MONGO_DB_CONNECTION_STRING
jwt.token.secret=TOKEN_SECRET
jwt.token.expiration-time=TOKEN_EXPIRY_TIME
spring.data.mongodb.auto-index-creation=true
```

**APP_NAME**: Name of your application.

**MONGO_DB_CONNECTION_STRING**: Connection string to your MongoDB instance.

**TOKEN_SECRET**: Secret key used for signing JWT tokens.

**TOKEN_EXPIRY_TIME**: Expiration time for JWT tokens in milliseconds.


<hr />

To run the application, use the following Maven command:
```
mvn spring-boot:run 
```

There are 3 endpoints `/api/user/register`, `/api/user/ogin` and `/api/user/fetch` and make use the following cURLs:

cURL to register user:
```
curl --location 'http://localhost:8080/api/user/register' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=56E32873998D2F91BB2F39D9FCFCE486' \
--data-raw '{
    "username": "sample1@gmail.com",
    "name": "Sample",
    "password": "Sample@1234"
}'
```

cURL to login user:
```
curl --location 'http://localhost:8080/api/user/login' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=56E32873998D2F91BB2F39D9FCFCE486' \
--data-raw '{
    "username": "sample@gmail.com",
    "password": "Sample@1234"
}'
```

cURL to fetch user details - replace the bearer token with the generated accessToken:
```
curl --location 'http://localhost:8080/api/user/fetch?username=sample%40gmail.com' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2NjZmZGUyOWYyZjM2YTFjOGM3ZTM1M2UiLCJ1c2VybmFtZSI6InNhbXBsZUBnbWFpbC5jb20iLCJuYW1lIjoiU2FtcGxlIiwiaWF0IjoxNzE4NjQxMzk4LCJleHAiOjE3MTg2NDQ5OTh9.k7ydvevKQUiKYsKGcAIDGFvfPp0jz2KuROEUWzmRejg' \
--header 'Cookie: JSESSIONID=56E32873998D2F91BB2F39D9FCFCE486'
```
