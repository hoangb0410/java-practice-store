# java-practice-store

### Admin

- Manage the list of all users (CRUD)
- Manage the list of all stores (CRUD)
- Approve new stores

### Store

- Register (verify OTP), log in via email (must be approved)
- Manage the list of users of the store
- Accumulate points for users
- Create a list of items for redemption (name, points required, image, expiration date, quantity, description)

### User

- Users register accounts and redeem rewards at all stores in the system
- Users register/log in via phone number and send OTP for verification
- When users make purchases at stores, their points are accumulated in two ways:
  - Fixed points (e.g., 100k earns 5 points, 200k earns 10 points)
  - Percentage of the order value
- User points are divided into rank levels (default rank upon registration is Bronze):
  - Bronze Rank (default): Every 100k earns 5 points or 10% up to 50 points
  - Silver Rank (achieved at 2000 points): Every 100k earns 10 points or 15% up to 100 points
  - Gold Rank (achieved at 5000 points): Every 100k earns 15 points or 20% up to 200 points
- Redeem products (each user can redeem up to 3 items at a time, with a maximum of 1000 points per redemption)

---

cp src/main/resources/application-example.properties src/main/resources/application.properties

# Dependencies

`spring-boot-starter-web`  
`spring-boot-starter-data-jpa`  
`spring-boot-starter-data-redis`  
`spring-boot-starter-mail`  
`spring-boot-starter-validation`  
`spring-boot-devtools`  
`spring-boot-starter-test`  
`spring-security-test`  
`spring-security-crypto`  
`postgresql`  
`lombok`  
`aws-java-sdk-s3`  
`java-jwt`  
`commons-codec`  
`springdoc-openapi-starter-webmvc-ui`
