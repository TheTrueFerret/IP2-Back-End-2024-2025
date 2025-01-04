INSERT INTO playing_field (id)
VALUES ('00000000-0000-0000-0000-000000000001'),
       ('00000000-0000-0000-0000-000000000019');

-- Insert into TileSet
INSERT INTO tile_set (id, start_coordinate, end_coordinate, grid_row, playing_field_id)
VALUES ('00000000-0000-0000-0000-000000000002', 0, 10, 2, '00000000-0000-0000-0000-000000000001'),
       ('00000000-0000-0000-0000-000000000003', 11, 20, 1, '00000000-0000-0000-0000-000000000001');

INSERT INTO tile_pool (id)
VALUES ('00000000-0000-0000-0000-000000000010');

-- Insert into Deck
INSERT INTO deck (id)
VALUES ('00000000-0000-0000-0000-000000000012'),
       ('00000000-0000-0000-0000-000000000013');

-- Insert into Tile
INSERT INTO tile (id, number_value, tile_color, tile_set_id, tile_pool_id, grid_column, grid_row, deck_id)
VALUES ('00000000-0000-0000-0000-000000000004', 1, 'BLUE', '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000010', 5, 4, '00000000-0000-0000-0000-000000000012'),
       ('00000000-0000-0000-0000-000000000005', 2, 'RED', '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000010', 5, 3, '00000000-0000-0000-0000-000000000012'),
       ('00000000-0000-0000-0000-000000000006', 3, 'BLACK', '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000010', 6, 7, '00000000-0000-0000-0000-000000000013'),
       ('00000000-0000-0000-0000-000000000007', 4, 'ORANGE', '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000010', 6, 9, '00000000-0000-0000-0000-000000000013');

INSERT INTO tile (id, number_value, tile_color, grid_row, grid_column, tile_pool_id, deck_id)
VALUES ('00000000-0000-0000-0000-000000000055', 10, 'BLUE', 0, 0, '00000000-0000-0000-0000-000000000010',
        '00000000-0000-0000-0000-000000000012');

INSERT INTO tile (id, number_value, tile_color, grid_row, grid_column)
VALUES ('00000000-0000-0000-0000-000000000056', 10, 'BLUE', 0, 0),
       ('00000000-0000-0000-0000-000000000057', 35, 'RED', 0, 0), --voor testing
       ('00000000-0000-0000-0000-000000000058', 35, 'ORANGE', 0, 0),
       ('00000000-0000-0000-0000-000000000059', 25, 'BLACK', 0, 0),
       ('00000000-0000-0000-0000-000000000060', 5, 'BLUE', 0, 0),
       ('00000000-0000-0000-0000-000000000061', 5, 'BLACK', 0, 0),
       ('00000000-0000-0000-0000-000000000062', 5, 'ORANGE', 0, 0),
       ('00000000-0000-0000-0000-000000000063', 5, 'RED', 0, 0);
-- Insert into GameUser
INSERT INTO game_user (id, username, avatar)
VALUES ('00000000-0000-0000-0000-000000000008', 'Player1', 'avatar1.png'),
       ('00000000-0000-0000-0000-000000000009', 'Player2', 'avatar2.png'),
       ('fbe4a1d1-1c44-49b8-911f-7bc77a78b001', 'Player3', 'avatar3.png'),
       ('4e861d2e-5f89-47b1-91e4-a3aef9c97b02', 'Player4', 'avatar4.png'),
       ('87afee3d-2c6b-4876-8f2b-9e1d6f41c503', 'Player5', 'avatar5.png'),
       ('c4a2fa67-6a4d-4d9b-9c59-4f96b6fbc104', 'Player6', 'avatar6.png'),
       ('d61e872f-7784-4e27-996b-cad743916105', 'Player7', 'avatar7.png'),
       ('1c14c66a-b034-4531-a1e2-dfb07e7f5706', 'Player8', 'avatar8.png'),
       ('1c14c66a-b034-4531-a1e2-dfb07e7f5707', 'Player9', 'avatar9.png'),
       ('11111111-1111-1111-1111-111111111111', 'Player10', 'avatar8.png'),
       ('11111111-1111-1111-1111-111111111112', 'Player11', 'avatar9.png'),
       ('11111111-1111-1111-1111-111111111113', 'Player12', 'avatar8.png'),
       ('11111111-1111-1111-1111-111111111114', 'Player13', 'avatar9.png'),
       ('11111111-1111-1111-1111-111111111115', 'Player14', 'avatar8.png'),
       ('11111111-1111-1111-1111-111111111116', 'Player15', 'avatar9.png'),
       ('00000000-0000-0000-0000-000000000010', 'Player16', 'avatar10.png');


