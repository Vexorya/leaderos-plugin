package net.leaderos.shared.model.request.impl.credit;

import net.leaderos.shared.model.request.PostRequest;

import java.io.IOException;
import java.util.HashMap;

public class SetBonusRequest extends PostRequest {
    public SetBonusRequest(int amount) throws IOException {
        super("credits/bonus", new HashMap<String, String>() {{
            put("bonus", String.valueOf(amount));
        }});
    }
}
