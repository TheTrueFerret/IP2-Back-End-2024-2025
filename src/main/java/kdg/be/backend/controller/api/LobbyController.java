package kdg.be.backend.controller.api;

import jakarta.validation.Valid;
import kdg.be.backend.controller.dto.game.LobbyDto;
import kdg.be.backend.controller.dto.mapper.GameUserDtoMapper;
import kdg.be.backend.controller.dto.requests.CreateJoinLobbyRequest;
import kdg.be.backend.controller.dto.requests.CreateLobbySettingsRequest;
import kdg.be.backend.controller.dto.user.GameUserDto;
import kdg.be.backend.domain.Lobby;
import kdg.be.backend.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {
    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    public LobbyDto mapToDto(Lobby lobby) {
        List<GameUserDto> gameUserDtos = lobby
                .getUsers()
                .stream()
                .map(user -> new GameUserDto(user.getUsername(), user.getId()))
                .toList();

        return new LobbyDto(lobby.getId(), lobby.getStatus(), GameUserDtoMapper.mapToDto(lobby.getHostUser()), gameUserDtos, lobby.getJoinCode(), lobby.getMinimumPlayers(), lobby.getMaximumPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LobbyDto> getLobby(@PathVariable UUID id) {
        return lobbyService.getLobbyById(id)
                .map(lobby -> ResponseEntity.ok(mapToDto(lobby)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<LobbyDto>> getAllLobbies() {
        List<Optional<Lobby>> allLobbies = lobbyService.getAllLobbies();

        List<LobbyDto> lobbyDtos = new ArrayList<>();

        allLobbies.forEach(lobby -> lobby.ifPresent(po -> lobbyDtos.add(this.mapToDto(po))));

        return ResponseEntity.ok(lobbyDtos);
    }

    // Most of these requests could be optimized by just returning a lobbyId
    /**
     * To Create a new Lobby
     * Returns: a Lobby Object
     */
    @PostMapping("/create")
    public ResponseEntity<LobbyDto> createLobby(@RequestParam UUID userId, @Valid @RequestBody CreateLobbySettingsRequest req) {
        return lobbyService.createLobby(userId, req.minimumPlayers(), req.maximumPlayers(), req.joinCode())
                .map(lobby -> ResponseEntity.status(HttpStatus.CREATED).body(this.mapToDto(lobby)))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    /**
     * For when the frontend already knows the lobbyId
     * Returns: a Lobby Object
     */
    @PatchMapping("/join/{id}")
    public ResponseEntity<LobbyDto> joinLobby(@PathVariable UUID id, @RequestParam UUID userId) {
        return lobbyService.addPlayerToLobbyByLobbyId(id, userId)
                .map(lobby -> ResponseEntity.ok(mapToDto(lobby)))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    /**
     * For When you want to join a Lobby By the LobbyCode
     * Returns: a Lobby Object
     */
    @PatchMapping("/join")
    public ResponseEntity<LobbyDto> joinLobbyWithCode(@RequestParam UUID userId, @Valid @RequestBody CreateJoinLobbyRequest req) {
        return lobbyService.addPlayerToLobbyByCode(userId, req.joinCode())
                .map(lobby -> ResponseEntity.ok(mapToDto(lobby)))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PostMapping("/leave/{id}")
    public boolean leaveLobby(@PathVariable UUID id, @RequestParam UUID userId) {
        return lobbyService.removeUserFromLobby(id, userId);
    }


    @PatchMapping("/ready/{id}")
    public ResponseEntity<LobbyDto> startGame(@PathVariable UUID id, @RequestParam UUID userId) {
        return lobbyService.readyLobby(id, userId)
                .map(lobby -> ResponseEntity.ok(mapToDto(lobby)))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }
}