----------------------------------------------------------------------------------------------------------------------

INSERT INTO lobby (id, status, host_user_id, minimum_players, maximum_players, join_code)
VALUES ('a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006', 'READY', 'fbe4a1d1-1c44-49b8-911f-7bc77a78b001', 2, 6,
        'JOIN123'), -- Host is Player3
       ('ef673b41-d76d-4b96-99d8-41beef0c3707', 'WAITING', 'd61e872f-7784-4e27-996b-cad743916105', 2, 2,
        'JOINME'),  -- Host is Player7
       ('21111111-1111-1111-1111-111111111111', 'READY', '11111111-1111-1111-1111-111111111111', 2, 2,
        'JOIN1236'),
       ('31111111-1111-1111-1111-111111111111', 'READY', '11111111-1111-1111-1111-111111111113', 2, 2,
        'JOIN1235'),
       ('41111111-1111-1111-1111-111111111111', 'READY', '11111111-1111-1111-1111-111111111115', 2, 2,
        'JOIN1234');

INSERT INTO game (id, turn_time, start_tile_amount, date_time, playing_field_id,
                  tile_pool_id, lobby_id)
VALUES ('00000000-0000-0000-0000-000000000011', 60, 14, now(), '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000010', 'a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006');

-- Insert into Player
INSERT INTO player (id, game_user_id, deck_id, game_id, score)
VALUES ('00000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000008',
        '00000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000011', 10),
       ('00000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000009',
        '00000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000011', 10);

INSERT INTO lobby_users (lobby_id, users_id)
VALUES
-- Gebruikers voor Lobby 1
('a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006', 'fbe4a1d1-1c44-49b8-911f-7bc77a78b001'), -- Player3 (host)
('a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006', '4e861d2e-5f89-47b1-91e4-a3aef9c97b02'), -- Player4
('a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006', '87afee3d-2c6b-4876-8f2b-9e1d6f41c503'), -- Player5
('a1e4c8d3-9f3b-4c8e-85ba-7fcf1eb8d006', 'c4a2fa67-6a4d-4d9b-9c59-4f96b6fbc104'), -- Player6

-- Gebruikers voor Lobby 2
('ef673b41-d76d-4b96-99d8-41beef0c3707', 'd61e872f-7784-4e27-996b-cad743916105'), -- Player7 (host)
('ef673b41-d76d-4b96-99d8-41beef0c3707', '1c14c66a-b034-4531-a1e2-dfb07e7f5706'), -- Player8

-- Gebruikers voor Lobby 3
('21111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111'), -- Player7 (host)
('21111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111112'), -- Player8

-- Gebruikers voor Lobby 4
('31111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111113'), -- Player3 (host)
('31111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111114'), -- Player8

-- Gebruikers voor Lobby 5
('41111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111115'), -- Player5 (host)
('41111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111116'); -- Player8

INSERT INTO achievement (id, title, description)
VALUES (1, 'First Move', 'Complete your first move'),
       (2, 'Participation', 'Play 10 games');

-- Insert friend requests
INSERT INTO friend_request (id, sender_id, receiver_id, status)
VALUES
-- Friend request already exists
('00000000-0000-0000-0000-000000000001', 'fbe4a1d1-1c44-49b8-911f-7bc77a78b001', '4e861d2e-5f89-47b1-91e4-a3aef9c97b02',
 'PENDING'),
-- Friend request is not pending
('00000000-0000-0000-0000-000000000002', 'fbe4a1d1-1c44-49b8-911f-7bc77a78b001', '87afee3d-2c6b-4876-8f2b-9e1d6f41c503',
 'ACCEPTED');

INSERT INTO game_user_friend_list (friend_list_id, game_user_id)
VALUES ('fbe4a1d1-1c44-49b8-911f-7bc77a78b001', '87afee3d-2c6b-4876-8f2b-9e1d6f41c503'),
       ('87afee3d-2c6b-4876-8f2b-9e1d6f41c503', 'fbe4a1d1-1c44-49b8-911f-7bc77a78b001');

-- Insert into GameStat
INSERT INTO game_stat (game_id, game_name, year_published, min_players, max_players, play_time, min_age, board_game_honor, mechanics)
VALUES ('00000000-0000-0000-0000-000000000020', 'Rummikub', 2023, 2, 4, 60, 12, 1, 'Strategy');

-- Insert into Prediction
INSERT INTO prediction (id, game_stat_id, prediction_date, rating_average, complexity_average, owned_users)
VALUES ('00000000-0000-0000-0000-000000000021', '00000000-0000-0000-0000-000000000020', '2023-10-01T12:00:00', 8.5, 3.2, 1000);