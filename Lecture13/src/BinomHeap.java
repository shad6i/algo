import java.util.Comparator;
import java.util.Iterator;

/**
 * ���������� ������������ ����. ����������� ��������:
 * - size() ������ ���� (����� ���������)
 * - getBest() ����� "�������" ��������;
 * - retrieveBest() ���������� "�������" ��������;
 * - add(key) ���������� ������ ��������;
 * - add(heap) ���������� ������������ ����;
 * - find(key), findNode(key) ����� ���� �� �����;
 * - changeKey(node, key) ��������� ���������� ����;
 * - removeNode(node) �������� ����.
 *
 * @param <K> ��� ������.
 * @param <V> ��� ��������������� ����.
 */
public class BinomHeap<K extends Comparable<K>> {
	/**
	 * ���������� ��� ��������� ������. �� ��������� - "������������" ���������.
	 */
	private Comparator<K> comparator = new Comparator<K>() {
		public int compare(K o1, K o2) {
			return o1.compareTo(o2);
		}
	};

	/**
	 * �����, �������������� ���� ������������� ������.
	 *
	 * @param <K> ��� �����.
	 */
	public static class Node<K> {
		/**
		 * ������ �� ������������ ���� (����� �������� ������).
		 */
		private Node<K> parent = null;

		/**
		 * ����, ���������� � ����.
		 */
		private K key;

		/**
		 * ������� ���� � ������.
		 */
		private int degree = 0;

		/**
		 * ������ �� �������� ���� (���� ����������� ������).
		 */
		private Node<K> son = null;

		/**
		 * ������ �� �������� ���� (����������� ������).
		 */
		private Node<K> brother = null;

		/**
		 * ����������� ���� - ���������� ����� ������, ��� ���
		 * ��������� ���� ����� ������ ������� ������ BinomHeap. 
		 * @param key ���� ������ ����.
		 */
		private Node(K key) { this.key = key; }

		/**
		 * ������� ������� � ����� ����.
		 * @return ���� ����.
		 */
		public K key() { return key; }

		/**
		 * ������� ���������� ���� ������������ �������� ������ ������.
		 * ����������� � ���� ������ ����, � ������� � �������� ������� 
		 * �������������� ������ ����.
		 * @param node �������������� ����
		 */
		private void addNode(Node<K> node) {
			if (node == null || node.degree != this.degree) {
				// ��������� ����� ������ ���� ������ ������.
				throw new IllegalArgumentException("incompatible nodes");
			}
			// ���������� ��������.
			node.parent = this;
			node.brother = son;
			son = node;
			// ������� ������������� ������ ����������.
			++degree;
		}
	}

	/**
	 * ������ ��������� ������ ������������ ��������.
	 */
	private Node<K> head = null;
	private int size = 0;

	/**
	 * ����������� ������� ������ �� ���������� ����� "�� ���������".
	 */
	public BinomHeap() {}

	/**
	 * ����������� ������� ������ � �������� ������������ ��� ���������� �����.
	 */
	public BinomHeap(Comparator<K> comparator) {
		this.comparator = comparator;
	}
	
	/**
	 * ���������� ��������� � ����
	 * @return
	 */
	public int size() { return size; }

	/**
	 * ������ ������� ���� � ������������ �����������.
	 * ������ ������� ������ - O(log N), ��� N - ����� �����.
	 * @return ������� � ������������ �����������.
	 */
	public K getBest() {
		// ���� "������" ���� ����������� (������ ������),
		// �� ��������� �������� NullPointerException
		return getBestNode().key; 
	}

	/**
	 * ������ ������ �� ���� � ������������ �����������.
	 * ������ ������� ������ - O(log N), ��� N - ����� �����.
	 * @return ������ �� ���� � ������������ ����������� ��� null, ���� ������ �����.
	 */
	public Node<K> getBestNode() {
		if (head == null) {
			return null;
		}
		Node<K> minNode = head;
		// ����� �� ������ ������������ �������� ����� � ������������ �����������.
		for (Node<K> curr = head.brother; 
				curr != null; 
				curr = curr.brother) {
			if (comparator.compare(minNode.key, curr.key) < 0) {
				minNode = curr;
			}
		}
		return minNode; 
	}

