package com.battle.snakes.util;


import com.battle.snakes.game.*;

import java.util.*;

public class SnakeUtil {

  private static final Random RANDOM = new Random();

  public static MoveType getRandomMove(List<MoveType> possibleMoves) {
    /* TODO
     * Given all possible moves, picks a random move
     * */
    int random = RANDOM.nextInt(possibleMoves.size() - 1);
    return possibleMoves.get(random);
  }

  public static boolean isInBounds(Board board, Coordinate coordinate) {
    /* TODO
     * Given the game board, calculates if a coordinate is within the board
     * */
    int coordinateX = coordinate.getX();
    int coordinateY = coordinate.getY();
    if(coordinateX <= 0 || coordinateY <= 0) {
      return false;
    } else {
      return coordinateY <= board.getHeight() && coordinateX <= board.getWidth();
    }
  }

  public static Coordinate getNextMoveCoords(MoveType moveType, Coordinate start) {
    /* TODO
     * Given the move type and the start coordinate, returns the coordinates of the next move
     * */
    int coordinateX = start.getX();
    int coordinateY = start.getY();
    switch (moveType) {
      case UP:
        coordinateY += 1;
      case DOWN:
        coordinateY -= 1;
      case RIGHT:
        coordinateX += 1;
      case LEFT:
        coordinateX -= 1;
    }
    return Coordinate.builder().x(coordinateX).y(coordinateY).build();
  }

  public static List<MoveType> getAllowedMoves(MoveRequest request) {
    /* TODO
     * Given the move request, returns a list of all the moves that do not end in the snake dieing
     * Hint: finding all the coordinates leading to the snakes death and
     * comparing it to the potential moves is a good starting point
     * */


    List<MoveType> allowedMoves = new ArrayList<>();
    Snake snake = request.getYou();
    List<Coordinate> snakePosition = snake.getBody();

    //Saa katte lauaaareni viivad moveid + teiste snakeide vastu viivad moveid

    for(MoveType moveType: MoveType.values()) {

    }

    return allowedMoves;
  }

  public static double getDistance(Coordinate first, Coordinate second) {
    /* TODO
     * Given two coordinates on a 2D grid, calculates the distance between them
     * */
    return 0;
  }

  public static MoveType getNearestMoveToTarget(Coordinate target, Coordinate current, List<MoveType> moves) {
    /* TODO
     * Given the target coordinate, the current coordinate and a list of moves, returns
     * the nearest move to the target, selected from the moves list
     * */
    return MoveType.LEFT;
  }

  public static Coordinate getNearestCoordinateToTarget(Coordinate target, List<Coordinate> coords) {
    /* TODO
     * Given the target coordinate and a list of coordinates, finds the nearest coordinate to the target
     * */
    return Coordinate.builder()
            .build();
  }
}
