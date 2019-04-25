package com.game.sdk.dolls.utils;

import java.util.List;

public class PageUtils {
    private PageUtils(){}
    public static List builderPageParams(List params, Integer page, Integer rows) {
        int size = 20;
        int offset = 0;

        if (rows != null && rows > 0) {
            size = rows;
        }
        if (page != null && page > 0) {
            offset = (page - 1) * size;
        }
        params.add(offset);
        params.add(size);
        return params;
    }

}
