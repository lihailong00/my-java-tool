package com.lhl.tool.exception;

import com.lhl.tool.constant.IError;
import com.lhl.tool.errorcode.BaseErrorCodeEnum;
import com.lhl.tool.model.dto.NotifyDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class NonBlockValidateException extends RuntimeException {
    private int code = BaseErrorCodeEnum.NON_BLOCK_VALIDATOR_ERROR.getCode();
    private List<NotifyDTO> notifyDTOList = new ArrayList<>();

    public NonBlockValidateException(String notifyInfo) {
        this(BaseErrorCodeEnum.NON_BLOCK_VALIDATOR_ERROR, notifyInfo);
    }

    public NonBlockValidateException(IError code, String notifyInfo) {
        NotifyDTO notifyDTO = new NotifyDTO();
        notifyDTO.setNotifyInfo(notifyInfo);
        notifyDTO.setNotifyCode(code.getCode());
        notifyDTOList.add(notifyDTO);
    }

}