	/**
	 * ��������������� ������� - ���������� ���� �������� �������
	 * � ����. ��� ������� ��������������� �� ������ ������, ��
	 * ��� ���� ����������� ������� � ����� ������� ����������
	 * �������� ������ � ���� �� ������. ������ ������� ������ -
	 * O(log N), ��� N - ����� ����� ����� � ����� �������� �������.
	 * 
	 * @param rootList ����������� ������ ��������.
	 */
	private void merge(Node<K> rootList) {
		// ������ �������� ������ �������������� ��������.
		assert rootList != null;
		
		Node<K> p1 = head;       // ��������� �� ������� ��������� ������.
		Node<K> p2 = rootList;   // ��������� �� ������� ��������� ������.
		Node<K> pm = null;       // ��������� �� ��������� ������ ������������ ����.
		while (p1 != null && p2 != null) {
			if (p1.degree < p2.degree) {
				if (pm == null) {
					head = p1;
				} else {
					pm.brother = p1;
				}
				pm = p1;
				p1 = p1.brother;
			} else {
				if (pm == null) {
					head = p2;
				} else {
					pm.brother = p2;
				}
				pm = p2;
				p2 = p2.brother;
			}
		}
		pm.brother = p1 == null ? p2 : p1;
	}

	/**
	 * ���������� ���� ��� � ����� � ��� �� ������������.
	 * ���� ����������� ���� ��� ���� ������ ���������� ���������
	 * ���� ���������, �� ��������� ������ �������������!
	 * ������ ������� ������ - O(log N), ��� N - ����� ����� ����� � �����.
	 * @param heap ����������� ����. ����������� ����� ����������.
	 */
	public void add(BinomHeap<K> heap) {
		if (heap.head == null) {
			return;
		}
		size += heap.size;
		if (head == null) { 
			head = heap.head; 
			return; 
		}
		// ��� ���� �� ������, ������� �������� ������.
		merge(heap.head);
		// ������ �������� �� ��������� ������ � ����������
		// ������� ������ ������ ("�������� � �������").
		consolidate();
	}
	
	/**
	 * ������� ������������ ������. �������� �� ��������� ������ �
	 * ���������� ������� ������ ������ ���, ����� � �������� ������
	 * �� �������� ������ �������� ������ ������.
	 */
	private void consolidate() {
		Node<K> prev = null;
		Node<K> curr = head;
		Node<K> next = head.brother;
		while (next != null) {
			if (next.degree != curr.degree ||
					(next.brother != null &&
					curr.degree == next.brother.degree)) {
				// ������ 1. ���� ������ ������� ������ ��� ���. ���������� ������.
				prev = curr; curr = next;
			} else if (comparator.compare(curr.key, next.key) > 0) {
				// ������ 2. ��������� ��� �������� ������ ������ ������ � ����.
				curr.brother = next.brother;
				curr.addNode(next);
			} else {
				// ������ 3. ������������ ������ (2), �� ������ ���� ���������� ��������.
				if (prev == null) head = next; else prev.brother = next;
				next.addNode(curr);
				curr = next;
			}
			next = curr.brother;
		}
	}

	/**
	 * ���������� ������ ���� �������� � ���������� ���� � �������� ������
	 * � ����������� ������������� ������. ������ ������� ������ - O(log N)
	 * @param key ���� ������ ����.
	 */
	public void add(K key) {
		Node<K> newNode = new Node<K>(key);
		newNode.brother = head;
		head = newNode;
		size++;
		consolidate();
	}

