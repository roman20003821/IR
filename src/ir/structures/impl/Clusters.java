package ir.structures.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Clusters {
    private Map<Integer, Set<Integer>> data;

    public Clusters(Set<Integer> docsId, int b1) {
        data = new HashMap<>();
        prunClusters(docsId, b1);
    }

    private void prunClusters(Set<Integer> docsId, int b1) {
        ClusterPruning clusterPruning = new ClusterPruning(docsId);
        data = clusterPruning.leadersToFollowersMap(b1);
    }

    public Set<Integer> setOfLeaderAndFollowers(int leader) {
        Set<Integer> set = new HashSet<>(leader);
        set.addAll(followers(leader));
        return set;
    }

    public Set<Integer> leaders() {
        return data.keySet();
    }

    public Set<Integer> followers(int leader) {
        if (!data.containsKey(leader)) throw new IllegalArgumentException("Leader " + leader + " doesn't exist");
        return data.get(leader);
    }
}
