package com.tfkfan.nettywebgame.game.model;

import com.tfkfan.nettywebgame.game.room.DefaultGameRoom;
import com.tfkfan.nettywebgame.networking.session.PlayerSession;
import com.tfkfan.nettywebgame.networking.pack.init.PlayerInitPack;
import com.tfkfan.nettywebgame.networking.pack.privat.PrivatePlayerUpdatePack;
import com.tfkfan.nettywebgame.networking.pack.update.PlayerUpdatePack;
import com.tfkfan.nettywebgame.shared.Constants;
import com.tfkfan.nettywebgame.shared.Direction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public class DefaultPlayer extends AbstractPlayer<DefaultGameRoom, PlayerInitPack, PlayerUpdatePack, PrivatePlayerUpdatePack> {
    public DefaultPlayer(Long id, DefaultGameRoom gameRoom, PlayerSession session) {
        super(id, gameRoom, session);
    }

    public void updateState(Direction direction, Boolean state) {
        movingState.put(direction, state);
        isMoving = this.movingState.containsValue(true);
    }

    @Override
    public void update() {
        double ABS_PLAYER_SPEED = Constants.ABS_PLAYER_SPEED;

        velocity.setX(isMoving && movingState.get(Direction.RIGHT) ?
                ABS_PLAYER_SPEED : (isMoving && movingState.get(Direction.LEFT) ?
                -ABS_PLAYER_SPEED : 0.0));
        velocity.setY(isMoving && movingState.get(Direction.UP) ?
                -ABS_PLAYER_SPEED : (isMoving && movingState.get(Direction.DOWN) ?
                ABS_PLAYER_SPEED : 0.0));

        position.sum(velocity);
    }

    @Override
    public PlayerUpdatePack getUpdatePack() {
        return new PlayerUpdatePack(id, position);
    }

    @Override
    public PlayerInitPack getInitPack() {
        return new PlayerInitPack(id, position);
    }

    @Override
    public PlayerInitPack init() {
        return getInitPack();
    }

    @Override
    public PrivatePlayerUpdatePack getPrivateUpdatePack() {
        return new PrivatePlayerUpdatePack(id);
    }
}
