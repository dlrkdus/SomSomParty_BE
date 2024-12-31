package com.acc.somsomparty.domain.Notification.projection;

import com.acc.somsomparty.domain.Festival.entity.Festival;
import com.acc.somsomparty.domain.Notification.enums.TokenState;

public interface FestivalTokenProjection {
    String getFcmToken();
    TokenState getTokenState();
    Festival getFestival();
}
