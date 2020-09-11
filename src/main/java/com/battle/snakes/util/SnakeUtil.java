package com.battle.snakes.util;


import com.battle.snakes.game.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class SnakeUtil {

  private static final Random RANDOM = new Random();

  public static MoveType getRandomMove(List<MoveType> possibleMoves) {
    /* TODO
     * Given all possible moves, picks a random move
     * */
    MoveType foundMove;
    int random = RANDOM.nextInt(possibleMoves.size());
    foundMove = possibleMoves.get(random);

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

  //todo write test
  public static boolean isCollidingWithSnake(Coordinate destination, MoveRequest request) {
    List<Coordinate> invalidCoordinates = new ArrayList<>();
    List<Coordinate> protagonistBody;

    protagonistBody = request.getYou().getBody();

    //add all other snake bodies to invalid moves
    for (Snake snake : request.getBoard().getSnakes()) {
      invalidCoordinates.addAll(snake.getBody());
    }
    //add protagonist body to invalid moves
    invalidCoordinates.addAll(protagonistBody);

    return invalidCoordinates
            .stream()
            .anyMatch(coordinate -> coordinate.getX().equals(destination.getX())
                    && coordinate.getY().equals(destination.getY()));
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
      if(isInBounds(request.getBoard(), moveCoordinate)
              && !isCollidingWithSnake(moveCoordinate, request)) {
          allowedMoves.add(moveType);
      }
    }
    return allowedMoves;
  }

  public static double getDistance(Coordinate first, Coordinate second) {
    /* TODO
     * Given two coordinates on a 2D grid, calculates the distance between them
     * */
    double distance;
    double a = Math.abs(first.getX() - second.getX());
    double b = Math.abs(first.getY() - second.getY());
    distance = Math.sqrt(Math.pow(a,2) + Math.pow(b,2));

    return distance;
  }

  public static MoveType getNearestMoveToTarget(Coordinate target, Coordinate current, List<MoveType> moves) {
    /* TODO
     * Given the target coordinate, the current coordinate and a list of moves, returns
     * the nearest move to the target, selected from the moves list
     * */
    Map<Double, MoveType> distances = new HashMap<>();
    Coordinate moveCoordinate;

    for(MoveType move : moves) {
      moveCoordinate = getNextMoveCoords(move,current);
      distances.put(getDistance(moveCoordinate,target),move);
    }
    return distances.get(Collections.min(distances.keySet()));
  }

  public static Coordinate getNearestCoordinateToTarget(Coordinate target, List<Coordinate> coords) {
    /* TODO
     * Given the target coordinate and a list of coordinates, finds the nearest coordinate to the target
     * */

    Map<Double, Coordinate> distances = new HashMap<>();
    for(Coordinate coordinate : coords) {
      distances.put(getDistance(coordinate, target), coordinate);
    }
    return distances.get(Collections.min(distances.keySet()));
  }

//  public static boolean isTrappingMove(Coordinate futureLocation, MoveType move, MoveRequest request) {
//    Snake futureSnake = request.getYou();
//    int i = 0;
//    while(i < 3) {
//      futureSnake.getBody().add(0, futureLocation);
//      request.setYou(futureSnake);
//      if(getAllowedMoves(request).size() < 1) {
//        return true;
//      } else {
//        for (MoveType futureMove : getAllowedMoves(request)) {
//          return isTrappingMove(getNextMoveCoords(futureMove,futureLocation), futureMove, request);
//        }
//      }
//      futureLocation = getNextMoveCoords(move, futureLocation);
//      i++;
//    }
//    return false;
//  }

    public static List<Coordinate> getOptimalFoods(MoveRequest request) {

      List<Snake> boardSnakes = new ArrayList<>();
      List<Coordinate> body = request.getYou().getBody();
      List<Coordinate> optimalFoods = new ArrayList<>();
      Coordinate hostileHead;

      for (Snake snake: request.getBoard().getSnakes()) {
        if (!snake.getId().equals(request.getYou().getId())) {
          boardSnakes.add(snake);
        }
      }
      Coordinate head = body.get(0);
      List<Coordinate> foods = request.getBoard().getFood();

      for(Coordinate food: foods) {
        for(Snake snake : boardSnakes) {
          hostileHead = snake.getBody().get(0);
          if (SnakeUtil.getDistance(hostileHead, food) > SnakeUtil.getDistance(head,food)) {
            optimalFoods.add(food);
            log.info("ENEMY:" + SnakeUtil.getDistance(hostileHead, food) + " ME:" + SnakeUtil.getDistance(head,food));
          } else if(snake.getBody().size() < body.size()
                  && SnakeUtil.getDistance(hostileHead, food) == SnakeUtil.getDistance(head,food)) {
            log.info("ENEMY:" + snake.getBody().size() + "  YOU:" + body.size());

            optimalFoods.add(food);
          }
        }
      }
      return optimalFoods;
    }
}
