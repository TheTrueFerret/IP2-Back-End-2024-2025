package kdg.be.backend.service;

import kdg.be.backend.domain.user.GameUser;
import kdg.be.backend.domain.Lobby;
import kdg.be.backend.domain.enums.LobbyStatus;
import kdg.be.backend.repository.GameUserRepository;
import kdg.be.backend.repository.LobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LobbyService {
    private final LobbyRepository lobbyRepository;
    private final GameUserRepository gameUserRepository;

    private static final Logger log = LoggerFactory.getLogger(LobbyService.class);

    public LobbyService(LobbyRepository lobbyRepository, GameUserRepository gameUserRepository) {
        this.lobbyRepository = lobbyRepository;
        this.gameUserRepository = gameUserRepository;
    }

    public Optional<Lobby> getLobbyById(UUID id) {
        return lobbyRepository.findLobbyById(id);
    }

    public List<Optional<Lobby>> getAllLobbies() {
        return lobbyRepository.findAllLobbies();
    }

    public Optional<Lobby> createLobby(UUID hostGameUserId, int minimumPlayers, int maximumPlayers, String joinCode) {
        return Optional.of(gameUserRepository.findById(hostGameUserId)
                .map(hostGameUser -> {
                    log.info("Game user found, continue creating a new lobby");

                    if (lobbyRepository.findLobbyByHostGameUserId(hostGameUser.getId()).isPresent()) {
                        log.error("Lobby already exists for game user");
                        throw new DataIntegrityViolationException("Lobby already exists for game user: " + hostGameUser.getUsername() + " " + hostGameUserId);
                    }

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

    public Optional<Lobby> addPlayerToLobby(UUID lobbyId, UUID playerId, String joinCode) {
        return Optional.of(lobbyRepository.findLobbyById(lobbyId)
                .map(lobby -> {
                    if (lobby.getUsers().size() >= lobby.getMaximumPlayers()) {
                        throw new DataIntegrityViolationException("Maximum player count exceeded");
                    }

                    if (!lobby.getJoinCode().equals(joinCode)) {
                        throw new DataIntegrityViolationException("Join code is not valid");
                    }

                    GameUser user = gameUserRepository.findById(playerId)
                            .orElseThrow(() -> new DataIntegrityViolationException("User not found"));

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

    public Optional<Lobby> removePlayerFromLobby(UUID lobbyId, UUID playerId) {
        return Optional.of(lobbyRepository.findLobbyById(lobbyId)
                .map(lobby -> {
                    GameUser user = gameUserRepository.findById(playerId)
                            .orElseThrow(() -> new DataIntegrityViolationException("User not found"));

                    boolean userFound = false;

                    for (Iterator<GameUser> iterator = lobby.getUsers().iterator(); iterator.hasNext(); ) {
                        GameUser gameUserToBeDeleted = iterator.next();
                        if (gameUserToBeDeleted.getId().equals(user.getId())) {
                            iterator.remove();
                            userFound = true;
                            log.info("User {} left lobby {}", lobby.getHostUser().getUsername(), lobby.getId());
                        }
                    }

                    if (!userFound) {
                        log.error("User could not leave the lobby");
                        throw new DataIntegrityViolationException("User not found in lobby");
                    }

                    return lobbyRepository.save(lobby);
                }).orElseThrow(() -> new NullPointerException("Lobby not found")));
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