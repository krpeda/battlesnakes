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

    List<Coordinate> body = request.getYou().getBody();
    List<Coordinate> optimalFoods;
    List<MoveType> moves = SnakeUtil.getAllowedMoves(request);
    List<MoveType> futureMoves;
    List<Snake> hostileSnakes = new ArrayList<>();
    Map<Integer, MoveType> optimalMoveMap = new HashMap<>();
    List<MoveType> dangerousMoves = new ArrayList<>();
    MoveType mostOptimalMove;
    Coordinate optimalFood;
    Snake futureSnake;
    MoveRequest futureRequest;


    Coordinate head = body.get(0);
    List<Coordinate> foods = request.getBoard().getFood();

    log.info(" TURN:" + request.getTurn());

    for (Snake snake: request.getBoard().getSnakes()) {
      if (!snake.getId().equals(request.getYou().getId())) {
        hostileSnakes.add(snake);
        if (snake.getBody().size() >= body.size()) {
          for(MoveType moveType : moves) {
            if(SnakeUtil.isHeadCollision(snake,foods, SnakeUtil.getNextMoveCoords(moveType,head))) {
              dangerousMoves.add(moveType);
              log.info(moveType.toString()+ ": ENEMY: " + snake.getBody().size() + " ME: " + body.size());
            }
          }
        }
      }
    }



    optimalFoods = SnakeUtil.getOptimalFoods(request,hostileSnakes);

    if (optimalFoods.size() < 1) {
      optimalFoods = foods;
    }

    optimalFood = SnakeUtil.getNearestCoordinateToTarget(head, optimalFoods);
    log.info(optimalFood.toString());

//    if (moves.size() > 1) {
//      for(MoveType moveType : moves) {
//        futureSnake = request.getYou();
//        futureSnake.getBody().add(0, SnakeUtil.getNextMoveCoords(moveType, head));
//        futureRequest = request;
//        futureRequest.setYou(futureSnake);
//        futureMoves = SnakeUtil.getAllowedMoves(futureRequest);
//
//          optimalMoveMap.put(futureMoves.size(), moveType);
//
//      }
//
//      if(optimalMoveMap.keySet().stream().distinct().count() > 1) {
//        moves.remove(optimalMoveMap.get(Collections.min(optimalMoveMap.keySet())));
//        log.info("REMOVED MOVE:" + optimalMoveMap.get(Collections.min(optimalMoveMap.keySet())).toString());
//      }
//    }

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



