package edmondskarp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * ������� ������ ���������� ������������� ������ � ����
 * ������� ���������� - ����� (���������� ��������� �������� - �����).
 * 
 * ����� ������������ ����� �������� ����, ����������� ������ ����������.
 * ������������� ���� - � ���� ������� ���������.
 * 
 */
public class Graph {
	/**
	 * ���� �������� ��� ������������ ��������, ��� � ��������
	 * �������� ������ � ����.
	 */
	private static class Arc {
		final int from, to;	// ����� ����
		final int capacity;	// ���������� ����������� ����
		int flow = 0;		// �������� ������ �� ����
		
		Arc(int from, int to, int capacity) {
			this.from = from;
			this.to = to;
			this.capacity = capacity;
		}
	}
	
	/**
	 * ���������� ������ �����.
	 */
	private final int count;
	
	/**
	 * �������� � ���� ����.
	 */
	final int source, sink;
	
	/**
	 * ��� ���� �����.
	 */
	private final List<Arc> arcs = new ArrayList<>();
	
	/**
	 * ������ ��������� �����.
	 */
	private final List<Arc>[] graph;
	
	/**
	 * ����������� ������ ����.
	 * @param n			����� ������ �����.
	 * @param source	��������.
	 * @param sink		����.
	 */
	@SuppressWarnings("unchecked")
	public Graph(int n, int source, int sink) {
		if (n < 2) throw new IllegalArgumentException("There should exist at least 2 nodes");
		// ������������� ������� ���������.
		graph = new List[count = n];
		for (int i = 0; i < n; i++) graph[i] = new ArrayList<Arc>();
		// ������������� ������ � �����.
		this.source = source;
		this.sink = sink;
	}
	
	/**
	 * ��������� ���� � ����. ���� �������� � ������ ��������� ������,
	 * ���� ��� ��� ������ ����, � ������ ��� - ��� ��������.
	 * @param from		������ ����.
	 * @param to		����� ����.
	 * @param capacity	���������� ����������� ����.
	 */
	public void addArc(int from, int to, int capacity) {
		if (from < 0 || to < 0 || from >= count || to >= count || capacity < 0) {
			throw new IllegalArgumentException("Invalid arc parameters");
		}
		Arc arc = new Arc(from, to, capacity);
		arcs.add(arc);
		graph[from].add(arc);
		graph[to].add(arc);
	}
	
	/**
	 * ������� ���� �� �����. ���� ������ ���� ������� �� ���� ������� ���������.
	 * @param from	������ ����.
	 * @param to	����� ����.
	 */
	public void removeArc(int from, int to) {
		// ����� ���� � ������ ���
		Arc arcToRemove = null;
		for(Arc arc : graph[from]) {
			if (arc.from == from && arc.to == to) {
				arcToRemove = arc;
				break;
			}
		}
		if (null == arcToRemove) return;
		// �������� ���� �� ������� ���������.
		graph[from].remove(arcToRemove);
		graph[to].remove(arcToRemove);
		arcs.remove(arcToRemove);
	}
	
	/**
	 * ������� ������������ ����� � ���� �� ������ ����� - ����������.
	 * ��������� �������� �������� - �����.
	 * @return	�������� ������������� ������ � ����
	 */
	public int edmondsKarp() {
		// ������������ ����� ���������.
		clearFlow();
		// ���� �� ���������� ������ � ����, ���� ������� ����
		// � ������������� ��������� �� ������ � ����� � ���������� ����.
		while(searchPath() > 0) ;
		// �������� ������������� ������ - ����� ������� ������ �� �����,
		// ��������� �� ������.
		int maxFlow = 0;
		for (Arc arc : graph[source]) {
			maxFlow += arc.flow;
		}
		return maxFlow;
	}
	
	/**
	 * �������� ����� �� ���� �����.
	 */
	private void clearFlow() {
		arcs.forEach(arc -> arc.flow = 0);
	}
	
	/**
	 * ���� ����������� ���� � ���������� ���� ����� � ������������� ���������.
	 * ����� ������� ������� ����� � ������ � ����������� � ��� ������, �����
	 * "�����" ����������� ����.
	 * @return	�������� �������������� ��������.
	 */
	private int searchPath() {
		// ������ ������ � ������. � ������ �������� ����, � �� �������,
		// ��� ��������� ����������� ������ � ����������� ������ ����� ����.
		Arc[] path = new Arc[count];
		// ������� ���������� ������. ������� ��������� ���������� � ��� ������,
		// ����� �� ������� ����������� "�����".
		boolean[] passed = new boolean[count];
		// ������� ������ ��� ������.
		LinkedList<Integer> queue = new LinkedList<>();
		
		// ����� �� ���������� ����.
		queue.addLast(source);
		passed[source] = true;
		while (!queue.isEmpty()) {
			int u = queue.removeFirst();
			for (Arc arc : graph[u]) {
				int end = -1;
				if (arc.from == u && !passed[arc.to] && arc.flow < arc.capacity) {
					// ������ ����, ����� ��������� ����� ����� ����.
					end = arc.to;
				} else if (arc.to == u && !passed[arc.from] && arc.flow > 0) {
					// �������� ����, ����� ��������� ����� ����� ����.
					end = arc.from;
				}
				if (end >= 0) {
					// ������� ����� �������. ��������� ������ ������.
					path[end] = arc;
					if (end == sink) {
						// ������ ���� � ���������� ����!
						return modifyPath(path);
					} else {
						passed[end] = true;
						queue.add(end);
					}
				}
			}
		}
		// ���� �� ������.
		return 0;
	}
	
	/**
	 * ����������� ����, ��������� � ���������� ����, � ������������ ����� ����� ����.
	 * @param p
	 * @return
	 */
	private int modifyPath(Arc[] p) {
		// ��������� ����������� ��������� ���������� ������ ����� ����.
		int addition = Integer.MAX_VALUE;
		int next = sink, pred;
		while (next != source) {
			Arc arc = p[next];
			if (arc.to == next) {
				pred = arc.from;
				if (addition > arc.capacity - arc.flow) {
					addition = arc.capacity - arc.flow;
				}
			} else {
				pred = arc.to;
				if (addition > arc.flow) {
					addition = arc.flow;
				}
			}
			next = pred;
		}
		// ���������� ��������� ������ ����� ����.
		next = sink;
		while (next != source) {
			Arc arc = p[next];
			if (arc.to == next) {
				pred = arc.from;
				arc.flow += addition;
			} else {
				pred = arc.to;
				arc.flow -= addition;
			}
			next = pred;
		}
		return addition;
	}
	
	/**
	 * ������ ������ �� ������. ������ ������ ������������ ����� ������ �����,
	 * ��������������� ���� �����: ������ ������ ������ ���� � �� ���������� �����������.
	 * ������ ���� �� ���� �������� ���.
	 * @param n			// ���������� ������ �����
	 * @param arcs		// ����� �����, �������������� ����
	 * @param source	// ����� ������� ������
	 * @param sink		// ����� ������� �����
	 * @return			// ����, ����������� �� �������� ����������.
	 */
	public static Graph build(int n, Stream<String> arcs, int source, int sink) {
		Graph g = new Graph(n, source, sink);
		arcs
			.filter(s -> s.matches("\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*"))
			.map(String::trim)
			.map((s -> s.split("\\s*,\\s*")))
			.forEach(triple -> g.addArc(Integer.parseInt(triple[0]),
					                    Integer.parseInt(triple[1]),
					                    Integer.parseInt(triple[2])));
		return g;
	}
}
