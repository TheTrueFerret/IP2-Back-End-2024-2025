package kdg.be.backend.service;

import kdg.be.backend.domain.GameUser;
import kdg.be.backend.domain.Lobby;
import kdg.be.backend.domain.enums.LobbyStatus;
import kdg.be.backend.repository.GameRepository;
import kdg.be.backend.repository.GameUserRepository;
import kdg.be.backend.repository.LobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LobbyService {
    private final LobbyRepository lobbyRepository;
    private final GameUserRepository gameUserRepository;

    private static final Logger log = LoggerFactory.getLogger(LobbyService.class);
    private final GameRepository gameRepository;

    public LobbyService(LobbyRepository lobbyRepository, GameUserRepository gameUserRepository, GameRepository gameRepository) {
        this.lobbyRepository = lobbyRepository;
        this.gameUserRepository = gameUserRepository;
        this.gameRepository = gameRepository;
    }

    public Optional<Lobby> getLobbyById(UUID id) {
        return lobbyRepository.findLobbyById(id);
    }

    public List<Optional<Lobby>> getAllLobbies() {
        return lobbyRepository.findAllLobbies();
    }

    @Transactional
    public Optional<Lobby> createLobby(UUID hostGameUserId, int minimumPlayers, int maximumPlayers, String joinCode) {
        return Optional.of(gameUserRepository.findById(hostGameUserId)
                .map(hostGameUser -> {
                    log.info("Game user found, continue creating a new lobby");

                    lobbyRepository.findLobbyByHostUserOrGameUserId(hostGameUserId)
                            .ifPresent(oldLobby -> {
                                log.error("Lobby already exists for game user");
                                if (!removeUserFromLobby(oldLobby.getId(), hostGameUserId)) {
                                    throw new DataIntegrityViolationException("Lobby Could not be removed from GameUser");
                                }
                            });

                    List<GameUser> usersInLobby = new ArrayList<>();
                    usersInLobby.add(hostGameUser);

                    Lobby lobby = new Lobby(
                            LobbyStatus.WAITING,
                            hostGameUser,
                            usersInLobby,
                            joinCode,
                            minimumPlayers,
                            maximumPlayers
                    );
                    Lobby savedLobby = lobbyRepository.save(lobby);
                    log.info("New lobby created with ID: {}", savedLobby.getId());
                    return savedLobby;
                }).orElseThrow(() -> new NullPointerException("Game user not found")));
    }

    @Transactional
    public Optional<Lobby> addPlayerToLobbyByLobbyId(UUID lobbyId, UUID userId) {
        return Optional.of(lobbyRepository.findLobbyById(lobbyId)
                .map(lobby -> {
                    if (lobby.getUsers().size() >= lobby.getMaximumPlayers()) {
                        throw new DataIntegrityViolationException("Maximum player count exceeded");
                    }

                    GameUser user = gameUserRepository.findById(userId)
                            .orElseThrow(() -> new DataIntegrityViolationException("User not found"));

                    lobbyRepository.findLobbyByHostUserOrGameUserId(userId)
                            .ifPresent(oldLobby -> {
                                log.error("Lobby already exists for game user");
                                if (!removeUserFromLobby(oldLobby.getId(), userId)) {
                                    throw new DataIntegrityViolationException("Lobby Could not be removed from GameUser");
                                }
                            });

                    for (GameUser userInLobby : lobby.getUsers()) {
                        if (userInLobby.getId().equals(user.getId())) {
                            log.error("User could not join the lobby");
                            throw new DataIntegrityViolationException("User is already in the lobby");
                        }
                    }

                    lobby.getUsers().add(user);
                    log.info("User {} joined lobby {}", lobby.getHostUser().getUsername(), lobby.getId());
                    return lobbyRepository.save(lobby);
                }).orElseThrow(() -> new NullPointerException("Lobby not found")));
    }


    @Transactional
    public Optional<Lobby> addPlayerToLobbyByCode(UUID userId, String joinCode) {
        return Optional.of(lobbyRepository.findLobbyByJoinCode(joinCode)
                .map(lobby -> {
                    if (lobby.getUsers().size() >= lobby.getMaximumPlayers()) {
                        throw new DataIntegrityViolationException("Maximum player count exceeded");
                    }

                    if (!lobby.getJoinCode().equals(joinCode)) {
                        throw new IllegalArgumentException("Join code is not valid");
                    }

                    GameUser user = gameUserRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));


                    lobbyRepository.findLobbyByHostUserOrGameUserId(userId)
                            .ifPresent(oldLobby -> {
                                log.error("Lobby already exists for game user");
                                if (!removeUserFromLobby(oldLobby.getId(), userId)) {
                                    throw new DataIntegrityViolationException("Lobby Could not be removed from GameUser");
                                }
                            });


                    for (GameUser userInLobby : lobby.getUsers()) {
                        if (userInLobby.getId().equals(user.getId())) {
                            log.error("User could not join the lobby");
                            throw new DataIntegrityViolationException("User is already in the lobby");
                        }
                    }

                    lobby.getUsers().add(user);
                    log.info("User {} joined lobby {}", lobby.getHostUser().getUsername(), lobby.getId());
                    return lobbyRepository.save(lobby);
                }).orElseThrow(() -> new NullPointerException("Lobby not found")));
    }


    @Transactional
    public boolean removeUserFromLobby(UUID lobbyId, UUID userId) {
        Lobby lobby = lobbyRepository.findLobbyById(lobbyId)
                .orElseThrow(() -> new IllegalArgumentException("Lobby does not Exist"));

        List<GameUser> usersInLobby = lobby.getUsers();
        boolean isUserPresent = usersInLobby.stream()
                .anyMatch(userInLobby -> userInLobby.getId().equals(userId));

        if (!isUserPresent) {
            throw new IllegalArgumentException("User Not in This Lobby");
        }

        usersInLobby.removeIf(user -> user.getId().equals(userId));

        if (lobby.getHostUser().getId().equals(userId) && !usersInLobby.isEmpty()) {
            lobby.setHostUser(usersInLobby.getFirst());
        }

        lobby.setUsers(usersInLobby);
        lobbyRepository.save(lobby);

        //TODO Delete Lobby if No more Players inside (and game and everything related to it....)

        return true;
    }


    // this deletes the Lobby
    public void deleteLobby(UUID lobbyId) {
        lobbyRepository.findById(lobbyId).ifPresentOrElse(
                        existingLobby -> {
                            lobbyRepository.deleteById(lobbyId);
                        },
                        () -> {
                            log.error("Error deleting lobby");
                            throw new DataIntegrityViolationException("Error deleting lobby");
                        });
    }

    public Optional<Lobby> readyLobby(UUID lobbyId, UUID hostUserId) {
        return lobbyRepository.findLobbyById(lobbyId)
                .map(lobby -> {
                    if (lobby.getUsers().size() < lobby.getMinimumPlayers()) {
                        throw new DataIntegrityViolationException("Not enough players to ready up");
                    }

                    if (!lobby.getHostUser().getId().equals(hostUserId)) {
                        throw new IllegalStateException("Host user is not the same as the one who requested the ready");
                    }

                    lobby.setStatus(LobbyStatus.READY);
                    return lobbyRepository.save(lobby);
                });
    }
}