package prim;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import prim.Graph.Arc;

/**
 * �������� ����� ���������� ������������ ��������� ������ ���
 * �������� ������������������ ������������ �����.
 */
public class Prim {
	/**
	 * ���� ����� ������������ �����, � ������� ������ ����� ����
	 * � �� ����� ���������� ��� � ������.
	 */
	public static class Edge extends Arc {
		final int from;	// ������ ����
		
		public Edge(int from, int to, double weight) {
			super(to, weight);
			this.from = from;
		}
		
		@Override
		public String toString() {
			return "(" + from + "," + to + "," + weight + ")";
		}
	}
	
	final private Graph graph;	// ����, ��� �������� �������� ������
	final private int nVert;	// ����� ������ � �����
	
	private Set<Edge> edges = null;	// ������ �����, �������� � ������
	
	// ������� (�������������) ����������, ������������ ��� ���������� �������
	int[] positions;	// ������� ������ � ������� � �����������
	Arc[] binHeap;		// ������� � ������������ ������ (��������� - ����������)
	int heapSize;		// ������ ������� (����� ���������)
	boolean[] passed;	// ������ ����������� � ������ ������

	public Prim(Graph g) {
		graph = g;
		nVert = g.getCount();
	}
	
	/**
	 * ������ ����������� ������ �����.
	 * ���� ������ ��� �� ��������, ������������ ������ ��������� �����.
	 * @return
	 */
	public Set<Edge> getSceleton() {
		if (edges == null) {
			prim();
		}
		return edges;
	}
	
	/**
	 * ���������� ��������� ����� ���������� ������������ �������.
	 * �� �������� ��������� �������� ��������, �� ������ ���������� ��
	 * ��������� ������� ��� ���������� ����� ����������� ����� �����.
	 */
	public void prim() {
		edges = new HashSet<Edge>();		// ����� �������
		positions = new int[nVert];			// ������� ������ � ����		
		int[] tree = new int[nVert];		// ������ ����� ����� �������		
		binHeap = new Arc[nVert];			// �������� ����
		heapSize = 0;						// ������ ����
		passed = new boolean[nVert];		// ������ ���������� ������
		for (int i = 0; i < nVert; ++i) {
			passed[i] = false;
			positions[i] = -1;
			tree[i] = -1;
		}
		
		// ������������� ����
		clearHeap();
		addToHeap(new Arc(0,0));
		
		while (!emptyHeap()) {
			// ������ �������� �������� ��������� � ������� �������
			Arc minPair = extractHeap();
			int vert = minPair.to;
			passed[vert] = true;
			if (tree[vert] != -1) {
				edges.add(new Edge(tree[vert], vert, minPair.weight));
			}
			
			// ���������� ���������� ���, ������� �� ��������� �������
			for (Iterator<Arc> iArc = graph.arcs(vert); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				int end = arc.to();
				if (!passed[end]) {
					if (positions[end] == -1) {
						// ����� ������� - ��������� � ����
						addToHeap(new Arc(arc));
						tree[end] = vert;
					} else {
						// ������� ��� ���� � ����, ���������� �� ����������.
						Arc p = getFromHeap(positions[end]);
						if (arc.weight < p.weight) {
							changeHeap(positions[end], arc.weight);
							tree[end] = vert;
						}
					}
				}
			}
		}
	}
	
	//--------------------- PRIVATE ---------------------
	
	/**
	 * ��������� ��� �� �����
	 * @param arc1	������ ����
	 * @param arc2	������ ����
	 * @return
	 */
	private static int compare(Arc arc1, Arc arc2) {
		return arc1.weight < arc2.weight ? -1 : arc1.weight == arc2.weight ? arc1.to - arc2.to : 1;
	}

	/**
	 * ���������� ������� �������� � ���� � ������������ � ������������
	 * (�������������) ����������� �� ���.
	 * @param i			������� �������� � ����
	 * @param newDist	����� ����������
	 */
	private void changeHeap(int i, double newDist) {
		binHeap[i].weight = newDist;
		heapUp(i);
	}

