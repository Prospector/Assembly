package teamreborn.assembly.powernet;

import org.apache.commons.lang3.Validate;

import java.util.*;

//A simple java graph implimentation. I have no idea what im doing so feel free to help me
public class Graph {
    private Map<Long, Set<Long>> nodes;

    public Graph() {
        nodes = new HashMap<>();
    }

    public void addLine(long start, long dest) {
        Validate.isTrue(start != dest);
        getNode(start).add(dest);
        getNode(dest).add(start);
    }

    public void removeNode(long node) {
        Set<Long> connections = getNode(node);
        connections.forEach(aLong -> getNode(aLong).remove(node));
        nodes.remove(node);
    }

    private Set<Long> getNode(long loc) {
        return nodes.computeIfAbsent(loc, k -> new HashSet<>());
    }

    public void clear() {
        nodes.clear();
    }

    public boolean isLinked(long pos1, long pos2) {
        Set<Long> visited = new HashSet<>();
        LinkedList<Long> queue = new LinkedList<>();
        visited.add(pos1);
        queue.add(pos1);

        while (!queue.isEmpty()) {
            long pos = queue.poll();
            Set<Long> connections = getNode(pos);
            for (Long check : connections) {
                if (visited.contains(check)) {
                    continue;
                }
                if (check == pos2) {
                    return true;
                }
                visited.add(check);
                queue.add(check);
            }
        }
        return false;
    }

    public Set<Long> getConnections(long pos1) {
        Set<Long> visited = new HashSet<>();
        LinkedList<Long> queue = new LinkedList<>();
        visited.add(pos1);
        queue.add(pos1);

        while (!queue.isEmpty()) {
            long pos = queue.poll();
            Set<Long> connections = getNode(pos);
            for (Long check : connections) {
                if (visited.contains(check)) {
                    continue;
                }
                visited.add(check);
                queue.add(check);
            }
        }
        return visited;
    }

    //Returns a list of networks in a graph with all the positions of each node
    public Set<Set<Long>> buildNetworkMap() {
        Set<Set<Long>> networks = new HashSet<>();
        for (Long node : nodes.keySet()) {
            boolean foundNetwork = false;
            for (Set<Long> networkMap : networks) { //TODO is there a better way?
                if (networkMap.contains(node)) {
                    foundNetwork = true;
                    break;
                }
            }
            if (foundNetwork) { //This node is part of a pre-existing network, no need to find all the connections
                continue;
            }
            Set<Long> connections = getConnections(node);
            if (!connections.isEmpty()) {
                //At this point we know the node is not part of a network that we know about
                networks.add(getConnections(node));
            }
        }
        return networks;
    }

}
