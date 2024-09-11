package com.god.dragon.mybatisplus.tool;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @implNote start with 2024/9/11 15:20
 */
public class QueryWrapperTools {

    private static final String EQ = "=";
    private static final Pattern pattern = Pattern.compile("MPGENVAL\\d+");
    /**
     * 解析QueryWrapper的SQL,获取查询主键(目前只支持单主键),多用于双层缓存中使用本地缓存
     * @param queryWrapper
     * @return
     */
    public String getKeyByQueryWrapper(AbstractWrapper<?,?,?> queryWrapper, String idColumn) {
        if(StringUtils.isEmpty(idColumn)){
            throw new IllegalArgumentException("idColumn is null");
        }
        Iterator<ISqlSegment> iterator = queryWrapper.getExpression().getNormal().iterator();
        while (iterator.hasNext()) {
            ISqlSegment sqlSegment = iterator.next();
            if (idColumn.equals(sqlSegment.getSqlSegment()) && EQ.equals(iterator.next().getSqlSegment())){
                ISqlSegment valueSegment = iterator.next();
                Matcher matcher = pattern.matcher(valueSegment.getSqlSegment());
                if(!matcher.find()){
                    break;
                }
                String mpGenVal = matcher.group();

                Map<?,?> paramNameValuePairs = queryWrapper.getParamNameValuePairs();
                return String.valueOf(paramNameValuePairs.get(mpGenVal));
            }
        }
        throw new IllegalArgumentException("cannot find idColumn");
    }
}
