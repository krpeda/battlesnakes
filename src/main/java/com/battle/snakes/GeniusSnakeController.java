package com.battle.snakes;

import com.battle.snakes.game.*;
import com.battle.snakes.util.SnakeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/genius")
public class GeniusSnakeController extends BaseController {

  @RequestMapping(value = "/start", method = RequestMethod.POST, produces = "application/json")
  public StartResponse start(@RequestBody StartRequest request) {

//    log.info(request.getBoard().getSnakes().toString());

    return StartResponse.builder()
      .color("cf2400")
      .headType(HeadType.FANG.getValue())
      .tailType(TailType.FAT_RATTLE.getValue())
      .build();
  }

  @RequestMapping(value = "/end", method = RequestMethod.POST)
  public Object end(@RequestBody EndRequest request) {
    return new HashMap<String, Object>();
  }

  @RequestMapping(value = "/move", method = RequestMethod.POST, produces = "application/json")
  public MoveResponse move(@RequestBody MoveRequest request) {
    List<Coordinate> optimalFoods;
    MoveType mostOptimalMove;
    Coordinate futureLocation;
    Coordinate optimalFood;
    Snake futureSnake;

    List<Coordinate> body = request.getYou().getBody();
    Coordinate head = body.get(0);
    List<MoveType> moves = SnakeUtil.getAllowedMoves(request);
    List<Snake> hostileSnakes = new ArrayList<>();
    List<MoveType> dangerousMoves = new ArrayList<>();
    List<Coordinate> food = request.getBoard().getFood();

    log.info("|TURN|:" + request.getTurn());
    for (Snake snake: request.getBoard().getSnakes()) {
      if (!snake.getId().equals(request.getYou().getId())) {
        hostileSnakes.add(snake);
      }
    }

    //Get the most optimal targets
    optimalFoods = SnakeUtil.getOptimalFoods(request,hostileSnakes);

    if (optimalFoods.size() < 1) {
      optimalFoods = food;
    }
    optimalFood = SnakeUtil.getNearestCoordinateToTarget(head, optimalFoods);
    log.info("TARGET:" + optimalFood);

    //Determine if any moves are potentially trapping or dangerous
    if (!moves.isEmpty()) {
      for(MoveType moveType : moves) {
        futureLocation = SnakeUtil.getNextMoveCoords(moveType, head);
        futureSnake = Snake.builder().body(body).build();
        //Move head
        futureSnake.getBody().add(0,futureLocation);
          if(SnakeUtil.isHeadCollision(hostileSnakes, request, futureLocation)
                  || SnakeUtil.isTrappingMove(request.getBoard(), futureSnake)) {
            dangerousMoves.add(moveType);
          }
      }
    }
    log.info("DANGEROUS MOVES: " + dangerousMoves);
    moves.removeAll(dangerousMoves);

    if (moves.isEmpty()) {
      return MoveResponse.builder()
              .move(MoveType.LEFT.getValue())
              .build();
    }

    mostOptimalMove = SnakeUtil.getNearestMoveToTarget(optimalFood, head, moves);
    return MoveResponse.builder()
            .move(mostOptimalMove.getValue())
            .build();
  }
}



