package kdg.be.backend.controller.api;

import jakarta.validation.Valid;
import kdg.be.backend.controller.dto.GameUserDto;
import kdg.be.backend.controller.dto.LobbyDto;
import kdg.be.backend.controller.dto.mapper.GameUserDtoMapper;
import kdg.be.backend.controller.dto.requests.CreateJoinLobbyRequest;
import kdg.be.backend.controller.dto.requests.CreateLobbySettingsRequest;
import kdg.be.backend.domain.Lobby;
import kdg.be.backend.service.LobbyService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                .map(user -> new GameUserDto(user.getUsername(), user.getAvatar()))
                .toList();

        return new LobbyDto(lobby.getStatus(), GameUserDtoMapper.mapToDto(lobby.getHostUser()), gameUserDtos, lobby.getJoinCode(), lobby.getMinimumPlayers(), lobby.getMaximumPlayers());
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

    @PostMapping("/create")
    public ResponseEntity<LobbyDto> createLobby(@RequestParam UUID userId, @Valid @RequestBody CreateLobbySettingsRequest req) {
        return lobbyService.createLobby(userId, req.minimumPlayers(), req.maximumPlayers(), req.joinCode())
                .map(lobby -> ResponseEntity.status(HttpStatus.CREATED).body(this.mapToDto(lobby)))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PatchMapping("/join/{id}")
    public ResponseEntity<LobbyDto> joinLobby(@PathVariable UUID id, @RequestParam UUID userId, @Valid @RequestBody CreateJoinLobbyRequest req) {
        return lobbyService.addPlayerToLobby(id, userId, req.joinCode())
                .map(lobby -> ResponseEntity.ok(mapToDto(lobby)))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PatchMapping("/leave/{id}")
    public ResponseEntity<LobbyDto> leaveLobby(@PathVariable UUID id, @RequestParam UUID userId) {
        return lobbyService.removePlayerFromLobby(id, userId)
                .map(lobby -> ResponseEntity.ok(mapToDto(lobby)))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PatchMapping("/start/{id}")
    public ResponseEntity<LobbyDto> startGame(@PathVariable UUID id) {
        return lobbyService.readyLobby(id)
                .map(lobby -> ResponseEntity.ok(mapToDto(lobby)))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerExceptionn(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
