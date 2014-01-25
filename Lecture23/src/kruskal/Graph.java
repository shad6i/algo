package kruskal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ������������� ������������ ����� �������� ���������.
 * �������� �� ���� - ������������ ����� ("����� ����").
 */
public class Graph {
	/**
	 * ������������� ���� �����
	 */
	public static class Arc {
		double weight;	// �������� �� ����
		int to;			// ����� �������, � ������� ����� ����

		public Arc(int to, double info) {
			this.to = to; this.weight = info;
		}
		
		public Arc(Arc arc) {
			this(arc.to, arc.weight);
		}
		
		public double weight() { return weight; }
		
		public int to() { return to; }
	};

	private final List<Arc>[] lGraph;	// ������ ���������
	private final int nVertex;			// ����� ������

	/**
	 * ����������� ������� ����� � �������� ������ ������
	 * @param nVert ����� ������
	 */
	public Graph(int nVert) {
		lGraph = new List[nVert];
		for (int i = 0; i < nVert; ++i) {
			lGraph[i] = new ArrayList<Arc>();
		}
		nVertex = nVert;
	}
	
	/**
	 * ����� ������ �����
	 * @return
	 */
	public int getCount() { return nVertex; }

	/**
	 * ���������� ���� � ����. ��������������, ��� ����� ����� ���� � ����� �� ����.
	 * @param from	������ ���� (����� �������)
	 * @param to	����� ���� (����� �������)
	 * @param info	�������� �� ����
	 */
	public void addEdge(int from, int to, double info) {
		assert from < nVertex && from >= 0;
		assert to < nVertex && to >= 0;
		
		lGraph[from].add(new Arc(to, info));
		if (from != to) {
			lGraph[to].add(new Arc(from, info));
		}
	}
	
	/**
	 * �������� ���, ������� �� �������� �������
	 * @param u	�������� �������
	 * @return
	 */
	public Iterator<Arc> arcs(int u) {
		return lGraph[u].iterator();
	}
}