	/**
	 * �������� ���� �� ��������� ������. ������� ������������ ��� ����������
	 * ������ ������������� �������� �� ���� � ��� �������� ��������� ����.
	 * ������ ������� ������ - O(log N).
	 * @param node ��������� ���� �� ��������� ������.
	 * @param predNode ���������� ���� � �������� ������.
	 * @return ��������� ����
	 */
	private Node<K> removeRootNode(Node<K> node) {
		assert node != null && node.parent == null;
		
		// 1. ����� ����������� ���� � ������ (���� ����).
		Node<K> predNode = null;
		if (head != node) {
			predNode = head;
			while (predNode.brother != node) {
				predNode = predNode.brother;
			}
		}
		
		// 2. �������� ����� ������ �� ��������� ������.
		if (predNode == null) {
			head = node.brother; 
		} else { 
			predNode.brother = node.brother;
		}
		
		if (node.son != null) {
			// 3. ���������� ���������� ���� �������� ����� �������� ������.
			Node<K> rootList = null;
			for (Node<K> child = node.son; child != null; ) {
				Node<K> next = child.brother;
				child.brother = rootList;
				rootList = child;
				child = next;
			}
		
			// 4. ���������� �������� ������ ����������� � ���������.
			merge(rootList);
		}
		
		// ��������� ���� ������������ � �������� ����������
		size--;
		return node;
	}

	/**
	 * ���������� ���� � ����� ������� �����������.
	 * ������ ������� ������ - O(log N).
	 * @return ��������� ����.
	 */
	public Node<K> retrieveBestNode() { 
		if (head == null) return null;
		// ����� ���� � ����� ������� �����������.
		Node<K> bestNode = head;
		for (Node<K> curr = head.brother; curr != null; curr = curr.brother) {
			if (comparator.compare(curr.key, bestNode.key) >= 0) {
				bestNode = curr;
			}
		}
		return removeRootNode(bestNode); 
	}

	/**
	 * ���������� ���� � ����� ������� �����������.
	 * ��� ������� ���������� ���� �� ������� ������ ��������� NullPointerException.
	 * ������ ������� ������ - O(log N).
	 * @return �������������� ���� ���������� ����.
	 */
	public K retrieveBest() {
		return retrieveBestNode().key;
	}

	/**
	 * ��������� ���������� ��������� ���� � ����������� ��� ��
	 * ����� ������� ������� � ������������ ������.
	 * ������ ������� ������ - O(log N).
	 * @param node ����, � �������� ���������� ���������.
	 * @param newKey ����� ���� (���������).
	 * @return ������ �� ����� ������� ���� � ���������� �����������.
	 */
	public Node<K> changeKey(Node<K> node, K newKey) {
		if (node == null) throw new NullPointerException();
		if (comparator.compare(newKey, node.key) < 0) {
			throw new IllegalArgumentException("��������� ���������� �� �����������.");
		}
		// "�������������" ���������� ����� �� ������.
		Node<K> current = node;
		while (current.parent != null && comparator.compare(newKey, current.parent.key) > 0) {
			current.key = current.parent.key;
			current = current.parent;
		}
		current.key = newKey;
		return current;
	}

	/**
	 * �������� ��������� ���� �� ����.
	 * ������ ������� ������ - O(log N).
	 * @param node ��������� ����.
	 */
	public Node<K> removeNode(Node<K> node) {
		if (node == null) throw new NullPointerException();
		// ������� ���� �������������� � ������ ������...
		Node<K> current = node;
		while (current.parent != null) {
			current.key = current.parent.key;
			current = current.parent;
		}
		// ...� ����� ���������.
		return removeRootNode(current);
	}

