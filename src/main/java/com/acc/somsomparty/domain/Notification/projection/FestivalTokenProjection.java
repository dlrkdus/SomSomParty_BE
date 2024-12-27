package com.acc.somsomparty.domain.Notification.projection;

import com.acc.somsomparty.domain.Festival.entity.Festival;

public interface FestivalTokenProjection {
    String getFcmToken();
    Festival getFestival();
}
