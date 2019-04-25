package com.game.sdk.dolls.dao;

import com.game.sdk.dolls.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameDao extends JpaRepository<Game,Integer> {

}
