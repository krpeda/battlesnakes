package com.battle.snakes.util;


import com.battle.snakes.game.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SnakeUtil {

  private static final Random RANDOM = new Random();

  public static MoveType getRandomMove(List<MoveType> possibleMoves) {
    MoveType foundMove;
    int random = RANDOM.nextInt(possibleMoves.size());
    foundMove = possibleMoves.get(random);

    return foundMove;
  }

  public static boolean isInBounds(Board board, Coordinate coordinate) {
    boolean isInBounds = false;
    int coordinateX = coordinate.getX();
    int coordinateY = coordinate.getY();
    if(coordinateX >= 0 && coordinateY >= 0) {
      isInBounds = coordinateY < board.getHeight() && coordinateX < board.getWidth();
    }
    return isInBounds;
  }

  public static Coordinate getNextMoveCoords(MoveType moveType, Coordinate start) {
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

    double distance;
    double a = Math.abs(first.getX() - second.getX());
    double b = Math.abs(first.getY() - second.getY());
    distance = Math.sqrt(Math.pow(a,2) + Math.pow(b,2));

    return distance;
  }

  public static MoveType getNearestMoveToTarget(Coordinate target, Coordinate current, List<MoveType> moves) {
    Map<Double, MoveType> distances = new HashMap<>();
    Coordinate moveCoordinate;

    for(MoveType move : moves) {
      moveCoordinate = getNextMoveCoords(move,current);
      distances.put(getDistance(moveCoordinate,target),move);
    }
    return distances.get(Collections.min(distances.keySet()));
  }

  public static Coordinate getNearestCoordinateToTarget(Coordinate target, List<Coordinate> coords) {
    Map<Double, Coordinate> distances = new HashMap<>();
    for(Coordinate coordinate : coords) {
      distances.put(getDistance(coordinate, target), coordinate);
    }
    return distances.get(Collections.min(distances.keySet()));
  }

  //Determine if a coordinate is occupied by a snake
  public static boolean isCollidingWithSnake(Coordinate destination, MoveRequest request) {
    List<Coordinate> friendlyBody = request.getYou().getBody();
    List<Coordinate> invalidCoordinates = new ArrayList<>();

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

  //Simulate future position to determine if
  // move would result in the snake being stuck in a tunnel
  public static boolean isTrappingMove(Coordinate futureLocation, MoveRequest request) {
    MoveType futureMove;
    List<MoveType> allowedMoves;
    boolean isTrapping = false;
    List<Coordinate> futureBody = new ArrayList<>(request.getYou().getBody());
    Snake futureSnake = Snake.builder().body(futureBody).build();
    futureSnake.getBody().add(0, futureLocation);

    while(!isTrapping) {
      allowedMoves = getAllowedMoves(MoveRequest
               .builder()
               .board(request.getBoard())
               .you(futureSnake).build());
      if(allowedMoves.size() < 1 ) {
        isTrapping = true;
      } else if (allowedMoves.size() == 1){
        futureMove = allowedMoves.get(0);
        futureLocation = getNextMoveCoords(futureMove, futureSnake.getBody().get(0));
        futureSnake.getBody().add(0, futureLocation);
      } else {
        break;
      }
    }
    return isTrapping;
  }

    //Choose optimal target foods by comparing
    // enemy size and distances to potential targets
    public static List<Coordinate> getOptimalFoods(MoveRequest request, List<Snake> hostileSnakes) {
      Coordinate hostileHead;
      double hostileDistance;
      double friendlyDistance;
      boolean isFoodOptimal;
      List<Coordinate> body = request.getYou().getBody();
      List<Coordinate> optimalFoods = new ArrayList<>();
      Coordinate head = body.get(0);
      List<Coordinate> foods = request.getBoard().getFood();

      for(Coordinate food: foods) {
        isFoodOptimal = true;
        friendlyDistance = SnakeUtil.getDistance(head,food);
        for(Snake snake : hostileSnakes) {
          hostileHead = snake.getBody().get(0);
          hostileDistance = SnakeUtil.getDistance(hostileHead, food);
          if (hostileDistance < friendlyDistance
                  || (hostileDistance == friendlyDistance && snake.getBody().size() >= body.size())) {
            isFoodOptimal = false;
            break;
          }
        }
        if(isFoodOptimal) {
          optimalFoods.add(food);
        }
      }
//      If enemy has an advantage in reaching every target then
//      pick the most distant one to avoid suicide targets
      if (optimalFoods.isEmpty()) {
        optimalFoods.add(foods
                .stream()
                .max(Comparator.comparing(food -> getDistance(food,head)))
                .orElseThrow(NoSuchElementException::new));
      }
      return optimalFoods;
    }

    //Simulate enemy movement one turn ahead to determine if a possible head-on-head collision is unfavourable
    public static boolean isHeadCollision(List<Snake> hostileSnakes,  MoveRequest request, Coordinate targetCoordinate) {
    Coordinate hostileTarget;
    MoveType hostileMove;
    List<MoveType> possibleEnemyMoves;
    boolean isCollision = false;
    List<Coordinate> food = request.getBoard().getFood();
    List<Coordinate> body = request.getYou().getBody();

    for (Snake hostileSnake : hostileSnakes) {
      if (body.size() <= hostileSnake.getBody().size()) {
        Coordinate enemyHead = hostileSnake.getBody().get(0);
        possibleEnemyMoves = Arrays
                .stream(MoveType.values())
                .collect(Collectors.toList());
        hostileTarget = getNearestCoordinateToTarget(enemyHead, food);
        hostileMove = getNearestMoveToTarget(hostileTarget,enemyHead, possibleEnemyMoves);
        isCollision = getNextMoveCoords(hostileMove,enemyHead).equals(targetCoordinate);
      }
    }
    return isCollision;
    }
}
