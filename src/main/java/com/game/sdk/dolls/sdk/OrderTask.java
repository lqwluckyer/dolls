package com.game.sdk.dolls.sdk;

import com.game.sdk.dolls.entity.Game;
import com.game.sdk.dolls.service.PayOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(OrderTask.class);
    private final Long orderId;
    private final PayOrderService payOrderService;
    private final Game game;

    public OrderTask(Long orderId, PayOrderService payOrderService, Game game) {
        this.orderId = orderId;
        this.payOrderService = payOrderService;
        this.game = game;
    }

    @Override
    public void run() {

        return;
    }
}
