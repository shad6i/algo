import java.util.*;


/**
 * Представление графа списками смежности.
 * 
 * @param <W> Тип нагрузки на дуги (в топологической сортировке не используется)
 */
public class Graph<W extends Number> implements Iterable<Integer> {
	/**
	 * Представление дуги графа
	 *
	 * @param <W> Тип нагрузки на дугу (в топологической сортировке не используется)
	 */
	static class Arc<W> {
		W info;			// Нагрузка на дугу
		int to;			// Номер вершины, в которую ведет дуга
		Arc<W> next;	// Следующая дуга в списке

		public Arc(int to, W info, Arc<W> next) {
			this.to = to; this.info = info; this.next = next;
		}
		
		public Arc(int to, W info) {
			this(to, info, null);
		}
	};

	private final Arc<W>[] lGraph;	// Списки смежности
	private final int nVertex;		// Число вершин

	/**
	 * Конструктор пустого графа с заданным числом вершин
	 * @param nVert Число вершин
	 */
	@SuppressWarnings("unchecked")
	public Graph(int nVert) {
		lGraph = new Arc[nVert];
		nVertex = nVert;
	}
	
	/**
	 * Число вершин графа
	 * @return
	 */
	public int getCount() { return nVertex; }

	/**
	 * Покомпонентный обход графа в глубину
	 * @param visitor Посетитель вершин и дуг графа.
	 */
	public void traverseDepthGraph(GraphVisitor<W> visitor) {
		boolean[] visited = new boolean[nVertex];
		for (int i = 0; i < nVertex; i++) {
			if (!visited[i]) {
				// Обход одной компоненты связности
				visitor.visitComponentStart(i);
				traverseDepthComponent(i, visitor, visited);
			}
		}
	}

	/**
	 * Добавление дуги в граф. Предполагается, что ранее такой дуги в графе не было.
	 * @param from	Начало дуги (номер вершины)
	 * @param to	Конец дуги (номер вершины)
	 * @param info	Нагрузка на дугу
	 */
	public void addArc(int from, int to, W info) {
		assert from < nVertex && from >= 0;
		assert to < nVertex && to >= 0;

		lGraph[from] = new Arc<W>(to, info, lGraph[from]);
	}

	/**
	 * Добавление ребра в граф. Предполагается, что ранее такого ребра в графе не было.
	 * @param from	Один конец ребра (номер вершины)
	 * @param to	Другой конец ребра (номер вершины)
	 * @param info	Нагрузка на ребро
	 */
	public void addEdge(int from, int to, W info) {
		addArc(from, to, info);
		addArc(to, from, info);
	}

	@Override
	public Iterator<Integer> iterator() {
		return new DepthFirstIterator();
	}

	public Iterator<Integer> bfIterator() { return new BreadthFirstIterator(); }
	

	//---------------------- PRIVATE -----------------------

	private class BreadthFirstIterator implements Iterator<Integer> {
		List<Integer> queue = new LinkedList<>();
		boolean[] visited = new boolean[nVertex];

		@Override
		public boolean hasNext() {
			if (queue.isEmpty()) {
				// Возможно, остались еще не пройденные вершины
				for (int i = 0; i <nVertex; ++i) {
					if (!visited[i]) {
						queue.add(i);
						visited[i] = true;
						break;
					}
				}
			}
			return !queue.isEmpty();
		}

		@Override
		public Integer next() {
			if (!hasNext()) throw new NoSuchElementException();
			// Выбираем вершину из стека
			Integer next = queue.remove(0);
			// Помещаем в очередь новые вершины, в которые ведут дуги из выбранной
			for (Arc<W> arc = lGraph[next]; arc != null; arc = arc.next) {
				if (!visited[arc.to]) {
					queue.add(arc.to);
					visited[arc.to] = true;
				}
			}
			return next;
		}
	}
	
	/**
	 * Итератор вершин графа в порядке обхода в глубину
	 */
	private class DepthFirstIterator implements Iterator<Integer> {
		Stack<Integer> stack = new Stack<Integer>();	// Контейнер-стек
		boolean[] marked;								// Массив пройденных вершин

		/**
		 * Инициализация итератора
		 */
		DepthFirstIterator() {
			marked = new boolean[nVertex];
		}

		@Override
		public boolean hasNext() {
			if (stack.empty()) {
				// Возможно, остались еще не пройденные вершины
				for (int i = 0; i <nVertex; ++i) {
					if (!marked[i]) {
						stack.push(i);
						break;
					}
				}
			}
			return !stack.empty();
		}

		@Override
		public Integer next() {
			if (!hasNext()) throw new NoSuchElementException();
			// Выбираем вершину из стека
			Integer next = stack.pop();
			marked[next] = true;
			// Помещаем в стек новые вершины, в которые ведут дуги из выбранной
			for (Arc<W> arc = lGraph[next]; arc != null; arc = arc.next) {
				if (!marked[arc.to] && !stack.contains(arc.to))
					stack.push(arc.to);
			}
			return next;
		}
	}

	/**
	 * Обход одной компоненты связности.
	 * @param i			Номер начальной вершины
	 * @param visitor	Посетитель вершин и дуг графа
	 * @param visited	Массив уже посещенных вершин
	 */
	private void traverseDepthComponent(int i, 
			GraphVisitor<W> visitor,
			boolean[] visited) {
		// Вход в вершину с отметкой о её посещении
		visitor.visitVertexIn(i);
		visited[i] = true;
		
		// Перебор дуг, ведущих из этой вершины
		for (Graph.Arc<W> arc = lGraph[i]; arc != null; arc = arc.next) {
			// Проходим по дуге
			visitor.visitArcForward(i, arc, visited[arc.to]);
			if (!visited[arc.to]) {
				// Дуга ведет в еще не посещенную вершину, делаем рекурсивный вызов
				traverseDepthComponent(arc.to, visitor, visited);
			}
			// Возвращаемся по дуге обратно
			visitor.visitArcBackward(i, arc);
		}
		// Покидаем вершину
		visitor.visitVertexOut(i);
	}

}
