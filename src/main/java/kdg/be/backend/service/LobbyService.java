package kdg.be.backend.service;

import kdg.be.backend.domain.GameUser;
import kdg.be.backend.domain.Lobby;
import kdg.be.backend.domain.enums.LobbyStatus;
import kdg.be.backend.repository.GameUserRepository;
import kdg.be.backend.repository.LobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

                    Optional<Lobby> oldLobby = lobbyRepository.findLobbyByHostGameUserId(hostGameUser.getId());
                    if (oldLobby.isPresent()) {
                        log.error("Lobby already exists for game user");

                        // Als de persoon die een nieuwe lobby probeerd aan te maken nog in een oude lobby zit.
                        // Word deze persoon verwijderd van die lobby.
                        if (!removeUserFromLobby(oldLobby.get().getId(), hostGameUserId)) {
                            return null;
                        }
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

    public boolean removeUserFromLobby(UUID lobbyId, UUID userId) {
        Optional<Lobby> lobby = lobbyRepository.findLobbyById(lobbyId);

        boolean isRemoved = false;

        if (lobby.isPresent()) {

            GameUser user = gameUserRepository.findById(userId)
                    .orElseThrow(() -> new DataIntegrityViolationException("User not found"));

            List<GameUser> updatedUsers = lobby.get().getUsers().stream()
                    .filter(userInLobby -> !userInLobby.getId().equals(userId))
                    .collect(Collectors.toList());

            if (lobby.get().getHostUser().getId().equals(userId) && !updatedUsers.isEmpty()) {
                lobby.get().setHostUser(updatedUsers.getFirst());
            }

            lobby.get().setUsers(updatedUsers);

            // Delete lobby if no users remain
            if (updatedUsers.isEmpty()) {
                deleteLobby(lobbyId);
            }
            isRemoved = true;
        } else {
            throw new IllegalArgumentException("User Cannot be Removed from a Lobby");
        }
        return isRemoved;
    }


    public void deleteLobby(UUID lobbyId) {
        try {
            Optional<Lobby> existingLobby = lobbyRepository.findById(lobbyId);

            if (existingLobby.isPresent()) {
                lobbyRepository.deleteById(lobbyId);
            }
        } catch (Exception e) {
            log.error("Error deleting lobby: {}", e.getMessage());
            throw new DataIntegrityViolationException("Error deleting lobby");
        }
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