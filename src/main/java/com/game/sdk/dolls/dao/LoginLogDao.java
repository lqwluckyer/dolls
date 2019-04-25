package com.game.sdk.dolls.dao;

import com.game.sdk.dolls.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogDao extends JpaRepository<LoginLog, Long> {
}