	/**
	 * ����� ���� ����� � ���� � �������� ����������� (������).
	 * ������ ������� ������ - O(N), ��� N - ����� ����� � ����.
	 * ��������� ����������� ����� ���� ����������, ����
	 * � ������ ����� ����� � �������� ������� �����������.
	 * @param key ���� ������.
	 * @return �������� ���� ����� � �������� ������ ������.
	 */
	public Iterator<Node<K>> findNode(final K key) {
		return new Iterator<Node<K>>() {
			Node<K> current = head;   // ������� ����������� ����.
			Node<K> toRemove = null;  // ��������� ���������� ����.

			// ��� �������� ��������� ����� ���� ������ ���� � �������� ������.
			{ findKey(); }

			/**
			 * ������� ������ ���������� ���� � �������� ������.
			 */
			private void findKey() {
				while (current != null && !current.key.equals(key)) {
					if (comparator.compare(key, current.key) > 0 || current.degree == 0) {
						if (current.brother != null || current.parent == null) {
							current = current.brother;
						} else {
							current = current.parent.brother;
						}
					} else {
						current = current.son;
					}
				}
			}

			@Override
			public boolean hasNext() {
				return current != null;
			}

			@Override
			public Node<K> next() {
				if (current == null) throw new IllegalStateException();
				toRemove = current;
				// ������ ������� � ���������� ����.
				if (current.degree > 0) {
					current = current.son;
				} else if (current.parent == null) {
					current = current.brother;
				} else {
					current = current.parent.brother;
				}
				// ���� ���� � �������� ������.
				findKey();
				return toRemove;
			}

			@Override
			public void remove() {
				if (toRemove == null) throw new IllegalStateException();
				removeNode(toRemove);
				toRemove = null;
			}
		};
	}

	/**
	 * ����� ���� ����� � ���� � �������� ����������� (������).
	 * ������ ������� ������ - O(N), ��� N - ����� ����� � ����.
	 * ��������� ����������� ����� ���� ����������, ����
	 * � ������ ����� ����� � �������� ������� �����������.
	 * @param key ���� ������.
	 * @return �������� ���� �������������� �������� � ����� � �������� ������ ������.
	 */
	public Iterator<K> find(final K key) {
		return new Iterator<K>() {
			/**
			 * �������� �����.
			 */
			Iterator<Node<K>> internal = findNode(key);

			@Override
			public boolean hasNext() {
				return internal.hasNext();
			}

			@Override
			public K next() {
				return internal.next().key;
			}

			@Override
			public void remove() {
				internal.remove();
			}
		};
	}

	/**
	 * ���������� ������� (unit test).
	 * @param args �� ������������.
	 */
	public static void main(String[] args) {
		// �������� ����� �� 13 ������.
		int[] keys = { 10, 1, 12, 25, 18, 6, 8, 14, 29, 11, 17, 38, 27 };
		// "��������" ���������� - ��������� ����� ��� ����, ��� �������� ������.
		Comparator<Integer> reversedComparator = new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		};

		// ������� ���� �� ������ 6 �����.
		BinomHeap<Integer> t1 = new BinomHeap<Integer>(reversedComparator);
		for (int i = 0; i < 6; ++i) {
			t1.add(keys[i]);
		}

		// ������� ���� �� ��������� 7 �����.
		BinomHeap<Integer> t2 = new BinomHeap<Integer>(reversedComparator);
		for (int i = 6; i < 13; ++i) {
			t2.add(keys[i]);
		}

		// ��������� ��� ����
		t1.add(t2);

		// �������� ������������ ���� � �������� ����� ����� � ���.
		System.out.println("size=" + t1.size());

		// ������ � ���� ���� � �������� ������ � ������ ���.
		Iterator<Integer> it = t1.find(14);
		if (it.hasNext()) {
			Integer found = it.next();
			System.out.println("Found: " + found);
			it.remove();
		} else {
			System.out.println("Not found");
		}

		// ������ ��� ���� �� �������, ��������� �������
		// �������� ������ ������������� ����.
		Integer min;
		while(t1.size() != 0) {
			min = t1.retrieveBest();
			System.out.println("number=" + t1.size() + ", min=" + min);
		}
	}
}
