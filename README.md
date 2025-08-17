Collaborative Project Management Portal
A collaborative project management system built with a Spring Boot backend and a JavaFX frontend, designed to help teams manage projects, tasks, documents, and real-time communication.
________________________________________
 Features :
•	Authentication & Security
o	JWT-based authentication for secure access.
o	Role-based privileges (Admins, Members, Guests).
•	Project Management
o	Create, validate, and manage projects.
o	Public or private visibility.
o	Project lifecycle: Proposal → Validation → Active → Closed → Archived.
•	Task Management
o	Tasks & subtasks with states (To Do, In Progress, Completed, Cancelled).
o	Assignment of tasks to members.
•	Collaboration Tools
o	Real-time messaging via WebSocket.
o	Shared resources: document repositories (src, web), mailing lists, and agendas.
o	Automatic group creation for project members and administrators.
•	Frontend (JavaFX + CSS)
o	Desktop client with a responsive interface.
o	Dashboards for projects, members, tasks, and messaging.
•	Backend (Spring Boot)
o	RESTful API endpoints.
o	Spring Security with JWT.
o	WebSocket for chat and notifications.
o	Hibernate/JPA for database persistence.
________________________________________
Tech Stack :
Backend: Spring Boot, Spring Security (JWT), WebSocket, Hibernate/JPA, PostgreSQL
Frontend: JavaFX, CSS
Build Tools: Maven/Gradle
Other Tools: Scene Builder, IntelliJ IDEA
________________________________________
Installation :
  Prerequisites
  •	JDK 17+
  •	Maven or Gradle
  •	PostgreSQL
  Steps
  1.	Clone the repository:
  2.	git clone https://github.com/your-username/your-repo-name.git
  3.	cd your-repo-name
  4.	Configure the database in application.properties.
  5.	Build and run the backend:
  6.	mvn spring-boot:run
  7.	Launch the JavaFX frontend with your IDE or mvn javafx:run.
________________________________________
Screenshots :

________________________________________
Roadmap :
  •	Notifications for tasks and deadlines.
  •	File sharing improvements.
  •	Multi-language support.
________________________________________
License :
This project is licensed under the MIT License
