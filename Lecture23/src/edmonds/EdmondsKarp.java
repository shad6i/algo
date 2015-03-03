﻿package edmonds;

import java.util.Arrays;
import java.util.Iterator;

import edmonds.Graph.Arc;

/**
 * Решение задачи нахождения максимального потока в сети
 * методом Фалкерсона - Форда (реализация алгоритма Эдмондса - Карпа).
 * 
 * Класс представляет собой описание сети, нагруженной целыми значениями.
 * Представление сети - в виде списков смежности.
 * 
 */
public class EdmondsKarp {
	/**
	 * Фиктивная дуга, несущая обратный поток.
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
	 * Реализация алгоритма Эдмондса - Карпа (по методу Форда - Фалкерсона).
	 * Последовательно увеливаем поток на величину остаточного пути.
	 * Предполагается, что начальный поток равен нулю. Тогда после
	 * окончания работы алгоритма дуги графа будут нести максимальный поток.
	 * @return		Величина максимального потока в сети.
	 */
	public int edmondsKarp() {
		int source = graph.getSource();
		int sink = graph.getSink();
		
		// Добавляем обратные дуги
		for (int i = 0; i < nVert; ++i) {
			for (Iterator<Arc> iArc = graph.arcs(i); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				arc.setFlow(0);
				if (graph.getArc(arc.to(), i) == null) {
					graph.addArc(arc.to(), new ExtArc(i));
				}
			}
		}
		
		// Цикл последовательного увеличения потока.
		int[] path;
		while((path = getPath(source, sink)) != null) {
			// Найден путь в остаточной сети.
			// Вычисляем минимальный остаточный поток на этом пути
			int min = Integer.MAX_VALUE;
			for (int curr = sink;  curr != source; ) {
				Arc arcFwd = graph.getArc(path[curr], curr);
				if (arcFwd.capacity() - arcFwd.flow() < min) {
					min = arcFwd.capacity() - arcFwd.flow();
				}
				curr = path[curr];
			}
			// Корректируем поток вдоль прямых и обратных дуг на этом пути.
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
		
		// Величина потока равна сумме потоков на исходящих из истока дугах.
		int s = 0;
		for (Iterator<Arc> iArc = graph.arcs(source); iArc.hasNext(); ) {
			s += iArc.next().flow();
		}
		
		// Убираем обратные дуги
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
	 * Поиск пути с ненулевым дополнительным потоком в сети.
	 * Поиск ведется "в ширину", при этом вершины, уже попавшие
	 * в дерево обхода, пропускаются. 
	 * @param from - исток
	 * @param to - сток
	 * @return путь, если он существует, null в противном случае
	 */
	private int[] getPath(int from, int to) {
		// Дерево поиска в ширину
		int[] tree = new int[nVert];
		Arrays.fill(tree, -1);
		// Очередь вершин
		int[] queue = new int[nVert];
		int qHead = 0;	// Указатель на первый элемент в очереди
		int qCount = 1;	// Количество элементов очереди
		
		queue[0] = from;
		while(qCount > 0) {
			// Выбираем вершину из очереди
			int curr = queue[qHead++];
			qCount--;
			// Анализируем дуги, ведущие из этой вершины
			for (Iterator<Arc> iArc = graph.arcs(curr); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				int end = arc.to;
				if (tree[end] == -1 && arc.capacity() - arc.flow() > 0) {
					// Дуга с ненулевой остаточной пропускной способностью,
					// ведущая в еще не пройденную вершину.
					tree[end] = curr;
					if (end == to) {
						// Путь найден!
						return tree;
					}
					queue[qHead + (qCount++)] = end;
				}
			}
		}
		// Все пути исследованы, ничего не найдено.
		return null;
	}

	/**
	 * Проверочная функция организует граф, представляющий некоторую
	 * сеть из 9 узлов (исток - вершина № 0, сток - вершина № 4)
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
