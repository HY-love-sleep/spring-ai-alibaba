package com.cubigdata.sec.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cubigdata.sec.server.entity.Field;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HY-love-sleep
 * @since 2025-04-15
 */
public interface IFieldService extends IService<Field> {
    Field getFieldByName(String name);
}
