package nukeduck.armorchroma.config;

import java.util.HashMap;

import nukeduck.armorchroma.Mergeable;

public class MergeMap<K, V extends Mergeable<V>> extends HashMap<K, V> {
    public static final long serialVersionUID = 1L;

    @Override
    public V put(K key, V value) {
        // Can't use any method that calls put itself e.g. compute or merge
        V v = get(key);

        if(v != null) {
            v.merge(value);
            return v;
        } else {
            return super.put(key, value);
        }
    }
}
