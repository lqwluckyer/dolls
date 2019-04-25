package com.game.sdk.dolls.dao;


import com.game.sdk.dolls.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerDao extends JpaRepository<Player, Long> {

}
