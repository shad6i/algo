package edmonds;

import java.util.Arrays;
import java.util.Iterator;

import edmonds.Graph.Arc;

/**
 * ������� ������ ���������� ������������� ������ � ����
 * ������� ���������� - ����� (���������� ��������� �������� - �����).
 * 
 * ����� ������������ ����� �������� ����, ����������� ������ ����������.
 * ������������� ���� - � ���� ������� ���������.
 * 
 */
public class EdmondsKarp {
	/**
	 * ��������� ����, ������� �������� �����.
	 */
	private static class ExtArc extends Arc {
		public ExtArc(int to) {
			super(to, 0);
		}

	}
	
	private Graph graph;
	private int nVert;
	
	public EdmondsKarp(Graph g) {
		graph = g;
		nVert = g.getCount();
	}
	
	/**
	 * ���������� ��������� �������� - ����� (�� ������ ����� - ����������).
	 * ��������������� ��������� ����� �� �������� ����������� ����.
	 * ��������������, ��� ��������� ����� ����� ����. ����� �����
	 * ��������� ������ ��������� ���� ����� ����� ����� ������������ �����.
	 * @return		�������� ������������� ������ � ����.
	 */
	public int edmondsKarp() {
		int source = graph.getSource();
		int sink = graph.getSink();
		
		// ��������� �������� ����
		for (int i = 0; i < nVert; ++i) {
			for (Iterator<Arc> iArc = graph.arcs(i); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				arc.setFlow(0);
				if (graph.getArc(arc.to(), i) == null) {
					graph.addArc(arc.to(), new ExtArc(i));
				}
			}
		}
		
		// ���� ����������������� ���������� ������.
		int[] path;
		while((path = getPath(source, sink)) != null) {
			// ������ ���� � ���������� ����.
			// ��������� ����������� ���������� ����� �� ���� ����
			int min = Integer.MAX_VALUE;
			for (int curr = sink;  curr != source; ) {
				Arc arcFwd = graph.getArc(path[curr], curr);
				if (arcFwd.capacity() - arcFwd.flow() < min) {
					min = arcFwd.capacity() - arcFwd.flow();
				}
				curr = path[curr];
			}
			// ������������ ����� ����� ������ � �������� ��� �� ���� ����.
			for (int curr = sink;  curr != source; ) {
				Arc arcFwd = graph.getArc(path[curr], curr);
				Arc arcBack = graph.getArc(curr, path[curr]);
				arcFwd.changeFlow(min);
				arcBack.changeFlow(-min);
				curr = path[curr];
			}
			//graph.printNet();
			//System.out.println("-----------------------");
		}
		
		// �������� ������ ����� ����� ������� �� ��������� �� ������ �����.
		int s = 0;
		for (Iterator<Arc> iArc = graph.arcs(source); iArc.hasNext(); ) {
			s += iArc.next().flow();
		}
		
		// ������� �������� ����
		for (int i = 0; i < nVert; ++i) {
			for (Iterator<Arc> iArc = graph.arcs(i); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				if (arc instanceof ExtArc) {
					iArc.remove();
				}
			}
		}
		
		return s;
	}

	/**
	 * ����� ���� � ��������� �������������� ������� � ����.
	 * ����� ������� "� ������", ��� ���� �������, ��� ��������
	 * � ������ ������, ������������. 
	 * @param from - �����
	 * @param to - ����
	 * @return ����, ���� �� ����������, null � ��������� ������
	 */
	private int[] getPath(int from, int to) {
		// ������ ������ � ������
		int[] tree = new int[nVert];
		Arrays.fill(tree, -1);
		// ������� ������
		int[] queue = new int[nVert];
		int qHead = 0;	// ��������� �� ������ ������� � �������
		int qCount = 1;	// ���������� ��������� �������
		
		queue[0] = from;
		while(qCount > 0) {
			// �������� ������� �� �������
			int curr = queue[qHead++];
			qCount--;
			// ����������� ����, ������� �� ���� �������
			for (Iterator<Arc> iArc = graph.arcs(curr); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				int end = arc.to;
				if (tree[end] == -1 && arc.capacity() - arc.flow() > 0) {
					// ���� � ��������� ���������� ���������� ������������,
					// ������� � ��� �� ���������� �������.
					tree[end] = curr;
					if (end == to) {
						// ���� ������!
						return tree;
					}
					queue[qHead + (qCount++)] = end;
				}
			}
		}
		// ��� ���� �����������, ������ �� �������.
		return null;
	}

	/**
	 * ����������� ������� ���������� ����, �������������� ���������
	 * ���� �� 9 ����� (����� - ������� � 0, ���� - ������� � 4)
	 * @param args
	 */
	public static void main(String[] args) {
		Graph net = new Graph(9, 0, 4);

		net.addArc(0, 1, 20);
		net.addArc(0, 6, 17);
		net.addArc(1, 2, 3);
		net.addArc(1, 5, 15);
		net.addArc(2, 3, 15);
		net.addArc(3, 4, 12);
		net.addArc(5, 3, 10);
		net.addArc(5, 4, 17);
		net.addArc(5, 8, 6);
		net.addArc(6, 1, 5);
		net.addArc(6, 7, 20);
		net.addArc(7, 1, 10);
		net.addArc(7, 5, 10);
		net.addArc(8, 4, 10);
		net.addArc(8, 7, 12);

		EdmondsKarp ek = new EdmondsKarp(net);
		int maxFlow = ek.edmondsKarp();
		net.printNet();
		System.out.println("Maximum flow = " + maxFlow);
	}
}
