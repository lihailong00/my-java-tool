package com.lhl.tool.converter;

import com.lhl.tool.errorcode.BaseErrorCodeEnum;
import com.lhl.tool.exception.BizRuntimeException;
import com.lhl.tool.model.bo.FieldSortBO;
import com.lhl.tool.model.dto.FieldSortDTO;
import org.apache.commons.lang3.StringUtils;

public class FieldSortBOConverter {
    private FieldSortBOConverter() {}

    public static FieldSortBO toFieldSortBO(FieldSortDTO fieldSortDTO) {
        if (fieldSortDTO == null || StringUtils.isBlank(fieldSortDTO.getField()) || StringUtils.isBlank(fieldSortDTO.getDirection())) {
            return null;
        }
        String direction = fieldSortDTO.getDirection().toLowerCase();
        if (!direction.equals("asc") && !direction.equals("desc")) {
            throw new BizRuntimeException(BaseErrorCodeEnum.PARAM_ERROR);
        }
        FieldSortBO fieldSortBO = new FieldSortBO();
        fieldSortBO.setAsc(direction.equals("asc"));
        fieldSortBO.setColumn(camelToSnake(fieldSortDTO.getField()));
        return fieldSortBO;
    }

    // （小/大）驼峰格式字符串转蛇形字符串
    public static String camelToSnake(String camelCaseStr) {
        return StringUtils.uncapitalize(StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(camelCaseStr), '_')
        ).toLowerCase();
    }
}
