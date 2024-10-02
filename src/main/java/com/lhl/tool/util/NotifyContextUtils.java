package com.lhl.tool.util;

import com.lhl.tool.model.dto.NotifyDTO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public final class NotifyContextUtils {
    private static ThreadLocal<List<NotifyDTO>> notifyDTOsThreadLocal = new ThreadLocal<>();

    private NotifyContextUtils() {
        // do nothing
    }

    public static List<NotifyDTO> getNotifyDTOs() {
        return notifyDTOsThreadLocal.get();
    }

    public static void addNotifyDTO(NotifyDTO notifyDTO) {
        List<NotifyDTO> notifyDTOs = notifyDTOsThreadLocal.get();

        if (CollectionUtils.isEmpty(notifyDTOs)) {
            notifyDTOs = new ArrayList<>();
        }

        notifyDTOs.add(notifyDTO);

        notifyDTOsThreadLocal.set(notifyDTOs);
    }

    public static void removeThreadLocal() {
        notifyDTOsThreadLocal.remove();
    }


}