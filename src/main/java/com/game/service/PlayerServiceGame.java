package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.PlayerNotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PlayerServiceGame implements ServiceGame {
    private PlayerRepository playerRepository;

    @Autowired
    public void setShipRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Player> getListAllRegisteredPlayers(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }

    @Override
    public Page<Player> getListAllRegisteredPlayers(Specification<Player> specification, Pageable sortedByName) {
        return playerRepository.findAll(specification, sortedByName);
    }

    //Создание игрока
    @Override
    public Player createPlayer(Player player) {
        checkAllData(player);
        if (player.getBanned() == null) player.setBanned(false);
        currentCharacterAndExperienceLevel(player);
        return playerRepository.saveAndFlush(player);
    }
    //Валидация поля name
    private void checkPlayerName(String name) {
        if (name == null || name.isEmpty() || name.length() > 12) throw new BadRequestException(); }
    //Валидация поля title
    private void checkPlayerTitle(String title) {
        if (title == null || title.isEmpty() || title.length() > 30) throw new BadRequestException();
    }
    //Валидация поля Race
    private void checkPlayerRace(Race race){
        if (race == null) throw new BadRequestException();
    }
    //Валидация поля Profession
    private void checkPlayerProfession(Profession profession){
        if (profession == null) throw new BadRequestException();
    }
    //Валидация поля experience
    private void checkPlayerExperience(Integer experience) {
        if (experience == null || experience < 0 || experience > 10000000) throw new BadRequestException();
    }
    //Валидация поля birthday
    private void checkPlayerBirthday(Date birthday){
        if (birthday == null) {
            throw new BadRequestException();
        }
        Calendar calendar = Calendar.getInstance();
        Date after;
        Date before;
        calendar.set(2000, Calendar.JANUARY, 01);
        after = calendar.getTime();
        calendar.set(3000, Calendar.DECEMBER, 31);
        before = calendar.getTime();
        if (birthday.getTime() < after.getTime()  || birthday.getTime() > before.getTime()) throw new BadRequestException();
    }

    private void checkAllData(Player player){
        checkPlayerName(player.getName());
        checkPlayerTitle(player.getTitle());
        checkPlayerRace(player.getRace());
        checkPlayerProfession(player.getProfession());
        checkPlayerExperience(player.getExperience());
        checkPlayerBirthday(player.getBirthday());
    }

    // Проверка валидации и конвертация ID
    @Override
    public Long idChecker(String id) {
        if (id == null || id.equals("0") || id.equals("")) {
            throw new BadRequestException();
        }
        try {
            Long iD = Long.parseLong(id);
            return iD;
        } catch (NumberFormatException e) {
            throw new BadRequestException();
        }
    }
    //Удаление игрока
    @Override
    public void deleteByID(Long id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
        } else {
            throw new PlayerNotFoundException();
        }
    }
    //Get player
    @Override
    public Player getPlayer(Long id) {
        if (playerRepository.existsById(id)) {
            return playerRepository.findById(id).get();
        } else {
            throw new PlayerNotFoundException();
        }
    }
    @Override
    public Player updatePlayer(Long id, Player player) {
        Player editPlayer = getPlayer(id);
        if (player.getName() != null) {
            checkPlayerName(player.getName());
            editPlayer.setName(player.getName());
        }
        if (player.getTitle() != null) {
            checkPlayerTitle(player.getTitle());
            editPlayer.setTitle(player.getTitle());
        }
        if (player.getRace() != null) {
            checkPlayerRace(player.getRace());
            editPlayer.setRace(player.getRace());
        }
        if (player.getProfession() != null) {
            checkPlayerProfession(player.getProfession());
            editPlayer.setProfession(player.getProfession());
        }
        if (player.getExperience() != null) {
            checkPlayerExperience(player.getExperience());
            editPlayer.setExperience(player.getExperience());
        }
        if (player.getBirthday() != null) {
            checkPlayerBirthday(player.getBirthday());
            editPlayer.setBirthday(player.getBirthday());
        }
        if (player.getBanned() != null) {
            editPlayer.setBanned(player.getBanned());
        }
        currentCharacterAndExperienceLevel(editPlayer);
        playerRepository.save(editPlayer);
        return editPlayer;
    }

    //Текущий уровень персонажа
    private void currentCharacterAndExperienceLevel(Player player) {
        player.setLevel(currentLevel(player.getExperience()));
        player.setUntilNextLevel(experienceLevel(player.getExperience(), player.getLevel()));
    }
    private Integer currentLevel(int experience){
        return (int) ((Math.sqrt(2500 + 200 * experience) - 50)/100);
    }
    private Integer experienceLevel(int experience, int level){
        return 50 * (level+1) * (level+2) - experience;
    }

    @Override
    public Specification<Player> nameFilter(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Player> titleFilter(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    @Override
    public Specification<Player> raceFilter(Race race) {
        return (root, query, criteriaBuilder) -> race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    @Override
    public Specification<Player> professionFilter(Profession profession) {
        return (root, query, criteriaBuilder) -> profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    @Override
    public Specification<Player> experienceFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);
            }
            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    @Override
    public Specification<Player> levelFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);
            }
            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }

    @Override
    public Specification<Player> birthdayFilter(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) {
                return null;
            }
            if (after == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(before));
            }
            if (before == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(after));
            }
            return criteriaBuilder.between(root.get("birthday"), new Date(after), new Date(before));
        };
    }

    @Override
    public Specification<Player> bannedFilter(Boolean banned) {
        return (root, query, criteriaBuilder) -> {
            if (banned == null) {
                return null;
            }
            if (banned) {
                return criteriaBuilder.isTrue(root.get("banned"));
            } else {
                return criteriaBuilder.isFalse(root.get("banned"));
            }
        };
    }
}