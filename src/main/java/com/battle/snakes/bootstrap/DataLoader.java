package com.battle.snakes.bootstrap;

import com.battle.snakes.game.*;
import com.battle.snakes.util.SnakeUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    static final int BOARD_WIDTH = 15;
    static final int BOARD_HEIGHT = 15;
    @Override
    public void run(String... args) throws Exception {

//        Game game = Game.builder()
//                .id("3e02b354-ae29-4c3e-8c5b-26a04c764f8c")
//                .build();
//
//        List<Coordinate> food = new ArrayList<>();
//        food.add(Coordinate.builder()
//                .x(2)
//                .y(4)
//                .build()
//        );
//
//        Snake snake = createSnake(4, 3);
//        Snake snake2 = createSnake(4, 5);
//
//        List<Snake> snakes = new ArrayList<>();
//        snakes.add(snake);
//        snakes.add(snake2);
//
//        Board board = Board.builder()
//                .width(6)
//                .height(6)
//                .food(food)
//                .snakes(snakes)
//                .build();
//
//        Snake protagonist = createSnake(2,2);
//        protagonist.setName("MINA");
//
//        MoveRequest actual = MoveRequest.builder()
//                .game(game)
//                .board(board)
//                .turn(1)
//                .you(protagonist)
//                .build();
//
//        List<MoveType> moveTypes = SnakeUtil.getAllowedMoves(actual);
//
//        for (Snake oneSnake : actual.getBoard().getSnakes()) {
//            System.out.println(oneSnake);
//        }
//
//        System.out.println(SnakeUtil.getDistance(snake.getBody().get(0), snake2
//        .getBody().get(0)));
        Coordinate coordinate1 = Coordinate.builder().x(5).y(5).build();
        Coordinate coordinate2 = Coordinate.builder().x(5).y(5).build();

        System.out.println(coordinate1.equals(coordinate2));

    }

    private MoveRequest createMoveRequest(int width, int height) {

        Game game = Game.builder()
                .id("3e02b354-ae29-4c3e-8c5b-26a04c764f8c")
                .build();

        List<Coordinate> food = new ArrayList<>();
        food.add(Coordinate.builder()
                .x(2)
                .y(4)
                .build()
        );

        Snake snake = createSnake(8, 10);

        List<Snake> snakes = new ArrayList<>();
        snakes.add(snake);

        Board board = Board.builder()
                .width(width)
                .height(height)
                .food(food)
                .snakes(snakes)
                .build();

        return MoveRequest.builder()
                .game(game)
                .board(board)
                .turn(1)
                .you(createSnake(5, 5))
                .build();
    }

    private Snake createSnake(int x, int y) {

        List<Coordinate> body = new ArrayList<>();
        body.add(Coordinate.builder()
                .x(x)
                .y(y)
                .build());

        return Snake.builder()
                .body(body)
                .build();
    }

    private Snake createSnake(List<Coordinate> body) {

        return Snake.builder()
                .body(body)
                .build();
    }

    private Board createBoard(int width, int height) {

        return Board.builder()
                .width(width)
                .height(height)
                .build();
    }

    private Coordinate createCoordinate(int x, int y) {

        return Coordinate.builder()
                .x(x)
                .y(y)
                .build();
    }

    private MoveRequest createMoveRequestWithSnake(Snake snake, int width, int height) {

        MoveRequest request = createMoveRequest(width, height);
        request.setYou(snake);

        return request;
    }
}
