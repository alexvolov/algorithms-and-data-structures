package com.alexvolov.ads.ds.impl;

import com.alexvolov.ads.ds.Graph;
import com.alexvolov.ads.ds.common.GraphEdge;
import com.alexvolov.ads.ds.common.GraphType;

import java.util.*;

import static com.alexvolov.ads.ds.common.GraphType.SIMPLE_DIRECTED;
import static com.alexvolov.ads.ds.common.GraphType.SIMPLE_UNDIRECTED;
import static com.alexvolov.ads.ds.common.GraphType.WEIGHTED_DIRECTED;
import static com.alexvolov.ads.ds.common.GraphType.WEIGHTED_UNDIRECTED;

/**
 * Simple implementation of a graph data structure that is based on adjacency list.
 * My version doesn't support resizing and two vertices can't have more than one edge.
 *
 * @author Alexander Volov (mailto: alevolov@gmail.com)
 * @version 14.02.15
 */
public class AdjacencyList implements Graph {

    private Map<Integer, Map<Integer, Integer>> list;
    private int size;
    private GraphType graphType;
    private int numberOfEdges;
    private Set<Integer> startNodes;
    private Map<GraphEdge, Integer> edges;

    /**
     * Constrictor which creates a new instance of adjacency list.
     *
     * @param size number of vertices.
     * @param graphType type of the graph.
     */
    public AdjacencyList(int size, GraphType graphType) {
        if (size < 1) {
            throw new IllegalArgumentException("Number of vertices must be grater than zero.");
        }

        this.edges = new HashMap<GraphEdge, Integer>();
        this.size = size;
        this.list = new HashMap<Integer, Map<Integer, Integer>>();
        this.startNodes = new HashSet<Integer>();
        this.graphType = graphType;
        this.numberOfEdges = 0;

        for (int i = 0 ; i < size ; i++) {
            list.put(i, new HashMap<Integer, Integer>());
            startNodes.add(i);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEdge(int i, int j) {
        if (graphType != SIMPLE_DIRECTED && graphType != SIMPLE_UNDIRECTED) {
            throw new UnsupportedOperationException("This operation is not supported for this graphType of graph.");
        }

        if (i > size - 1 || j > size - 1) {
            throw new IllegalArgumentException("Vertex is not found in matrix.");
        }

        edges.put(new GraphEdge(i, j), null);
        final Map<Integer, Integer> origin = list.get(i);
        origin.put(j, 1);
        final Map<Integer, Integer> destination = list.get(j);
        if (!destination.containsKey(i)) {
            if (graphType == SIMPLE_DIRECTED) {
                destination.put(i, -1);
            } else {
                destination.put(i, 1);
            }
        }
        startNodes.remove(j);
        numberOfEdges++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEdge(int i, int j, Integer weight) {
        if (graphType != WEIGHTED_DIRECTED && graphType != WEIGHTED_UNDIRECTED) {
            throw new UnsupportedOperationException("This operation is not supported for this graphType of graph.");
        }

        if (i > size - 1 || j > size - 1) {
            throw new IllegalArgumentException("Vertex is not found in matrix.");
        }

        if (null == weight) {
            throw new IllegalArgumentException("Weight cannot be null.");
        }

        if (weight < 0 && graphType == WEIGHTED_DIRECTED) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }

        edges.put(new GraphEdge(i, j), weight);
        final Map<Integer, Integer> origin = list.get(i);
        origin.put(j, weight);
        final Map<Integer, Integer> destination = list.get(j);
        if (!destination.containsKey(i)) {
            if (graphType == WEIGHTED_DIRECTED) {
                destination.put(i, weight * (-1));
            } else {
                destination.put(i, weight);
            }
        }
        startNodes.remove(j);
        numberOfEdges++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Integer> getNeighbours(int vertex) {
        Set<Integer> result = new HashSet<Integer>();
        final Map<Integer, Integer> vertexObj = list.get(vertex);
        if (null != vertexObj) {
            for (Integer e : vertexObj.keySet()) {
                if (vertexObj.get(e) >= 0) {
                    result.add(e);
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAdjacent(int i, int j) {
        boolean res = false;
        final Map<Integer, Integer> origin = list.get(i);
        if (null != origin) {
            res = origin.containsKey(j);
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEdge(int i, int j) {
        if (i > size - 1 || j > size - 1) {
            throw new IllegalArgumentException("Vertex is not found in matrix.");
        }
        edges.remove(new GraphEdge(i, j));
        final Map<Integer, Integer> origin = list.get(i);
        final Map<Integer, Integer> destination = list.get(j);
        origin.remove(j);
        destination.remove(i);
        numberOfEdges--;
        if (!hasParents(j)) {
            startNodes.add(j);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Integer> getStartNodes() {
        return startNodes;
    }

    /**
     * Returns a copy of adjacency list.
     *
     * @return adjacency list;
     */
    public Map<Integer, Map<Integer, Integer>> getList() {
        Map<Integer, Map<Integer, Integer>> copy = new HashMap<Integer, Map<Integer, Integer>>();
        Map<Integer, Integer> originalEntry;
        for (int i = 0 ; i < size ; i++) {
            originalEntry = list.get(i);
            Map<Integer, Integer> copyEntry = new HashMap<Integer, Integer>();
            for (Integer key : originalEntry.keySet()) {
                copyEntry.put(key, originalEntry.get(key));
            }
            copy.put(i, copyEntry);
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasParents(Integer v) {
        boolean result = false;
        final Map<Integer, Integer> destination = list.get(v);
        for (Integer key : destination.keySet()) {
            if (destination.get(key) < 0) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphType getType() {
        return graphType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWeight(int source, int destination) {
        final Map<Integer, Integer> origin = list.get(source);
        return origin.get(destination);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<GraphEdge, Integer> getEdges() {
        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addVertex() {
        size++;
        list.put(size - 1, new HashMap<Integer, Integer>());
        startNodes.add(size - 1);
        return size - 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeWeight(int source, int destination, Integer weight) {
        if (graphType != WEIGHTED_DIRECTED) {
            throw new UnsupportedOperationException("This operation is supported on weighted and directed graph.");
        }

        if (source > size - 1 || destination > size - 1) {
            throw new IllegalArgumentException("Vertex is not found in matrix.");
        }
        GraphEdge edge = new GraphEdge(source, destination);
        if (!edges.containsKey(edge)) {
            throw new IllegalArgumentException("Such edge does not exist.");
        }

        if (null == weight) {
            throw new IllegalArgumentException("Weight cannot be null.");
        }

        if (weight < 0 && graphType == WEIGHTED_DIRECTED) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }

        edges.put(edge, weight);
        Map<Integer, Integer> o = list.get(source);
        o.put(source, weight);
        Map<Integer, Integer> d = list.get(destination);
        d.put(source, weight * (-1));
    }

}
