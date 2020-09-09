package com.battle.snakes.util;


import com.battle.snakes.game.*;

import java.util.*;
import java.util.stream.Collectors;

public class SnakeUtil {

  private static final Random RANDOM = new Random();

  public static MoveType getRandomMove(List<MoveType> possibleMoves) {
    /* TODO
     * Given all possible moves, picks a random move
     * */
    MoveType foundMove;
    if (possibleMoves.size() > 0) {
      int random = RANDOM.nextInt(possibleMoves.size());
      foundMove = possibleMoves.get(random);
    } else {
      foundMove = null;
    }
    return foundMove;
  }

  public static boolean isInBounds(Board board, Coordinate coordinate) {
    /* TODO
     * Given the game board, calculates if a coordinate is within the board
     * */
    boolean isInBounds = false;
    int coordinateX = coordinate.getX();
    int coordinateY = coordinate.getY();
    if(coordinateX >= 0 && coordinateY >= 0) {
      isInBounds = coordinateY < board.getHeight() && coordinateX < board.getWidth();
    }
    return isInBounds;
  }

  public static Coordinate getNextMoveCoords(MoveType moveType, Coordinate start) {
    /* TODO
     * Given the move type and the start coordinate, returns the coordinates of the next move
     * */
    int x = start.getX();
    int y = start.getY();
    switch (moveType) {
      case UP:
        y -= 1;
        break;
      case DOWN:
        y += 1;
        break;
      case RIGHT:
        x += 1;
        break;
      case LEFT:
        x -= 1;
        break;
    }
    return Coordinate.builder().x(x).y(y).build();
  }

  public static List<MoveType> getAllowedMoves(MoveRequest request) {
    /* TODO
     * Given the move request, returns a list of all the moves that do not end in the snake dieing
     * Hint: finding all the coordinates leading to the snakes death and
     * comparing it to the potential moves is a good starting point
     * */
    Coordinate moveCoordinate;
    List<MoveType> allowedMoves = new ArrayList<>();
    List<Coordinate> snakePosition = request.getYou().getBody();

    for(MoveType moveType: MoveType.values()) {
      moveCoordinate = getNextMoveCoords(moveType, snakePosition.get(0));
      if(isInBounds(request.getBoard(), moveCoordinate)) {
        if(!isCollidingWithSnake(moveCoordinate, request)) {
          allowedMoves.add(moveType);
        }
      }
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

  //todo write test
  public static boolean isCollidingWithSnake(Coordinate destination, MoveRequest request) {
    List<Coordinate> invalidCoordinates = new ArrayList<>();
    List<Coordinate> protagonistBody;

    protagonistBody = request.getYou().getBody();

    //add all other snake bodies to invalid moves
    for (Snake snake : request.getBoard().getSnakes()) {
        invalidCoordinates.addAll(snake.getBody());
    }
    //add protagonist body(except head) to invalid moves
    invalidCoordinates.addAll(protagonistBody.subList(1, protagonistBody.size()));

    return invalidCoordinates
            .stream()
            .anyMatch(coordinate -> coordinate.getX().equals(destination.getX())
                    && coordinate.getY().equals(destination.getY()));
  }
}
