# RouteEd: How to Use the Application

RouteEd has two roles:

- **Learner**: takes a diagnostic assessment, receives an AI-personalized route, and completes modules.
- **Course administrator**: creates courses, modules, and assessments; then watches learner progress and health analytics.

## Start RouteEd

The development services must be running:

```powershell
docker compose up -d api
cd frontend
npm run dev
```

Open [http://localhost:5173](http://localhost:5173).

## Learner flow

1. On the login page, select **Create an account**.
2. Enter your name, email, and a password with at least eight characters.
3. Choose **Learner** and create the account. RouteEd signs you in automatically.
4. The learner dashboard shows the ready-to-use **Java Foundations** and **Python Foundations** tutorials.
5. Select **Take assessment** on a course.
6. Answer each question and select **Finish assessment**.
7. RouteEd sends your skill scores to the local `llama3.2:3b` Ollama model, then creates your personalized lesson order.
8. Select **Open route**. Each module card shows whether it is up next, completed, skipped, or locked.
9. Open a module. Until video content is added, the video area says **“The video is yet to be added.”** Select **Mark complete** after finishing the lesson.

## Administrator flow

1. Create a second account and choose **Course administrator**.
2. Open **Course builder** from the sidebar.
3. Create a course with a title, description, and completion duration.
4. Add modules with a title, skill tag, duration, and optional content URL.
5. Create a diagnostic question. The correct option must match one of the listed options exactly.
6. Open **Admin workspace** to select a course and view:
   - learners with personalized routes;
   - completion rate;
   - module drop-off chart;
   - learner health score and risk classification.

## Learner Health Score

RouteEd calculates the score in real time:

```text
(completion pace × 0.5) + (login recency × 0.3) + (assessment score × 0.2)
```

- **Good**: 75 or higher
- **At-Risk**: 50–74
- **Critical**: below 50

## Login troubleshooting

- Use the exact email and password used at account creation. Email addresses are treated without case sensitivity.
- Ensure the API is available at [http://localhost:8080/api/health](http://localhost:8080/api/health). It should return `"status": "UP"`.
- Refresh the browser after a code update using `Ctrl + R`.
- If an older session appears stuck, use the arrow icon in the top-right to sign out, then sign in again.

## Verified end-to-end scenario

The following complete workflow has been run successfully against the live Docker stack:

1. Register administrator.
2. Create course, module, diagnostic assessment, and question.
3. Register learner.
4. Submit the diagnostic assessment.
5. Generate and store an Ollama personalized route.
6. Mark the module complete.
7. Confirm 100% learner progress and 100% course completion analytics.
