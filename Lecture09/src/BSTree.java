import java.util.Arrays;


public abstract class BSTree<K extends Comparable<K>, V> {
	/**
	 * Класс представляет узел дерева. Этот класс предназначен только
	 * для внутренних целей, поэтому он private, и доступ к полям объектов
	 * этого класса осуществляется непосредственно.
	 */
	protected class BSNode {
		// Ссылки на левое и правое поддеревья:
		public BSNode left, right;
		// Ключ:
		public K key;
		// Значение:
		public V value;

		/**
		 * Конструктор произвольного узла.
		 * @param key ключ
		 * @param value значение
		 * @param color цвет узла
		 * @param left левое поддерево
		 * @param right правое поддерево
		 */
		public BSNode(K key, V value, BSNode left, BSNode right) {
			this.key = key; this.value = value;
			this.left = left; this.right = right;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		public BSNode(K key, V value) { this(key, value, null, null); }
		
		@Override
		public String toString() {
			return "<" + key + ", " + value + ">";
		}
	}
	
	// Корень дерева
	BSNode root = null;
	
	/**
	 * "Красивая" печать дерева.
	 */
	public void print() {
		print(root, 0);
	}

	/**
	 * Вспомогательная функция для "красивой" печати дерева.
	 * @param node корневой узел.
	 * @param indent начальный отступ при печати.
	 */
	private void print(BSNode node, int indent) {
		// Формируем строку из indent пробелов.
		char[] spaces = new char[indent];
		Arrays.fill(spaces, ' ');
		System.out.print(String.valueOf(spaces));

		if (node == null) {
			System.out.println("..");
		} else {
			// Печать узла и его поддеревьев.
			System.out.println(node);
			print(node.left, indent + 2);
			print(node.right, indent + 2);
		}
	}

	/**
	 * Поиск в дереве по ключу.
	 * @param key ключ поиска.
	 * @return найденное значение или null, если такого ключа нет в дереве.
	 */
	public V get(K key) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		return get(key, root);
	}
	
	/**
	 * Стандартный двоичный поиск в дереве по ключу
	 * @param key	Ключ поиска
	 * @param node	Начальный корень
	 * @return
	 */
	private V get(K key, BSNode node) {
		while (node != null) {
			int cmp = key.compareTo(node.key);
			if (cmp < 0) node = node.left; else
			if (cmp > 0) node = node.right; else
			return node.value;
		}
		// Ключ не найден
		return null;
	}
}