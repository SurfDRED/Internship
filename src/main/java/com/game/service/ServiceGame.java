package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ServiceGame {
    //получать список всех зарегистрированных игроков;
    List<Player> getListAllRegisteredPlayers(Specification<Player> specification);
    Page<Player> getListAllRegisteredPlayers(Specification<Player> specification, Pageable sortedByName);
    //создавать нового игрока;
    Player createPlayer(Player player);
    //редактировать характеристики существующего игрока;
    Player updatePlayer(Long id, Player player);
    //удалять игрока по id;
    void deleteByID(Long id);
    //получать игрока по id;
    Player getPlayer(Long id);
    // Проверка валидации и конвертация ID
    Long idChecker(String id);
    //получать отфильтрованный список игроков в соответствии с переданными фильтрами;
    Specification<Player> nameFilter(String name);
    Specification<Player> titleFilter(String title);
    Specification<Player> raceFilter(Race race);
    Specification<Player> professionFilter(Profession profession);
    Specification<Player> experienceFilter(Integer min, Integer max);
    Specification<Player> levelFilter(Integer min, Integer max);
    Specification<Player> birthdayFilter(Long after, Long before);
    Specification<Player> bannedFilter(Boolean banned);
}