	/**
	 * ������ � �������� ���� �� �������.
	 * @param i	������ ��������
	 * @return
	 */
	private Arc getFromHeap(int i) {
		return binHeap[i];
	}

	/**
	 * ���������� �� ���� �������� � ����������� ����������� �� ����.
	 * @return	������� � ��������� ����������� (���������� �����������).
	 */
	private Arc extractHeap() {
		Arc minPair = binHeap[0];
		positions[minPair.to] = -1;
		if (--heapSize > 0) {
			binHeap[0] = binHeap[heapSize];
			binHeap[heapSize] = null;
			positions[binHeap[0].to] = 0;
			heapDown(0);
		}
		return minPair;
	}

	/**
	 * ���������� ������ �������� � ����.
	 * @param pair	����� �������
	 */
	private void addToHeap(Arc pair) {
		binHeap[positions[pair.to] = heapSize] = pair;
		heapUp(heapSize++);
	}

	/**
	 * ������� ����.
	 */
	private void clearHeap() {
		for (int i = 0; i < heapSize; ++i) binHeap[i] = null;
		heapSize = 0;
	}

	/**
	 * ��������, ����� �� ����.
	 * @return
	 */
	private boolean emptyHeap() {
		return heapSize == 0;
	}

	/**
	 * ������������� �������� ���� � �������� �������� ����� �� ����
	 * @param i	������ ��������
	 */
	private void heapUp(int i) {
		Arc pair = binHeap[i];
		int pred = (i - 1) / 2;
		while (pred >= 0 && compare(pair, binHeap[pred]) < 0) {
			positions[binHeap[pred].to] = i;
			binHeap[i] = binHeap[pred];
			i = pred;
			if (pred == 0) break;
			pred = (i - 1) / 2;
		}
		positions[pair.to] = i;
		binHeap[i] = pair;
	}

	/**
	 * ������������� �������� ���� � �������� �������� ���� �� ����
	 * @param i	������ ��������
	 */
	private void heapDown(int i) {
		Arc pair = binHeap[i];
		int next = 2 * i + 1;
		while (next < heapSize) {
			if (next + 1 < heapSize && compare(binHeap[next+1], binHeap[next]) < 0) {
				next++;
			}
			if (compare(pair, binHeap[next]) <= 0) {
				break;
			}
			positions[binHeap[next].to] = i;
			binHeap[i] = binHeap[next];
			i = next;
			next = 2 * i + 1;
		}
		positions[pair.to] = i;
		binHeap[i] = pair;
	}
	
	/**
	 * �������� �������, �������� ������� ���� � ����������� ��� ������
	 * @param args
	 */
	public static void main(String[] args) {
		Graph g = new Graph(10);
		
		g.addEdge(0, 1, 1);
		g.addEdge(0, 3, 3);
		g.addEdge(1, 7, 4);
		g.addEdge(1, 4, 2);
		g.addEdge(1, 2, 5);
		g.addEdge(1, 9, 2);
		g.addEdge(2, 5, 1);
		g.addEdge(2, 9, 3);
		g.addEdge(3, 7, 5);
		g.addEdge(3, 8, 3);
		g.addEdge(4, 5, 4);
		g.addEdge(5, 6, 4);
		g.addEdge(5, 9, 5);
		g.addEdge(6, 7, 2);
		g.addEdge(7, 8, 1);
		
		Prim prim = new Prim(g);
		Set<Edge> sceleton = prim.getSceleton();
		System.out.println(Arrays.toString(sceleton.toArray(new Edge[0])));
		double wholeWeight = 0;
		for (Edge edge : sceleton) {
			wholeWeight += edge.weight;
		}
		System.out.println("Sceleton weight = " + wholeWeight);
	}
}
