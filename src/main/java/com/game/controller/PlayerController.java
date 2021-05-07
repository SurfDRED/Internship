package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.service.ServiceGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    private ServiceGame serviceGame;

    @Autowired
    public PlayerController(ServiceGame serviceGame) {
        this.serviceGame = serviceGame;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getListAllRegisteredPlayers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return serviceGame.getListAllRegisteredPlayers(
                Specification.where(
                        serviceGame.nameFilter(name)
                                .and(serviceGame.titleFilter(title)))
                        .and(serviceGame.raceFilter(race))
                        .and(serviceGame.professionFilter(profession))
                        .and(serviceGame.birthdayFilter(after, before))
                        .and(serviceGame.bannedFilter(banned))
                        .and(serviceGame.experienceFilter(minExperience, maxExperience))
                        .and(serviceGame.levelFilter(minLevel, maxLevel)), pageable)
                .getContent();
    }

    @GetMapping("/players/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer getCount(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "race", required = false) Race race,
                            @RequestParam(value = "profession", required = false) Profession profession,
                            @RequestParam(value = "after", required = false) Long after,
                            @RequestParam(value = "before", required = false) Long before,
                            @RequestParam(value = "banned", required = false) Boolean banned,
                            @RequestParam(value = "minExperience", required = false) Integer minExperience,
                            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                            @RequestParam(value = "minLevel", required = false) Integer minLevel,
                            @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        return serviceGame.getListAllRegisteredPlayers(
                Specification.where(
                        serviceGame.nameFilter(name)
                                .and(serviceGame.titleFilter(title)))
                        .and(serviceGame.raceFilter(race))
                        .and(serviceGame.professionFilter(profession))
                        .and(serviceGame.birthdayFilter(after, before))
                        .and(serviceGame.bannedFilter(banned))
                        .and(serviceGame.experienceFilter(minExperience, maxExperience))
                        .and(serviceGame.levelFilter(minLevel, maxLevel))).size();
    }
    //Создать игрока
    @PostMapping("/players")
    //@ResponseStatus(HttpStatus.OK)
    public Player createPlayer(@RequestBody Player player) {
        return serviceGame.createPlayer(player);
    }
    //Получить игрока
    @GetMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayer(@PathVariable("id") String id) {
        Long iD = serviceGame.idChecker(id);
        return serviceGame.getPlayer(iD);
    }
    //Изменение
    @PostMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player updatePlayer(@PathVariable("id") String id, @RequestBody Player player) {
        Long iD = serviceGame.idChecker(id);
        final Player request = serviceGame.getPlayer(iD);
        if (request == null) {
            throw new BadRequestException();
        }
        Player change;
        try {
            change = serviceGame.updatePlayer(iD, player);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
        return change;
    }

    //Удаление
    @DeleteMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteByID(@PathVariable("id") String id) {
        Long iD = serviceGame.idChecker(id);
        serviceGame.deleteByID(iD);
    }
}