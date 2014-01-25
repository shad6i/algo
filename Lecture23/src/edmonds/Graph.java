package edmonds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ������������� ���� �������� ���������.
 * �������� �� ���� - ����� ����� ("���������� ����������� ����").
 */
public class Graph {
	/**
	 * ������������� ���� �����
	 */
	public static class Arc {
		int to;			// ����� �������, � ������� ����� ����
		int capacity;	// ���������� ����������� ����
		int flow;		// ����� �� ����

		public Arc(int to, int capacity, int flow) {
			this.to = to;
			this.capacity = capacity;
			this.flow = flow;
		}
		
		public Arc(int to, int capacity) {
			this(to, capacity, 0);
		}
		
		public Arc(Arc arc) {
			this(arc.to, arc.capacity, arc.flow);
		}
		
		public int to() { return to; }
		
		public int capacity() { return capacity; }
		
		public int flow() { return flow; }
		
		public void setFlow(int flow) { this.flow = flow; }
		
		public void changeFlow(int delta) { flow += delta; }
		
		public String toString() {
			return "-> " + flow + "/" + capacity + " -> " + to;
		}
	};

	private final List<Arc>[] lGraph;	// ������ ���������
	private final int nVertex;			// ����� ������
	private int source;					// �����
	private int sink;					// ����

	/**
	 * ����������� ������� ����� � �������� ������ ������
	 * @param nVert ����� ������
	 */
	public Graph(int nVert, int source, int sink) {
		lGraph = new List[nVert];
		for (int i = 0; i < nVert; ++i) {
			lGraph[i] = new ArrayList<Arc>();
		}
		nVertex = nVert;
		this.source = source;
		this.sink = sink;
	}
	
	/**
	 * ����� ������ �����
	 * @return
	 */
	public int getCount() { return nVertex; }
	
	/**
	 * ����� � ����
	 * @return	����� ������� ������
	 */
	public int getSource() { return source; }
	
	/**
	 * ���� � ����
	 * @return	����� ������� �����
	 */
	public int getSink() { return sink; }

	/**
	 * ���������� ���� � ����. ��������������, ��� ����� ����� ���� � ����� �� ����.
	 * @param from	������ ���� (����� �������)
	 * @param to	����� ���� (����� �������)
	 * @param capacity	�������� �� ����
	 */
	public void addArc(int from, int to, int capacity) {
		assert to < nVertex && to >= 0;
		assert capacity >= 0;
		
		addArc(from, new Arc(to, capacity));
	}
	
	/**
	 * ���������� ���� � ����. ��������������, ��� ����� ����� ���� � ����� �� ����.
	 * @param from	������ ���� (����� �������)
	 * @param arc	����������� ����
	 */
	public void addArc(int from, Arc arc) {
		assert from < nVertex && from >= 0;
		
		lGraph[from].add(arc);
	}
	
	/**
	 * �������� ���, ������� �� �������� �������
	 * @param u	�������� �������
	 * @return
	 */
	public Iterator<Arc> arcs(int u) {
		return lGraph[u].iterator();
	}
	
	/**
	 * ���� � ����� ���� � ��������� ������� � ������.
	 * @param from	������ ���� (����� �������)
	 * @param to	����� ���� (����� �������)
	 * @return		����, ���� ��� ����, ��� null, ���� ���� �����������.
	 */
	public Arc getArc(int from, int to) {
		for (Iterator<Arc> iArc = arcs(from); iArc.hasNext(); ) {
			Arc arc = iArc.next();
			if (arc.to == to) return arc;
		}
		return null;
	}
	
	/**
	 * ���������� ������� ������ ���� ����
	 */
	public void printNet() {
		for (int i = 0; i < nVertex; ++i) {
			for (Iterator<Arc> iArc = arcs(i); iArc.hasNext(); ) {
				System.out.println(i + " " + iArc.next());
			}
		}
	}
}
