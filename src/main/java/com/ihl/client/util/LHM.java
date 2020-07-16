package com.ihl.client.util;

import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;

import java.util.*;

/**
 * Linked HashMap
 */
public class LHM extends LinkedHashMap<String, Option> {
    public Module module;
    public LHM(Module module) {
        this.module = module;
    }

    @Override
    public Option put(String key, Option value) {
        value.module = module;
        return super.put(key, value);
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for
     * any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    @Override
    public void putAll(Map<? extends String, ? extends Option> m) {
        for (Map.Entry<? extends String, ? extends Option> entry: m.entrySet()) {
            entry.getValue().module = module;
        }
        super.putAll(m);
    }
}
