# GolfClub API

QAP 4 for SDAT &amp; Dev Ops
===================================================================================================


Description
===================================================================================================

This project is a SpringBoot REST API for managing golf club members and tournaments.


Requirements
===================================================================================================

-Java 17+ & Maven 3.6+

-Docker Desktop


Installation & Build
===================================================================================================

Clone the repository into your IDE with this link: https://github.com/Noah-Hickey/GolfClub

Once cloned, build the application by running this into your IDE's terminal: mvn clean package


Docker - Run the following commands under each step.
===================================================================================================

# Build and start API + MySQL services:
docker compose up --build -d

#Verify running containers:
docker compose ps

# Tear down when done:
docker compose down

Your API will be available at http://localhost:8080 and MySQL at localhost:3306.


API Endpoints
===================================================================================================

<Method>

<Path>

<Description>

---------------------------------------------------------------------------------------------------

GET

/members

List all members

GET

/members?name={name}

Search members by name (partial match)

POST

/members

Create a new member

GET

/tournaments

List all tournaments

GET

/tournaments?startDate={YYYY-MM-DD}

Search tournaments by start date

POST

/tournaments

Create a new tournament

POST

/tournaments/{tournamentId}/members/{memberId}

Add a member to a tournament


**Screenshots for examples are provided in the Postman_Screenshots folder!**
