package com.game.sdk.dolls.dao;

import com.game.sdk.dolls.entity.PayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PayOrderDao extends JpaRepository<PayOrder, Long> {
    @Query(value="select * from t_pay_order where id=? and state=1 for update", nativeQuery=true)
    PayOrder getPayOrderWithLock(@Param("id") Long id);
}
