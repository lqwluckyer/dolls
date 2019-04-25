package com.game.sdk.dolls.dao;


import com.game.sdk.dolls.entity.ServerArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServerAreaDao extends JpaRepository<ServerArea, Long> {

    @Query(value = "from ServerArea where gameId= :gameId and serverId= :serverId")
    List<ServerArea> getServerAreaByGameId(@Param("gameId") Integer gameId, @Param("serverId") String serverId);
}
