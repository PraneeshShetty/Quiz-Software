# Quiz App (Spring Boot + MongoDB) with Safe Browsing Mode

A simple quiz web app built with Spring Boot, Thymeleaf, and MongoDB. It enforces Safe Browsing Mode (SBM) during the quiz: if the user switches tabs, the window loses focus, or fullscreen is exited, the quiz is auto-submitted. Each user can attempt the quiz only once unless an Admin explicitly approves a retake.

Key updates per request:
- The Admin Panel button is no longer shown on the result page; the Admin Panel is only accessible at a separate endpoint (/admin) for Admin users.
- When a quiz is auto-submitted due to SBM, that status is visible to the Admin in the Admin Panel.
- Responsive UI across devices (mobile and laptop/desktop).


## Features
- Login first, then take the quiz
- Safe Browsing Mode while taking quiz (requests fullscreen, auto-submits on tab switch/blur/exit FS)
- One attempt per user; Admin can approve retake
- Admin Panel at /admin (not linked on result page)
- MongoDB storage for users, questions, and results
- Responsive pages for phones, tablets, and laptops


## Technology
- Java 17+ (or compatible)
- Spring Boot (Web, Thymeleaf, Data MongoDB)
- MongoDB (local or Atlas)


## Run locally
1. Ensure MongoDB is available (pick one):
   - Local MongoDB on default port (mongodb://localhost:27017), or
   - MongoDB Atlas connection string (recommended for hosting)

2. Configure MongoDB connection via environment variables (preferred) or application.properties:
   - Environment variables (recommended):
     - SPRING_DATA_MONGODB_URI: Mongo connection URI
     - SPRING_DATA_MONGODB_DATABASE: quiz_app_db (or any name)
   - Or edit src/main/resources/application.properties and set:
     - spring.data.mongodb.uri=<your_uri>
     - spring.data.mongodb.database=quiz_app

3. Build and run:
   - Using Maven Wrapper:
     - macOS/Linux: ./mvnw spring-boot:run
     - Windows: mvnw.cmd spring-boot:run

4. Open http://localhost:8080

5. Login with seeded users (if running with default seed):
   - Admin: admin@example.com / admin123
   - User: user@example.com / user123

6. Start the quiz. Do not switch tabs or exit fullscreen; if you do, the quiz auto-submits.


## How data is stored
- MongoDB collections:
  - users: { id, email, password, role, takenQuiz }
  - questions: { id, text, options[], correctIndex }
  - results: { id, userId, totalQuestions, correct, answers{questionId:selectedOption}, submittedAt, autoSubmitted, autoReason }
- Seed data on startup creates:
  - Admin user (admin@example.com)
  - Regular user (user@example.com)
  - Sample questions

Note: Passwords are plain text for demo simplicity. Do not use in production.


## Admin Panel access
- Endpoint: /admin
- Accessible only to logged-in users with role ADMIN.
- The result page no longer displays an Admin Panel button. To access the Admin panel, browse directly to /admin after logging in as an Admin.
- In the Admin panel, you can:
  - See all users
  - See each user’s latest attempt and whether it was auto-submitted (with reason)
  - Approve a retake (sets takenQuiz=false)


## Hosting on Railway (free tier example)
Railway supports deploying Java apps easily. You will also need a managed MongoDB (e.g., Railway’s MongoDB plugin if available for your region/account, or MongoDB Atlas).

Steps:
1. Push this repository to GitHub.
2. Create a new project on Railway and choose to deploy from your GitHub repo.
3. Set environment variables in Railway’s project settings:
   - SPRING_DATA_MONGODB_URI: your Mongo URI (Atlas or Railway Mongo plugin)
   - SPRING_DATA_MONGODB_DATABASE: quiz_app (or your DB name)
   - SERVER_PORT: 8080 (Railway often sets PORT automatically; Spring will adapt. If needed, add server.port=${PORT:8080} to application.properties.)
4. Build and start command (Railway usually detects automatically). If you need to set explicitly:
   - Build: mvn -DskipTests package
   - Start: java -jar target/Quiz_App-0.0.1-SNAPSHOT.jar
5. Wait for the deployment to finish. Railway will provide a public URL like https://your-app.up.railway.app
6. Access the app’s homepage at that URL.
7. Admin access: login as admin@example.com (admin123) and navigate directly to https://your-app.up.railway.app/admin

MongoDB Atlas quick setup (if not using a Railway Mongo plugin):
- Create an Atlas cluster (free tier), create a database user and obtain the connection string.
- Allow your Railway project’s IPs to access the cluster (Network Access) or use “Allow access from anywhere” (0.0.0.0/0) for testing only.
- Set the Atlas connection string (with username/password) as SPRING_DATA_MONGODB_URI in Railway.

Security note: Never commit secrets. Use Railway’s environment variables.


## Safe Browsing Mode details
On the quiz page, the app requests fullscreen and listens for:
- visibilitychange (page hidden or tab switch)
- window blur (loss of focus)
- fullscreenchange (user exits fullscreen)
If any of the above occurs, the form auto-submits with an autoReason indicating the cause. Admins can see if a submission was manual or auto-submitted.


## FAQ
- I can’t access /admin
  - Ensure you are logged in as admin@example.com (role ADMIN). Regular users cannot access it. The result page will not link to /admin.
- Can users retake the quiz?
  - Only if an Admin clicks "Approve Retake" in the Admin Panel; otherwise, users are limited to a single attempt.
- Can I change the seeded users or questions?
  - Yes. Update DataLoader.java or preload your own data in Mongo.


## Development notes
- Minimal demo security: session-based auth with a simple interceptor. For production use Spring Security and hashed passwords (BCrypt), CSRF, etc.
- SBM is client-side best-effort and can be bypassed by advanced users; for high-stakes exams, use kiosk mode or proctoring.
# Quiz_App_Spring
