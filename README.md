# Gym Manager API
## Simple Spring Boot API with JWT Authentication

### List of Endpoints

#### Unprotected Edpoints
> These endpoints are available for all users and serve the purpose of registering a new gym and logging both gyms and clients.
>>##### POST Requests
>>Register gym.
>>
>>This request saves a new gym in the database and gives back a response containing a message and a JWT that can be used to access gym-only endpoints. The body must contain a unique username, a password, a name and an address.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/authentication/register_gym/
>>
>>request body:
>>{
>>      "username": "gym1",
>>      "password": "gym1pw",
>>      "name": "gym1name",
>>      "address": "gym1address"
>>}
>>```
>>Login.
>>
>>This is a login request. Usernames and passwords for both clients and gyms can be used. The body must contain the username and the password. It gives back a JWT that can be used to access restricted endpoints (either gym-only or client-only, depending on who's logging in).
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/authentication/login/
>>
>>request body:
>>{
>>      "username": "gym1",
>>      "password": "gym1pw"
>>}
>>```
>>Logout.
>>
>>This is a logout request. It expires the JWT present in the authentication header. It works for both gym and client.
>>```
>>request endpoint:
>>localhost:8080/api/v1/logout/
>>```

#### Protected Endpoints
>These endpoints are available only for requests with [Bearer Authentication](https://swagger.io/docs/specification/authentication/bearer-authentication/) in the header, following the format:
>
>Authorization: Bearer \<JWT\>
>>##### POST Requests
>>Register client. [GYM-ONLY]
>>
>>This is how a gym register its clients, creating both a new user and a new client in the database. The body must contain the user's username, its name and a password. The username submitted to the database will not be the one used by the client to log in. It is formatted as \<submitted-usernamename\>@\<gym-username\>.This request gives back a JWT, a message and the usernamed that was saved to the database.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/client/
>>
>>request body:
>>{
>>      "username": "client1",
>>      "name": "client1name",
>>      "password": "client1pw"
>>}
>>```
>>Register daily routine. [GYM-ONLY]
>>
>>This is how a gym registers a daily routine of exercises. The body must contain a title, an array of strings containing the execises' names, an array of integers containing the exercises' loads, an array of integers containing the exercises' reps and another array of integers containing the exercises' sets. The exercises are ruled by the indexes of the arrays. It gives back a message containing the object that was saved to the database.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/daily_routine/
>>
>>request body:
>>{
>>      "title": "BICEP DAY",
>>      "exerciseName": [
>>          "Biceps Curls", "Concentration Curls"
>>      ],
>>      "exerciseLoad": [
>>          25, 25
>>      ],
>>      "exerciseReps": [
>>          15, 15
>>      ],
>>      "exerciseSets": [
>>          4, 4
>>      ]
>>}
>>```
>>Register weekly routine. [GYM-ONLY]
>>
>>This is how a gym registers a new (empty) weekly routine. The body must contain the weekly routine's title. It gives back a messega containing the object that was saved to the database.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/weekly_routine/
>>
>>request body:
>>{
>>      "title": "Beginner Week"
>>}
>>```
>>##### PUT Requests
>>Edit gym. [GYM-ONLY]
>>
>>This is how a logged gym changes its name and/or address. The body can contain name and/or address. The response is the gym object that was saved by the database.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/
>>
>>request body:
>>{
>>      "name": "NEW NAME",
>>      "address": "NEW LOCATION"
>>}
>>```
>>Edit client by id. [GYM-ONLY]
>>
>>This is how a gym edits one of its clients' name. The body must contain the new name. The response is the object that was saved by the database.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/client/{id}/
>>
>>request body:
>>{
>>      "name": "Client's new name"
>>}
>>```
>>Edit a weekly routine by id. [GYM-ONLY].
>>
>>This is how a gym edits one of its weekly routines' title. The body must contain the new title. The response is the weekly routine object as it was saved to the database.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/weekly_routine/{id}/
>>
>>request body:
>>{
>>      "title": "new title"
>>}
>>```
>>Edit daily routine by id. [GYM-ONLY]
>>
>>This is how a gym edits one of its daily routines by id. The body can be empty (nothing will change), contain any, or all of the fields as described in the "Register daily routine session".
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/daily_routine/{id}/
>>
>>request body:
>>{
>>      "exerciseName": [
>>          "THIS", "TEST"
>>      ],
>>      "exerciseReps": [
>>          99, 99
>>      ]
>>}
>>```
>>
>>Add daily routine to weekly routine. [GYM-ONLY]
>>
>>This is how a gym adds one of its daily routines to one of its weekly routines. The body must be empty. The response is the weekly routine object with the daily routine object added to it (as it was saved to the database).
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/add_daily_weekly/weekly_routine/{weeklyRoutineId}/daily_routine/{dailyRoutineId}/
>>```
>>Remove daily routine from weekly routine. [GYM-ONLY]
>>
>>This is how a gym removes one of its daily routines from one of its weekly routines. The body muust be empty. The response is a weekly routine without the daily routine that was removed from it (as it was saved to the database).
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/remove_daily_weekly/weekly_routine/1/daily_routine/102/
>>```
>>Attribute weekly routine to client. [GYM-ONLY]
>>
>>This is how a gym attributes one of its weekly routines to one of its clients. The body must be empty. The response is the client object with the weekly routine object added to it (as it was saved to the database).
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/add_weekly_client/client/{clientId}/weekly_routine/{weeklyRountineId}/
>>```
>>##### GET Requests
>>Get client's homepage. [CLIENT-ONLY]
>>
>>This is how a registered client, after authentication, can get its weekly routines and exercises. The response is the client object.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/client/
>>```
>>Get all clients. [GYM-ONLY]
>>
>>This is how a gym gets all of its clients. The response is a HATEOAS response containing all clients and relevant links.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/client/
>>```
>>Get client by id. [GYM-ONLY]
>>
>>This is how a gym gets info on one of its clients based on its id. The response is a HATEOAS response containing the client object and relevant links.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/client/{id}/
>>```
>>Get all weekly routines. [GYM-ONLY]
>>
>>This is how a gym gets all of its weekly routines. The response is a HATEOAS response containing all weekly routines and relevant links.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/weekly_routine/
>>```
>>Get weekly routine by id. [GYM-ONLY]
>>
>>This is how a gym gets info on one of its weekly routines based on its id. The response is a HATEOAS response containing the client object and relevant links.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/weekly_routine/{id}/
>>```
>>Get all daily routines. [GYM-ONLY]
>>
>>This is how a gym gets all of its daily routines. The response is a HATEOAS response containing all daily routines and relevant links.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/daily_routine/
>>```
>>Get daily routine by id. [GYM-ONLY]
>>
>>This is how a gym gets info on one of its daily routines based on its id. The response is a HATEOAS response containing the daily routine object and relevant links.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/daily_routine/{id}/
>>```
>>##### DELETE Requests
>>Delete client. [CLIENT-ONLY]
>>
>>Deletes the client that is currently logged in. The response contains a message.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/client/
>>```
>>Delete gym. [GYM-ONLY]
>>
>>Deletes the gym that is currently logged in. The response contains a message.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/
>>```
>>Delete client by id. [GYM-ONLY]
>>
>>This is how a gym deletes one of its clients. The response contains a message.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/client/{id}/
>>```
>>Delete weekly routine by id. [GYM-ONLY]
>>
>>This is how a gym deletes one of its weekly routines. The response contains a message.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/weekly_routine/{id}/
>>```
>>Delete daily routine by id. [GYM-ONLY]
>>
>>This is how a gym deletes one of its daily routines. The response contains a message.
>>```JSON
>>request endpoint:
>>localhost:8080/api/v1/gym/daily_routine/{id}/
>>```

