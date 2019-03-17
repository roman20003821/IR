package ir.structures.impl;

import java.util.*;

public class ClusterPruning {
    private Set<Integer> docIdSet;
    private Map<Integer, Set<Integer>> leadersToFollowersMap;
    private boolean setSeparated;

    public ClusterPruning(Set<Integer> docIdSet) {
        this.docIdSet = docIdSet;
        leadersToFollowersMap = new HashMap<>();
    }

    public Map<Integer, Set<Integer>> leadersToFollowersMap() {
        return leadersToFollowersMap(1);
    }

    public Map<Integer, Set<Integer>> leadersToFollowersMap(int b1) {
        if (!setSeparated)
            separateSet(b1);
        setSeparated = true;
        return leadersToFollowersMap;
    }


    private void separateSet(int b1) {
        int n = (int) Math.sqrt(docIdSet.size());
        if (b1 > docIdSet.size()) throw new IllegalArgumentException(b1 + " < leaders size: " + n);
        List<Integer> leaders = getLeaders(n);
        int counter = 0;
        int currentLeader = 0;
        for (int docId : docIdSet) {
            if (counter % n == 0) {
                currentLeader = getNextLeaderIndex(currentLeader, leaders.size());
            } else {
                addFollowerToLeaders(docId, leaders, currentLeader, b1);
            }
            ++counter;
        }
    }

    private List<Integer> getLeaders(int n) {
        List<Integer> res = new ArrayList<>();
        int counter = 0;
        for (int docId : docIdSet) {
            if (counter % n == 0)
                res.add(docId);
            ++counter;
        }
        return res;
    }

    private int getNextLeaderIndex(int currentLeaderIndex, int leadersSize) {
        return ++currentLeaderIndex >= leadersSize ? 0 : currentLeaderIndex;
    }

    private void addFollowerToLeaders(int follower, List<Integer> leaders, int currentIndex, int b1) {
        for (int i = 0; i < b1; i++) {
            addLeaderAndFollower(leaders.get(currentIndex), follower);
            currentIndex = getNextLeaderIndex(currentIndex, leaders.size());
        }
    }

    private void addLeaderAndFollower(int leader, int follower) {
        Set<Integer> followers = leadersToFollowersMap.computeIfAbsent(leader, k -> new HashSet<>());
        followers.add(follower);
    }
}
