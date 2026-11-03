package com.realestate.offer;

import java.util.List;
import com.realestate.common.CurrentAccount;
import com.realestate.offer.dto.*;

public interface OfferService {
    OfferResponse create(CreateOfferRequest request, CurrentAccount actor);
    OfferResponse detail(String id, CurrentAccount actor);
    List<OfferResponse> list(CurrentAccount actor, String q, String city);
    OfferResponse update(String id, UpdateOfferRequest request, CurrentAccount actor);
    void delete(String id, CurrentAccount actor);
    OfferResponse changeStatus(String id, String status, CurrentAccount actor);
}
