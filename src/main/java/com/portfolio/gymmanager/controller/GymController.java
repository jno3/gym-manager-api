package com.portfolio.gymmanager.controller;

import com.portfolio.gymmanager.model.Client;
import com.portfolio.gymmanager.model.DailyRoutine;
import com.portfolio.gymmanager.model.Gym;
import com.portfolio.gymmanager.model.WeeklyRoutine;
import com.portfolio.gymmanager.request.DailyRoutineRequest;
import com.portfolio.gymmanager.request.RegisterRequest;
import com.portfolio.gymmanager.request.WeeklyRoutineRequest;
import com.portfolio.gymmanager.response.DeleteResponse;
import com.portfolio.gymmanager.security.AuthenticationResponse;
import com.portfolio.gymmanager.security.AuthenticationService;
import com.portfolio.gymmanager.security.JwtService;
import com.portfolio.gymmanager.service.ClientService;
import com.portfolio.gymmanager.service.DailyRoutineService;
import com.portfolio.gymmanager.service.GymService;
import com.portfolio.gymmanager.service.WeeklyRoutineService;
import lombok.Data;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
//@PreAuthorize("hasRole('GYM')")
@RequestMapping("/api/v1/gym")
@Data
public class GymController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final GymService gymService;
    private final ClientService clientService;
    private final WeeklyRoutineService weeklyRoutineService;
    private final DailyRoutineService dailyRoutineService;

    @PutMapping("/")
    public ResponseEntity<EntityModel<Gym>> editGym(
            @RequestHeader("Authorization") String bearer,
            @RequestBody RegisterRequest request
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        gymService.editGym(request, gym);
        return ResponseEntity.ok(EntityModel.of(gym));
    }

    @DeleteMapping("/")
    public ResponseEntity<DeleteResponse> removeGym(
            @RequestHeader("Authorization") String bearer
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        gymService.deleteGym(gym);
        DeleteResponse deleteResponse = DeleteResponse.builder().
                message("Gym successfully removed").
                build();
        return ResponseEntity.ok(deleteResponse);
    }


    @PostMapping("/client/")
    public ResponseEntity<AuthenticationResponse> newClient(
            @RequestBody RegisterRequest request,
            @RequestHeader("Authorization") String bearer
    ) {
        String jwt = bearer.substring(7);
        System.out.println("bbb");
        String gymUsername = jwtService.extractUsername(jwt);
        request.setGymUsername(gymUsername);
        return ResponseEntity.ok(authenticationService.registerClient(request));
    }

    @GetMapping("/client/")
    public ResponseEntity<CollectionModel<Client>> getAllClients(
            @RequestHeader("Authorization") String bearer
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));

        List<Client> clients = gym.getClients();
        if (clients != null) {
            for (Client client : clients) {
                Link selfClientLink = linkTo(methodOn(GymController.class).getSingleClient(bearer, client.getId())).withSelfRel();
                Link allClientsLink = linkTo(methodOn(GymController.class).getAllClients(bearer)).withRel("allClients");

                WeeklyRoutine weeklyRoutine = client.getWeeklyRoutine();
                if (weeklyRoutine != null) {
                    Link singleWeeklyRoutine = linkTo(methodOn(GymController.class).getSingleWeeklyRoutine(bearer, weeklyRoutine.getId())).withSelfRel();
                    Link allDailyRoutinesLink = linkTo(methodOn(GymController.class).getAllWeeklyRoutines(bearer)).withRel("allWeeklyRoutines");
                    List<DailyRoutine> dailyRoutines = weeklyRoutine.getDailyRoutines();
                    for (DailyRoutine dailyRoutine : dailyRoutines) {
                        Link selfDailyRoutineLink = linkTo(methodOn(GymController.class).getSingleDailyRoutine(bearer, dailyRoutine.getId())).withSelfRel();
                        dailyRoutine.add(selfDailyRoutineLink);
                        dailyRoutine.add(allDailyRoutinesLink);
                    }
                    weeklyRoutine.add(allDailyRoutinesLink);
                    weeklyRoutine.add(singleWeeklyRoutine);
                }
                client.add(selfClientLink);
                client.add(allClientsLink);
            }
            Link link = linkTo(methodOn(GymController.class).getAllWeeklyRoutines(bearer)).withSelfRel();
            return ResponseEntity.ok((CollectionModel.of(clients, link)));
        } else {
            throw new RuntimeException();
        }
    }

    @GetMapping("/client/{id}/")
    public ResponseEntity<EntityModel<Client>> getSingleClient(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "id") Integer id) {
        Client client = clientService.getClientById(id);
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        if (!client.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            WeeklyRoutine weeklyRoutine = client.getWeeklyRoutine();
            if (weeklyRoutine != null) {
                if (weeklyRoutine.getDailyRoutines().size() > 0) {
                    List<DailyRoutine> dailyRoutines = weeklyRoutine.getDailyRoutines();
                    for (DailyRoutine dailyRoutine : dailyRoutines) {
                        Link singleDailyRoutineLink = linkTo(methodOn(GymController.class).getSingleDailyRoutine(bearer, dailyRoutine.getId())).withSelfRel();
                        Link allDailyRoutinesLink = linkTo(methodOn(GymController.class).getAllDailyRoutines(bearer)).withRel("allDailyRoutines");
                        dailyRoutine.add(singleDailyRoutineLink);
                        dailyRoutine.add(allDailyRoutinesLink);
                    }
                }
                Link singleWeeklyRoutineLink = linkTo(methodOn(GymController.class).getSingleWeeklyRoutine(bearer, weeklyRoutine.getId())).withSelfRel();
                Link allWeeklyRoutinesLink = linkTo(methodOn(GymController.class).getAllWeeklyRoutines(bearer)).withRel("allWeeklyRoutines");
                weeklyRoutine.add(singleWeeklyRoutineLink);
                weeklyRoutine.add(allWeeklyRoutinesLink);
            }

            Link singleClientLink = linkTo(methodOn(GymController.class).getSingleClient(bearer, client.getId())).withSelfRel();
            Link allClientsLink = linkTo(methodOn(GymController.class).getAllClients(bearer)).withRel("allClients");
            client.add(singleClientLink);
            client.add(allClientsLink);
            EntityModel<Client> result = EntityModel.of(client);
            return ResponseEntity.ok(result);
        }
    }

    @PutMapping("/client/{id}/")
    public ResponseEntity<EntityModel<Client>> editClient(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "id") Integer id,
            @RequestBody RegisterRequest request
    ) {
        Client client = clientService.getClientById(id);
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        if (!client.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            clientService.editClient(request, client);
            return ResponseEntity.ok(EntityModel.of(client));
        }
    }

    @DeleteMapping("/client/{id}/")
    public ResponseEntity<DeleteResponse> removeClient(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "id") Integer id
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        Client client = clientService.getClientById(id);
        if (!client.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            clientService.deleteClient(client);
            DeleteResponse deleteResponse = DeleteResponse.builder().
                    message("Client successfully removed").
                    build();
            return ResponseEntity.ok(deleteResponse);
        }
    }


    @PostMapping(value = "/weekly_routine/")
    public ResponseEntity<WeeklyRoutine> newWeeklyRoutine(
            @RequestHeader("Authorization") String bearer,
            @RequestBody WeeklyRoutineRequest weeklyRoutineRequest
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        return ResponseEntity.ok(weeklyRoutineService.saveWeeklyRoutineRequest(weeklyRoutineRequest, gym));
    }

    @GetMapping("/weekly_routine/")
    public ResponseEntity<CollectionModel<WeeklyRoutine>> getAllWeeklyRoutines(
            @RequestHeader("Authorization") String bearer
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        List<WeeklyRoutine> allWeeklyRoutines = gym.getWeeklyRoutines();
        if (allWeeklyRoutines.size() > 0) {
            for (WeeklyRoutine weeklyRoutine : allWeeklyRoutines) {
                Link selfWeeklyLink = linkTo(methodOn(GymController.class).getSingleWeeklyRoutine(bearer, weeklyRoutine.getId())).withSelfRel();
                Link allWeeklyLink = linkTo(methodOn(GymController.class).getAllWeeklyRoutines(bearer)).withRel("allWeeklyRoutines");
                if (weeklyRoutine.getDailyRoutines().size() > 0) {
                    Link allDailyLink = linkTo(methodOn(GymController.class).getAllDailyRoutines(bearer)).withRel("allDailyRoutines");
                    List<DailyRoutine> allDailyRoutines = weeklyRoutine.getDailyRoutines();
                    for (DailyRoutine dailyRoutine : allDailyRoutines) {
                        Link selfDailyLink = linkTo(methodOn(GymController.class).getSingleDailyRoutine(bearer, dailyRoutine.getId())).withSelfRel();
                        dailyRoutine.add(selfDailyLink);
                        dailyRoutine.add(allDailyLink);
                    }
                    weeklyRoutine.add(allDailyLink);
                }
                weeklyRoutine.add(selfWeeklyLink);
                weeklyRoutine.add(allWeeklyLink);
            }
        }
        Link link = linkTo(methodOn(GymController.class).getAllWeeklyRoutines(bearer)).withSelfRel();
        CollectionModel<WeeklyRoutine> result = CollectionModel.of(allWeeklyRoutines, link);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/weekly_routine/{id}/")
    public ResponseEntity<EntityModel<WeeklyRoutine>> getSingleWeeklyRoutine(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "id") Integer id
    ) {
        WeeklyRoutine weeklyRoutine = weeklyRoutineService.getWeeklyRoutineById(id);
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        if (!weeklyRoutine.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            List<DailyRoutine> dailyRoutines = weeklyRoutine.getDailyRoutines();
            Link allDailyRoutines = linkTo(methodOn(GymController.class).getAllDailyRoutines(bearer)).withRel("allDailyRoutines");
            if (dailyRoutines.size() > 0) {
                for (DailyRoutine dailyRoutine : dailyRoutines) {
                    Link singleDailyRoutine = linkTo(methodOn(GymController.class).getSingleDailyRoutine(bearer, dailyRoutine.getId())).withSelfRel();
                    dailyRoutine.add(singleDailyRoutine);
                    dailyRoutine.add(allDailyRoutines);
                }
            }

            Link allWeeklyRoutines = linkTo(methodOn(GymController.class).getAllWeeklyRoutines(bearer)).withRel("allWeeklyRoutines");
            Link singleWeeklyRoutine = linkTo(methodOn(GymController.class).getSingleWeeklyRoutine(bearer, weeklyRoutine.getId())).withSelfRel();
            weeklyRoutine.add(allDailyRoutines);
            weeklyRoutine.add(allWeeklyRoutines);
            weeklyRoutine.add(singleWeeklyRoutine);

            return ResponseEntity.ok(
                    EntityModel.of(weeklyRoutine)
            );
        }
    }

    @PutMapping(value = "weekly_routine/{id}/")
    public ResponseEntity<EntityModel<WeeklyRoutine>> editWeeklyRoutine(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "id") Integer id,
            @RequestBody WeeklyRoutineRequest request
    ) {
        WeeklyRoutine weeklyRoutine = weeklyRoutineService.getWeeklyRoutineById(id);
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        if (!weeklyRoutine.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            weeklyRoutineService.editWeeklyRoutine(request, weeklyRoutine);
            return ResponseEntity.ok(EntityModel.of(weeklyRoutine));
        }
    }

    @DeleteMapping("/weekly_routine/{id}/")
    public ResponseEntity<DeleteResponse> removeWeeklyRoutine(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "id") Integer id
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        WeeklyRoutine weeklyRoutine = weeklyRoutineService.getWeeklyRoutineById(id);
        if (!weeklyRoutine.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            weeklyRoutineService.deleteWeeklyRoutine(weeklyRoutine);
            DeleteResponse deleteResponse = DeleteResponse.builder().
                    message("Weekly routine successfully removed").
                    build();
            return ResponseEntity.ok(deleteResponse);
        }
    }


    @PostMapping(value = "/daily_routine/")
    public ResponseEntity<DailyRoutine> newDailyRoutine(
            @RequestHeader("Authorization") String bearer,
            @RequestBody DailyRoutineRequest dailyRoutineRequest
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        return ResponseEntity.ok(dailyRoutineService.saveDailyRoutine(dailyRoutineRequest, gym));
    }

    @GetMapping("/daily_routine/")
    public ResponseEntity<CollectionModel<EntityModel<DailyRoutine>>> getAllDailyRoutines(
            @RequestHeader("Authorization") String bearer
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));

        List<EntityModel<DailyRoutine>> dailyRoutines = gym.getDailyRoutines().stream()
                .map(dailyRoutine -> EntityModel.of(dailyRoutine,
                        linkTo(methodOn(GymController.class).getSingleDailyRoutine(bearer, dailyRoutine.getId())).withSelfRel(),
                        linkTo(methodOn(GymController.class).getAllDailyRoutines(bearer)).withRel("allDailyRoutines")))
                .collect(Collectors.toList());

        return ResponseEntity.ok((CollectionModel.of(dailyRoutines, linkTo(methodOn(GymController.class).getAllDailyRoutines(bearer)).withSelfRel())));

    }

    @GetMapping(value = "/daily_routine/{id}/")
    public ResponseEntity<EntityModel<DailyRoutine>> getSingleDailyRoutine(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "id") Integer id
    ) {
        DailyRoutine dailyRoutine = dailyRoutineService.getDailyRoutineById(id);
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        if (!dailyRoutine.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            return
                    ResponseEntity.ok(
                            EntityModel.of(dailyRoutine,
                                    linkTo(methodOn(GymController.class).getSingleDailyRoutine(bearer, id)).withSelfRel(),
                                    linkTo(methodOn(GymController.class).getAllDailyRoutines(bearer)).withRel("allDailyRoutines")
                            )
                    );
        }
    }

    @PutMapping(value = "/daily_routine/{id}/")
    public ResponseEntity<EntityModel<DailyRoutine>> editDailyRoutine(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "id") Integer id,
            @RequestBody DailyRoutineRequest request
    ) {
        DailyRoutine dailyRoutine = dailyRoutineService.getDailyRoutineById(id);
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        if (!dailyRoutine.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            dailyRoutineService.editDailyRoutine(request, dailyRoutine);
            return ResponseEntity.ok(EntityModel.of(dailyRoutine));
        }
    }

    @DeleteMapping("/daily_routine/{id}/")
    public ResponseEntity<DeleteResponse> removeDailyRoutine(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "id") Integer id
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        DailyRoutine dailyRoutine = dailyRoutineService.getDailyRoutineById(id);
        if (!dailyRoutine.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            dailyRoutineService.deleteDailyRoutine(dailyRoutine);
            DeleteResponse deleteResponse = DeleteResponse.builder().
                    message("Daily routine successfully removed").
                    build();
            return ResponseEntity.ok(deleteResponse);
        }
    }


    @PutMapping("/add_daily_weekly/weekly_routine/{weeklyRoutineId}/daily_routine/{dailyRoutineId}/")
    public ResponseEntity<EntityModel<WeeklyRoutine>> addDailyToWeekly(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "weeklyRoutineId") Integer weeklyRoutineId,
            @PathVariable(value = "dailyRoutineId") Integer dailyRoutineId
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        WeeklyRoutine weeklyRoutine = weeklyRoutineService.getWeeklyRoutineById(weeklyRoutineId);
        DailyRoutine dailyRoutine = dailyRoutineService.getDailyRoutineById(dailyRoutineId);
        if (!weeklyRoutine.getGym().equals(gym) || !dailyRoutine.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            List<DailyRoutine> dailyRoutines = weeklyRoutine.getDailyRoutines();
            dailyRoutines.add(dailyRoutine);
            weeklyRoutine.setDailyRoutines(dailyRoutines);
            return
                    ResponseEntity.ok(
                            EntityModel.of(weeklyRoutineService.saveWeeklyRoutine(weeklyRoutine))
                    );
        }
    }

    @PutMapping("/add_weekly_client/client/{clientId}/weekly_routine/{weeklyRoutineId}/")
    public ResponseEntity<EntityModel<Client>> addWeeklyToClient(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "clientId") Integer clientId,
            @PathVariable(value = "weeklyRoutineId") Integer weeklyRoutineId
    ) throws AuthenticationException {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));

        Client client = clientService.getClientById(clientId);
        WeeklyRoutine weeklyRoutine = weeklyRoutineService.getWeeklyRoutineById(weeklyRoutineId);
        if (!weeklyRoutine.getGym().equals(gym) || !client.getGym().equals(gym)) {
            throw new AuthenticationException();
        } else {
            client.setWeeklyRoutine(weeklyRoutine);
            return
                    ResponseEntity.ok(
                            EntityModel.of(clientService.saveClient(client))
                    );
        }
    }

    @PutMapping("/remove_daily_weekly/weekly_routine/{weeklyRoutineId}/daily_routine/{dailyRoutineId}/")
    public ResponseEntity<EntityModel<WeeklyRoutine>> removeDailyFromWeekly(
            @RequestHeader("Authorization") String bearer,
            @PathVariable(value = "weeklyRoutineId") Integer weeklyRoutineId,
            @PathVariable(value = "dailyRoutineId") Integer dailyRoutineId
    ) {
        String jwt = bearer.substring(7);
        Gym gym = gymService.getGymByUsername(jwtService.extractUsername(jwt));
        WeeklyRoutine weeklyRoutine = weeklyRoutineService.getWeeklyRoutineById(weeklyRoutineId);
        DailyRoutine dailyRoutine = dailyRoutineService.getDailyRoutineById(dailyRoutineId);
        if (!weeklyRoutine.getGym().equals(gym) || !dailyRoutine.getGym().equals(gym)) {
            throw new RuntimeException();
        } else {
            List<DailyRoutine> dailyRoutines = weeklyRoutine.getDailyRoutines();
            dailyRoutines.remove(dailyRoutine);
            weeklyRoutine.setDailyRoutines(dailyRoutines);
            return
                    ResponseEntity.ok(
                            EntityModel.of(weeklyRoutineService.saveWeeklyRoutine(weeklyRoutine))
                    );
        }
    }
}
