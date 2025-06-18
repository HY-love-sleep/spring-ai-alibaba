package com.cubigdata.sec.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cubigdata.sec.server.entity.Field;
import com.cubigdata.sec.server.mapper.FieldMapper;
import com.cubigdata.sec.server.service.IFieldService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HY-love-sleep
 * @since 2025-04-15
 */
@Service
public class FieldServiceImpl extends ServiceImpl<FieldMapper, Field> implements IFieldService {

    @Override
    public Field getFieldByName(String name) {
        LambdaQueryWrapper<Field> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Field::getFieldName, name);
        return this.getOne(lqw);
    }
}
