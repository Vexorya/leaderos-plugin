package net.leaderos.shared.model.request.impl.credit;

import net.leaderos.shared.model.request.PostRequest;

import java.io.IOException;
import java.util.HashMap;

public class CreateCouponRequest extends PostRequest {
    public CreateCouponRequest(String playerName, String key, int amount) throws IOException {
        super("credits/coupon", new HashMap<String, String>() {{
            put("target", playerName);
            put("key", key);
            put("amount", String.valueOf(amount));
        }});
    }
}
