package com.battle.snakes.util;


import com.battle.snakes.game.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

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
    List<Coordinate> friendlyBody;

    friendlyBody = request.getYou().getBody();

    //add all snake bodies to invalid moves
    for (Snake snake : request.getBoard().getSnakes()) {
      invalidCoordinates.addAll(snake.getBody());
    }
    invalidCoordinates.addAll(friendlyBody);

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

  //TODO fix body issue
  public static boolean isTrappingMove(Coordinate futureLocation,MoveRequest request) {
    boolean isTrapping = false;
    Snake futureSnake = request.getYou();
    List<Coordinate> snakeBody = futureSnake.getBody();
    MoveType futureMove;
    List<MoveType> allowedMoves;

    futureSnake.getBody().add(0, futureLocation);
    request.setYou(futureSnake);

    while(!isTrapping) {
       allowedMoves = getAllowedMoves(request);
      if(allowedMoves.size() < 1 ) {
        isTrapping = true;
      } else if (allowedMoves.size() == 1){
        futureMove = allowedMoves.get(0);
        futureLocation = getNextMoveCoords(futureMove, futureSnake.getBody().get(0));

         futureSnake.getBody().add(0, futureLocation);
         futureSnake.getBody().remove(futureSnake.getBody().size() - 1);
        request.setYou(futureSnake);
      } else {
        break;
      }
    }
    return isTrapping;
  }

    //TODO write test
    public static List<Coordinate> getOptimalFoods(MoveRequest request, List<Snake> hostileSnakes) {

      List<Coordinate> body = request.getYou().getBody();
      List<Coordinate> optimalFoods = new ArrayList<>();
      Coordinate hostileHead;
      double hostileDistance;
      double friendlyDistance;
      boolean isFoodOptimal;

      Coordinate head = body.get(0);
      List<Coordinate> foods = request.getBoard().getFood();

      for(Coordinate food: foods) {
        isFoodOptimal = true;
        friendlyDistance = SnakeUtil.getDistance(head,food);
        for(Snake snake : hostileSnakes) {
          hostileHead = snake.getBody().get(0);
          hostileDistance = SnakeUtil.getDistance(hostileHead, food);
          if (hostileDistance < friendlyDistance
                  ||(hostileDistance == friendlyDistance && snake.getBody().size() >= body.size())) {
            isFoodOptimal = false;
            break;
          }
        }
        if(isFoodOptimal) {
          optimalFoods.add(food);
        }
      }

      if (optimalFoods.isEmpty()) {
        optimalFoods.add(foods
                .stream()
                .max(Comparator.comparing(food -> getDistance(food,head)))
                .orElseThrow(NoSuchElementException::new));
        log.info("!!!!!CHOSE DISTANT!!!!!");
      }
      return optimalFoods;
    }

    public static boolean isHeadCollision(List<Snake> hostileSnakes,  MoveRequest request, Coordinate targetCoordinate) {

    Coordinate hostileTarget;
    MoveType hostileMove;
    List<MoveType> possibleEnemyMoves;
    boolean isCollision = false;
    List<Coordinate> food = request.getBoard().getFood();
    List<Coordinate> friendlyBody = request.getYou().getBody();

    for (Snake hostileSnake : hostileSnakes) {
      log.info("FRIENDLY:" +  friendlyBody.size()+ " VS ENEMY:" + hostileSnake.getBody().size());
      if (friendlyBody.size() <= hostileSnake.getBody().size()) {
        Coordinate enemyHead = hostileSnake.getBody().get(0);
        possibleEnemyMoves = Arrays
                .stream(MoveType.values())
                .collect(Collectors.toList());
        hostileTarget = getNearestCoordinateToTarget(enemyHead, food);
        hostileMove = getNearestMoveToTarget(hostileTarget,enemyHead, possibleEnemyMoves);
        isCollision = getNextMoveCoords(hostileMove,enemyHead).equals(targetCoordinate);
      }
    }
      if(isCollision) {
        log.info("COLLISION WARNING:" + targetCoordinate);
      }
    return isCollision;
    }
}
